package ChiselScratch.USRP.bundles

import Chisel._
import ChiselScratch.USRP.common._

class SplitStreamFIFO(dataWidth: Int = 16) extends Module {
  val io = new SplitStreamIO(dataWidth)
  val split_stream = Module(new SplitStream(dataWidth))
  val short_fifo0 = Module(new Queue(new StreamPayload(dataWidth),16))
  val short_fifo1 = Module(new Queue(new StreamPayload(dataWidth),16))
  io.in <> split_stream.io.in
  short_fifo0.io.enq <> split_stream.io.o0
  short_fifo1.io.enq <> split_stream.io.o1
  io.o0 <> short_fifo0.io.deq
  io.o1 <> short_fifo1.io.deq
}
