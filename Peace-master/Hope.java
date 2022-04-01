package src;


import eu.verdelhan.ta4j.Rule;

public class Hope {
	
	protected double appreciation;
	protected int nTrades;
	protected int totalTrades;
	protected String symbol;
	protected Rule goodRule;
	protected String ruleAsString; 
	
 public Hope(Rule r, String rule, String n, int trades, int total, double a){
	 
	 ruleAsString = rule;
	 appreciation = a;
	 nTrades = trades;
	 symbol = n;
	 goodRule = r;
	 totalTrades = total;
	 
 }
 
 

}
