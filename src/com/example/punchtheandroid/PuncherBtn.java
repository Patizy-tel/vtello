package com.example.punchtheandroid;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.view.View;

/**
 * An instance of this class represents a single button.
 *
 */
public class PuncherBtn {
	private View view;
	private boolean enable;
	private boolean status;
	private GameActivity ga;
	private Thread scheduler;
	
	/**
	 * Construct a PuncherBtn instance
	 * @param view	the view of a btn, or a text view
	 * @param ga	the GameActivity where this view lies in
	 */
	public PuncherBtn(View view, GameActivity ga) {
		this.view = view;
		this.enable = true;
		this.status = false;
		this.ga = ga;
	}
	
	public void start() {
		scheduler = new Thread(new ShowHideScheduler());
		scheduler.start();
	}
	
	public void click() {
		/* Doesn't work if the puncher is hidden */
		if (this.status) {
			hidePuncher();
			GameActivity.incScore();
			scheduler.interrupt();
			start();
		}
	}
	
	public synchronized void showPuncher() {
		this.status = true;
		ga.showPuncher(view);
	}
	
	private synchronized void hidePuncher() {
		this.status = false;
		ga.hidePuncher(view);
	}
	
	public void enable() {
		this.enable = true;
	}
	
	public void disable() {
		this.enable = false;
	}
	
	public void stop() {
		scheduler.interrupt();
	}
	
	private class ShowHideScheduler implements Runnable {
		@Override
		public void run() {
			Random rand = new Random(); 
			int interval = 0;
			
			ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
			ScheduledFuture<?> future;
			while (enable) {
				interval = rand.nextInt(4000) + 1000;
				future = ses.schedule(new Runnable() {
					@Override
					public void run() {
						showPuncher();
					}
				}, interval, TimeUnit.MILLISECONDS);
				
				if (Thread.currentThread().isInterrupted()) {
					hidePuncher();
					return;
				}
				
				try {
					future.get();
				} catch (InterruptedException e) {
					return;
				} catch (ExecutionException e) {
					return;
				}
				
				future = ses.schedule(new Runnable() {
					@Override
					public void run() {
						hidePuncher();
					}
				}, 1, TimeUnit.SECONDS);
				
				try {
					future.get();
				} catch (InterruptedException e) {
					return;
				} catch (ExecutionException e) {
					return;
				}
			}
		}
	}
}
