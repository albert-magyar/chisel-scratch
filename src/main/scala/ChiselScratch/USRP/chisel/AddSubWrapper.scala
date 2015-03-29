package ChiselScratch.USRP.chisel

import Chisel._

class AddSubWrapper(dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)
  val core = Module(new AddSub(dataWidth))
  io <> core.io

  io.i0.bits.data.setName("i0_tdata")
  io.i0.valid.setName("i0_tvalid")
  io.i0.bits.last.setName("i0_tlast")
  io.i0.ready.setName("i0_tready")

  io.i1.bits.data.setName("i1_tdata")
  io.i1.valid.setName("i1_tvalid")
  io.i1.bits.last.setName("i1_tlast")
  io.i1.ready.setName("i1_tready")

  io.sum.bits.data.setName("sum_tdata")
  io.sum.valid.setName("sum_tvalid")
  io.sum.bits.last.setName("sum_tlast")
  io.sum.ready.setName("sum_tready")

  io.diff.bits.data.setName("diff_tdata")
  io.diff.valid.setName("diff_tvalid")
  io.diff.bits.last.setName("diff_tlast")
  io.diff.ready.setName("diff_tready")
}
