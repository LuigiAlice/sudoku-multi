class Sudoku {

    private val blockFieldSize: Int = 3
    private val gameFieldSize: Int = blockFieldSize * blockFieldSize     // quadratic field
    private lateinit var gameField:Array<Array<Field>>


    fun initField(field: String) {
        gameField = Array(gameFieldSize) { Array(gameFieldSize) { Field(0) } }

        field.lineSequence().filterNot { it.isBlank() }.forEachIndexed { rowNo, row ->
            row.split("""\s+""".toRegex()).forEachIndexed { colNo, col ->
                if (col != "." && col != "") {
                    gameField[rowNo][colNo] = Field(col.toInt(), true)
                }
            }
        }
    }

    fun printField() {
        gameField.forEachIndexed { idxr, col ->
            col.forEachIndexed { idxc, field ->
                if (field.number != 0) print(field.number) else print(".")
                print(" ")
                if (idxc % blockFieldSize == blockFieldSize - 1) print("  ")
            }
            println()
            if (idxr % blockFieldSize == blockFieldSize - 1)  println()
        }
    }

    fun generateField(): Int {
        initField("")

        var counter = 0
        val fieldNumbers = (0 until gameFieldSize*gameFieldSize).toList().shuffled()
        var currentFieldNo = 0

        // as long as not half of all fields are filled
        while (currentFieldNo < gameFieldSize*gameFieldSize / 2) {
            val fieldNumber = fieldNumbers[currentFieldNo]
            val row = fieldNumber / gameFieldSize
            val col = fieldNumber % gameFieldSize
            var isOk = false

            for (number in (gameField[row][col].number+1)..gameFieldSize) {
                gameField[row][col].number = number
                isOk = checkHorizontal(row, col) && checkVertical(row, col) && checkBlockField(row, col)
                if (isOk) break
            }

            if (!isOk) {
                gameField[row][col].number = 0
                break
            } else {
                currentFieldNo++
            }

            counter++
        }

        counter += solveGameField(maxTries = 100_000)

        // generate new one if not solvable
        if (!gameIsSolved()) counter += generateField()

        return counter
    }

    fun solveGameField(maxTries: Int = Int.MAX_VALUE): Int {
        var row = 0
        var col = 0
        var counter = 0

        // as long as not all fields are filled
        while (counter < maxTries
            && row < gameField.size && row > -1
            && col < gameField.size && col > -1
        ) {
            counter++

            if (checkAndIncrementField(row, col)) {
                // if ok, go to next field
                col = (col + 1) % gameFieldSize
                if (col == 0) row++
            } else {
                // if not ok, clear this field for new tries
                gameField[row][col].clearIfPossible()
                do {
                    // and go to previous field
                    col--
                    if (col == -1) {
                        col = gameFieldSize - 1
                        row--
                    }
                    // and look for the previous incrementable field (skip fixed ones)
                } while (row > -1 && !gameField[row][col].incrementIfPossible())
            }
        }
        return counter
    }

    fun gameIsSolved() = gameField.firstOrNull { it.firstOrNull { it.number == 0 }?.number == 0 } == null

    private inline fun checkAndIncrementField(row: Int, col: Int): Boolean {
        gameField[row][col].let { field ->
            // fixed numbers can not be changed
            if (!field.fixed) {
                while (field.number <= gameFieldSize) {
                    // if number never fits
                    if (field.number == 0
                        || !checkHorizontal(row, col)
                        || !checkVertical(row, col)
                        || !checkBlockField(row, col)
                    ) {
                        // then try another one
                        field.incrementIfPossible()
                    } else {
                        // number is ok, accept
                        return true
                    }
                }
            } else {
                // fixed number is always ok, accept
                return true
            }
        }
        return false
    }

    private inline fun checkHorizontal(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        for (c in 0 until gameFieldSize) {
            if (c != col && gameField[row][c].number == field.number)
                return false    // number already exists in row
        }
        return true // number is the only one
    }

    private inline fun checkVertical(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        for (r in 0 until gameFieldSize) {
            if (r != row && gameField[r][col].number == field.number)
                return false    // number already exists in column
        }
        return true // number is the only one
    }

    private inline fun checkBlockField(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        val blockRowStart: Int = (row / blockFieldSize) * blockFieldSize
        val blockColStart: Int = (col / blockFieldSize) * blockFieldSize
        for (r in 0 until blockFieldSize) {
            for (c in 0 until blockFieldSize) {
                val rr = blockRowStart + r
                val cc = blockColStart + c
                if (rr != row && cc != col
                    && gameField[rr][cc].number == field.number
                )
                    return false    // number already exists in block
            }
        }
        return true // number is the only one
    }

    private data class Field(var number: Int, var fixed: Boolean = false) {
        inline fun clearIfPossible() {
            if (!fixed) number = 0
        }

        inline fun incrementIfPossible(): Boolean {
            if (!fixed) number++
            return !fixed
        }
    }
}
