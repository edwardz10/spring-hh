package hello.services;

import org.springframework.data.repository.CrudRepository;

import hello.entities.Statistics;

public interface StatisticsRepository extends CrudRepository<Statistics, Long> {
}
