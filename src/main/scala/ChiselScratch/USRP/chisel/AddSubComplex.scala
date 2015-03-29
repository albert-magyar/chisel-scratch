package ChiselScratch.USRP.chisel

import Chisel._
import ChiselScratch.USRP.common._

class AddSubComplex(dataWidth: Int = 16) extends Module {
  val io = new AddSubComplexIO(dataWidth)
  val sum_q = Module(new Queue(Sample(dataWidth),16))
  val diff_q = Module(new Queue(Sample(dataWidth),16))

  sum_q.io.enq.bits := io.i0.bits + io.i1.bits
  diff_q.io.enq.bits := io.i0.bits - io.i1.bits

  io.sum.bits := sum_q.io.deq.bits
  io.diff.bits := diff_q.io.deq.bits
  SyncDecoupled(GroupDecoupled(sum_q.io.deq,diff_q.io.deq),
    GroupDecoupled(io.sum,io.diff))
}
