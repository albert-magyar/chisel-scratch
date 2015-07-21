package ChiselScratch

import Chisel._

class TestIO extends Bundle {
  val in = Decoupled(Bits(width = 32)).flip
  val test = Vec.fill(10){ Bits(INPUT, width = 32) }
  val out = Decoupled(Bits(width = 32))
}

class RepeatedLBarrelShift(n: Int) extends Module {
  val io = new TestIO
  io.in.ready := io.out.ready
  io.out.valid := io.in.valid
  val test = Vec.fill(n){ Bits(width = 32) }
  for (i <- 0 to n-2) {
    test(i+1) := Cat(test(i)(30,0),test(i)(31))
  }
  io.out.bits := test(n-2)
}
