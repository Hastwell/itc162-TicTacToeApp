package itc162.tackitt.net.a4tictactoe;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import itc162.tackitt.net.a4tictactoe.gamelogic.GameSquare;
import itc162.tackitt.net.a4tictactoe.gamelogic.IGameStateUpdateHandler;

import static android.view.View.Y;
import static itc162.tackitt.net.a4tictactoe.gamelogic.GameSquare.SquareState.O;
import static itc162.tackitt.net.a4tictactoe.gamelogic.GameSquare.SquareState.X;

public class TicTacToe extends AppCompatActivity
implements IGameStateUpdateHandler {

    GameSquare[] gameSquares;
    TextView playerTurnLabel;
    Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        this.playerTurnLabel = (TextView) this.findViewById(R.id.labelGameStatus);
        this.initializeGridButtonHandlers();

        this.newGameButton = (Button) this.findViewById(R.id.buttonNewGame);
        this.newGameButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TicTacToe.this.startNewGame();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences.Editor prefs = this.getSharedPreferences("SavedValues", MODE_PRIVATE).edit();

        this.saveGame(prefs);

        prefs.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.loadGame();
    }

    private void initializeGridButtonHandlers()
    {
        Button[] gridButtonViews = new Button[]
        {
                (Button) this.findViewById(R.id.buttonTicTacGrid1x1), (Button) this.findViewById(R.id.buttonTicTacGrid1x2), (Button) this.findViewById(R.id.buttonTicTacGrid1x3),
                (Button) this.findViewById(R.id.buttonTicTacGrid2x1), (Button) this.findViewById(R.id.buttonTicTacGrid2x2), (Button) this.findViewById(R.id.buttonTicTacGrid2x3),
                (Button) this.findViewById(R.id.buttonTicTacGrid3x1), (Button) this.findViewById(R.id.buttonTicTacGrid3x2), (Button) this.findViewById(R.id.buttonTicTacGrid3x3)
        };
        this.gameSquares = new GameSquare[gridButtonViews.length];

        for(int gridIndex = 0; gridIndex < gridButtonViews.length; gridIndex++)
        {
            Button currentButton = gridButtonViews[gridIndex];
            int column = gridIndex % 3;
            int row = gridIndex / 3; // integer division intentional

            GameSquare newSquare = new GameSquare(currentButton, column, row);
            newSquare.BindUxButton(this);
            this.gameSquares[gridIndex] = newSquare;
        }
    }


    @Override
    public void GameStateUpdated()
    {
        switch (GameSquare.CurrentPlayersTurn)
        {
            case X:
                GameSquare.CurrentPlayersTurn = GameSquare.SquareState.O;

                break;
            case O:
                GameSquare.CurrentPlayersTurn = GameSquare.SquareState.X;
                break;

            default:
                Toast.makeText(this, "It isn't anybody's turn!", Toast.LENGTH_SHORT).show();
                return;
        }

        this.checkWinCondition();
        this.updateGameViews();
    }

    private void checkWinCondition()
    {
        GameSquare.SquareState[] statesToCheck = new GameSquare.SquareState[] { X, O };
        for(GameSquare.SquareState player : statesToCheck)
        {
            for(int counter = 0; counter < 3; counter++)
            {
                // first, check rows and columns
                if
                (
                        (this.gameSquares[(counter*3) + 0].squareState == player && this.gameSquares[(counter*3) + 1].squareState == player && this.gameSquares[(counter*3) + 2].squareState == player)
                        ||
                        (this.gameSquares[0 + counter].squareState == player && this.gameSquares[3 + counter].squareState == player && this.gameSquares[6 + counter].squareState == player)
                )
                // if's then
                {
                    GameSquare.GameWinner = player;
                    return;
                }
            }

            if
            (
                    (this.gameSquares[(0*3)+0].squareState == player && this.gameSquares[(1*3)+1].squareState == player && this.gameSquares[(2*3)+2].squareState == player)
                    ||
                    (this.gameSquares[(0*3)+2].squareState == player && this.gameSquares[(1*3)+1].squareState == player && this.gameSquares[(2*3)].squareState == player)
            )
            {
                GameSquare.GameWinner = player;
                return;
            }
        }

        // If possible wins have been tested and failed, check if all squares are filled.
        // If all are filled, but there is not already a winner, the game is tied.
        boolean allSquaresFilled = true;

        for (GameSquare square : this.gameSquares)
        {
            allSquaresFilled = allSquaresFilled & square.squareState != GameSquare.SquareState.NONE;
            if(!allSquaresFilled) break;
        }

        if(allSquaresFilled)
        {
            // all squares are filled, but there was no winner already (they would have been found earlier)
            // this is a tied game
            GameSquare.GameWinner = GameSquare.SquareState.TIEGAME;
        }
    }

    public void updateGameViews()
    {
        if(GameSquare.GameWinner != GameSquare.SquareState.NONE)
        {
            switch (GameSquare.GameWinner)
            {
                case X:
                    this.playerTurnLabel.setText(R.string.ticTacToeStatus_winX);
                    break;
                case O:
                    this.playerTurnLabel.setText(R.string.ticTacToeStatus_winO);
                    break;
                default:
                    this.playerTurnLabel.setText(R.string.ticTacToeStatus_winNone);
                    break;
            }
        }
        else
        {
            switch (GameSquare.CurrentPlayersTurn)
            {
                case X:
                    this.playerTurnLabel.setText(R.string.ticTacToeStatus_turnX);
                    break;
                case O:
                    this.playerTurnLabel.setText(R.string.ticTacToeStatus_turnO);
                    break;
                default:
                    this.playerTurnLabel.setText(R.string.ticTacToeStatus_winNone);
                    break;
            }
        }

        for (GameSquare square : this.gameSquares)
        {
            square.updateButton();
        }
    }

    public void startNewGame()
    {
        GameSquare.CurrentPlayersTurn = GameSquare.SquareState.X;
        GameSquare.GameWinner = GameSquare.SquareState.NONE;

        for(GameSquare square : this.gameSquares)
        {
            square.squareState = GameSquare.SquareState.NONE;
        }

        this.updateGameViews();
        this.saveGame();
    }

    private void saveGame()
    {
        SharedPreferences.Editor editor = this.getSharedPreferences("SavedValues", MODE_PRIVATE).edit();
        this.saveGame(editor);
        editor.apply();
    }
    private void saveGame(SharedPreferences.Editor prefs)
    {
        prefs.putInt("CURRENT_TURN", GameSquare.CurrentPlayersTurn.getValue());
        prefs.putInt("WINNER", GameSquare.GameWinner.getValue());

        for(GameSquare square : this.gameSquares)
        {
            prefs.putInt("SQUARE_" + Integer.toString(square.squareColumn) + "x" + Integer.toString(square.squareRow), square.squareState.getValue());
        }
    }

    private void loadGame() { this.loadGame(this.getSharedPreferences("SavedValues", MODE_PRIVATE)); }
    private void loadGame(SharedPreferences prefs)
    {
        GameSquare.CurrentPlayersTurn = GameSquare.SquareState.FromInt(prefs.getInt("CURRENT_TURN", GameSquare.SquareState.X.getValue()));
        GameSquare.GameWinner = GameSquare.SquareState.FromInt(prefs.getInt("WINNER", GameSquare.SquareState.NONE.getValue()));

        for(GameSquare square : this.gameSquares)
        {
            square.squareState = GameSquare.SquareState.FromInt(prefs.getInt("SQUARE_" + Integer.toString(square.squareColumn) + "x" + Integer.toString(square.squareRow), GameSquare.SquareState.NONE.getValue()));
            square.updateButton();
        }
        this.updateGameViews();
    }
}
