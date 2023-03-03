package be.adamv.impuls
package delta

import be.adamv.momentum.{*, given}

import scala.collection.mutable


enum TreeMapDelta[K : Ordering, V] extends Delta[mutable.TreeMap[K, V]]:
  case Insert[KK : Ordering, VV](k: KK, v: VV) extends TreeMapDelta[KK, VV]
  case Delete[KK : Ordering, VV](k: KK) extends TreeMapDelta[KK, VV]

  def mapValues[W](f: V => W): TreeMapDelta[K, W] = this match
    case Insert(i, t) => Insert(i, f(t))
    case Delete(i) => Delete(i)
import TreeMapDelta.*


trait TreeMapHandle[K, V] extends UDeltaSink[mutable.TreeMap[K, V], TreeMapDelta[K, V]]:
  self =>
  given ord: Ordering[K]
  lazy val insertHandle: Sink[(K, V), Unit] = self.dsink.contramap(Insert[K, V])
  lazy val deleteHandle: Sink[K, Unit] = self.dsink.contramap(Delete[K, V])


trait TreeMapView[K, V] extends UDeltaDescend[mutable.TreeMap[K, V], TreeMapDelta[K, V]]:
  self =>
  given ord: Ordering[K]
  lazy val insertView: Descend[Unit, (K, V), Unit] = self.ddescend.collect { case Insert(k, v) => (k, v) }
  lazy val deleteView: Descend[Unit, K, Unit] = self.ddescend.collect { case Delete(k) => k }

  def mapValues[W](f: V => W): TreeMapView[K, W] = new TreeMapView[K, W]:
    val ord: Ordering[K] = ord
    override def get(e: Unit): mutable.TreeMap[K, W] = self.get(e).map((k, v) => (k, f(v)))
    override val ddescend: Descend[Unit, TreeMapDelta[K, W], Unit] = self.ddescend.map(_.mapValues(f))


class TreeMapRelayVar[K : Ordering, V](initial: mutable.TreeMap[K, V]) extends UDeltaRelayVar[mutable.TreeMap[K, V], TreeMapDelta[K, V]](initial):
  val ord: Ordering[K] = summon

  override def integration(dt: TreeMapDelta[K, V]): mutable.TreeMap[K, V] = dt match
    case Insert(k, v) => value.addOne(k, v) // overwrites
    case Delete(k) => value.subtractOne(k)

  def insert(k: K, v: V): Unit =
    integrate(Insert(k, v))

  def delete(k: K): Unit =
    integrate(Delete(k))

  def deleteAll(p: (K, V) => Boolean): Int =
    val todo = mutable.ArrayBuffer.empty[K]
    value.foreachEntry { (k, v) =>
      if p(k, v) then todo.addOne(k)
    }
    todo.foreach(delete)
    todo.length


