import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class Board(val rows: Int, val cols: Int) {
    // Row / Col
    private val squares = Array(rows) { IntArray(cols) }

    operator fun get(row: Int, col: Int): Int {
        return squares[row][col]
    }

    operator fun set(row: Int, col: Int, value: Int) {
        squares[row][col] = value
    }

    fun size(): Int {
        return squares.size
    }

    init {
        for (c in 0 until cols) {
            for (r in 0 until rows) {
                squares[r][c] = 0
            }
        }
    }

    override fun toString(): String {
//        for (row in 0 until rows) {
        for (row in rows - 1 downTo 0) {
            for (col in 0 until cols) {
//            for (col in cols-1 downTo 0) {
                print(squares[col][row])
                print(" ")
            }
            println("")
        }
        return ""
    }
}

class Solution(cols: Int, rows: Int) {
    val solved: Boolean = false
    var totalSolutions = 0L
    val board: Board
    val sucessfulBoards = mutableListOf<Board>()
    val mutex: Mutex = Mutex()

    // Left Diagonal
    val ld = Array<Int>(cols * 2) { _ -> 0 }

    // Right Diagonal
    val rd = Array<Int>(cols * 2) { _ -> 0 }

    // Column Array
    val cl = Array<Int>(cols * 2) { _ -> 0 }

    init {
        this.board = Board(rows, cols)
    }

    suspend fun snapshot() {
        totalSolutions++
    }
}

suspend fun solveNQUtil(solution: Solution, col: Int): Boolean {
    val board = solution.board
    val N = board.size()

    if (col >= N) {
        solution.snapshot()
        return true
    }

    for (row in 0 until N) {

        if (solution.ld[row - col + N - 1] != 1 &&
            solution.rd[row + col] != 1 &&
            solution.cl[row] != 1
        ) {
            // Place this queen in board[i][col]
            board[row, col] = 1
            solution.cl[row] = 1
            solution.rd[row + col] = solution.cl[row]
            solution.ld[row - col + N - 1] = solution.rd[row + col]

            // Recurse to the next column.
            solveNQUtil(solution, col + 1)

            // Rollback, try the next one
            board[row, col] = 0
            solution.cl[row] = 0
            solution.rd[row + col] = solution.cl[row]
            solution.ld[row - col + N - 1] = solution.rd[row + col]
        }
    }

    return false
}

// Searches for a specific starting row only in the first column. This lets us parallelize multiple paths based on each
// with a different start row in the first column.
suspend fun solveNQUtilHelper(solution: Solution, row: Int, col: Int): Boolean {
    val board = solution.board
    val N = board.size()

    if (col >= N) {
        solution.snapshot()
        return true
    }

    if (solution.ld[row - col + N - 1] != 1 &&
        solution.rd[row + col] != 1 &&
        solution.cl[row] != 1
    ) {
        // Place this queen in board[i][col]
        board[row, col] = 1
        solution.cl[row] = 1
        solution.rd[row + col] = solution.cl[row]
        solution.ld[row - col + N - 1] = solution.rd[row + col]

        // Recurse to the next column.
        solveNQUtil(solution, col + 1)

        // Rollback, try the next one
        board[row, col] = 0
        solution.cl[row] = 0
        solution.rd[row + col] = solution.cl[row]
        solution.ld[row - col + N - 1] = solution.rd[row + col]
    }

    return false
}

suspend fun main(args: Array<String>) {
    println("Start")
    val start = System.currentTimeMillis()
    val N = args[0].toInt()

    val scopes = mutableListOf<Job>()
    val solutions = mutableListOf<Solution>()
    for (i in 0 until N) {
        val scope = CoroutineScope(Default).launch {
            println("Starting job on ${Thread.currentThread().name}")
            val solution = Solution(N, N)
            solveNQUtilHelper(solution, i, 0)
            solutions.add(solution)
            println("Finished job on ${Thread.currentThread().name}")
        }
        scopes.add(scope)
    }

    for (scope in scopes) {
        scope.join()
    }

    val end = System.currentTimeMillis()
    val solution = Solution(N, N)
    for (s in solutions) {
        solution.totalSolutions += s.totalSolutions
    }

    println("Num solutions for ${N}x${N} grid: ${solution.totalSolutions}")
    println("Solved in ${(end - start) / 1000}s")
}