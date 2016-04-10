import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * @author Junaid Rasheed
 * @author Jack Zhang
 * @author Ben Lawton
 * @author Jordan Bird
 *
 */
public class LocalBoardGUI extends Application implements GUI {

	//the scene used to display in the window
    private final Scene scene;
    private final int width = 17;
    private final int height = 17;
    private final HBox player2StatsPane = new HBox(120);
    private final HBox buttonPane = new HBox(10);

    private final HBox currentPlayerPane = new HBox();

    private final HBox errorPane = new HBox();
    private Text errorPaneText;

    //root pane which contain all the information from board to statistic
    private VBox rootPane;
    // player one stats
    private final HBox player1StatsPane = new HBox(120);
    // player two stats
    private GridPane boardPane;
    //amount of move the player has make
    private Text player1Moves;
    private Text currentPlayerText;
    //amount of walls the player has
    private int player1WallCount;
    private Text player1Walls;
    //same as player one move
    private Text player2Moves;
    //same as player one place wall
    private int player2WallCount;
    private Text player2Walls;
    //draw the wall and movement in the board
    private Rectangle[][] grids;
    private Button highlightPositionsButton;
    private Circle firstPawn;
    private Circle secondPawn;
    private boolean drawing;

    private Controller controller;


    /**
     * Constructor for objects of class BoardGUI
     * Models and creates a GUI for the game itself
     */
    public LocalBoardGUI() {
        rootPane = new VBox();
        boardPane = new GridPane();
        boardPane.setGridLinesVisible(true);
        grids = new Rectangle[height][width];
        highlightPositionsButton = new Button("Hint");
        scene = new Scene(rootPane, 800, 800);
        firstPawn = new Circle(15);
        secondPawn = new Circle(15);
        drawing = true;
        player1Moves = new Text("Moves: " + 0);
        currentPlayerText = new Text("Player 1's turn...");
        player1WallCount = 10;
        player1Walls = new Text("Walls: " + player1WallCount);
        player2Moves = new Text("Moves: " + 0);
        player2WallCount = 10;
        player2Walls = new Text("Walls: " + player2WallCount);
        errorPaneText = new Text("");
    }

    public void setController(Controller controller) {
    	this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Quoridor");
        setPanes();
        initialiseBoardSpaces();
        setPlayerStats();
        setPawn(firstPawn, Color.BLUE, (controller.getPlayer1X() * 2), (controller.getPlayer1Y() * 2));
        setPawn(secondPawn, Color.RED, (controller.getPlayer2X() * 2), (controller.getPlayer2Y() * 2));
        scene.getStylesheets().add("Theme.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("BOARD");
        primaryStage.show();
    }



   /**
    * Removes all of the walls on the board
    */
   public void resetWalls() {
	   for (int y = 0; y < 17; y += 2) {
		   for (int x = 1; x < 17; x+= 2) {
			   grids[y][x].setFill(Color.GREY);
			   grids[y][x].setStroke(Color.GREY);
		   }
	   }
	   for (int y = 1; y < 17; y += 2) {
		   for (int x = 0; x < 17; x++) {
			   grids[y][x].setFill(Color.GREY);
			   grids[y][x].setStroke(Color.GREY);
		   }
	   }
   }

/**
 * Sets the player stats so they can be displayed in the UI
 */
    public void setPlayerStats() {
        player1Walls.setTextAlignment(TextAlignment.CENTER);
        player1Walls.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
        player1Moves.setTextAlignment(TextAlignment.CENTER);
        currentPlayerText.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.ITALIC, 15));
        errorPaneText.setFont(Font.font("Calibri", FontWeight.BOLD, 15));
        errorPaneText.setFill(Color.RED);
        player1Moves.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
        Text player1Title = new Text("Player 1");
        player1Title.setFont(Font.font("Calibri", FontWeight.BOLD, 15));
        player1StatsPane.getChildren().addAll(player1Moves, player1Title, player1Walls);
        currentPlayerPane.getChildren().addAll(currentPlayerText);
        errorPane.getChildren().addAll(errorPaneText);
        player2Walls.setTextAlignment(TextAlignment.CENTER);
        player2Walls.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
        player2Moves.setTextAlignment(TextAlignment.CENTER);
        player2Moves.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
        Text player2Title = new Text("Player 2");
        player2Title.setFont(Font.font("Calibri", FontWeight.BOLD, 15));
        player2StatsPane.getChildren().addAll(player2Moves, player2Title, player2Walls);
    }

    /**
     * Draw a player piece at a position on the board
     *
     * @param pawn	the Player to be drawn
     * @param colour	the colour of the piece
     * @param x		the horizontal co-ordinate of the player
     * @param y		the vertical co-ordinate of the player
     */
    public void setPawn(Circle pawn, Color colour, int x, int y) {
        pawn.setFill(colour);
        pawn.setStroke(colour);
        pawn.setTranslateX(5);
        boardPane.setConstraints(pawn, x, y);
        boardPane.getChildren().add(pawn);
    }

    /**
     *  Checks whether the program is currently drawing
     * @return		the value of isDrawing
     */
    public boolean isDrawing() {
        return drawing;
    }

    /**
     * Sets the drawing boolean to the passed boolean
     * @param b		is the program drawing or not
     */
    public void setDrawing(boolean b) {
        drawing = b;
    }

    /**
     * Highlights all of the surrounding valid places for the player to occupy
     * @param x		the x co-ordinate
     * @param y		the y co-ordinate
     */
    public void highlightPositionAvailability(int x, int y) {

        grids[y][x].setFill(Color.YELLOW);
        grids[y][x].setStroke(Color.YELLOW);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        for (int x1 = 0; x1 < width; x1++) {
                            for (int y1 = 0; y1 < width; y1++) {
                                if(x1 % 2 == 0 && y1 % 2 == 0) {
                                    grids[y1][x1].setFill(Color.WHITE);
                                    grids[y1][x1].setStroke(Color.WHITE);
                                }
                            }
                        }
                    }
                },
                1000
        );
    }

    public void updatePlayerMoveCount(int moveCount, int playerID) {
    	if (playerID == 1) {
    		player1Moves.setText("Moves: " + moveCount);
    	}
    	else if (playerID == 2) {
    		player2Moves.setText("Moves: " + moveCount);
    	}
    }

    public void updatePlayerWallCount(int wallCount, int playerID) {
    	if (playerID == 1) {
    		player1WallCount = wallCount;
            player1Walls.setText("Walls: " + player1WallCount);
    	}
    	else if (playerID == 2) {
    		player2WallCount = wallCount;
            player2Walls.setText("Walls: " + player2WallCount);
    	}
    }

    public void updatePlayerPawnPosition(int x, int y, int playerID) {
    	// convert the 9x9 coordinates from the controller to 18x8 coordinates for the GUI
    	int eighteenByEighteenX = x * 2;
    	int eighteenByEighteenY = y * 2;

    	if (playerID == 1) {
    		boardPane.getChildren().remove(firstPawn);
            boardPane.setConstraints(firstPawn, eighteenByEighteenX, eighteenByEighteenY);
            boardPane.getChildren().add(firstPawn);
    	}
    	else if (playerID == 2) {
    		boardPane.getChildren().remove(secondPawn);
            boardPane.setConstraints(secondPawn, eighteenByEighteenX, eighteenByEighteenY);
            boardPane.getChildren().add(secondPawn);
    	}
    }

    /**
     * change the active player to the next player
     */
    public void updateActivePlayer() {
        if (controller.getCurrentPlayer().getID() == 2) {
            currentPlayerText.setText("Player 2's turn...");
        }
        else if (controller.getCurrentPlayer().getID() == 1) {
            currentPlayerText.setText("Player 1's turn...");
        }
    }

    /**
     * Method the controller calls to place a wall in the GUI
     */
    public void displayWall(int x, int y, PositionWallLocation relativeLocation, int playerID) {
    	x *= 2;
    	y *= 2;

    	switch(relativeLocation) {
	    	case LEFT: {
	            x -= 1;

	            break;
	        }
	        case RIGHT: {
	            x += 1;
	            break;
	        }
	        case TOP: {
	            y -= 1;
	            break;
	        }
	        case BOTTOM: {
	            y += 1;
	            break;
	        }
    	}
    	if (playerID == 1) {
    		grids[y][x].setFill(Color.BLUE);
    		grids[y][x].setStroke(Color.BLUE);
    	} else if (playerID == 2) {
    		grids[y][x].setFill(Color.RED);
    		grids[y][x].setStroke(Color.RED);
    	}
    }

    /**
     * Set the alignment of all instantiated fields to the centre of the pane
     */
    private void setPanes() {
        rootPane.setAlignment(Pos.CENTER);
        player1StatsPane.setAlignment(Pos.CENTER);
        currentPlayerPane.setAlignment(Pos.CENTER_RIGHT);
        errorPane.setAlignment(Pos.CENTER);
        player2StatsPane.setAlignment(Pos.CENTER);
        boardPane.setAlignment(Pos.CENTER);
        buttonPane.setAlignment(Pos.CENTER);
        //boardPane.setHgap(5);
        //boardPane.setVgap(5);
        rootPane.getChildren().addAll(currentPlayerPane, player1StatsPane, boardPane, player2StatsPane, buttonPane, errorPane);
        player1StatsPane.setPadding(new Insets(5, 0, 5, 0));
        player2StatsPane.setPadding(new Insets(5, 0, 5, 0));
        currentPlayerPane.setPadding(new Insets(0, 180, 0, 0));
        errorPane.setPadding(new Insets(5, 0, 0, 0));
    }

    /**
     * Create the board spaces and add them to the 2D array
     */
    private void initialiseBoardSpaces() {
    	for(int x = 0 ; x < width; x++){
    		for(int y = 0; y < width; y++){
    			grids[y][x] = new Rectangle();
    			// middle points between walls
    			if(x % 2 != 0 && y % 2 != 0) {
                    initialiseUnusedSquare(x, y);
    			}
    			// occupiable position
    			if(x % 2 == 0 && y % 2 == 0) {
                    initialiseOccupiableGrid(x, y);
    			}
    			// wide, short walls
    			if(x % 2 == 0 && y % 2 != 0) {
                    initialiseWideWall(x, y);
    			}
    			// tall, thin walls
    			if(x % 2 != 0 && y % 2 == 0) {
                    initialiseThinWall(x, y);
    			}
    		}
    	}
 	   highlightPositionsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               controller.showCurrentPlayerMoves();
           }
       });
       buttonPane.getChildren().add(highlightPositionsButton);
    }

    /**
     * Allows a user to place a wall on a wall space that is available
     */
   private void placeWall(int x, int y) {
       // tall, thin wall
	   if (x % 2 != 0) {
           placeThinWall(x, y);
       }
       //short, wide wall
       if (y % 2 != 0) {
    	   placeWideWall(x, y);
       }
   }

    /**
     * Set the occupiable positions
     * @param x
     * @param y
     * @param X
     * @param Y
     */
    private void initialiseOccupiableGrid(int x, int y) {
        grids[y][x].setHeight(40);
        grids[y][x].setWidth(40);
        grids[y][x].setStroke(Color.WHITE);
        grids[y][x].setFill(Color.WHITE);
        boardPane.setConstraints(grids[y][x],x,y);
        boardPane.getChildren().add(grids[y][x]);
        grids[y][x].setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // convert the 18x18 GUI coordinates to the 9x9 coordinates for the controller (the controller has a 9x9 model of the board)
                int nineByNineX = x / 2;
                int nineByNineY = y / 2;
                try {
                	controller.movePawn(nineByNineX, nineByNineY);
                }
                catch (IllegalArgumentException e) {
                	errorPaneText.setText(e.getMessage());
                	new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    errorPaneText.setText("");
                                }
                            },
                            1000
                    );
                }
            }
        });
    }

    private void initialiseWideWall(int x, int y) {
        grids[y][x].setHeight(10);
        grids[y][x].setWidth(40);
        grids[y][x].setStroke(Color.GREY);
        grids[y][x].setFill(Color.GREY);
        boardPane.setConstraints(grids[y][x],x,y);
        boardPane.getChildren().add(grids[y][x]);
        grids[y][x].setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                placeWall(x, y);
            }
        });
    }

    private void initialiseThinWall(int x, int y) {
        grids[y][x].setWidth(10);
        grids[y][x].setHeight(40);
        grids[y][x].setStroke(Color.GREY);
        grids[y][x].setFill(Color.GREY);
        boardPane.setConstraints(grids[y][x],x,y);
        boardPane.getChildren().add(grids[y][x]);
        grids[y][x].setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                placeWall(x, y);
            }
        });
    }

    private void initialiseUnusedSquare(int x, int y) {
        grids[y][x].setHeight(10);
        grids[y][x].setWidth(10);
        grids[y][x].setStroke(Color.rgb(192, 192, 192));
        grids[y][x].setFill(Color.rgb(192, 192, 192));
        boardPane.setConstraints(grids[y][x],x,y);
        boardPane.getChildren().add(grids[y][x]);
    }

    private void placeThinWall(int x, int y) {
        // coordinates of the position to the top left of the horizontal wall
        int topLeftPosX = x / 2;
        int topLeftPosY = y / 2;
        // coordinates of the position to the bottom left of the horizontal wall
        int bottomLeftPosX = topLeftPosX;
        int bottomLeftPosY = topLeftPosY + 1;
        // coordinates of the position to the top right of the horizontal wall
        int topRightPosX = topLeftPosX + 1;
        int topRightPosY = topLeftPosY;
        // coordinates of the position to the bottom right of the horizontal wall
        int bottomRightPosX = topLeftPosX + 1;
        int bottomRightPosY = bottomLeftPosY;
        PositionWallLocation left = PositionWallLocation.LEFT;
        PositionWallLocation right = PositionWallLocation.RIGHT;
        try {
        	controller.placeWall(topLeftPosX, topLeftPosY, right, topRightPosX, topRightPosY, left, bottomLeftPosX, bottomLeftPosY, right, bottomRightPosX, bottomRightPosY, left);
        	grids[y][x].setOnMouseClicked(new EventHandler<MouseEvent>() {
        		@Override
        		public void handle(MouseEvent event) {
        			controller.removeWall(topLeftPosX, topLeftPosY, right, topRightPosX, topRightPosY, left, bottomLeftPosX, bottomLeftPosY, right, bottomRightPosX, bottomRightPosY, left);
        		}
        	});
        }
        catch (IllegalStateException e) {
        	errorPaneText.setText(e.getMessage());
        	new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            errorPaneText.setText("");
                        }
                    },
                    1000
            );
        }
    }

    private void placeWideWall(int x, int y) {
        // coordinates of the position to the top left of the vertical wall
        int topLeftPosX = x / 2;
        int topLeftPosY = y / 2;
        // coordinates of the position to the bottom left of the vertical wall
        int bottomLeftPosX = topLeftPosX;
        int bottomLeftPosY = topLeftPosY + 1;
        // coordinates of the position to the top right of the vertical wall
        int topRightPosX = topLeftPosX + 1;
        int topRightPosY = topLeftPosY;
        // coordinates of the position to the bottom right of the vertical wall
        int bottomRightPosX = topLeftPosX + 1;
        int bottomRightPosY = bottomLeftPosY;
        PositionWallLocation top = PositionWallLocation.TOP;
        PositionWallLocation bottom = PositionWallLocation.BOTTOM;
        try {
        	controller.placeWall(topLeftPosX, topLeftPosY, bottom, bottomLeftPosX, bottomLeftPosY, top, topRightPosX, topRightPosY, bottom, bottomRightPosX, bottomRightPosY, top);
        }
        catch (IllegalStateException e) {
        	errorPaneText.setText(e.getMessage());
        	new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            errorPaneText.setText("");
                        }
                    },
                    1000
            );
        }
    }
}
