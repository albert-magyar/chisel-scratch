package ChiselScratch.USRP.basic

import Chisel._

class AddSubIO(val dataWidth: Int = 16) extends Bundle {
  val i0_tdata = Bits(INPUT,dataWidth*2)
  val i0_tlast = Bool(INPUT)
  val i0_tvalid = Bool(INPUT)
  val i0_tready = Bool(OUTPUT)

  val i1_tdata = Bits(INPUT,dataWidth*2)
  val i1_tlast = Bool(INPUT)
  val i1_tvalid = Bool(INPUT)
  val i1_tready = Bool(OUTPUT)

  val sum_tdata = Bits(OUTPUT,dataWidth*2)
  val sum_tlast = Bool(OUTPUT)
  val sum_tvalid = Bool(OUTPUT)
  val sum_tready = Bool(INPUT)

  val diff_tdata = Bits(OUTPUT,dataWidth*2)
  val diff_tlast = Bool(OUTPUT)
  val diff_tvalid = Bool(OUTPUT)
  val diff_tready = Bool(INPUT)
}

class AddSub(val dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)

  val int_tready = Bool()
  val int_tvalid = io.i0_tvalid && io.i1_tvalid
  val int_tlast = io.i0_tlast
  io.i0_tready := int_tvalid && int_tready
  io.i1_tready := int_tvalid && int_tready

  val sum_a = io.i0_tdata(dataWidth*2-1,dataWidth) + io.i1_tdata(dataWidth*2-1,dataWidth)
  val diff_a = io.i0_tdata(dataWidth*2-1,dataWidth) - io.i1_tdata(dataWidth*2-1,dataWidth)
  val sum_b = io.i0_tdata(dataWidth-1,0) + io.i1_tdata(dataWidth-1,0)
  val diff_b = io.i0_tdata(dataWidth-1,0) - io.i1_tdata(dataWidth-1,0)

  val int_tdata = Cat(sum_a, sum_b, diff_a, diff_b)

  val splitter = Module(new SplitStreamFIFO(4*dataWidth))

  splitter.io.i_tdata := int_tdata
  splitter.io.i_tlast := int_tlast
  splitter.io.i_tvalid := int_tvalid
  int_tready := splitter.io.i_tready

  io.sum_tdata := splitter.io.o0_tdata(4*dataWidth-1,2*dataWidth)
  io.sum_tlast := splitter.io.o0_tlast
  io.sum_tvalid := splitter.io.o0_tvalid
  splitter.io.o0_tready := io.sum_tready

  io.diff_tdata := splitter.io.o1_tdata(2*dataWidth-1,0)
  io.diff_tlast := splitter.io.o1_tlast
  io.diff_tvalid := splitter.io.o1_tvalid
  splitter.io.o1_tready := io.diff_tready
}
