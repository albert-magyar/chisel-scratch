package ChiselScratch

import Chisel._

class WithLast[+T <: Data](gen: T) extends Bundle {
  val data = gen.clone.asOutput
  val last = Bool(OUTPUT)
}

object WithLast {
  def apply[T <: Data](gen: T): WithLast[T] = new WithLast(gen)
}

class Axi4Stream[+T <: Data](gen: T) extends DecoupledIO(WithLast(gen)) {
  def last: Bool = bits.last
  def data: T = bits.data
  override def clone = new Axi4Stream(gen)
}

object PackedSIMD {
  def apply(x: Node, y: Node, elementWidth: Int, f: (Node, Node) => Node): Node = {
    var result = Seq.empty[Node]
    assert(x.width == y.width)
    assert(x.width % elementWidth == 0)
    for (i <- 0 until x.width / elementWidth) {
      x_elem = x((i+1)*elementWidth-1,i*elementWidth)
      y_elem = y((i+1)*elementWidth-1,i*elementWidth)
      result = result ++ f(x_elem,y_elem)
    }
    Cat(result)
  }
}

object SplitDecoupled {
  def apply[T <: Data](in: DecoupledIO[T], n: Int): Seq[DecoupledIO[T]] = {
    val outs = Seq.fill(n){ in.clone }
    var iready = Bool(true)
    for (i <- 0 until n) {
      iready = iready && outs(i).ready
      var ovalid = in.valid
      for (j <- 0 until n) {
        if (i != j) {
          ovalid = ovalid && outs(j).ready
        }
      }
      outs(i).valid := ovalid
      outs(i).bits := in.bits
    }
    in.ready := iready
    outs
  }
}

class SplitDecoupledExampleIO(n: Int) extends Bundle {
  val in = new DecoupledIO(Bits(width = 32)).flip
  val outs = Vec.fill(n) { new DecoupledIO(Bits(width = 32)) }
}

class SplitDecoupledExample(val n: Int) extends Module {
  val io = new SplitDecoupledExampleIO(n)
  val split_in = SplitDecoupled(io.in)
  for (i <- 0 until n) {
    io.outs(i) <> split_in(i)
  }
}

class SplitDecoupledExampleTester(c: SplitDecoupledExample) extends Tester(c) {

  poke(c.io.in.valid, 1)
  poke(c.io.in.bits, 15)

  for (i <- 0 until c.n) {
    poke(c.io.outs(i).ready, 1)
  }

  for (i <- 0 until c.n) {
    expect(c.io.outs(i).valid, 1)
  }

}
