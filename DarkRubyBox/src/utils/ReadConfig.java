package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public final class ReadConfig {
	private Properties prop;// = new Properties();
	private static String configFilePath;
	private static ReadConfig readSentimentConfig;
	
	private ReadConfig() {
		prop = new Properties();
		BufferedReader ReadMag;

		try {
			ReadMag = new BufferedReader(new FileReader(configFilePath));
			prop.load(ReadMag);
			ReadMag.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param configFilePath
	 * @return
	 */
	public static ReadConfig getInstance(String configFilePath){
		if(readSentimentConfig==null && !configFilePath.isEmpty()){
			ReadConfig.readSentimentConfig = new ReadConfig();
			ReadConfig.configFilePath = configFilePath;
		}
		else if(readSentimentConfig!=null && !configFilePath.equalsIgnoreCase(ReadConfig.configFilePath))
		{
			ReadConfig.readSentimentConfig = new ReadConfig();
			ReadConfig.configFilePath = configFilePath;
		}
		
		return readSentimentConfig;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static ReadConfig getInstance(){
		if(readSentimentConfig==null){
			System.err.print("Please pass yours file path to the function as an arguments");
			return null;
		}
				
		return readSentimentConfig;
	}
	
	/**
	 * 
	 * @param ParamName
	 * @return
	 */
	public String ReadValue(String ParamName)
	{
				return prop.getProperty(ParamName).toString();
	}
}