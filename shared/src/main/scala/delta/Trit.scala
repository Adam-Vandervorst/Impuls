package be.adamv.impuls
package delta

import be.adamv.momentum.{*, given}
import be.adamv.impuls.*
import be.adamv.impuls.delta.BitDelta.Flip


type Trit = -1 | 0 | 1


enum TritDelta extends Delta[Trit]:
  case ToPos, ToNeg, Neutralize, Negate
import TritDelta.*


trait TritHandle extends UDeltaSink[Trit, TritDelta]:
  self =>
  lazy val toPosHandle: Sink[Unit, Unit] = self.dsink.contramapTo(ToPos)
  lazy val toNegHandle: Sink[Unit, Unit] = self.dsink.contramapTo(ToNeg)
  lazy val neutralizeHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Neutralize)
  lazy val negateHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Negate)

  lazy val signHandle: Sink[Int, Unit] = math.signum

  lazy val oppositeHandle: TritHandle = new TritHandle:
    override def set(v: Trit): Unit = self.set((-v).asInstanceOf[Trit])

    override val dsink: Sink[TritDelta, Unit] = self.dsink.contramap {
      case ToPos => ToNeg
      case ToNeg => ToPos
      case x => x
    }


trait TritView extends UDeltaDescend[Trit, TritDelta]:
  self =>
  lazy val toPosView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case ToPos => () }
  lazy val toNegView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case ToNeg => () }
  lazy val neutralizeView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Neutralize => () }
  lazy val negateView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Negate => () }

  lazy val oppositeView: TritView = new TritView:
    override def get(u: Unit): Trit = (-self.value).asInstanceOf[Trit]

    override val ddescend: Descend[Unit, TritDelta, Unit] = self.ddescend.map{
      case ToPos => ToNeg
      case ToNeg => ToPos
      case x => x
    }

  // -1 -> T, 0 -> F, 1 -> T
  lazy val absView: BitView = new:
    override def get(u: Unit): Boolean = self.value != 0

    override val ddescend: Descend[Unit, BitDelta, Unit] = self.ddescend.collect {
      case ToPos => BitDelta.Rise
      case ToNeg =>  BitDelta.Rise
      case Neutralize =>  BitDelta.Fall
    }

  // -1 -> F,  0 -> F,  1 -> T
  lazy val higherView: BitView = new:
    override def get(u: Unit): Boolean = self.value == 1

    override val ddescend: Descend[Unit, BitDelta, Unit] = self.ddescend.collect {
      case ToPos => BitDelta.Rise
      case ToNeg => BitDelta.Fall
      case Neutralize => BitDelta.Fall
      case Negate => BitDelta.Flip // to be value-independent, this assumes Negate is not propagated to here when the value is 0
    }

  // -1 -> F, 0 -> T, 1 -> T
  lazy val lowerView: BitView = new:
    override def get(u: Unit): Boolean = self.value != -1

    override val ddescend: Descend[Unit, BitDelta, Unit] = self.ddescend.collect {
      case ToPos => BitDelta.Rise
      case ToNeg => BitDelta.Fall
      case Neutralize => BitDelta.Rise
      case Negate => BitDelta.Flip
    }


class TritRelayVar(initial: Trit) extends UDeltaRelayVar[Trit, TritDelta](initial), TritHandle, TritView:
  self =>
  def integration(dt: TritDelta): Trit = dt match
    case ToPos => 1
    case ToNeg => -1
    case Neutralize => 0
    case Negate => (-value).asInstanceOf[Trit]

  def setPos(): Boolean =
    if value == 1 then false
    else
      integrate(if value == 0 then ToPos else Negate)
      true

  def setNeg(): Boolean =
    if value == -1 then false
    else
      integrate(if value == 0 then ToNeg else Negate)
      true

  def setZero(): Boolean =
    if value == 0 then false
    else
      integrate(Neutralize)
      true

  def negate(): Boolean =
    if value == 0 then false
    else
      integrate(Negate)
      true
