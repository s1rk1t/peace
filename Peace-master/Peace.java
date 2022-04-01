package src;


import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Order;
import eu.verdelhan.ta4j.Rule;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.Order.OrderType;
import eu.verdelhan.ta4j.analysis.criteria.TotalProfitCriterion;
import eu.verdelhan.ta4j.trading.rules.*;
import eu.verdelhan.ta4j.trading.*;
import eu.verdelhan.ta4j.analysis.*;
import au.com.bytecode.opencsv.CSVReader;
import eu.verdelhan.ta4j.Tick;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.text.AbstractDocument.BranchElement;

import eu.verdelhan.ta4j.indicators.oscillators.*;
import eu.verdelhan.ta4j.indicators.helpers.*;
import eu.verdelhan.ta4j.indicators.simple.*;
import eu.verdelhan.ta4j.indicators.trackers.*;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.*;
import eu.verdelhan.ta4j.indicators.volume.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.joda.time.*;


public class Peace {

	protected static int foldFactor = 12;//default number of folds for cross validation

	protected static int foldLength = 250;//approx 1 year of data

	protected static int gLimit = 0;//goodness limit, a measure that enables the confidence level to have some good minimum

	protected static double totalTime = 0.0;//for benchmarking

	protected double maxFitness = 0.0;

	protected int fittestRow = 0;

	protected static double elitism = .7;//what precentage fo teh best rules are kept for later comparison 

	protected int[] eliteRows;//which rows are the most fit

	protected File f;

	protected String sourcePath;

	protected String symbol;//stock ticker

	protected static double tp = 0.0;//not toilet paper :)

	protected static ArrayList<String> bRuleStrings = new ArrayList<String>();//buy rules as Strings
	protected static ArrayList<String> sRuleStrings = new ArrayList<String>();//sell rules as Strings

	protected int goodness;//number of folds that meet fitness criteria 

	protected static TreeMap<Double, Strategy> hybrid = new TreeMap<Double, Strategy>();//fitness for a given trading strategy

	protected static Love l;//class that metes the amount of mutation

	protected static ArrayList<Rule> buyRuleSet = new ArrayList<Rule>();
	protected static ArrayList<Rule> sellRuleSet = new ArrayList<Rule>();

	protected static Vector<Rule> fitBuyRules = new Vector<Rule>();
	protected static Vector<Rule> fitSellRules = new Vector<Rule>();

	protected static ArrayList<Double> bestF = new ArrayList<Double>();

	protected Vector<Rule> bestB = new Vector<Rule>();
	protected Vector<Rule> bestS = new Vector<Rule>();

	protected static Vector<Rule> get = new Vector<Rule>();
	protected static Vector<Rule> let = new Vector<Rule>();

	protected static TreeMap<String, Rule> buyMap = new TreeMap<String, Rule>();
	protected static TreeMap<String, Rule> sellMap = new TreeMap<String, Rule>();

	//protected <Double, Integer> fitnesses = new TreeMap<Double, Integer>();

	protected long date;//timestamp from unix

	Random ran = new Random();

	//LinkedList<Genie> bestBRules = new LinkedList<Genie>();
	//LinkedList<Genie> bestSRules = new LinkedList<Genie>();


	protected int branchingFactor = 5;//ran.nextInt(3)+3;//banana

	protected int maxSector = 0;//fold with greatest profit, within the fitrness function, the closer this is to the present, the better

	protected double maxProfit = 0.0;//obv

	protected double absoluteMax = 0.0;//greatest profit from all runs from every fold

	protected double radiation = Math.sqrt(branchingFactor);//how much 'give' is there in range of mutation

	//calculated radiation is entirely stochastic up to 100% of branching factor, so as to keep logic intact for analysis

	protected double mutationRate = Math.pow(radiation, branchingFactor);//how often are mutations allowed

	protected double give = (Math.random() * mutationRate);	//multidimensionsal flux shall be given certain priorities that may appear unnecessary at present, bu the future holds the protein modeling which I think

	protected double mood = Math.sqrt(give/mutationRate);	//another dimensional change that allows of general economic mood to be considered as a reflection of certain behavioral tendencies relative to progress and/or valatility 											//will necessitate this kind of flux to be passe in terms of necessary and complete understanding

	protected int population = 200;//how many individuals in one run

	protected static String nysePaths = "nysePaths.txt";//source file paths
	protected static String nasdaqPaths = "nasdaqPaths.txt";
	protected static String otherPaths = "syms.txt";

	protected double tolerance = Math.floor(population/give); //depth of variability in mutation;i.e.: how many tiers to the decision tree?
	//this depth is the product of a random normalized translation
	//and the give, which means that the growth into more sophisticated systems
	//is in a quotient relationship with the radiation moentioned earlier
	//to make this process more simple, an integer has been used to give a more clear "fuzzy" logic that can be quantified if/when there is a definable relationship germane to the progress of knowledge 


	protected int genesAffected = (int) (tolerance*mutationRate*Math.log(radiation));//max number of genes affected by mutation

	protected double up = 0.0;//how far above 100% is the profit



	protected static int nTicks = 0;//number of trades 

	protected double minorityShare = 0;//for every fold that is not the majority profit holder, what is the average profit

	ArrayList<TimeSeries> seriesOfFolds = new ArrayList<TimeSeries>();

	protected static ArrayList<String> buyRules = new ArrayList<String>();
	protected static ArrayList<String> sellRules = new ArrayList<String>();

	protected LinkedList<Genie> buyG = new LinkedList<Genie>();
	protected LinkedList<Genie> sellG = new LinkedList<Genie>();

	protected static LinkedList<Genie> greater = new LinkedList<Genie>();
	protected static LinkedList<Genie> lesser = new LinkedList<Genie>();

	protected static ArrayList<Rule> goodBuyRules = new ArrayList<Rule>();
	protected static ArrayList<Rule> goodSellRules = new ArrayList<Rule>();

	protected static ArrayList<String> goodBuyStrings = new ArrayList<String>();
	protected static ArrayList<String> goodSellStrings = new ArrayList<String>();

	static long beginfit = 0;//first fitness

	protected static String[] genes = { //explained in README.txt

			"AverageDirectionalMovementIndicator",
			"AverageDirectionalMovementDownIndicator", 
			"AverageDirectionalMovementUpIndicator", 
			"AroonUpIndicator", "AroonDownIndicator", 
			"AccelerationDecelerationIndicator", 
			"AccumulationDistributionIndicator", 
			"AverageTrueRange", 
			"BollingerBandsLowerIndicator", 
			"BollingerBandsUpperIndicator",
			"CCIIndicator", 
			"DirectionalUpIndicator", 
			"DirectionalDownIndicator", 
			"DirectionalMovementDownIndicator",  
			"DirectionalMovementUpIndicator",
			"DirectionalMovementIndicator", 
			"OnBalanceVolumeIndicator",
			"ParabolicSarIndicator", 
			"PPOIndicator",
			"PreviousPriceIndicator",
			"PriceVariationIndicator",
			"ROCIndicator",
			"RSIIndicator",
			"StochasticOscillatorDIndicator",
			"StochasticOscillatorKIndicator",
			"TripleEMAIndicator", 
			"DoubleEMAIndicator", 
			"EMAIndicator",
			"TrueRangeIndicator", 
			"TypicalPriceIndicator",
			"WilliamsRIndicator",
			"WMAIndicator"

	};

	int nGenes = genes.length;


	protected static String[] names = {

			"AverageDirectionalMovementDownIndicator", 
			//   "AbstractIndicator", 
			"AccelerationDecelerationIndicator",//lm1
			"AccumulationDistributionIndicator",//cc1
			//   "AmountIndicator", 
			"AroonDownIndicator", 
			"AroonUpIndicator", 
			"AverageDirectionalMovementIndicator", 
			"AverageDirectionalMovementUpIndicator", 
			//   "AverageGainIndicator", 
			//   "AverageLossIndicator", 
			"AverageTrueRangeIndicator", 
			//   "AwesomeOscillatorIndicator", 
			"BollingerBandsLowerIndicator", 
			"BollingerBandsMiddleIndicator", 
			"BollingerBandsUpperIndicator", 
			//   "CachedIndicator", 
			//  "CashFlow", 
			"CCIIndicator", 
			//   "ClosePriceIndicator", 
			//   "ConstantIndicator", 
			//   "CrossIndicator", 
			//   "DifferenceIndicator", 
			"DirectionalDownIndicator", 
			"DirectionalMovementDownIndicator", 
			"DirectionalMovementIndicator", 
			"DirectionalMovementUpIndicator", 
			"DirectionalUpIndicator", 
			"DoubleEMAIndicator", 
			"EMAIndicator", 
			//  "HighestValueIndicator", 
			//  "LowestValueIndicator", 
			"MACDIndicator", 
			//  "MaxPriceIndicator", 
			//  "MeanDeviationIndicator", 
			//  "MedianPriceIndicator", 
			//  "MinPriceIndicator", 
			//  "MultiplierIndicator", 
			"OnBalanceVolumeIndicator", 
			//   "OpenPriceIndicator", 
			//   "ParabolicSarIndicator", 
			"PPOIndicator", 
			"PreviousPriceIndicator", 
			"PriceVariationIndicator", 
			"ROCIndicator", 
			"RSIIndicator", 
			//  "SMAIndicator", 
			//   "StandardDeviationIndicator", 
			"StochasticOscillatorDIndicator", 
			"StochasticOscillatorKIndicator", 
			// "TradeCountIndicator", 
			"TripleEMAIndicator", 
			"TrueRangeIndicator", 
			"TypicalPriceIndicator", 
			//   "VolumeIndicator", 
			"WilliamsRIndicator", 
			"WMAIndicator"


	};


	protected int nAttributes = ran.nextInt(names.length)+3;//how many things are we measuring per stock? 

	boolean setNAttributes(int atties){

		this.nAttributes = atties;
		return true;

	}

	protected int getNAttributes(){

		return this.nAttributes;
	}
	//look for bounces and regression for trait strength and market share, liquidity, analysts, etc

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");//for logging

	protected TimeSeries t;

	static Random b = new Random();

	//protected static double  ul = (b.nextInt(10) * b.nextGaussian())+1;

	protected static double upLimit = 1;//default limit of minimum folds over minimum profit limit

	protected static double alternateFoldUp = 1;//profit in some fold not already above initial threshold


	boolean setUpLimit(double foldUp){

		Peace.upLimit = foldUp;
		return true;

	}

	protected double getUpLimit(){

		return Peace.upLimit;

	}

	boolean setAlternateFoldUp(double foldUp){
		Peace.alternateFoldUp = foldUp;
		return true;}

	protected double getAlternateFoldUp(){
		return Peace.alternateFoldUp;
	}

	protected static int nFolds = 12;


	int getNTicks(){


		return nTicks;
	}

	protected double avProfit;

	protected int avTrades;// ave trades for all profitable folds

	protected static double percentSplit = .1;// used to calculate the fold factor, i.e. by what ratio do we divide the dataset

	static int getRowCount(String path) throws IOException{

		int counter = 0;

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));

		//String line = "";

		while(b.readLine() != null){

			counter++;

		}//end while

		b.close();

		return counter;

	}//end getRowCount

	/////// constructor definition beginning ///////////////////

	public Peace(String _path, String _source){

		sourcePath = _path;
		if(_source.indexOf('.') != -1) symbol = _source.substring(0, _source.indexOf('.'));
		else symbol = _source;
		nTicks = 0;

		//evolve();

	}//end constructor

	//TODO: output parameters values for EMA and ADI to diagnose error
	//begin main

	public static void main(String[] args) throws IOException, ParseException{

		//TODO: rearrange rule order in rule lists, as buy and sell are transposed for testing purposes
		double whole = 0;
		double tProfit = 0;
		long start = System.currentTimeMillis();
		int testTrades = 0;
		//int b = y.nextInt(DJsources.length);		

		String master = "";
		String source = "";

		
		//	ArrayList<String> nyse = getSymbols(Peace.nysePaths);
		//ArrayList<String> nasdaq = getSymbols(Peace.nasdaqPaths);
		ArrayList<String> other = getSymbols(Peace.otherPaths);
		//	for(int i = 0; i < other.size(); i++){


		//	Split.makeTestData(other.get(i), 100);

		//}
		Random y = new Random();
		Piece pc = null;
		String[] DJsources = {//"AAPL.csv", "AXP.csv", //
				"WMT.csv", "MRK.csv", //"JPM.csv", "KO.csv","MMM.csv","V.csv",
				//"BA.csv", "CAT.csv", "CSCO.csv", "CVX.csv", "DD.csv",//
				"DELL.csv", "HP.csv", "ORCL.csv", "NANO.csv", "GOOG.csv", "F.csv", "C.csv", "PRU.csv", "FIZZ.csv", "NFG.csv", 
				//"DIS.csv", "GE.csv", "GS.csv", "HD.csv", 
				"WFC.csv", "GM.csv",
				//"IBM.csv", "INTC.csv", "JNJ.csv", "MCD.csv",  
				//	"MSFT.csv", "NKE.csv", "PFE.csv","PG.csv", 
				//	"TRV.csv", "UNH.csv", "UTX.csv", "VZ.csv", "XOM.csv"
		};

		String[] newSources = {"HOMB.csv", "SLAB.csv", "PFIS.csv", "FE.csv", "LXK.csv", "WEN.csv", "DVN.csv", "K.csv", "KMB.csv", "KR.csv", "LMT.csv", "HPQ.csv"};

		//int z = 0;

		//source = nyse.get(y.nextInt(nyse.size()));
		//source = DJsources[y.nextInt(DJsources.length)];
		//String exchange = "nyse";
		//source = nyse.get(y.nextInt(nyse.size()));

		TreeMap<String, Rule> bMap = new TreeMap<String, Rule>();

		TreeMap<String, Rule> sMap = new TreeMap<String, Rule>();

		Piece one = new Piece();

		double maximum = 0.0;


		source = other.get(y.nextInt(other.size())); //DJsources[11];//24 totaly.nextInt(DJsources.length)
		master = "src/files/" + source + ".csv";//change if not using sources form other array
		int testedRule = 0;
		File rts = new File("src/rules/" + source + "_ruleList.csv");
		if(rts.isFile() != true){ 

			File of = new File(master);
			if(of.isFile()){

				for(int k = 0; k < 5; k++){//jargon


					//	System.out.println(source);

					//	z = k;

					double profit = 0.0;

					ArrayList<String> foldHolder = new ArrayList<String>();

					int nRows = getRowCount(master); 

					Random ran = new Random();

					//default nFolds = 12;
					foldFactor = ran.nextInt(300)+ran.nextInt(300);
					
					//stochastic calculation for more accurate accommodation of variance
					nFolds = (int)(nRows/foldFactor);

					if(nFolds >= 20) nFolds = ran.nextInt(10)+15;
					if(nFolds <= 10) nFolds = ran.nextInt(5)+ 11;
					//System.out.println(nFolds + " tFolds, " + upLimit + ": uplim");
					gLimit = nFolds;

					long date = System.currentTimeMillis()/1000;


					//String header = "source; individual; nGenes; good folds; nFolds ; buy Rule/sell Rule; param values; param order ; maxSector; totalProfit; profitInMaxSector; minorityShare; buySize; sellSize; goodness; nTrades; avTrades per fold; pop size";
					//	BufferedWriter bw2 = new BufferedWriter(new FileWriter (new File("rules/" + source + "_ruleList.csv"), true));
					//		bw2.write(header);
					//bw2.close();

					////////////begin for each fold, make a Peace and calculate trading viability//////////

					//System.out.println(nFolds + " nFolds");

					for(int r = 0; r < nFolds; r++){

						//if((testedRule == 0) && (r > nFolds/2)) break;

						upLimit = b.nextInt(3)+1;

					

						Peace p = new Peace(master, source);

						p.seriesOfFolds = getTSArray(p.symbol, nRows, date, master, nFolds);//getTSArray(p.symbol, nRows, date, master, nFolds);

						date = System.currentTimeMillis()/1000;

						p.date = date;

						//TimeSeries rt = null;
						//rt = p.seriesOfFolds.get(r);//getTS(p.symbol, nRows, 1, master, date);

						if(r < p.seriesOfFolds.size()) p.t = p.seriesOfFolds.get(r);

						else break;

						pc = new Piece(p.t, p.date);

						Random o = new Random();

						p.nGenes = o.nextInt(p.nAttributes);

						p.iGenes(p, p.t, pc, p.tolerance, p.nGenes, p.mutationRate, p.population, p.branchingFactor, Peace.buyMap, Peace.sellMap);

						bMap = Peace.buyMap;
						sMap = Peace.sellMap;

						String params = "";

						for(int h = 0; h < pc.params.length; h++){

							if(h == (pc.params.length - 1)) params += Integer.toString(pc.params[h]);

							else{

								params += Integer.toString(pc.params[h]);

								params += ",";

							}//end else params += pc.params[h] 

						}//end for all params (where h < pc.params.length)

						//do one rule per population
						//then compare that to what constitutes successful gain 

						int nTrades = 0;

						Rule getIt;
						Rule letIt;

						int buySize;
						int sellSize;

						String buyS = "";
						String sellS = "";

						Strategy s;

						String folds = "";	

						//one rule per individual




						//	String testRuleB = "adxBuy0_or_adxBuy1_or_adxBear0";
						//	String testRuleS = "adxSell1_and_adxSell2";
						//	String[] testB = testRuleB.split("_");
						//	String[] testS = testRuleS.split("_");
						//	LinkedList<Genie> b = stringToGenies(testB, Peace.greater);
						//	LinkedList<Genie> se = stringToGenies(testS, Peace.lesser);

						//	Rule h = ruleFromGenie(b, Peace.buyMap, pc);
						//	Rule j = ruleFromGenie(se, Peace.sellMap, pc);


						//	int a = getRowCount("src/" + source);
						//	Decimal d = pc.closePrice.getValue(1);
						//	Decimal amt = Decimal.valueOf(10);

						/*
				Peace.update(master, "today_"+ master);
				BufferedWriter bw = new BufferedWriter(new FileWriter( new File("get.txt"), true));
				BufferedWriter be = new BufferedWriter(new FileWriter( new File("let.txt"), true));
				if(h.isSatisfied(1)){
					bw.write("b: " + p.symbol);
					bw.close();

					}

				if(j.isSatisfied(1)){
					be.write("s: " + p.symbol);
					be.close();
				}
						 */
						//Strategy hope = new Strategy(ruleFromGenie(b, Peace.buyMap, pc), ruleFromGenie(se, Peace.sellMap, pc));


						//	buySize = 3;
						//	sellSize = 2;
						//	test(p.symbol, one, pc, hope, testRuleB, testRuleS);
						
						p.population = 200;
						
						for(int g = 0; g < p.population; ++g){//hummus

							//	System.out.println(g + " i");

							profit = 0.0;

							foldHolder.clear();

							getIt = l.arrayToRule(Peace.get, p.buyG);

							buySize = l.ruleSize;
							buyS = l.ruleString;

							l.ruleSize = 0;
							l.ruleString = "";

							letIt = l.arrayToRule(Peace.let, p.sellG);

							sellSize = l.ruleSize;
							sellS = l.ruleString;

							l.ruleSize = 0;
							l.ruleString = "";

							s = new Strategy(getIt, letIt);

							TradingRecord tr = p.t.run(s);
							//TradingRecord tr = p.t.run(hope);
							nTrades += tr.getTradeCount();

							p.up = new TotalProfitCriterion().calculate(p.t, tr);

							profit += p.up;

							if(p.up > Peace.upLimit){

								if(!folds.isEmpty()) folds = r + ", ";

								p.maxSector = r;

								p.maxProfit = p.up;

								foldHolder.add(Integer.toString(r));

								p.goodness = 0;

								for(int x = 0; x < p.seriesOfFolds.size(); ++x){

									if(x != r){
										//	System.out.println(r + ": fold");
										Peace newPeace = new Peace(master, source);

										newPeace.date = System.currentTimeMillis()/1000;

										newPeace.t = p.seriesOfFolds.get(x);

										TradingRecord newTR = newPeace.t.run(s);	
										//TradingRecord newTR = newPeace.t.run(hope);	

										double newUp = new TotalProfitCriterion().calculate(newPeace.t, newTR);

										profit += newUp;

										nTrades += newTR.getTradeCount();	 	    	 						

										if(newUp >= Peace.alternateFoldUp){

											p.goodness++;

											if(!(folds.lastIndexOf(Integer.toString(x)) > 0)) folds += x + ", ";

											if(newUp > p.maxProfit) {

												p.maxSector = x;

												p.maxProfit = newUp;

											}//end if newUp > p.maxProfit

										}//end if goodness > gLimit 

									}//end if alternateTierUp is met

								}//end if x!=r 

							}//end for all folds other than primary  

							int ask = ran.nextInt((int)(4))+1;
							//confidence limit test
							if((p.goodness > (gLimit-ask)) && (folds.lastIndexOf(Integer.toString(0)) > 0)){

								testedRule++;
								tProfit += profit;

								if(maximum < profit) {

									maximum = profit;

								}

								p.avProfit = profit/nFolds;

								p.avTrades = nTrades/nFolds;

								double leftOvers = profit - p.maxProfit;

								if(p.absoluteMax < p.maxProfit) p.absoluteMax = p.maxProfit;

								p.minorityShare = leftOvers/(nFolds - 1);

								String temp = p.symbol;

								//temp = temp.substring(temp.indexOf('N'), temp.length());
								p.symbol = temp;
								//int rows  = getRowCount("src/data/stocks/"+ p.symbol + ".csv");
								int rows = getRowCount(master);

								double fitness = profit * (double)rows/(double)nTrades  * (double)p.goodness/(double)p.seriesOfFolds.size() * p.minorityShare * 1/(double)(buySize + sellSize); 

								String maxsc = Integer.toString(p.maxSector);

								String maxpro = Double.toString(p.maxProfit);

								String min = Double.toString(p.minorityShare);

								// String buyz = buyS;//Integer.toString(testRuleB);

								//  String sellz =  sellS;

								String good =  Integer.toString(p.goodness);

								//String foldData =  folds;

								String tradeData =  Integer.toString(nTrades);


								//	String profitData =  Double.toString(profit);

								String mutParams = Integer.toString(pc.rsiL)+ ", " + Integer.toString(pc.rsiU) + ", "+ Integer.toString(pc.willU)+ ", " + Integer.toString(pc.willD) + ", "+ Integer.toString(pc.adxU) + ", " + Integer.toString(pc.adxD) + ", "  + Integer.toString(pc.stoU) + ", " + Integer.toString(pc.stoDn);

								String mutParamOrder = "rsiL, rsiU, willU, willD, adxU, adxL, stoU, stoDn";//

								String dataRow = master + "; " + Double.toString(fitness) + ";" + g + ";  " + Integer.toString(p.nGenes) + "; " + folds + "; " + Integer.toString(p.seriesOfFolds.size()) + "; " + sellS  + "; " + buyS  + "; " + params + "; " + pc.paramOrder + ";" + mutParams + ";"+ mutParamOrder + ";" + maxsc + "; " + profit + ";" + maxpro + "; " + min + "; " + buySize +"; " + sellSize +"; " + good +"; " + nTrades +"; " + Peace.upLimit + ";" + gLimit +"\n";

								//	sellRow = master + "; " + g +"; " + Integer.toString(p.nGenes) + "; " + folds + "; " + Integer.toString(seriesOfFolds.size()) + "; " +  sellS  + "; " + params + "; " + pc.paramOrder + ";" + maxsc + "; " + profit + ";" + maxpro + "; " + min + "; " + buyz +"; " + sellz +"; " + good + "; " + nTrades +"; " + Integer.toString(p.population);

								if(p.symbol.contains("data") || (p.symbol.contains("stocks"))) p.symbol = p.symbol.substring(p.symbol.indexOf('k')+3, p.symbol.length());

								BufferedWriter bw2 = new BufferedWriter(new FileWriter (new File("src/rules/" + p.symbol + "_ruleList.csv"), true));

								bw2.write(dataRow);
				
								bw2.close();

				  				//real time output to console
								//System.out.println(p.symbol);

							//	if(g%40 == 0)System.out.println("Run: " + k + ", fold: " + r);

							//	if(g%40 == 0)System.out.println("i: " + g + ", n: " + nFolds);

								//System.out.println();

		// uncomment the following lines for your viewing pleasure // 						
								
								//	System.out.println("Max B: " + p.branchingFactor);
								//	System.out.println("\n");
								//	System.out.println("maxSector: " + maxsc);
								//	System.out.println("maxProfit: " + maxpro);
								//	System.out.println("minorityShare: " + min);
								//  System.out.println("Subgroup: " + Integer.toString(r));
								//	System.out.println("B: " + buySize + ", S: " + sellSize);
								//	System.out.println();
								//  System.out.println("total folds: " + Integer.toString(p.seriesOfFolds.size()));
								//	System.out.println("c: "+ (Double.parseDouble(good)+1)/(double)p.seriesOfFolds.size());
								//	System.out.println();


								//	System.out.println("r: "+ Integer.toString(testedRule));
								//  System.out.println("All good folds: " + foldData);
								//	System.out.println("trades: " + Double.parseDouble(tradeData));
								//	System.out.println();


								//	int size = getRowCount(master);
								//  System.out.println("Av T: " + Double.toString((double)p.avTrades/(double)size));
								//	System.out.println("p: " + profit);
								//	System.out.println(upLimit + ": uplim");//+ " " + upLimit + ": uplim");
								//	System.out.println(ask + ": ask");


							}//end if profit > 2

							folds = "";

							nTrades = 0;

						}//end for all population

						tProfit = tProfit/testedRule;

						whole += tProfit;

						p.seriesOfFolds.clear();

					}//end for each primary fold

				}//end for each run

				String ticker = "";

				if(source.indexOf('.') != -1) ticker = source.substring(0, source.indexOf('.'));

				else ticker = source;

				beginfit = System.currentTimeMillis();

				//0 == tProfit
				//1 == nTrades
				//2 == goodness
				//3 == sizeOfBuyRule
				//4 == sizeOfSellRule
				//5 == minShare

				/*	

		TreeMap<Double, Integer> pTree = new TreeMap<Double, Integer>();
		TreeMap<Integer, Double> pTre = new TreeMap<Integer, Double>();

		pTree = getFittestIndividuals(ticker, 0, 100);
		pTre = getFittestIndividuals(ticker, 1, 100, true);

		TreeMap<Double, Integer> tradeTree = new TreeMap<Double, Integer>();
		TreeMap<Integer, Double> tradeTre = new TreeMap<Integer, Double>();

		tradeTree = getFittestIndividuals(ticker, 1, 100);
		tradeTre = getFittestIndividuals(ticker, 1, 100, true);

		TreeMap<Double, Integer> gTree = new TreeMap<Double, Integer>();
		TreeMap<Integer, Double> gTre = new TreeMap<Integer, Double>();

		gTree = getFittestIndividuals(ticker, 2, 100);
		gTre = getFittestIndividuals(ticker, 2, 100, true);

		TreeMap<Double, Integer> bTree = new TreeMap<Double, Integer>();
		TreeMap<Integer, Double> bTre = new TreeMap<Integer, Double>();

		bTree = getFittestIndividuals(ticker, 3, 100);
		bTre = getFittestIndividuals(ticker, 3, 100, true);

		TreeMap<Double, Integer> sTree = new TreeMap<Double, Integer>();
		TreeMap<Integer, Double> sTre = new TreeMap<Integer, Double>();

		sTree = getFittestIndividuals(ticker, 4, 100);
		sTre = getFittestIndividuals(ticker, 4, 100, true);

		TreeMap<Double, Integer> mTree = new TreeMap<Double, Integer>();
		TreeMap<Integer, Double> mTre = new TreeMap<Integer, Double>();

		mTree = getFittestIndividuals(ticker, 5, 100);
		mTre = getFittestIndividuals(ticker, 5, 100, true);



				 */


				/*
		if(testedRule > 0) {
			Vector<LinkedList<Genie> > b = Peace.getBest(ticker, 0, 100, true, one, Peace.greater);

			Vector<LinkedList<Genie> > s = Peace.getBest(ticker, 0, 100, false, one, Peace.lesser);


			int nGens = 200;
			int rows = Peace.getRowCount(source);

			//TODO: write all of the fitnesses and get the best based on fitness, then mix
			ArrayList<TimeSeries> t = Peace.getTS(ticker, rows, 1, master, beginfit);
			Peace.mixGoodRules(b, s, nGens, one, t.get(0));

		}

				 */

				//runTest(ticker, one, pc, bMap, sMap, 10, testTrades);//lubs
				//int oldRowCount = getRowCount(ticker);
				//TODO: take out redundant sell rules
				//System.out.println(oldRowCount + " rulesBeforeRemoval");
				//System.out.println(ticker);
				//remRules(ticker);
				//oldRowCount = getRowCount(ticker);
				//System.out.println(oldRowCount + " rulesAfterRemoval");

				//br.close(); 	 


				long endfit = System.currentTimeMillis();

				long u = endfit - beginfit;

				long finish = System.currentTimeMillis();

				long difference = finish - start;

				totalTime = difference;

				Log log = new Log("runTimeLog.txt");

				//System.out.println(whole/(double)8 + " avProfit per run");

				//System.out.println("runtime = " + (totalTime/(double)1000) + " seconds");

				Date n = new Date(start);

				log.write( (totalTime/1000)/4, master + ": runtime in seconds on: " + n.toString());

				//System.out.println("completed fitness calculations for: " + master + " = "+  (u/1000) + " seconds");

				log.write( (u/1000), master + ": fitness runtime in seconds on: " + n.toString());

					System.out.println("ready");
				
			}//end if file exists
			else{
				
				System.out.println("file not found");
			
			 }

		}//end if rule file exists
		else{
			System.out.println("rules made");
		}
	}///end main	 		   


	static void remRules(String tick) throws IOException{

		String path = "src/rules/"+ tick + "_ruleList.csv";
		String p = "src/rules/"+ tick + "_refRules.csv";

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(p), true));

		String temp = "";
		String line = "";
		
		int index1 = 0;
		int index2 = 0;
		
		while((temp = b.readLine()) != null){
			
			index1++;
			temp = line;
			String[] t = temp.split(";");
			String y = t[5];
			
			while((line = b.readLine()) != null){
				
				index2++;
				String[] row = line.split(";");
				String r = row[5];
				
				if((r).equals(y) && (index1 != index2))continue;
				else {
					w.write(temp);
				}
			}
			w.close();
			b.close();
		}
		System.out.println("refined");
	}//end rem rules
	
	static Vector< LinkedList<Genie> > getBest(String symbol, int fitIndex, int nIndividuals, boolean isBuy, Piece p, LinkedList<Genie> list) throws IOException{

		//0 == tProfit
		//1 == nTrades
		//2 == goodness
		//3 == sizeOfBuyRule
		//4 == sizeOfSellRule
		//5 == minShare

		TreeMap<Double, Integer> tree = new TreeMap<Double, Integer>();

		tree = Peace.getFittestIndividuals(symbol, fitIndex, nIndividuals);

		Vector<LinkedList<Genie>> best = new Vector<LinkedList<Genie>>();

		Vector<Integer> vec = new Vector<Integer>();

		for(int i = 0; i < nIndividuals; i++){

			vec.add(tree.get(tree.lastKey()));

		}

		boolean good = false;

		String row;

		int line = 0;

		Vector<String> rowStrings = new Vector<String>();

		String path = "src/rules/" + symbol + "_ruleList.csv";

		for(int index = 0; index < vec.size(); index++){
			good = true;

			BufferedReader b = new BufferedReader(new FileReader(new File(path)));

			while((good == true) && ((row = b.readLine()) != null)) {

				line++;

				if(line == (vec.get(index))){		
					rowStrings.add(row); 
					line = 0;
					good = false;
					b.close();
				}//end if
			}//end while read line is not null
		}//end for each row in vec

		for(int i = 0; i < vec.size(); i++){

			String[] f = rowStrings.get(i).split(";");
			
			String s = "";

			//	System.out.println(f[5] + ": buy rule");
			if(isBuy == true){
				
				s = f[5];
				
			}//end if
			else{

				if(f[9].indexOf(',') > 0) {
					//	System.out.println(f[6] + ": sell rule");
					s = f[6];
				}
				else {
					//	System.out.println(f[21] + ": sell rule");
					s = f[21];
				}

			}//end else
			
			String[] sp = s.split("_");

			LinkedList<Genie> sg = new LinkedList<Genie>();

			sg = stringToGenies(sp, list);

			best.add(sg);
		}//end for
		return best;
	}//end get best


	static void mixGoodRules(Vector<LinkedList<Genie> > buy, Vector<LinkedList<Genie> > sell, int generations, Piece p, TimeSeries t){



		LinkedList<Genie> v = new LinkedList<Genie>();

		LinkedList<Genie> g = new LinkedList<Genie>();

		LinkedList<Genie>  btemp = new LinkedList<Genie>();
		LinkedList<Genie>  stemp = new LinkedList<Genie>();

		Vector<LinkedList<Genie> > bMut = new Vector< LinkedList<Genie> >();
		Vector<LinkedList<Genie> > sMut = new Vector< LinkedList<Genie> >();

		Vector<LinkedList<Genie>> bChrom = new Vector<LinkedList<Genie>>();
		Vector<LinkedList<Genie>> sChrom = new Vector<LinkedList<Genie>>();



		Random r = new Random();

		int andnot = r.nextInt(3);
		int and = r.nextInt(5);
		int or = r.nextInt(2);
		int ornot = r.nextInt(4);
		int xor = r.nextInt(10);

		for(int i = 0; i < buy.size(); i++){

			v = buy.get(i);
			g = sell.get(i);


			//TODO: splice with some varying percentage according to the boolean connection logic
			//or 50%
			//and 20%
			//xor 10%
			//andnot 33%
			//ornot 25%



			for(int s = 0; s < v.size(); s++){


				if((v.get(s).bool == 1) && ((or%2) ==0)) {

					stemp.add(v.get(s));


				}
				else if((v.get(s).bool == 0) && ((and%3) == 0)){

					stemp.add(v.get(s));



				}
				else if((v.get(s).bool == 2) && ((xor%8) == 0)){

					stemp.add(v.get(s));



				}
				else if((v.get(s).bool == 3) && ((andnot%2) == 0)){

					stemp.add(v.get(s));



				}
				else if((v.get(s).bool == 4) && ((ornot%3) == 0)){

					stemp.add(v.get(s));



				}
				else {

					if(s > 0) {

						stemp.get(s-1).next = v.get(s).previous;
						stemp.get(s).previous = stemp.get(s-1).next;

					}//end if s > 0

					sChrom.add(stemp);
					stemp.clear();

				}//end else if none of the booleans are included

			}//end for each genie in temp list

		}//end for each list of genies from good fitness list

		//redo percentages for the boolean connectors
		andnot = r.nextInt(3);
		and = r.nextInt(5);
		or = r.nextInt(2);
		ornot = r.nextInt(4);
		xor = r.nextInt(10);

		for(int s = 0; s < g.size(); s++){


			if((g.get(s).bool == 1) && ((or%2) ==0)) {

				btemp.add(g.get(s));


			}
			else if((g.get(s).bool == 0) && ((andnot%3) == 0)){

				btemp.add(g.get(s));



			}
			else if((g.get(s).bool == 2) && ((xor%8) == 0)){

				btemp.add(g.get(s));

			}
			else if((g.get(s).bool == 3) && ((andnot%2) == 0)){

				btemp.add(g.get(s));

			}
			else if((g.get(s).bool == 4) && ((ornot%3) == 0)){

				btemp.add(g.get(s));

			}
			else {

				if(s > 0) {

					btemp.get(s-1).next = g.get(s).previous;
					btemp.get(s).previous = btemp.get(s-1).next;

				}//end if s > 0

				bChrom.add(btemp);
				btemp.clear();

			}//end else if none of the booleans are included

		}//end for each genies in buy treemap	

		int whichB = 0;
		int whichS = 0;

		int nBGenes = 0;
		int nSGenes = 0;

		//recombination step after hybrid chromosomes are created

		for(int a = 0; a < generations; a++){

			//how many genes in new chromosome

			nBGenes = r.nextInt(3);
			nSGenes = r.nextInt(3);

			//randomly add fit genes together

			for(int y = 0; y < nBGenes; y++){

				whichB = r.nextInt(bChrom.size());

				bMut.add(bChrom.get(whichB));

			}

			for(int y = 0; y < nSGenes; y++){

				whichS = r.nextInt(sChrom.size());

				sMut.add(sChrom.get(whichS));

			}

			//link the good spliced genes together
			LinkedList<Genie> bLinks = new LinkedList<Genie>();

			int index = bMut.size();

			while(index != 0){

				if(index == bMut.size()) bLinks = bMut.get(index);
				else {

					bLinks.getLast().next = bMut.get(index).getFirst().previous;
					bLinks.getFirst().previous = bMut.get(index+1).getLast().next;

				}
				index--;

			}//end of recombining good buy rules

			//link the good spliced genes together

			index = sMut.size();
			LinkedList<Genie> sLinks = new LinkedList<Genie>();

			while(index != 0){

				if(index == sMut.size()) sLinks = sMut.get(index);

				else {

					sLinks.getLast().next = sMut.get(index).getFirst().previous; 
					sLinks.getFirst().previous = sMut.get(index+1).getLast().next;
				}

				index--;

			}//end of splicing good sell rules

			//make a strategy from the newly aggregated chromosomes
			Strategy mutant = new Strategy(Peace.ruleFromGenie(bLinks, Peace.buyMap, p), Peace.ruleFromGenie(sLinks, Peace.sellMap, p));

			TradingRecord tr = t.run(mutant);

			double mutantProfit = new TotalProfitCriterion().calculate(t, tr);

			if(mutantProfit > 0) hybrid.put(mutantProfit, mutant);


		}//end for each generation




	}//end getCommonTraits

	protected static void update(String oldpath, String newpath) throws IOException{


		BufferedReader bo = new BufferedReader(new FileReader(new File(oldpath)));

		String line = "";
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newpath), true));

		while((line = bo.readLine()) != null){

			bw.write(line);

		}
		int row = getRowCount(oldpath);
		int row1 = getRowCount(newpath);
		if((row1 - row) != 1) System.out.println("error appending data from old file to new file");
		//TODO: delete old file
		bo.close();
		bw.close();

	}//end update

	protected static void writeFitness(String ticker, String exchange) throws IOException{
		//	System.out.println("inside write");
		BufferedWriter w = new BufferedWriter(new FileWriter(new File("src/rules/"+ticker+".csv"), true));

		BufferedReader b = new BufferedReader(new FileReader(new File("src/rules/"+ticker+".csv")));

		TreeMap<Integer, Double> t = calcFitness(exchange, ticker, true);
		String l = "";
		String[] r;
		System.out.println(ticker);
		while((l = b.readLine())!=null ){


			r = l.split(";");
			int s = r.length;

			String[] g = new String[s+1];

			for(int a = 0; a < s+1; a++){

				if(a<s)g[a] = r[a];
				else g[a] = Double.toString(t.get(a));



			}
			String j = "";
			for(int h = 0; h < s+1; h++){

				j += g[h];
			}
			w.write(j);
		}
		b.close();
		w.close();

		//System.out.println("after write");
	}
	protected static TreeMap<Double, Integer> getFitnesses(String ticker) throws IOException{

		//	String path = "src/rules/" + ticker + "_ruleList.csv";
		TreeMap<Double, Integer> fit = new  TreeMap<Double, Integer>();
		//	BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		fit = calcFitness(ticker);
		//	String row = "";
		//	int index = 0;
		//TODO: find indexes of fitnesses once they are calculated
		/*	while((row = b.readLine()) != null){
			index++;
			String[] f = row.split(";");

			if(f[8].length() > 2) {

				fit.put(Double.parseDouble(f[21]), index);

			}//end if
			else{

				fit.put(Double.parseDouble(f[15]), index);
			}//end else


		}//end while
		 */
		//b.close();
		return fit;
	}//end getFitnesses

	protected static void runTest(String ticker, Piece one, Piece pc, TreeMap<String, Rule> bMap, TreeMap<String, Rule> sMap, double elite, int trades) throws IOException, NumberFormatException, ParseException{

		double avP = 0.0;
		int maxTrades = 0;
		int avTrades = 0;
		double minProfit = 1000;
		double maxProfit = 0;
		String path = "src/test/" + ticker + "_ruleList.csv";

		TreeMap<Double, Integer> tree = new TreeMap<Double, Integer>();

		tree = Peace.calcFitness(ticker);
		writeFitness(ticker, "DJ");
		Vector<Integer> vec = new Vector<Integer>();

		for(int i = 0; i < (int)(tree.size()*(elite*.01)); i++){

			if(!(vec.contains(tree.get(tree.lastKey())))) vec.add(tree.get(tree.lastKey()));

			tree.remove(tree.lastKey());

		}

		//	System.out.println("vec size:  " + vec.size());

		String source = ticker + ".csv";	

		Peace testP = new Peace("src/test/" + source, source);

		ArrayList<TimeSeries> array = new ArrayList<TimeSeries>();

		int nRows = getRowCount(testP.sourcePath);

		long date = System.currentTimeMillis();

		double max = 0;

		array = getTSArray(testP.symbol, nRows, date, testP.sourcePath, 1);

		testP.t = array.get(0);

		one.series = testP.t;

		swapParams(one, pc);
		//System.out.println(testP.t.getTickCount()  +": ticks in timeseries of test run data");
		testP.buyG = greater;
		testP.sellG = lesser;
		//System.out.println(greater.size()  + ": g size");
		//System.out.println(lesser.size()  + ": l size");

		testP.bestB = Peace.getRulesByRow(vec, ticker, bMap, true, one, testP.buyG);
		testP.bestS = Peace.getRulesByRow(vec, ticker, sMap, false, one, testP.sellG);

		//	double minp = 1000;
		double maxp = 0;

		//	System.out.println(testP.bestB.size() + " size of best buy rules");
		//	System.out.println(testP.bestS.size() + " size of best sell rules");

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), true));    		

		//	int counter = 0;

		for(int i1 = 1; i1 < testP.bestB.size()+1; i1++){


			if((testP.bestB.elementAt(i1-1) != null) && (testP.bestS.elementAt(i1-1) != null)) {


				Strategy strat = new Strategy(testP.bestB.elementAt(i1-1), testP.bestS.elementAt(i1-1));

				TradingRecord tr = testP.t.run(strat);

				int nTrades = tr.getTradeCount();


				testP.up = new TotalProfitCriterion().calculate(testP.t, tr);



				if (testP.up < minProfit) minProfit = testP.up;
				if(maxProfit < testP.up) maxProfit = testP.up;

				//rule index from best rule container, row index from best rule vector (should be source row), # of trades, net
				if(testP.up >= 1.00000000001) {
					avP += testP.up;
					if(max < testP.up) {
						max = testP.up;
						maxTrades = nTrades;
					}

					avTrades += nTrades;
					//			counter++;
					//rule index, row that rule came from, number of trades, profit, self explanatory
					bw.write(i1-1 + ";" + vec.get(i1-1) + ";" + Integer.toString(nTrades) + ";" + Double.toString(testP.up) + "\n");

					//			System.out.println("rule written");
					//string[5] + ";" + string[6] + ";"  + Double.toString(testP.up) + string[7]  + ";" + string[8] + ";" + "\n");
				}
			}
		}//end for each rule in p.bRule

		//		System.out.println(counter + " good rules");

		//		System.out.println(max + ": max");

		//		System.out.println(avTrades/(double)counter + " av Trades per good rule");
		System.out.println(maxTrades + " trades for profit of: " + max);
		//	System.out.println("t: " + maxTrades);
		//	System.out.println("av p" + avP/(double)counter);


		bw.close();


	}

	protected static void test(String ticker, Piece one, Piece pc, Strategy s, String buy, String sell) throws IOException, NumberFormatException, ParseException{


		String path = "src/test/" + ticker + "_ruleList.csv";

		String source = ticker + ".csv";	

		Peace testP = new Peace("src/test/" + source, source);

		ArrayList<TimeSeries> array = new ArrayList<TimeSeries>();

		int nRows = getRowCount(testP.sourcePath);

		long date = System.currentTimeMillis();

		double max = 0;

		array = getTSArray(testP.symbol, nRows, date, testP.sourcePath, 1);

		testP.t = array.get(0);

		one.series = testP.t;

		swapParams(one, pc);








		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), true));    		

		int counter = 0;









		TradingRecord tr = testP.t.run(s);

		int nTrades = tr.getTradeCount();

		testP.up = new TotalProfitCriterion().calculate(testP.t, tr);

		//rule index from best rule container, row index from best rule vector (should be source row), # of trades, net
		if(testP.up > 1.0) {

			if(max < testP.up) max = testP.up;

			counter++;

			bw.write(buy + ";" + sell + ";" + Integer.toString(nTrades) + ";" + Double.toString(testP.up) + "\n");

			System.out.println("rule written");
			System.out.println(testP.up + ": p");
			System.out.println(max + ": m");
			System.out.println(nTrades + ": t");
			//string[5] + ";" + string[6] + ";"  + Double.toString(testP.up) + string[7]  + ";" + string[8] + ";" + "\n");


		}//end for each rule in p.bRule

		//System.out.println(counter + " good rules");



		bw.close();


	}

	private static ArrayList<TimeSeries> getTSArray(String symbol, int nRows, long date, String master, int nFolds) throws ParseException, NumberFormatException, IOException {


		ArrayList<TimeSeries> seriesArray = new ArrayList<TimeSeries>();	

		if((nFolds) == 0) {
			System.out.println("error in fold buildings, zero folds for this time series");
			return null;
		}

		else{

			seriesArray = getTS(symbol, nRows, nFolds, master, date);

		}//end else 

		return seriesArray;
	}//end getTimeSeriesArray


	static String[] getPaths(String source) throws IOException{
		String temp;
		String[] paths = null;
		BufferedReader b = new BufferedReader(new FileReader(source));
		while((temp = b.readLine()) != null){

			paths = temp.split(" ");
			//System.out.println(paths.length);

		}
		b.close();
		return paths;

	}//end getPaths

	TreeMap<Integer, Integer> compareFittest(ArrayList<int[]> list){

		int counter = 0;
		int g = 0;

		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();

		for(int i = 0; i < list.size(); i++){

			for (int d = 0; d < list.get(i).length; d++){
				//g is row
				g = list.get(i)[d];

				for(int h = 0; h < list.size(); h++){

					if(h != i) {

						for(int s = 0; s < list.get(h).length; s++){

							if(g == list.get(h)[s]){

								counter++;

							}//end if g == element in array

						}//end for elements in inner array list compare

					}//end if h!=i

				}//end for inner array list compare

				if(!(map.containsValue(g))) map.put(counter, g);

			}//end for all elements in  array


		}//end for all arrays in array list


		return map;

	}
	/*
private static double calcFitnessByRow(String symbol, int row) throws IOException{


	double fitness = 0;

	String path = "rules/" + symbol + "_ruleList.csv";

	double share = getMinShare(path, row);
	double profit = getProfit(path, row);

	int nTrades = getTrades(path, row);
	//int buy = getBSize(path, row);
	//int sell = getSSize(path, row);
	//int goodness = getGoodness(path, row);
	int folds = getNFolds(path, row);
	//int maxSect = getMaxSector(path, row);

	String ogPath = "src/" + symbol + ".csv";

	int rows = getRowCount(ogPath);

	fitness =  (int)(profit/folds)  * (int)(nTrades/rows) * (int)share; 
	//if(maxSect == 0) fitness *= 1.2;
	//if(maxSect == 1) fitness *= 1.1;
	//if(maxSect == 1) fitness *= 1.05;



	return fitness;

}
	 */

	private static TreeMap<Double, Integer> calcFitness(String symbol) throws IOException{

		//int fitCount = 0;

		String path = "src/rules/" + symbol + "_ruleList.csv";

		double fitness = 0;

		TreeMap<Double, Integer> tree = new TreeMap<Double, Integer>();

		int index = -1;

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));

		String row = "";

		double share = 0;
		double profit = 0;

		int nTrades = 0;
		int buy = 0;
		int sell = 0;
		int goodness = 0;
		//int folds = 0;
		//int maxSect = 0;

		while((row = b.readLine()) != null ){

			index++;	

			String[] data = row.split(";");
			String t = data[9].trim();

			if((t.indexOf(',') > 0) && (data[1].trim().length() < 6)){

				share = Double.parseDouble(data[14].trim());
				profit = Double.parseDouble(data[12].trim());
				nTrades = Integer.parseInt(data[18].trim());
				buy = Integer.parseInt(data[15].trim());
				sell = Integer.parseInt(data[16].trim());
				goodness = Integer.parseInt(data[17].trim());
				//folds = Integer.parseInt(data[4].trim());
				//	maxSect = Integer.parseInt(data[11].trim());



			}

			else if((data[1].trim().length() < 6)){
				share = Double.parseDouble(data[11].trim());
				profit = Double.parseDouble(data[9].trim());
				nTrades = Integer.parseInt(data[15].trim());
				buy = Integer.parseInt(data[12].trim());
				sell = Integer.parseInt(data[13].trim());
				goodness = Integer.parseInt(data[14].trim());
				//folds = Integer.parseInt(data[4].trim());
				//	maxSect = Integer.parseInt(data[8].trim());

			}
			else {
				fitness = Double.parseDouble(data[1].trim());
			}
			String source = "src/" + symbol + ".csv";

			int rows = getRowCount(source);
			//System.out.println(profit/folds + " proft");
			//	System.out.println(1.0/(double)(buy+sell) + ": buy sell");
			//System.out.println((double)nTrades/(double)rows + " tr/row");
			//System.out.println(share + ": share");
			//	System.out.println(profit + ": profit");
			fitness = Math.pow(profit,((double)rows/(double)nTrades))  * (double)goodness * Math.log(share) * Math.exp(1/(double)(buy+sell)); 
			//	System.out.println(fitness);
			//if(maxSect == 0) fitness *= 1.03;
			//	if(maxSect == 1) fitness *= 1.02;
			//	if(maxSect == 2) fitness *= 1.01;
			//if(tree.containsKey(fitness) == true)fitCount++;
			//	System.out.println(fitness + " = fitness");
			if(tree.containsKey(fitness) == false) {
				tree.put(fitness, index);
			}

		}//end while
		b.close();
		//System.out.println(fitCount + " = fitcount");
		return tree;

	}//end  calcFitness <Double, Integer>

	static protected ArrayList<String> getSymbols(String path) throws IOException{

		ArrayList<String> symbols = new ArrayList<String>();
		BufferedReader b;
		b = new BufferedReader(new FileReader(new File("src/ref/" + path)));


		String row = b.readLine();
		if(path.equals("syms.txt")){
			//	int index = 0; 
			String sym = "";
			while((sym = b.readLine()) != null){

				sym = sym.substring(sym.indexOf("/"), sym.length());
				symbols.add(sym);

			}

		}
		else {
			String[] paths = row.split(" ");
			for(int i = 0; i < paths.length; i++){

				symbols.add("data/stocks/" + paths[i]);

			}
		}

		b.close();
		return symbols;

	}//end get symbols

	static protected boolean makeTestSet(int length, String source) throws IOException{

		String sPth = "src/data/stocks/" + source;
		String wPth = "src/test/test_" + source;
		BufferedReader b = new BufferedReader(new FileReader(new File(sPth)));
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(wPth), true));
		String temp = b.readLine();//header 
		while((length+1 > 0)){
			temp = "";
			temp = b.readLine();
			w.write(temp);
			length--;

		}
		b.close();
		w.close();


		if (length == getRowCount(sPth)) return true;
		else return false;
	}//end makeTestSet


	static protected void swapParams(Piece pc, Piece b){

		pc.adxD = b.adxD;
		pc.adxP = b.adxP;
		pc.adxU = b.adxU;
		pc.aroonP = b.aroonP;
		pc.atrP = b.atrP;
		pc.bbP = b.bbP;
		pc.cciP = b.cciP;
		pc.macdL = b.macdL;
		pc.macdS = b.macdS;
		pc.ema2P = b.ema2P;
		pc.ema3P = b.ema3P;
		pc.emaP = b.emaP;
		pc.devP = b.devP;
		pc.origin = b.origin;
		pc.positive100 = b.positive100;
		pc.negative100 = b.negative100;
		pc.unit = b.unit;
		pc.threshold1 = b.threshold1;
		pc.threshold2 = b.threshold2;
		pc.stoLBound = b.stoLBound;
		pc.stoUBound = b.stoUBound;
		pc.rsiLowerBound = b.rsiLowerBound;
		pc.rsiUpperBound = b.rsiUpperBound;
		pc.willLBound = b.willLBound;
		pc.willUBound = b.willUBound;

		// Close price
		ClosePriceIndicator closePrice = new ClosePriceIndicator(pc.series);

		// Typical price
		pc.typicalPrice = new TypicalPriceIndicator(pc.series);

		// Price variation
		pc.priceVariation = new PriceVariationIndicator(pc.series);


		// Simple moving averages
		pc.shortSma = new SMAIndicator(closePrice, pc.sSMA);

		// decvec.add(shortSma);

		pc.longSma = new SMAIndicator(closePrice, pc.lSMA);

		// decvec.add(longSma);

		// Exponential moving averages
		pc.shortEma = new EMAIndicator(closePrice, pc.sEMA);

		//    decvec.add(shortEma);

		pc.longEma = new EMAIndicator(closePrice, pc.lEMA);

		//    decvec.add(longEma);

		// Percentage price oscillator
		pc.ppo = new PPOIndicator(closePrice, pc.ppoS, pc.ppoL);

		//ema for ppo only

		pc.ppoema = new EMAIndicator(closePrice, pc.ema2P);

		//    decvec.add(ppo);

		// Rate of change
		pc.roc = new ROCIndicator(closePrice, pc.rocP);

		//   decvec.add(roc);

		// Relative strength index
		pc.rsi = new RSIIndicator(closePrice, pc.rsiP);

		//   decvec.add(rsi);

		// Williams %R
		pc.willR = new WilliamsRIndicator(pc.series, pc.willP);
		//    decvec.add(williamsR);

		// Average true range
		pc.atr = new AverageTrueRangeIndicator(pc.series, pc.atrP);
		//    decvec.add(atr);
		//  pc.atr = atr;
		// Standard deviation
		pc.sd = new StandardDeviationIndicator(closePrice, pc.devP);// Close price
		//   decvec.add(sd);

		pc.ade = new AccelerationDecelerationIndicator(pc.series, pc.sSMA, pc.lSMA);

		//    decvec.add(ade);
		pc.adi = new AccumulationDistributionIndicator(pc.series);

		//    decvec.add(adi);
		pc.arooD = new AroonDownIndicator(pc.series, pc.aroonP);

		//    decvec.add(arooD);
		pc.arooU = new AroonUpIndicator(pc.series, pc.aroonP);

		//    decvec.add(arooU);
		pc.admD = new AverageDirectionalMovementDownIndicator(pc.series, pc.adxP);

		//    decvec.add(admD);
		pc.admU = new AverageDirectionalMovementUpIndicator(pc.series, pc.adxP);

		//    decvec.add(admU);
		pc.adm = new AverageDirectionalMovementIndicator(pc.series, pc.adxP);

		//    decvec.add(adm);
		pc.ags= new AverageGainIndicator(closePrice, pc.sEMA);

		//    decvec.add(ags);
		pc.agl = new AverageGainIndicator(closePrice, pc.lEMA);

		//    decvec.add(agl);
		pc.als= new AverageLossIndicator(closePrice, pc.sEMA);

		//    decvec.add(als);getTS
		pc.all = new AverageLossIndicator(closePrice, pc.lEMA);

		//    decvec.add(all);
		pc.atri = new AverageTrueRangeIndicator(pc.series, pc.atrP); 

		//    decvec.add(atri);
		pc.bbM = new BollingerBandsMiddleIndicator(closePrice); 

		//    decvec.add(bbm);
		pc.bbL = new BollingerBandsLowerIndicator(pc.bbM, closePrice); 

		//    decvec.add(bbl);
		pc.bbU = new BollingerBandsUpperIndicator(pc.bbM, closePrice); 

		//    decvec.add(bbu);
		pc.cci = new CCIIndicator(pc.series, pc.cciP);
		;
		//    decvec.add(cci);
		pc.dmD = new DirectionalMovementDownIndicator(pc.series);

		//    decvec.add(dmd);
		pc.dmU = new DirectionalMovementUpIndicator(pc.series);

		//    decvec.add(dmu);
		pc.dm = new DirectionalMovementIndicator(pc.series, pc.adxP); 

		pc.dxD = new DirectionalDownIndicator(pc.series, pc.adxP);

		//    decvec.add(ddi);
		pc.dxU = new DirectionalUpIndicator(pc.series, pc.adxP);

		//    decvec.add(dui);
		pc.dubE = new DoubleEMAIndicator(closePrice, pc.ema2P);

		//    decvec.add(dema);
		pc.triE = new TripleEMAIndicator(closePrice, pc.ema3P);

		//    decvec.add(tema);
		pc.macd = new MACDIndicator(closePrice, pc.macdS, pc.macdL);

		//    decvec.add(macd);
		pc.meanDev = new MeanDeviationIndicator(closePrice, pc.devP);

		//    decvec.add(meandev);
		pc.obv = new OnBalanceVolumeIndicator(pc.series);

		//    decvec.add(obv);
		pc.sar = new ParabolicSarIndicator(pc.series, pc.sarP);


		pc.stoKi = new StochasticOscillatorKIndicator(pc.series, pc.stoK);

		pc.stoDi = new StochasticOscillatorDIndicator(pc.stoKi);


		pc.smoothedDXD = new SMAIndicator(pc.admD, pc.smoothingPeriod); 


		pc.smoothedDXU = new SMAIndicator(pc.admU, pc.smoothingPeriod);



		//    decvec.add(sar);
		pc.wma = new WMAIndicator(closePrice, pc.wmaP); 

		//    decvec.add(wma);
		pc.tri = new TrueRangeIndicator(pc.series);

		//    decvec.add(tr);
		pc.typ = new TypicalPriceIndicator(pc.series); 

	}

	private static TreeMap<Integer, Double> calcFitness(String exchange, String symbol, boolean is) throws IOException{

		String path = "src/rules/" + symbol + "_ruleList.csv";

		double fitness = 0;

		TreeMap<Integer, Double> tree = new TreeMap<Integer, Double>();

		int index = -1;

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));

		String row = "";

		double share = 0;
		double profit = 0;

		double nTrades = 0;
		double buy = 0;
		double sell = 0;
		double goodness = 0;
		double folds = 0;
		//int maxSect = 0;
		String t = "";

		while((row = b.readLine()) != null ){
			//	System.out.println(symbol + ": sym");
			//System.out.println(index++ + ": row index");	

			String[] data = row.split(";");
			if(exchange.equals("DJ")) {
				t = data[9].trim();
			}
			if(exchange.equals("nyse")){
				t = data[12].trim();
			}


			if((t.indexOf(',') > 0) && (data[1].trim().length() < 6)){

				share = Double.parseDouble(data[14].trim());
				profit = Double.parseDouble(data[12].trim());
				nTrades = Double.parseDouble(data[18].trim());
				buy = Double.parseDouble(data[15].trim());
				sell = Double.parseDouble(data[16].trim());
				goodness = Double.parseDouble(data[17].trim());
				folds = Double.parseDouble(data[4].trim());
				//	maxSect = Integer.parseInt(data[11].trim());



			}

			else if((data[1].trim().length() < 6)){
				share = Double.parseDouble(data[11].trim());
				profit = Double.parseDouble(data[9].trim());
				nTrades = Double.parseDouble(data[15].trim());
				buy = Double.parseDouble(data[12].trim());
				sell = Double.parseDouble(data[13].trim());
				goodness = Double.parseDouble(data[14].trim());
				folds = Double.parseDouble(data[4].trim());
				//	maxSect = Integer.parseInt(data[8].trim());

			}
			else {
				fitness = Double.parseDouble(data[1].trim());
			}
			String source = "src/" + symbol + ".csv";

			int rows = getRowCount(source);
			//System.out.println(profit/folds + " proft");
			//	System.out.println(1.0/(double)(buy+sell) + ": buy sell");
			//System.out.println((double)nTrades/(double)rows + " tr/row");
			//System.out.println(share + ": share");
			//	System.out.println(profit + ": profit");
			fitness = profit * nTrades/rows  * goodness/folds * share * (1/(buy+sell)); 
			//	System.out.println(fitness);
			//if(maxSect == 0) fitness *= 1.03;
			//	if(maxSect == 1) fitness *= 1.02;
			//	if(maxSect == 2) fitness *= 1.01;
			//if(tree.containsKey(fitness) == true)fitCount++;
			//	System.out.println(fitness + " = fitness");
			if(tree.containsKey(index) == false) {
				tree.put(index, fitness);
			}

		}//end while
		b.close();
		//System.out.println(fitCount + " = fitcount");
		return tree;
	}


	static Vector<Rule> getRulesByRow(Vector<Integer> rows, String symbol, TreeMap<String, Rule> map, boolean isBuy, Piece one, LinkedList<Genie> list) throws IOException{
		//	System.out.println(rows.size() + " size of row vector with rule indexes");
		Vector<Rule> vec = new Vector<Rule>();

		LinkedList<Genie> g = new LinkedList<Genie>();

		String path = "src/rules/" + symbol + "_ruleList.csv";

		ArrayList<String> rowStrings = new ArrayList<String>();



		String row = "";


		String s = "";
		int line = -1;
		boolean good = false;
		for(int index = 1; index < rows.size(); index++){
			good = true;
			BufferedReader b = new BufferedReader(new FileReader(new File(path)));
			while((good == true) && ((row = b.readLine()) != null)) {
				//		System.out.println(rows.get(1) + " row 1");
				line++;
				//	System.out.println(line + "line");
				//	System.out.println(rows.get(index) + ": row");

				if(line == (rows.get(index))){		
					rowStrings.add(row); 
					line = 0;
					good = false;
					b.close();
				}//end if
			}//end while read line is not null
		}//end for each row in vec
		for(int i = 0; i < rowStrings.size(); i++){

			String[] f = rowStrings.get(i).split(";");


			if(isBuy) {
				//	System.out.println(f[5] + ": buy rule");
				s = f[5];
			}
			else {
				if(f[9].indexOf(',') > 0) {
					//	System.out.println(f[6] + ": sell rule");
					s = f[6];
				}
				else {
					//	System.out.println(f[21] + ": sell rule");
					s = f[21];
				}
			}

			String[] sp = s.split("_");
			//System.out.println(sp.length + " length of rule string");
			//	System.out.println(list.size() + " size of genie list");
			g = stringToGenies(sp, list);
			//System.out.println(g.size() + " size of new genie list");

			Rule rule = ruleFromGenie(g, map, one);

			vec.add(rule);

		}//end for


		if(isBuy == true) bRuleStrings = rowStrings;
		else sRuleStrings = rowStrings;

		return vec;




	}



	static int[] getFitRowsByParam(String symbol, int fitIndex, int nIndividuals) throws IOException{

		//0 == tProfit
		//1 == nTrades
		//2 == goodness
		//3 == sizeOfBuyRule
		//4 == sizeOfSellRule
		//5 == minShare


		int[] fittest = new int[nIndividuals];

		String path = symbol + "_ruleList.csv";

		int nRows = getRowCount(path);

		//profit

		Map<Double, Integer> map = new TreeMap<Double, Integer>();


		for(int i = 0; i < nRows; i++){

			if(fitIndex == 0){

				//double[] all = new double[nRows]; all[i] = getProfit(path, i);
				map.put(getProfit(path, i),i);
			}
			else if(fitIndex == 1){

				//double[] all = new double[nRows]; all[i] = getTrades(path, i);
				map.put((double)getTrades(path, i),i);
			}
			else if(fitIndex == 2){

				//double[] all = new double[nRows]; all[i] = getGoodness(path, i);
				map.put((double)getGoodness(path, i),i);
			}
			else if(fitIndex == 3){

				//double[] all = new double[nRows]; all[i] = getBSize(path, i);
				map.put((double)getBSize(path, i),i);

			}
			else if(fitIndex == 4){

				//double[] all = new double[nRows]; all[i] = getSSize(path, i);
				map.put((double)getSSize(path, i),i);

			}
			else if(fitIndex == 5){

				//double[] all = new double[nRows]; all[i] = getMinShare(path, i);
				map.put(getMinShare(path, i),i);

			}

		}//end for all rows, make map

		int i = 0;

		for(int h: map.values()){

			if(i == nIndividuals) break;
			fittest[i++] = h;

		}

		return fittest;

	}

	static TreeMap<Double, Integer> getFittestIndividuals(String symbol, int fitIndex, int nIndividuals) throws IOException{

		//0 == tProfit
		//1 == nTrades
		//2 == goodness
		//3 == sizeOfBuyRule
		//4 == sizeOfSellRule
		//5 == minShare

		//TODO: debug getProfit


		//ArrayList<Rule> good = new ArrayList<Rule>();

		String path = "src/rules/" + symbol + "_ruleList.csv";//.substring(symbol.indexOf('k')+3, symbol.length()) + 

		int nRows = getRowCount(path);

		//profit

		TreeMap<Double, Integer> map = new TreeMap<Double, Integer>();


		for(int i = 0; i < nRows; i++){

			if(fitIndex == 0){

				//double[] all = new double[nRows]; all[i] = getProfit(path, i);
				map.put(getProfit(path, i),i);
			}
			else if(fitIndex == 1){
				//	System.out.println("row index: " + i + " " + symbol);
				//double[] all = new double[nRows]; all[i] = getTrades(path, i);
				map.put((double)getTrades(path, i),i);
			}
			else if(fitIndex == 2){

				//double[] all = new double[nRows]; all[i] = getGoodness(path, i);
				map.put((double)getGoodness(path, i),i);
			}
			else if(fitIndex == 3){

				//double[] all = new double[nRows]; all[i] = getBSize(path, i);
				map.put((double)getBSize(path, i),i);

			}
			else if(fitIndex == 4){

				//double[] all = new double[nRows]; all[i] = getSSize(path, i);
				map.put((double)getSSize(path, i),i);

			}
			else if(fitIndex == 5){

				//double[] all = new double[nRows]; all[i] = getMinShare(path, i);
				map.put(getMinShare(path, i),i);

			}

		}//end for all rows, make map

		/*int i = 0;

		for(double h: map.keySet()){

			if(i == nIndividuals) break;
			fittest[i++] = h;

		}*/

		return map;

	}
	static TreeMap<Integer, Double> getFittestIndividuals(String symbol, int fitIndex, int nIndividuals, boolean good) throws IOException{

		//0 == tProfit
		//1 == nTrades
		//2 == goodness
		//3 == sizeOfBuyRule
		//4 == sizeOfSellRule
		//5 == minShare

		//TODO: debug getProfit


		//ArrayList<Rule> good = new ArrayList<Rule>();

		String path = "src/rules/" + symbol + "_ruleList.csv";//.substring(symbol.indexOf('k')+3, symbol.length()) + 

		int nRows = getRowCount(path);

		//profit

		TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();


		for(int i = 0; i < nRows; i++){

			if(fitIndex == 0){

				//double[] all = new double[nRows]; all[i] = getProfit(path, i);
				map.put(i, getProfit(path, i));
			}
			else if(fitIndex == 1){
				//System.out.println("row index: " + i + symbol + " : symbol");
				//double[] all = new double[nRows]; all[i] = getTrades(path, i);
				map.put(i, (double)getTrades(path, i));
			}
			else if(fitIndex == 2){

				//double[] all = new double[nRows]; all[i] = getGoodness(path, i);
				map.put(i, (double)getGoodness(path, i));
			}
			else if(fitIndex == 3){

				//double[] all = new double[nRows]; all[i] = getBSize(path, i);
				map.put(i, (double)getBSize(path, i));

			}
			else if(fitIndex == 4){

				//double[] all = new double[nRows]; all[i] = getSSize(path, i);
				map.put(i, (double)getSSize(path, i));

			}
			else if(fitIndex == 5){

				//double[] all = new double[nRows]; all[i] = getMinShare(path, i);
				map.put(i, getMinShare(path, i));

			}

		}//end for all rows, make map

		/*int i = 0;

		for(double h: map.keySet()){

			if(i == nIndividuals) break;
			fittest[i++] = h;

		}*/

		return map;

	}

	static TreeMap<String, Rule> iSellRuleNames(Vector<Rule> let){

		TreeMap<String, Rule> map = new TreeMap<String, Rule>();

		String[] sell = {"adxSell", "adxSell1", "adxSell2", "adiUnder", "adiCrossDown", "bollingerDownCross", "cciUnder100", "macdUnderZero", "macdCrossNegative", "ppoDown", "rocUnder", "rsiSell", "stoSell", "willSell", "willTrendDown"};

		for(int i = 0; i < sell.length; i++){

			map.put(sell[i], let.get(i));


		}//end for
		return map;
	}




	static HashMap<String, Rule> iBuyRuleNames(Vector<Rule> get){

		HashMap<String, Rule> map = new HashMap<String, Rule>();

		String[] buy = {"adxBear0", "addxBuy0", "adxBuy1", "adxBull0", "adxBull1", "adiOver", "adiCrossUp", "bollingerUpCross", "cciOver100", "macdOverZero", "macdCrossPositive", "ppoUp", "rocOver", "rsiBuy", "stoBuy", "willBuy", "willTrendUp"}; 

		for(int i = 0; i < buy.length; i++){

			map.put(buy[i], get.get(i));


		}//end for

		return map;


	}

	void iGenes(Peace p, TimeSeries series, Piece pc, double tolerance, int nGenes, double _mutationRate, int population, int _nBranchingFactor, TreeMap<String, Rule> b, TreeMap<String, Rule> s) throws IOException{



		l = new Love(pc, nGenes, tolerance, _mutationRate, population, _nBranchingFactor);




		//TradingRecord tr;


		// Close price
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

		// Typical price
		pc.typicalPrice = new TypicalPriceIndicator(series);

		// Price variation
		pc.priceVariation = new PriceVariationIndicator(series);


		// Simple moving averages
		pc.shortSma = new SMAIndicator(closePrice, pc.sSMA);

		// decvec.add(shortSma);

		pc.longSma = new SMAIndicator(closePrice, pc.lSMA);

		// decvec.add(longSma);

		// Exponential moving averages
		pc.shortEma = new EMAIndicator(closePrice, pc.sEMA);

		//    decvec.add(shortEma);

		pc.longEma = new EMAIndicator(closePrice, pc.lEMA);

		//    decvec.add(longEma);

		// Percentage price oscillator
		pc.ppo = new PPOIndicator(closePrice, pc.ppoS, pc.ppoL);

		//ema for ppo only

		pc.ppoema = new EMAIndicator(closePrice, pc.ema2P);

		//    decvec.add(ppo);

		// Rate of change
		pc.roc = new ROCIndicator(closePrice, pc.rocP);

		//   decvec.add(roc);

		// Relative strength index
		pc.rsi = new RSIIndicator(closePrice, pc.rsiP);

		//   decvec.add(rsi);

		// Williams %R
		pc.willR = new WilliamsRIndicator(series, pc.willP);
		//    decvec.add(williamsR);

		// Average true range
		pc.atr = new AverageTrueRangeIndicator(series, pc.atrP);
		//    decvec.add(atr);
		//  pc.atr = atr;
		// Standard deviation
		pc.sd = new StandardDeviationIndicator(closePrice, pc.devP);// Close price
		//   decvec.add(sd);

		pc.ade = new AccelerationDecelerationIndicator(series, pc.sSMA, pc.lSMA);

		//    decvec.add(ade);
		pc.adi = new AccumulationDistributionIndicator(series);

		//    decvec.add(adi);
		pc.arooD = new AroonDownIndicator(series, pc.aroonP);

		//    decvec.add(arooD);
		pc.arooU = new AroonUpIndicator(series, pc.aroonP);

		//    decvec.add(arooU);
		pc.admD = new AverageDirectionalMovementDownIndicator(series, pc.adxP);

		//    decvec.add(admD);
		pc.admU = new AverageDirectionalMovementUpIndicator(series, pc.adxP);

		//    decvec.add(admU);
		pc.adm = new AverageDirectionalMovementIndicator(series, pc.adxP);

		//	    decvec.add(adm);
		pc.ags= new AverageGainIndicator(closePrice, pc.sEMA);

		//    decvec.add(ags);
		pc.agl = new AverageGainIndicator(closePrice, pc.lEMA);

		//	    decvec.add(agl);
		pc.als= new AverageLossIndicator(closePrice, pc.sEMA);

		//    decvec.add(als);getTS
		pc.all = new AverageLossIndicator(closePrice, pc.lEMA);

		//    decvec.add(all);
		pc.atri = new AverageTrueRangeIndicator(series, pc.atrP); 

		//	    decvec.add(atri);
		pc.bbM = new BollingerBandsMiddleIndicator(closePrice); 

		//    decvec.add(bbm);
		pc.bbL = new BollingerBandsLowerIndicator(pc.bbM, closePrice); 

		//	    decvec.add(bbl);
		pc.bbU = new BollingerBandsUpperIndicator(pc.bbM, closePrice); 

		//	    decvec.add(bbu);
		pc.cci = new CCIIndicator(series, pc.cciP);
		;
		//	    decvec.add(cci);
		pc.dmD = new DirectionalMovementDownIndicator(series);

		//	    decvec.add(dmd);
		pc.dmU = new DirectionalMovementUpIndicator(series);

		//	    decvec.add(dmu);
		pc.dm = new DirectionalMovementIndicator(series, pc.adxP); 

		pc.dxD = new DirectionalDownIndicator(series, pc.adxP);

		//    decvec.add(ddi);
		pc.dxU = new DirectionalUpIndicator(series, pc.adxP);

		//    decvec.add(dui);
		pc.dubE = new DoubleEMAIndicator(closePrice, pc.ema2P);

		//    decvec.add(dema);
		pc.triE = new TripleEMAIndicator(closePrice, pc.ema3P);

		//	    decvec.add(tema);
		pc.macd = new MACDIndicator(closePrice, pc.macdS, pc.macdL);

		//	    decvec.add(macd);
		pc.meanDev = new MeanDeviationIndicator(closePrice, pc.devP);

		//	    decvec.add(meandev);
		pc.obv = new OnBalanceVolumeIndicator(series);

		//    decvec.add(obv);
		pc.sar = new ParabolicSarIndicator(series, pc.sarP);


		pc.stoKi = new StochasticOscillatorKIndicator(series, pc.stoK);

		pc.stoDi = new StochasticOscillatorDIndicator(pc.stoKi);


		pc.smoothedDXD = new SMAIndicator(pc.admD, pc.smoothingPeriod); 


		pc.smoothedDXU = new SMAIndicator(pc.admU, pc.smoothingPeriod);



		//    decvec.add(sar);
		pc.wma = new WMAIndicator(closePrice, pc.wmaP); 

		//	    decvec.add(wma);
		pc.tri = new TrueRangeIndicator(series);

		//	    decvec.add(tr);
		pc.typ = new TypicalPriceIndicator(series); 

		//	    decvec.add(typ);




		//enumerated Decimal values 


		//   Vector<Decimal> parameters = new Vector<Decimal>();




		//adx 

		//Rule adxHold = new UnderIndicatorRule(adm, threshold1);
		//.add(adxHold);
		//enumerated link key:
		//1 == AND, 2 == OR, 3 == XOR, 4 == NOT, 5 == PIPE, 6 == UNDER, 7 == OVER, 8 == CROSSUP, 9 == CROSSDOWN
		//10 == FIXED, 11 == StopGain, 12 == StopLoss, 13 == WaitFor, 14 == JustOnce, 15 == BooleanIndicator
		//16 == Boolean, 17 == Abstract

		//enumerated indicator values
		//1 Average Directional Move Down, 2 AverageDirectionalMoveUp
		//3 Average Gain, 4 Average Gain, 5 AverageLoss
		//6 Cross, 7 DirectionalDown, 8 DirectionalMoveDown, 9 DirectionalMoveUp, 10 DirectionalUp
		//11 HighestValue, 12 LowestValue, 13 MeanDeviation, 14 StandardDev, 15 TrueRange
		//16 AroonUp, 17 AroonDown, 18 Awesome, 18 CCI, 19 PPO, 
		//20 StochasticOscD, 21 StochsticOscK
		//22 AccelDeccel, 23 AvDirectionalMove, 24 DirectionalMove, 25 DoubleEMA, 26 EMA, 27 MACD, 28 Parabolic
		//29 ROC, 30 RSI, 31 SMA, 32 TripleEMA, 33 WilliamsR, 34 WMA
		//35 BBLow, 36 BBHigh, 37 BBMid
		//38 AccumDistribut, 39 OBV
		//0 Abstract, 40 Cached
		//41 threshold1, 42 threshold2
		//43 smoothed DXU, 44 smoothed DXD
		//45 origin, 46 closePrice, 47 PPOEMA

		//48 unit, 49 rsiLowBound, 50 rsiUpBound
		//51 pos100, 52 neg100, 53 stoUBound, 54 stoLBound
		//55 willUBound, 56 willLBound, 
		//cart
		//threshold1, threshold2, origin, pos100, neg100, stoUB, stoLB, willU, willL, rsiU, rsiL, 

		//0
		Decimal origin = Decimal.valueOf(0);
		pc.origin = origin;
		//  parameters.add(origin);
		//1
		Decimal unit = Decimal.valueOf(1);
		pc.unit = unit;
		// parameters.add(unit);
		//etc


		Decimal positive100 = Decimal.valueOf(100);
		pc.positive100 = positive100;
		//  parameters.add(positive100);
		Decimal negative100 = Decimal.valueOf(-100);
		pc.negative100 = negative100;
		//  parameters.add(negative100);
		Random ran = new Random();



		pc.arUB = ran.nextInt(15)+75; 
		Decimal arooUB = Decimal.valueOf(pc.arUB);
		pc.arooUB = arooUB;
		pc.arLB = ran.nextInt(10)+15;
		Decimal arooLB = Decimal.valueOf(pc.arLB);
		pc.arooLB = arooLB;

		pc.stoU = ran.nextInt(15)+75; 
		Decimal stoUBound = Decimal.valueOf(pc.stoU);
		pc.stoUBound = stoUBound;
		pc.stoDn = ran.nextInt(10)+15;
		Decimal stoLBound = Decimal.valueOf(pc.stoDn);
		pc.stoLBound = stoLBound;
		//parameters.add(stoLBound);

		//adx
		pc.adxD = ran.nextInt(5) + 20;
		Decimal threshold1 = Decimal.valueOf(pc.adxD);
		pc.threshold1 = threshold1;
		//parameters.add(threshold1);
		pc.adxU = ran.nextInt(10) + 20;
		Decimal threshold2 = Decimal.valueOf(pc.adxU);
		pc.threshold2 = threshold2;
		//parameters.add(threshold2);

		//rsi

		pc.rsiL = ran.nextInt(20) + 20;
		Decimal rsiLowerBound = Decimal.valueOf(pc.rsiL);
		pc.rsiLowerBound = rsiLowerBound;
		pc.rsiU = ran.nextInt(20) + 60;
		Decimal rsiUpperBound = Decimal.valueOf(pc.rsiU);
		pc.rsiUpperBound = rsiUpperBound;

		//willR

		pc.willD = ran.nextInt(20) + 10;
		Decimal willLBound = Decimal.valueOf(pc.willD);
		pc.willLBound = willLBound;
		pc.willU = ran.nextInt(20) + 70;
		Decimal willUBound = Decimal.valueOf(pc.willU);
		pc.willUBound = willUBound;





		Rule adxSell = new UnderIndicatorRule(pc.admU, pc.admD);

		short ruleIndex = 0;
		short logic = 6;
		short i1 = 2;
		short i2 = 1;

		Genie g0 = new Genie("adxSell", adxSell,  logic, i1, i2, ruleIndex);

		p.sellG.add(g0);
		let.add(adxSell);
		sellRuleSet.add(adxSell);
		s.put("adxSell", adxSell);



		Rule adxBear0 = new OverIndicatorRule(pc.admD, pc.admU);
		ruleIndex = 1;
		logic = 7;
		i1 = 1;
		i2 = 2;

		Genie g1 = new Genie("adxBear0", adxBear0, logic, i1, i2, ruleIndex);


		p.buyG.add(g1);
		g1.previous = g0;
		g0.next = g1;
		b.put("adxBear0", adxBear0);
		get.add(adxBear0);
		buyRuleSet.add(adxBear0);


		Rule adxBuy0 = new CrossedUpIndicatorRule(pc.admU, pc.admD);

		ruleIndex = 2;
		logic = 8;
		i1 = 2;
		i2 = 1;

		Genie g2 = new Genie("adxBuy0", adxBuy0,  logic, i1, i2, ruleIndex);


		p.buyG.add(g2);
		g1.next = g2;
		g2.previous = g1;
		b.put("adxBuy0", adxBuy0);
		get.add(adxBuy0);
		buyRuleSet.add(adxBuy0);

		Rule adxBuy1 = new CrossedUpIndicatorRule(pc.smoothedDXU, pc.smoothedDXD);

		ruleIndex =3;
		logic = 8;
		i1 = 43;
		i2 = 44;

		Genie g3 = new Genie("adxBuy1", adxBuy1,  logic, i1, i2, ruleIndex);
		p.buyG.add(g3);
		g2.next = g3;
		g3.previous = g2; 
		b.put("adxBuy1", adxBuy1);
		get.add(adxBuy1);
		buyRuleSet.add(adxBuy1);


		Rule adxSell1 = new CrossedDownIndicatorRule(pc.smoothedDXU, pc.smoothedDXD);

		ruleIndex =4;
		logic = 9;
		i1 = 43;
		i2 = 44;

		Genie g4 = new Genie("adxSell1", adxSell1,  logic, i1, i2, ruleIndex);
		p.sellG.add(g4);
		g3.next = g4;
		g4.previous = g3; 
		let.add(adxSell1);
		sellRuleSet.add(adxSell1);
		s.put("adxSell1", adxSell1);

		Rule adxSell2 = new CrossedDownIndicatorRule(pc.admD, pc.admU);
		ruleIndex = 5;
		logic = 9;
		i1 = 1;
		i2 = 2;

		Genie g5 = new Genie("adxSell2", adxSell2,  logic, i1, i2, ruleIndex);
		p.sellG.add(g5);
		g4.next = g5;
		g5.previous = g4; 
		s.put("adxSell2", adxSell2);
		let.add(adxSell2);
		sellRuleSet.add(adxSell2);

		Rule adxBull0 = new OverIndicatorRule(pc.adm, pc.threshold2);
		ruleIndex =6;
		logic = 7;
		i1 = 23;
		i2 = 42;

		Genie g6 = new Genie("adxBull0", adxBull0,  logic, i1, i2, ruleIndex);
		p.buyG.add(g6);
		g5.next = g6;
		g6.previous = g5; 
		b.put("adxBull0", adxBull0);
		get.add(adxBull0);
		buyRuleSet.add(adxBull0);

		Rule adxBull1 = new OverIndicatorRule(pc.admU, pc.admD);

		ruleIndex =7;
		logic = 7;
		i1 = 2;
		i2 = 1;

		Genie g7 = new Genie("adxBull1", adxBull1,  logic, i1, i2, ruleIndex);
		p.buyG.add(g7);
		g6.next = g7;
		g7.previous = g6; 
		b.put("adxBull1", adxBull1);
		get.add(adxBull1);
		buyRuleSet.add(adxBull1);

		//acceleration / deceleration indicator

		//TODO: add fitness function that looks for accel decel pattern

		Rule adeOver = new OverIndicatorRule(pc.ade, pc.origin);
		//.add(adeOver);

		Rule adeUnder = new UnderIndicatorRule(pc.ade, pc.origin);
		//rules.add(adeUnder);
		//TODO: when ade buy or sell is crossed, check to see if it occurs during a period when the price volatility
		//is consistent with patterns that are described in the notebook.

		//accumulation/distribution line

		Rule adiOver = new OverIndicatorRule(pc.adi, pc.origin);

		ruleIndex=8;
		logic = 7;

		i1 = 38;
		i2 = 45;


		Genie g8 = new Genie("adiOver", adiOver, logic, i1, i2, ruleIndex);
		p.buyG.add(g8);

		b.put("adiOver", adiOver);
		get.add(adiOver);
		buyRuleSet.add(adiOver);

		Rule adiUnder = new UnderIndicatorRule(pc.adi, pc.origin);
		ruleIndex=9;
		logic = 6;
		i1 = 38;
		i2 = 45;

		Genie g9 = new Genie("adiUnder", adiUnder, logic, i1, i2, ruleIndex);
		p.sellG.add(g9);
		s.put("adiUnder", adiUnder);
		let.add(adiUnder);
		sellRuleSet.add(adiUnder);

		Rule adiCrossUp = new CrossedUpIndicatorRule(pc.adi, pc.origin);
		ruleIndex=10;
		logic = 8;
		i1 = 38;
		i2 = 45;


		Genie g10 = new Genie("adxCrossUp", adiCrossUp, logic, i1, i2, ruleIndex);
		p.buyG.add(g10);
		get.add(adiCrossUp);
		buyRuleSet.add(adiCrossUp);
		b.put("adxCrossUp", adiCrossUp);

		Rule adiCrossDown = new CrossedDownIndicatorRule(pc.adi, pc.origin);
		ruleIndex=11;
		logic = 9;
		i1 = 38;
		i2 = 45;


		Genie g11 = new Genie("adiCrossDown", adiCrossDown, logic, i1, i2, ruleIndex);
		p.sellG.add(g11);

		s.put("adiCrossDown", adiCrossDown);
		let.add(adiCrossDown);
		sellRuleSet.add(adiCrossDown);


		//bollinger bands

		Rule bollingerUpCross = new CrossedUpIndicatorRule(pc.closePrice, pc.bbM);
		ruleIndex=12;
		logic = 8;
		i1 = 46;
		i2 = 37;

		Genie g12 = new Genie("bollingerUpCross", bollingerUpCross, logic, i1, i2, ruleIndex);
		p.buyG.add(g12);
		b.put("bollingerUpCross", bollingerUpCross);

		get.add(bollingerUpCross);
		buyRuleSet.add(bollingerUpCross);

		Rule bollingerDownCross = new CrossedDownIndicatorRule(closePrice, pc.bbM);

		ruleIndex=13;
		logic = 9;
		i2 = 37;
		i1 = 46;


		Genie g13 = new Genie("bollingerDownCross", bollingerDownCross, logic, i1, i2, ruleIndex);
		p.sellG.add(g13);
		s.put("bollingerDownCross",bollingerDownCross);

		let.add(bollingerDownCross);
		sellRuleSet.add(bollingerDownCross);
		//commodity channel index


		Rule cciOver100 = new CrossedUpIndicatorRule(pc.cci, pc.positive100);
		ruleIndex=14;
		logic = 8;
		i1 = 18;
		i2 = 51;

		Genie g14 = new Genie("cciOver100", cciOver100, logic, i1, i2, ruleIndex);
		p.buyG.add(g14);
		b.put("cciOver100", cciOver100);

		get.add(cciOver100);
		buyRuleSet.add(cciOver100);

		Rule cciUnder100 = new CrossedDownIndicatorRule(pc.cci, pc.negative100);
		ruleIndex=15;
		logic = 9;
		i1 = 18;
		i2 = 52;

		Genie g15 = new Genie("cciUnder100", cciUnder100, logic, i1, i2, ruleIndex);
		p.sellG.add(g15);
		s.put("cciUnder100", cciUnder100);

		let.add(cciUnder100);
		sellRuleSet.add(cciUnder100);
		//ema-macd

		Rule macdOverZero = new OverIndicatorRule(pc.macd, pc.origin);//uptrend strength
		ruleIndex=16;
		i1 = 27;
		i2 = 45;		   
		logic = 7;


		Genie g16 = new Genie("macdOverZero", macdOverZero, logic, i1, i2, ruleIndex);
		p.buyG.add(g16);
		b.put("macdOverZero", macdOverZero);

		get.add(macdOverZero);
		buyRuleSet.add(macdOverZero);

		Rule macdUnderZero = new UnderIndicatorRule(pc.macd, pc.origin);//downtrend strength
		ruleIndex=17;
		logic = 6;
		i1 = 27;
		i2 = 45;


		Genie g17 = new Genie("macdUnderZero", macdUnderZero, logic, i1, i2, ruleIndex);
		p.sellG.add(g17);
		s.put("macdUnderZero", macdUnderZero);

		let.add(macdUnderZero);
		sellRuleSet.add(macdUnderZero);
		Rule macdCrossPositive = new CrossedUpIndicatorRule(pc.macd, pc.origin);//move to positive
		ruleIndex=18;
		logic = 8;
		i1 = 27;
		i2 = 45;

		Genie g18 = new Genie("macdCrossPositive", macdCrossPositive, logic, i1, i2, ruleIndex);
		p.buyG.add(g18);
		b.put("macdCrossPositive", macdCrossPositive);
		//	   g17.next = g18;
		//   g18.previous = g17; 

		get.add(macdCrossPositive);
		buyRuleSet.add(macdCrossPositive);

		Rule macdCrossNegative = new CrossedDownIndicatorRule(pc.macd, pc.origin);//move to negative
		ruleIndex=19;
		logic = 9;
		i1 = 27;
		i2 = 45;


		Genie g19 = new Genie("macdCrossNegative", macdCrossNegative, logic, i1, i2, ruleIndex);
		p.sellG.add(g19);
		sellRuleSet.add(macdCrossNegative);
		s.put("macdCrossNegative", macdCrossNegative);
		let.add(macdCrossNegative);
		//on balance volume

		//TODO:better rules for obv trend analysis

		//  Rule obvPositive = new OverIndicatorRule(pc.obv, pc.origin);


		//.add(obvPositive);
		//  Rule obvNegative = new UnderIndicatorRule(pc.obv, pc.origin);


		//rules.add(obvNegative);
		//ppo



		CrossedUpIndicatorRule ppoUp = new CrossedUpIndicatorRule(pc.ppo, pc.ppoema);//buy

		ruleIndex=20;

		logic = 8;

		i2 = 47;
		i1 = 19;

		Genie g20 = new Genie("ppoUp", ppoUp, logic, i1, i2, ruleIndex);

		p.buyG.add(g20);
		b.put("ppoUp", ppoUp);

		get.add(ppoUp);
		buyRuleSet.add(ppoUp);

		CrossedDownIndicatorRule ppoDown = new CrossedDownIndicatorRule(pc.ppo, pc.ppoema);//sell

		logic = 9;
		i2 = 47;
		i1 = 19;
		ruleIndex=21;


		Genie g21 = new Genie("ppoDown", ppoDown, logic, i1, i2, ruleIndex);
		p.sellG.add(g21);
		s.put("ppoDown", ppoDown);
		//	   g20.next = g21;
		//	   g21.previous = g20; 

		let.add(ppoDown);
		sellRuleSet.add(ppoDown);


		//roc

		Rule rocOver = new OverIndicatorRule(pc.roc, pc.unit);//buy signal
		ruleIndex=22;
		logic = 7;
		i1 = 29;
		i2 = 48;

		Genie g22 = new Genie("rocOver", rocOver, logic, i1, i2, ruleIndex);
		p.buyG.add(g22);
		b.put("rocOver", rocOver);
		//	   g21.next = g22;
		//	   g22.previous = g21; 

		get.add(rocOver);
		buyRuleSet.add(rocOver);

		Rule rocUnder = new UnderIndicatorRule(pc.roc, pc.unit);//sell signal
		ruleIndex=23;
		logic = 6;
		i1 = 29;
		i2 = 48;
		Genie g23 = new Genie("rocUnder", rocUnder, logic, i1, i2, ruleIndex);
		p.sellG.add(g23);
		s.put("rocUnder", rocUnder);

		let.add(rocUnder);
		sellRuleSet.add(rocUnder);
		//rsi


		Rule rsiBuy = new UnderIndicatorRule(pc.rsi, pc.rsiLowerBound);
		ruleIndex=24;
		logic = 6;
		i1 = 30;
		i2 = 49;

		Genie g24 = new Genie("rsiBuy", rsiBuy, logic, i1, i2, ruleIndex);
		p.buyG.add(g24);
		b.put("rsiBuy", rsiBuy);

		get.add(rsiBuy);
		buyRuleSet.add(rsiBuy);

		Rule rsiSell = new UnderIndicatorRule(pc.rsi, pc.rsiUpperBound);
		ruleIndex=25;
		logic = 6;
		i1 = 30;
		i2 = 50;
		Genie g25 = new Genie("rsiSell", rsiSell, logic, i1, i2, ruleIndex);
		p.sellG.add(g25);
		s.put("rsiSell", rsiSell);

		let.add(rsiSell);
		sellRuleSet.add(rsiSell);

		//stochastic oscillator

		Rule stoBuy = new UnderIndicatorRule(pc.stoDi, pc.stoUBound); 
		logic = 6;
		i1 = 20;
		i2 = 53;
		ruleIndex=26;

		Genie g26 = new Genie("stoBuy", stoBuy, logic, i1, i2, ruleIndex);
		p.buyG.add(g26);
		b.put("stoBuy", stoBuy);

		get.add(stoBuy);
		buyRuleSet.add(stoBuy);

		Rule stoSell = new OverIndicatorRule(pc.stoDi, pc.stoLBound);
		ruleIndex=27;
		logic = 7;
		i1 = 20;
		i2 = 54;

		Genie g27 = new Genie("stoSell", stoSell, logic, i1, i2, ruleIndex);
		p.sellG.add(g27);
		s.put("stoSell", stoSell);

		let.add(stoSell);
		sellRuleSet.add(stoSell);

		//williams r

		Rule willSell = new UnderIndicatorRule(pc.willR, pc.willLBound);
		ruleIndex=28;
		logic = 6;
		i1 = 33;
		i2 = 56;

		Genie g28 = new Genie("willSell", willSell, logic, i1, i2, ruleIndex);
		p.sellG.add(g28);
		s.put("willSell", willSell);
		let.add(willSell);
		sellRuleSet.add(willSell);

		Rule willBuy = new UnderIndicatorRule(pc.willR, pc.willUBound);
		ruleIndex=29;
		logic = 6;
		i1 = 33;
		i2 = 55;

		Genie g29 = new Genie("willBuy", willBuy, logic, i1, i2, ruleIndex);
		p.buyG.add(g29);
		b.put("willBuy", willBuy);
		get.add(willBuy);
		buyRuleSet.add(willBuy);

		Rule willTrendDown = new CrossedDownIndicatorRule(pc.closePrice, pc.willUBound);
		logic = 9;
		i1 = 46;
		i2 = 55;
		ruleIndex=30;


		Genie g30 = new Genie("willTrendDown", willTrendDown, logic, i1, i2, ruleIndex);
		p.sellG.add(g30);
		s.put("willTrendDown", willTrendDown);
		let.add(willTrendDown);
		sellRuleSet.add(willTrendDown);

		Rule willTrendUp = new CrossedUpIndicatorRule(pc.closePrice, pc.willLBound);
		ruleIndex=31;
		logic = 8;
		i1 = 46;
		i2 = 56;

		Genie g31 = new Genie("willTrendUp", willTrendUp, logic, i1, i2, ruleIndex);
		p.buyG.add(g31);
		b.put("willTrendUp", willTrendUp);
		get.add(willTrendUp);
		buyRuleSet.add(willTrendUp);

		Rule aroonBuy1 = new CrossedUpIndicatorRule(pc.arooU, pc.arooD);

		ruleIndex =32;
		logic = 8;
		i1 = 16;
		i2 = 17;
		/* 
		   Genie g32 = new Genie("aroonBuy1", aroonBuy1,  logic, i1, i2, ruleIndex);
		   p.buyG.add(g3);
		   g2.next = g3;
		   g3.previous = g2; 
		   b.put("aroonBuy1", aroonBuy1);
	      get.add(aroonBuy1);
	      buyRuleSet.add(aroonBuy1);


	      Rule aroonSell1 = new CrossedDownIndicatorRule(pc.arooU, pc.arooD);

	      ruleIndex =33;
		   logic = 9;
		   i1 = 16;
		   i2 = 17;

		   Genie g33 = new Genie("aroonSell1", aroonSell1,  logic, i1, i2, ruleIndex);
		   p.sellG.add(g4);
		   g3.next = g4;
		   g4.previous = g3; 
	      let.add(aroonSell1);
	      sellRuleSet.add(aroonSell1);
	      s.put("aroonSell1", aroonSell1);
		 */


		Rule buy = null;
		Rule sell = null;

		Random r = new Random();
		int mutantTest = 0;

		String buyRule = "";
		String sellRule = "";

		greater = p.buyG;
		lesser = p.sellG;


		for(int i = 0; i < l.popSize; i++){

			mutantTest = r.nextInt(100);

			Random o = new Random();
			int mut = o.nextInt(30)+5;
			if(mutantTest % mut == 0) {
				// System.out.println("mutating");
				l.mutate(nGenes/2+1);
			}

			//TODO: mutate, do crossover with buy's and sells, fitness function, generation number

			//   l.generateI(get);

			buy = l.arrayToRule(get, p.buyG);
			buyRuleSet.add(buy);
			buyRule = l.ruleString;
			buyRules.add(buyRule);

			// l.generateI(let);


			l.ruleString = "";//clear cache

			sell = l.arrayToRule(let, p.sellG);
			sellRuleSet.add(sell);
			sellRule = l.ruleString;
			sellRules.add(sellRule);









		} 		


	}

	static Rule ruleFromGenie(LinkedList<Genie> g, TreeMap<String, Rule> t, Piece p){
		int type = g.get(0).logic;
		Rule r= null;
		Rule j = null;
		short indicator = g.get(0).indicator1;
		//	System.out.println(indicator + ": indicator 1");
		//  System.out.println(type + ": type of rule");
		//short threshold = g.get(0).indicator2;

		switch(indicator){

		case(1):{

			switch(type){

			case(6):{

				r = new UnderIndicatorRule(p.admU, p.admD);

			}
			case(7):{

				r = new OverIndicatorRule(p.admU, p.admD);

			}
			case(8):{

				r = new CrossedUpIndicatorRule(p.admU, p.admD);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.admU, p.admD);

			}
			}
		}//end 1
		case(2):{

			switch(type){

			case(6):{

				r = new UnderIndicatorRule(p.admD, p.admU);

			}
			case(7):{

				r = new OverIndicatorRule(p.admD, p.admU);

			}
			case(8):{

				r = new CrossedUpIndicatorRule(p.admD, p.admU);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.admD, p.admU);

			}
			}
		}//end 2
		case(43):{

			switch(type){

			case(6):{

				r = new UnderIndicatorRule(p.smoothedDXU, p.smoothedDXD);

			}
			case(7):{

				r = new OverIndicatorRule(p.smoothedDXU, p.smoothedDXD);

			}
			case(8):{

				r = new CrossedUpIndicatorRule(p.smoothedDXU, p.smoothedDXD);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.smoothedDXU, p.smoothedDXD);

			}
			}
		}//end 43
		case(44):{

			switch(type){

			case(6):{

				r = new UnderIndicatorRule(p.smoothedDXD, p.smoothedDXU);

			}
			case(7):{
				r = new OverIndicatorRule(p.smoothedDXD, p.smoothedDXU);

			}
			case(8):{

				r = new CrossedUpIndicatorRule(p.smoothedDXD, p.smoothedDXU);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.smoothedDXD, p.smoothedDXU);

			}
			}
		}//end 44
		case(23):{

			switch(type){

			case(6):{

				r = new UnderIndicatorRule(p.adm, p.threshold2);

			}
			case(7):{

				r = new OverIndicatorRule(p.adm, p.threshold2);

			}
			case(8):{

				r = new CrossedUpIndicatorRule(p.adm, p.threshold2);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.adm, p.threshold2);

			}
			}
		}//end 23
		case(38):{

			switch(type){

			case(6):{

				r = new UnderIndicatorRule(p.adi, p.origin);

			}
			case(7):{

				r = new OverIndicatorRule(p.adi, p.origin);

			}
			case(8):{

				r = new CrossedUpIndicatorRule(p.adi, p.origin);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.adi, p.origin);

			}
			}
		}//end 38
		case(46):{

			switch(type){
			case(6):{

				r = new UnderIndicatorRule(p.adi, p.origin);
			}
			case(7):{

				r = new OverIndicatorRule(p.adi, p.origin);
			}
			case(8):{

				short c = g.get(0).index;

				if(c == 13){
					r = new CrossedUpIndicatorRule(p.adi, p.bbM);
				}
				else {
					r = new CrossedUpIndicatorRule(p.adi, p.origin);
				}

			}
			case(9):{
				short c = g.get(0).index;

				if(c ==12){
					r =	new CrossedDownIndicatorRule(p.adi, p.bbM);
				}
				else{
					r = new CrossedDownIndicatorRule(p.adi, p.origin);
				}

			}
			}
		}//end 46
		case(18):{

			switch(type){

			case(8):{

				r = new CrossedUpIndicatorRule(p.cci, p.positive100);

			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.cci, p.negative100);

			}
			}
		}//end 18
		case(27):{

			switch(type){
			case(6):{

				r = new UnderIndicatorRule(p.macd, p.origin);
			}
			case(7):{

				r = new OverIndicatorRule(p.macd, p.origin);
			}
			case(8):{


				r = new CrossedUpIndicatorRule(p.macd, p.origin);



			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.macd, p.origin);
			}


			}
		}//end 27
		case(19):{

			switch(type){
			case(6):{

				r = new UnderIndicatorRule(p.ppo, p.ppoema);
			}
			case(7):{

				r = new OverIndicatorRule(p.ppo, p.ppoema);
			}
			case(8):{


				r = new CrossedUpIndicatorRule(p.ppo, p.ppoema);



			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.ppo, p.ppoema);
			}


			}
		}//end 19
		case(29):{

			switch(type){
			case(6):{

				r = new UnderIndicatorRule(p.roc, p.unit);
			}
			case(7):{

				r = new OverIndicatorRule(p.roc, p.unit);
			}
			case(8):{


				r = new CrossedUpIndicatorRule(p.roc, p.unit);



			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.roc, p.unit);
			}


			}
		}//end 29
		case(30):{

			switch(type){
			case(6):{

				r = new UnderIndicatorRule(p.rsi, p.rsiUpperBound);
			}
			case(7):{

				r = new OverIndicatorRule(p.rsi, p.rsiLowerBound);
			}
			case(8):{


				r = new CrossedUpIndicatorRule(p.rsi, p.rsiUpperBound);



			}
			case(9):{

				r = new CrossedDownIndicatorRule(p.rsi, p.rsiLowerBound);
			}


			}
		}//end 30
		case(33):{

			switch(type){
			case(6):{

				if(g.get(0).index == 28) {
					r = new UnderIndicatorRule(p.willR, p.rsiLowerBound);
				}
				else{
					r = new UnderIndicatorRule(p.willR, p.rsiUpperBound);
				}
			}





			}
		}//end 33

		}//end switch indicator	
		for(int i = 1; i < g.size();i++){

			type = g.get(i).logic;

			indicator = g.get(i).indicator1;

			switch(indicator){

			case(1):{

				switch(type){

				case(6):{

					j = new UnderIndicatorRule(p.admU, p.admD);

				}
				case(7):{

					j= new OverIndicatorRule(p.admU, p.admD);

				}
				case(8):{

					j= new CrossedUpIndicatorRule(p.admU, p.admD);

				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.admU, p.admD);

				}
				}
			}//end 1
			case(2):{

				switch(type){

				case(6):{

					j = new UnderIndicatorRule(p.admD, p.admU);

				}
				case(7):{

					j= new OverIndicatorRule(p.admD, p.admU);

				}
				case(8):{

					j = new CrossedUpIndicatorRule(p.admD, p.admU);

				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.admD, p.admU);

				}
				}
			}//end 2
			case(43):{

				switch(type){

				case(6):{

					j = new UnderIndicatorRule(p.smoothedDXU, p.smoothedDXD);

				}
				case(7):{

					j = new OverIndicatorRule(p.smoothedDXU, p.smoothedDXD);

				}
				case(8):{

					j = new CrossedUpIndicatorRule(p.smoothedDXU, p.smoothedDXD);

				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.smoothedDXU, p.smoothedDXD);

				}
				}
			}//end 43
			case(44):{

				switch(type){

				case(6):{

					j = new UnderIndicatorRule(p.smoothedDXD, p.smoothedDXU);

				}
				case(7):{

					j = new OverIndicatorRule(p.smoothedDXD, p.smoothedDXU);

				}
				case(8):{

					j = new CrossedUpIndicatorRule(p.smoothedDXD, p.smoothedDXU);

				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.smoothedDXD, p.smoothedDXU);

				}
				}
			}//end 44
			case(23):{

				switch(type){

				case(6):{

					j= new UnderIndicatorRule(p.adm, p.threshold2);

				}
				case(7):{

					j= new OverIndicatorRule(p.adm, p.threshold2);

				}
				case(8):{

					j = new CrossedUpIndicatorRule(p.adm, p.threshold2);

				}
				case(9):{

					j= new CrossedDownIndicatorRule(p.adm, p.threshold2);

				}
				}
			}//end 23
			case(38):{

				switch(type){

				case(6):{

					j = new UnderIndicatorRule(p.adi, p.origin);

				}
				case(7):{

					j = new OverIndicatorRule(p.adi, p.origin);

				}
				case(8):{

					j = new CrossedUpIndicatorRule(p.adi, p.origin);

				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.adi, p.origin);

				}
				}
			}//end 38
			case(46):{

				switch(type){
				case(6):{

					j = new UnderIndicatorRule(p.adi, p.origin);
				}
				case(7):{

					j = new OverIndicatorRule(p.adi, p.origin);
				}
				case(8):{

					short c = g.get(i).index;

					if(c == 13){
						j = new CrossedUpIndicatorRule(p.adi, p.bbM);
					}
					else {
						j = new CrossedUpIndicatorRule(p.adi, p.origin);
					}

				}
				case(9):{
					short c = g.get(i).index;

					if(c ==12){
						j = new CrossedDownIndicatorRule(p.adi, p.bbM);
					}
					else{
						j = new CrossedDownIndicatorRule(p.adi, p.origin);
					}

				}
				}
			}//end 46
			case(18):{

				switch(type){

				case(8):{

					j = new CrossedUpIndicatorRule(p.cci, p.positive100);

				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.cci, p.negative100);

				}
				}
			}//end 18
			case(27):{

				switch(type){
				case(6):{

					j = new UnderIndicatorRule(p.macd, p.origin);
				}
				case(7):{

					j = new OverIndicatorRule(p.macd, p.origin);
				}
				case(8):{


					j= new CrossedUpIndicatorRule(p.macd, p.origin);



				}
				case(9):{

					j= new CrossedDownIndicatorRule(p.macd, p.origin);
				}


				}
			}//end 27
			case(19):{

				switch(type){
				case(6):{

					j= new UnderIndicatorRule(p.ppo, p.ppoema);
				}
				case(7):{

					j = new OverIndicatorRule(p.ppo, p.ppoema);
				}
				case(8):{


					j= new CrossedUpIndicatorRule(p.ppo, p.ppoema);



				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.ppo, p.ppoema);
				}


				}
			}//end 19
			case(29):{

				switch(type){
				case(6):{

					j = new UnderIndicatorRule(p.roc, p.unit);
				}
				case(7):{

					j = new OverIndicatorRule(p.roc, p.unit);
				}
				case(8):{


					j= new CrossedUpIndicatorRule(p.roc, p.unit);



				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.roc, p.unit);
				}


				}
			}//end 29
			case(30):{

				switch(type){
				case(6):{

					j= new UnderIndicatorRule(p.rsi, p.rsiUpperBound);
				}
				case(7):{

					j = new OverIndicatorRule(p.rsi, p.rsiLowerBound);
				}
				case(8):{


					j = new CrossedUpIndicatorRule(p.rsi, p.rsiUpperBound);



				}
				case(9):{

					j = new CrossedDownIndicatorRule(p.rsi, p.rsiLowerBound);
				}


				}
			}//end 30
			case(33):{

				switch(type){
				case(6):{

					if(g.get(i).index == 28) {
						j = new UnderIndicatorRule(p.willR, p.rsiLowerBound);

					}
					else{
						j = new UnderIndicatorRule(p.willR, p.rsiUpperBound);
					}
				}





				}
			}//end 33



			if(j == null)System.out.println("j is null");
			//if(r == null)System.out.println("r is null");

			int logic = g.get(i-1).bool;

			switch(logic){


			case(0): {

				r = r.and(j);

			}

			case(1): {



				r = r.or(j);

			}

			case(2): {

				r = r.xor(j);
			}

			case(3): {


				Rule notRule = new NotRule(j);


				r = r.and(notRule);

			}

			case(4): {


				Rule notRule = new NotRule(j);


				r = r.or(notRule);

			}

			}//end switch boolean connectors





			}//end for each member of linked list
			r = j;
		}
		return r;
	}
	/*
static Rule ruleFromGenie(LinkedList<Genie> g, int[] logics){
	//System.out.println(map.size() + " size of map");
	System.out.println(g.size() + ": size of genie list");
	Rule newRule = g.get(0).r;

	for(int i = 1; i < logics.length;i++){

		int logic = logics[i];

		switch(logic){


		case(0): {

			newRule = newRule.and(g.get(i).r);

		}

		case(1): {

			newRule = newRule.or(g.get(i).r);

		}

		case(2): {


			newRule = newRule.xor(g.get(i).r);
		}

		case(3): {


			Rule notRule = new NotRule(g.get(i).r);


			newRule = newRule.and(notRule);

		}

		case(4): {

			Rule notRule = new NotRule(g.get(i).r);


			newRule = newRule.or(notRule);

		}


		}//end switch/case



	}//end for each member of linked list
	return newRule;
}
	 */


	Decimal getAroonOsc(Indicator<Decimal> up, Indicator<Decimal> down, int index){

		double aroonOsc = up.getValue(index).toDouble() - down.getValue(index).toDouble();

		Decimal d;

		d = Decimal.valueOf(aroonOsc);

		return d;

	}



	/*
Rule generateRule(boolean trade, int[] pieces, Rule currentRule, double fitness){



	long seed = pieces.length;

	Random random = new Random(seed+5);

	int mutator = random.nextInt();

	Rule tempRule = currentRule;

	for(int i = 0; i <= mutator; i++){

		if(mutator%2!=0)tempRule = currentRule.and(pieces[mutator]);
		else if(mutator%3!=0)tempRule = currentRule.or(pieces[mutator]);
		else if(mutator%4!=0)tempRule = currentRule.and(new Rule getRule(random0)).or(new getRule(random1));
		else if(mutator%5!=0)tempRule = currentRule.or(new Rule getRule(random1)).and(new getRule(random2));
	}



}
	 */



	/*
	 *     AbstractRule, 
	 *     AndRule, 
	 *     BooleanIndicatorRule, 
	 *     BooleanRule, 
	 *     CrossedDownIndicatorRule, 
	 *     CrossedUpIndicatorRule, 
	 *     FixedRule, 
	 *     InPipeRule, 
	 *     JustOnceRule, 
	 *     NotRule, 
	 *     OrRule, 
	 *     OverIndicatorRule, 
	 *     StopGainRule, 
	 *     StopLossRule,
	 *     UnderIndicatorRule, 
	 *     WaitForRule, 
	 *     XorRule
	 * 

Rule entryRule = new CrossedUpIndicatorRule(shortSma, longSma)
.or(new CrossedDownIndicatorRule(closePrice, Decimal.valueOf("800")));

Rule exitRule = new CrossedDownIndicatorRule(shortSma, longSma)
.or(new StopLossRule(closePrice, Decimal.valueOf("3")))
.or(new StopGainRule(closePrice, Decimal.valueOf("2")));


Rule 	and(Rule rule) 
boolean 	isSatisfied(int index) 
Rule 	negation() 
Rule 	or(Rule rule) 
protected void 	traceIsSatisfied(int index, boolean isSatisfied)
Traces the isSatisfied() method calls.
Rule 	xor(Rule rule) 


	 */
	double getAvTrades(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r ="";
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}
		String[] data = r.split(";");
		String t = data[15].trim();
		if(t.equals("")){
			b.close();
			return Double.parseDouble(data[14]);
		}
		else {
			b.close();
			return Double.parseDouble(t); 
		}

	}

	String getParamName(String path, int paramIndex, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r;
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}

		String[] data = r.split(";");
		String t = data[8];
		String[] p = t.split(",");
		String param = p[paramIndex].trim();
		b.close();
		return param;

	}


	static double getMinShare(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r = "";


		while((r = b.readLine())!= null){

			if(index == row)break;

			index++;
		}

		String[] data = r.split(";");
		String t = data[11].trim();
		if(t.equals("")){
			b.close();
			return Double.parseDouble(data[14]);
		}
		else {
			b.close();
			return Double.parseDouble(t); 
		}



	}//end getMinShare



	static int getTrades(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r ="";
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}
		String[] data = r.split(";");
		String t = data[15].trim();
		if(t.length() < 2) {
			b.close();
			return Integer.parseInt(data[18].trim());
		}
		else if ((data[15].trim().length() > 2) && (data[19].trim().length() < 2)){
			b.close();
			return Integer.parseInt(t); 
		}
		else{
			b.close();
			return Integer.parseInt(data[19].trim());
		}


	}//end getTrades

	static int getMaxSector(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r;
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}
		String[] data = r.split(";");
		String sect = data[8].trim();
		if(sect.length() > 2) {
			b.close();
			return Integer.parseInt(data[11].trim());
		}
		else if ((data[15].trim().length() > 2) && (data[19].trim().length() < 2)){
			b.close();
			return Integer.parseInt(sect); 
		}
		else{
			b.close();
			return Integer.parseInt(data[12].trim());
		}


	}
	static int getGoodness(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r = "";
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}
		String[] data = r.split(";");
		String g = data[14].trim();
		if(g.length() > 2) {

			b.close();

			return Integer.parseInt(data[17].trim());
		}
		else if ((data[15].trim().length() > 2) && (data[19].trim().length() < 2)){
			b.close();
			return Integer.parseInt(g); 
		}
		else{
			b.close();
			return Integer.parseInt(data[18].trim());
		}
	}


	@SuppressWarnings("resource")
	static int getNFolds(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r = "";
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}
		String[] data = r.split(";");

		String t = data[4].trim();
		String i = data[1].trim();
		if(i.length() > 5) return Integer.parseInt(data[5].trim()); 
		int trades = Integer.parseInt(t);
		b.close();
		return trades;

	}

	static int getBSize(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));

		int index = 0;

		String r ="";

		while((r = b.readLine())!= null){

			if(index == row)break;

			index++;
		}

		String[] data = r.split(";");

		if(data[12].length() > 2) 

		{
			b.close();
			return Integer.parseInt(data[15].trim());
		}
		else if ((data[15].trim().length() > 2) && (data[19].trim().length() < 2)){
			b.close();
			return Integer.parseInt(data[12].trim()); 
		}
		else{
			b.close();
			return Integer.parseInt(data[16].trim());
		}



	}//end getBSize

	static int getSSize(String path, int row) throws IOException{


		BufferedReader b = new BufferedReader(new FileReader(new File(path)));

		int index = 0;

		String r ="";

		while((r = b.readLine())!= null){

			if(index == row)break;

			index++;
		}

		String[] data = r.split(";");

		if(data[13].length() > 2) 

		{
			b.close();
			return Integer.parseInt(data[16].trim());
		}
		else if ((data[15].trim().length() > 2) && (data[19].trim().length() < 2)){
			b.close();
			return Integer.parseInt(data[13].trim()); 
		}
		else{
			b.close();
			return Integer.parseInt(data[17].trim());
		}



	}//end getSSize
	/*
void iFitness(String symbol, Peace p) throws IOException{

	String path = symbol + "_ruleList.txt";

	int nRows = getRowCount(path);

	for(int i = 0; i < nRows; i++){

		p.fitnesses.put(calcFitness(symbol, i), i);

	}//end for


}//end iFitnesses
	 */
	int[] getParamValues(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));
		int index = 0;
		String r ="";
		while((r = b.readLine())!= null){


			if(index == row)break;
			index++;
		}
		String[] data = r.split(";");
		String t = data[7];
		if(t.indexOf(',') == -1) t = data[8];
		String[] paramValues = t.split(",");
		int[] params = new int[paramValues.length];
		for(int i = 0; i < params.length; i++){

			params[i] = Integer.parseInt(paramValues[i].trim()); 
		}
		b.close();	

		return params;

	}


	static double getProfit(String path, int row) throws IOException{

		BufferedReader b = new BufferedReader(new FileReader(new File(path)));

		int index = 0;
		String r = "";

		while((r = b.readLine()) != null){

			if(index == row)break;
			index++;
		}

		String[] data = r.split(";");
		String p = data[9].trim();
		String x = data[13].trim();
		String z = data[12].trim();
		String h = data[1].trim();

		if((h.length() > 4) && (x.trim().length() > 3)){
			b.close();
			return Double.parseDouble(x);

		}
		else if((p.indexOf(',') != -1) && (z.trim().length() > 3)){
			b.close();
			return Double.parseDouble(z);
		}
		else {
			b.close();
			return Double.parseDouble(p);
		}



	}//end getProfit

	double getAvProfit(String path, int row) throws IOException{

		double p = getProfit(path, row);

		return p/((double)this.seriesOfFolds.size()); 



	}

	static LinkedList<Genie> stringToGenies(String[] rule, LinkedList<Genie> list) throws IOException{


		int logic = 0;
		int name = 0;
		int logicLength = (int)Math.floor(rule.length/2);
		int nameLength = logicLength + 1;
		String[] logics = new String[logicLength]; 
		String[] names = new String[nameLength]; 

		for(int i = 0; i < rule.length;i++){

			if(i%2==1){
				//System.out.println(rule[i] + " logic");
				logics[logic] = rule[i];logic++;
			}//end if
			else {
				//System.out.println(rule[i] + " name");
				names[name] = rule[i]; name++;
			}//end else


		}//end for all rule string

		short l = 0;

		LinkedList<Genie> g = new LinkedList<Genie>();

		//for(int j = 0; j < logics.length;j++){


		Genie temp = null;
		int index = 0;
		int s = names.length;
		///while(index < s){

		//	System.out.println(names[index]);
		index++;

		//}
		//System.out.println();
		//s = 0;
		//System.out.println("start of list");
		while(s < list.size()-1){

			//	System.out.println(list.get(s).name);
			s++;

		}
		//System.out.println("end of list");
		for(int d = 0; d < names.length; d++){

			//last one doesn't need logical connector
			if(d < names.length-1){

				if(logics[d].equals("and")) l = 0;
				else if(logics[d].equals("or")) l = 1;
				else if(logics[d].equals("andNOT")) l = 2;
				else if(logics[d].equals("orNot")) l = 3;
				else if(logics[d].equals("xor")) l = 4;

			}
			else l = -1;
			index = -1;
			temp = null;

			while((temp == null) && (index < list.size()-1)){
				index++;
				String newName = list.get(index).name;

				if(names[d].equals((newName))){

					temp = list.get(index);
					//System.out.println(newName + ": name");
					//System.out.println(l + " : logic");

					temp.bool = l;

					if(d < list.size()-1) temp.bool = -1;

				}//end if names[d] == name



			}//end if names[d] == newTemp.name

			if((temp != null) && (temp.name != null)) g.add(temp);

		}//end while

		//last one in set is outside bounds of length of iteration, so must be added last
		return g;

	}//end string to genies


	/*
 Vector<Rule> getFitRules(String symbol, TreeMap<Double, Integer> fits, TreeMap<String, Rule> map, int elitePercent, boolean letGet) throws IOException{

	int s = fits.size();

	ArrayList<Integer> rows = new ArrayList<Integer>();

	Vector<Rule> fittest = new Vector<Rule>(); 

	int bound = (int)Math.floor((s*(elitePercent/100)));

	 for(int i = 0; i < bound; i++){

		 rows.add(fits.get(i));

	 }//end for, rows for best fitnesses found

	 fittest = getRulesByRow(rows, symbol, map, letGet);

	return fittest;

}//end getFitRules
	 */

	void writeAll(Map<String, Decimal> map, String symbol, String index, int tick) throws IOException{

		//now that you have initialized them all, lets write the array to file

		String path = index+"_"+ symbol+"_metrics.csv";

		String header = null;

		//this should error check that the map contains the same names for indicators as the original container
		//builds header for csv holding all metrics


		//close without a comma :)

		header += genes[genes.length - 1];

		StringBuilder sb = new StringBuilder(header+"\n");

		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));

		writer.write(sb.toString());


		String row = null;

		for(int j = 0; j < genes.length; j++){

			row += map.get(genes[j]) + ",";


			//iterate through map and get the values

		}//end for each gene
		row += "\n";

		writer.write(row);

		writer.close();

	}//end writeAll


	static ArrayList<TimeSeries> getTS(String symbol, int nRows, int nFolds, String master, long timestamp) throws ParseException, NumberFormatException, IOException{

		ArrayList<TimeSeries> seriesOfFolds = new ArrayList<TimeSeries>();

		List<Tick> ticks = new ArrayList<Tick>();

		CSVReader csvReader = new CSVReader(new FileReader(master));

		String[] parsedLine;

		int lineCount = 0;
		int rowCounter = 0;
		parsedLine = csvReader.readNext();//header line

		while ((parsedLine = csvReader.readNext()) != null) {
			rowCounter++;
			// System.out.println(rowCounter + " row");
			// System.out.println(nRows -1);
			DateTime date = new DateTime(DATE_FORMAT.parse(parsedLine[0]));

			if(((!parsedLine[1].equals("0") && (!parsedLine[1].equals("")) && !parsedLine[1].equals(null))) && (((!parsedLine[2].equals("0") && !parsedLine[2].equals("")) && !parsedLine[2].equals(null)) && (((!parsedLine[3].equals("0") && !parsedLine[3].equals(null) && !parsedLine[3].equals(""))) && (((!parsedLine[4].equals("0") && !parsedLine[4].equals(null) && !parsedLine[4].equals(""))) && (((!parsedLine[5].equals("0") && !parsedLine[5].equals(null) && !parsedLine[5].equals("")))))))){		
				//if(nFolds == 1)  System.out.println("inside tick: " + nTicks);
				double open = Double.parseDouble(parsedLine[1]);

				double high = Double.parseDouble(parsedLine[2]);

				double low = Double.parseDouble(parsedLine[3]);

				double close = Double.parseDouble(parsedLine[4]);

				double volume = Double.parseDouble(parsedLine[5]);

				nTicks++;  

				ticks.add(new Tick(date, open, high, low, close, volume));

				//System.out.println("here too");
				if((nFolds > 1) && (lineCount < ((nRows/nFolds)-1))) {
					lineCount++;
					//System.out.println("here last");
				}
				else if((nFolds == 1) && (lineCount < (nRows-2))) {
					//System.out.println("here wrong");
					lineCount++;
				}
				else{
					//System.out.println("first here");
					TimeSeries t = new TimeSeries(symbol, ticks);

					seriesOfFolds.add(t);
					//	System.out.println("here");
					if(nFolds == 1) return seriesOfFolds;
					lineCount = 0;

				}//end else

			}//end if parsedLine doesn't have null data (and therefore is useless to TimeSeries

		}//end while readLine not null


		csvReader.close();

		return seriesOfFolds;

	}//end getTS()



	/////////sample code from API/////////////////////
	/**
	 * 
	 * 
	 * 
	 * 
         // Getting the close price of the ticks
        Decimal firstClosePrice = series.getTick(0).getClosePrice();
        System.out.println("First close price: " + firstClosePrice.toDouble());
        // Or within an indicator:
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Here is the same close price:
        System.out.println(firstClosePrice.isEqual(closePrice.getValue(0))); // equal to firstClosePrice

        // Getting the simple moving average (SMA) of the close price over the last 5 ticks
        SMAIndicator shortSma = new SMAIndicator(closePrice, 5);
        // Here is the 5-ticks-SMA value at the 42nd index
        System.out.println("5-ticks-SMA value at the 42nd index: " + shortSma.getValue(42).toDouble());

        // Getting a longer SMA (e.g. over the 30 last ticks)
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);


        // Ok, now let's building our trading rules!

        // Buying rules
        // We want to buy:
        //  - if the 5-ticks SMA crosses over 30-ticks SMA
        //  - or if the price goes below a defined price (e.g $800.00)
        Rule buyingRule = new CrossedUpIndicatorRule(shortSma, longSma)
                .or(new CrossedDownIndicatorRule(closePrice, Decimal.valueOf("800")));

        // Selling rules
        // We want to sell:
        //  - if the 5-ticks SMA crosses under 30-ticks SMA
        //  - or if if the price looses more than 3%
        //  - or if the price earns more than 2%
        Rule sellingRule = new CrossedDownIndicatorRule(shortSma, longSma)
                .or(new StopLossRule(closePrice, Decimal.valueOf("3")))
                .or(new StopGainRule(closePrice, Decimal.valueOf("2")));

        // Running our juicy trading strategy...
        TradingRecord tradingRecord = series.run(new Strategy(buyingRule, sellingRule));
        System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());


        // Analysis

        // Getting the cash flow of the resulting trades
        CashFlow cashFlow = new CashFlow(series, tradingRecord);

        // Getting the profitable trades ratio
        AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
        System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
        // Getting the reward-risk ratio
        AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
        System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));

        // Total profit of our strategy
        // vs total profit of a buy-and-hold strategy
        AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
        System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));

        // Your turn!
    }


    //NEXT CLASS FOLLOWS





	 * Creating indicators

        // Close price
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Typical price
        TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
        // Price variation
        PriceVariationIndicator priceVariation = new PriceVariationIndicator(series);
        // Simple moving averages
        SMAIndicator shortSma = new SMAIndicator(closePrice, 8);
        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
        // Exponential moving averages
        EMAIndicator shortEma = new EMAIndicator(closePrice, 8);
        EMAIndicator longEma = new EMAIndicator(closePrice, 20);
        // Percentage price oscillator
        PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);
        // Rate of change
        ROCIndicator roc = new ROCIndicator(closePrice, 100);
        // Relative strength index
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        // Williams %R
        WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);
        // Average true range
        AverageTrueRangeIndicator atr = new AverageTrueRangeIndicator(series, 20);
        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 14);

        /**
	 * Building header

        StringBuilder sb = new StringBuilder("timestamp,close,typical,variation,sma8,sma20,ema8,ema20,ppo,roc,rsi,williamsr,atr,sd\n");

        /**
	 * Adding indicators values

        final int nbTicks = series.getTickCount();
        for (int i = 0; i < nbTicks; i++) {
            sb.append(series.getTick(i).getEndTime().getMillis() / 1000d).append(',')
            .append(closePrice.getValue(i)).append(',')
            .append(typicalPrice.getValue(i)).append(',')
            .append(priceVariation.getValue(i)).append(',')
            .append(shortSma.getValue(i)).append(',')
            .append(longSma.getValue(i)).append(',')
            .append(shortEma.getValue(i)).append(',')
            .append(longEma.getValue(i)).append(',')
            .append(ppo.getValue(i)).append(',')
            .append(roc.getValue(i)).append(',')
            .append(rsi.getValue(i)).append(',')
            .append(williamsR.getValue(i)).append(',')
            .append(atr.getValue(i)).append(',')
            .append(sd.getValue(i)).append('\n');
        }


        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("indicators.csv"));
            writer.write(sb.toString());
        } catch (IOException ioe) {
            Logger.getLogger(IndicatorsToCsv.class.getName()).log(Level.SEVERE, "Unable to write CSV file", ioe);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
            }
        }

    }
}





//////////////////////NEXT CLASS FOLLOWS////////////////////////






	 public static TimeSeries loadAppleIncSeries() {

	        InputStream stream;
	        //= CsvTicksLoader.class.getClassLoader().getResourceAsStream("appleinc_ticks_from_20130101_usd.csv");

	        List<Tick> ticks = new ArrayList<Tick>();

	        CSVReader csvReader = new CSVReader(new InputStreamReader(stream, Charset.forName("UTF-8")), ',', '"', 1);
	        try {
	            String[] line;
	            while ((line = csvReader.readNext()) != null) {
	              //  DateTime date = new DateTime(DATE_FORMAT.parse(line[0]));
	                double open = Double.parseDouble(line[1]);
	                double high = Double.parseDouble(line[2]);
	                double low = Double.parseDouble(line[3]);
	                double close = Double.parseDouble(line[4]);
	                double volume = Double.parseDouble(line[5]);

	                ticks.add(new Tick(date, open, high, low, close, volume));
	            }
	        } catch (IOException ioe) {
	     //       Logger.getLogger(CsvTicksLoader.class.getName()).log(Level.SEVERE, "Unable to load ticks from CSV", ioe);
	        } catch (ParseException pe) {
	     //       Logger.getLogger(CsvTicksLoader.class.getName()).log(Level.SEVERE, "Error while parsing date", pe);
	        } catch (NumberFormatException nfe) {
	     //       Logger.getLogger(CsvTicksLoader.class.getName()).log(Level.SEVERE, "Error while parsing value", nfe);
	        }

	        return new TimeSeries("apple_ticks", ticks);
	    }
/*
	    public static void main(String args[]) {
	        TimeSeries series = CsvTicksLoader.loadAppleIncSeries();

	        System.out.println("Series: " + series.getName() + " (" + series.getSeriesPeriodDescription() + ")");
	        System.out.println("Number of ticks: " + series.getTickCount());
	        System.out.println("First tick: \n"
	                + "\tVolume: " + series.getTick(0).getVolume() + "\n"
	                + "\tOpen price: " + series.getTick(0).getOpenPrice()+ "\n"
	                + "\tClose price: " + series.getTick(0).getClosePrice());


	    }	
	 */

}//end main


