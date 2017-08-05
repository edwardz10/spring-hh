package hello.entities;

import java.util.Set;

public class Vacancy {
	private Long id;
	private String url;
	private String position;
	private String company;
	private String salary;
	private Long mediumSalary;
	private Set<String> keywordSet;
	private String keywords;

	public Vacancy() {}
	
	public Vacancy(Long id, String url, String position, String company, String salary, Long mediumSalary, Set<String> keywordSet, String keywords) {
		this.id = id;
		this.url = url;
		this.position = position;
		this.company = company;
		this.salary = salary;
		this.mediumSalary = mediumSalary;
		this.keywordSet = keywordSet;
		this.keywords = keywords;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}

	public Long getMediumSalary() {
		return mediumSalary;
	}

	public void setMediumSalary(Long mediumSalary) {
		this.mediumSalary = mediumSalary;
	}

	public Set<String> getKeywordSet() {
		return keywordSet;
	}

	public void setKeywordSet(Set<String> keywordSet) {
		this.keywordSet = keywordSet;
	}

	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Override
	public String toString() {
		return "id=" + id
				+ ", position=" + position
				+ ", company=" + company
				+ ", salary=" + salary
				+ ", mediumSalary=" + mediumSalary
				+ ", keywords=" + keywords;
	}
}
