package Format;

public class OutputFormat {
	public String kanjiFormat="%s";
	public String kanaFormat="%s";
	public String othersFormat="%s";
	public OutputFormat(String kanji,String kana,String others) {
		this.kanjiFormat = kanji;
		this.kanaFormat = kana;
		this.othersFormat = others;
	}
	
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
	
	public String toFormatString(String fmt,String surface,String furigana,String hiragana,String katakana,String roman) {
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
}
