package com.darkoverlordofdata.entitas

import scala.collection.mutable

class GroupObserver(groups:Array[Group], eventTypes:Array[GroupEventType.EnumVal]) {

  private var _collectedEntities = new mutable.HashSet[Entity]()

  def collectedEntities = _collectedEntities

  if (groups.length != eventTypes.length) {
    throw new GroupObserverException(s"Unbalanced count with groups (${groups.length}) and event types (${eventTypes.length})")
  }
  activate()

  val addEntity = (e: GroupChangedArgs) =>  {
    if (_collectedEntities.contains(e.entity)) {
      _collectedEntities.add(e.entity)
      e.entity.retain()
    }
  } : Unit

  def activate() = {
    for (i <- groups.indices) {
      val group = groups(i)
      val eventType = eventTypes(i)
      eventType match {
        case GroupEventType.OnEntityAdded => {
          group.onEntityAdded -= addEntity
          group.onEntityAdded += addEntity
        }
        case GroupEventType.OnEntityRemoved => {
          group.onEntityRemoved -= addEntity
          group.onEntityRemoved += addEntity
        }
        case GroupEventType.OnEntityAddedOrRemoved => {
          group.onEntityAdded -= addEntity
          group.onEntityAdded += addEntity
          group.onEntityRemoved -= addEntity
          group.onEntityRemoved += addEntity
        }
        case _ => throw new Exception(s"Invalid eventType $eventType in GroupObserver::activate")
      }

    }

  }

  def deactivate() = {
    for (group <- groups) {
      group.onEntityAdded -= addEntity
      group.onEntityRemoved -= addEntity
    }
    clearCollectedEntities()

  }

  def clearCollectedEntities() = {
    for (entity: Entity <- _collectedEntities)
      entity.release()
    _collectedEntities = new mutable.HashSet[Entity]()

  }

}
