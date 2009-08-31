package org.devtcg.testharmony;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

/*
 * This class is not being used in the test, but is provided to demonstrate
 * how I ultimately arrived at this bug.
 */
class HttpClientThread extends ClientThread
{
	private final HttpClient mClient;
	private volatile HttpRequestBase mMethod;

	public HttpClientThread()
	{
		super("HttpClientThread");
		mClient = new DefaultHttpClient();
	}

	@Override
	protected void tryDownload() throws Exception
	{
		mMethod = new HttpGet("http://204.152.191.37/pub/dist/knoppix/knoppix-dvd/KNOPPIX_V5.3.1DVD-2008-03-26-EN.iso");

		System.out.println("Connecting...");
		HttpResponse resp = mClient.execute(mMethod);

		InputStream in = null;

		try {
			in = resp.getEntity().getContent();
			byte[] b = new byte[4096*10];
			int n;
			while ((n = in.read(b)) >= 0)
			{
				if (hasCanceled())
					break;

				onRead(b, 0, n);
			}
		} finally {
			if (in != null)
				try { in.close(); } catch (IOException e) {}
		}
	}

	@Override
	protected void onRequestCancel()
	{
		/*
		 * This doesn't work properly because HttpClient merely calls
		 * Socket#close!
		 */
		if (mMethod != null)
			mMethod.abort();
	}
}
