import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    // run gradle task: wasm32Binaries

    println("Sudoku Resolver in Wasm32")

    val sudoku = Sudoku()
    with(sudoku) {
        initField(DemoFields.testField)

        printField()

        val counter = measureTimedValue {
            solveField()
        }
        println("${counter.value} Züge in ${counter.duration.inSeconds}s")
        printField()
    }
}