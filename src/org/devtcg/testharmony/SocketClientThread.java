package org.devtcg.testharmony;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClientThread extends ClientThread
{
	private final Socket mSocket;

	private static final String REQUEST;

	static {
		REQUEST = "GET /pub/dist/knoppix/knoppix-dvd/KNOPPIX_V5.3.1DVD-2008-03-26-EN.iso HTTP/1.0\r\n\r\n";
	}

	public SocketClientThread()
	{
		super("SocketClientThread");

		mSocket = new Socket();
	}

	@Override
	protected void tryDownload() throws Exception
	{
		mSocket.connect(new InetSocketAddress("204.152.191.37", 80));

		OutputStream out = mSocket.getOutputStream();
		out.write(REQUEST.getBytes());

		InputStream in = null;
		try {
			in = mSocket.getInputStream();

			byte[] b = new byte[4096 * 10];
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

	public void onRequestCancel()
	{
		try {
			/*
			 * Removing shutdownInput and shutdownOutput cause the download
			 * thread to block up to the socket timeout!
			 */
			mSocket.shutdownInput();
			mSocket.shutdownOutput();
			mSocket.close();
		} catch (IOException e) {}
	}
}
