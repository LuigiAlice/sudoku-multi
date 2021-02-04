class Sudoku {

    private val blockFieldSize: Int = 3
    private val gameFieldSize:Int = blockFieldSize * blockFieldSize     // quadratic field
    private val gameField = Array(gameFieldSize) { Array(gameFieldSize) { Field(0)  } }

    fun solveField(): Int {
        var row:Int = 0
        var col:Int = 0

        var counter = 0

        // as long as not all fields are filled
        while (row < gameField.size && row > -1
            && col < gameField.size && col > -1) {
            counter++

            if (checkField(row, col)) {
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

    private fun checkField(row: Int, col: Int): Boolean {
        gameField[row][col].let { field ->
            // fixed numbers can not be changed
            if (!field.fixed) {
                while (field.number <= gameFieldSize) {
                    // if number never fits
                    if (field.number == 0
                        || !checkHorizontal(row, col)
                        || !checkVertical(row, col)
                        || !checkBlockField(row, col) ) {
                        // then try another one
                        field.incrementIfPossible()
                    }
                    else {
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
        val blockRowStart:Int = (row / blockFieldSize) * blockFieldSize
        val blockColStart:Int = (col / blockFieldSize) * blockFieldSize
        for (r in 0 until blockFieldSize) {
            for (c in 0 until blockFieldSize) {
                val rr = blockRowStart + r
                val cc = blockColStart + c
                if (rr != row && cc != col
                    && gameField[rr][cc].number == field.number)
                    return false    // number already exists in block
            }
        }
        return true // number is the only one
    }


    fun initField() {
        // see /resources/img.png
//        gameField[0][1] = Field(1, true)
//        gameField[0][5] = Field(8, true)
//        gameField[0][6] = Field(2, true)
//        gameField[0][8] = Field(3, true)
//
//        gameField[1][1] = Field(3, true)
//
//        gameField[2][5] = Field(2, true)
//        gameField[2][7] = Field(5, true)
        gameField[2][8] = Field(8, true)

        gameField[3][3] = Field(6, true)
        gameField[3][7] = Field(7, true)

        gameField[4][0] = Field(9, true)
        gameField[4][3] = Field(7, true)
        gameField[4][8] = Field(4, true)

        gameField[5][0] = Field(8, true)
        gameField[5][2] = Field(4, true)

        gameField[6][2] = Field(5, true)
        gameField[6][3] = Field(3, true)
        gameField[6][4] = Field(1, true)
        gameField[6][6] = Field(6, true)
        gameField[6][8] = Field(2, true)

        gameField[7][0] = Field(4, true)
        gameField[7][2] = Field(3, true)
        gameField[7][4] = Field(7, true)

        gameField[8][4] = Field(2, true)
        gameField[8][6] = Field(5, true)
    }

    fun printField() {
        gameField.forEach { col ->
            col.forEach { field ->
                print("  " + if (field.number != 0) field.number else " ")
            }
            println()
        }
        println()
    }

    data class Field(var number: Int, val fixed: Boolean = false) {
        fun clearIfPossible() {
            if (!fixed) number = 0
        }
        fun incrementIfPossible(): Boolean {
            if (!fixed) number++
            return !fixed
        }
    }
}