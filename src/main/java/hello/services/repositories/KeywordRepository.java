package hello.services.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hello.entities.Keyword;

public interface KeywordRepository extends CrudRepository<Keyword, String> {

	@Query("select k from keywords k order by 2 desc")
	public List<Keyword> getSortedKeywords();
}
