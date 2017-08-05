package hello.entities;

public class SearchParameters {
	
	private String keyword;
	private String salary;
	private Integer keywordLimit;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public Integer getKeywordLimit() {
		return keywordLimit;
	}
	public void setKeywordLimit(Integer keywordLimit) {
		this.keywordLimit = keywordLimit;
	}

}
