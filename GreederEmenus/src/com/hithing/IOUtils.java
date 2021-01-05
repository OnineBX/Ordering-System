package com.hithing;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class IOUtils{

	public static boolean copyUrl(String srcUrl, OutputStream destOutpstream) {
		InputStream is = null;
		HttpEntity entity = null;
		HttpClient client = null;
		boolean finish = false;
		try {
			client = new DefaultHttpClient();
			HttpGet get = new HttpGet(srcUrl);
			HttpResponse response = client.execute(get);
			entity = response.getEntity();
			is = entity.getContent();
			copy(is, destOutpstream);
			finish = true;
		} catch (IOException ex) {

		} finally {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {

				}
			}
			 closeQuietly(is);
			 closeQuietly(destOutpstream);
		}
		return finish;
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L)
			return -1;
		else
			return (int) count;
	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte buffer[] = new byte[4096];
		long count = 0L;
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}

	public static void closeQuietly(InputStream input) {
		closeQuietly(((Closeable) (input)));
	}

	public static void closeQuietly(OutputStream output) {
		closeQuietly(((Closeable) (output)));
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null)
				closeable.close();
		} catch (IOException ioe) {
		}
	}
}
