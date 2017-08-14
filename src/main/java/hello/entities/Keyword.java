package hello.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="keywords")
public class Keyword implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id 
	@Column(name="keyword")
	private String keyword;

	@Column(name="count")
	private Long count;

	protected Keyword() {}

	public Keyword(String keyword) {
		this.keyword = keyword;
		this.count = 1L;
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
		return "keyword=" + keyword
				+ ", count=" + count;
	}
}

