package hello.services;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hello.entities.SearchParameters;

@Service
public class UrlBuilderService {

	private static final Logger LOG = LoggerFactory.getLogger(UrlBuilderService.class);

	@Value("${url.basic}")
	private String basicUrl;

	private Map<Long, String> vacancyUrls;

	@PostConstruct
	public void initialize() {
		vacancyUrls = new LinkedHashMap<>();
	}
	
	public String getVacanciesUrlTotal(SearchParameters searchParams) {
		return basicUrl + "search/vacancy?text=" + searchParams.getKeyword()
			+ "&area=2&salary=" + searchParams.getSalary() 
			+ "&items_on_page=" + searchParams.getItemsOnPage();
	}

	public String getVacanciesUrlWithPage(SearchParameters searchParams, int page) {
		return basicUrl + "search/vacancy?text=" + searchParams.getKeyword()
			+ "&area=2&salary=" + searchParams.getSalary() 
			+ "&items_on_page=" + searchParams.getItemsOnPage()
			+ "&page=" + page;
	}

	public synchronized String getVacancyUrl(long vacancyId) {
		String url = vacancyUrls.get(vacancyId);

		if (url == null) {
			url = basicUrl + "vacancy/" + vacancyId + "?query=Java";
			vacancyUrls.put(vacancyId, url);
		}
		
		return url;
	}
}
