/**
 * This package stores the selection operators
 */
package jmetal.coevolutionary.base.operator.selection;


import java.util.Comparator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.BinaryTournamentComparator;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.util.PseudoRandom;


/**
 * This class implements an opertor for binary selections.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero (improvements)
 * @version 1.1
 */
@SuppressWarnings("unchecked")
public class BinaryTournament extends Operator{

	private static final long serialVersionUID = -6155510776793236308L;

	private Comparator comparator_; ///< Stores the <code>Comparator</code> used to compare two solutions

	/**
	 * Creates a new Binary tournament operator using a BinaryTournamentComparator
	 */
	public BinaryTournament(){

		comparator_ = new BinaryTournamentComparator();
	} // BinaryTournament


	/**
	 * Constructor
	 * Creates a new Binary tournament with a specific <code>Comparator</code>
	 * @param comparator The comparator
	 */
	public BinaryTournament(Comparator comparator) {

		comparator_ = comparator;
	} // Constructor


	/**
	 * Performs the operation
	 * @param object Object representing a SolutionSet
	 * @return the selected solution
	 */
	public Object execute(Object object,int none){
		SolutionSet SolutionSet = (SolutionSet) object;
		Solution solution1,solution2;
		
		int[] idx = RandomVector.getRandomVector_Int( 2 , SolutionSet.size()-1 );
	    solution1 = SolutionSet.get( idx[0] );
	    solution2 = SolutionSet.get( idx[1] );

		int flag = comparator_.compare( solution1 , solution2 );
		if (flag == -1)
			return solution1;
		else if (flag == 1)
			return solution2;
		else
			if ( PseudoRandom.randDouble()<0.5 )
				return solution1;
			else
				return solution2;                       
	} // execute

} // BinaryTournament
