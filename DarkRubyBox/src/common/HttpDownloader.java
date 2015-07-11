package common;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader {
	private URL url = null;

	// 该函数返回整形 -1：代表下载文件出错 ;0：代表下载文件成功; 1：代表文件已经存在
	public int download(String urlStr, File file) {
		int downloadResult = -1;
		InputStream inputstream = null;
		OutputStream outputstream = null;
		HttpURLConnection httpUrlConnection = null;

		try {
			url = new URL(urlStr);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestMethod("GET");
		    httpUrlConnection.setReadTimeout(3000);
			httpUrlConnection.setRequestProperty("User-Agent", "User-Agent: Yahoo AppID: dj0zaiZpPXZPM3JFem9KaGF5TyZzPWNvbnN1bWVyc2VjcmV0Jng9ZDA-");
			httpUrlConnection.setRequestProperty("Accept-Charset", "UTF-8"); 
			httpUrlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			httpUrlConnection.setDoInput(true);
			inputstream = httpUrlConnection.getInputStream();
			
			
			outputstream = new FileOutputStream(file);
			byte buffer[] = new byte[102400];
			int len = 0;
			while ((len = inputstream.read(buffer)) != -1) {
				outputstream.write(buffer, 0, len);
			}

			outputstream.flush();
			downloadResult = 0;
		} catch (IOException e) {
			e.printStackTrace();
			downloadResult = 1;
		} finally {
			try {
				outputstream.close();
				inputstream.close();
				httpUrlConnection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return downloadResult;
	}
}