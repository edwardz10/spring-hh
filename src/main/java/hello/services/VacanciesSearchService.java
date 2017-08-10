package hello.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		Set<Long> vacancyIds = getVacancyIds(searchParams); 
				
		LOG.info("Vacancy ids: " + vacancyIds);

		for (Long vacancyId : vacancyIds) {
			String url = urlBuilder.getVacancyUrl(vacancyId);
			LOG.info("Vacancy url: " + url);
			String vacancyResponse = restTemplate.getForObject(url, String.class, restParams);
			Vacancy v = parserService.getVacancy(vacancyId, url, vacancyResponse);

			vacancies.add(v);

			for (String keyword : v.getKeywordSet()) {
				if (!omittedKeywords.contains(keyword)) {
					statisticService.register(v.getId(), keyword);
				}
			}
		}

		return vacancies;
	}

	public void addOmittedKeyword(String keyword) {
		omittedKeywords.add(keyword);
	}

	private Set<Long> getVacancyIds(SearchParameters searchParams) {
		String vacanciesUrl = urlBuilder.getVacanciesUrlTotal(searchParams);
		Set<Long> vacancyIds = getVacancyIds(vacanciesUrl);
		Set<Long> portion;

		int pages = 20;

		for (int page = 0; page < pages; page++) {
			try {
				portion = getVacancyIds(urlBuilder.getVacanciesUrlWithPage(searchParams, page + 1));
				LOG.info("Vacancies on page " + page + ": " + portion);
				vacancyIds.addAll(portion);
			} catch (Exception e) {
				break;
			}
		}
		
		return vacancyIds;
	}

	private Set<Long> getVacancyIds(String url) {
		LOG.info("Get vacancies URL: " + url);
		String vacanciesResponse = restTemplate.getForObject(url, String.class, restParams);
		return parserService.getVacancyIds(vacanciesResponse);
	}

}
