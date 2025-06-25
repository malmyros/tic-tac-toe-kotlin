import kotlin.system.exitProcess

class Game {
    private val board = MutableList<Cell>(size = 9) { Cell.Empty }
    private var status: Status = Status.Idle
    private lateinit var player: Player

    fun start() {
        status = Status.Running
        println("--------------------------------")
        println("| Welcome to TIC-TAC-TOE Game! |")
        println("|     Pick a number from 0-8   |")
        println("--------------------------------")
        getName()

        while (status == Status.Running) {
            getCell()
        }
    }

    private fun getName() {
        print("Choose Your Name: ")
        val name = readlnOrNull()
        try {
            require(value = name != null) { "Name must not be null or blank" }
            player = Player(name = name, symbol = 'X')
            println("It's your move ${player.name}")
            printBoard()
        } catch (e: Throwable) {
            println("Invalid Name. ${e.message}")
        }
    }

    private fun getCell() {
        val input = readlnOrNull()
        try {

            require(value = input != null) { "Cell must not be null or blank" }
            val cellNumber = input.toInt()
            require(value = cellNumber in 0..8) { "Position must be between 0 and 8" }
            setCell(cellNumber = cellNumber)

        } catch (e: Throwable) {
            println("Invalid Cell Number. ${e.message}")
        }
    }

    private fun setCell(cellNumber: Int) {
        val cell = board[cellNumber]
        if (cell is Cell.Empty) {

            board.set(index = cellNumber, element = Cell.Filled(player = player))
            checkBoard()
            generateComputerMove()
            printBoard()

        } else {
            println("Cell Taken, Choose Another.")
        }
    }

    private fun generateComputerMove() {
        try {

            val availableCells = mutableListOf<Int>()
            board.forEachIndexed { index, cell ->
                if (cell is Cell.Empty) {
                    availableCells.add(index)
                }
            }

            if (!availableCells.isEmpty()) {
                val randomCell = availableCells.random()
                board.set(index = randomCell, element = Cell.Filled(player = Player()))
            }

        } catch (e: Throwable) {
            println("Error: ${e.message}")
        }
    }

    private fun checkBoard() {
        val winningCombinations = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6),
        )

        val playerCells = mutableListOf<Int>()
        val computerCells = mutableListOf<Int>()
        board.forEachIndexed { index, cell ->
            if (cell.placeholder == 'X') {
                playerCells.add(element = index)
            }
            if (cell.placeholder == 'O') {
                computerCells.add(element = index)
            }
        }
        println("Your Moves: ${playerCells}")
        println("Computer Moves: ${computerCells}")

        run CombinationLoop@{
            winningCombinations.forEach { combination ->
                if (playerCells.containsAll(elements = combination)) {
                    won()
                    return@CombinationLoop
                }
                if (computerCells.containsAll(elements = combination)) {
                    lost()
                    return@CombinationLoop
                }
            }
        }

        if (board.none { it is Cell.Empty } && status is Status.Running) {
            draw()
        }

        if (status is Status.GameOver) {
            finish()
        }
    }

    private fun printBoard() {
        println()
        println(" -------- ")
        println("| ${board[0].placeholder} ${board[1].placeholder} ${board[2].placeholder} |")
        println("| ${board[3].placeholder} ${board[4].placeholder} ${board[5].placeholder} |")
        println("| ${board[6].placeholder} ${board[7].placeholder} ${board[8].placeholder} |")
        println(" -------- ")
        println()
    }

    private fun finish() {
        status = Status.Idle
        board.replaceAll { Cell.Empty }
        playAgain()
    }

    private fun playAgain() {
        println("Do you wish to play another one? (Y/N)")
        val input = readlnOrNull()
        try {
            require(value = input != null)
            val capitalizedInput = input.replaceFirstChar(Char::titlecase)
            val positive = capitalizedInput.contains(other = "Y")
            val negative = capitalizedInput.contains(other = "N")
            require(value = positive || negative)
            if (positive) {
                start()
            } else {
                exitProcess(status = 0)
            }
        } catch (_: IllegalArgumentException) {
            println("Wrong option. Type either 'Y or 'N")
            playAgain()
        }
    }

    private fun won() {
        status = Status.GameOver
        printBoard()
        println("Congratulations! ${player.name}, You won!")
    }

    private fun lost() {
        status = Status.GameOver
        printBoard()
        println("Sorry! ${player.name}, You lost!")
    }

    private fun draw() {
        status = Status.GameOver
        printBoard()
        println("Draw!")
    }
}

data class Player(
    val name: String = "Computer",
    val symbol: Char = 'O'
)

sealed class Status {
    object Idle : Status()
    object Running : Status()
    object GameOver : Status()
}

sealed class Cell(val placeholder: Char) {
    object Empty : Cell(placeholder = '_')
    data class Filled(val player: Player) : Cell(placeholder = player.symbol)
}