package common;


import java.io.BufferedInputStream;
import java.util.Base64.Encoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class SocketDownloader {
	private static BufferedReader bufferedReader;
	private static BufferedWriter bufferedWriter;

	public static void fetchXml(String urlStr,String filename) throws IOException
	{
		
		URL url = new URL(urlStr);
		String host = url.getHost();
	    int port = url.getDefaultPort();
		SocketAddress dest = new InetSocketAddress(host,port);
		Socket socket = new Socket();
		
		socket.connect(dest);
		OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
		bufferedWriter = new BufferedWriter(streamWriter);
		bufferedWriter.write("GET " +url.getPath()+"?"+url.getQuery() +  " HTTP/1.1\r\n");
		bufferedWriter.write("Host: " + host + "\r\n");
		bufferedWriter.write("\r\n");
		bufferedWriter.flush();
		
		
		BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());
		bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));
		StringBuilder sb = new StringBuilder("");
		String line;
		boolean xmlSection = false;
		PrintWriter out = new PrintWriter(new File(filename),"UTF-8");
		while((line = bufferedReader.readLine())!= null)
		{
			if(line.startsWith("<")) {
				xmlSection = true;
			}
			if(xmlSection) {
				out.println(line);
				sb.append(line);
				sb.append("\r\n");
			}
		}
		out.close();
		//System.out.println(sb.toString());
		socket.close();
	}

}