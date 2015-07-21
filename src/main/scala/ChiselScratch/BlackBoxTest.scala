package ChiselScratch

import Chisel._

class VscaleBlackBox extends BlackBox {
  val io = new Bundle {
    val clk = Bool(INPUT).setName("clk")
  }
}

class Vscale extends Module {
  val io = new Bundle {
    val clk = Bool(INPUT)
  }

  val bb = Module(new VscaleBlackBox())

  bb.io.clk := io.clk

}
