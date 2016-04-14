import java.util.ArrayList;

public class NetworkedGameController implements Controller {

	private ClientSocketIOThread player1IO;
	private ClientSocketIOThread player2IO;

	private Board board;

	public NetworkedGameController(Board board) {
		this.board = board;
	}

	public void setPlayer1IO(ClientSocketIOThread ioThread) {
		if (player1IO == null) {
			this.player1IO = ioThread;
		}
	}

	public void setPlayer2IO(ClientSocketIOThread ioThread) {
		if (player2IO == null) {
			this.player2IO = ioThread;
		}
	}

	@Override
	public Player getCurrentPlayer() {
		return board.getCurrentPlayer();
	}

	@Override
	public void showCurrentPlayerMoves() {
		ArrayList<Position> availablePositions = board.getCurrentPlayerOccupiablePositions();
    	if (availablePositions.size() > 0) {
    		for (Position pos : availablePositions) {
				int x = pos.getX() * 2;
				int y = pos.getY() * 2;
    			player1IO.sendAvailableMove(x, y);
    			player2IO.sendAvailableMove(x, y);
    		}
    	}
	}

	/**public void placeWall(int pos1x, int pos1y, PositionWallLocation pos1Border, int pos2x, int pos2y,
			PositionWallLocation pos2Border, int pos3x, int pos3y, PositionWallLocation pos3Border, int pos4x,
			int pos4y, PositionWallLocation pos4Border, int playerID) {

		if (playerID == board.getCurrentPlayer().getID()) {
			Position coveredPos1 = board.getPosition(pos1x, pos1y);
			Position coveredPos2 = board.getPosition(pos2x, pos2y);
			Position coveredPos3 = board.getPosition(pos3x, pos3y);
			Position coveredPos4 = board.getPosition(pos4x, pos4y);
			try {
		        board.placeWalls(coveredPos1, pos1Border, coveredPos2, pos2Border, coveredPos3, pos3Border, coveredPos4, pos4Border);
                sendWallUpdate(coveredPos1, coveredPos2, coveredPos3, coveredPos4, pos1Border, pos2Border, pos3Border, pos4Border);
                sendWallRemovalListenerSignal(coveredPos1, coveredPos2, coveredPos3, coveredPos4, pos1Border, pos2Border, pos3Border, pos4Border);
			} catch (IllegalStateException e) {
				if (playerID == 1) {
					player1IO.sendErrorMessage(e.getMessage());
				}
				else if (playerID == 2) {
					player2IO.sendErrorMessage(e.getMessage());
				}
			}
		}
		else {
			if (playerID == 1) {
				player1IO.sendErrorMessage("It isn't your turn.");
			}
			else if (playerID == 2) {
				player2IO.sendErrorMessage("It isn't your turn.");
			}
		}
	}**/

	public void placeWall(int topLeftX, int topLeftY, WallPlacement orientation, int playerID) {
		try {
			board.placeWalls(topLeftX, topLeftY, orientation);
			sendWallUpdate(topLeftX, topLeftY, orientation);
		} catch (IllegalStateException e) {
			if (playerID == 1) {
				player1IO.sendErrorMessage(e.getMessage());
			} else if (playerID == 2) {
				player2IO.sendErrorMessage(e.getMessage());
			}
		}
	}

	@Override
	public int getPlayer1X() {
		return board.getPlayer1().getPosition().getX();
	}

	@Override
	public int getPlayer1Y() {
		return board.getPlayer1().getPosition().getY();
	}

	@Override
	public int getPlayer2X() {
		return board.getPlayer2().getPosition().getX();
	}

	@Override
	public int getPlayer2Y() {
		return board.getPlayer2().getPosition().getY();
	}

	@Override
	public void movePawn(int posX, int posY) {/*Method not implemented; player ID required. See below.*/}

	public void movePawn(int posX, int posY, int playerID) {
		if (playerID == board.getCurrentPlayer().getID()) {
			try {
				boolean gameOver = board.movePawn(posX, posY);
                Player prevPlayer = board.getPreviousPlayer();
                sendPawnUpdate(prevPlayer);
                if (gameOver) {
                    sendGUIResetCommands();
                }
			} catch (IllegalArgumentException e) {
				if (playerID == 1) {
					player1IO.sendErrorMessage(e.getMessage());
				}
				else if (playerID == 2) {
					player2IO.sendErrorMessage(e.getMessage());
				}
			}
		}
		else {
			if (playerID == 1) {
				player1IO.sendErrorMessage("It isn't your turn.");
			}
			else if (playerID == 2) {
				player2IO.sendErrorMessage("It isn't your turn.");
			}
		}
	}

	public void removeWall (int topLeftX, int topLeftY, PositionWallLocation topLeftBorder, int pos2X, int pos2Y, PositionWallLocation pos2Border,
			int pos3X, int pos3Y, PositionWallLocation pos3Border, int pos4X, int pos4Y, PositionWallLocation pos4Border, int playerID) {

		/**if (this.board instanceof ChallengeBoard) {
			Position coveredPos1 = board.getPosition(topLeftX, topLeftY);
    		Position coveredPos2 = board.getPosition(pos2X, pos2Y);
        	Position coveredPos3 = board.getPosition(pos3X, pos3Y);
        	Position coveredPos4 = board.getPosition(pos4X, pos4Y);
        	boolean wallsRemoved = ((ChallengeBoard) board).removeWalls(coveredPos1, topLeftBorder, coveredPos2, pos2Border, coveredPos3, pos3Border, coveredPos4, pos4Border);
        	if (wallsRemoved) {
        		sendWallRemovalUpdate(topLeftX, topLeftY, topLeftBorder, pos2X, pos2Y, pos2Border, pos3X, pos3Y, pos3Border, pos4X, pos4Y, pos4Border);
        	} else {
        		player1IO.sendErrorMessage("Could not remove the walls");
        		player2IO.sendErrorMessage("Could not remove the walls");
        	}
		}**/
	}

	private void sendWallRemovalUpdate(int topLeftX, int topLeftY, PositionWallLocation topLeftBorder, int pos2X, int pos2Y, PositionWallLocation pos2Border, int pos3X, int pos3Y, PositionWallLocation pos3Border,
			int pos4X, int pos4Y, PositionWallLocation pos4Border) {

		player1IO.sendRemoveWallDisplay(topLeftX, topLeftY, topLeftBorder);
		player1IO.sendRemoveWallDisplay(pos2X, pos2Y, pos2Border);
		player1IO.sendRemoveWallDisplay(pos3X, pos3Y, pos3Border);
		player1IO.sendRemoveWallDisplay(pos4X, pos4Y, pos4Border);

		player2IO.sendRemoveWallDisplay(topLeftX, topLeftY, topLeftBorder);
		player2IO.sendRemoveWallDisplay(pos2X, pos2Y, pos2Border);
		player2IO.sendRemoveWallDisplay(pos3X, pos3Y, pos3Border);
		player2IO.sendRemoveWallDisplay(pos4X, pos4Y, pos4Border);

		Player player1 = board.getPlayer1();
		Player player2 = board.getPlayer2();

		// send stats updates for both players as the other player's wall count will be incremented by their wall being removed
		player1IO.sendStatsUpdate(player1.getMoveCount(), player1.getWallCount(), 1);
		player1IO.sendStatsUpdate(player2.getMoveCount(), player2.getWallCount(), 2);
		player2IO.sendStatsUpdate(player1.getMoveCount(), player1.getWallCount(), 1);
		player2IO.sendStatsUpdate(player2.getMoveCount(), player2.getWallCount(), 2);

		player1IO.sendCurrentPlayerGUIUpdate(board.getCurrentPlayer().getID());
        player2IO.sendCurrentPlayerGUIUpdate(board.getCurrentPlayer().getID());
	}

	private void sendWallUpdate(int topLeftX, int topLeftY, WallPlacement orientation) {
        player1IO.sendWallUpdate(topLeftX, topLeftY, orientation, board.getPreviousPlayer().getID());
        player2IO.sendWallUpdate(topLeftX, topLeftY, orientation, board.getPreviousPlayer().getID());

        player1IO.sendStatsUpdate(board.getPreviousPlayer().getMoveCount(), board.getPreviousPlayer().getWallCount(), board.getPreviousPlayer().getID());
        player2IO.sendStatsUpdate(board.getPreviousPlayer().getMoveCount(), board.getPreviousPlayer().getWallCount(), board.getPreviousPlayer().getID());

        player1IO.sendCurrentPlayerGUIUpdate(board.getCurrentPlayer().getID());
        player2IO.sendCurrentPlayerGUIUpdate(board.getCurrentPlayer().getID());
	}

	private void sendWallRemovalListenerSignal(Position coveredPos1, Position coveredPos2, Position coveredPos3, Position coveredPos4,
			PositionWallLocation pos1Border, PositionWallLocation pos2Border, PositionWallLocation pos3Border, PositionWallLocation pos4Border) {

		player1IO.sendWallRemovalListenerSignal(coveredPos1.getX(), coveredPos1.getY(), pos1Border, coveredPos2.getX(), coveredPos2.getY(), pos2Border, coveredPos3.getX(), coveredPos3.getY(), pos3Border, coveredPos4.getX(), coveredPos4.getY(), pos4Border);
		player2IO.sendWallRemovalListenerSignal(coveredPos1.getX(), coveredPos1.getY(), pos1Border, coveredPos2.getX(), coveredPos2.getY(), pos2Border, coveredPos3.getX(), coveredPos3.getY(), pos3Border, coveredPos4.getX(), coveredPos4.getY(), pos4Border);
	}

    private void sendPawnUpdate(Player prevPlayer) {
        player1IO.sendPawnUpdate(prevPlayer.getPosition().getX(), prevPlayer.getPosition().getY(), prevPlayer.getID());
        player1IO.sendStatsUpdate(prevPlayer.getMoveCount(), prevPlayer.getWallCount(), prevPlayer.getID());
        player2IO.sendPawnUpdate(prevPlayer.getPosition().getX(), prevPlayer.getPosition().getY(), prevPlayer.getID());
        player2IO.sendStatsUpdate(prevPlayer.getMoveCount(), prevPlayer.getWallCount(), prevPlayer.getID());
        player1IO.sendCurrentPlayerGUIUpdate(board.getCurrentPlayer().getID());
        player2IO.sendCurrentPlayerGUIUpdate(board.getCurrentPlayer().getID());
    }

    private void sendGUIResetCommands() {
        Player player1 = board.getPlayer1();
        Player player2 = board.getPlayer2();
        // send updates to player 1's GUI
        player1IO.sendStatsUpdate(player1.getMoveCount(), player1.getWallCount(), player1.getID());
        player1IO.sendStatsUpdate(player2.getMoveCount(), player2.getMoveCount(), player2.getID());
        player1IO.sendPawnUpdate(player1.getPosition().getX(), player1.getPosition().getY(), player1.getID());
        player1IO.sendPawnUpdate(player2.getPosition().getX(), player2.getPosition().getY(), player2.getID());
        // send updates for player 2's GUI
        player2IO.sendStatsUpdate(player2.getMoveCount(), player2.getWallCount(), player2.getID());
        player2IO.sendStatsUpdate(player1.getMoveCount(), player1.getMoveCount(), player1.getID());
        player2IO.sendPawnUpdate(player2.getPosition().getX(), player2.getPosition().getY(), player2.getID());
        player2IO.sendPawnUpdate(player1.getPosition().getX(), player1.getPosition().getY(), player1.getID());
        // send commands to reset both GUI's walls
        player1IO.sendResetWalls();
        player2IO.sendResetWalls();
    }

	@Override
	public void resetGame() {
		// TODO stub
	}

	@Override
	public void placeWall(int topLeftX, int topLeftY, PositionWallLocation topLeftBorder, int pos2x, int pos2y,
			PositionWallLocation pos2Border, int pos3x, int pos3y, PositionWallLocation pos3Border, int pos4x,
			int pos4y, PositionWallLocation pos4Border) {
		// TODO Auto-generated method stub

	}

	@Override
	public void placeWall(int topLeftX, int topLeftY, WallPlacement orientation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeWall(int topLeftX, int topLeftY, WallPlacement orientation) {
		// TODO Auto-generated method stub

	}
}
