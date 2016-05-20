package com.darkoverlordofdata.entitas.demo.systems

import com.darkoverlordofdata.entitas.demo.{Match}
import com.darkoverlordofdata.entitas.{Matcher, Pool, IExecuteSystem}


class ScoreRenderSystem (val pool:Pool) extends IExecuteSystem {
  lazy val group = pool.getGroup(Matcher.allOf(Match.Player, Match.Score))

  override def execute(): Unit = {

  }
}
