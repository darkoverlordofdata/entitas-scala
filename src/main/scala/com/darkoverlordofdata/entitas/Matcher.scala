package com.darkoverlordofdata.entitas

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Matcher {
  private var _uniqueId = 0
  def uniqueId = {
    _uniqueId += 1
    _uniqueId
  }

  def distinctIndices(indices:Array[Int]):Array[Int] = {
      val indicesSet = new mutable.HashSet[Int]()
      for (index <- indices) indicesSet.add(index)
      indicesSet.toArray
  }

  def mergeIndices(matchers:Array[IMatcher]):Array[Int] = {
    val indices = new ListBuffer[Int]()
    for (matcher <- matchers) {
      if (matcher.indices.length != 1)
        throw new MatcherException(matcher)
      indices += matcher.indices(0)
    }
    indices.toArray
  }

  def allOf(args:Array[Int]): IAllOfMatcher = {
    val matcher = new Matcher()
    matcher.allOfIndices = distinctIndices(args.toArray)
    matcher
  }

  def allOf(args: IMatcher*): IAllOfMatcher = {
    val result = new ListBuffer[IMatcher]()
    for (arg <- args) result += arg
    allOf(mergeIndices(result.toArray))
  }

  def anyOf(args:Array[Int]): IAnyOfMatcher = {
    val matcher = new Matcher()
    matcher.anyOfIndices = distinctIndices(args.toArray)
    matcher
  }

  def anyOf(args: IMatcher*): IAnyOfMatcher = {
    val result = new ListBuffer[IMatcher]()
    for (arg <- args) result += arg
    anyOf(mergeIndices(result.toArray))
  }

}

class Matcher extends IAllOfMatcher with IAnyOfMatcher with INoneOfMatcher {

  private val _id = Matcher.uniqueId
  private var _indices = new Array[Int](0)
  private var toStringCache = ""

  override var anyOfIndices: Array[Int] = Array()
  override var allOfIndices: Array[Int] = Array()
  override var noneOfIndices: Array[Int] = Array()

  override def id: Int = _id

  override def indices: Array[Int] = {
    if (_indices.length == 0) {
      _indices = mergeIndices()
    }
    _indices
  }

  override def matches(entity: Entity): Boolean = {
    val matchesAllOf = if (allOfIndices.length == 0) true else entity.hasComponents(allOfIndices)
    val matchesAnyOf = if (anyOfIndices.length == 0) true else entity.hasAnyComponent(anyOfIndices)
    val matchesNoneOf = if (noneOfIndices.length == 0) true else !entity.hasAnyComponent(noneOfIndices)
    matchesAllOf && matchesAnyOf && matchesNoneOf
  }


  override def anyOf(indices: Array[IMatcher]): IAnyOfMatcher = {
    anyOf(Matcher.mergeIndices(indices))
  }

  override def anyOf(indices: Array[Int]): IAnyOfMatcher = {
    anyOfIndices = Matcher.distinctIndices(indices)
    _indices = new Array[Int](0)
    this
  }

  override def noneOf(indices: Array[IMatcher]): INoneOfMatcher = {
    noneOf(Matcher.mergeIndices(indices))
  }

  override def noneOf(indices: Array[Int]): INoneOfMatcher = {
    noneOfIndices = Matcher.distinctIndices(indices)
    _indices = new Array[Int](0)
    this
  }

  def mergeIndices():Array[Int]  = {
    var indicesList = new ListBuffer[Int]()
    if (allOfIndices.length > 0) indicesList ++= allOfIndices
    if (anyOfIndices.length > 0) indicesList ++= anyOfIndices
    if (noneOfIndices.length > 0) indicesList ++= noneOfIndices
    Matcher.distinctIndices(indicesList.toArray)
  }


  def onEntityAdded(): TriggerOnEvent = {
    new TriggerOnEvent(this, GroupEventType.OnEntityAdded)
  }

  def onEntityRemoved(): TriggerOnEvent = {
    new TriggerOnEvent(this, GroupEventType.OnEntityRemoved)
  }

  def onEntityAddedOrRemoved(): TriggerOnEvent = {
    new TriggerOnEvent(this, GroupEventType.OnEntityAddedOrRemoved)
  }

  override def toString:String = {
    if (toStringCache == "") {
      val sb = new StringBuilder()
      toStringHelper(sb, "AllOf", allOfIndices)
      toStringHelper(sb, "AnyOf", anyOfIndices)
      toStringHelper(sb, "NoneOf", noneOfIndices)
      toStringCache = sb.toString
    }
    toStringCache
  }
  private def toStringHelper(sb:StringBuilder, prefix:String, indexArray:Array[Int]) {
    if (indexArray.length > 0) {
      sb.append(prefix)
      sb.append("(")
      for (i <- indexArray.indices) {
        //sb.append(Pool.instance!!.componentName(i))
        sb.append(i.toString)
        if (i < indexArray.length-1) sb.append(",")
      }
      sb.append(")")
    }

  }

}
