package hello.services;

//import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import hello.entities.SearchParameters;
import hello.entities.Vacancy;
import hello.services.repositories.VacanciesRepository;

@Service
public class VacanciesSearchService {

	static final Logger LOG = LoggerFactory.getLogger(VacanciesSearchService.class);

	/**
	 * Services
	 */
	@Autowired 
	UrlBuilderService urlBuilder;
	@Autowired 
	VacanciesRepository vacanciesRepository;
	@Autowired 
	KeywordsAnalizerService analizerService;
	@Autowired 
	StatisticsService statisticsService;
	@Autowired 
	CurrencyService currencyService;

	private List<Vacancy> vacancies;
	private RestTemplate restTemplate;
	private HttpHeaders headers;
	private Map<String, String> restParams;

	private List<String> omittedKeywords;

	private int availableProcessors;
	
	private ExecutorService executorService;
	
	@PostConstruct
	public void initialize() {
		vacancies = new CopyOnWriteArrayList<>();
		
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restParams = new LinkedHashMap<>();
		omittedKeywords = new LinkedList<>();

		availableProcessors = Runtime.getRuntime().availableProcessors();

		LOG.info("Available processors: " + availableProcessors);
	}

	public List<Vacancy> getVacancies() {
		return vacancies;
	}

	public void startFeed(SearchParameters searchParams) {
	    executorService = Executors.newFixedThreadPool(availableProcessors);
        addOmittedKeyword(searchParams.getKeyword());
		Set<Long> vacancyIds = getVacancyIds(searchParams); 
		LinkedList<Long> vacanciesFromHH = new LinkedList<>();
		
		LOG.info("Vacancy ids: " + vacancyIds
				+ ", using search parameters: " + searchParams
				+ ", in total: " + vacancyIds.size());

		vacancyIds.forEach(vId -> {
				Vacancy v = vacanciesRepository.findOne(vId);
	
				if (v == null) {
					vacanciesFromHH.push(vId);
					LOG.info("Vacancy with id=" + vId + " must be fetched from hh.ru...");
				} else {
					vacancies.add(v);
					LOG.info("Fetched vacancy with id=" + v.getId() + " from the DB...");
				}
			}
		);
		
		if (!vacanciesFromHH.isEmpty()) {
			for (int i = 0; i < availableProcessors; i++) {
				executorService.execute(new VacanciesFeeder(this, vacanciesFromHH, vacancies));
			}

		}
	}
	
	public List<Vacancy> findVacancies(SearchParameters searchParams) {
        addOmittedKeyword(searchParams.getKeyword());
		Set<Long> vacancyIds = getVacancyIds(searchParams); 
		LinkedList<Long> vacanciesFromHH = new LinkedList<>();
		
		LOG.info("Vacancy ids: " + vacancyIds
				+ ", using search parameters: " + searchParams
				+ ", in total: " + vacancyIds.size());

		vacancyIds.forEach(vId -> {
				Vacancy v = vacanciesRepository.findOne(vId);
	
				if (v == null) {
					vacanciesFromHH.push(vId);
					LOG.info("Vacancy with id=" + vId + " must be fetched from hh.ru...");
				} else {
					vacancies.add(v);
					LOG.info("Fetched vacancy with id=" + v.getId() + " from the DB...");
				}
			}
		);
		
		if (!vacanciesFromHH.isEmpty()) {
			for (int i = 0; i < availableProcessors; i++) {
				executorService.execute(new VacanciesFeeder(this, vacanciesFromHH, vacancies));
			}

		}

		return vacancies;
	}

	public void addOmittedKeyword(String keyword) {
		omittedKeywords.add(keyword);
	}

	private Set<Long> getVacancyIds(SearchParameters searchParams) {
		String vacanciesUrl = urlBuilder.getVacanciesUrlTotal(searchParams);
		Set<Long> vacancyIds = getVacancyIdsFromUrl(vacanciesUrl);
		Set<Long> portion;

		int pages = 20;

		for (int page = 0; page < pages; page++) {
			try {
				portion = getVacancyIdsFromUrl(urlBuilder.getVacanciesUrlWithPage(searchParams, page + 1));
				LOG.info("Vacancies on page " + page + ": " + portion);
				vacancyIds.addAll(portion);
			} catch (Exception e) {
				break;
			}
		}
		
		return vacancyIds;
	}

	private Set<Long> getVacancyIdsFromUrl(String url) {
		LOG.info("Get vacancies URL: " + url);
		String vacanciesResponse = restTemplate.getForObject(url, String.class, restParams);
		return getVacancyIds(vacanciesResponse);
	}

	private Set<Long> getVacancyIds(String vacanciesHtml) {
		Set<Long> vacancyIds = new LinkedHashSet<>();
		Document doc = Jsoup.parse(vacanciesHtml);
		Elements links = doc.select("a");

		links.forEach(link -> {
			String href = link.attr("href");
			
			if (href != null && href.contains("vacancy_id=")) {
				vacancyIds.add(Long.valueOf(href.split("=")[1]));	
			}
		});
		
		return vacancyIds;
	}

    public void stop() {
        executorService.shutdown();
        
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            vacancies.clear();
        } catch (InterruptedException ie) {
            LOG.warn("Failed to stop threads: " + ie.getLocalizedMessage());
        }
        
    }

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> restParams = new LinkedHashMap<>();
		String url = "https://spb.hh.ru/vacancy/23318016?query=Java";
		Long vacancyId = 23318016L;
		String position = "", company = "", salary = "";
		
		String vacancyResponse = restTemplate.getForObject(url, String.class, restParams);
		System.out.println("Vacancy response: " + vacancyResponse);
		
		Document doc = Jsoup.parse(vacancyResponse);
		
		Element positionElement = doc.select("h1[class=vacancy__name]").first();
		position = positionElement.text().trim();

		Element companyElement = doc.select("div[class=navigate]").first();

		if (companyElement != null) {
			String html = companyElement.html();
			int index = html.indexOf("<div");
			
			company = (index == -1) ? html.trim() : html.substring(0, index).trim();
		}
		
		Element salaryDiv = doc.select("div[class=vacancy__salary").first();
		Element salarySpan = salaryDiv.select("span[data-qa=vacancy-salary").first();
			
		salary = (salarySpan == null ? "n/a" : salarySpan.text().trim()); 
		
	
		System.out.println("Position = " + position
				+ ", company = " + company
				+ ", salary = " + salary);
	}

}
