package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.{Pool, IExecuteSystem}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._

class SoundEffectSystem (val pool:Pool) extends IExecuteSystem {

  val group = pool.getGroup(Match.SoundEffect)

  override def execute(): Unit = {
    for (entity <- group.entities) {
      entity.soundEffect.effect match {
        case 1 => Platform.play("pew")
        case 2 => Platform.play("smallasplode")
        case 3 => Platform.play("asplode")
        case _ => ()
      }
      entity.removeSoundEffect()
    }
  }
}
