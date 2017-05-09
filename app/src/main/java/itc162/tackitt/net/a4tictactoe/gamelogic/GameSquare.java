package itc162.tackitt.net.a4tictactoe.gamelogic;

import android.view.View;
import android.widget.Button;

import itc162.tackitt.net.a4tictactoe.R;

/**
 * Created by turner on 5/8/17.
 */

public class GameSquare
    implements View.OnClickListener
{
    public static SquareState CurrentPlayersTurn = SquareState.NONE;
    public static SquareState GameWinner = SquareState.NONE;

    public GameSquare()
    {

    }

    public GameSquare(Button associatedUxElement, int squareColumn, int squareRow)
    {
        this.associatedUxElement = associatedUxElement;
        this.squareColumn = squareColumn;
        this.squareRow = squareRow;
        this.squareState = SquareState.NONE;
    }

    public enum SquareState
    {
        NONE(0), X(1), O(2), TIEGAME(3);

        private final int value;
        private SquareState(int value) { this.value = value; }
        public int getValue() { return this.value; }

        public static SquareState FromInt(int value)
        {
            switch (value)
            {
                case 1:
                    return X;
                case 2:
                    return O;
                case 3:
                    return TIEGAME;
                default:
                    return NONE;
            }
        }
    }

    public Button associatedUxElement;
    public SquareState squareState;
    public int squareColumn;
    public int squareRow;

    private  IGameStateUpdateHandler stateUpdateHandler;

    public void BindUxButton(IGameStateUpdateHandler stateUpdateHandler)
    {
        this.stateUpdateHandler = stateUpdateHandler;
        this.associatedUxElement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        // Ignore the event if it is not from the button associated with this instance of GameSquare.
        // Also ignore if the square has already been selected.
        // Also ignore if is not currently a player's turn.
        if(v != this.associatedUxElement || this.squareState != SquareState.NONE || GameSquare.CurrentPlayersTurn == SquareState.NONE)
        {
            return;
        }

        this.squareState = GameSquare.CurrentPlayersTurn;
        this.updateButton();

        // Call the activity to handle gamestate change.
        if(this.stateUpdateHandler != null)
        {
            this.stateUpdateHandler.GameStateUpdated();
        }
    }

    public void updateButton()
    {
        if(this.associatedUxElement == null)
        {
            throw new NullPointerException("No UX element is associated with this GameSquare; cannot update state.");
        }

        switch (this.squareState)
        {
            case X:
                this.associatedUxElement.setText(R.string.ticTacToeGrid_X);
                break;
            case O:
                this.associatedUxElement.setText(R.string.ticTacToeGrid_O);
                break;
            default:
                this.associatedUxElement.setText(R.string.ticTacToeGrid_emptyText);
                break;
        }

        // Ensure the button is disabled if a player has already used this square.
        this.setButtonEnabled(this.squareState == SquareState.NONE && GameSquare.GameWinner == SquareState.NONE);
    }

    public void setButtonEnabled(boolean value)
    {
        this.associatedUxElement.setEnabled(value);
    }
}
