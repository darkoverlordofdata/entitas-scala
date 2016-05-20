package com.darkoverlordofdata.entitas

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object Pool {
  private var _instance: Pool = null
  def instance  = _instance
  def setPool(system: ISystem, pool: Pool) {
    system match {
      case s:ISetPool => s.setPool(pool)
      case _ =>
    }
  }
}

class Pool(val totalComponents:Int, val startCreationIndex:Int=0) {


  val onEntityCreated = new Event[PoolEntityChangedArgs]()
  val onEntityWillBeDestroyed = new Event[PoolEntityChangedArgs]()
  val onEntityDestroyed = new Event[PoolEntityChangedArgs]()
  val onGroupCreated = new Event[PoolGroupChangedArgs]()

  private var _creationIndex = startCreationIndex
  private val _entities = new mutable.HashSet[Entity]()
  private val _groups = new mutable.HashMap[Int,Group]()
  private val _groupsForIndex = new mutable.HashMap[Int,ArrayBuffer[Group]]()
  private val _reusableEntities = new ListBuffer[Entity]()
  private val _retainedEntities = new mutable.HashSet[Entity]()
  private val _entitiesCache = new ArrayBuffer[Entity]()

  type OnEntityReleased = (EntityReleasedArgs) => Unit
  private var onEntityReleasedCache: OnEntityReleased = null


  def count = _entities.size
  def reusableEntitiesCount = _reusableEntities.size
  def retainedEntitiesCount = _retainedEntities.size

  val onEntityReleased = (e: EntityReleasedArgs) =>  {
    if (e.entity.isEnabled)
      throw new EntityIsNotDestroyedException("Cannot release entity.")

    e.entity.onEntityReleased -= onEntityReleasedCache
    _retainedEntities.remove(e.entity)
    _reusableEntities += e.entity
  } : Unit


  val updateGroupsComponentAddedOrRemoved = (e: EntityChangedArgs) => {
    if (_groupsForIndex.contains(e.index)) {
      for (i <- _groupsForIndex(e.index).indices) {
        val group = _groupsForIndex(e.index)(i)
        if (e.component != null) {
          group.handleEntity(e.entity, e.index, e.component)
        }
      }
    }
  } : Unit

  val updateGroupsComponentReplaced = (e: ComponentReplacedArgs) => {
    if (_groupsForIndex.contains(e.index)) {
      for (i <- _groupsForIndex(e.index).indices) {
        val group = _groupsForIndex(e.index)(i)
          group.updateEntity(e.entity, e.index, e.previous, e.replacement)
      }
    }
  } : Unit

  onEntityReleasedCache = onEntityReleased
  Pool._instance = this

  /**
    *
    * @param name
    * @return
    */
  def createEntity(name: String): Entity = {
    val entity = if (_reusableEntities.nonEmpty) _reusableEntities.remove(_reusableEntities.size-1) else new Entity(totalComponents)
    _creationIndex += 1
    entity.initialize(name, _creationIndex)
    entity.retain()
    entity.onComponentAdded += updateGroupsComponentAddedOrRemoved
    entity.onComponentRemoved += updateGroupsComponentAddedOrRemoved
    entity.onComponentReplaced += updateGroupsComponentReplaced
    entity.onEntityReleased += onEntityReleased
    _entities.add(entity)
    _entitiesCache.clear()
    onEntityCreated(new PoolEntityChangedArgs(this, entity))
    entity
  }

  /**
    *
    * @param entity
    * @return
    */
  def destroyEntity(entity: Entity) = {
    if (entity != null)  {

      if (!_entities.contains(entity)) {
        throw new PoolDoesNotContainEntityException(entity, "Could not destroy entity!")
      }
      _entities.remove(entity)
      _entitiesCache.clear()
      onEntityWillBeDestroyed(new PoolEntityChangedArgs(this, entity))
      entity.destroy()
      onEntityDestroyed(new PoolEntityChangedArgs(this, entity))

      if (entity.refCount == 1) {
        entity.onEntityReleased -= onEntityReleased
        _reusableEntities += entity
      } else {
        _retainedEntities.add(entity)
      }
    }
  }

  def destroyAllEntities() = {
    val entities = getEntities
    for (entity <- entities)
      destroyEntity(entity)

  }

  def hasEntity(entity: Entity): Boolean = {
    _entities.contains(entity)
  }

  def getEntities: ArrayBuffer[Entity] = {
    if (_entitiesCache.isEmpty) {
      for (entity <- _entities)
        _entitiesCache += entity
    }
    _entitiesCache

  }

  def getEntities(matcher: IMatcher): Array[Entity] = {
    getGroup(matcher).entities.toArray
  }


  def createSystem(system: ISystem): ISystem = {
    Pool.setPool(system, this)
    system match {
      case s:IReactiveSystem => new ReactiveSystem(this, s)
      case s:IMultiReactiveSystem => new ReactiveSystem(this, s)
      case _ => system
    }
  }

  def getGroup(matcher: IMatcher): Group = {
    if (_groups.contains(matcher.id)) {
      val group = _groups(matcher.id)
      group
    } else {
      val group = new Group(matcher)
      for (entity <- getEntities)
        group.handleEntitySilently(entity)
      _groups.update(matcher.id, group)

      for (index <- matcher.indices) {
        if (!_groupsForIndex.contains(index)) {
          _groupsForIndex.update(index, new ArrayBuffer[Group]())
        }
        _groupsForIndex(index) += group
      }
      onGroupCreated(new PoolGroupChangedArgs(this, group))
      group
    }

  }
}
