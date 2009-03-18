package eu.tilsner.cubansea.utilities;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringHelper {
	public static String join(List<String> words, String separator) {
		return StringUtils.join(words, separator);
	}

	public static List<String> split(String content, String separator) {
		return Arrays.asList(StringUtils.split(content, separator));
	}
}
