package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Match}
import com.darkoverlordofdata.entitas.{Matcher, Pool, IExecuteSystem}


class HealthRenderSystem (val pool:Pool) extends IExecuteSystem {
  lazy val group = pool.getGroup(Matcher.allOf(Match.Position, Match.Health))

  override def execute(): Unit = {

  }
}
