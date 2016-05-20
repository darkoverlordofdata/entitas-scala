package com.darkoverlordofdata.entitas

trait IComponent{}

trait IMatcher {
  def id:Int
  def indices:Array[Int]
  def matches(entity: Entity):Boolean
}

trait ICompoundMatcher extends IMatcher {
  var allOfIndices:Array[Int]
  var anyOfIndices:Array[Int]
  var noneOfIndices:Array[Int]
}

trait INoneOfMatcher extends ICompoundMatcher{}

trait IAnyOfMatcher extends ICompoundMatcher {
  def noneOf(indices:Array[IMatcher]): INoneOfMatcher
  def noneOf(indices:Array[Int]): INoneOfMatcher
}

trait IAllOfMatcher extends ICompoundMatcher{
  def anyOf(indices:Array[IMatcher]): IAnyOfMatcher
  def anyOf(indices:Array[Int]): IAnyOfMatcher
  def noneOf(indices:Array[IMatcher]): INoneOfMatcher
  def noneOf(indices:Array[Int]): INoneOfMatcher
}

trait ISystem {}

trait ISetPool {
  def setPool(pool: Pool)
}

trait IInitializeSystem extends ISystem {
  def initialize()
}

trait IExecuteSystem extends ISystem {
  def execute()
}

trait IReactiveExecuteSystem extends ISystem {
  def execute(entities:Array[Entity])
}

trait IReactiveSystem extends IReactiveExecuteSystem{
  val trigger: TriggerOnEvent
}

trait IMultiReactiveSystem extends IReactiveExecuteSystem{
  val triggers:Array[TriggerOnEvent]
}

trait IEnsureComponents {
  val ensureComponents: IMatcher
}

trait IExcludeComponents {
  val excludeComponents: IMatcher
}

trait IClearReactiveSystem {
  val clearAfterExecute:Boolean
}
