package hello.services;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UtilService {

	@Value("${usdrus}")
	private String usdRus;

	private Long usdExchangeRus;

	@PostConstruct
	public void initialize() {
		usdExchangeRus = Long.valueOf(usdRus);
	}

	public String stringSetToString(Set<String> stringSet) {
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

	public Long parseSalary(String salary) {
		Long parsedSalary = 0L;

		if (!salary.equals("n/a")) {
			if (containsMinMax(salary)) {
				String str1 = "" , str2 = "";

				int index = salary.indexOf("—");

				if (index != -1) {
					str1 = salary.substring(0, index);
					str2 = salary.substring(index + 1);
				} else {
					index = salary.indexOf("-");
					
					if (index != -1) {
						str1 = salary.substring(0, index);
						str2 = salary.substring(index + 1);
					}
				}

				if (!str1.isEmpty()) {
					parsedSalary = getAverage(toLong(str1), toLong(str2));
				}
			} else {
				parsedSalary = toLong(salary);
			}

			if (salary.endsWith("USD")
					|| salary.endsWith("$")) {
				parsedSalary *= usdExchangeRus;
			}

		}

		return parsedSalary;
	}

	public boolean containsMinMax(String salary) {
		return (salary.indexOf("—") != -1 
				|| salary.indexOf("-") != -1
				|| salary.indexOf("–") != -1) ? true : false; 
	}

	public static Long getAverage(Long min, Long max) {
		return (max + min) / 2;
	}

	public static Long toLong(String str) {
		str = str.replaceAll("[^\\d]", "");
		return Long.valueOf(str);
	}
	
	public static void main(String[] args) {
//		String num,num1,num2;
//		String str = "150 000—250 000 руб.";
//		Long avgSalary = 0L;
//		
//		if (containsMinMax(str)) {
//			String str1 = "" , str2 = "";
//
//			int index = str.indexOf("—");
//
//			if (index != -1) {
//				str1 = str.substring(0, index);
//				str2 = str.substring(index + 1);
//			} else {
//				index = str.indexOf("-");
//				
//				if (index != -1) {
//					str1 = str.substring(0, index);
//					str2 = str.substring(index + 1);
//				}
//			}
//
//			if (!str1.isEmpty()) {
//				avgSalary = getAverage(toLong(str1), toLong(str2));
//			}
//		} else {
//			avgSalary = toLong(str);
//		}
//
//		if (str.endsWith("USD")) {
//			avgSalary *= 60;
//		}
//
//		System.out.println("Average: " + avgSalary);
	}
}
