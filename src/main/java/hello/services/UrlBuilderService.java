package hello.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import entities.SearchParameters;

@Service
public class UrlBuilderService {

	private static final Logger LOG = LoggerFactory.getLogger(UrlBuilderService.class);

	@Value("${url.basic}")
	private String basicUrl;

	public String getVacanciesUrl(SearchParameters searchParams) {
		return basicUrl + "search/vacancy?text=" + searchParams.getKeyword()
			+ "&area=2&salary=" + searchParams.getSalary() 
			+ "&currency_code=RUR&experience=doesNotMatter&order_by=relevance&search_period=&items_on_page=100&no_magic=true";
	}

	public String getVacancyUrl(long vacancyId) {
		return basicUrl + "vacancy/" + vacancyId + "?query=Java";
	}
}
