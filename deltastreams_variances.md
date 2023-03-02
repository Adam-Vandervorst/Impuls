Given a domain T, e.g. Trit:
```scala
type Trit = -1 | 0 | 1


enum TritDelta extends Delta[Trit]:
  case ToPos, ToNeg, Neutralize, Negate


trait TritHandle extends UDeltaSink[Trit, TritDelta]:
  // i < 0 -> -1, 0 -> 0, i > 0 -> 1
  lazy val signHandle: Sink[Int, Unit] = math.signum

trait TritView extends UDeltaDescend[Trit, TritDelta]:
  // -1 -> T, 0 -> F, 1 -> T
  lazy val absView: BitView = new:
    override def get(u: Unit): Boolean = self.value != 0

    override val dsource: Descend[Unit, BitDelta, Unit] = self.ddescend.collect {
      case ToPos => BitDelta.Rise
      case ToNeg =>  BitDelta.Rise
      case Neutralize =>  BitDelta.Fall
    }
```
notice that the codomain of views is included in T,
and T is included in the domain of handles.

This corresponds to the variances of `Sink[-T]` and `Descend[+T]`.