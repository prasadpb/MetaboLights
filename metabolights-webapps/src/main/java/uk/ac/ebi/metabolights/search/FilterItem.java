package uk.ac.ebi.metabolights.search;

import java.io.Serializable;

public class FilterItem {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	
	private String text;
	private String name;
	private String value;
	private int number = 0;
	private boolean isChecked;
	public FilterItem(String text, String name){
		this.text = text;
		this.name = name;
		this.value = text;
	}
	public String getText(){
		return this.text;
	}
	public String getName(){
		return this.name;
	}
	public boolean getIsChecked(){
		return isChecked;
	}
	public void setIsChecked(boolean isChecked){
		this.isChecked = isChecked;
	}
	public String getValue(){
		return this.value;
	}
	public void setValue(String value){
		this.value = value;
	}
	public int getNumber(){
		return number;
	}
	public void addToNumber(int value){
		number= number + value;
	}
	public void reset(){
		this.isChecked = false;
		this.number= 0;
	}
}
