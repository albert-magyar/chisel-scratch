package ChiselScratch.USRP.basic

import Chisel._

class SplitStreamFIFO(dataWidth: Int = 16) extends Module {
  val io = new SplitStreamIO(dataWidth)

  val split_stream = Module(new SplitStream(dataWidth))
  val short_fifo0 = Module(new Queue(Bits(width = dataWidth + 1),16))
  val short_fifo1 = Module(new Queue(Bits(width = dataWidth + 1),16))

  val o0_tdata_int = Bits(width = dataWidth+1)
  val o0_tvalid_int = Bool()
  val o0_tready_int = Bool()

  val o1_tdata_int = Bits(width = dataWidth+1)
  val o1_tvalid_int = Bool()
  val o1_tready_int = Bool()

  split_stream.io.i_tdata := io.i_tdata
  split_stream.io.i_tvalid := io.i_tvalid
  split_stream.io.i_tlast := io.i_tlast
  io.i_tready := split_stream.io.i_tready

  o0_tvalid_int := split_stream.io.o0_tvalid
  o0_tdata_int := Cat(split_stream.io.o0_tlast,split_stream.io.o0_tdata)
  split_stream.io.o0_tready := o0_tready_int

  o1_tvalid_int := split_stream.io.o1_tvalid
  o1_tdata_int := Cat(split_stream.io.o1_tlast,split_stream.io.o1_tdata)
  split_stream.io.o1_tready := o1_tready_int

  short_fifo0.io.enq.bits := o0_tdata_int
  short_fifo0.io.enq.valid := o0_tvalid_int
  o0_tready_int := short_fifo0.io.enq.ready

  short_fifo1.io.enq.bits := o1_tdata_int
  short_fifo1.io.enq.valid := o1_tvalid_int
  o1_tready_int := short_fifo1.io.enq.ready

  io.o0_tvalid := short_fifo0.io.deq.valid
  io.o0_tdata := short_fifo0.io.deq.bits(dataWidth-1,0)
  io.o0_tlast := short_fifo0.io.deq.bits(dataWidth)
  short_fifo0.io.deq.ready := io.o0_tready

  io.o1_tvalid := short_fifo1.io.deq.valid
  io.o1_tdata := short_fifo1.io.deq.bits(dataWidth-1,0)
  io.o1_tlast := short_fifo1.io.deq.bits(dataWidth)
  short_fifo1.io.deq.ready := io.o1_tready
}
