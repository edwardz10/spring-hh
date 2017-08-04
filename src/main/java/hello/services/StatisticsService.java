package hello.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hello.entities.Statistics;

@Service
public class StatisticsService {

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsService.class);

	private final StatisticsRepository statisticsRepository;
	private Map<String, Integer> keywords; 
	
	@Autowired
	public StatisticsService(StatisticsRepository statisticsRepository) {
		this.statisticsRepository = statisticsRepository;
	}
	
	@PostConstruct
	public void initialize() {
		keywords = new LinkedHashMap<>();

		statisticsRepository.deleteAll();
	}

	public void register(Long vacancyId, String keyword) {
		try {
			statisticsRepository.save(new Statistics(vacancyId, keyword));

			Integer count = keywords.get(keyword);

			if (count == null) {
				keywords.put(keyword, 1);
			} else {
				keywords.put(keyword, ++count);
			}
		} catch (Exception e) {
			LOG.warn("Failed to add keyword '" + keyword + " to statistics ");
		}
	}

	protected void sortKeywordsByCount() {
		List<Map.Entry<String, Integer>> list =
	            new LinkedList<Map.Entry<String, Integer>>(keywords.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
		});

		keywords = new LinkedHashMap<String, Integer>();

		for (Map.Entry<String, Integer> entry : list) {
			keywords.put( entry.getKey(), entry.getValue() );
		}
	}

	public Map<String, Integer> getTopKeywords(int limit) {
		sortKeywordsByCount();

		Map<String, Integer> topKeywords = new LinkedHashMap<String, Integer>();
		int i = 0;

		for (Map.Entry<String, Integer> entry : keywords.entrySet()) {
			if (i++ < limit) {
				topKeywords.put(entry.getKey(), entry.getValue());
			} else {
				break;
			}
		}
		
		return topKeywords;
	}
}
