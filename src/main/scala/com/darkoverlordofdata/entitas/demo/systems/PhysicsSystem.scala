package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.{Matcher, IExecuteSystem, Pool}
import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._


class PhysicsSystem (val pool:Pool) extends IExecuteSystem {

  lazy val group = pool.getGroup(Matcher.allOf(Match.Position, Match.Velocity))

  override def execute(): Unit = {

    for (entity <- group.entities) {
      val position = entity.position
      entity.updatePosition(position.copy(
        x = position.x + entity.velocity.x * Platform.delta,
        y = position.y - entity.velocity.y * Platform.delta
      ))
      //entity.position.x = entity.position.x + entity.velocity.x * Gdx.graphics.getDeltaTime
      //entity.position.y = entity.position.y - entity.velocity.y * Gdx.graphics.getDeltaTime

    }
  }
}
