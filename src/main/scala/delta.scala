package be.adamv.impuls

import be.adamv.momentum.{Source, Descend, Sink, adaptNow}
import be.adamv.momentum.concrete.{Var, Relay}

trait Delta[+T]

trait UDeltaSource[T, DT <: Delta[T]] extends Source[T, Unit]:
  val dsource: Descend[Unit, DT, Unit]

trait UDeltaSink[T, DT <: Delta[T]] extends Sink[T, Unit]:
  val dsink: Sink[DT, Unit]

abstract class UDeltaRelayVar[T, DT <: Delta[T]](initial: T) extends Var[T](initial), UDeltaSource[T, DT], UDeltaSink[T, DT]:
  val drelay: Relay[DT] = Relay[DT]
  override val dsource: Descend[Unit, DT, Unit] = drelay
  override val dsink: Sink[DT, Unit] = drelay

  drelay.adaptNow(contramap(integration))

  def integration(dt: DT): T

  def integrate(dt: DT): Unit = drelay.set(dt)
