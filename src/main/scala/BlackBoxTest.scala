package ChiselScratch

import Chisel._

class VscaleBlackBox extends BlackBox {
  val io = new Bundle {
    val clk = Bool().setName("clk")
  }
}
