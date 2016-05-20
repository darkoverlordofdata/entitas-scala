package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.{IExecuteSystem, Pool}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._


class ExpiringSystem (val pool:Pool) extends IExecuteSystem {

  lazy val group = pool.getGroup(Match.Expires)

  override def execute(): Unit = {
    val delta = Platform.delta
    for (entity <- group.entities) {
      val expires = entity.expires
      entity.updateExpires(expires.copy(delay = expires.delay-delta))

      //entity.expires.delay -= delta
      if (entity.expires.delay < 0) {
        //pool.destroyEntity(entity)
        entity.setDestroy(true)
      }
    }


  }
}
