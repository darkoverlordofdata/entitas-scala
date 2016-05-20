package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.{GroupChangedArgs, ISystem, Pool}
import com.darkoverlordofdata.entitas.demo.{Platform, Match}
import com.darkoverlordofdata.entitas.demo.EntityExtensions._

class ViewManagerSystem (val pool:Pool) extends ISystem {

  /**
    * onEntityAdded
    * Fix up the sprite position
    */
  pool.getGroup(Match.Position).onEntityAdded += {it:GroupChangedArgs =>
    val entity = it.entity
    val sprite = entity.view.sprite
    if (sprite != null) {
      if (entity.hasPosition) {
        val bounds = sprite.getBounds()
        sprite.x = entity.position.x - (bounds.width/2)
        sprite.y = Platform.height - entity.position.y - (bounds.height/2)
      }
    }
  }

  /**
    * onEntityAdded
    * Fix up the sprite color
    */
  pool.getGroup(Match.Tint).onEntityAdded += {it:GroupChangedArgs =>
    val entity = it.entity
    val sprite = entity.view.sprite
    if (entity.hasTint) {
      sprite.alpha = .5
//      println("Set Tint")
//      val tint = entity.tint
//      sprite.filters = js.Array(new ColorFilter(0, 0, 0, 1, 255, 0, 0))
//      sprite.cache()
    }
  }


  /**
    * onEntityRemoved
    * Reset the sprite color
    */
  pool.getGroup(Match.Tint).onEntityRemoved += {it:GroupChangedArgs =>
    val entity = it.entity
    val sprite = entity.view.sprite
    //sprite.setColor(0f, 0f, 0f, 0f)
  }

  /**
    * onEntityAdded
    * Fix up the sprite scale
    */
  pool.getGroup(Match.Scale).onEntityAdded += {it:GroupChangedArgs =>
    val entity = it.entity
    val sprite = entity.view.sprite
    if (entity.hasScale) {
      val scale = entity.scale
      sprite.scaleX = scale.x
      sprite.scaleY = scale.y
    }
  }


}
