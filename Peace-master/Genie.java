package src;


import eu.verdelhan.ta4j.Rule;

public class Genie {
	
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
	

	
	
	
	protected String name;
	
	protected Rule r;
	
	protected int logic;//connector
	
	protected int bool;
	
	//protected boolean isHead;
	//protected boolean isTail;
	
	protected short index;//position in rule branch
	
	protected short indicator1;
	protected short indicator2;
	protected short indicator3;
	
	protected double threshold;
	
	protected short p0d;
	protected short[] p1d;//params
	protected short[][] p2d;
	
	protected Genie previous;
	protected Genie next;
	
public Genie(String _ruleName, Rule _r, short _l, short _i, short _i2, short index){
		
		this.indicator1 = _i;
		this.indicator2 = _i2;

		this.name = _ruleName;
		this.logic = _l;
		this.r = _r;		
		this.index = index;
		
	}//end 1st head constructor

public Genie(String _ruleName, Rule _r, short _l, short _i){
	
	this.indicator1 = _i;
	this.r = _r;

	this.name = _ruleName;
	this.logic = _l;
		
	
	
}//end 1st head constructor
	
public Genie(String _ruleName,  int _i){
	

	this.name = _ruleName;
	this.bool = _i;
		
	
	
}//end 1st head constructor
	
	
	public Genie(String _ruleName, Rule _r, short _l, short _i, short index){
		
		this.indicator1 = _i;
		

		this.name = _ruleName;
		this.logic = _l;
		this.r = _r;
		this.index = index;
	
	}//end 2nd head constructor
	
	public Genie(String n){
		
	


		this.name = n;
	
	}//addToTop constructor
	
	public Genie(Genie g, Genie g1, Rule r, String n, short l, short i, short i2, short index){
		
		this.indicator1 = i;
		this.indicator2 = i2;

		this.name = n;
		this.logic = l;
		this.r = r;
		this.previous = g;
		this.next = g1;
		this.index = index;
		
	}//insert constructor two indicators and params
	
	public Genie(Genie g, boolean isHead, Rule r, String n, short l, short i, short i2, short index){
		
		this.indicator1 = i;
		this.indicator2 = i2;

		this.name = n;
		this.logic = l;
		this.r = r;
		if(isHead == false) this.previous = g;
		else this.next = g;
		this.index = index;
		
	}//2nd insert constructor
	


	public Genie(Genie g, Genie g1, Rule r, String n, short l, short i, short index){
	

		this.indicator1 = i;
	
		this.name = n;
		this.logic = l;
		this.r = r;
		this.previous = g;
		this.next = g1;
		this.index = index;
		
	}//threshold constructor insert 1 indicator
	

	

	
    public Genie(Genie g, Rule r, String n, short l, short i,short i2, short i3, short index){
    	
    	this.indicator1 = i;
		this.indicator2 = i2;
		this.indicator3 = i3;
		this.threshold = threshold;
		this.name = n;
		this.logic = l;
	
		this.r = r;
		this.previous = g;
		this.next = g;
		this.index = index;
		
	}//threshold constructor attach threshold three indicators
    
    public Genie(Genie g, Genie g2, Rule r, String n, short l, short i,short i2, short i3, short index){
    	
    	this.indicator1 = i;
		this.indicator2 = i2;
		this.indicator3 = i3;
		
		this.name = n;
		this.logic = l;
	
		this.r = r;
		this.previous = g;
		this.next = g;
		this.index = index;
		
   	}//threshold constructor insert three indicators
   	
	
	
	public Genie(String n,  short logic){
    
    	this.name = n;
		this.logic = logic;

	
		
	}
	
Genie getPrev(){
	
	return this.previous;
}


Genie getNext(){

return this.next;
}


String getName(){

return this.name;
}
/*
short getI1(){

return this.indicator1;
}


short[] getP1D(){

return this.p1d;
}

short[][] getP2D(){

return this.p2d;
}


double getThresh(){

return this.threshold;
}


Rule getRule(){

return this.r;
}

short getI2(){

return this.indicator2;
}


short getI3(){

return this.indicator3;
}

int getLogic(){

return this.logic;
}
	

short getIndex(){

return this.index;
}


void setPrev(Genie g){
	
	 this.previous = g;
}


void setNext(Genie g){

 this.next = g;
}


void setName(String s){

 this.name = s;
}

void setI1(short i){

 this.indicator1 = i;
}


void setP1D(short[] i){

 this.p1d = i;
}

void setP2D(short [][] i){

 this.p2d = i;
}


void setThresh(double t){

 this.threshold = t;
}


void setRule(Rule r){

 this.r = r;
}

void setI2(short i){

 this.indicator2 = i;
}


void setI3(short i){

 this.indicator3 = i;
}

void setLogic(short l){

 this.logic = l;
}
	

void setIndex(short i){

this.index = i;
}
*/	
	
}//end class definition
