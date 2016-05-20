package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.{IExecuteSystem, Pool}
import com.darkoverlordofdata.entitas.demo.{Match}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._

class RemoveOffscreenShipsSystem (val pool:Pool) extends IExecuteSystem {

  lazy val group = pool.getGroup(Match.Position)

  override def execute(): Unit = {

    for (entity <- group.entities) {
      if (entity.isEnemy) {
        if (entity.position.y < 0) {
          entity.setDestroy(true)
          //pool.destroyEntity(entity)
        }
      }

    }
  }
}
