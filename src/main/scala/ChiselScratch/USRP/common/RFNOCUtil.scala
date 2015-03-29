package ChiselScratch.USRP.common

import Chisel._

class WithLast[+T <: Data](gen: T) extends Bundle {
  val data = gen.clone
  val last = Bool()
  override def clone: this.type = { new WithLast(gen).asInstanceOf[this.type]; }
}

object WithLast {
  def apply[T <: Data](gen: T): WithLast[T] = new WithLast(gen)
}

class Axi4Stream[+T <: Data](gen: T) extends DecoupledIO(WithLast(gen)) {
  def last: Bool = bits.last
  def data: T = bits.data
}

class StreamPayload(dataWidth: Int = 16) extends Bundle {
  val data = Bits(width = dataWidth)
  val last = Bool()
  override def clone: this.type
    = { new StreamPayload(dataWidth).asInstanceOf[this.type] };
}

object Sample {
  def apply(elementWidth: Int = 16) = {
    new Sample(Complex(SInt(width = elementWidth),SInt(width = elementWidth)),Bool())
  }
}

class Sample(val data: Complex[SInt], val last: Bool) extends Bundle {
  override def clone: this.type
    = { new Sample(data.clone,last.clone).asInstanceOf[this.type] };
  def + (r: Sample): Sample = {
    new Sample(new Complex(data.real + r.data.real, data.imag + r.data.imag), last && last)
  }
  def - (r: Sample): Sample = {
    new Sample(new Complex(data.real - r.data.real, data.imag - r.data.imag), last && last)
  }
}

object PackedSIMD {
  def apply(x: Bits, y: Bits, fullWidth: Int, numElements: Int, f: (Bits, Bits) => Data): Data = {
    var result:Seq[Data] = Seq[Data]()
    assert(fullWidth % numElements == 0)
    val elementWidth = fullWidth / numElements
    for (i <- 0 until fullWidth / elementWidth) {
      val x_elem = x((i+1)*elementWidth-1,i*elementWidth)
      val y_elem = y((i+1)*elementWidth-1,i*elementWidth)
      result = result :+ f(x_elem,y_elem)
    }
    Cat(result)
  }
}

object GroupDecoupled {
  def apply(signals: DecoupledIO[Data]*) = signals
}

object SyncDecoupled {
  def apply(ins: Seq[DecoupledIO[Data]], outs: Seq[DecoupledIO[Data]]): Unit = {
    val allInsValid = ins.map(_.valid).reduce(_ && _)
    val allOutsReady = outs.map(_.ready).reduce(_ && _)
    for (in <- ins) {
      val otherInsValid = ins.filter(_ != in).map(_.valid).reduce(_ && _)
      in.ready := otherInsValid && allOutsReady
    }
    for (out <- outs) {
      val otherOutsReady = outs.filter(_ != out).map(_.ready).reduce(_ && _)
      out.valid := otherOutsReady && allInsValid
    }
  }
}

object SplitDecoupled {
  def apply[T <: Data](in: DecoupledIO[T], n: Int): Seq[DecoupledIO[T]] = {
    val splitter = Module(new SplitDecoupled(in,n))
    splitter.io.in.valid := in.valid
    splitter.io.in.bits := in.bits
    in.ready := splitter.io.in.ready
    splitter.io.outs
  }
}

class SplitDecoupled[T <: Data](gen: DecoupledIO[T], n: Int) extends Module {
  val io = new Bundle {
    val in = gen.clone.flip
    val outs = Vec.fill(n){ gen.clone }
  }
}

class SplitDecoupledExample(val n: Int) extends Module {
  val io = new Bundle {
    val in = new DecoupledIO(Bits(width = 32)).flip
    val outs = Vec.fill(n) { new DecoupledIO(Bits(width = 32)) }
  }
  val split_in = SplitDecoupled(io.in,n)
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
