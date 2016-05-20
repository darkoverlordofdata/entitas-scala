package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.{Pool, IExecuteSystem}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._

class DestroySystem (val pool:Pool) extends IExecuteSystem {
  lazy val group = pool.getGroup(Match.Destroy)

  override def execute(): Unit = {
    //group.entities.map(pool.destroyEntity)

    for (entity <- group.entities) {
      if (entity.hasView)
        Platform.stage.removeChild(entity.view.sprite)
      pool.destroyEntity(entity)
    }
  }
}
