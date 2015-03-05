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
      case "TreePLRUTester" =>
        chiselMainTest(rArgs, () => Module(new TreePLRUTop(2,4))){
          c => new TreePLRUTester(c)
        }
      case "VLSTreePLRUTester" =>
        chiselMainTest(rArgs, () => Module(new VLSTreePLRUTop(2,4))){
          c => new VLSTreePLRUTester(c)
        }
      case "SplitDecoupledExampleTester" =>
        chiselMainTest(rArgs, () => Module(new SplitDecoupledExample(10))){
          c => new SplitDecoupledExampleTester(c)
        }
    }
  }
}

