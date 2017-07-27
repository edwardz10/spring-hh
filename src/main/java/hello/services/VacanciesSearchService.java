package hello.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import entities.SearchParameters;
import entities.Vacancy;

@Service
public class VacanciesSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(VacanciesSearchService.class);

	private final UrlBuilderService urlBuilder;
	private final ParserService parserService;

	private RestTemplate restTemplate;
	private HttpHeaders headers;
	private Map<String, String> restParams;
	
	@Autowired
	public VacanciesSearchService(UrlBuilderService urlBuilder, ParserService parserService) {
		this.urlBuilder = urlBuilder;
		this.parserService = parserService;
	}

	@PostConstruct
	public void initialize() {
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restParams = new LinkedHashMap<>();
	}
	
	public List<Vacancy> findVacancies(SearchParameters searchParams) {
		List<Vacancy> vacancies = new ArrayList<>();
		String vacanciesResponse = restTemplate.getForObject(urlBuilder.getVacanciesUrl(searchParams), String.class, restParams);

		List<Long> vacancyIds = parserService.getVacancyIds(vacanciesResponse);
		LOG.info("Vacancy ids: " + vacancyIds);

		for (Long vacancyId : vacancyIds) {
			String vacancyResponse = restTemplate.getForObject(urlBuilder.getVacancyUrl(vacancyId), String.class, restParams);
			Vacancy v = parserService.getVacancy(vacancyResponse);

			if (v.getSalary() != "n/a") {
				vacancies.add(v);	
			}
		}

		Collections.sort(vacancies, new Comparator<Vacancy>() {
			public int compare(Vacancy v1,Vacancy v2) {
				return (v1.getSalary().compareTo(v2.getSalary()));
          }});
		
		return vacancies;
	}

}
