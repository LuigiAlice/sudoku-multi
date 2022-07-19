import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun start(args: Array<String>)  {
    println("Sudoku Resolver")

    val sudoku = Sudoku()
    with(sudoku) {
        if (args.isNotEmpty() && args[0] == "generate") {
            println("Generating sudoku field:")
            val counter = measureTimedValue {
                generateField()
            }
            println("${counter.value} steps in ${counter.duration.toDouble(DurationUnit.SECONDS)}s")
        } else {
            if (args.isEmpty()) {
                println("Usage: generate | <file of sudoku field to resolve>")
                println("Resolving example field:")
                println(DemoFields.testField)
                initField(DemoFields.testField)
            } else {
                val field = readAllText(args[0])
                initField(field)
            }
            println("Parsed field:")
            printField()
            val counter = measureTimedValue {
                solveGameField()
            }
            println("${counter.value} moves in ${counter.duration.toDouble(DurationUnit.SECONDS)}s")
            if (!gameIsSolved()) println ("No solution found!")
        }
        printField()
    }
}
