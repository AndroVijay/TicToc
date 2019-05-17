package info.tictac

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private var lastPressedTime: Long = 0
    private val PERIOD : Int = 2000

    //Creating a 2D Array of ImageViews
    private val boardCells = Array(3) { arrayOfNulls<ImageView>(3) }

    //creating the board instance
    var board = Board()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //calling the function to load our tic tac toe board
        loadBoard()


        //restart functionality
        button_restart.setOnClickListener {
            //creating a new board instance
            //it will empty every cell
            board = Board()

            //setting the result to empty
            text_view_result.text = ""

            //this function will map the internal board
            //to the visual board
            mapBoardToUi()
        }
    }

    //function is mapping
    //the internal board to the ImageView array board
    private fun mapBoardToUi() {
        for (i in board.board.indices) {
            for (j in board.board.indices) {
                when (board.board[i][j]) {
                    Board.PLAYER -> {
                        boardCells[i][j]?.setImageResource(R.mipmap.circle)
                        boardCells[i][j]?.isEnabled = false
                    }
                    Board.COMPUTER -> {
                        boardCells[i][j]?.setImageResource(R.mipmap.cross)
                        boardCells[i][j]?.isEnabled = false
                    }
                    else -> {
                        boardCells[i][j]?.setImageResource(0)
                        boardCells[i][j]?.isEnabled = true
                    }
                }
            }
        }
    }


    private fun loadBoard() {

        for (i in boardCells.indices) {
            for (j in boardCells.indices) {
                boardCells[i][j] = ImageView(this)
                boardCells[i][j]?.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = 200
                    height = 180
                    bottomMargin = 5
                    topMargin = 5
                    leftMargin = 5
                    rightMargin = 5
                }
                boardCells[i][j]?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

                //attached a click listener to the board
                boardCells[i][j]?.setOnClickListener(CellClickListener(i, j))

                layout_board.addView(boardCells[i][j])

            }
        }

    }

    inner class CellClickListener(
        val i: Int,
        val j: Int
    ) : View.OnClickListener {

        override fun onClick(p0: View?) {
            //here we will code the move

            //checking if the game is not over
            if (!board.isGameOver) {

                //creating a new cell with the clicked index
                val cell = Cell(i, j)

                //placing the move for player
                board.placeMove(cell, Board.PLAYER)

                //calling minimax to calculate the computers move
                board.minimax(0, Board.COMPUTER)

                //performing the move for computer
                board.computersMove?.let {
                    board.placeMove(it, Board.COMPUTER)
                }

                //mapping the internal board to visual board
                mapBoardToUi()
            }

            //Displaying the results
            //according to the game status
            when {
                board.hasComputerWon() -> text_view_result.text = "Computer Won"
                board.hasPlayerWon() -> text_view_result.text = "Player Won"
                board.isGameOver -> text_view_result.text = "Game Tied"
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {


        if (event != null) {
            if (event.getKeyCode() === KeyEvent.KEYCODE_BACK) {
                when (event.getAction()) {
                    KeyEvent.ACTION_DOWN -> {
                        if (event.getDownTime() - lastPressedTime < PERIOD) {
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext, "Press again to exit.",
                                Toast.LENGTH_SHORT
                            ).show()
                            lastPressedTime = event.getEventTime()
                        }
                        return true
                    }
                }
            }
        }
        return false
    }
}
