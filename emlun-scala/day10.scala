object Main extends App {

  val nums = (for {
    line <- io.Source.stdin.getLines
    char: Char <- line
  } yield char.toInt).toList

  def process(lengths: List[Int], numbers: List[Int] = (0 to 255).toList, pos: Int = 0, skip: Int = 0): List[Int] = lengths match {
    case Nil => numbers
    case length :: restLengths => {
      val op1 = (numbers ++ numbers).splitAt(pos) match {
        case (newHead, back) => back.splitAt(length) match {
          case (front, newTail) => {
            val wrapLength =
              if (pos + length >= numbers.size)
                (pos + length) % numbers.size
              else 0
            val selection = front.reverse

            (selection.drop(length - wrapLength).take(wrapLength)
              ++ newHead.drop(wrapLength)
              ++ selection.take(length - wrapLength)
              ++ newTail
              ) take numbers.size
          }
        }
      }

      val nextPos = (pos + length + skip) % numbers.size
      val nextSkip = skip + 1

      process(restLengths, op1, nextPos, nextSkip)
    }
  }

  def solveA(lengths: List[Int]): Int = process(lengths) match {
    case a :: b :: tail => a * b
  }

  def solveB(bytes: List[Int]): String = {
    val padded = bytes ++ List(17, 31, 73, 47, 23)
    val bytesForAllRounds: List[Int] = (1 to 64).toList flatMap { i => padded }
    val sparseHash: List[Int] = process(bytesForAllRounds)
    val denseHash: List[Int] = sparseHash
      .grouped(16)
      .map { block =>
        block.reduce({ _ ^ _ })
      }
      .toList

    denseHash map { d => f"${d}%02x" } mkString ""
  }

  println(s"B: ${solveB(nums)}")
}
