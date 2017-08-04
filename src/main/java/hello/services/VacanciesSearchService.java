package hello.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

import hello.entities.SearchParameters;
import hello.entities.Vacancy;

@Service
public class VacanciesSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(VacanciesSearchService.class);

	private final UrlBuilderService urlBuilder;
	private final ParserService parserService;
	private final StatisticsService statisticService;

	private RestTemplate restTemplate;
	private HttpHeaders headers;
	private Map<String, String> restParams;

	private List<String> omittedKeywords;
	
	@Autowired
	public VacanciesSearchService(UrlBuilderService urlBuilder, ParserService parserService,
			StatisticsService statisticService) {
		this.urlBuilder = urlBuilder;
		this.parserService = parserService;
		this.statisticService = statisticService;
	}

	@PostConstruct
	public void initialize() {
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restParams = new LinkedHashMap<>();
		omittedKeywords = new LinkedList<>();
	}
	
	public List<Vacancy> findVacancies(SearchParameters searchParams) {
		List<Vacancy> vacancies = new ArrayList<>();
		String getVacanciesUrl = urlBuilder.getVacanciesUrl(searchParams);
		LOG.info("Get vacancies URL: " + getVacanciesUrl);
		String vacanciesResponse = restTemplate.getForObject(getVacanciesUrl, String.class, restParams);

		List<Long> vacancyIds = parserService.getVacancyIds(vacanciesResponse);
		LOG.info("Vacancy ids: " + vacancyIds);

		for (Long vacancyId : vacancyIds) {
			String url = urlBuilder.getVacancyUrl(vacancyId);
			LOG.info("Vacancy url: " + url);
			String vacancyResponse = restTemplate.getForObject(url, String.class, restParams);
			Vacancy v = parserService.getVacancy(vacancyId, url, vacancyResponse);

//			if (v.getSalary() != "n/a") {
				vacancies.add(v);

				for (String keyword : v.getKeywordSet()) {
					if (!omittedKeywords.contains(keyword)) {
						statisticService.register(v.getId(), keyword);
					}
				}
//			}
		}

		return vacancies;
	}

	public void addOmittedKeyword(String keyword) {
		omittedKeywords.add(keyword);
	}
}
