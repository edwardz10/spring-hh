package hello.services;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class CurrencyService {

	private static final Logger LOG = LoggerFactory.getLogger(CurrencyService.class);

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

	private GsonJsonParser parser;
	
	@PostConstruct
	public void initialize() {
		try {
			parser = new GsonJsonParser();
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

	public Double getUsdExchange() {
		return usdExchangeRus;
	}
	
	public Double getEuroExchange() {
		return eurExchangeRus;
	}

	private Double getRubCurrencyExchange(String currency) throws Exception {
		String usdRates = restTemplate.getForObject(USD_RATES_URL + currency, String.class, restParams);
		LinkedTreeMap<String, Object> rates = (LinkedTreeMap<String, Object>)parser.parseMap(usdRates).get("rates");
		
		return (Double)rates.get("RUB");
	}
}
