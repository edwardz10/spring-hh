package hello.services;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import hello.entities.Vacancy;

class VacanciesFeeder implements Runnable {

		static final Logger LOG = LoggerFactory.getLogger(VacanciesFeeder.class);

		private final VacanciesSearchService vacanciesSearchService;
        private final StatisticsService statisticsService;
		private final LinkedList<Long> vacancyIds;
		private final List<Vacancy> vacancies;

		private RestTemplate restT;
		private HttpHeaders hhhh;
		private Map<String, String> restP;
		
		public VacanciesFeeder(VacanciesSearchService vacanciesSearchService,
		                       StatisticsService statisticsService,
							   LinkedList<Long> vacancyIds, 
							   List<Vacancy> vacancies) {
			this.vacanciesSearchService = vacanciesSearchService;
			this.statisticsService = statisticsService;
			this.vacancyIds = vacancyIds;
			this.vacancies = vacancies;

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

			synchronized (vacancyIds) {
    			while (!vacancyIds.isEmpty()) {
    				
   					VacanciesSearchService.LOG.info(Thread.currentThread() + " " + vacancyIds.size() + " vacancies in the queue...");
   					vacancyId = vacancyIds.pop();
    
    				url = this.vacanciesSearchService.urlBuilder.getVacancyUrl(vacancyId);
    				VacanciesSearchService.LOG.info(Thread.currentThread() + " Fetch vacancy with id=" + vacancyId + " from a REST request...");
    				
    				vacancyResponse = restT.getForObject(url, String.class, restP);
    				v = getVacancy(vacancyId, url, vacancyResponse);
    
    				if (v != null) {
    					vacancies.add(v);
    	
    					this.vacanciesSearchService.vacanciesRepository.save(v);
    				}
    			}
			}

			LOG.info(Thread.currentThread() + " No more vacancies in the queue.. exit");
		}

		protected Vacancy getVacancy(Long vacancyId, String url, String vacancyHtml) {
			Vacancy v = null;
			Document doc = Jsoup.parse(vacancyHtml);
			Set<String> keywordSet = getKeywords(doc);

			if (keywordSet.isEmpty()) {
				keywordSet = this.vacanciesSearchService.analizerService.getKeywordsHypothetical(doc);
			}

			statisticsService.registerAllKeywords(keywordSet);
			
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

				VacanciesSearchService.LOG.info(Thread.currentThread() + " Found vacancy: " + v);
			} else {
				VacanciesSearchService.LOG.info(Thread.currentThread() + " vacancy " + url + " is invalid:"
						+ " position = " + position
						+ " company = " + company
						+ " salary = " + salary);
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
				VacanciesSearchService.LOG.warn("Key skills are not present on the page!");
			}
			
			return keywordsSet;
		}

		protected String getPosition(Document vacancyDoc) {
			Element positionElement = vacancyDoc.select("h1[class=vacancy__name]").first();
			return positionElement.text().trim();
		}

		protected String getCompany(Document vacancyDoc) {
			String company = null;
			Element companyElement = vacancyDoc.select("div[class=navigate]").first();

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
					parsedSalary = new Long(Math.round(parsedSalary * this.vacanciesSearchService.currencyService.getUsdExchange()));
				} else if (salary.endsWith("EUR")) {
					parsedSalary = new Long(Math.round(parsedSalary * this.vacanciesSearchService.currencyService.getEuroExchange()));
				}
			}

			return parsedSalary;
		}	
	}