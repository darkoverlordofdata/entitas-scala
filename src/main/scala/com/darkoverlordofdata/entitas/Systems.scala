package com.darkoverlordofdata.entitas

import scala.collection.mutable.ArrayBuffer

class Systems extends IInitializeSystem with IExecuteSystem{

  private var _initializeSystems = new ArrayBuffer[IInitializeSystem]()
  private var _executeSystems = new ArrayBuffer[IExecuteSystem]()

  def add(system: ISystem): Systems = {
    val reactiveSystem = system match {
      case s:ReactiveSystem => s
      case _ => null
    }

    val initializeSystem =
      if (reactiveSystem != null) {
        reactiveSystem.subsystem match {
          case s: IInitializeSystem => s
          case _ => null
        }
      } else {
        system match {
          case s: IInitializeSystem => s
          case _ => null
        }
      }

    if (initializeSystem != null)
      _initializeSystems += initializeSystem

    val executeSystem = system match {
      case s:IExecuteSystem => s
      case _ => null
    }

    if (executeSystem != null) {
      _executeSystems += executeSystem
    }
    this
  }

  override def initialize() {
    for (system <- _initializeSystems) system.initialize()

  }
  override def execute() {
    for (system <- _executeSystems) system.execute()
  }


  def clearReactiveSystems():Unit = {
    for (system <- _executeSystems) {
      system match {
        case s:ReactiveSystem => s.clear()
        case _ =>
      }
      system match {
        case s:Systems => s.clearReactiveSystems()
        case _ =>
      }
    }
  }
}
