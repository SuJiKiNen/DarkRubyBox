package utils;

public class Katakana {
	public static String toHiragana(String kata) {
		StringBuilder hira = new StringBuilder();
		for (int i = 0; i < kata.length(); i++) {
			char c = kata.charAt(i);
			if (c >= 'ア' && c <= 'ン') {
				hira.append((char)(c + 'ぁ' - 'ァ'));
			}
		}
		return hira.toString();
	}
}
