package jmetal.coevolutionary.base.operator.mutation;


import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.base.Configuration.VariableType_; 
import jmetal.base.variable.Permutation;
import jmetal.coevolutionary.base.Solution;


/**
 * This class implements a swap mutation operator.
 * NOTE: the operator is applied to the first variable of the solutions, and 
 * the type of those variables must be <code>VariableType_.Permutation</code>.
 * @author Antonio J.Nebro
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class SwapMutation extends Operator{

	private static final long serialVersionUID = 978614045262117488L;


	/** 
	 * Constructor
	 */
	public SwapMutation() {    
	} // Constructor

	/**
	 * Performs the operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public void doMutation(double probability, Solution solution) throws JMException {   
		int permutation[] ;
		int permutationLength ;
		if (solution.getDecisionVariables().variables_[0].getVariableType() ==
			VariableType_.Permutation) {

			permutationLength = ((Permutation)solution.getDecisionVariables().variables_[0]).getLength() ;
			permutation = ((Permutation)solution.getDecisionVariables().variables_[0]).vector_ ;

			if (PseudoRandom.randDouble() < probability) {
				int pos1 ;
				int pos2 ;

				pos1 = PseudoRandom.randInt(0,permutationLength-1) ;
				pos2 = PseudoRandom.randInt(0,permutationLength-1) ;

				while (pos1 == pos2) {
					if (pos1 == (permutationLength - 1)) 
						pos2 = PseudoRandom.randInt(0, permutationLength- 2);
					else 
						pos2 = PseudoRandom.randInt(pos1, permutationLength- 1);
				} // while
				// swap
				int temp = permutation[pos1];
				permutation[pos1] = permutation[pos2];
				permutation[pos2] = temp;    
			} // if
		} // if
		else  {
			Configuration.logger_.severe("SwapMutation.doMutation: invalid type. " +
					""+ solution.getDecisionVariables().variables_[0].getVariableType());

			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".doMutation()") ;
		} // catch               
	} // doMutation

	/**
	 * Executes the operation
	 * @param object An object containing the solution to mutate
	 * @return an object containing the mutated solution
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public Object execute( Object object , int none ) throws JMException {
		Solution solution = (Solution)object;

		Double probability = (Double)getParameter("probability");       
		if (probability == null) {
			Configuration.logger_.severe("SwapMutation.execute: probability " +
			"not specified");
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;  
		} // if

		this.doMutation(probability.doubleValue(), solution);
		return solution;
	} // execute

} // SwapMutation
