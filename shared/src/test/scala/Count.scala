package be.adamv.impuls

import be.adamv.impuls.delta.CountRelayVar
import be.adamv.momentum.util.*
import be.adamv.momentum.*

import munit.FunSuite


class CountTest extends FunSuite:
  test("value incr reset") {
    val cv = new CountRelayVar()

    assert(cv.value == 0)
    cv.incr()
    assert(cv.value == 1)
    cv.incr()
    assert(cv.value == 2)
    cv.incr(8)
    assert(cv.value == 10)
    cv.reset()
    assert(cv.value == 0)
    cv.incr(5)
    assert(cv.value == 5)
  }

  test("incrHandle resetHandle") {
    val cv = new CountRelayVar()

    cv.incrHandle.tick()
    assert(cv.value == 1)
    cv.incrHandle.tick()
    assert(cv.value == 2)
    for _ <- 1 to 8 do cv.incrHandle.tick()
    assert(cv.value == 10)
    cv.resetHandle.tick()
    assert(cv.value == 0)
    for _ <- 1 to 5 do cv.incrHandle.tick()
    assert(cv.value == 5)
  }

  test("incrView resetView") {
    val cv = new CountRelayVar()

    val (incrs, incrsRes) = newTrace[Unit]()
    val (resets, resetsRes) = newTrace[Unit]()

    cv.incrView.adaptNow(incrs)
    cv.resetView.adaptNow(resets)

    cv.incr()
    assert(incrsRes() == List(()) && resetsRes().isEmpty)
    cv.incr()
    assert(incrsRes() == List(()) && resetsRes().isEmpty)
    cv.incr(8)
    assert(incrsRes().length == 8 && resetsRes().isEmpty)
    cv.reset()
    assert(incrsRes().isEmpty && resetsRes() == List(()))
    cv.incr(5)
    assert(incrsRes().length == 5 && resetsRes().isEmpty)
  }