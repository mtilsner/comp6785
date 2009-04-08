package eu.tilsner.cubansea.utilities;

import java.util.ArrayList;
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
	
	public static List<String> multiply(String simple, int count) {
		List<String> _list = new ArrayList<String>();
		for(int i=0; i<count; i++) {
			_list.add(simple);
		}
		return _list;
	}
}
