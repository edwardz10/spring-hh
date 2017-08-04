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
	
	public String getVacanciesUrl(SearchParameters searchParams) {
		return basicUrl + "search/vacancy?text=" + searchParams.getKeyword()
			+ "&area=2&salary=" + searchParams.getSalary() 
			+ "&currency_code=RUR"
//			+ "&only_with_salary=true"
			+ "&experience=doesNotMatter"
			+ "&order_by=relevance"
			+ "&search_period="
			+ "&items_on_page=100"
			+ "&no_magic=true";
	}

	public String getVacancyUrl(long vacancyId) {
		String url = vacancyUrls.get(vacancyId);

		if (url == null) {
			url = basicUrl + "vacancy/" + vacancyId + "?query=Java";
			vacancyUrls.put(vacancyId, url);
		}
		
		return url;
	}
}
