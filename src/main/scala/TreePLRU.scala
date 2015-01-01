package ChiselScratch

import Chisel._

abstract class ReplacementPolicy {
  def way(index: UInt): UInt
  def miss(index: UInt): Unit
  def hit(index: UInt, way: UInt): Unit
}

trait PLRUSubtree {
  def victim_way: UInt
  def replaceLRU: Unit
  def hit(way: UInt): Unit
}

class TreePLRU(sets: Int, ways: Int) extends ReplacementPolicy {
  val trees = List.fill(sets) { makeSubtree(log2Up(ways)) }
  val victim_way = UInt()
  victim_way := UInt(0)
  def way(index: UInt): UInt = {
    val index1H = UIntToOH(index)
    trees.zipWithIndex.foreach{ case (x,i) => when(index1H(i)) { victim_way := x.victim_way } }
    victim_way
  }
  def miss(index: UInt): Unit = {
    val index1H = UIntToOH(index)
    trees.zipWithIndex.foreach{ case (x,i) => when(index1H(i)) { x.replaceLRU } }
  }
  def hit(index: UInt, way: UInt): Unit = {
    val index1H = UIntToOH(index)
    trees.zipWithIndex.foreach{ case (x,i) => when(index1H(i)) { x.hit(way) } }
  }

  def makeSubtree(depth: Int): PLRUSubtree = {
    if (depth > 1) {
      new PLRUInterior(depth)
    } else {
      new PLRULeaf()
    }
  }

  protected class PLRULeaf extends PLRUSubtree {
    val lru = Reg(init = UInt(0,width=1))
    var replace_right = lru
    def victim_way = replace_right
    def replaceLRU: Unit = { lru := ~lru }
    def hit(way: UInt): Unit = { lru := ~way(0) }
  }

  protected class PLRUInterior(depth: Int) extends PLRUSubtree {
    val lru = Reg(init = UInt(0,width=1))
    val left_child = makeSubtree(depth-1)
    val right_child = makeSubtree(depth-1)
    var replace_right = lru
    def victim_way = Cat(replace_right,Mux(replace_right.toBool,right_child.victim_way,left_child.victim_way))
    def replaceLRU: Unit = {
      when (replace_right.toBool) { right_child.replaceLRU }
      .otherwise { left_child.replaceLRU } 
      lru := ~lru
    }
    def hit(way: UInt): Unit = {
      lru := ~way(depth-1)
      when (way(depth-1)) { right_child.hit(way) }
      .otherwise { left_child.hit(way) }
    }
  }
}

class TreePLRUTop(sets: Int, ways: Int) extends Module {
  val io = new Bundle {
    val isHit = Bool(INPUT)
    val index = Bits(INPUT, log2Up(sets))
    val hitWay = Bits(INPUT, log2Up(ways))
    val victimWay = Bits(OUTPUT, log2Up(ways))
  }

  val tree = new TreePLRU(sets, ways)
  when (io.isHit) {
    tree.hit(io.index,io.hitWay)  
  } .otherwise {
    tree.miss(io.index)
  }

  io.victimWay := tree.way(io.index)
}

class TreePLRUTester(c: TreePLRUTop) extends Tester(c) {

  expect(c.io.victimWay,0)

  poke(c.io.isHit, 0)
  poke(c.io.index, 0)
  step(1)

  expect(c.io.victimWay,2)

  poke(c.io.isHit, 0)
  poke(c.io.index, 0)
  step(1)

  expect(c.io.victimWay,1)

  poke(c.io.isHit, 0)
  poke(c.io.index, 0)
  step(1)

  expect(c.io.victimWay,3)

  poke(c.io.isHit, 0)
  poke(c.io.index, 0)
  step(1)

  expect(c.io.victimWay,0)

  poke(c.io.isHit, 1)
  poke(c.io.index, 0)
  poke(c.io.hitWay, 0)
  step(1)

  expect(c.io.victimWay,2)

  poke(c.io.isHit, 1)
  poke(c.io.index, 0)
  poke(c.io.hitWay, 0)
  step(1)

  expect(c.io.victimWay,2)
}
