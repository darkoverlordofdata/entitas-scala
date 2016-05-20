package com.darkoverlordofdata.entitas.demo

import com.darkoverlordofdata.entitas.{Pool, Entity}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._
object Layer extends Enumeration {
  type Effect = Value
  var UNKNOWN, STATUS, ENEMY3, ENEMY2, ENEMY1, PLAYER, BATTLE, BULLET = Value
}

object Effect extends Enumeration {
  type Effect = Value
  var PEW, ASPLODE, SMALLASPLODE = Value
}


object Factory {

  implicit class EntityFactory(val pool: Pool) {
    val Random = new java.util.Random()

    def getSoundEffect(name: String): Int = {
      name match {
        case "bullet" => 1
        case "bang" => 2
        case "explosion" => 3
        case _ => 0
      }
    }

    def prefab(name: String): Entity = {

      val sprite = Platform.getSprite(name)
      val entity = pool.createEntity(name)
        .addLayer(Platform.getLayer(name))
        .addBounds(sprite.getBounds().width.toFloat/2)
        .addView(sprite)
      entity
    }

    def createPlayer(width: Float, height: Float): Entity = {
      val entity = prefab("player")
        .addPosition(width / 2, 80f)
        .addScore(0)
        .setPlayer(true)
      entity
    }

    def createBullet(x: Float, y: Float): Entity = {
      val entity = prefab("bullet")
        .addExpires(.5f)
        .addPosition(x, y)
        .addSoundEffect(getSoundEffect("bullet"))
        .addTint(1f, 1f, 115f / 255f, 1f)
        .addVelocity(0f, -800f)
        .setBullet(true)
      entity
    }

    def createEnemy1(width: Float, height: Float): Entity = {
      val entity = prefab("enemy1")
      entity.addHealth(10f, 10f)
        .addPosition(Random.nextFloat() * width, height - entity.bounds.radius)
        .addVelocity(0f, 40f)
        .setEnemy(true)
      entity
    }

    def createEnemy2(width: Float, height: Float): Entity = {
      val entity = prefab("enemy2")
      entity.addHealth(20f, 20f)
        .addPosition(Random.nextFloat() * width, height - entity.bounds.radius)
        .addVelocity(0f, 30f)
        .setEnemy(true)
      entity
    }

    def createEnemy3(width: Float, height: Float): Entity = {
      val entity = prefab("enemy3")
      entity.addHealth(60f, 60f)
        .addPosition(Random.nextFloat() * width, height - entity.bounds.radius)
        .addVelocity(0f, 10f)
        .setEnemy(true)
      entity
    }

    def createSmallExplosion(x: Float, y: Float): Entity = {
      val scale = 1f
      val entity = prefab("bang")
        .addExpires(.5f)
        .addPosition(x, y)
        .addScale(scale, scale)
        .addSoundEffect(getSoundEffect("bang"))
        .addTint(1f, 1f, 39 / 255f, .5f)
        .addTween(scale / 100, scale, -3f, repeat = false, active = true)
      entity
    }

    def createBigExplosion(x: Float, y: Float): Entity = {
      val scale = .5f
      val entity = prefab("explosion")
        .addExpires(.5f)
        .addPosition(x, y)
        .addScale(scale, scale)
        .addSoundEffect(getSoundEffect("explosion"))
        .addTint(1f, 1f, 39 / 255f, .5f)
        .addTween(scale / 100, scale, -3f, repeat = false, active = true)
      entity
    }
  }
}