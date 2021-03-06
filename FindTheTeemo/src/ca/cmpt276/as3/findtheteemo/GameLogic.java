package ca.cmpt276.as3.findtheteemo;

public class GameLogic {
	private GameBoard gameBoard = null;
	private int teemosFound = 0;
	private int numOfTeemos = 0;
	private int numOfFacechecks = 0;
	private boolean isWin = false;
	
	public GameLogic(String size, int numOfTeemos) {
		gameBoard = new GameBoard(size, numOfTeemos);
		teemosFound = 0;
		this.numOfTeemos = numOfTeemos;
		numOfFacechecks = 0;
		isWin = false;
	}
	
	
	public void doCheck(int column, int row) {
		if (!isBushScanned(column, row)) {
			if (isTeemoThere(column, row) && !isBushRevealed(column, row)) {
				gameBoard.setRevealed(column, row);
				gameBoard.updateNearbyTeemosData(column, row);
				teemosFound++;
				if(teemosFound >= numOfTeemos) {
					isWin = true;
				}
			} else {
				gameBoard.setScanned(column, row);
				numOfFacechecks++;
			}
		}
	}
	
	public boolean isTeemoThere(int column, int row) {
		return gameBoard.getIsTeemo(column, row);
	}
	
	public boolean isBushRevealed(int column, int row) {
		return gameBoard.getIsRevealed(column, row);
	}
	
	public boolean isBushScanned(int column, int row) {
		return gameBoard.getIsScanned(column, row);
	}
	

	public int getTeemosFound() {
		return teemosFound;
	}
	
	public int getNumFacechecks() {
		return numOfFacechecks;
	}
	
	public int getTeemosAround(int column, int row) {
		int teemosAround = gameBoard.getTeemosAround(column, row);
		return teemosAround;
	}
	
	public boolean isWin() {
		return isWin;
	}
}