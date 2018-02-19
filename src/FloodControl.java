
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class FloodControl {

	private Stage stage;
	private Canvas canvas;
	private GraphicsContext graphicsContext;
	private long startNanoTime;

	private Image playingPieces;
	private Image backgroundScreen;
	private Image titleScreen;
	private UserInputQueue userInputQueue;
	private GameAnimationTimer animationTimer;
	private GameBoard gameBoard;
	private Point2D boardOrigin;

	private int level;
	private int playerScore = 0;
	private int tempScore = 0;
	private double timeStart;
	private long timeMax = 3000;
	private int varTemp;

	private enum State {
		TitleScreen, Playing
	};

	private State state;

	private class GameAnimationTimer extends AnimationTimer {
		@Override
		public void handle(long currentNanoTime) {
			update(currentNanoTime);
			draw(currentNanoTime);
		}
	}

	public FloodControl(Stage primaryStage) {
		stage = primaryStage;
		stage.setTitle("Flood Control");
		stage.getIcons().add(new Image("icons/Game.png"));
		startNanoTime = System.nanoTime(); // czas rozpoczï¿½cia gry
	}

	public void run() {
		initialize();
		loadContent();
		stage.show();
		animationTimer = new GameAnimationTimer();
		animationTimer.start();
	}

	private void initialize() {
		level = 6;
		Group root = new Group();
		canvas = new Canvas(800, 600);
		root.getChildren().add(canvas);

		graphicsContext = canvas.getGraphicsContext2D();

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.sizeToScene();
		stage.setOnCloseRequest(e -> stage_CloseRequest(e));
		state = State.TitleScreen;

		userInputQueue = new UserInputQueue();
		scene.setOnKeyPressed(keyEvent -> userInputQueue.addKey(keyEvent));
		scene.setOnMouseClicked(mouseEvent -> userInputQueue.addMouse(mouseEvent));
		gameBoard = new GameBoard();
		boardOrigin = new Point2D(70, 89);
	}

	private void stage_CloseRequest(WindowEvent windowEvent) {
		windowEvent.consume();

		Platform.runLater(() -> {
			if (AlertBox.showAndWait(AlertType.CONFIRMATION, "Flood Control", "Do you want to stop the game?")
					.orElse(ButtonType.CANCEL) == ButtonType.OK) {
				animationTimer.stop();
				unloadContent();
				stage.close();
			}
		});
	}

	private void loadContent() {
		playingPieces = new Image("textures/Tile_Sheet.png");
		backgroundScreen = new Image("textures/Background.png");
		titleScreen = new Image("textures/TitleScreen.png");
	}

	private void unloadContent() {
	}

	private void update(long currentNanoTime) {
		KeyCode keyCode = userInputQueue.getKeyCode();

		switch (state) {
		case TitleScreen:
			if (keyCode == KeyCode.SPACE) {
				state = State.Playing;
				gameBoard.generateNewPieces();
			} else if (keyCode == KeyCode.ESCAPE) {
				stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			}
			break;
		case Playing:
			gameBoard.resetWater();
			gameBoard.generateNewPieces();
			for (int y = 0; y < GameBoard.GAME_BOARD_HEIGHT; y++) {
				checkScoringChain(gameBoard.getWaterChain(y));
			}
			gameBoard.updateFadingPieces();
			handleMouseInput();
			break;
		}
	}

	private void draw(long currentNanoTime) {
		graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		switch (state) {
		case TitleScreen:
			graphicsContext.drawImage(titleScreen, 0, 0);
			break;
		case Playing:
			graphicsContext.drawImage(backgroundScreen, 0, 0);

			PixelReader pixReader = playingPieces.getPixelReader();
			WritableImage emptyPiece = new WritableImage(pixReader, 1, 247, GamePiece.pieceWidth,
					GamePiece.pieceHeight);

			for (int x = 0; x < GameBoard.GAME_BOARD_WIDTH; x++) {
				for (int y = 0; y < GameBoard.GAME_BOARD_HEIGHT; y++) {
					int pixelX = (int) boardOrigin.getX() + (x * GamePiece.pieceWidth);
					int pixelY = (int) boardOrigin.getY() + (y * GamePiece.pieceHeight);

					graphicsContext.drawImage(emptyPiece, pixelX, pixelY);

					// FC 07
					String positionName = x + " " + y;
					if (gameBoard.fadingPieces.containsKey(positionName)) {
						drawFadingPiece(pixelX, pixelY, positionName);
					} else {
						Rectangle2D rect = gameBoard.getSourceRect(x, y);
						WritableImage thePiece = new WritableImage(pixReader, (int) rect.getMinX(),
								(int) rect.getMinY(), GamePiece.pieceWidth, GamePiece.pieceHeight);
						graphicsContext.drawImage(thePiece, pixelX, pixelY);
					}
				}
			}
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.setLineWidth(3);
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.setLineWidth(4);
			Font theFont = Font.font("Arial", FontWeight.BOLD, 72);
			graphicsContext.setFont(theFont);
			graphicsContext.fillText(level + "", 515, 270);

			PixelReader water = backgroundScreen.getPixelReader();
			WritableImage tank = new WritableImage(water, (WaterPiece.wodaStartx),
					(WaterPiece.wodaStopy - 55 - varTemp), WaterPiece.getWodaX, 44 + varTemp);  // wycina coraz wiêkszy rysunek wody
			graphicsContext.drawImage(tank, 480, 545 - varTemp);								// wrysowuje coraz wiêkszy rysunek coraz wy¿ej 

			WritableImage foam = new WritableImage(water, WaterPiece.pianaStartx, WaterPiece.pianaStarty,	
					WaterPiece.getPianaX, WaterPiece.getPianaY);								// wycina pianke, taka sama caly czas
			graphicsContext.drawImage(foam, 480, varTemp < 200 ? 545 - WaterPiece.getPianaY - varTemp : 335);			// rysuje pianke dokladnie nad woda

			if (playerScore == tempScore) {
			} else {
				tempScore = playerScore;
				level++;
				timeStart = currentNanoTime;
//				System.out.println(timeTemp);
			}
			graphicsContext.fillText(playerScore + "", 603, 270);

			if (timeStart == 0) {
				timeStart = currentNanoTime; // ustaw czas na 0
			}

			varTemp = (int) ((((currentNanoTime - timeStart) / 1000000000.0)) * Math.pow(level, 2));
			System.out.println(varTemp);

			if (varTemp >= 200 && varTemp <=250) {
				theFont = Font.font("Arial", FontWeight.BOLD, 100);
				graphicsContext.setFill(Color.RED);
				graphicsContext.setFont(theFont);
				graphicsContext.fillText("Game Over", 100, 300); //tylko na jedna klatke :/
				
				if(varTemp == 250)
				state = State.TitleScreen;
			}

			break;
		}
	}

	private void handleMouseInput() {
		MouseEvent mouseState = userInputQueue.getMouse();
		if (mouseState == null)
			return;

		int x = (int) (mouseState.getSceneX() - (int) boardOrigin.getX()) / GamePiece.pieceWidth;
		int y = (int) (mouseState.getSceneY() - (int) boardOrigin.getY()) / GamePiece.pieceHeight;

		if ((x >= 0) && (x < GameBoard.GAME_BOARD_WIDTH) && (y >= 0) && (y < GameBoard.GAME_BOARD_HEIGHT)) {
			if (mouseState.getButton() == MouseButton.PRIMARY) {
				gameBoard.addRotatingPiece(x, y, gameBoard.getPieceType(x, y), false);
				gameBoard.rotatePiece(x, y, false);
			} else if (mouseState.getButton() == MouseButton.SECONDARY) {
				gameBoard.addRotatingPiece(x, y, gameBoard.getPieceType(x, y), true);
				gameBoard.rotatePiece(x, y, true);
			}
		}
	}

	private void drawFadingPiece(int pixelX, int pixelY, String positionName) {

		Rectangle2D rect = gameBoard.fadingPieces.get(positionName).getSourceRect();
		PixelReader pixReader = playingPieces.getPixelReader();
		WritableImage thePiece = new WritableImage(pixReader, (int) rect.getMinX(), (int) rect.getMinY(),
				GamePiece.pieceWidth, GamePiece.pieceHeight);

		double currAlpha = graphicsContext.getGlobalAlpha();
		graphicsContext.setGlobalAlpha(gameBoard.fadingPieces.get(positionName).alphaLevel);
		graphicsContext.drawImage(thePiece, pixelX, pixelY);
		graphicsContext.setGlobalAlpha(currAlpha);
	}

	private void checkScoringChain(ArrayList<Point2D> waterChain) {
		if (waterChain.size() > 0) {

			Point2D lastPipe = waterChain.get(waterChain.size() - 1);

			if (lastPipe.getX() == GameBoard.GAME_BOARD_WIDTH - 1) {
				if (gameBoard.hasConnector((int) lastPipe.getX(), (int) lastPipe.getY(), "Right")) {

					playerScore += determineScore(waterChain.size());

					for (Point2D scoringSquare : waterChain) {
						gameBoard.addFadingPiece((int) scoringSquare.getX(), (int) scoringSquare.getY(),
								gameBoard.getPieceType((int) scoringSquare.getX(), (int) scoringSquare.getY()));
						gameBoard.setPiece((int) scoringSquare.getX(), (int) scoringSquare.getY(), "Empty");
					}
				}
			}
		}
	}

	private int determineScore(int squareCount) {
		return (int) ((Math.pow((squareCount / 5), 2) + squareCount) * 10);
	}
}
