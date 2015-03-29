package ChiselScratch

import Chisel._

object OrderedPrint {

  import ChiselError._

  var lastPrint: Option[Printf] = None

  def apply(message: String, args: Node*): Unit = {
    val p = new Printf(Module.current.whenCond && !Module.current.reset, message, args)
    Module.current.printfs += p
    Module.current.debug(p)
    p.inputs.foreach(Module.current.debug _)
    for (arg <- args)
      if (arg.isInstanceOf[Aggregate])
        ChiselErrors += new ChiselError(() => { "unable to printf aggregate argument " + arg }, arg.line)
    lastPrint.foreach(lastP => p.inputs += lastP) 
  }

}

class OrderedPrints extends Module {

  val io = new Bundle {
    val in = Bits(INPUT, 32)
    val out = Bits(OUTPUT, 32)
  }

  val regvec = Vec.fill(32){ (Reg(init = Bits(0))) }

  for (i <- 0 until 32) {
    printf("Vec[" + i + "] = %d\n", regvec(i))
  }

}

class OrderedPrintsTester(c: OrderedPrints) extends Tester(c) {
  poke(c.io.in, 7)
  step(4)
  expect(c.io.out, 7)
}