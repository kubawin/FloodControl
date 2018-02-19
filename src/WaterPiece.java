

import javafx.geometry.Rectangle2D;

public class WaterPiece {

	public static final int pianaStartx = 83;
	public static final int pianaStarty = 223;
	public static final int pianaStopx = 377;
	public static final int pianaStopy = 237;
	
	public static final int wodaStartx = 83;
	public static final int wodaStarty = 243;
	public static final int wodaStopx = 377;
	public static final int wodaStopy = 487;
	
	public static int getWodaX =  wodaStopx-wodaStartx ;
	
	public static int getWodaY =  wodaStopy-wodaStarty ;
	
	public static int getPianaX = pianaStopx - pianaStartx;
	public static int getPianaY = pianaStopy - pianaStarty;
	
			

	public Rectangle2D getSourceRectPiana() {
		int x = pianaStartx;
		int y = pianaStarty;
		
		return new Rectangle2D(x, y, pianaStopx-x, pianaStopy-y);
	}
	
	public Rectangle2D getSourceRectWoda() {
		int x = wodaStartx;
		int y = wodaStarty;
		
		return new Rectangle2D(x, y, wodaStopx-x, wodaStopy-y);
	}



}