package ca.cmpt276.as3.findtheteemo;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameActivity extends Activity {

	private final static int SMALL_NUM_TEEMOS = 6;
	private final static int MEDIUM_NUM_TEEMOS = 10;
	private final static int LARGE_NUM_TEEMOS = 15;
	private final static int XLARGE_NUM_TEEMOS = 20;
	
	private final static int SMALL_BOARD = 0;
	private final static int MEDIUM_BOARD = 1;
	
	private final static int SMALL_NUM_ROWS = 3;
	private final static int SMALL_NUM_COLS = 4;
	private final static int MEDIUM_NUM_ROWS = 4;
	private final static int MEDIUM_NUM_COLS = 6;
	private final static int LARGE_NUM_ROWS = 6;
	private final static int LARGE_NUM_COLS = 10;
	
	private Button[][] buttons = null; 
	private GameLogic game = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		if (getBoardSize() == SMALL_BOARD) {
			populateGameBoard(SMALL_NUM_COLS, SMALL_NUM_ROWS);
			game = new GameLogic("small", getNumberOfTeemos());
		} else if (getBoardSize() == MEDIUM_BOARD) {
			populateGameBoard(MEDIUM_NUM_COLS, MEDIUM_NUM_ROWS);
			game = new GameLogic("medium", getNumberOfTeemos());
		} else {
			populateGameBoard(LARGE_NUM_COLS, LARGE_NUM_ROWS);
			game = new GameLogic("large", getNumberOfTeemos());
		}
		
		incrementNumberGamesStarted();
		setupNumGamesStartedDisplay();
	}

	private int getBoardSize() {
		SharedPreferences size = getSharedPreferences("BoardSize", MODE_PRIVATE);
		return size.getInt("BoardSizeIndex", 0);
	}
	
	private void populateGameBoard(final int numCols, final int numRows) {
		TableLayout gameBoard = (TableLayout) findViewById(R.id.gameBoard);
		buttons = new Button[numRows][numCols];
		
		for (int row = 0; row < numRows; row++) {
			TableRow tableRow = new TableRow(this);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.MATCH_PARENT,
					1.0f));
			gameBoard.addView(tableRow);
			
			for (int col = 0; col < numCols; col++) {
				final int FINAL_ROW = row;
				final int FINAL_COL = col;
				
				Button button = new Button(this);
				button.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT,
						1.0f));
				
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// Lock Button Sizes:
						lockButtonSizes(numCols, numRows);
						
						if (game.isTeemoThere(FINAL_COL, FINAL_ROW)) {
							if (game.isBushRevealed(FINAL_COL, FINAL_ROW)) {
								setButtonRevealed(FINAL_COL, FINAL_ROW);								
							} else {
								setButtonHit(FINAL_COL, FINAL_ROW);
							}
							game.doCheck(FINAL_COL, FINAL_ROW);
							updateTeemoCountDisplay();
							updateNumberScansUsed();
							if (game.isWin()) {
								setupWinDialog();
							}
						} else {
							setButtonMiss(FINAL_COL, FINAL_ROW);
							game.doCheck(FINAL_COL, FINAL_ROW);
							updateNumberScansUsed();
						}
						
						setButtonText(numCols, numRows);							
					}

				});
				
				tableRow.addView(button);
				buttons[row][col] = button;
			}
		}
	}
	private void lockButtonSizes(int numCols, int numRows) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Button button = buttons[row][col];
				
				int width = button.getWidth();
				button.setMinWidth(width);
				button.setMaxWidth(width);
				
				int height = button.getHeight();
				button.setMinHeight(height);
				button.setMaxHeight(height);
			}
		}	
	}
	private void setupWinDialog() {
		final Dialog dialog = new Dialog(this){
			@Override
			public boolean dispatchTouchEvent(MotionEvent event)  
			{
				finish();
				return false;
			}
			
			@Override
			public void onBackPressed() {
				finish();
			}
		};
		dialog.setContentView(R.layout.activity_win_dialog);
		dialog.setTitle("Congratulations!");
		
		TextView text = (TextView) dialog.findViewById(R.id.txtWin);
		String congratsMsg = (String)getResources().getString(R.string.congrats_text);
		text.setText(congratsMsg);
		dialog.show();		
	}
	private void updateNumberScansUsed() {
		TextView view = (TextView) findViewById(R.id.txtNumberOfFacechecks);
		String message = (String) getResources().getText(R.string.number_of_scans_used);
		int numFacechecks = game.getNumFacechecks();
		message += numFacechecks;
		view.setText(message);
	}
	
	
	public void updateTeemoCountDisplay() {
		TextView view = (TextView) findViewById(R.id.txtAboutAuthor);
		String message = (String) getResources().getText(R.string.teemos_found);
		int numFound = game.getTeemosFound();
		message += numFound + " of " + getNumberOfTeemos();
		view.setText(message);
	}
	private int getNumberOfTeemos() {
		SharedPreferences numTeemos = getSharedPreferences("NumberOfTeemos", MODE_PRIVATE);
		int value = numTeemos.getInt("NumberOfTeemosIndex", 0);
		
		if (value == 0) {
			return SMALL_NUM_TEEMOS;
		} else if (value == 1) {
			return MEDIUM_NUM_TEEMOS;
		} else if (value == 2) {
			return LARGE_NUM_TEEMOS;
		} else {
			return XLARGE_NUM_TEEMOS;
		}
	}

	private void incrementNumberGamesStarted() {
		SharedPreferences runStats = getSharedPreferences("GameStats", MODE_PRIVATE);
        int timesRun = runStats.getInt("TimesLoaded", 0);
        
        timesRun++;

        SharedPreferences.Editor prefEditor = runStats.edit();
        prefEditor.putInt("TimesLoaded", timesRun);
        prefEditor.commit();	
	}

	private void setupNumGamesStartedDisplay() {
		TextView view = (TextView) findViewById(R.id.txtTimesPlayed);
		String message = (String) getResources().getText(R.string.times_played);
		message += getNumberTimesStarted();
		view.setText(message);
	}
	private int getNumberTimesStarted() {
	        SharedPreferences runStats = getSharedPreferences("GameStats", MODE_PRIVATE);
	        return runStats.getInt("TimesLoaded", 0);         
	}
	
	private void setButtonDefault(int col, int row) { //DOES NOT WORK ATMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
		Button button = buttons[row][col];
		
		// Scale image to button: Only works in JellyBean!
		int newWidth = button.getWidth();
		int newHeight = button.getHeight();
		Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.teemo);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
		Resources resource = getResources();
		button.setBackground(new BitmapDrawable(resource, scaledBitmap));
	}
	
	private void setButtonHit(int col, int row) {
		Button button = buttons[row][col];
		
		// Scale image to button: Only works in JellyBean!
		int newWidth = button.getWidth();
		int newHeight = button.getHeight();
		Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dead_teemo);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
		Resources resource = getResources();
		button.setBackground(new BitmapDrawable(resource, scaledBitmap));
	}
	
	private void setButtonRevealed(int col, int row) {
		Button button = buttons[row][col];
		
		// Scale image to button: Only works in JellyBean!
		int newWidth = button.getWidth();
		int newHeight = button.getHeight();
		Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dead_teemo2);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
		Resources resource = getResources();
		button.setBackground(new BitmapDrawable(resource, scaledBitmap));
	}
	
	private void setButtonMiss(int col, int row) {
		Button button = buttons[row][col];
		
		// Scale image to button: Only works in JellyBean!
		int newWidth = button.getWidth();
		int newHeight = button.getHeight();
		Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shroom_button);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
		Resources resource = getResources();
		button.setBackground(new BitmapDrawable(resource, scaledBitmap));
	}
	
	public void setButtonText(int numCols, int numRows) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Button button = buttons[row][col];
				if(game.isBushScanned(col, row)) {
					button.setText("" + game.getTeemosAround(col, row));
				}
			}
		}
	}
}
