package hello.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.internal.LinkedTreeMap;

@Service
public class UtilService {

	private static final Logger LOG = LoggerFactory.getLogger(UtilService.class);

	private static String USD_RATES_URL = "http://api.fixer.io/latest?base=";
	
	@Value("${usdrus}")
	private String usdRus;
	
	@Value("${eurrus}")
	private String eurRus;

	private Double usdExchangeRus;
	private Double eurExchangeRus;

	private RestTemplate restTemplate;
	private HttpHeaders headers;
	private Map<String, String> restParams;

	@PostConstruct
	public void initialize() {
		try {
			restTemplate = new RestTemplate();
			headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			restParams = new LinkedHashMap<>();
	
			usdExchangeRus = getRubCurrencyExchange("USD");
			eurExchangeRus = getRubCurrencyExchange("EUR");
		} catch (Exception e) {
			LOG.warn("Failed to get currency exchange via " + USD_RATES_URL + ": " + e.getMessage());
			usdExchangeRus = Double.valueOf(usdRus);
			eurExchangeRus = Double.valueOf(eurRus);
		} 

		LOG.info("USD-RUB currency exchange: " + usdExchangeRus);
	}

	private Double getRubCurrencyExchange(String currency) throws Exception {
		String usdRates = restTemplate.getForObject(USD_RATES_URL + currency, String.class, restParams);
		GsonJsonParser parser = new GsonJsonParser();
		Map<String, Object> values = parser.parseMap(usdRates);

		LinkedTreeMap<String, Object> rates = (LinkedTreeMap<String, Object>)values.get("rates");
		
		return (Double)rates.get("RUB");
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
				parsedSalary = new Long(Math.round(parsedSalary * usdExchangeRus));
			} else if (salary.endsWith("EUR")) {
				parsedSalary = new Long(Math.round(parsedSalary * eurExchangeRus));
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
		String USD_RATES_URL = "http://api.fixer.io/latest?base=USD";
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> restParams = new LinkedHashMap<>();

		String usdRates = restTemplate.getForObject(USD_RATES_URL, String.class, restParams);
		GsonJsonParser parser = new GsonJsonParser();
		Map<String, Object> values = parser.parseMap(usdRates);

		LinkedTreeMap<String, Object> rates = (LinkedTreeMap<String, Object>)values.get("rates");
//		Object usd = rates.get("USD");
//		LOG.info("usd: " + usd + ", class: " + usd.getClass());
	}
}
