	

import java.util.Arrays;

import javafx.geometry.Rectangle2D;

public class GamePiece {

	public static String[] pieceTypes = { "Left,Right", "Top,Bottom", "Left,Top", "Top,Right", "Right,Bottom",
			"Bottom,Left", "Empty" };

	public static final int pieceHeight = 40;
	public static final int pieceWidth = 40;

	public static final int maxPlayablePieceIndex = 2;
	public static final int emptyPieceIndex = pieceTypes.length - 1;

	private static final int textureOffsetX = 1;
	private static final int textureOffsetY = 1;
	private static final int texturePaddingX = 1;
	private static final int texturePaddingY = 1;

	private String pieceType = "";
	private String pieceSuffix = "";

	public String getPieceType() {
		return pieceType;
	}

	public String getSuffix() {
		return pieceSuffix;
	}

	public GamePiece(String type, String suffix) {
		pieceType = type;
		pieceSuffix = suffix;
	}

	public void setPiece(String type, String suffix) {
		pieceType = type;
		pieceSuffix = suffix;
	}

	public void rotatePiece(boolean isClockwise) {
		switch (pieceType) {
		case "Left,Right":
			pieceType = "Top,Bottom";
			break;
		case "Top,Bottom":
			pieceType = "Left,Right";
			break;
		case "Left,Top":
			if (isClockwise)
				pieceType = "Top,Right";
			else
				pieceType = "Bottom,Left";
			break;
		case "Top,Right":
			if (isClockwise)
				pieceType = "Right,Bottom";
			else
				pieceType = "Left,Top";
			break;
		case "Right,Bottom":
			if (isClockwise)
				pieceType = "Bottom,Left";
			else
				pieceType = "Top,Right";
			break;
		case "Bottom,Left":
			if (isClockwise)
				pieceType = "Left,Top";
			else
				pieceType = "Right,Bottom";
			break;
		case "Empty":
			break;
		}
	}

	public Rectangle2D getSourceRect() {
		int x = textureOffsetX;
		int y = textureOffsetY;

		if (pieceSuffix.contains("W"))
			x += pieceWidth + texturePaddingX;

		y += Arrays.asList(pieceTypes).indexOf(pieceType) * (pieceHeight + texturePaddingY);

		return new Rectangle2D(x, y, pieceWidth, pieceHeight);
	}

	public void setSuffix(String suffix) {
		pieceSuffix = suffix;
	}

	public boolean hasConnector(String direction) {
		return pieceType.contains(direction);
	}

	public String getOtherEnds(String startingEnd) {
		for (String end : pieceType.split(",")) {
			if (!end.equals(startingEnd))
				return end;
		}
		return "";
	}

}
