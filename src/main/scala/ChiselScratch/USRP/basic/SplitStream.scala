package ChiselScratch.USRP.basic

import Chisel._

class SplitStreamIO(dataWidth: Int = 16) extends Bundle {
  val i_tdata = Bits(INPUT,dataWidth)
  val i_tlast = Bool(INPUT)
  val i_tvalid = Bool(INPUT)
  val i_tready = Bool(OUTPUT)

  val o0_tdata = Bits(OUTPUT,dataWidth)
  val o0_tlast = Bool(OUTPUT)
  val o0_tvalid = Bool(OUTPUT)
  val o0_tready = Bool(INPUT)

  val o1_tdata = Bits(OUTPUT,dataWidth)
  val o1_tlast = Bool(OUTPUT)
  val o1_tvalid = Bool(OUTPUT)
  val o1_tready = Bool(INPUT)
}

class SplitStream(dataWidth: Int = 16) extends Module {
  val io = new SplitStreamIO(dataWidth)
  io.o0_tlast := io.i_tlast
  io.o0_tdata := io.i_tdata

  io.o1_tlast := io.i_tlast
  io.o1_tdata := io.i_tdata

  val outs_tready = io.o0_tready && io.o1_tready
  io.i_tready := outs_tready
  val o_tvalid = io.i_tvalid && outs_tready
  io.o0_tvalid := o_tvalid
  io.o1_tvalid := o_tvalid
}
