package hello.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import hello.entities.Keyword;
import hello.entities.SearchParameters;
import hello.entities.Vacancy;
import hello.services.StaticUtils;
import hello.services.StatisticsService;
import hello.services.VacanciesSearchService;

@RestController
@RequestMapping("/api/vacancies")
public class VacancyController {

	private static final Logger LOG = LoggerFactory.getLogger(VacancyController.class);

	@Autowired
	private VacanciesSearchService searchService;
	@Autowired
	private StatisticsService statisticsService;

	private SearchParameters searchParameters;

	@RequestMapping(method = GET)
    public ResponseEntity<List<Vacancy>> getVacancies() {
        return new ResponseEntity(searchService.getVacancies(), HttpStatus.OK);
    }

    @RequestMapping(value="/keywords", method = GET)
    public ResponseEntity<String> getKeywords() {
        List<Keyword> keywords = statisticsService.getTopKeywords(searchParameters.getKeyword(), searchParameters.getKeywordLimit());
        return new ResponseEntity(StaticUtils.keywordsToString(keywords), HttpStatus.OK);
    }
    
    @RequestMapping(value="/start", method=RequestMethod.POST)
    public ResponseEntity<?> startFeed(@RequestBody SearchParameters searchParameters) {
        LOG.info("Search params: keyword=" + searchParameters.getKeyword() + ", salary=" + searchParameters.getSalary());
        this.searchParameters = searchParameters;
        searchService.startFeed(searchParameters);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/stop", method=RequestMethod.POST)
    public ResponseEntity<?> stopFeedAndReset() {
        LOG.info("Stop vacancies feed and reset");
        searchService.stop();
        statisticsService.clear();
        return new ResponseEntity(HttpStatus.OK);
    }    
//    @RequestMapping(value="/search", method=RequestMethod.POST)
//    public String searchSubmit(@ModelAttribute SearchParameters searchParams, Model model) {
//        LOG.info("search params: keyword=" + searchParams.getKeyword() + ", salary=" + searchParams.getSalary());
//        searchService.addOmittedKeyword(searchParams.getKeyword());
//        List<Vacancy> vacancies = searchService.findVacancies(searchParams);
//
//        vacancies.sort((o1, o2) -> o2.getMediumSalary().compareTo(o1.getMediumSalary()));
//        
//        LOG.info("Found " + vacancies.size() + " vacancies");
//
//        List<Keyword> topKeywords = statisticsService.getTopKeywords(searchParams.getKeyword(), searchParams.getKeywordLimit());
//        LOG.info("top keywords: " + topKeywords);
//        
//        model.addAttribute("vacancies", vacancies)
//        	.addAttribute("topKeywords", StaticUtils.keywordsToString(topKeywords));
//
//        return "results";
//    }
	
}
