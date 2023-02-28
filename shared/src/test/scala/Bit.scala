import be.adamv.impuls.delta.BitRelayVar
import be.adamv.momentum.util.*
import be.adamv.momentum.*

import munit.FunSuite


class BitTest extends FunSuite:
  test("initial value setLow setHigh flip") {
    val fbs = new BitRelayVar(false)
    val tbs = new BitRelayVar(true)

    assert(!fbs.value)
    assert(tbs.value)
    assert(tbs.setLow())
    assert(!tbs.value)
    assert(fbs.setHigh())
    assert(fbs.value)
    assert(fbs.flip() != tbs.flip())
    assert(!fbs.value)
    assert(tbs.value)
  }

  test("oppositeView setLow setHigh flip") {
    val fbs = new BitRelayVar(false)
    val fbson = fbs.oppositeView

    assert(fbson.value)

    assert(!fbs.setLow())
    assert(!fbs.value)
    assert(fbson.value)

    assert(fbs.setHigh())
    assert(fbs.value)
    assert(!fbson.value)

    fbs.flip()
    assert(!fbs.value)
    assert(fbson.value)
  }

  test("oppositeHandle riseHandle fallHandle flipHandle") {
    val v: BitRelayVar = new BitRelayVar(false)
    val vOp = v.oppositeHandle

    assert(!v.value)

    vOp.riseHandle.tick()
    assert(!v.value)

    vOp.fallHandle.tick()
    assert(v.value)

    vOp.flipHandle.tick()
    assert(!v.value)

    vOp.set(true)
    assert(!v.value)
  }

  test("oppositeView riseView fallView flipView") {
    val v: BitRelayVar = new BitRelayVar(false)
    val vOp = v.oppositeView

    val (rises, risesRes) = newTrace[Unit]()
    val (falls, fallsRes) = newTrace[Unit]()
    val (flips, flipsRes) = newTrace[Unit]()

    vOp.riseView.adaptNow(rises)
    vOp.fallView.adaptNow(falls)
    vOp.flipView.adaptNow(flips)

    v.setLow()
    assert(risesRes().isEmpty && fallsRes().isEmpty && flipsRes().isEmpty)

    v.setHigh()
    assert(risesRes().isEmpty && fallsRes() == List(()) && flipsRes().isEmpty)

    v.flip()
    assert(risesRes().isEmpty && fallsRes().isEmpty && flipsRes() == List(()))

    v.set(true)
    assert(!vOp.value)
  }

  test("oppositeView involutory") {
    val fbs: BitRelayVar = new BitRelayVar(false)
    val fbs2 = fbs.oppositeView.oppositeView

    assert(!fbs.value)
    assert(!fbs2.value)

    assert(!fbs.setLow())
    assert(!fbs.value)
    assert(!fbs2.value)

    assert(fbs.setHigh())
    assert(fbs.value)
    assert(fbs2.value)

    fbs.flip()
    assert(!fbs.value)
    assert(!fbs2.value)
  }

  test("oppositeHandle involutory") {
    val fbs: BitRelayVar = new BitRelayVar(false)
    val fbs2 = fbs.oppositeHandle.oppositeHandle

    fbs2.riseHandle.tick()
    assert(fbs.value)

    fbs2.fallHandle.tick()
    assert(!fbs.value)

    fbs2.flipHandle.tick()
    assert(fbs.value)
  }

  test("oppositeHandle oppositeView") {
    val fbs: BitRelayVar = new BitRelayVar(false)
    val ophandle = fbs.oppositeHandle
    val opview = fbs.oppositeView

    ophandle.riseHandle.tick()
    assert(opview.value)

    ophandle.riseHandle.tick()
    assert(opview.value)

    ophandle.flipHandle.tick()
    assert(!opview.value)
  }
