package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._
import com.darkoverlordofdata.entitas.demo.Factory._
import com.darkoverlordofdata.entitas.{IInitializeSystem, Pool, IExecuteSystem}

class PlayerInputSystem (val pool:Pool) extends IExecuteSystem with IInitializeSystem  {

  lazy val group = pool.getGroup(Match.Player)


  val FireRate = .1f
  var timeToFire = 0.0f

  override def initialize(): Unit = {
    pool.createPlayer(Platform.width.toFloat, Platform.height.toFloat)
  }

  override def execute(): Unit = {
    val player = group.singleEntity

    if (player != null) {
      val position = player.position
      player.updatePosition(position.copy(x = Platform.mouseX.toFloat, y = Platform.mouseY.toFloat))

      if (Platform.shoot) {
        timeToFire -= Platform.delta
        if (timeToFire < 0) {
          pool.createBullet(Platform.mouseX - 27f, Platform.mouseY - 10f)
          pool.createBullet(Platform.mouseX + 27f, Platform.mouseY - 10f)
          timeToFire = FireRate
        }
      }
    }
  }
}
