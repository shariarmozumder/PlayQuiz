package com.sell.arkaysell.bean;

public class SingleAnswareLevelInfo {
	private int lelveno;
	private String levelname;
	
	
	public SingleAnswareLevelInfo(int lelveno, String levelname) {
		this.lelveno = lelveno;
		this.levelname = levelname;
	}
	
	public int getLevelNo() {
		return lelveno;
	}
	public void setLevelNo(int lelveno) {
		this.lelveno = lelveno;
	}
	public String getLevelName() {
		return levelname;
	}
	public void setLevelName(String levelname) {
		this.levelname = levelname;
	}
	
	
	
}
