import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun start(args: Array<String>)  {
    println("Sudoku Resolver")

    val sudoku = Sudoku()
    with(sudoku) {

        if (args.isEmpty()) {
            println("Usage: <file of sudoku field>")
            println("Example field:")
            println(DemoFields.testField)
            initField(DemoFields.testField)
        } else {
            val field = readAllText(args[0])
            println(field)
            initField(field)
        }
        println("Parsed field:")
        printField()
        val counter = measureTimedValue {
            solveField()
        }
        println("${counter.value} moves in ${counter.duration.toDouble(DurationUnit.SECONDS)}s")
        printField()
    }
}