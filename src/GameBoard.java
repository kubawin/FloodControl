
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class GameBoard {

	static final int GAME_BOARD_WIDTH = 8;
	static final int GAME_BOARD_HEIGHT = 10;
	private static final GamePiece[][] boardSquares = new GamePiece[GAME_BOARD_WIDTH][GAME_BOARD_HEIGHT];
	private final Random random;
	public RotatingPiece rotatingPiece = null;
	public String rotPositionName = "";

	private ArrayList<Point2D> waterTracker = new ArrayList<Point2D>();
	public HashMap<String, FadingPiece> fadingPieces = new HashMap<String, FadingPiece>();

	public GameBoard() {
		random = new Random();
		clearBoard();
	}

	public void clearBoard() {
		for (int x = 0; x < GAME_BOARD_WIDTH; x++)
			for (int y = 0; y < GAME_BOARD_HEIGHT; y++)
				boardSquares[x][y] = new GamePiece("Empty", "");
	}

	public Rectangle2D getSourceRect(int x, int y) {
		return boardSquares[x][y].getSourceRect();
	}

	public void randomPiece(int x, int y) {
		boardSquares[x][y].setPiece(GamePiece.pieceTypes[random.nextInt(GamePiece.maxPlayablePieceIndex + 1)], "");
	}

	public String getPieceType(int x, int y) {
		return boardSquares[x][y].getPieceType();
	}

	public void setPiece(int x, int y, String pieceType) {
		boardSquares[x][y].setPiece(pieceType, "");
	}

	public void generateNewPieces() {
		for (int y = 0; y < GAME_BOARD_HEIGHT; y++) {
			for (int x = 0; x < GAME_BOARD_WIDTH; x++) {
				if (getPieceType(x, y) == "Empty") {
					randomPiece(x, y);
				}
			}
		}
	}

	public void rotatePiece(int x, int y, boolean clockwise) {
		boardSquares[x][y].rotatePiece(clockwise);
	}

	public void addRotatingPiece(int x, int y, String pieceName, boolean clockwise) {
		rotatingPiece = new RotatingPiece(pieceName, clockwise);
		rotPositionName = x + " " + y;
	}

	public boolean hasConnector(int x, int y, String direction) {
		return boardSquares[x][y].hasConnector(direction);
	}

	public void propagateWater(int x, int y, String fromDirection) {
		if ((y >= 0) && (y < GAME_BOARD_HEIGHT) && (x >= 0) && (x < GAME_BOARD_WIDTH)) {

			if (boardSquares[x][y].hasConnector(fromDirection) && !boardSquares[x][y].getSuffix().contains("W")) {
				fillPieceWithWater(x, y);
				waterTracker.add(new Point2D(x, y));

				String end = boardSquares[x][y].getOtherEnds(fromDirection);
				switch (end) {
				case "Left":
					propagateWater(x - 1, y, "Right");
					break;
				case "Right":
					propagateWater(x + 1, y, "Left");
					break;
				case "Top":
					propagateWater(x, y - 1, "Bottom");
					break;
				case "Bottom":
					propagateWater(x, y + 1, "Top");
					break;
				}
			}
		}
	}

	public void fillPieceWithWater(int x, int y) {
		boardSquares[x][y].setSuffix("W");
	}

	public void resetWater() {
		for (int y = 0; y < GAME_BOARD_HEIGHT; y++)
			for (int x = 0; x < GAME_BOARD_WIDTH; x++)
				boardSquares[x][y].setSuffix("");
	}

	public ArrayList<Point2D> getWaterChain(int y) {
		waterTracker.clear();
		propagateWater(0, y, "Left");
		return waterTracker;
	}
	
    public void addFadingPiece(int x, int y, String PieceName) {
        fadingPieces.put(x + " " + y, new FadingPiece(PieceName, "W"));
    }
    

    public void updateFadingPieces() {

        LinkedList<String> removeKeys = new LinkedList<String>();

        for (String theKey : fadingPieces.keySet()) {
            fadingPieces.get(theKey).updatePiece();
            if (fadingPieces.get(theKey).alphaLevel == 0.0f)
                removeKeys.add(theKey);
        }

        while (removeKeys.size() > 0)
            fadingPieces.remove(removeKeys.remove());
    }

}
