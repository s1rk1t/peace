package src;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Rule;
import eu.verdelhan.ta4j.trading.rules.*;



//TODO: add stop gain and stop loss rules 
public class Love {

	protected int nGenes;
	protected int nCrosses;
	protected int popSize;
	protected short [] rules;
	protected short [] logics;
	protected double range;
	protected double mutation;
	protected int branchingFactor;	
	protected Piece p;

	protected int ruleSize;
	protected ArrayList<Integer> getIndexes;
	protected ArrayList<Integer> letIndexes;
	protected String ruleString = "";
	protected String[] logicStrings = new String[5];

	public Love(Piece _p, int _nGenes, double _range, double _mutationRate, int _popSize, int bf){

		p = _p;
		range = _range;
		nGenes = _nGenes;
		mutation = _mutationRate;
		popSize = _popSize;

		branchingFactor = bf;
		logicStrings[0] = " And ";
		logicStrings[1] = " Or " ;
		logicStrings[2] = " Xor ";
		logicStrings[3] = " AndNot ";
		logicStrings[4] = " OrNot ";

		ruleSize = 0;

	}//Love never ends....


	void mutate(int _nMutants){

		Random r = new Random();
		int variance;

		int nMutants = r.nextInt(_nMutants);
		if(nMutants == 0) nMutants = 1;

		int rando;
		int mutatedParam;

		for(int i = 0; i < nMutants; i++){

			rando = r.nextInt(2);
			variance = r.nextInt(3);
			mutatedParam = r.nextInt(24);

			int temp = p.params[mutatedParam];

			if(rando == 0) p.params[mutatedParam] += 1;
			else p.params[mutatedParam] -= 1;

			if( p.params[mutatedParam] <= 1) p.params[mutatedParam] = (short)(variance * temp);


		}//end for all mutants


	}//end mutate

	String rulesToString(LinkedList<Genie> g){

		String rule = null;

		for(int i = 0; i < rules.length; i++){

			if(i == rules.length - 1) rule += g.get(rules[i]).name;	

			else{

				rule += g.get(rules[i]).name;

				rule += this.logicStrings[logics[i]];

			}	

		}//end for

		return rule;

	}//end rulesToString


	Rule arrayToRule(Vector<Rule> r, LinkedList<Genie> g){

		Rule output = null;

		int logic = 0;

		Random o = new Random();

		ruleSize = o.nextInt(branchingFactor); 

		if(ruleSize < 3) ruleSize = 3;

		ArrayList<Integer> rules = new ArrayList<Integer>();

		int rand = o.nextInt(ruleSize);
		
		output = r.get(rand);
		
		rules.add(rand);
		
		ruleString += g.get(rand).name;

		for(int i = 0; i < ruleSize; i++){

			while((rules.indexOf(rand) > -1) && (i < ruleSize-1)){
				//System.out.println("rand == " + rand + "\n" + "ruleSize == " + ruleSize+ "\ninside while, i = " + i);
				rand = o.nextInt(ruleSize);

			}
			rules.add(rand);
			//changed from: logic = Math.random * logicStrings.length; and rand = Math.random * r.size()
			logic = o.nextInt(logicStrings.length);



			if(logic == 0) {

				output = output.and(r.get(rand));

				ruleString += "_and_" + g.get(rand).name;
			}

			else if(logic == 1) {

				output = output.or(r.get(rand));

				ruleString += "_or_" + g.get(rand).name;
			}

			else if(logic == 2) {

				output = output.xor(r.get(rand));


				ruleString += "_xor_" + g.get(rand).name;
			}

			else if(logic== 3) { output = addAndNotRule(output, r.get(rand));

			ruleString += "_andNOT_" + g.get(rand).name;

			}

			else if(logic == 4) {

				output = addOrNotRule(output, r.get(rand));

				ruleString += "_orNot_" + g.get(rand).name;
			}
		}

		return output;


	}
	/*
void generateI(Vector<Rule> get){



	Random r = new Random();

	int nRules = Math.abs(r.nextInt(branchingFactor));
	if(nRules == 0)nRules = 2;

	short logic = 0;

	short geneIndex = 0;

	int index = 0;

	this.rules = new short[nRules]; 
	this.logics = new short[nRules];

	while(index < nRules){


		geneIndex = (short)r.nextInt(get.size());
		logic = (short)r.nextInt(5);//number of logical operators
		rules[index] = geneIndex;
		logics[index] = logic;
		index++;
	}
		//code[index][0] = logic;


}//end generate

	 */


	Rule addAndRule(Rule oldRule, Rule newRule){





		oldRule = oldRule.and(newRule);

		return oldRule;

	}

	Rule addOrRule(Rule oldRule, Rule newRule){








		oldRule = oldRule.or(newRule);

		return oldRule;

	}

	Rule addXorRule(Rule oldRule, Vector<Rule> set, int index){



		Rule newRule;
		newRule = oldRule.xor(set.get(index));

		return newRule;

	}

	Rule addAndNotRule(Rule oldRule, Rule notRule){



		Rule not = new NotRule(notRule);

		oldRule = oldRule.and(not);

		return oldRule;

	}


	Rule addOrNotRule(Rule oldRule, Rule notRule){



		Rule not = new NotRule(notRule);

		oldRule = oldRule.or(not);

		return oldRule;

	}

	Rule addAndInPipeRule(Rule oldRule, Indicator<Decimal> i, Decimal up, Decimal down){

		Rule r = new InPipeRule(i, up, down);

		oldRule = oldRule.and(r);

		return oldRule;


	}

	Rule addOrInPipeRule(Rule oldRule, Indicator<Decimal> i, Decimal up, Decimal down){

		Rule r = new InPipeRule(i, up, down);

		oldRule = oldRule.or(r);

		return oldRule;


	}



	Rule addNotInPipeRule(Rule oldRule, Indicator<Decimal> i, Decimal up, Decimal down){

		Rule r = new InPipeRule(i, up, down);

		Rule not = new NotRule(r);

		oldRule = oldRule.and(not);

		return oldRule;

	}

	Rule addAndInPipeRule(Rule oldRule, Indicator<Decimal> i, Indicator<Decimal> j, Indicator<Decimal> k){

		Rule r = new InPipeRule(i, j, k);

		oldRule = oldRule.and(r);

		return oldRule;


	}

	Rule addOrInPipeRule(Rule oldRule, Indicator<Decimal> i, Indicator<Decimal> j, Indicator<Decimal> k){

		Rule r = new InPipeRule(i, j, k);

		oldRule = oldRule.or(r);

		return oldRule;


	}

	Rule addNotInPipeRule(Rule oldRule, Indicator<Decimal> i, Indicator<Decimal> j, Indicator<Decimal> k){

		Rule r = new InPipeRule(i, j, k);

		Rule not = new NotRule(r);

		oldRule = oldRule.and(not);

		return oldRule;

	}



	Rule addOrNegateRule(Rule oldRule, Vector<Rule> set){

		Random i = new Random();

		int index = i.nextInt(set.size());
		if(index == 0) index = 1;
		Rule notRule = new NotRule(set.elementAt(index));

		Rule tempRule = oldRule;

		tempRule = tempRule.and(notRule);

		return tempRule;

	}




	/*
void evolve(int nCrossOver){

	BitStringFactory b = new BitStringFactory(nGenes); 

	LinkedList<EvolutionaryOperator<Decimal>> operators = new LinkedList<EvolutionaryOperator<Decimal>>();

	EvolutionaryOperator<boolean[]> d = new DoubleArrayCrossover(nGenes/nCrossOver);

	EvolutionaryOperator<double[]> e = new DoubleArrayMutation();

	operators.add(d);



	EvolutionaryOperator<double[]> pipeline = new EvolutionPipeline<double[]>(operators);




	List<EvolutionaryOperator<String>> operators



    = new LinkedList<EvolutionaryOperator<String>>();
operators.add(new StringCrossover());
operators.add(new StringMutation(chars, new Probability(0.02)));

EvolutionaryOperator<String> pipeline
    = new EvolutionPipeline<String>(operators);



EvolutionEngine<String> engine = new GenerationalEvolutionEngine<String>(factory,
                                          evolutionaryOperator,
                                          fitnessEvaluator,
                                          selectionStrategy,
                                          rng);

}//end evolve

	 */
}

