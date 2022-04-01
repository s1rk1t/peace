package src;

import java.io.BufferedWriter;

//will likely use parallel architecture and some message passing in medium chunks 
//quadratic volumetricity seems logical for data transmission rates, so as to exercise
//but not *overload* the hardware's shortegrity. 

//double[][] learner = new double[nStack][nAttributes];//container indexes for attributes in learners

//String[] attributes = new String[nAttributes];

//Map<String[], double[][]> m = new Map<String[nAttributes],  double[nStack][nAttributes]>();

import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
//import java.security.Timestamp;

import au.com.bytecode.opencsv.CSVWriter;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.trackers.*;
import eu.verdelhan.ta4j.indicators.helpers.*;
import eu.verdelhan.ta4j.indicators.oscillators.*;
import eu.verdelhan.ta4j.indicators.simple.*;
import eu.verdelhan.ta4j.indicators.trackers.bollingerbands.*;
import eu.verdelhan.ta4j.indicators.volume.*;
import ta4jexamples.indicators.IndicatorsToCsv;

public class Piece{

	protected Piece(TimeSeries t){
		series = t;//matrix of values from source data 

	}

	protected Piece(){

	}


	protected Piece(TimeSeries t, long _date){
		series = t;
		date = _date;
	}

	Random ran = new Random();

	protected TimeSeries series = null;

	//TODO: enumerate hard coded boundaries for mutation 
	protected double range = 0.0;//fuzziness of logic

	//member var's for each piece of the peace

	protected static String name;//indicator for each piece

	protected static String symbol;//stock or commodity symbol 

	protected static String index;//associated index (both for data pulls)

	protected static long date;//timestamp of query, keep track of them as the come shorto being, no?

	protected static short tick;//index in master data file for reference in calculations





	//protected Vector<Decimal> values;//will hold all values for a particular symbol 


	protected short chromosomeIndex = -1;

	protected double fluidity = 0.0;//measure of neural weight for this particular piece of the peace

	//mutable indicator params 



	protected short aroonP = 14;//separate P for aroon calc's

	protected short sSMA = 5;//for accel/decel, awesomeOsc, 

	protected short lSMA = 34;

	protected short macdS = 12;//short and long ema Ps for MACD

	protected short macdL = 26;

	protected short adxP = 7;//average directional index smoothing P (common range is from 7-30)

	protected short atrP = 14;//average true range P

	protected short bbP = 20;//Bollinger Bands default SMA P 

	protected short cciP = 20;//commodity channel P

	protected short ema2P = 26;//double exponential moving average

	protected short ema3P = 39;//triple exponential average

	protected short smoothingPeriod = 20;

	protected short emaP = 12;//ema P (need to calculate 12, 26, 50, 200) for short range and long range estimates

	protected short devP = 20;//to help find the mean deviation in a time series given some P 

	protected short sarP = 14;//parabolic SAR time frame

	protected short ppoS = 12;//percentage price short (then long) ema P

	protected short ppoL = 26;

	protected short rocP = 12;//rate of change

	protected short rsiP = 26;//relative strength P

	protected int rsiL = 0;

	protected int rsiU = 0; 

	protected short stoD = 26;//stochastic oscillator D Period

	protected short stoK = 26;//stochastic osc. K ""

	protected short willP = 28;//default is 14 or 28

	protected int willU = 0;

	protected int willD = 0;

	protected short wmaP = 10;//weighted moving average period

	protected short sEMA = 8;

	protected short lEMA = 20;

	protected int stoU = 0;

	protected int stoDn = 0;

	protected int adxU = 0;

	protected int adxD = 0;

	protected int arUB = 0;

	protected int arLB = 0;

	Decimal unit = null;
	Decimal origin = null;
	Decimal stoUBound = null;
	Decimal stoLBound= null;
	Decimal threshold1= null;
	Decimal threshold2= null;
	Decimal rsiLowerBound= null;
	Decimal rsiUpperBound= null;
	Decimal willLBound= null;
	Decimal willUBound= null;
	Decimal positive100 = null;
	Decimal negative100 = null;
	Decimal arooUB = null;
	Decimal arooLB = null;
	// Decimal 
	// Decimal

	// protected double geneWeight;//regression normalizer
	protected short[] params = {aroonP, sSMA, lSMA, macdS, macdL, adxP, atrP, bbP, cciP, sEMA, lEMA, emaP, ema2P, 
			ema3P, devP, sarP, ppoS, ppoL, rocP, rsiP, stoD, stoK, willP, wmaP}; 

	protected int[] mutatedParams = {rsiL, rsiU, willU, willD, adxU, adxD, stoU, stoDn};

	protected String paramNames1 = "aroonP, sSMA, lSMA, macdS, macdL, adxP, atrP, bbP, cciP, sEMA, lEMA, emaP, ema2P";
	protected String paramNames2 = "ema3, devP, sarP, ppoS, ppoL, rocP, rsiP, stoD, stoK, willP, wmaP";
	protected String mutParamOrder = "rsiL, rsiU, willU, willD, adxU, adxL, stoU, stoDn";
	protected String paramOrder = paramNames1 + paramNames2;


	// Close price
	protected ClosePriceIndicator closePrice = new ClosePriceIndicator(series);//new ClosePriceIndicator(series);

	// Typical price
	TypicalPriceIndicator typicalPrice = null;//new TypicalPriceIndicator(series);

	// Price variation
	PriceVariationIndicator priceVariation = null;//new PriceVariationIndicator(series);

	// Simple moving averages
	SMAIndicator shortSma = null;//new SMAIndicator(closePrice, sSMA);
	// decvec.add(shortSma);

	SMAIndicator longSma = null;//new SMAIndicator(closePrice, lSMA);
	// decvec.add(longSma);

	// Exponential moving averages
	EMAIndicator shortEma = null;//new EMAIndicator(closePrice, sEMA);
	//    decvec.add(shortEma);

	EMAIndicator longEma = null;//new EMAIndicator(closePrice, lEMA);
	//    decvec.add(longEma);

	// Percentage price oscillator
	PPOIndicator ppo = null;//new PPOIndicator(closePrice, ppoS, ppoL);
	//ema for ppo only

	EMAIndicator ppoema = null;//new EMAIndicator(closePrice, ema2P);
	//    decvec.add(ppo);

	// Rate of change
	ROCIndicator roc = null;//new ROCIndicator(closePrice, rocP);
	//   decvec.add(roc);

	// Relative strength index
	RSIIndicator rsi = null;//new RSIIndicator(closePrice, rsiP);
	//   decvec.add(rsi);

	// Williams %R
	WilliamsRIndicator willR = null;//new WilliamsRIndicator(series, willP);
	//    decvec.add(williamsR);

	// Average true range
	AverageTrueRangeIndicator atr = null;//new AverageTrueRangeIndicator(series, atrP);
	//    decvec.add(atr);

	// Standard deviation
	StandardDeviationIndicator sd = null;//new StandardDeviationIndicator(closePrice, devP);// Close price
	//   decvec.add(sd);

	AccelerationDecelerationIndicator ade = null;//new AccelerationDecelerationIndicator(series, sSMA, lSMA);
	//    decvec.add(ade);
	AccumulationDistributionIndicator adi = null;//new AccumulationDistributionIndicator(series);
	//    decvec.add(adi);
	AroonDownIndicator arooD = null;//new AroonDownIndicator(series, aroonP);
	//    decvec.add(arooD);
	AroonUpIndicator arooU = null;//new AroonUpIndicator(series, aroonP);
	//    decvec.add(arooU);
	AverageDirectionalMovementDownIndicator admD = null;//new AverageDirectionalMovementDownIndicator(series, adxP); 
	//    decvec.add(admD);
	AverageDirectionalMovementUpIndicator admU = null;//new AverageDirectionalMovementUpIndicator(series, adxP); 
	//    decvec.add(admU);
	AverageDirectionalMovementIndicator adm = null;//new AverageDirectionalMovementIndicator(series, adxP);
	//	    decvec.add(adm);
	AverageGainIndicator ags= null;//new AverageGainIndicator(closePrice, sEMA);
	//    decvec.add(ags);
	AverageGainIndicator agl = null;//new AverageGainIndicator(closePrice, lEMA);
	//	    decvec.add(agl);
	AverageLossIndicator als= null;//new AverageLossIndicator(closePrice, sEMA);
	//    decvec.add(als);getTS
	AverageLossIndicator all = null;//new AverageLossIndicator(closePrice, lEMA);
	//    decvec.add(all);
	AverageTrueRangeIndicator atri = null;//new AverageTrueRangeIndicator(series, atrP); 
	//	    decvec.add(atri);
	BollingerBandsMiddleIndicator bbM = null;//new BollingerBandsMiddleIndicator(closePrice); 
	//    decvec.add(bbm);
	BollingerBandsLowerIndicator bbL = null;//new BollingerBandsLowerIndicator(bbM, closePrice); 
	//	    decvec.add(bbl);
	BollingerBandsUpperIndicator bbU = null;//new BollingerBandsUpperIndicator(bbM, closePrice); 
	//	    decvec.add(bbu);
	CCIIndicator cci = null;//new CCIIndicator(series, cciP);
	//	    decvec.add(cci);
	DirectionalMovementDownIndicator dmD = null;//new DirectionalMovementDownIndicator(series);
	//	    decvec.add(dmd);
	DirectionalMovementUpIndicator dmU = null;//new DirectionalMovementUpIndicator(series);
	//	    decvec.add(dmu);
	DirectionalMovementIndicator dm = null;//new DirectionalMovementIndicator(series, pc.adxP); 
	//    decvec.add(dm);
	DirectionalDownIndicator dxD = null;//new DirectionalDownIndicator(series, pc.adxP);
	//    decvec.add(ddi);
	DirectionalUpIndicator dxU = null;//new DirectionalUpIndicator(series, pc.adxP);
	//    decvec.add(dui);
	DoubleEMAIndicator dubE = null;//new DoubleEMAIndicator(closePrice, pc.ema2P);
	//    decvec.add(dema);
	TripleEMAIndicator triE = null;//new TripleEMAIndicator(closePrice, pc.ema3P);
	//	    decvec.add(tema);
	MACDIndicator macd = null;//new MACDIndicator(closePrice, macdS, macdL);
	//	    decvec.add(macd);
	MeanDeviationIndicator meanDev = null;//new MeanDeviationIndicator(closePrice, pc.devP);
	//	    decvec.add(meandev);
	OnBalanceVolumeIndicator obv = null;//new OnBalanceVolumeIndicator(series); 
	//    decvec.add(obv);
	ParabolicSarIndicator sar = null;//new ParabolicSarIndicator(series, pc.sarP);


	StochasticOscillatorKIndicator stoKi = null;//new StochasticOscillatorKIndicator(series, stoK);

	StochasticOscillatorDIndicator stoDi = null;//new StochasticOscillatorDIndicator(stoKi);

	SMAIndicator smoothedDXD = null;//new SMAIndicator(admD, smoothingPeriod);  

	SMAIndicator smoothedDXU = null;//new SMAIndicator(admU, smoothingPeriod);
	WMAIndicator wma = null;
	TrueRangeIndicator tri = null;
	TypicalPriceIndicator typ = null;









	// protected short getwmaP(){return this.wmaP;}

	void setwmaP(short x){this.wmaP = x;}


	protected short getwillP(short x){return this.willP;}

	void setwillP(short x){this.willP = x;}

	protected short getstoK(short x){return this.stoK;}

	void setstoK(short x){this.stoK = x;}


	protected short getstoD(short x){return this.stoD;}

	void setstoD(short x){this.stoD = x;}


	protected short getrsiP(short x){return this.rsiP;}

	void setrsiP(short x){this.rsiP = x;}


	protected short getrocP(short x){return this.rocP;}

	void setrocP(short x){this.rocP = x;}


	protected short getppoL(short x){return this.ppoL;}

	void setppoL(short x){this.ppoL = x;}


	protected short getppoS(short x){return this.ppoS;}

	void setppoS(short x){this.ppoS = x;}


	protected short getsarP(short x){return this.sarP;}

	void setsarP(short x){this.sarP = x;}


	protected short getdevP(short x){return this.devP;}

	void setdevP(short x){this.devP = x;}


	protected short getema3P(short x){return this.ema3P;}

	void setema3P(short x){this.ema3P = x;}


	protected short getema2P(short x){return this.ema2P;}

	void setema2P(short x){this.ema2P = x;}


	protected short getemaP(short x){return this.emaP;}

	void setemaP(short x){this.emaP = x;}


	protected short getcciP(short x){return this.cciP;}

	void setcciP(short x){this.cciP = x;}


	protected short getbbP(short x){return this.bbP;}

	void setbbP(short x){this.bbP = x;}


	protected short getatrP(short x){return this.atrP;}

	void setatrP(short x){this.atrP = x;}


	protected short getadxP(short x){return this.adxP;}

	void setadxP(short x){this.adxP = x;}




	protected short getmacdS(short x){return this.macdS;}

	void setmacdS(short x){this.macdS = x;}

	protected short getmacdL(short x){return this.macdL;}

	void setmacdL(short x){this.macdL = x;}

	protected short getaroonP(short x){return this.aroonP;}

	void setaroonP(short x){this.aroonP = x;}


	protected short getsSMA(){return this.sSMA;}

	void setsSMA(short x){this.sSMA = x;}


	protected short getlSMA(){return this.lSMA;}

	void setlSMA(short x){this.lSMA = x;}



	protected static short marketCap;

	//Map<String, Decimal> indicators = new HashMap();

	protected String [] genes = { 

			"AverageDirectionalMovementIndicator",
			"AverageDirectionalMovementDownIndicator", "AverageDirectionalMovementUpIndicator", 
			"AroonUpIndicator", "AroonDownIndicator", 
			"AccelerationDecelerationIndicator", 
			//"AccumulationDistributionIndicator", //some issue here with stack overflow
			"AverageTrueRange", 
			"BollingerBandsLowerIndicator", "BollingerBandsUpperIndicator",
			"CCIIndicator", 
			"DirectionalUpIndicator", "DirectionalDownIndicator", 
			"DirectionalMovementDownIndicator",  "DirectionalMovementUpIndicator","DirectionalMovementIndicator", 
			"OnBalanceVolumeIndicator",
			"ParabolicSarIndicator", 
			"PPOIndicator",
			//"PreviousPriceIndicator",
			"PriceVariationIndicator",
			"ROCIndicator",
			"RSIIndicator",
			"StochasticOscillatorDIndicator","StochasticOscillatorKIndicator",
			"TripleEMAIndicator", "DoubleEMAIndicator", "EMAIndicator",
			"TrueRangeIndicator", 
			//"TypicalPriceIndicator",
			"WilliamsRIndicator",
			"WMAIndicator"
	};

	String[] pieces = { 

			"AverageDirectionalMovementDownIndicator", 
			"AbstractIndicator", 
			"AccelerationDecelerationIndicator",//lm1
			"AccumulationDistributionIndicator",//cc1
			"AmountIndicator", 
			"AroonDownIndicator", 
			"AroonUpIndicator", 
			"AverageDirectionalMovementIndicator", 
			"AverageDirectionalMovementUpIndicator", 
			"AverageGainIndicator", 
			"AverageLossIndicator", 
			"AverageTrueRangeIndicator", 
			"AwesomeOscillatorIndicator", 
			"BollingerBandsLowerIndicator", 
			"BollingerBandsMiddleIndicator", 
			"BollingerBandsUpperIndicator", 
			"CachedIndicator", 
			"CashFlow", 
			"CCIIndicator", 
			"ClosePriceIndicator", 
			"ConstantIndicator", 
			"CrossIndicator", 
			"DifferenceIndicator", 
			"DirectionalDownIndicator", 
			"DirectionalMovementDownIndicator", 
			"DirectionalMovementIndicator", 
			"DirectionalMovementUpIndicator", 
			"DirectionalUpIndicator", 
			"DoubleEMAIndicator", 
			"EMAIndicator", 
			"HighestValueIndicator", 
			"LowestValueIndicator", 
			"MACDIndicator", 
			"MaxPriceIndicator", 
			"MeanDeviationIndicator", 
			"MedianPriceIndicator", 
			"MinPriceIndicator", 
			"MultiplierIndicator", 
			"OnBalanceVolumeIndicator", 
			"OpenPriceIndicator", 
			"ParabolicSarIndicator", 
			"PPOIndicator", 
			"PreviousPriceIndicator", 
			"PriceVariationIndicator", 
			"ROCIndicator", 
			"RSIIndicator", 
			"SMAIndicator", 
			"StandardDeviationIndicator", 
			"StochasticOscillatorDIndicator", 
			"StochasticOscillatorKIndicator", 
			"TradeCountIndicator", 
			"TripleEMAIndicator", 
			"TrueRangeIndicator", 
			"TypicalPriceIndicator", 
			"VolumeIndicator", 
			"WilliamsRIndicator", 
			"WMAIndicator"
	};

	// protected short chromosomeIndex;//where is attribute in chromosome
	// protected short nStack; //size of learner stack in WEKA
	// protected short nAttributes;//size of chromosome









	//get the value with some fun stats and linear algebra magic, fool.

	///////////////////example/////////////////////////////////


	//ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
	// Here is the same close price:
	//System.out.prshortln(firstClosePrice.isEqual(closePrice.getValue(0)));


	//protected static String get$(){return this.$;}
	//bool String set$(short x){this.$ = x;return true;}










	/*

	Decimal def;
	def = Decimal.valueOf(-1000000);//default value of nonviable queries is negative one million

	//TODO: idea: have vectors of similar types of indicators:
	//e.g. leading momentum vector, crossing indicator vector maybe, stuff like that....

	//TODO: write all indicators to csv for each tick in source data


	switch(name){


	case "AverageDirectionalMovementIndicator":{

		AverageDirectionalMovementIndicator a  = new AverageDirectionalMovementIndicator(t, adxP);

		 if(a.getValue(tick).isNaN() == false) indicators.put("AverageDirectionalMovementIndicator", a.getValue(tick)); 
		 else indicators.put("AverageDirectionalMovementIndicator", def);								
											}//end adx

	case "AverageDirectionalMovementDownIndicator":{

		AverageDirectionalMovementDownIndicator a  = new AverageDirectionalMovementDownIndicator(t, adxP);

		 if(a.getValue(tick).isNaN() == false) indicators.put("AverageDirectionalMovementDownIndicator", a.getValue(tick)); 
		 else indicators.put("AverageDirectionalMovementDownIndicator", def);
												    }//end -DX

	case "AverageDirectionalMovementUpIndicator":{

		AverageDirectionalMovementDownIndicator a  = new AverageDirectionalMovementDownIndicator(t, adxP);

		 if(a.getValue(tick).isNaN() == false) indicators.put("AverageDirectionalMovementUpIndicator", a.getValue(tick)); 
		 else indicators.put("AverageDirectionalMovementUpIndicator", def);
												    }//end +DX

	case "AroonUpIndicator":{

		AroonUpIndicator u = new AroonUpIndicator(t, aroonP);

		if(u.getValue(tick).isNaN() == false) indicators.put("AroonUpIndicator", u.getValue(tick));
		else indicators.put("AroonUpIndicator", def);
							}//end Aroon Up

	case "AroonDownIndicator":{

		AroonDownIndicator d = new AroonDownIndicator(t, aroonP);

		if(d.getValue(tick).isNaN() == false) indicators.put("AroonDownIndicator", d.getValue(tick));
		else indicators.put("AroonDownIndicator", def);
							}//end Aroon Down

    case "AccelerationDecelerationIndicator":{

    	        AccelerationDecelerationIndicator a = new AccelerationDecelerationIndicator(t, sSMA, lSMA);

    	        if(a.getValue(tick).isNaN() == false) indicators.put("AccelerationDecelerationIndicator", a.getValue(tick));
    	        else indicators.put("AccelerationDecelerationIndicator", def);

											  }//end Accel/Decel 

    case "AccumulationDistributionIndicator":{

    	AccumulationDistributionIndicator a = new AccumulationDistributionIndicator(t);

    	if(a.getValue(tick).isNaN() == false) indicators.put("AccumulationDistributionIndicator", a.getValue(tick));
    	else indicators.put("AccumulationDistributionIndicator", def);
    										  }//end list[3]

    case "AverageTrueRange":{

    	AverageTrueRangeIndicator a = new AverageTrueRangeIndicator(t, atrP);

    	if((tick < atrP) && (a.getValue(tick).isNaN() == false)) indicators.put("AverageTrueRange", a.getValue(tick));
    	else indicators.put("AverageTrueRange", def);
    							 }//end atr

    case "BollingerBandsLowerIndicator":{

    	ClosePriceIndicator c = new ClosePriceIndicator(t);

    	SMAIndicator s = new SMAIndicator(c, 20);

    	BollingerBandsMiddleIndicator bm = new BollingerBandsMiddleIndicator(s);

    	BollingerBandsLowerIndicator bl = new BollingerBandsLowerIndicator(bm, s);

    	if(bl.getValue(tick).isNaN() == false) indicators.put("BollingerBandsLowerIndicator", bl.getValue(tick));
    	else indicators.put("BollingerBandsLowerIndicator", def);
    							}//end bollinger bands lower

    case "BollingerBandsUpperIndicator":{


    	ClosePriceIndicator c = new ClosePriceIndicator(t);

    	SMAIndicator s = new SMAIndicator(c, 20);


    	BollingerBandsMiddleIndicator bm = new BollingerBandsMiddleIndicator(s);

    	BollingerBandsUpperIndicator bu = new BollingerBandsUpperIndicator(bm, s);

    	if(bu.getValue(tick).isNaN() == false) indicators.put("BollingerBandsUpperIndicator", bu.getValue(tick));
    	else indicators.put("BollingerBandsUpperIndicator", def);


    							}//end bollinger bands upper
    case "CCIIndicator":{

    	CCIIndicator c = new CCIIndicator(t, cciP);

    	if(c.getValue(tick).isNaN() == false) indicators.put("CCIIndicator", c.getValue(tick));
    	else indicators.put("CCIIndicator", def);
    							}//end CCI 

    case "DirectionalUpIndicator":{

    	DirectionalUpIndicator a  = new DirectionalUpIndicator(t, adxP);

    	if(a.getValue(tick).isNaN() == false) indicators.put("DirectionalUpIndicator",a.getValue(tick));
    	else indicators.put("DirectionalUpIndicator", def);
    											    }//end +DI

    case "DirectionalDownIndicator":{

    	DirectionalDownIndicator a  = new DirectionalDownIndicator(t, adxP);

    	if(a.getValue(tick).isNaN() == false) indicators.put("DirectionalDownIndicator",a.getValue(tick));
    	else indicators.put("DirectionalDownIndicator", def);



											    }//end -DI

    case "DirectionalMovementDownIndicator":{

    	DirectionalMovementDownIndicator a  = new DirectionalMovementDownIndicator(t);

    	if(a.getValue(tick).isNaN() == false) indicators.put("DirectionalMovementDownIndicator", a.getValue(tick)); 
    	else indicators.put("DirectionalMovementDownIndicator", def);
											    }//end -DX

    case "DirectionalMovementUpIndicator":{

    	DirectionalMovementUpIndicator a  = new DirectionalMovementUpIndicator(t);

    	if(a.getValue(tick).isNaN() == false) indicators.put("DirectionalMovementUpIndicator", a.getValue(tick)); 
    	else indicators.put("DirectionalMovementUpIndicator", def);
											    }//end +DX


    case "DirectionalMovementIndicator":{

    	DirectionalMovementIndicator a  = new DirectionalMovementIndicator(t, adxP);

    	if(a.getValue(tick).isNaN() == false) indicators.put("DirectionalMovemenIndicator", a.getValue(tick)); 
    	else indicators.put("DirectionalMovementIndicator", def);

											    }//end -DX

    case "OnBalanceVolumeIndicator":{

    	OnBalanceVolumeIndicator a  = new OnBalanceVolumeIndicator(t);

    	if(a.getValue(tick).isNaN() == false) indicators.put("OnBalanceVolumeIndicator", a.getValue(tick)); 
    	else indicators.put("OnBalanceVolumeIndicator", def);

										    }//end OBV
    /*
    case "ParabolicSarIndicator":{

    	ParabolicSarIndicator p  = new ParabolicSarIndicator(t, sarP);

    	if((tick < sarP) && (p.getValue(tick).isNaN() == false)) indicators.put("ParabolicSarIndicator", p.getValue(tick)); 
    	else indicators.put("ParabolicSarIndicator", def);

										    }//end SAR

    case "PPOIndicator":{

    	ClosePriceIndicator c = new ClosePriceIndicator(t);

    	PPOIndicator p  = new PPOIndicator(c, ppoS, ppoL);

    	if((tick < ppoL) && (p.getValue(tick).isNaN() == false)) indicators.put("PPOIndicator", p.getValue(tick)); 
    	else indicators.put("PPOIndicator", def);

										    }//end PPO


    case "PriceVariationIndicator":{

    	PriceVariationIndicator c = new PriceVariationIndicator(t);

    	if(c.getValue(tick).isNaN() == false) indicators.put("PriceVariationIndicator", c.getValue(tick)); 
    	else indicators.put("PriceVariationIndicator", def);

										    }//end Price Variation

    case "ROCIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(t);

    	ROCIndicator r = new ROCIndicator(c, rocP);

    	if((tick < rocP) && (r.getValue(tick).isNaN() == false)) indicators.put("ROCIndicator", r.getValue(tick)); 
    	else indicators.put("ROCIndicator", def);

										    }//end ROC

	case "RSIIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(t);

		RSIIndicator r = new RSIIndicator(c, rsiP);

		if((tick < rsiP) && (r.getValue(tick).isNaN() == false)) indicators.put("RSIIndicator", r.getValue(tick)); 
		else indicators.put("RSIIndicator", def);

									    }//end RSI

	case "StochasticOscillatorDIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(t);

		StochasticOscillatorDIndicator s = new StochasticOscillatorDIndicator(c);

		if((tick < stoD) && (s.getValue(tick).isNaN() == false)) indicators.put("StochasticOscillatorDIndicator", s.getValue(tick)); 
		else indicators.put("StochasticOscillatorDIndicator", def);

	}//end sto d

	case "StochasticOscillatorKIndicator":{

		StochasticOscillatorKIndicator s = new StochasticOscillatorKIndicator(t, stoK);

		if((tick < stoK) && (s.getValue(tick).isNaN() == false)) indicators.put("StochasticOscillatorKIndicator", s.getValue(tick)); 
		else indicators.put("StochasticOscillatorKIndicator", def);

	}//end sto k	

	case "TripleEMAIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(t);

		TripleEMAIndicator s = new TripleEMAIndicator(c, ema3P);

		if((tick < ema3P) && (s.getValue(tick).isNaN() == false)) indicators.put("TripleEMAIndicator", s.getValue(tick)); 
		else indicators.put("TripleEMAIndicator", def);

	}//end 3 EMA

	case "DoubleEMAIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(ts);

		DoubleEMAIndicator d = new DoubleEMAIndicator(c, ema2P);

		if((tick < ema2P) && (d.getValue(tick).isNaN() == false)) indicators.put("DoubleEMAIndicator", d.getValue(tick)); 
		else indicators.put("DoubleEMAIndicator", def);

	}//end 2 EMA

	case "EMAIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(ts);

		EMAIndicator d = new EMAIndicator(c, emaP);

		if((tick < emaP) && (d.getValue(tick).isNaN() == false)) indicators.put("EMAIndicator", d.getValue(tick)); 

		else indicators.put("EMAIndicator", def);

	}//end EMA

	case "TrueRangeIndicator":{

		TrueRangeIndicator tr = new TrueRangeIndicator(ts);

		if((tick < atrP) && (tr.getValue(tick).isNaN() == false)) indicators.put("TrueRangeIndicator", tr.getValue(tick)); 
		else indicators.put("TrueRangeIndicator", def);

	}//end true range



	case "WilliamsRIndicator":{

		WilliamsRIndicator w = new WilliamsRIndicator(ts, willP);

		if((tick < willP) && (w.getValue(tick).isNaN() == false)) indicators.put("WilliamsRIndicator", w.getValue(tick)); 
		else indicators.put("WilliamsRIndicator", def);

	}//end williams r oscillator

	case "WMAIndicator":{

		ClosePriceIndicator c = new ClosePriceIndicator(ts);

		WMAIndicator w = new WMAIndicator(c, wmaP);;

		if((tick < wmaP) && (w.getValue(tick).isNaN() == false)) indicators.put("WMAIndicator", w.getValue(tick)); 
		else indicators.put("WMAIndicator", def);

	}//end weighted moving average indicator

	default:{
    	//"Abstract Indicator";
    		}//end default

	}//end switch

	 */






	//begin utility indicator getter definitions

	Indicator<Decimal> getPreviousPriceIndicator(short tick){

		PreviousPriceIndicator c = new PreviousPriceIndicator(series);
		return c;

	}//end get Previous Price

	Indicator<Decimal> getTypicalPrice(){

		TypicalPriceIndicator tp = new TypicalPriceIndicator(series);

		return tp; 

	}

	Indicator<Integer> getTradeCount(){

		TradeCountIndicator s = new TradeCountIndicator(series);

		return s;

	}//end get trade count


	Indicator<Decimal> getStdDev(Indicator<Decimal> i, short period){

		StandardDeviationIndicator s = new StandardDeviationIndicator(i, period);

		return s;


	}//end get standard dev


	Indicator<Decimal> getSMA(Indicator<Decimal> i, short period){

		SMAIndicator s = new SMAIndicator(i, period);

		return s;


	}//end get sma




	Indicator<Decimal> getExponential(Indicator<Decimal> i, Decimal coefficient){

		MultiplierIndicator m = new MultiplierIndicator(i, coefficient);

		return m;
	}//end get Multiplier



	Indicator<Decimal> getMin(){

		MinPriceIndicator m = new MinPriceIndicator(series);

		return m;

	}//get Min Price



	Indicator<Decimal> getMedian(){

		MedianPriceIndicator m = new MedianPriceIndicator(series);

		return m;

	}//get Median Price

	Indicator<Decimal> getDeviation(Indicator<Decimal> i){

		MeanDeviationIndicator m = new MeanDeviationIndicator(i, devP);

		return m;

	}//get Mean Deviation

	Indicator<Decimal> getMax(){

		MaxPriceIndicator m = new MaxPriceIndicator(series);

		return m;

	}//end get max periodrice

	Indicator<Decimal> getHighest(Indicator<Decimal> indicator, short period){

		HighestValueIndicator h = new HighestValueIndicator(indicator, period);

		return h;

	}//end get highest

	Indicator<Decimal> getLowest(Indicator<Decimal> indicator, short period){

		LowestValueIndicator h = new LowestValueIndicator(indicator, period);
		return h;


	}//end get lowest

	Indicator<Decimal> getMACD(Indicator<Decimal> i){

		MACDIndicator m = new MACDIndicator(i, macdS, macdL); 

		return m;

	}//end MACD

	Indicator<Decimal> getDEMA(Indicator<Decimal> i){

		DoubleEMAIndicator d = new DoubleEMAIndicator(i, ema2P);

		return d;

	}//end get DEMA


	Indicator<Decimal> getEMA(Indicator<Decimal> i){

		EMAIndicator d = new EMAIndicator(i, emaP);

		return d;

	}//end get EMA

	Indicator<Boolean> getCross(Indicator<Decimal> top, Indicator<Decimal> bottom){

		CrossIndicator c = new CrossIndicator(top, bottom);

		return c;

	}//end getCross

	Indicator<Decimal> getDif(Indicator<Decimal> i , Indicator<Decimal> j){

		DifferenceIndicator d = new DifferenceIndicator(i,j);

		return d;

	}//end getDif


	Indicator<Decimal> getAwesome(Indicator<Decimal> i , short period){

		AwesomeOscillatorIndicator a = new AwesomeOscillatorIndicator(i, sSMA, lSMA);

		return a;

	}//end getAwesome


	Indicator<Decimal> getAvGain(Indicator<Decimal> i, short period){

		AverageGainIndicator a = new AverageGainIndicator(i, period);
		return a;

	}//end getAvGain

	Indicator<Decimal> getAvLoss(Indicator<Decimal> i, short period){

		AverageLossIndicator a = new AverageLossIndicator(i, period);
		return a;

	}//end getAvLoss




	//to write indicators to csv after init

	void write(String prefix) throws IOException{

		String filepath = prefix + "_attributes.csv";

		// CSVWriter writer = new CSVWriter(new FileWriter(filename), '\t');

		/**
		 * Building header
		 */

		String header = null;


		for(short i = 0; i < pieces.length -1 ; i++){

			header += pieces[i] + ",";

		}//end for


		header += pieces[pieces.length - 1];

		StringBuilder sb = new StringBuilder(header+"\n");

		/**
		 * Adding indicators values

     final short nbTicks = series.getTickCount();
     for (short i = 0; i < nbTicks; i++) {
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

     /**
		 * Writing CSV file
		 */
		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(filepath));
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


}//end Piece class def

