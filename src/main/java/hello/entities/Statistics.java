package hello.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Statistics implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="vacancy_id")
	private Long vacancyId;
	@Column(name="keyword")
	private String keyword;

	protected Statistics() {}

	public Statistics(Long vacancyId, String keyword) {
		this();
		this.vacancyId = vacancyId;
		this.keyword = keyword;
	}

	public Long getVacancyId() {
		return vacancyId;
	}
	public void setVacancyId(Long vacancyId) {
		this.vacancyId = vacancyId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
