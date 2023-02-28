package be.adamv.impuls
package delta


import scala.collection.mutable


enum SequenceDelta[T] extends Delta[mutable.ArrayDeque[T]]:
  case Update(i: Int, t: T)
  case Insert(i: Int, t: T)
  case Remove(i: Int)
  case Swap(i: Int, j: Int)
import SequenceDelta.*

object SequenceDelta:
  def map[T, U](f: T => U)(sd: SequenceDelta[T]): SequenceDelta[U] = sd match
    case Update(i, t) => Update(i, f(t))
    case Insert(i, t) => Insert(i, f(t))
    case Remove(i) => Remove(i)
    case Swap(i, j) => Swap(i, j)



/*

trait SequenceIntegrator[T] extends Integrator[mutable.ArrayDeque[T], SequenceDelta[T]]:
  self =>
  val updateSource: Source[SequenceDelta.Update[T]] = ???
  val insertSource: Source[SequenceDelta.Insert[T]] = ???
  val removeSource: Source[SequenceDelta.Remove[T]] = ???
  val swapSource: Source[SequenceDelta.Swap[T]] = ???
  val deltaSource: Source[SequenceDelta[T]] = ???

  override def integrate(dt: SequenceDelta[T]): mutable.ArrayDeque[T] = dt match
    case Update(i, t) => value.update(i, t); value
    case Insert(i, t) => value.insert(i, t); value
    case Remove(i) => value.remove(i); value
    case Swap(i, j) =>
      val vi = value(i)
      value(i) = value(j)
      value(j) = vi
      value

  def update(i: Int, f: T => T): Unit =
    val elem = value(i)
    integrate(Update(i, f(elem)))

  def insert(i: Int, t: T): Unit = integrate(Insert(i, t))
  inline def append(t: T): Unit = insert(value.length, t)
  inline def prepend(t: T): Unit = insert(0, t)

  def pop(i: Int): T =
    val elem = value(i)
    integrate(Remove(i))
    elem
  inline def popHead(): T = pop(0)
  inline def popLast(): T = pop(value.length - 1)

  def swap(i: Int, j: Int): Unit =
    assert(0 <= i && i < value.length && 0 <= j && j < value.length)
    integrate(Swap(i, j))

  lazy val length: Source[Int] = new Relay[Int] with CountIntegrator:
    var value: Int = 0 // initial length
    set(value)  // start the stream with the initial length

    // invariant under Update, Swap
    this.incrementChanges <-- self.appendChanges.mapTo(Incr)
    this.decrementChanges <-- self.removeChanges.mapTo(Decr)
  // invariant under Swap

  //  Insert, Remove
  def indexOf(t: T): Source[Int] = ???
  //  Insert, Remove, Swap
  def fold[T1 >: T](z: T1)(op: (T1, T1) => T1): Source[T1] = ???
  //  Update, Insert, Remove
  def foldLeft[S](z: S)(op: (S, T) => S): Source[S] = new Relay[S]:
    var value: S = z
    set(value)

    this <-- self.updateIntegrations.map( seq => seq.foldLeft(z)(op) )
    this <-- self.appendChanges.map{ case Append(t) => op(value, t) }
    this <-- self.removeIntegrations.map( seq => seq.foldLeft(z)(op) )
    this <-- self.swapIntegrations.map( seq => seq.foldLeft(z)(op) ) // not needed for `fold`


  //  Update, Insert optimize append, Remove, Swap

  def map[U](f: T => U): Source[collection.Seq[U]] = new Relay[collection.Seq[U]] with SequenceIntegrator[U]:
    var value: mutable.ArrayDeque[U] = self.value.map(f)
    set(value)

    this <-- self.deltaSource.map(SequenceDelta.map(f) andThen this.integrate)

  def filter(f: T => Boolean): Source[collection.Seq[T]] = new Relay[collection.Seq[T]] with SequenceIntegrator[T]:
    var value: mutable.ArrayDeque[T] = self.value.filter(f)
    set(value)

    this <-- self.deltaSource.map {
      case Update(i, t) =>
        val fi = imap(i); val p = f(t)
        if fi then
          if p then Update(fi, t)
          else Remove(fi)
        else
          if p then
            val ni = update_imap()
            Insert(ni, t)
      case Insert(i, t) =>
        if f(t) then
          val ni = update_imap()
          Insert(ni, t)
      case Remove(i) =>
        val fi = imap(i)
        if fi then
          Remove(fi)
      case Swap(i, j) =>
        val fi = imap(i); val fj = imap(j)
        if fi && fj then Swap(fi, fj)
        else if fi then ???
        else if fj then ???
    }.map(this.integrate)*/
