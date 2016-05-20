package com.darkoverlordofdata.entitas

class EntityAlreadyHasComponentException(message:String, index:Int)
  extends Exception(s"$message\nEntity already has a component at index ($index)") {}

class EntityDoesNotHaveComponentException(message:String, index:Int)
  extends Exception(s"$message\nEntity does not have a component at index ($index)") {}

class EntityIsAlreadyReleasedException()
  extends Exception("Entity is already released!"){}

class EntityIsNotDestroyedException(message:String)
  extends Exception(s"$message\nEntity is not destroyed yet!"){}

class EntityIsNotEnabledException(message:String)
  extends Exception(s"$message\nEntity is not enabled"){}

class GroupObserverException(message:String)
  extends Exception(s"$message"){}

class MatcherException(matcher: IMatcher)
  extends Exception(s"matcher.indices.length must be 1 but was ${matcher.indices.size}"){}

class PoolDoesNotContainEntityException(entity: Entity, message:String)
  extends Exception(s"$message\nPool does not contain entity ${entity.toString()}"){}

class SingleEntityException(matcher: IMatcher)
  extends Exception(s"Multiple entities exist matching ${matcher.toString()}"){}



