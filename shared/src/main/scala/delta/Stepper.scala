package be.adamv.impuls
package delta

import be.adamv.momentum.{*, given}


enum StepperDelta extends Delta[Int]:
  case Incr, Decr
import StepperDelta.*


trait StepperHandle extends UDeltaSink[Int, StepperDelta]:
  self =>
  lazy val incrHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Incr)
  lazy val decrHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Decr)


trait StepperView extends UDeltaDescend[Int, StepperDelta]:
  self =>
  lazy val incrView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Incr => () }
  lazy val decrView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Decr => () }


class StepperRelayVar(i: Int) extends UDeltaRelayVar[Int, StepperDelta](i), StepperHandle, StepperView:
  def integration(dt: StepperDelta): Int = dt match
    case Incr => value + 1
    case Decr => value - 1

  def incr(n: Int = 1): Unit =
    (1 to n).foreach(_ => integrate(Incr))

  def decr(n: Int = 1): Unit =
    (1 to n).foreach(_ => integrate(Decr))
