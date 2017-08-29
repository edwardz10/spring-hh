package hello.services;

import java.util.List;
import java.util.Set;

import hello.entities.Keyword;

public class StaticUtils {

	public static boolean containsMinMax(String salary) {
		return (salary.indexOf("—") != -1 
				|| salary.indexOf("-") != -1
				|| salary.indexOf("–") != -1) ? true : false; 
	}

	public static Long toLong(String str) {
		str = str.replaceAll("[^\\d]", "");
		return Long.valueOf(str);
	}

	public static Long getAverage(Long min, Long max) {
		return (max + min) / 2;
	}

	public static String stringSetToString(Set<String> stringSet) {
		StringBuilder keywords = new StringBuilder();
		boolean first = true;
		
		for (String k : stringSet) {
			if (!first) {
				keywords.append(", ");
			} else {
				first = false;
			}
	
			keywords.append(k);
		}

		return keywords.toString().trim();
	}

	public static String keywordsToString(List<Keyword> kk) {
		StringBuilder keywords = new StringBuilder();

		boolean first = true;
		
		for (Keyword k : kk) {
			if (!first) {
				keywords.append(", ");
			} else {
				first = false;
			}
	
			keywords.append(k.getKeyword());
		}

		return keywords.toString().trim();
	}
}
