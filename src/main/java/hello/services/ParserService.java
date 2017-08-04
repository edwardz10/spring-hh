package hello.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hello.entities.Vacancy;

@Service
public class ParserService {

	private static final Logger LOG = LoggerFactory.getLogger(ParserService.class);

	private final KeywordsAnalizerService analizerService;
	private final UtilService utilService;

	@Autowired
	public ParserService(KeywordsAnalizerService analizerService, UtilService utilService) {
		this.analizerService = analizerService;
		this.utilService = utilService;
	}
	
	public List<Long> getVacancyIds(String vacanciesHtml) {
		List<Long> vacancyIds = new ArrayList<>();
		Document doc = Jsoup.parse(vacanciesHtml);
		Elements links = doc.select("a");
		
		for (Element link : links) {
			String href = link.attr("href");
			
			if (href.contains("vacancy_id=")) {
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
		
		String position = getPosition(doc);
		String company = getCompany(doc);
		String salary = getSalary(doc);

		Vacancy v = new Vacancy(vacancyId, 
								url, 
								position, 
								company, 
								salary, 
								utilService.parseSalary(salary), 
								keywordSet, 
								utilService.stringSetToString(keywordSet));

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
		Element salarySpan = vacancyDoc.select("span[data-qa=vacancy-salary").first();

		return salarySpan == null ? "n/a" : salarySpan.text().trim(); 
	}

}
