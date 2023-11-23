import be.adamv.impuls.delta.TreeMapRelayVar
import be.adamv.momentum.util.*
import be.adamv.momentum.*

import munit.FunSuite
import collection.mutable


class TreeMapTest extends FunSuite:
  test("initial value insert delete") {
    val tm = TreeMapRelayVar[Int, Option[String]](mutable.TreeMap.empty)

    println(tm.value)
    tm.insert(1, Some("thing"))
    println(tm.value)
  }
