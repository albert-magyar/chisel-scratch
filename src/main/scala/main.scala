package ChiselScratch

import Chisel._

object Main {
  def main(args: Array[String]): Unit = { 
    val rArgs  = args.slice(1, args.length)

    args(0) match {
      case "OrderedPrintsTester" =>
        chiselMainTest(rArgs, () => Module(new OrderedPrints())){
          c => new OrderedPrintsTester(c)
        }
    }
  }
}

