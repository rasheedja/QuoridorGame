import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * @author Ben Lawton
 * @author Junaid Rasheed
 *
 * Thread dedicated to interacting with the game server. Responsibilities:
 *
 * - Listening for/parsing input from the server
 * - Sending input to the server
 * - Updating the GUI based on input received from the server
 */

public class GameClient extends Thread {

	// Game server socket
	private Socket serverSocket;

	// Server socket input/output
	private PrintWriter out;
	private BufferedReader in;

	private NetworkedBoardGUI gui;

	// Flagged by the server to say whether or not the Board GUI can (should) be launched
	private boolean guiCanBeLaunched;
	// Flag to let the game server know whether or not the Board GUI has been launched
	private boolean guiIsLaunched;

	// ID belonging to the client's player; player 1's ID would be, well... 1
	private int playerID;
	private boolean IDIsAssigned;

    private Alert alert;

	public GameClient(GUI gui) {
		this.gui = (NetworkedBoardGUI) gui;
		guiCanBeLaunched = false;
		guiIsLaunched = false;
		IDIsAssigned = false;
        alert = new Alert(Alert.AlertType.CONFIRMATION, "");
	}

	public void run() {
		while (true) {
			listenForServerInput();
		}
	}

	public void setPlayerID(int id) {
		if (!IDIsAssigned) {
			this.playerID = id;
			IDIsAssigned = true;
		}
	}

	public int getPlayerID() {
		return playerID;
	}

	public boolean guiCanBeLaunched() {
		return guiCanBeLaunched;
	}

	public GUI getGUI() {
		return gui;
	}

	public void connectToServer(String IPAddress, int portAddress) {
		if (portAddress <= 65535) {
			try {
				serverSocket = new Socket(IPAddress, portAddress);
				out = new PrintWriter(serverSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				initThread();
				SystemLogger.logInfo("Connected to server");
                showAlert("Connected to server");
			} catch (Exception e) {
				SystemLogger.logError(e.getMessage());
                showAlert("Error connecting to server");
			}
		}
	}

	// Listen for and parse input from the server
	public void listenForServerInput() {
		String fromServer;

		try {
			while ((fromServer = in.readLine()) != null) {
				String[] commands = fromServer.split("\\s+");
				if (commands[0].equals("bootGUI")) {
					guiCanBeLaunched = true;
				}
				else if (commands[0].equals("setID")) {
					int id = Integer.parseInt(commands[1]);
					setPlayerID(id);
				}
				else if (commands[0].equals("stats")) {
					updatePlayerStats(commands);
				}
				else if (commands[0].equals("pawn")) {
					updatePlayerPawnPosition(commands);
				}
				else if (commands[0].equals("currentPlayer")) {
					updateCurrentPlayer(commands);
				}
				else if (commands[0].equals("highlight")) {
					highlightAvailablePositions(commands);
				}
				else if (commands[0].equals("error")) {
					displayErrorMessage(commands);
				}
				else if (commands[0].equals("wall")) {
					updateWallPosition(commands);
				}
				else if (commands[0].equals("reset")) {
                    gui.resetWalls();
                }
				else if (commands[0].equals("remove-wall-display")) {
					removeWallDisplay(commands);
				}
				else if (commands[0].equals("coordinate")) {
					int player1X = Integer.parseInt(commands[1]);
					int player1Y = Integer.parseInt(commands[2]);
					int player2X = Integer.parseInt(commands[3]);
					int player2Y = Integer.parseInt(commands[4]);
					int player3X;
					int player3Y;
					int player4X;
					int player4Y;
                    if (gui.getNumberOfPlayers() == 2) {
                        player3X = -1;
                        player3Y = -1;
                        player4X = -1;
                        player4Y = -1;
                    } else {
                        player3X = Integer.parseInt(commands[5]);
                        player3Y = Integer.parseInt(commands[6]);
                        player4X = Integer.parseInt(commands[7]);
                        player4Y = Integer.parseInt(commands[8]);
                    }
					Platform.runLater(new Runnable() {
					   @Override
					   public void run() {
					       gui.setInitialPawnPositions(player1X, player1Y, player2X, player2Y, player3X, player3Y, player4X, player4Y);
					   }
					});
				}
			}
		} catch (Exception e) {
			SystemLogger.logError(e.getMessage());
		}
	}

	public void sendMove(int x, int y) {
		out.println("move " + x + " " + y + " " + playerID);
	}

	public void sendWallMove(int topLeftX, int topLeftY, WallPlacement orientation) {
		out.println("wall " + topLeftX + " " + topLeftY + " " + orientation + " " + playerID);
	}

	public void sendWallRemoval(int topLeftX, int topLeftY, WallPlacement orientation) {

		out.println("remove-wall " + topLeftX + " " + topLeftY + " " + orientation + " " + playerID);
	}

	public void requestCurrentPlayerAvailableMoves() {
		out.println("available");
	}

	public void requestInitialPlayerPawnPositions() {
		out.println("start-coordinates");
	}

	public boolean guiIsLaunched() {
		return guiIsLaunched;
	}

	public void setGUILaunched(boolean booted) {
		this.guiIsLaunched = booted;
	}

	private void removeWallDisplay(String[] commands) {
		int topLeftX = Integer.parseInt(commands[1]);
		int topLeftY = Integer.parseInt(commands[2]);
		WallPlacement orientation = WallPlacement.valueOf(commands[3]);
		gui.removeWallDisplay(topLeftX, topLeftY, orientation);
	}

    private void updateWallPosition(String[] commands) {
        int topLeftX = Integer.parseInt(commands[1]);
        int topLeftY = Integer.parseInt(commands[2]);
        WallPlacement orientation = WallPlacement.valueOf(commands[3]);
        int playerID = Integer.parseInt(commands[4]);
        gui.displayWall(topLeftX, topLeftY, orientation, playerID);
    }

    private void displayErrorMessage(String[] commands) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < commands.length; i++) {
            message.append(commands[i] + " ");
        }
        gui.displayErrorMessage(message.toString());
    }

    private void highlightAvailablePositions(String[] commands) {
        int x = Integer.parseInt(commands[1]);
        int y = Integer.parseInt(commands[2]);
        gui.highlightPositionAvailability(x, y);
    }

    private void updateCurrentPlayer(String[] commands) {
        int playerID = Integer.parseInt(commands[1]);
        gui.updateActivePlayer(playerID);
    }

    private void updatePlayerPawnPosition(String[] commands) {
        int x = Integer.parseInt(commands[1]);
        int y = Integer.parseInt(commands[2]);
        int playerID = Integer.parseInt(commands[3]);
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                gui.updatePlayerPawnPosition(x, y, playerID);
            }
        });
    }

    private void updatePlayerStats(String[] commands) {
        int moveCount = Integer.parseInt(commands[1]);
        int wallCount = Integer.parseInt(commands[2]);
        int playerID = Integer.parseInt(commands[3]);
        gui.updatePlayerMoveCount(moveCount, playerID);
        gui.updatePlayerWallCount(wallCount, playerID);
    }

    private void initThread() {
		Thread thread = new Thread(this);
		thread.start();
	}

    private void showAlert(String alertText) {
        alert.setContentText(alertText);
        alert.showAndWait();
    }
}
