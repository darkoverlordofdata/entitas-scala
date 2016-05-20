package com.darkoverlordofdata.entitas.demo

import scala.scalajs.js.{JSON, JSApp}
import scala.scalajs.js
import org.scalajs.dom
import js.Dynamic.{ global => g }
import com.scalawarrior.scalajs.createjs._
import com.scalawarrior.scalajs.createjs.Sound

object MainApp extends JSApp {
  def main(): Unit = {
    Platform.initialize()
  }
}

object Platform {
  val PROJECT = "project.dt"        /** Overlap2D Project data */
  val SCENE = "scenes/MainScene.dt" /** Overlap2D Scene data */
  val ATLAS = "orig/pack.png"       /** Overlap2D Packed atlas */

  var delta = 0.0f
  lazy val game = { new GameScene() }
  lazy val stage  = { new Stage("shmupwarz") }
  lazy val width = { stage.canvas.width }
  lazy val height = { stage.canvas.height }
  lazy val background = new Sprite(sprites, "background")

  var shoot = false
  var mouseX = 0.0f
  var mouseY = 0.0f


  val manifest = js.Array(  /** spritesheet uses the overlap2d pack.atlas info */
    js.Dictionary("src" -> "spritesheet.json", "id" -> "resources", "type" -> "spritesheet"),
    js.Dictionary("src" -> s"assets/$PROJECT", "id" -> "project", "type" -> "json"),
    js.Dictionary("src" -> s"assets/$SCENE", "id" -> "scene", "type" -> "json")
  )

  lazy val resources = { new LoadQueue(true) }
  lazy val project = { JSON.parse(JSON.stringify(resources.getResult("project").asInstanceOf[js.Any])) }
  lazy val scene = { JSON.parse(JSON.stringify(resources.getResult("scene").asInstanceOf[js.Any])) }
  lazy val sprites:SpriteSheet = { resources.getResult("resources").asInstanceOf[SpriteSheet] }
  lazy val libraryItems = { project.libraryItems.asInstanceOf[js.Dictionary[js.Dynamic]] }

  def getResource(name:String):String = {
    libraryItems.get(name) match {
      case Some(entity) => {
        val sImages = entity.composite.sImages.asInstanceOf[js.Array[js.Dynamic]]
        val imageName  = sImages(0).imageName.asInstanceOf[String]
        imageName
      }
      case _ => ""
    }
  }

  def getSprite(name:String):Sprite = {
    new Sprite(sprites, name)
  }

  def getLayer(name:String):Int  = {
    libraryItems.get(name) match {
      case Some(entity) => {
        Layer.withName(entity.layerName.asInstanceOf[String].toUpperCase).id
      }
      case _ => 0
    }
  }


  def initialize() = {

    Sound.registerSound("assets/sfx/pew.ogg", "pew")
    Sound.registerSound("assets/sfx/asplode.ogg", "asplode")
    Sound.registerSound("assets/sfx/smallasplode.ogg", "smallasplode")

    resources.addEventListener("complete", loaded)
    resources.loadManifest(manifest)
    resources.load()

    g.addEventListener("keydown",
      (e: dom.KeyboardEvent) => {if (e.keyCode == 90) shoot = true}, false)
    g.addEventListener("keyup",
      (e: dom.KeyboardEvent) => {if (e.keyCode == 90) shoot = false}, false)
    Platform.stage.canvas.addEventListener("mousedown",
      (e: dom.MouseEvent) => {shoot = true})
    Platform.stage.canvas.addEventListener("mouseup",
      (e: dom.MouseEvent) => {shoot = false})
    Platform.stage.canvas.addEventListener("mousemove",
      (e: dom.MouseEvent) => {
        mouseX = e.clientX.toFloat
        mouseY = height - e.clientY.toFloat
      }
    )


  }

  def play(sound:String): Unit = {
    Sound.play(sound)
  }

  def loaded = (e: Object)  => {
    Ticker.setFPS(60.0)
    Ticker.addEventListener("tick", render)
    game.start()
    true
  }

  def render = (e: TickerEvent) => {
    delta = e.delta.toFloat/1000.0f
    game.render()
  }

}


