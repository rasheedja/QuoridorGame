import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author Ben Lawton
 * @author Khadija Patel
 * @author Jordan Bird
 *
 * @version 12/02/2016
 */
public class GameController<T> {

	// The game board and its positions' logic
    private static Board board;
    // GUI (View) representing the game board
    private static BoardGUI gui;

    //The two players in the game
    private static Player player1;
    private static Player player2;

    //The player whose turn it is
    private static Player currentPlayer;

    public GameController(BoardGUI gui, Board board) {
        GameController.board = board;
        GameController.gui = gui;

        player1 = new Player(4, 0);
        player2 = new Player(4, 8);
        currentPlayer = player1;
    }

    /**
     * Get the available positions a player can move into and then highlight them in the GUI
     */
    public static void showCurrentPlayerMoves() {
    	Position position = board.getPosition(currentPlayer.getX(), currentPlayer.getY());
    	ArrayList<Position> availablePositions = board.getOccupiablePositions(position);
    	if (availablePositions.size() > 0) {
    		for (Position pos : availablePositions) {
				int x = pos.getX() * 2;
				int y = pos.getY() * 2;
    			gui.highlightPositionAvailability(x, y);
    		}
    	}
    }

    /**
     * @return The current player
     */
    public static Player getCurrentPlayer() {
 	   return currentPlayer;
    }

    /**
     * @return The player who made a move most recently
     */
    public static Player getPreviousPlayer() {
 	   if (currentPlayer == player1) {
 		   return player2;
 	   }
 	   else {
 		   return player1;
 	   }
    }

    /**
     * Place a wall as the current player's move
     *
     * @param pos1X..pos4X x coordinates for the grid the wall will obstruct
     * @param pos1Y..pos4Y y coordinates for the grid the wall will obstruct
     * @param pos1Border..pos4Border location of the wall in relation to the grid it will obstruct
     */
    public static void placeWall(int pos1X, int pos1Y, PositionWallLocation pos1Border, int pos2X, int pos2Y, PositionWallLocation pos2Border, int pos3X, int pos3Y, PositionWallLocation pos3Border, int pos4X, int pos4Y, PositionWallLocation pos4Border) {
    	if (currentPlayer.hasWalls()) {
	    	Position coveredPosition1 = board.getPosition(pos1X, pos1Y);
	    	Position coveredPosition2 = board.getPosition(pos2X, pos2Y);
	    	Position coveredPosition3 = board.getPosition(pos3X, pos3Y);
	    	Position coveredPosition4 = board.getPosition(pos4X, pos4Y);

	    	assignWall(coveredPosition1, pos1Border);
	    	assignWall(coveredPosition2, pos2Border);
	    	assignWall(coveredPosition3, pos3Border);
	    	assignWall(coveredPosition4, pos4Border);

	    	board.addWalledOffPosition(coveredPosition1);
	    	board.addWalledOffPosition(coveredPosition2);
	    	board.addWalledOffPosition(coveredPosition3);
	    	board.addWalledOffPosition(coveredPosition4);

	    	currentPlayer.decrementWallCount();
	    	currentPlayer.incrementMoveCount();
	    	if (currentPlayer == player1) {
	    		gui.updatePlayer1MoveCount(currentPlayer.getMoveCount());
	    		gui.updatePlayer1WallCount(currentPlayer.getWallCount());
	    	}
	    	else {
	    		gui.updatePlayer2MoveCount(currentPlayer.getMoveCount());
	    		gui.updatePlayer2WallCount(currentPlayer.getWallCount());
	    	}
	    	changePlayer();
    	}
    	else {
    		throw new IllegalStateException("You have no remaining walls");
    	}
    }

    /**
     * Move a player to a specified position
     * @param posX the x co-ordinate of the move
     * @param posY the y co-ordinate of the move
     */
    public static void movePawn(int posX, int posY) {
    	if (currentPlayer == player1) {
    		if (posX == player2.getX() && posY == player2.getY()) {
    			throw new IllegalArgumentException("Position is occupied");
    		}
    		else {
    			if (isValidMove(currentPlayer, posX, posY)) {
		    		player1.setX(posX);
		    		player1.setY(posY);
		    		currentPlayer.incrementMoveCount();
		    		gui.updatePlayer1MoveCount(currentPlayer.getMoveCount());
		    		gui.updatePlayer1PawnPosition(posX, posY);
		    		if (board.getPosition(posX, posY).isBottom()) {
		    			resetGame();
		    		}
		    		changePlayer();
    			}
    			else {
    				throw new IllegalArgumentException("That isn't a valid move");
    			}
    		}
    	}
    	else {
    		if (posX == player1.getX() && posY == player1.getY()) {
	    		throw new IllegalArgumentException("Position is occupied");
    		}
    		else {
    			if (isValidMove(currentPlayer, posX, posY)) {
	    			player2.setX(posX);
		    		player2.setY(posY);
		    		currentPlayer.incrementMoveCount();
		    		gui.updatePlayer2MoveCount(currentPlayer.getMoveCount());
		    		gui.updatePlayer2PawnPosition(posX, posY);
		    		if (board.getPosition(posX, posY).isTop()) {
		    			resetGame();
		    		}
		    		changePlayer();
	    		}
    		}
    	}
    }

    /**
     * Check if a player is allowed to move to the given position
     * @param player the player looking to move
     * @param newX the new co-ordinates of the position the player wants to occupy
     * @param newY the new co-ordinates of the position the player wants to occupy
     * @return whether the player can or cannot move to the specified position
     */
    private static boolean isValidMove(Player player, int newX, int newY) {
    	boolean isValid = false;
    	// if the move is directly along the x axis
    	if (((newX == (player.getX() + 1)) || (newX == (player.getX() - 1))) && newY == player.getY()) {
    		// if the move is to the left and the player won't be blocked by a wall to the left
    		if ((newX == (player.getX() - 1) && (!board.getPosition(player.getX(), player.getY()).hasLeftWall()))) {
    			isValid = true;
    		}
    		// if the move is to the right and the player won't be blocked by a wall to the right
    		else if ((newX == (player.getX() + 1) && (!board.getPosition(player.getX(), player.getY()).hasRightWall()))) {
    			isValid = true;
    		}
    	}
    	// if the move is directly along the y axis
    	else if (((newY == (player.getY() + 1)) || (newY == (player.getY() - 1))) && newX == player.getX()) {
    		// if the move is up and the player won't be blocked by a wall to the top
    		if ((newY == (player.getY() - 1) && (!board.getPosition(player.getX(), player.getY()).hasTopWall()))) {
    			isValid = true;
    		}
    		// if the move is down and the player won't be blocked by a wall to the bottom
    		else if ((newY == (player.getY() + 1) && (!board.getPosition(player.getX(), player.getY()).hasBottomWall()))) {
    			isValid = true;
    		}
    	}
    	return isValid;
    }

/**
 * Reset the game's back-end state and GUI
 */
    private static void resetGame() {
    	gui.updatePlayer1MoveCount(0);
    	player1.setMoveCount(0);
    	gui.updatePlayer2MoveCount(0);
    	player2.setMoveCount(0);
    	gui.updatePlayer1WallCount(10);
    	player1.setWallCount(10);
    	gui.updatePlayer2WallCount(10);
    	player2.setWallCount(10);
    	gui.updatePlayer1PawnPosition(4, 0);
    	player1.setX(4);
    	player1.setY(0);
    	gui.updatePlayer2PawnPosition(4, 8);
    	player2.setX(4);
    	player2.setY(8);
    	currentPlayer = player1;
    	board.resetWalledOffPositions();
    	gui.resetWalls();
    }

    /**
     * Assign a wall a given position
     *
     * @param position the position of the wall
     * @param location location of the wall in relation to the position it's blocking
     */
    private static void assignWall(Position position, PositionWallLocation location) {
    	switch (location) {
	    	case LEFT: {
	    		position.setHasLeftWall(true);
	    		break;
	    	}
	    	case RIGHT: {
	    		position.setHasRightWall(true);
	    		break;
	    	}
	    	case TOP: {
	    		position.setHasTopWall(true);
	    		break;
	    	}
	    	case BOTTOM: {
	    		position.setHasBottomWall(true);
	    		break;
	    	}
    	}
    }

    /**
     * Make the next player the active player
     */
    private static void changePlayer() {
    	if (currentPlayer == player1) {
    		currentPlayer = player2;
    		gui.changeActivePlayer();
    	}
    	else {
    		currentPlayer = player1;
    		gui.changeActivePlayer();
    	}
    }
}
