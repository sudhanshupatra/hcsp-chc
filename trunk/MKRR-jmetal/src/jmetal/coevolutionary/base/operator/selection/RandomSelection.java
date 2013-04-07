package jmetal.coevolutionary.base.operator.selection;

import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.util.PseudoRandom;

/**
 * This class implements a random selection operator used for selecting two
 * random parents.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class RandomSelection extends Operator {

	private static final long serialVersionUID = 7446171924839213334L;

	/**
	 * Performs the operation
	 * @param object Object representing a SolutionSet.
	 * @return an object representing an array with the selected parents
	 */
	public Object execute( Object object , int none ) {
		SolutionSet population = (SolutionSet)object;
		int pos1, pos2;
		pos1 = PseudoRandom.randInt(0,population.size()-1);
		pos2 = PseudoRandom.randInt(0,population.size()-1);
		while ((pos1 == pos2) && (population.size()>1)) {
			pos2 = PseudoRandom.randInt(0,population.size()-1);
		} // while

		Solution [] parents = new Solution[2];
		parents[0] = population.get(pos1);
		parents[1] = population.get(pos2);

		return parents;
	} // Execute     

} // RandomSelection
