package hello.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="keywords")
public class Keyword implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Column(name="searchkey")
	private String searchKey;

	@Column(name="keyword")
	private String keyword;

	@Column(name="count")
	private Long count;

	protected Keyword() {}

	public Keyword(String searchKey, String keyword) {
		this.searchKey = searchKey;
		this.keyword = keyword;
		this.count = 1L;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public void increment() {
		this.count++;
	}

	@Override
	public String toString() {
		return "searchKey=" + searchKey
				+ ", keyword=" + keyword
				+ ", count=" + count;
	}
}

