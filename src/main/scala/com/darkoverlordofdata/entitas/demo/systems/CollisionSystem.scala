package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Match}
import com.darkoverlordofdata.entitas.{Entity, Pool, IExecuteSystem}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._
import com.darkoverlordofdata.entitas.demo.Factory._

import scala.collection.mutable.ListBuffer


class CollisionSystem (val pool:Pool) extends IExecuteSystem {

  lazy val bullets = pool.getGroup(Match.Bullet)
  lazy val enemies = pool.getGroup(Match.Enemy)
  lazy val players = pool.getGroup(Match.Player)
  var inactive = ListBuffer[Int]()

  override def execute(): Unit = {
    inactive.clear()
    for (bullet <- bullets.entities)
      for (enemy <- enemies.entities)
        if (!inactive.contains(enemy.creationIndex))
          if (collidesWith(bullet, enemy))
            collisionHandler(bullet, enemy)

  }

  def collidesWith(e1:Entity, e2:Entity):Boolean = {
    val position1 = e1.position
    val position2 = e2.position
    val a = (position1.x - position2.x).toDouble
    val b = (position1.y - position2.y).toDouble

    (Math.sqrt(a * a + b * b) - e1.bounds.radius) < e2.bounds.radius
  }

  def collisionHandler(weapon:Entity, ship:Entity) {
    val pos = weapon.position
    pool.createSmallExplosion(pos.x, pos.y)
    weapon.setDestroy(true)
    val health = ship.health
    ship.updateHealth(health.copy(currentHealth = health.currentHealth-1))
    if (health.currentHealth <= 0f) {
      inactive += ship.creationIndex
      val position = ship.position
      pool.createBigExplosion(position.x, position.y)
      ship.setDestroy(true)
      val player = players.singleEntity
      if (player != null) {
        val score = player.score
        player.updateScore(score.copy(value = score.value + health.maximumHealth.toInt))
      }
      //player.score.value += health.maximumHealth.toInt
    }
  }
}
