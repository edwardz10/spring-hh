package hello.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="vacancies")
public class Vacancy implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	private Long id;

	@Column(name="url")
	private String url;

	@Column(name="position")
	private String position;

	@Column(name="company")
	private String company;

	@Column(name="medium_salary")
	private Long mediumSalary;

	@Column(name="keywords")
	private String keywords;

	public Vacancy() {}
	
	public Vacancy(Long id, String url, String position, String company, Long mediumSalary, String keywords) {
		this.id = id;
		this.url = url;
		this.position = position;
		this.company = company;
		this.mediumSalary = mediumSalary;
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

	public Long getMediumSalary() {
		return mediumSalary;
	}

	public void setMediumSalary(Long mediumSalary) {
		this.mediumSalary = mediumSalary;
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
				+ ", salary=" + mediumSalary
				+ ", mediumSalary=" + mediumSalary
				+ ", keywords=" + keywords;
	}
}
