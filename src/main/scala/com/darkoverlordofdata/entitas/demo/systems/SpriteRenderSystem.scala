package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._
import com.darkoverlordofdata.entitas._


class SpriteRenderSystem (val pool:Pool)
  extends IInitializeSystem with IExecuteSystem {

  lazy val group = pool.getGroup(Match.View)

  /**
    * onEntityAdded
    * Maintain sorted list of entities
    */
  group.onEntityAdded += {e: GroupChangedArgs =>
    Platform.stage.addChild(e.entity.view.sprite)

    val sprite = e.entity.view.sprite
    val layer = if (e.entity.hasLayer) e.entity.layer.ordinal else 0
    sprite.name = layer.toString

    val children = Platform.stage.children
    var found = false
    for (i <- children.indices) {
      if (children(i).name <= sprite.name) {
        Platform.stage.addChildAt(sprite, i)
        found = true
      }
    }
    if (!found)
      Platform.stage.addChild(sprite)
  }

  override def initialize(): Unit = {
    println("Initialize background")
    Platform.background.scaleX = 1.3
    Platform.background.scaleY = 1.3
    Platform.background.name = "0"
    Platform.stage.addChild(Platform.background)
  }

  override def execute(): Unit = {
    for (entity <- group.entities) {
      if (entity.hasPosition) {
        val sprite = entity.view.sprite
        val bounds = sprite.getBounds()
        sprite.regX = bounds.width/2
        sprite.regY = bounds.height/2
        sprite.x = entity.position.x// - (image.width/2)
        sprite.y = Platform.height - entity.position.y //- (image.height/2)
        if (entity.hasScale) {
          val scale = entity.scale
          sprite.scaleX = scale.x
          sprite.scaleY = scale.y
        }
      }
    }
    Platform.stage.update()
  }
}
