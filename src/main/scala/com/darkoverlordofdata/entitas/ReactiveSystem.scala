package com.darkoverlordofdata.entitas

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class ReactiveSystem(val pool: Pool, val subsystem: IReactiveExecuteSystem) extends IExecuteSystem {

  private var _clearAfterExecute = false
  private var _buffer = new ArrayBuffer[Entity]()
  private var _ensureComponents: IMatcher = null
  private var _excludeComponents: IMatcher = null
  private var _observer: GroupObserver = null


  private val triggers = subsystem match {
    case s:IReactiveSystem => Array(s.trigger)
    case s:IMultiReactiveSystem => s.triggers
    case _ => Array()
  }

  subsystem match {
    case s: IEnsureComponents => _ensureComponents = s.ensureComponents
    case _ =>
  }

  subsystem match {
    case s:IExcludeComponents => _excludeComponents = s.excludeComponents
    case _ =>
  }

  subsystem match {
    case s:IClearReactiveSystem => _clearAfterExecute = true
    case _ =>
  }

  private val groups = new ListBuffer[Group]()
  private val eventTypes = new ListBuffer[GroupEventType.EnumVal]()
  for (trigger <- triggers) {
    val group = pool.getGroup(trigger.trigger)
    if (group != null) {
      groups += group
      eventTypes += trigger.eventType
    }

  }
  _observer = new GroupObserver(groups.toArray, eventTypes.toArray)

  def clear() {
    _observer.clearCollectedEntities()
  }

  def activate() = {
    _observer.activate()
  }

  def deactivate() = {
    _observer.deactivate()
  }

  override def execute() {
    val collectedEntities = _observer.collectedEntities
    if (collectedEntities.nonEmpty) {
      if (_ensureComponents != null) {
        if (_excludeComponents != null) {
          for (e <- collectedEntities) {
            if (_ensureComponents.matches(e)
            && !_excludeComponents.matches(e))
            _buffer += e.retain()
          }
        } else {
          for (e <- collectedEntities) {
            if (_ensureComponents.matches(e))
            _buffer += e.retain()
          }
        }
      } else if (_excludeComponents != null) {
        for (e <- collectedEntities) {
          if (_excludeComponents.matches(e))
          _buffer += e.retain()
        }
      } else {
        for (e <- collectedEntities) {
          _buffer += e.retain()
        }
      }
    }
    _observer.clearCollectedEntities()
    if (_buffer.nonEmpty) {
      subsystem.execute(_buffer.toArray)
      for (buf <- _buffer) buf.release()
      _buffer.clear()
      if (_clearAfterExecute) _observer.clearCollectedEntities()
    }
  }

}
