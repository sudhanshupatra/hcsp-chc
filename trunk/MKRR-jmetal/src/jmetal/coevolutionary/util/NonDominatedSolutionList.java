package jmetal.coevolutionary.util;


import java.util.*;
import jmetal.base.operator.comparator.SolutionComparator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;


/** 
 * This class implements an unbound list of non-dominated solutions
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class NonDominatedSolutionList extends SolutionSet{
	private static final long serialVersionUID = -7854756093076777613L;

	/**
	 * Stores a <code>Comparator</code> for dominance checking
	 */
	private Comparator dominance_ = new DominanceComparator(); 

	/**
	 * Stores a <code>Comparator</code> for checking if two solutions are equal
	 */
	private static final Comparator equal_ = new SolutionComparator();     


	/**
	 * The objects of this class are lists of non-dominated solutions according to
	 * a Pareto dominance comparator.
	 * 
	 * @param position
	 * @param numberOfIslands
	 * @param numberOfSolutions
	 */
	public NonDominatedSolutionList( int position , int numberOfIslands , int numberOfSolutions ) {
		
		super( position , numberOfIslands , numberOfSolutions );
	} // NonDominatedList


	// TODO NonDominatedSolutionList: Corregir la documentaci—n de los constructores

	/**
	 * Constructor.
	 * This constructor creates a list of non-dominated individuals using a
	 * comparator object.
	 * @param position Position of solution
	 * @param numberOfIslands
	 * @param numberOfSolutions
	 * @param dominance The comparator for dominance checking.
	 */
	public NonDominatedSolutionList( int position , int numberOfIslands , int numberOfSolutions , Comparator dominance ) {
		super( position , numberOfIslands , numberOfSolutions );
		dominance_ = dominance;
	} // NonDominatedList

	
	/** Inserts a solution in the list
	 * @param solution The solution to be inserted.
	 * @return true if the operation success, and false if the solution is 
	 * dominated or if an identical individual exists.
	 * The decision variables can be null if the solution is read from a file; in
	 * that case, the domination tests are omitted
	 */
	public boolean add( Solution solution ){
		Iterator<Solution> iterator = solutionsList_.iterator();

		if (solution.getDecisionVariables() != null) {
			while (iterator.hasNext()) {
				Solution listIndividual = iterator.next();
				int flag = dominance_.compare(solution,listIndividual);

				if (flag == -1) {  // A solution in the list is dominated by the new one
					iterator.remove();
				} // if
				else if (flag == 0) { // Non-dominated solutions
					flag = equal_.compare(solution,listIndividual);
					if (flag == 0) {
						return false;   // The new solution is in the list  
					} // if
				} // else if
				else if (flag == 1) { // The new solution is dominated
					return false;
				} // else if
			} // while 
		} // if

		//At this point, the solution is inserted into the list
		solutionsList_.add(solution);                

		return true;        
	} // add              

} // NonDominatedList
