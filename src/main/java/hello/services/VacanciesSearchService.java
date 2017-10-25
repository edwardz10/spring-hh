package hello.services;

import java.util.HashSet;
//import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

	private static final Logger LOG = LoggerFactory.getLogger(VacanciesSearchService.class);

	/**
	 * Services
	 */
	private final UrlBuilderService urlBuilder;
	private final VacanciesRepository vacanciesRepository;
	private final KeywordsAnalizerService analizerService;
	private final StatisticsService statisticsService;
	private final CurrencyService currencyService;

	private RestTemplate restTemplate;
	private HttpHeaders headers;
	private Map<String, String> restParams;

	private List<String> omittedKeywords;

	private int availableProcessors;
	
	private ExecutorService executorService;
	private CountDownLatch latch;
	
	private class VacanciesFeeder implements Runnable {

		private final LinkedList<Long> vacancyIds;
		private final LinkedList<Vacancy> vacancies;
		private final SearchParameters searchParams;
		private final CountDownLatch latch;

		private RestTemplate restT;
		private HttpHeaders hhhh;
		private Map<String, String> restP;
		
		public VacanciesFeeder(LinkedList<Long> vacancyIds, LinkedList<Vacancy> vacancies, 
				SearchParameters searchParams, CountDownLatch latch) {
			this.vacancyIds = vacancyIds;
			this.vacancies = vacancies;
			this.searchParams = searchParams;
			this.latch = latch;

			restT = new RestTemplate();
			hhhh = new HttpHeaders();
			hhhh.setContentType(MediaType.APPLICATION_JSON);
			restP = new LinkedHashMap<>();
		}
		
		@Override
		public void run() {
			Long vacancyId;
			String url, vacancyResponse;
			Vacancy v;

			while (!vacancyIds.isEmpty()) {
				
				synchronized (vacancyIds) {
					LOG.info(Thread.currentThread() + " " + vacancyIds.size() + " vacancies in the queue...");
					vacancyId = vacancyIds.pop();
				}

				url = urlBuilder.getVacancyUrl(vacancyId);
				LOG.info(Thread.currentThread() + " Fetch vacancy with id=" + vacancyId + " from a REST request...");
				
				vacancyResponse = restT.getForObject(url, String.class, restP);
				v = getVacancy(vacancyId, url, vacancyResponse, searchParams.getKeyword());

				if (v != null) {
					synchronized (vacancies) {
						vacancies.push(v);
					}
	
					vacanciesRepository.save(v);
				}
			}

			latch.countDown();
			LOG.info("DONE " + this.latch);
//			LOG.info(Thread.currentThread() + " No more vacancies in the queue.. exit");
		}

		protected Vacancy getVacancy(Long vacancyId, String url, String vacancyHtml, String searchKey) {
			Vacancy v = null;
			Document doc = Jsoup.parse(vacancyHtml);
			Set<String> keywordSet = getKeywords(doc);

			if (keywordSet.isEmpty()) {
				keywordSet = analizerService.getKeywordsHypothetical(doc);
			}

			registerAllKeywords(keywordSet);
			
			String position = getPosition(doc);
			String company = getCompany(doc);
			String salary = getSalary(doc);

			if (position != null
				&& company != null
				&& salary != null
				) {
				v = new Vacancy(vacancyId, 
						url, 
						position, 
						company, 
						parseSalary(salary), 
						StaticUtils.stringSetToString(keywordSet));

				LOG.info(Thread.currentThread() + " Found vacancy: " + v);
			}
			
			return v;
		}		

		protected Set<String> getKeywords(Document vacancyDoc) {
			Set<String> keywordsSet = new HashSet<>();
			Elements keySkillsSpans = vacancyDoc.select("span[class=keyskill]");

			if (!keySkillsSpans.isEmpty()) {
				keywordsSet = keySkillsSpans.stream()
								.map(Element::text)
								.collect(Collectors.toSet());
			} else {
				LOG.warn("Key skills are not present on the page!");
			}
			
			return keywordsSet;
		}

		protected void registerAllKeywords(Set<String> keywordSet) {
			keywordSet.forEach(i -> keywordSet.forEach(j -> statisticsService.register(i, j)));
		}

		protected String getPosition(Document vacancyDoc) {
			Element positionElement = vacancyDoc.select("h1[class=vacancy__name]").first();
			return positionElement.text().trim();
		}

		protected String getCompany(Document vacancyDoc) {
			String company = null;
			Element companyElement = vacancyDoc.select("div[class=navigate navigate_nopadding]").first();

			if (companyElement != null) {
				String html = companyElement.html();
				int index = html.indexOf("<div");
				
				company = (index == -1) ? html.trim() : html.substring(0, index).trim();
			}

			return company;
		}

		protected String getSalary(Document vacancyDoc) {
			Element salaryDiv = vacancyDoc.select("div[class=vacancy__salary").first();
			Element salarySpan = salaryDiv.select("span[data-qa=vacancy-salary").first();
			
			return salarySpan == null ? "n/a" : salarySpan.text().trim(); 
		}

		protected Long parseSalary(String salary) {
			Long parsedSalary = 0L;

			if (!salary.equals("n/a")) {
				if (StaticUtils.containsMinMax(salary)) {
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
						parsedSalary = StaticUtils.getAverage(StaticUtils.toLong(str1), StaticUtils.toLong(str2));
					}
				} else {
					parsedSalary = StaticUtils.toLong(salary);
				}

				if (salary.endsWith("USD")
						|| salary.endsWith("$")) {
					parsedSalary = new Long(Math.round(parsedSalary * currencyService.getUsdExchange()));
				} else if (salary.endsWith("EUR")) {
					parsedSalary = new Long(Math.round(parsedSalary * currencyService.getEuroExchange()));
				}
			}

			return parsedSalary;
		}	
	}
	
	@Autowired
	public VacanciesSearchService(UrlBuilderService urlBuilder,
			VacanciesRepository vacanciesRepository, KeywordsAnalizerService analizerService,
			StatisticsService statisticsService, CurrencyService utilService) {
		this.urlBuilder = urlBuilder;
		this.vacanciesRepository = vacanciesRepository;
		this.analizerService = analizerService;
		this.statisticsService = statisticsService;
		this.currencyService = utilService;
	}

	@PostConstruct
	public void initialize() {
		restTemplate = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restParams = new LinkedHashMap<>();
		omittedKeywords = new LinkedList<>();

		availableProcessors = Runtime.getRuntime().availableProcessors();
		executorService = Executors.newFixedThreadPool(availableProcessors);
		latch = new CountDownLatch(availableProcessors);

		LOG.info("Available processors: " + availableProcessors);
	}
	
	public List<Vacancy> findVacancies(SearchParameters searchParams) {
		LinkedList<Vacancy> vacancies = new LinkedList<>();
		
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
				executorService.execute(new VacanciesFeeder(vacanciesFromHH, vacancies, searchParams, latch));
			}

			try {
			    latch.await();
			} catch (InterruptedException e) {
				LOG.error("Failed to await for the countdown latch: " + e, e);
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

}
