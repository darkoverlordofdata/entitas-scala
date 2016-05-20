package com.darkoverlordofdata.entitas.demo

import com.darkoverlordofdata.entitas.demo.systems._
import com.darkoverlordofdata.entitas.{Pool, Systems}

class GameScene() {

  lazy val pool:Pool = { new Pool(Component.TotalComponents.id) }
  lazy val systems:Systems = { createSystems(pool) }

  def start(): Unit = {
    systems.initialize()
  }

  def createSystems(pool: Pool): Systems = {
    new Systems()
      .add(pool.createSystem(new SpriteRenderSystem(pool)))
      .add(pool.createSystem(new PhysicsSystem(pool)))
      .add(pool.createSystem(new ViewManagerSystem(pool)))
      .add(pool.createSystem(new PlayerInputSystem(pool)))
      .add(pool.createSystem(new SoundEffectSystem(pool)))
      .add(pool.createSystem(new CollisionSystem(pool)))
      .add(pool.createSystem(new ExpiringSystem(pool)))
      .add(pool.createSystem(new EntitySpawningTimerSystem(pool)))
      .add(pool.createSystem(new ScaleTweenSystem(pool)))
      .add(pool.createSystem(new RemoveOffscreenShipsSystem(pool)))
      .add(pool.createSystem(new HealthRenderSystem(pool)))
      .add(pool.createSystem(new ScoreRenderSystem(pool)))
      .add(pool.createSystem(new DestroySystem(pool)))
  }

  def render() = {
    systems.execute()
  }

}
