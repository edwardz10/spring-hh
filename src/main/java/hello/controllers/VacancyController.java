package hello.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import hello.entities.Keyword;
import hello.entities.SearchParameters;
import hello.entities.Vacancy;
import hello.services.StaticUtils;
import hello.services.StatisticsService;
import hello.services.CurrencyService;
import hello.services.VacanciesSearchService;

@Controller
public class VacancyController {

	private static final Logger LOG = LoggerFactory.getLogger(VacancyController.class);

	private final VacanciesSearchService searchService;
	private final StatisticsService statisticsService;
	private final CurrencyService utilService;
	
	@Autowired
	public VacancyController(VacanciesSearchService searchService, StatisticsService statisticsService,
			CurrencyService utilService) {
		this.searchService = searchService;
		this.statisticsService = statisticsService;
		this.utilService = utilService;
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
    public String searchForm(Model model) {
        model.addAttribute("search", new SearchParameters());
        LOG.info("seachForm");
        return "search";
    }

    @RequestMapping(value="/search", method=RequestMethod.POST)
    public String searchSubmit(@ModelAttribute SearchParameters searchParams, Model model) {
        LOG.info("search params: keyword=" + searchParams.getKeyword() + ", salary=" + searchParams.getSalary());
        searchService.addOmittedKeyword(searchParams.getKeyword());
        List<Vacancy> vacancies = searchService.findVacancies(searchParams);

        Collections.sort(vacancies, new Comparator<Vacancy>() {

			@Override
			public int compare(Vacancy o1, Vacancy o2) {
				return o2.getMediumSalary().compareTo(o1.getMediumSalary());
			}
		});
        
        LOG.info("Found " + vacancies.size() + " vacancies");

        List<Keyword> topKeywords = statisticsService.getTopKeywords(searchParams.getKeyword(), searchParams.getKeywordLimit());
        LOG.info("top keywords: " + topKeywords);
        
        model.addAttribute("vacancies", vacancies)
        	.addAttribute("topKeywords", StaticUtils.keywordsToString(topKeywords));

        return "results";
    }
	
}
