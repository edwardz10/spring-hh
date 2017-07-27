package entities;

public class Vacancy {
	private String position;
	private String company;
	private String salary;
	private String keywords;

	public Vacancy() {}
	
	public Vacancy(String position, String company, String salary, String keywords) {
		this.position = position;
		this.company = company;
		this.salary = salary;
		this.keywords = keywords;
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
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
