package common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.Hiragana;
import utils.Katakana;
import utils.RomanizationEnum;



//http://en.wikipedia.org/wiki/Kana
/* %f for furigana
 * %h for hiragana
 * %k for katakana
 * %r for roman
 * %s for surface 
 * 
 * %(separator%)
 * %[separator%)
 * %(separator%]
 * %[separator%]
 * input specific format string include 3 parts
 * 1,kanji
 * 2,kana(hiragana katakana)
 * 3,others
 */

public class XML {
	
	public static String kanjiFormat="%s";
	public static String kanaFormat="%s";
	public static String othersFormat="%s";
	public static String APPID = "";
	
	public static String join(String str,String delimiter,boolean leftBorder,boolean rightBorder){
		StringBuilder joinStringBuilder = new StringBuilder();
		for(int i=0; i<str.length(); i++) {
			if(i<=0) {
				if(leftBorder) {
					joinStringBuilder.append(delimiter);
				}
			}else {
				joinStringBuilder.append(delimiter);
			}
			joinStringBuilder.append(str.charAt(i));
		}
		if(rightBorder) {
			joinStringBuilder.append(delimiter);
		}
		return joinStringBuilder.toString();
	}
	public static String specificFmtString(String fmt,String surface,String furigana,String hiragana,String katakana,String roman) {
		byte state = 0;
		StringBuilder ret = new StringBuilder("");
		boolean hasDelimiter = false;
		boolean leftBorder = false;
		boolean rightBorder = false;
		StringBuilder delimiter = new StringBuilder("");
		
		for(int i=0; i<fmt.length(); i++) {
			char c = fmt.charAt(i);
			switch (state) {
			case 0:
				if(c=='%') {
					state = 1;
				}else {
					ret.append(c);
				}
				break;
				
			case 1:
				if(c=='(') {
					delimiter = new StringBuilder("");
					state = 2;
					hasDelimiter = true;
					leftBorder = false;
					break;
				}
				if(c==')') {
					state = 0;
					rightBorder = false;
					break;
				}
				if(c=='[') {
					delimiter = new StringBuilder("");
					state = 2;
					hasDelimiter = true;
					leftBorder = true;
					break;
				}
				if(c==']') {
					state = 0;
					rightBorder = true;
					break;
				}
				if(c=='f') {
					if(hasDelimiter) {
						ret.append(join(furigana, delimiter.toString(),leftBorder,rightBorder));
					}else {
						ret.append(furigana);
					}
					hasDelimiter = false;
					delimiter = null;
					state = 0;
					break;
				}
				if(c=='h') {
					if(hasDelimiter) {
						ret.append(join(hiragana, delimiter.toString(), leftBorder, rightBorder));
					}else {
						ret.append(hiragana);
					}
					hasDelimiter = false;
					delimiter = null;
					state = 0;
					break;
				}
				if(c=='k') {
					if(hasDelimiter) {
						ret.append(join(katakana, delimiter.toString(), leftBorder, rightBorder));
					}else {
						ret.append(katakana);
					}
					hasDelimiter = false;
					delimiter = null;
					state = 0;
					break;
				}
				if(c=='r') {
					if(hasDelimiter) {
						ret.append(join(roman, delimiter.toString(), leftBorder, rightBorder));
					}else {
						ret.append(roman);
					}
					hasDelimiter = false;
					delimiter = null;
					state = 0;
				}
				if(c=='s') {
					if(hasDelimiter) {
						ret.append(join(surface, delimiter.toString(), leftBorder, rightBorder));
					}else {
						ret.append(surface);
					}
					hasDelimiter = false;
					delimiter = null;
					state = 0;
				}
				break;
				
			case 2:
				if(c=='%') {
					state = 1;
				}else {
					delimiter.append(c);
				}
				break;
			default:
				break;
			}
		}
		return ret.toString();
	}
	public static Node getSubWords(Node word){
		NodeList wordChilds = word.getChildNodes();
		for(int i=0; i<wordChilds.getLength(); i++) {
			Node wordChild = wordChilds.item(i);
			if(wordChild.getNodeName().equalsIgnoreCase("SubWordList")) {
				return wordChild;
			}
		}
		return null;
	}
	public static String leafNode2String(Node word) {
		NodeList wordChilds = word.getChildNodes();
		String surface = "";
		String furigana = "";
		String hiragana = "";
		String katakana = "";
		String roman = "";
		StringBuilder fmtString = new StringBuilder();
		for(int i=0; i<wordChilds.getLength(); i++) {
			Node t = wordChilds.item(i);
			if(t.getNodeName().equalsIgnoreCase("Surface")) {
				surface = t.getTextContent();
			}
			if(t.getNodeName().equalsIgnoreCase("Furigana")) {
				furigana = t.getTextContent();
			}
			if(t.getNodeName().equalsIgnoreCase("Roman")) {
				roman = t.getTextContent();
			}
		}
		
		katakana = Hiragana.toKatakana(furigana);
		
		if(surface.equals("")==false ){
				boolean isKanji =( Character.UnicodeBlock.of( surface.charAt(0) ) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
				boolean isKata = ( Character.UnicodeBlock.of( surface.charAt(0) ) == Character.UnicodeBlock.KATAKANA);
				boolean isHira = ( Character.UnicodeBlock.of( surface.charAt(0) ) == Character.UnicodeBlock.HIRAGANA);
				if(isHira || isKata) {
					roman = RomanizationEnum.Hepburn.toRomaji(surface);
					furigana = surface;
					if(isHira) {
						hiragana = surface;
						katakana = Hiragana.toKatakana(hiragana);
					}
					if(isKata) {
						katakana = surface;
						hiragana = Katakana.toHiragana(katakana);
					}
				}else {
					roman = RomanizationEnum.Hepburn.toRomaji(furigana);
				}
				if(isKanji) {
					
					if(furigana.equals("")==false || roman.equals("")==false || katakana.equals("")==false) {
						fmtString.append(specificFmtString(kanjiFormat, surface, furigana, furigana, katakana,roman));
					}else{
						fmtString.append(surface);
					}
					
				}else if (isHira || isKata ) {
					
					if(furigana.equals("")==false || roman.equals("")==false || katakana.equals("")==false) {
						fmtString.append(specificFmtString(kanaFormat, surface, furigana, furigana, katakana,roman));
					}else{
						fmtString.append(surface);
					}
					
				}else {
					
					if(furigana.equals("")==false || roman.equals("")==false || katakana.equals("")==false) {
						fmtString.append(specificFmtString(othersFormat, surface, furigana, furigana, katakana,roman));
					}else{
						fmtString.append(surface);
					}
				}
		}
		
		/*
		if(surface!="") {
			fmtString = surface;
			if(furigana.equals("") == false && surface.equals(furigana)==false) {
				fmtString+="|"+furigana;
			}
		}*/
		return fmtString.toString();
	}
	public static String toFormatString(String propPath,String xmlPath) {
		//String yahooAPIRoot = "http://jlp.yahooapis.jp/FuriganaService/V1/furigana";
		//String yahooReqeustUrl = yahooAPIRoot + "?appid=" + APPID + "&grade="+ grade + "&sentence="+text;
		Properties currentFormat = new Properties();
		try {
			FileInputStream fileInputStream = new FileInputStream(propPath);
			currentFormat.load(fileInputStream);
			
			kanjiFormat = currentFormat.getProperty("KanjiFormat");
			kanaFormat = currentFormat.getProperty("KanaFormat");
			othersFormat = currentFormat.getProperty("OthersFormat");
			fileInputStream.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlPath);
			NodeList words = document.getElementsByTagName("Word");
			StringBuilder fmtString = new StringBuilder("");
			for(int i=0; i<words.getLength(); i++) {
				Node word = words.item(i);
				Node subWords = getSubWords(word);
				if(subWords!=null) {
					NodeList subWordsList = subWords.getChildNodes();
					for(int j=0; j<subWordsList.getLength(); j++) {
						Node subWord = subWordsList.item(j);
						fmtString.append(leafNode2String(subWord));
					}
				}else {
					fmtString.append(leafNode2String(word));
				}
			}
			return fmtString.toString();
			//System.out.println(fmtString);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static void readAPPID(){
		Properties properties = new Properties();
		String APPIDPath = "." + File.separator + "Config"+ File.separator + "APPID"+File.separator+"ID.properties";
		try {
			FileInputStream fileInputStream = new FileInputStream(APPIDPath);
			properties.load(fileInputStream);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		APPID = properties.getProperty("APPID");
		
	}
	public static void fetch(String text,String grade,String storePath){
		//storePath ="." + File.separator + "temp" + File.separator+ "123" + ".xml";
		String yahooAPIRoot = "http://jlp.yahooapis.jp/FuriganaService/V1/furigana";
		String yahooReqeustUrl = yahooAPIRoot + "?appid=" + APPID + "&grade="+ grade + "&sentence="+text;
		//HttpDownloader httpDownloader = new HttpDownloader();
		//httpDownloader.download(yahooReqeustUrl, new File(storePath));
		//System.out.println(yahooReqeustUrl);
		
		
		try {
			//TestDemo.fetchFile(yahooReqeustUrl, new File(storePath));
			SocketDownloader.fetchXml(yahooReqeustUrl,storePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//String propPath ="." + File.separator + "Config"+ File.separator + "OutputConfig"+File.separator+"LRC.properties";
		//toFormatString(propPath, storePath);
	}
	
	/*
	public static void main(String args[]){
		readAPPID();
		fetch("どんなに どんなに どんなに どんなに 遠くても", "1", "");
	}
	*/
}
