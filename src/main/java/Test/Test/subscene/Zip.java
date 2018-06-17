package Test.Test.subscene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Zip {

	private static final String zipTmpDir = "data/subtitles/tmp.zip";

	public static Zip download(String url)  {
		try {
			final OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(url)
					.build();

			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

				InputStream in = response.body().byteStream();
				OutputStream out = new FileOutputStream( zipTmpDir );
				byte[] buffer = new byte[4096];
				int n;
				while ((n = in.read(buffer)) != -1) 
				{
					out.write(buffer, 0, n);
				}
				out.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Zip();
	}

	public Zip unzip(String outputFile) {
		ZipInputStream zis = null;
		try {
			byte[] buffer = new byte[1024];

			zis = new ZipInputStream(new FileInputStream(zipTmpDir));
			FileOutputStream fos = new FileOutputStream(new File(outputFile));
			int len;
			zis.getNextEntry();
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			if(zis.getNextEntry() != null)
				throw new IOException("Error : more than one file in the downloaded zip directory");
		} catch(IOException e) {
			System.err.println("Unable to extract file from archive " + zipTmpDir);
			e.printStackTrace();
		} finally {
			if(zis != null) {
				try {
					zis.closeEntry();
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}

	public boolean delete() {
		return new File(zipTmpDir).delete();
	}
}
