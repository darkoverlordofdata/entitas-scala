package com.darkoverlordofdata.entitas

/**
  *
  *  Use pool.CreateEntity() to create a new entity and pool.DestroyEntity() to destroy it.
  */
class Entity(val totalComponents:Int) {

  def creationIndex = _creationIndex
  def refCount = _refCount
  def name = _name
  def isEnabled = _isEnabled

  val onEntityReleased = new Event[EntityReleasedArgs]()
  val onComponentAdded = new Event[EntityChangedArgs]()
  val onComponentRemoved = new Event[EntityChangedArgs]()
  val onComponentReplaced = new Event[ComponentReplacedArgs]()

  private var _name = ""
  private var _refCount = 0
  private var _isEnabled = false
  private var _creationIndex = 0
  private var toStringCache = ""
  private val components = new Array[IComponent](totalComponents)
  private var componentsCache:List[IComponent] = List()

  def initialize(name:String, creationIndex:Int) {
    _name = name
    _creationIndex = creationIndex
    _isEnabled = true
  }

  /**
    *  Adds a component at a certain index. You can only have one component at an index.
    *  Each component type must have its own constant index.
    *  The prefered way is to use the generated methods from the code generator.
    *
    * @param index
    * @param component
    * @return
    */
  def addComponent(index:Int, component: IComponent): Entity = {
    if (!isEnabled)
      throw new EntityIsNotEnabledException("Cannot add component!")

    if (hasComponent(index)) {
      val errorMsg = s"Cannot add component at index $index to $toString"
      throw new EntityAlreadyHasComponentException(errorMsg, index)
    }
    components.update(index, component)
    componentsCache = List()
    toStringCache = ""
    onComponentAdded(new EntityChangedArgs(this, index, component))
    this
  }


  /**
    *
    *  Removes a component at a certain index. You can only remove a component at an index if it exists.
    *  The prefered way is to use the generated methods from the code generator.
    *
    * @param index
    * @return
    */
  def removeComponent(index:Int): Entity = {
    if (!isEnabled)
      throw new EntityIsNotEnabledException("Entity is disabled, cannot remove component")
    if (!hasComponent(index)) {
      val errorMsg = s"Cannot remove component at index $index from $toString"
      throw new EntityDoesNotHaveComponentException(errorMsg, index)
    }
    _replaceComponent(index, null)
    this
  }

  /**
    *
    *  Replaces an existing component at a certain index or adds it if it doesn't exist yet.
    *  The prefered way is to use the generated methods from the code generator.
    *
    * @param index
    * @param component
    * @return
    */
  def replaceComponent(index:Int, component: IComponent): Entity = {
    if (!isEnabled)
      throw new EntityIsNotEnabledException(s"Entity is disabled, cannot replace at index $index, ${toString}")

    if (hasComponent(index)) {
      _replaceComponent(index, component)
    } else if (component != null) {
      addComponent(index, component)
    }
    this
  }

  def updateComponent(index:Int, component: IComponent): Entity = {
    val previousComponent = components(index)
    if (previousComponent != null) {
      components.update(index, component)
    }
    this
  }
  def _replaceComponent(index:Int, component: IComponent): Entity = {
    val previousComponent = components(index)
    if (previousComponent != null) {
      if (previousComponent.equals(component)) {
        onComponentReplaced(new ComponentReplacedArgs(this, index, previousComponent, component))
      } else {
        components.update(index, component)
        componentsCache = List()
        toStringCache = ""
        if (component == null) {
          onComponentRemoved(new EntityChangedArgs(this, index, previousComponent))
        } else {
          onComponentReplaced(new ComponentReplacedArgs(this, index, previousComponent, component))
        }
      }
    }
    this
  }

  /**
    *  Returns a component at a certain index. You can only get a component at an index if it exists.
    *  The prefered way is to use the generated methods from the code generator.
    *
    * @param index
    * @return
    */
  def getComponent(index:Int): IComponent = {
    if (!hasComponent(index)) {
      val errorMsg = s"Cannot get component at index $index from ${toString()}"
      throw new EntityDoesNotHaveComponentException(errorMsg, index)
    }
    components(index)
  }


  /**
    *  Returns all added components.
    *
    * @return
    */
  def getComponents:List[IComponent] = {
    if (componentsCache.isEmpty) {
      componentsCache = components.filter(_ != null).toList
    }
    componentsCache

  }

  /**
    *  Determines whether this entity has a component at the specified index.
    *
    * @param index
    * @return
    */
  def hasComponent(index:Int):Boolean = {
    components(index) != null
  }

  /**
    *  Determines whether this entity has components at all the specified indices.
    *
    * @param indices
    * @return
    */
  def hasComponents(indices:Array[Int]):Boolean = {
    for (index <- indices) {
      if (components(index) == null) return false
    }
    true
  }

  /**
    *  Determines whether this entity has a component at any of the specified indices.
    *
    * @param indices
    * @return
    */
  def hasAnyComponent(indices:Array[Int]):Boolean = {
    for (index <- indices) {
      if (components(index) != null) return true
    }
    false
  }

  /**
    *  Removes all components.
    *
    */
  def removeAllComponents() = {
    for (index <- 0 until totalComponents) {
      if (components(index) != null) {
        _replaceComponent(index, null)
      }
    }
  }

  def retain(): Entity = {
    _refCount += 1
    this
  }

  def release() = {
    _refCount -= 1
    if (_refCount == 0) {
      onEntityReleased(new EntityReleasedArgs(this))
    } else if (_refCount < 0)
      throw new Exception(s"Entity is already released ${toString()}")
  }

  def destroy() {
    removeAllComponents()
    onComponentAdded.clear()
    onComponentRemoved.clear()
    onComponentReplaced.clear()
    componentsCache = List()
    _name = ""
    _isEnabled = false
  }

  /**
    *  Returns a cached string to describe the entity with the following format:
    *  Entity_{name|creationIndex}({list of components})
    *
    * @return
    */
  override def toString:String = {
    if (toStringCache == "") {
      val sb = new StringBuilder()
      sb.append("Entity_")
      sb.append(if (name != "") name else creationIndex.toString)
      sb.append(s"(${_creationIndex})(")

      for (i <- components.indices) {
        if (components(i) != null) {
          sb.append(components(i).getClass.getName)
          sb.append(",")
        }
      }
      sb.append(")")
      toStringCache = sb.toString.replace(",)", ")")
    }
    toStringCache

  }
}
