package hello.services.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import hello.entities.Keyword;

public interface KeywordRepository extends CrudRepository<Keyword, String> {

	@Query("select k from keywords k where k.searchKey=:searchKey and k.keyword=:keyword")
	public Keyword getKeyword(@Param("searchKey") String searchKey, 
							  @Param("keyword") String keyword);
	
	@Query("select k from keywords k where k.searchKey=:searchKey order by 2 desc")
	public List<Keyword> getSortedKeywords(@Param("searchKey") String searchKey);
}
