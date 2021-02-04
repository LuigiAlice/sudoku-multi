import kotlinx.browser.document
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    println("Sudoku Resolver in JS")

    val sudoku = Sudoku()
    with(sudoku) {
        initField()

        document.writeln("<p>")
        printField()
        document.writeln("</p>")

        val counter = measureTimedValue {
            solveField()
        }
        document.writeln("<p>")
        document.writeln("${counter.value} Versuche in ${counter.duration.inSeconds}s")
        document.writeln("</p>")

        document.writeln("<p>")
        printField()
        document.writeln("</p>")
    }
}