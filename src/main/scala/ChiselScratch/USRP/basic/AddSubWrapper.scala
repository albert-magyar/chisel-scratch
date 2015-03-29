package ChiselScratch.USRP.basic

import Chisel._

class AddSubWrapper(dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)
  val core = Module(new AddSub(dataWidth))
  io <> core.io

  io.i0_tdata.setName("i0_tdata")
  io.i0_tvalid.setName("i0_tvalid")
  io.i0_tlast.setName("i0_tlast")
  io.i0_tready.setName("i0_tready")

  io.i1_tdata.setName("i1_tdata")
  io.i1_tvalid.setName("i1_tvalid")
  io.i1_tlast.setName("i1_tlast")
  io.i1_tready.setName("i1_tready")

  io.sum_tdata.setName("sum_tdata")
  io.sum_tvalid.setName("sum_tvalid")
  io.sum_tlast.setName("sum_tlast")
  io.sum_tready.setName("sum_tready")

  io.diff_tdata.setName("diff_tdata")
  io.diff_tvalid.setName("diff_tvalid")
  io.diff_tlast.setName("diff_tlast")
  io.diff_tready.setName("diff_tready")
}
