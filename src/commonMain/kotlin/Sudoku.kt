import kotlin.random.Random

class Sudoku {

    private val blockFieldSize: Int = 3
    private val gameFieldSize: Int = blockFieldSize * blockFieldSize     // quadratic field
    private lateinit var gameField:Array<Array<Field>>


    fun initField(field: String = "") {
        gameField = Array(gameFieldSize) { Array(gameFieldSize) { Field(0) } }

        field.lineSequence().filterNot { it.isBlank() }.forEachIndexed { rowNo, row ->
            row.split("""\s+""".toRegex()).forEachIndexed { colNo, col ->
                if (col != "." && col != "") {
                    gameField[rowNo][colNo] = Field(col.toInt(), true)
                }
            }
        }
    }

    fun printField(): String = StringBuilder().apply {
            gameField.forEachIndexed { idxr, col ->
                col.forEachIndexed { idxc, field ->
                    if (field.number != 0) append(field.number) else append(".")
                    append(" ")
                    if (idxc % blockFieldSize == blockFieldSize - 1) append("  ")
                }
                appendLine()
                if (idxr % blockFieldSize == blockFieldSize - 1) appendLine()
            }
        }.toString()


    fun generateField(): Int {
        initField()

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
                isOk = checkAllRules(row, col)
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

        counter += solveField(maxTries = 1_000_000)

        // generate new one if not solvable
        if (!gameIsSolved()) counter += generateField()

        return counter
    }

    /*
        fun generateField(): Int {
        initField()

        var counter = 0


        for (fieldNumber in 1..gameFieldSize) {
            // try to place randomly in each block
            var blockNo = 0
            while (blockNo < gameFieldSize) {
                var isOK = false
                for (position in (0 until gameFieldSize).shuffled()) {
                    counter++
                    val row = (blockNo / blockFieldSize) + position / blockFieldSize
                    val col = ((blockNo % blockFieldSize) * blockFieldSize) + position % blockFieldSize
                    if (!gameField[row][col].isEmpty())
                        continue
                    gameField[row][col].number = fieldNumber
                    isOK = checkAllRules(row, col)
                    if (!isOK) {
                        gameField[row][col].clear()
                        continue
                    } else {
                        break
                    }
                }
                if (!isOK) {
                    blockNo--
                }
                else {
                    blockNo++
                }
            }
        }


        return counter
    }

     */

    fun solveField(maxTries: Int = Int.MAX_VALUE): Int {
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

    fun deleteRandomNumbers(amount:Int) {
        var i = 0
        while (i < amount && !isCompletelyEmpty()) {
            val fieldNumber = Random.nextInt(0, gameFieldSize * gameFieldSize)
            val row = fieldNumber / gameFieldSize
            val col = fieldNumber % gameFieldSize
            with (gameField[row][col]) {
                if (!isEmpty()) {
                    clear()
                    i++
                }
            }
        }
    }

    private fun isCompletelyEmpty(): Boolean {
        for (row in gameField) {
            for (field in row) {
                if (!field.isEmpty()) return false
            }
        }
        return true
    }

    private fun checkAndIncrementField(row: Int, col: Int): Boolean {
        gameField[row][col].let { field ->
            // fixed numbers can not be changed
            if (!field.fixed) {
                while (field.number <= gameFieldSize) {
                    // if number never fits
                    if (field.number == 0 || !checkAllRules(row, col)) {
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

    private fun checkAllRules(row: Int, col: Int): Boolean =
            checkBlockField(row, col)
            && checkSameBlockPosition(row, col)
            && checkHorizontal(row, col)
            && checkVertical(row, col)
            && checkDiagonal(row, col)

    private fun checkHorizontal(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        for (c in 0 until gameFieldSize) {
            if (c != col && gameField[row][c].number == field.number)
                return false    // number already exists in row
        }
        return true // number is the only one
    }

    private fun checkVertical(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        for (r in 0 until gameFieldSize) {
            if (r != row && gameField[r][col].number == field.number)
                return false    // number already exists in column
        }
        return true // number is the only one
    }

    private fun checkBlockField(row: Int, col: Int): Boolean {
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

    private fun checkDiagonal(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        if (row == col) { // is on diagonal "\"
            for (r in 0 until gameFieldSize) {
                if (r != row && r != col && gameField[r][r].number == field.number) {
                    return false    // number already exists in diagonal
                }
            }
        }
        if (gameFieldSize-1-row == col) { // is on diagonal "/"
            for (r in 0 until gameFieldSize) {
                if (gameFieldSize-1-r != row && r != col && gameField[gameFieldSize-1-r][r].number == field.number) {
                    return false    // number already exists in diagonal
                }
            }
        }
        return true // number is the only one
    }

    private fun checkSameBlockPosition(row: Int, col: Int): Boolean {
        val field = gameField[row][col]
        val rdx = row % blockFieldSize
        val cdx = col % blockFieldSize
        for (blockNo in 0 until gameFieldSize) {
            val r0 = ((blockNo / blockFieldSize) * blockFieldSize) + rdx
            val c0 = ((blockNo % blockFieldSize) * blockFieldSize) + cdx
            if (r0 != row && c0 != col && gameField[r0][c0].number == field.number)
                return false
        }
        return true
    }

    private data class Field(var number: Int, var fixed: Boolean = false) {
        fun clear() {
            number = 0
            fixed = false
        }

        fun isEmpty(): Boolean = number == 0

        fun clearIfPossible() {
            if (!fixed) number = 0
        }

        fun incrementIfPossible(): Boolean {
            if (!fixed) number++
            return !fixed
        }
    }
}
