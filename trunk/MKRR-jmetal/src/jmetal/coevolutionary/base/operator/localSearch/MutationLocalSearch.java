package jmetal.coevolutionary.base.operator.localSearch;

import java.util.Comparator;

import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.operator.localSearch.LocalSearch;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;
import jmetal.coevolutionary.base.operator.comparator.OverallConstraintViolationComparator;
import jmetal.util.JMException;


/**
 * This class implements an local search operator based in the use of a 
 * mutation operator. An archive is used to store the non-dominated solutions
 * found during the search.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class MutationLocalSearch extends LocalSearch {

	private static final long serialVersionUID = 6132501458863378035L;

	private Problem     problem_;     ///< Stores the problem to solve
	private SolutionSet archive_;     ///< Stores a reference to the archive in which the non-dominated solutions are inserted
	private Comparator  constraintComparator_ ; ///< Stores comparators for dealing with constraints checking
	private Comparator  dominanceComparator_ ;  ///< Stores comparators for dealing with dominance checking
	private Operator    mutationOperator_;      ///< Stores the mutation operator
	int                 evaluations_ ;          ///< Stores the number of evaluations_ carried out


	/**
	 * Constructor. 
	 * Creates a new local search object.
	 * @param problem The problem to solve
	 * @param mutationOperator The mutation operator 
	 * @param archive The archive
	 */
	public MutationLocalSearch( Problem     problem,
                                Operator    mutationOperator,
                                SolutionSet archive) {

		evaluations_          = 0      ;
		problem_              = problem;
		archive_              = archive;
		dominanceComparator_  = new DominanceComparator();
		constraintComparator_ = new OverallConstraintViolationComparator();
	} // MutationLocalSearch


	/**
	 * Constructor. 
	 * Creates a new local search object.
	 * @param problem The problem to solve
	 * @param mutationOperator The mutation operator 
	 */
	public MutationLocalSearch( Problem problem, Operator mutationOperator ) {

		evaluations_          = 0 ;
		problem_              = problem;
		mutationOperator_     = mutationOperator;
		dominanceComparator_  = new DominanceComparator();
		constraintComparator_ = new OverallConstraintViolationComparator();
	} // MutationLocalSearch


	/**
	 * Executes the local search. The maximum number of iterations is given by 
	 * the param "improvementRounds", which is in the parameter list of the 
	 * operator. The archive to store the non-dominated solutions is also in the 
	 * parameter list.
	 * @param object Object representing a solution
	 * @return An object containing the new improved solution
	 * @throws JMException 
	 */
	public Object execute( Object object ) throws JMException {

		return this.execute( object , 0 );
	} // execute
	
	
	
	/**
	 * Executes the local search. The maximum number of iterations is given by 
	 * the param "improvementRounds", which is in the parameter list of the 
	 * operator. The archive to store the non-dominated solutions is also in the 
	 * parameter list. The island identificator is needed.
	 * 
	 * @param object Object representing a solution
	 * @param islandId the island identificator
	 * @return An object containing the new improved solution
	 * @throws JMException 
	 */
	public Object execute( Object object , int islandId ) throws JMException {
		int i    = 0;
		int best = 0;

		evaluations_ = 0;
		Solution solution    = (Solution) object;
		Integer  roundsParam = (Integer) getParameter( "improvementRounds" );
		archive_             = (SolutionSet) getParameter( "archive" );
		
		int rounds = (roundsParam == null)? 0 : roundsParam.intValue();

		if (rounds <= 0)
			return new Solution(solution);

		do {
			i++;
			Solution mutatedSolution = new Solution( solution );
			mutationOperator_.execute( mutatedSolution , islandId );

			// Evaluate the getNumberOfConstraints
			if (problem_.getNumberOfConstraints() > 0) {
				problem_.evaluateConstraints( mutatedSolution );
				best = constraintComparator_.compare( mutatedSolution , solution );
				if (best == 0) {
					// none of them is better that the other one
					problem_.evaluate( mutatedSolution , islandId );
					evaluations_++;
					best = dominanceComparator_.compare( mutatedSolution , solution );
				} // if
				else if (best == -1) {
					// mutatedSolution is best
					problem_.evaluate( mutatedSolution , islandId );
					evaluations_++;
				} // else
			} // if
			else {
				problem_.evaluate( mutatedSolution , islandId );
				evaluations_++;
				best = dominanceComparator_.compare( mutatedSolution , solution );
			} // else
			if (best == -1) // This is: Mutated is best
				solution = mutatedSolution;
			else if (best == 1) // This is: Original is best
				//delete mutatedSolution
				;
			else {
				// This is mutatedSolution and original are non-dominated
				// this.archive_.addIndividual(new Solution(solution));                
				// solution = mutatedSolution;
				if (archive_ != null)
					archive_.add(mutatedSolution);
			} // else                            
		} while (i < rounds);
		
		return new Solution( solution );
	} // execute


	/** 
	 * Returns the number of evaluations maded
	 */
	public int getEvaluations() {

		return evaluations_;
	} // evaluations

} // MutationLocalSearch
