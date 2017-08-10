package hello.entities;

public class SearchParameters {
	
	private String keyword;
	private String salary;
	private Integer keywordLimit;
	private Integer itemsOnPage = 100;
	
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
	public Integer getItemsOnPage() {
		if (itemsOnPage == null) {
			itemsOnPage = 100;
		}
		
		return itemsOnPage;
	}
	public void setItemsOnPage(Integer itemsOnPage) {
		this.itemsOnPage = itemsOnPage;
	}

}
