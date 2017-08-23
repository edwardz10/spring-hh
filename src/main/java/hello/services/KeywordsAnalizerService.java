package hello.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KeywordsAnalizerService {

	private static final Logger LOG = LoggerFactory.getLogger(KeywordsAnalizerService.class);

	private Map<String, Integer> RESERVED_KEYWORDS_MAP;
	
	private final String[] RESERVED_KEYWORDS = {
			"Java", "Javascript", "Spring", "Hybernate", "Android", "Python",
			"Perl", "google guice", "Ruby", "Haskell", "TeamCity", "Github", "git", "Git",
			"Scala", "Eclipse", "Groovy", "Kotlin", "Tomcat", "NoSQL", "No-SQL",
			"Spring", "Hibernate", "Kafka", "Hadoop", "Cassandra", "Linux", "SOAP", "REST",
			"Docker", "CSS", "HTML", "MongoDB"
	}; 

	@PostConstruct
	public void initialize() {
		RESERVED_KEYWORDS_MAP = new HashMap<>();

		for (String k : RESERVED_KEYWORDS) {
			RESERVED_KEYWORDS_MAP.put(k, 0);
		}
	}
	
	public synchronized Set<String> getKeywordsHypothetical(Document doc) {
		Set<String> keywordSet = getKeywordsFromDiv(doc, "div[class=vacancy__description usergenerate]");

		keywordSet.addAll(getKeywordsFromDiv(doc, "div[class=b-vacancy-desc-wrapper]"));
		
		return keywordSet;
	}

	protected Set<String> getKeywordsFromDiv(Document doc, String divClass) {
		Element div = doc.select(divClass).first();
		return (div != null) ? getKeywordsFromElement(div) : new HashSet<String>();
	}
	
	protected Set<String> getKeywordsFromElement(Element element) {
		String[] words = null;
		Set<String> keywordsSet = new HashSet<>();

		Elements liElements = element.select("li");

		for (Element liElement : liElements) {
			Element pElement = liElement.select("p").first();

			if (pElement != null) {
				words = pElement.html().split(" ");
			} else {
				words = liElement.html().split(" ");
			}

			for (String word : words) {
				if (RESERVED_KEYWORDS_MAP.get(word) != null
					|| (word.matches("[a-zA-Z]+") 
						&& word.toUpperCase().equals(word)
						&& word.length() > 1)) {
					keywordsSet.add(word);
				}
			}
			
		}
		
		return keywordsSet;
	}

}
