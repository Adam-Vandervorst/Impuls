package be.adamv.impuls
package delta

import be.adamv.momentum.{*, given}
import be.adamv.impuls.*


// Needs knowledge about the state to know the new state
enum BitFlipDelta extends Delta[Boolean]:
  case Flip

// Needs knowledge about the state to know the delta
enum BitSetDelta extends Delta[Boolean]:
  case Rise, Fall

enum BitDelta extends Delta[Boolean]:
  case Rise, Fall, Flip
import BitDelta.*


trait BitHandle extends UDeltaSink[Boolean, BitDelta]:
  self =>
  lazy val riseHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Rise)
  lazy val fallHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Fall)
  lazy val flipHandle: Sink[Unit, Unit] = self.dsink.contramapTo(Flip)

  lazy val oppositeHandle: BitHandle = new BitHandle:
    override def set(v: Boolean): Unit = self.set(!v)

    override val dsink: Sink[BitDelta, Unit] = self.dsink.contramap{
      case Rise => Fall
      case Fall => Rise
      case Flip => Flip
    }


trait BitView extends UDeltaDescend[Boolean, BitDelta]:
  self =>
  lazy val riseView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Rise => () }
  lazy val fallView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Fall => () }
  lazy val flipView: Descend[Unit, Unit, Unit] = self.ddescend.collect{ case Flip => () }

  lazy val oppositeView: BitView = new BitView:
    override def get(u: Unit): Boolean = !self.value

    override val ddescend: Descend[Unit, BitDelta, Unit] = self.ddescend.map{
      case Rise => Fall
      case Fall => Rise
      case Flip => Flip
    }


class BitRelayVar(initial: Boolean) extends UDeltaRelayVar[Boolean, BitDelta](initial), BitHandle, BitView:
  self =>
  def integration(dt: BitDelta): Boolean =
    val res = dt match
      case Rise => true
      case Fall => false
      case Flip => !value
    res

  /// Returns if the value was changed
  def setHigh(): Boolean =
    if value then false
    else
      integrate(Rise)
      true

  /// Returns if the value was changed
  def setLow(): Boolean =
    if value then
      integrate(Fall)
      true
    else false

  /// Returns the new value
  def flip(): Boolean =
    integrate(Flip)
    value

/*
inline def dmap[B](df: BitDelta => B): Source[B] =
  (g: B => _) =>
    relayRise.register(a => g(df(a)))
    relayFall.register(a => g(df(a)))
    relayFlip.register(a => g(df(a)))

inline def pdmap[B](inline drise: Rise.type => B,
                    inline dfall: Fall.type => B,
                    inline dflip: Flip.type => B): Source[B] =
  (g: B => _) =>
    relayRise.register(a => g(drise(a)))
    relayFall.register(a => g(dfall(a)))
    relayFlip.register(a => g(dflip(a)))

def filter(p: Boolean => Boolean): Source[Boolean] =
  (g: Boolean => _) => r(a => if p(a) then g(a))

def dfilter(p: BitDelta => Boolean): Source[Boolean] =
  (g: Boolean => _) =>
    ???

def collect[B](pf: PartialFunction[Boolean, B]): Source[B] =
  (g: B => _) => r(a => pf.unapply(a).foreach(g))

def dcollect[B](pf: PartialFunction[BitDelta, B]): Source[B] =
  (g: B => _) =>
    relayRise.register(a => pf.unapply(a).foreach(g))
    relayFall.register(a => pf.unapply(a).foreach(g))
    relayFlip.register(a => pf.unapply(a).foreach(g))

def scanLeft[B](z: B)(op: (B, Boolean) => B): Source[B] =
  var state: B = z
  (g: B => _) =>
    g(state)
    r(a => {
      state = op(state, a)
      g(state)
    })

def dscanLeft[B](z: B)(op: (B, BitDelta) => B): Source[B] =
  var state: B = z
  (g: B => _) =>
    g(state)
    relayRise.register(a => {
      state = op(state, a)
      g(state)
    })
    relayFall.register(a => {
      state = op(state, a)
      g(state)
    })
    relayFlip.register(a => {
      state = op(state, a)
      g(state)
    })
*/