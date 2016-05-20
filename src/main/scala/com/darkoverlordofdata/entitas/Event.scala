package com.darkoverlordofdata.entitas

import scala.collection.mutable.ListBuffer

trait EventArgs {}

class Event[T]() {

  private var invokers = new ListBuffer[T => Unit]()

  def apply(args : T) {
    invokers.foreach(_(args))
  }

  def +=(invoker : T => Unit) {
    invokers += invoker
  }

  def -=(invoker : T => Unit) {
    invokers -= invoker
  }

  def clear() = {
    invokers.clear()
  }
}
