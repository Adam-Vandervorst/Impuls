Given
```scala
trait Sink[A]:
  inline def scan[B, AA <: A](z: AA)(op: (AA, B) => AA): Sink[B] =
    var state: AA = z
    (b: B) =>
      state = op(state, b)
      self.set(state)
```
you can apply `z` with an initial value, and use `integrate` as the op, with `AA=A` and `B` a subtype of `Delta[B]`.

I.e. instead of an opaque `B`, you have a `Delta[B]`.
Now, let's say you map your Sink, instead of transforming every B (with `B => C`), you map with `Delta[B] => Delta[C]`.

Concretely, say `B=List` and `Delta[V]` is something like
```scala
enum LinkedListDelta[+A] extends Delta[List[A]]:
  case Prepend(a: A)
  case Tail
```
and you want to get the `length` of the list in a stream, instead of doing `liststream.map(_.length)` you do `liststream.dmap{case Prepend(_) => Incr; case Tail => Decr}` which is constant time instead of linear time.


