package hello.services.repositories;

import org.springframework.data.repository.CrudRepository;

import hello.entities.Vacancy;

public interface VacanciesRepository extends CrudRepository<Vacancy, Long> {

}
