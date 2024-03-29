import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    // run gradle task: wasm32Binaries

    println("Sudoku Resolver in Wasm32")

    val sudoku = Sudoku()
    with(sudoku) {
        initField(DemoFields.testField)

        print(printField())

        val counter = measureTimedValue {
            solveField()
        }
        println("${counter.value} moves in ${counter.duration.toDouble(DurationUnit.SECONDS)}s")
        print(printField())
    }
}
