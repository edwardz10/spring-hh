package hello.services;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import hello.entities.Vacancy;

@Service
public class ParserService {

	private static final Logger LOG = LoggerFactory.getLogger(ParserService.class);

	private final KeywordsAnalizerService analizerService;
	private final UtilService utilService;
	private final StatisticsService statisticsService;

	@Autowired
	public ParserService(KeywordsAnalizerService analizerService, UtilService utilService,
			StatisticsService statisticsService) {
		this.analizerService = analizerService;
		this.utilService = utilService;
		this.statisticsService = statisticsService;
	}
	
	public Set<Long> getVacancyIds(String vacanciesHtml) {
		Set<Long> vacancyIds = new LinkedHashSet<>();
		Document doc = Jsoup.parse(vacanciesHtml);
		Elements links = doc.select("a");
		
		for (Element link : links) {
			String href = link.attr("href");
			
			if (href != null && href.contains("vacancy_id=")) {
				vacancyIds.add(getVacancyId(href));	
			}

		}
		
		return vacancyIds;
	}

	public Vacancy getVacancy(Long vacancyId, String url, String vacancyHtml) {
		Document doc = Jsoup.parse(vacancyHtml);
		Set<String> keywordSet = getKeywords(doc);

		if (keywordSet.isEmpty()) {
			keywordSet = analizerService.getKeywordsHypothetical(doc);
		}

		registerKeywords(vacancyId, keywordSet);
		
		String position = getPosition(doc);
		String company = getCompany(doc);
		String salary = getSalary(doc);

		Vacancy v = new Vacancy(vacancyId, 
								url, 
								position, 
								company, 
//								salary, 
								utilService.parseSalary(salary), 
//								keywordSet, 
								utilService.stringSetToString(keywordSet));

//		v.setSalary(salary);
//		v.setKeywordSet(keywordSet);
		
		LOG.info("Found vacancy: " + v);
		
		return v;
	}

	protected long getVacancyId(String href) {
		return Long.valueOf(href.split("=")[1]);
	}

	protected Set<String> getKeywords(Document vacancyDoc) {
		Set<String> keywordsSet = new HashSet<>();

		Elements keySkillsSpans = vacancyDoc.select("span[class=keyskill]");

		if (!keySkillsSpans.isEmpty()) {
			for (Element keySkillSpan : keySkillsSpans) {
				keywordsSet.add(keySkillSpan.text());
			}
		} else {
			LOG.warn("Key skills are not present on the page!");
		}
		
		return keywordsSet;
	}

	protected String getPosition(Document vacancyDoc) {
		Element positionElement = vacancyDoc.select("h1[class=vacancy__name]").first();
		return positionElement.text().trim();
	}

	protected String getCompany(Document vacancyDoc) {
		Element companyElement = vacancyDoc.select("div[class=navigate navigate_nopadding]").first();
		String html = companyElement.html();
		int index = html.indexOf("<div");
		
		return (index == -1) ? html.trim() : html.substring(0, index).trim();
	}

	protected String getSalary(Document vacancyDoc) {
		Element salaryDiv = vacancyDoc.select("div[class=vacancy__salary").first();
		Element salarySpan = salaryDiv.select("span[data-qa=vacancy-salary").first();
		
		return salarySpan == null ? "n/a" : salarySpan.text().trim(); 
	}

	protected void registerKeywords(Long vacancyId, Set<String> keywordSet) {
		for (String k : keywordSet) {
			statisticsService.register(vacancyId, k);
		}
	}
	
	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> restParams = new LinkedHashMap<String, String>();
		String url = "https://spb.hh.ru/search/vacancy?text=Java&area=2&salary=200000&currency_code=RUR&experience=doesNotMatter&order_by=relevance&search_period=&items_on_page=200&no_magic=true";
//		String url = "https://spb.hh.ru/vacancy/22284505?query=Java";
			
		String vacancyResponse = restTemplate.getForObject(url, String.class, restParams);
//		System.out.println(vacancyResponse);

		Document doc = Jsoup.parse(vacancyResponse);
//		Element salaryDiv = doc.select("div[class=vacancy__salary").first();

//		System.out.println("Parent element: " + salarySpan.parent());
//		Element baseSalary = doc.select("meta[itemprop=salaryCurrency]").first();
//		Element salaryCurrency = doc.select("meta[itemprop=baseSalary]").first();

//		System.out.println(vacancyResponse);
		System.out.println(doc.html());

	}
}
