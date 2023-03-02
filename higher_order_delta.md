
```scala
enum BitSetDelta extends Delta[Boolean]:
  case Set1, Set0


enum BitSetDeltaDelta extends Delta[BitSetDelta]:
  case RisingEdge, FallingEdge

BitSetDeltaDelta =~= BitSetDelta =~= Boolean
```