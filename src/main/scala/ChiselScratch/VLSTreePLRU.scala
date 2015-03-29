package ChiselScratch

import Chisel._

class VLSTreePLRU(sets: Int, ways: Int, vls_mask: Bits) extends TreePLRU(sets, ways) {
  assert(vls_mask.getWidth == ways)
  override val trees = List.fill(sets) { makeVLSSubtree(log2Up(ways), vls_mask) }

  def makeVLSSubtree(depth: Int, vls_mask: Bits): PLRUSubtree = {
    if (depth > 1) {
      new PLRUInteriorVLS(depth, vls_mask)
    } else {
      new PLRULeafVLS(vls_mask)
    }
  }

  def split_bits(in: Bits): Tuple2[Bits,Bits] = {
    val width = in.getWidth
    assert((width % 2) == 0)
    val left = in(width/2 - 1, 0)
    val right = in(width - 1, width/2)
    Tuple2(left,right)
  }

  protected class PLRULeafVLS(vls_mask: Bits) extends PLRULeaf with PLRUSubtree {
    val split_mask = split_bits(vls_mask)
    replace_right = replace_right | split_mask._1.andR
  }
  
  protected class PLRUInteriorVLS(depth: Int, vls_mask: Bits) extends PLRUInterior(depth) with PLRUSubtree {
    val split_mask = split_bits(vls_mask)
    replace_right = replace_right | split_mask._1.andR
    override val left_child = makeVLSSubtree(depth-1,split_mask._1)
    override val right_child = makeVLSSubtree(depth-1,split_mask._2)
  }
}

class VLSTreePLRUTop(sets: Int, ways: Int) extends Module {
  val io = new Bundle {
    val isHit = Bool(INPUT)
    val index = Bits(INPUT, log2Up(sets))
    val hitWay = Bits(INPUT, log2Up(ways))
    val victimWay = Bits(OUTPUT, log2Up(ways))
    val VLSMask = Bits(INPUT, ways)
  }

  val tree = new VLSTreePLRU(sets, ways, io.VLSMask)
  when (io.isHit) {
    tree.hit(io.index,io.hitWay)  
  } .otherwise {
    tree.miss(io.index)
  }

  io.victimWay := tree.way(io.index)
}

class VLSTreePLRUTester(c: VLSTreePLRUTop) extends Tester(c) {

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

  val vls_masks = List(1, 3, 7)
  for (vls_mask <- vls_masks) {
    poke(c.io.VLSMask, vls_mask)

    val r = scala.util.Random
    var count = 0
    for (count <- 0 to 1000) {
      poke(c.io.isHit, r.nextInt(2))
      poke(c.io.index, r.nextInt(4))
      poke(c.io.hitWay, r.nextInt(4))
      step(1)
      if ((vls_mask & 0x1) != 0) { expect(peek(c.io.victimWay) != 0, "Checking for replacement of VLS-allocated way 0.") }
      if ((vls_mask & 0x2) != 0) { expect(peek(c.io.victimWay) != 1, "Checking for replacement of VLS-allocated way 1.") }
      if ((vls_mask & 0x4) != 0) { expect(peek(c.io.victimWay) != 2, "Checking for replacement of VLS-allocated way 2.") }
      if ((vls_mask & 0x8) != 0) { expect(peek(c.io.victimWay) != 3, "Checking for replacement of VLS-allocated way 3.") }
    }
  }
}
