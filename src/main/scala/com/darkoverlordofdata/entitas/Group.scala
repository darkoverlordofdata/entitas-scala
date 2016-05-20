package com.darkoverlordofdata.entitas

import scala.collection.mutable


class Group(val matcher: IMatcher) {

  val onEntityAdded = new Event[GroupChangedArgs]()
  val onEntityRemoved = new Event[GroupChangedArgs]()
  val onEntityUpdated = new Event[GroupUpdatedArgs]()

  private var _entities = new mutable.HashSet[Entity]()
  private var _toStringCache = ""
  private var _entitiesCache:List[Entity] = List()
  private var _singleEntityCache: Entity = null

  val count = _entities.size

  def entities = {
    if (_entitiesCache.isEmpty) {
      _entitiesCache = _entities.toList
      //_sortEntities(_entitiesCache)
    }
    _entitiesCache
  }

  def singleEntity = {
    entities.length match {
      case 1 => entities.head
      case 0 => null
      case _ => throw new SingleEntityException(matcher)
    }
  }

  def createObserver(eventType: GroupEventType.EnumVal): GroupObserver = {
    new GroupObserver(Array(this), Array(eventType))
  }

  def handleEntitySilently(entity: Entity) = {
    if (matcher.matches(entity))
      addEntitySilently(entity)
    else
      removeEntitySilently(entity)
  }

  def handleEntity(entity: Entity, index:Int, component: IComponent) = {
    if (matcher.matches(entity))
      addEntity(entity, index, component)
    else
      removeEntity(entity, index, component)
  }

  def updateEntity(entity: Entity, index:Int, previousComponent: IComponent, newComponent: IComponent) = {
    if (_entities.contains(entity)) {
      onEntityRemoved(new GroupChangedArgs(this, entity, index, previousComponent))
      onEntityAdded(new GroupChangedArgs(this, entity, index, newComponent))
      onEntityUpdated(new GroupUpdatedArgs(this, entity, index, previousComponent, newComponent))
    }
  }


  def addEntitySilently(entity: Entity) = {
    if (!_entities.contains(entity)) {
      _entities.add(entity)
      _entitiesCache = List()
      _toStringCache = ""
      entity.retain()
    }
  }

  def addEntity(entity: Entity, index:Int, component: IComponent) = {
    if (!_entities.contains(entity)) {
      _entities.add(entity)
      _entitiesCache = List()
      _toStringCache = ""
      entity.retain()
      onEntityAdded(new GroupChangedArgs(this, entity, index, component))
    }
  }

  def removeEntitySilently(entity: Entity) = {
    if (_entities.contains(entity)) {
      _entities.remove(entity)
      _entitiesCache = List()
      _singleEntityCache = null
      entity.release()
    }
  }

  def removeEntity(entity: Entity, index:Int, component: IComponent) = {
    if (_entities.contains(entity)) {
      _entities.remove(entity)
      _entitiesCache = List()
      _singleEntityCache = null
      onEntityRemoved(new GroupChangedArgs(this, entity, index, component))
      entity.release()
    }
  }


  def containsEntity(entity: Entity):Boolean = {
    _entities.contains(entity)  }

  override def toString:String = {
    if (_toStringCache == "") {
      _toStringCache = s"Group(${matcher.toString})"
    }
    _toStringCache
  }







}
