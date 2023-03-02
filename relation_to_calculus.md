```
Base type Int
Increment: Delta[Int]
Multiply(6): Delta[Int]

Vector(1, 2) x 1  |  UpdateX(5) x Increment
Vector(5, 2) x 2  |  ClipXY(10) x Increment
Vector(5, 2) x 3

case class FV(nv: Double) extends Delta[Double]
1.0 | FV(2.)
2.0 | FV(6.6)
6.6

case class DV(dv: Double) extends Delta[Double]
1.0 | DV(2.)
3.0 | DV(6.6)
9.6
```