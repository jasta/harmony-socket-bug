package org.devtcg.testharmony;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class SocketCloseTest extends Activity {
	private CancelableThread mThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.button).setOnClickListener(mClickListener);
	}

	private final OnClickListener mClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.button:
					startTest();
					break;
			}
		}
	};

	private synchronized void startTest()
	{
		if (mThread != null)
		{
			System.out.println("Already running, please wait...");
			return;
		}

		mThread = new SocketClientThread();
		mThread.start();

		mHandler.sendEmptyMessageDelayed(0, 8000);
	}

	private final Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			System.out.println("requestCancelAndWait()...");
			synchronized (SocketCloseTest.this) {
				mThread.requestCancelAndWait();
				mThread = null;
			}
			System.out.println("Exiting...");

		}
	};
}
