package com.darkoverlordofdata.entitas

object GroupEventType {
  sealed trait EnumVal
  case object OnEntityAdded extends EnumVal
  case object OnEntityRemoved extends EnumVal
  case object OnEntityAddedOrRemoved extends EnumVal
  val groupEventType = Seq(OnEntityAdded, OnEntityRemoved, OnEntityAddedOrRemoved)
}

class TriggerOnEvent(val trigger: Matcher, val eventType: GroupEventType.EnumVal) {}

class EntityReleasedArgs(val entity: Entity) extends EventArgs {}

class EntityChangedArgs(val entity: Entity, val index:Int, val component: IComponent) extends EventArgs {}

class ComponentReplacedArgs(val entity: Entity, val index:Int, val previous: IComponent, val replacement: IComponent) extends EventArgs {}

class GroupChangedArgs(val group: Group, val entity: Entity, val index:Int, val newComponent: IComponent) extends EventArgs {}

class GroupUpdatedArgs(val group: Group, val entity: Entity, val index:Int, val prevComponent: IComponent, val newComponent: IComponent) extends EventArgs {}

class PoolEntityChangedArgs(val pool: Pool, val entity: Entity) extends EventArgs {}

class PoolGroupChangedArgs(val pool: Pool, val group: Group) extends EventArgs {}


