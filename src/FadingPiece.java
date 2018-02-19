
public class FadingPiece extends GamePiece {
	public float alphaLevel = 1.0f;
	public static float alphaChangeRate = 0.01f;

	public FadingPiece(String pieceType, String suffix) {
		super(pieceType, suffix);
	}

	public void updatePiece() {
		alphaLevel = Math.max(0, alphaLevel - alphaChangeRate);
	}
}
