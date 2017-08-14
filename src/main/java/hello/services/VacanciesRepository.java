package hello.services;

import org.springframework.data.repository.CrudRepository;

import hello.entities.Vacancy;

public interface VacanciesRepository extends CrudRepository<Vacancy, Long> {

}
