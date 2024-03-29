import kotlinx.browser.document
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    // run gradle task: jsBrowserDevelopmentRun

    println("Sudoku Resolver in JS")

    val sudoku = Sudoku()
    with(sudoku) {
        initField(DemoFields.testField)

        document.writeln("<p>")
        print(printField())
        document.writeln("</p>")

        val counter = measureTimedValue {
            solveField()
        }
        document.writeln("<p>")
        document.writeln("${counter.value} moves in ${counter.duration.toDouble(DurationUnit.SECONDS)}s")
        document.writeln("</p>")

        document.writeln("<p>")
        print(printField())
        document.writeln("</p>")
    }
}
