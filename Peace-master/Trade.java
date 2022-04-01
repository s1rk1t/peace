package src;


import java.util.Vector;
import eu.verdelhan.ta4j.Rule;



public class Trade{
	
	protected double appreciation;
	protected double depreciation;
	
	protected int daysHeld;
	
	protected int daysUntilPut;
	protected int daysUntilCall;
	
	protected int nTrades;
	
	protected int totalTrades;
	
	protected String buySym;
	protected String sellSym;
	
	protected Rule buyRule;
	protected Rule sellRule;
	
	protected String putSym;
	protected String callSym;
	
	protected Rule putRule;
	protected Rule callRule;
	
	protected int putLimit;
	protected int callLimit;
	
	protected int holdLimit;
	
	protected int buyLimit;
	protected int sellLimit;
	
	protected int buyShares;
	protected int sellShares;
	
	protected int holdShares;
	
	protected int putShares;
	protected int callShares;
	
	protected Vector<Double> hedgeBTolerance = new Vector<Double>();//scale this to various fibonacci levels
	protected Vector<Double> hedgeSTolerance = new Vector<Double>();//measures the amount of capital willing to be lost or gained before hedging 	
	
	protected String putDate;
	protected String callDate;
	
	protected String buyDate;//may not need if done in reactive fashion
	protected String sellDate;//may not need if done in reactive fashion
	
	protected String buySymbol;
	protected String sellSymbol;
	protected String holdSymbol;
	
	protected Vector<Rule> bestB = new Vector<Rule>();
	protected Vector<Rule> bestS = new Vector<Rule>();
	
	protected Vector<Rule> bestCalls = new Vector<Rule>();
	protected Vector<Rule> bestPuts = new Vector<Rule>();
	
	
	
	protected Vector<String> lastGapUpDates;
	protected Vector<String> lastGapDownDates;//these containers have to be synchronous with the following four
    
	protected Vector<Double> sizeOfGapsUp;
    protected Vector<Double> sizeOfGapsDown;
    
    protected Vector<Double> fLimitB = new Vector<Double>();//fibonacci limits
	protected Vector<Double> fLimitS = new Vector<Double>();
	
	
	
 public Trade(Rule r, String n, int trades, int total, double a){
	 
	 
	 appreciation = a;
	 nTrades = trades;
	
	 totalTrades = total;
	 
 }
 
 

}
