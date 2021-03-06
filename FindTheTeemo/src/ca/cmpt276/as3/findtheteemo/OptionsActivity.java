package ca.cmpt276.as3.findtheteemo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.content.SharedPreferences;

public class OptionsActivity extends Activity {

	private static final String ERROR_MESSAGE = "Invalid game settings: number of Teemos cannot exceed total number of bushes!";
	private static final String RESET_STATS_MESSAGE = "Number of times played was reset!";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		    
		setupBoardSizeSpinner();
		setupNumberOfTeemos();
		getBoardSize();
		getNumberOfTeemos();
		setupEraseBtn();
	}

	private void setupBoardSizeSpinner() {
		final Spinner spinnerBoardSize = (Spinner) findViewById(R.id.spinnerBoardSize);
		spinnerBoardSize.setSelection(getBoardSize());
		spinnerBoardSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(getNumberOfTeemos() > 1 && pos == 0) {
					Toast.makeText(OptionsActivity.this, ERROR_MESSAGE, Toast.LENGTH_LONG).show();
					pos = getBoardSize();
					spinnerBoardSize.setSelection(pos);
				}
				SharedPreferences boardSizeSharedPref = getSharedPreferences("BoardSize", MODE_PRIVATE);
				
				SharedPreferences.Editor boardSizeEditor = boardSizeSharedPref.edit();
				boardSizeEditor.putInt("BoardSizeIndex", pos);
				boardSizeEditor.commit();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	private int getBoardSize() {
		SharedPreferences size = getSharedPreferences("BoardSize", MODE_PRIVATE);
		return size.getInt("BoardSizeIndex", 0);
	}
	
	private void setupNumberOfTeemos() {
		final Spinner spinnerNumberOfTeemos = (Spinner) findViewById(R.id.spinnerNumberOfTeemos);
		spinnerNumberOfTeemos.setSelection(getNumberOfTeemos());
		spinnerNumberOfTeemos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(getBoardSize() == 0 && pos > 1) {
					Toast.makeText(OptionsActivity.this, ERROR_MESSAGE, Toast.LENGTH_LONG).show();
					pos = getNumberOfTeemos();
					spinnerNumberOfTeemos.setSelection(pos);
				}
				SharedPreferences numberOfTeemosSharedPref = getSharedPreferences("NumberOfTeemos", MODE_PRIVATE);
				
				SharedPreferences.Editor numberOfTeemosEditor = numberOfTeemosSharedPref.edit();
				numberOfTeemosEditor.putInt("NumberOfTeemosIndex", pos);
				numberOfTeemosEditor.commit();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	private int getNumberOfTeemos() {
		SharedPreferences number = getSharedPreferences("NumberOfTeemos", MODE_PRIVATE);
		return number.getInt("NumberOfTeemosIndex", 0);
	}
	
	private void setupEraseBtn() {
		Button btn = (Button)findViewById(R.id.eraseTimesPlayedBtn);
        btn.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		SharedPreferences runStats = getSharedPreferences("GameStats", MODE_PRIVATE);
                
                SharedPreferences.Editor prefEditor = runStats.edit();
                prefEditor.putInt("TimesLoaded", 0);
                prefEditor.commit();
                
				Toast.makeText(OptionsActivity.this, RESET_STATS_MESSAGE, Toast.LENGTH_SHORT).show();
        	}
        });
	}
}
