package hello.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import entities.SearchParameters;
import entities.Vacancy;
import hello.services.VacanciesSearchService;

@Controller
public class VacancyController {

	private static final Logger LOG = LoggerFactory.getLogger(VacancyController.class);

	private final VacanciesSearchService searchService;
	
	@Autowired
	public VacancyController(VacanciesSearchService searchService) {
		this.searchService = searchService;
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
        List<Vacancy> vacancies = searchService.findVacancies(searchParams);
        
        LOG.info("Found " + vacancies.size() + " vacancies");
        
        model.addAttribute("vacancies", vacancies);
        return "results";
    }
	
}
