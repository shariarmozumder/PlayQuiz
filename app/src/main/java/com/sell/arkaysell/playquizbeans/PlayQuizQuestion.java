package com.sell.arkaysell.playquizbeans;

import java.util.ArrayList;

/**
 * This class is for question that is question and it's option with it't true answar.
 * @author Arkay Apps
 *
 */
public class PlayQuizQuestion {
	private String question;
	private String optiona;
	private String optionb;
	private String optionc;
	private String optiond;

	private ArrayList<String> options = new ArrayList<String>();
	private String rightans;
	
	public PlayQuizQuestion(String question) {
		super();
		this.question = question;
	}


	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOptiona() {
		return optiona;
	}

	public void setOptiona(String optiona) {
		this.optiona = optiona;
	}

	public String getOptionb() {
		return optionb;
	}

	public void setOptionb(String optionb) {
		this.optionb = optionb;
	}

	public String getOptionc() {
		return optionc;
	}

	public void setOptionc(String optionc) {
		this.optionc = optionc;
	}

	public String getOptiond() {
		return optiond;
	}

	public void setOptiond(String optiond) {
		this.optiond = optiond;
	}

	public String getRightans() {
		return rightans;
	}

	public void setRightans(String rightans) {
		this.rightans = rightans;
	}

	public boolean addOption(String option){
		return this.options.add(option);
	}


	public ArrayList<String> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	
	public String getTrueAns() {
		return rightans;
	}

	public void setTrueAns(String rightans) {
		this.rightans = rightans;
	}

	@Override
	public String toString() {
		return "Question: "+ question +" OptionS: "+ rightans.toString();
	}
	
		
}
