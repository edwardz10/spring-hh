package hello.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hello.entities.Keyword;
import hello.services.repositories.KeywordRepository;

@Service
public class StatisticsService {

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsService.class);

	private final KeywordRepository keywordRepository;
	
	@Autowired
	public StatisticsService(KeywordRepository keywordRepository) {
		this.keywordRepository = keywordRepository;
	}
	
	public void register(Long vacancyId, String keyword) {
		try {
			Keyword k = keywordRepository.findOne(keyword);

			if (k == null) {
				k = new Keyword(keyword);
				keywordRepository.save(k);
			} else {
				k.increment();
				keywordRepository.save(k);
			}
			
		} catch (Exception e) {
			LOG.warn("Failed to add keyword '" + keyword + " to statistics: " + e, e);
		}
	}

	public List<Keyword> getTopKeywords(int limit) {
		return keywordRepository.getSortedKeywords().subList(0, limit);
	}
}
