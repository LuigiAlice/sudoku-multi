import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main(args: Array<String>) {
    println("Sudoku Resolver")

    val sudoku = Sudoku()
    with(sudoku) {
        initField()
        printField()
        val counter = measureTimedValue {
            solveField()
        }
        println("${counter.value} Versuche in ${counter.duration.inSeconds}s")
        printField()
    }
}