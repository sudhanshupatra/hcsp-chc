package jmetal.coevolutionary.base.operator.crossover;

import jmetal.base.Configuration;
import jmetal.base.Variable;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Solution;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 * Class representing a one point crossover operator.
 * 
 * This class allows to apply a One Point crossover operator using two parent
 * solutions.
 * 
 * @author Juan A. Ca–ero (Inspired in SinglePointCrossover)
 * @version 1.0
 */
public class OnePointCrossover extends Operator {

	private static final long serialVersionUID = -6134245262142757366L;

	/**
	 * Constructor
	 * Creates a new instance of the one point crossover operator
	 */
	public OnePointCrossover(){
	} // OnePointCrossover


	/**
	 * Perform the crossover operation. 
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containig the two offsprings
	 * @throws JMException 
	 */
	public Solution[] doCrossover( double probability ,
                                   Solution parent1   ,
                                   Solution parent2   ) throws JMException {
		Solution [] offSpring = new Solution[2];

		offSpring[0] = new Solution( parent1 );
		offSpring[1] = new Solution( parent2 );

		try {         
			if (PseudoRandom.randDouble() < probability) {
				int len = offSpring[0].numberOfVariables();
				
				Variable[] vars1 = offSpring[0].getDecisionVariables().variables_;
				Variable[] vars2 = offSpring[1].getDecisionVariables().variables_;

				int crossPnt = PseudoRandom.randInt(1,len-1);

				int remain = len-crossPnt;
				Variable[] buffer = new Variable[ remain ];
				System.arraycopy( vars1  , crossPnt , buffer , 0        , remain );
				System.arraycopy( vars2  , crossPnt , vars1  , crossPnt , remain );
				System.arraycopy( buffer , 0        , vars2  , crossPnt , remain );

			} // if
		} // try
		catch (ClassCastException e1) {   
			Configuration.logger_.severe("OnePointCrossover.doCrossover: Cannot perfom " +
			"OnePointCrossover");
			throw new JMException("Exception in OnePointCrossover.doCrossover()") ; 
		} // catch
		return offSpring;                                              
	} // doCrossover


	/**
	 * Executes the operation
	 * @param object An object containing an array of two solutions 
	 * @param none
	 * @return An object containing an array with the offSprings
	 * @throws JMException 
	 */
	public Object execute( Object object , int none ) throws JMException {
		Solution [] parents = (Solution [])object;

		Double probability = (Double)getParameter("probability");
		
		if (parents.length < 2) {
			Configuration.logger_.severe("OnePointCrossover.execute: operator " +
			"needs two parents");
			throw new JMException("Exception in OnePointCrossover.execute()") ;
		} // if
		else if (probability == null){
			Configuration.logger_.severe("OnePointCrossover.execute: probability " +
			"not specified");
			throw new JMException("Exception in OnePointCrossover.execute()") ;  
		} // else if

		Solution [] offSpring;
		offSpring = doCrossover( probability.doubleValue() , parents[0], parents[1] );

		for (int i = 0; i < offSpring.length; i++) {
			offSpring[i].setCrowdingDistance(0.0);
			offSpring[i].setRank(0);
		} // for
		return offSpring;
	} // execute

} // OnePointCrossover
