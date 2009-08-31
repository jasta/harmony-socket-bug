package org.devtcg.testharmony;

public abstract class ClientThread extends CancelableThread
{
	private int x;

	public ClientThread(String name)
	{
		super(name);
	}

	public final void run()
	{
		try {
			tryDownload();
		} catch (Exception e) {
			System.out.println("Exception: " + e.toString());
		}

		System.out.println("Canceled = " + hasCanceled());
	}

	protected abstract void tryDownload() throws Exception;

	protected void onRead(byte[] b, int offset, int length)
	{
		System.out.println("[" + x++ + "] Read " + length + " bytes");
	}
}
