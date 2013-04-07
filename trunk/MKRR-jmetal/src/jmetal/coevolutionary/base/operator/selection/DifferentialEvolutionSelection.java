package jmetal.coevolutionary.base.operator.selection;


import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


/**
 * Class representing the selection operator used in differential evolution
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class DifferentialEvolutionSelection extends Operator {

	private static final long serialVersionUID = 2828487791690404779L;

	/**
	 * Constructor
	 */
	DifferentialEvolutionSelection() {
	} // Constructor


	/**
	 * Executes the operation
	 * @param object An object containing the population and the position (index)
	 *               of the current individual
	 * @return An object containing the three selected parents
	 */
	public Object execute(Object object,int none) throws JMException {
		Object[] parameters = (Object[])object ;
		SolutionSet population = (SolutionSet)parameters[0];
		int         index      = (Integer)parameters[1] ;

		Solution[] parents = new Solution[3] ;
		int r1, r2, r3 ;

		if (population.size() < 3)
			throw new JMException("DifferentialEvolutionSelection: the population has less than tree solutions") ;

		do {
			r1 = (int)(PseudoRandom.randInt(0,population.size()-1));
		} while( r1==index );
		do {
			r2 = (int)(PseudoRandom.randInt(0,population.size()-1));
		} while( r2==index || r2==r1);
		do {
			r3 = (int)(PseudoRandom.randInt(0,population.size()-1));
		} while( r3==index || r3==r1 || r3==r2 );

		parents[0] = population.get(r1) ;
		parents[1] = population.get(r2) ;
		parents[2] = population.get(r3) ;

		return parents ;
	} // execute

} // DifferentialEvolutionSelection
