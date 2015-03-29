package ChiselScratch.USRP.bundles

import Chisel._

class AddSub(val dataWidth: Int = 16) extends Module {
  val io = new ChiselScratch.USRP.common.AddSubIO(dataWidth)
  val splitter = Module(new SplitStreamFIFO(4*dataWidth))

  val ins_valid = io.i0.valid && io.i1.valid
  val int_ready = ins_valid && splitter.io.in.ready
  io.i0.ready := int_ready
  io.i1.ready := int_ready

  val sum_a = (io.i0.bits.data(dataWidth*2-1,dataWidth)
    + io.i1.bits.data(dataWidth*2-1,dataWidth))
  val diff_a = (io.i0.bits.data(dataWidth*2-1,dataWidth)
    - io.i1.bits.data(dataWidth*2-1,dataWidth))
  val sum_b = (io.i0.bits.data(dataWidth-1,0)
    + io.i1.bits.data(dataWidth-1,0))
  val diff_b = (io.i0.bits.data(dataWidth-1,0)
    - io.i1.bits.data(dataWidth-1,0))
  val int_data = Cat(sum_a, sum_b, diff_a, diff_b)
  splitter.io.in.valid := ins_valid
  splitter.io.in.bits.data := int_data
  splitter.io.in.bits.last := io.i0.bits.last

  io.sum.valid := splitter.io.o0.valid
  io.sum.bits.data := splitter.io.o0.bits.data(4*dataWidth-1,2*dataWidth)
  io.sum.bits.last := splitter.io.o0.bits.last
  splitter.io.o0.ready := io.sum.ready

  io.diff.valid := splitter.io.o1.valid
  io.diff.bits.data := splitter.io.o1.bits.data(2*dataWidth-1,0)
  io.diff.bits.last := splitter.io.o1.bits.last
  splitter.io.o1.ready := io.diff.ready
}
