package be.adamv.impuls
package delta

import be.adamv.momentum.{*, given}
import be.adamv.impuls.*

enum CountDelta extends Delta[Int]:
  case Incr, Reset
import CountDelta.*


trait CountHandle extends UDeltaSink[Int, CountDelta]:
  self =>
  lazy val incrHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Incr)
  lazy val resetHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Reset)

  def divHandle(n: Int): CountHandle = new:
    var k = 1

    override def set(v: Int): Unit = self.set(v/n)

    override val dsink: Sink[CountDelta, Unit] = self.dsink.contrafilter {
      case Incr =>
        k += 1
        if k == n then
          k = 1
          true
        else false
      case Reset =>
        k = 1
        true
    }
  end divHandle

  def modHandle(n: Int): CountHandle = new:
    var k = 1

    override def set(v: Int): Unit = self.set(v % n)

    override val dsink: Sink[CountDelta, Unit] = self.dsink.contracollect {
      case Incr =>
        k += 1
        if k == n then
          k = 1
          Reset
        else Incr
      case Reset =>
        Reset
    }
  end modHandle


trait CountView extends UDeltaSource[Int, CountDelta]:
  self =>
  lazy val incrView: Descend[Unit, Unit, Unit] = self.dsource.collect{ case Incr => () }
  lazy val resetView: Descend[Unit, Unit, Unit] = self.dsource.collect{ case Reset => () }

  def divView(n: Int): CountView = new CountView:
    var k = 1

    override def get(u: Unit): Int = self.value/n

    override val dsource: Descend[Unit, CountDelta, Unit] = self.dsource.filter{
      case Incr =>
        k += 1
        if k == n then
          k = 1
          true
        else false
      case Reset =>
        k = 1
        true
    }
  end divView

  def modView(n: Int): CountView = new CountView:
    var k = 1

    override def get(u: Unit): Int = self.value % n

    override val dsource: Descend[Unit, CountDelta, Unit] = self.dsource.collect {
      case Incr =>
        k += 1
        if k == n then
          k = 1
          Reset
        else Incr
      case Reset =>
        Reset
    }
  end modView

class CountRelayVar() extends UDeltaRelayVar[Int, CountDelta](0), CountHandle, CountView:
  def integration(dt: CountDelta): Int = dt match
    case Incr => value + 1
    case Reset => 0

  def incr(n: Int = 1): Unit =
    (1 to n).foreach(_ => integrate(Incr))

  def reset(): Unit =
    integrate(Reset)