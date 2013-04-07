/**
 * This package stores the mutation operators
 */
package jmetal.coevolutionary.base.operator.mutation;


import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.variable.Binary;
import jmetal.coevolutionary.base.Solution;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary solutions and it is applied to the 
 * whole solution as a single variable.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class BitFlipMutation extends Operator{
	
	private static final long serialVersionUID = 6637934909557237798L;

	
	/**
	 * Constructor
	 * Creates a new instance of the Bit Flip mutation operator
	 */
	public BitFlipMutation() {    
	} // BitFlipMutation

	/**
	 * Perform the mutation operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public void doMutation(double probability, Solution solution) throws JMException {        
		try {
			for (int i = 0; i < solution.getDecisionVariables().size(); i++) {                
				for (int j = 0; j < ((Binary)solution.getDecisionVariables().variables_[i]).getNumberOfBits(); j++) {
					if (PseudoRandom.randDouble() < probability) {
						((Binary) solution.getDecisionVariables().variables_[i]).bits_.flip(j);
					} // if
				} // for
			} //for

			for (int i = 0; i < solution.getDecisionVariables().size(); i++)
				((Binary) solution.getDecisionVariables().variables_[i]).decode();

		} catch (ClassCastException e1) {
			Configuration.logger_.severe("BitFlipMutation.doMutation: " + 
					"ClassCastException error" + e1.getMessage()) ;
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".doMutation()") ;    
		} // catch      
	} // doMutation


	/**
	 * Executes the operation
	 * @param object An object containing a solution to mutate
	 * @return An object containing the mutated solution
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public Object execute(Object object,int none) throws JMException {
		Solution solution = (Solution)object;

		if ((solution.getType() != SolutionType_.Binary) && 
				(solution.getType() != SolutionType_.BinaryReal)) {
			Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary' or " +
					"'BinaryReal', but " +solution.getType() + " is obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;
		} // if 

		Double probability = (Double)getParameter("probability");     
		if (probability == null) {
			Configuration.logger_.severe("BitFlipMutation.execute: probability not " +
			"specified");
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;  
		} // if

		doMutation(probability.doubleValue(),solution);
		return solution;
	} // execute

} // BitFlipMutation
