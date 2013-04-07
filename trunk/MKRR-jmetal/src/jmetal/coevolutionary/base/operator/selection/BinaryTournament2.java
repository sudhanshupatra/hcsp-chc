package jmetal.coevolutionary.base.operator.selection;


import java.util.Comparator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;
import jmetal.util.PseudoRandom;


/**
 * This class implements an opertor for binary selections using the same code
 * in Deb's NSGA-II implementation.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class BinaryTournament2 extends Operator{

	private static final long serialVersionUID = 4938421116618532932L;

	private Comparator dominance_; ///< dominance_ store the <code>Comparator</code> for check dominance_
	private int        a_[]      ; ///< a_ stores a permutation of the solutions in the solutionSet used
	private int        index_=0  ; ///< index_ stores the actual index for selection


	/**
	 * Constructor
	 * Creates a new instance of the Binary tournament operator (Deb's
	 * NSGA-II implementation version)
	 */
	public BinaryTournament2() {

		dominance_ = new DominanceComparator();              
	} // BinaryTournament2


	/**
	 * Performs the operation
	 * @param object Object representing a SolutionSet
	 * @return the selected solution
	 */
	public Object execute(Object object,int none){

		SolutionSet population = (SolutionSet) object;
		if (index_ == 0) {
			a_= (new jmetal.util.PermutationUtility()).intPermutation(population.size());
		} // if


		Solution solution1,solution2;
		solution1 = population.get(a_[index_]);
		solution2 = population.get(a_[index_+1]);

		index_ = (index_ + 2) % population.size();

		int flag = dominance_.compare(solution1,solution2);
		if (flag == -1)
			return solution1;
		else if (flag == 1)
			return solution2;
		else if (solution1.getCrowdingDistance() > solution2.getCrowdingDistance())
			return solution1;
		else if (solution2.getCrowdingDistance() > solution1.getCrowdingDistance())
			return solution2;
		else
			if (PseudoRandom.randDouble()<0.5)
				return solution1;
			else
				return solution2;        
	} // execute

} // BinaryTournament2
