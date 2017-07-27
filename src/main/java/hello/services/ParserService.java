package hello.services;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import entities.Vacancy;

@Service
public class ParserService {

	private static final Logger LOG = LoggerFactory.getLogger(ParserService.class);

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

	public Vacancy getVacancy(String vacancyHtml) {
		Document doc = Jsoup.parse(vacancyHtml);
		String keywords = getKeywords(doc);
		String position = getPosition(doc);
		String company = getCompany(doc);
		String salary = getSalary(doc);

		Vacancy v = new Vacancy(position, company, salary, keywords);
		
		return v;
	}

	protected long getVacancyId(String href) {
		return Long.valueOf(href.split("=")[1]);
	}

	protected String getKeywords(Document vacancyDoc) {
		StringBuilder keywords = new StringBuilder();
		boolean first = true;

		Elements keySkillsSpans = vacancyDoc.select("span[class=keyskill]");

		for (Element keySkillSpan : keySkillsSpans) {
			if (!first) {
				keywords.append(", ");
			} else {
				first = false;
			}

			keywords.append(keySkillSpan.text());
		}

		return keywords.toString().trim();
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
