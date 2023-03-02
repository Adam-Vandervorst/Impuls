package be.adamv.impuls

import be.adamv.momentum.{Source, Descend, Sink, adaptNow}
import be.adamv.momentum.concrete.{Var, Relay}


trait Delta[+T]

// handle
trait UDeltaSink[T, DT <: Delta[T]] extends Sink[T, Unit]:
  val dsink: Sink[DT, Unit]

// view
trait UDeltaDescend[T, DT <: Delta[T]] extends Source[T, Unit]:
  val ddescend: Descend[Unit, DT, Unit]

abstract class UDeltaRelayVar[T, DT <: Delta[T]](initial: T) extends Var[T](initial), UDeltaDescend[T, DT], UDeltaSink[T, DT]:
  val drelay: Relay[DT] = Relay[DT]
  override val ddescend: Descend[Unit, DT, Unit] = drelay
  override val dsink: Sink[DT, Unit] = drelay

  drelay.adaptNow(contramap(integration))

  // TODO compare with elm reducer function
  def integration(dt: DT): T

  def integrate(dt: DT): Unit = drelay.set(dt)
