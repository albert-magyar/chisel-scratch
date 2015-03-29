package ChiselScratch.USRP.chisel

import Chisel._

class AddSubWrapper(dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)
  val core = Module(new AddSub(dataWidth))
  io <> core.io

  io.i0.bits.setName("i0_tdata")
  io.i0.valid.setName("i0_tvalid")
  io.i0.ready.setName("i0_tready")

  io.i1.bits.setName("i1_tdata")
  io.i1.valid.setName("i1_tvalid")
  io.i1.ready.setName("i1_tready")

  io.sum.bits.setName("sum_tdata")
  io.sum.valid.setName("sum_tvalid")
  io.sum.ready.setName("sum_tready")

  io.diff.bits.setName("diff_tdata")
  io.diff.valid.setName("diff_tvalid")
  io.diff.ready.setName("diff_tready")
}
