package utils;

public class Hiragana {
	public static String toKatakana(String hira) {
		StringBuilder kata = new StringBuilder();
		for (int i = 0; i < hira.length(); i++) {
			char c = hira.charAt(i);
			if (c >= 'ぁ' && c <= 'ん') {
				kata.append((char)(c - 'ぁ' + 'ァ'));
			}
		}
		return kata.toString();
	}
}
