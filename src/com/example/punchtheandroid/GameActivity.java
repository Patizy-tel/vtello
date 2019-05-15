package com.example.punchtheandroid;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class GameActivity extends Activity {
	private Map<Integer, PuncherBtn> punchers;
	private static TextView score;
	private static int score_num;
	
	public GameActivity() {
		this.punchers = new HashMap<Integer, PuncherBtn>();
		score_num = 0;
	}
	
	@Override
	public void onBackPressed() {
		/* Do nothing to disable the back button */
	}
	
	private GameActivity getGameActivity() {
		return this;
	}
	
	public int getScore() {
		return score_num;
	}
	
	public static synchronized void incScore() {
		score_num++;
		score.setText("Score: " + (score_num * 10));
	}
	
	public synchronized void showPuncher(final View view) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				view.setBackgroundResource(R.drawable.puncher);
			}
		});
	}
	
	public synchronized void hidePuncher(final View view) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				view.setBackgroundResource(R.drawable.box);
			}
		});
	}
	

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		/* Initialize all the btns */
		punchers.put(R.id.btn11, new PuncherBtn(findViewById(R.id.btn11), this));
		punchers.put(R.id.btn12, new PuncherBtn(findViewById(R.id.btn12), this));
		punchers.put(R.id.btn13, new PuncherBtn(findViewById(R.id.btn13), this));
		punchers.put(R.id.btn21, new PuncherBtn(findViewById(R.id.btn21), this));
		punchers.put(R.id.btn22, new PuncherBtn(findViewById(R.id.btn22), this));
		punchers.put(R.id.btn23, new PuncherBtn(findViewById(R.id.btn23), this));
		punchers.put(R.id.btn31, new PuncherBtn(findViewById(R.id.btn31), this));
		punchers.put(R.id.btn32, new PuncherBtn(findViewById(R.id.btn32), this));
		punchers.put(R.id.btn33, new PuncherBtn(findViewById(R.id.btn33), this));
				
		score = (TextView)findViewById(R.id.score);
		final TextView info = (TextView)findViewById(R.id.game);
		
		new CountDownTimer(60000, 1000) {

		    public void onTick(long millisUntilFinished) {
		        info.setText("Time: " + millisUntilFinished / 1000 + "s");
		    }

		    public void onFinish() {
		        info.setText("Finish!");
		 		for (Entry<Integer, PuncherBtn> btn : punchers.entrySet()) {
					btn.getValue().disable();
					new AlertDialog.Builder(getGameActivity())
				    .setTitle("Game finish!")
				    .setMessage("Your score is " + (score_num * 10) + "!")
				    .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            // refresh everything
				    		Intent intent = new Intent(getGameActivity(), GameActivity.class);
				    		startActivity(intent);
				        }
				     })
				    .setNegativeButton("Back home", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				    		Intent intent = new Intent(getGameActivity(), MainActivity.class);
				    		startActivity(intent);
				        }
				     })
				     .show();
				}
		    }
		 }.start();
		 
		/* Start the game */
		for (Entry<Integer, PuncherBtn> btn : punchers.entrySet()) {
			btn.getValue().enable();
			btn.getValue().start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	public void clickBtn(View view) {
		punchers.get(view.getId()).click();
	}
}
