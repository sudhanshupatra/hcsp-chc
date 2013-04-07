package jmetal.coevolutionary.base.operator.selection;


import java.util.Comparator;
import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.CrowdingComparator;
import jmetal.coevolutionary.util.Ranking;
import jmetal.coevolutionary.util.Distance;
import jmetal.util.JMException;


/** 
 * This class implements a selection for selecting a number of solutions from
 * a solutionSet. The solutions are taken by mean of its ranking and 
 * crowding ditance values.
 * <b>NOTE:</b> if you use the default constructor, the problem has to be passed as
 * a parameter before invoking the execute() method -- see lines 67 - 74
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class RankingAndCrowdingSelection extends Operator {

	private static final long serialVersionUID = -5204799468091881046L;

	private Problem problem_; ///< stores the problem to solve 


	/**
	 * stores a <code>Comparator</code> for crowding comparator checking.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator crowdingComparator_ = 
		new CrowdingComparator();


	/**
	 * stores a <code>Distance</code> object for distance utilities.
	 */
	private static final Distance distance_ = new Distance();

	/**
	 * Constructor
	 */
	public RankingAndCrowdingSelection() {
		problem_ = null ;
	} // RankingAndCrowdingSelection

	/**
	 * Constructor
	 * @param problem Problem to be solved
	 */
	public RankingAndCrowdingSelection(Problem problem) {
		problem_ = problem;
	} // RankingAndCrowdingSelection

	/**
	 * Performs the operation
	 * @param object Object representing a SolutionSet.
	 * @return an object representing a <code>SolutionSet</code> with the selected parents
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public Object execute (Object object,int none) throws JMException {
		SolutionSet population = (SolutionSet) object;
		int populationSize     = (Integer)parameters_.get("populationSize");
		int loadingPosition    = population.getLoadingPosition();
		int numberOfPieces     = population.getNumberOfIslands();
		int numberOfSolutions  = population.getNumberOfSolutions();

		SolutionSet result     = new SolutionSet( loadingPosition , numberOfPieces , numberOfSolutions , populationSize );
		result.setBestExternResults( population );

		if ( problem_ == null ) {
			problem_ = (Problem) getParameter("problem");
			if ( problem_ == null ) {
				Configuration.logger_.severe("RankingAndCrowdingSelection.execute: " +
				"problem not specified") ;
				Class cls = java.lang.String.class;
				String name = cls.getName(); 
				throw new JMException("Exception in " + name + ".execute()") ;  
			} // if
		} // if

		//->Ranking the union
		Ranking ranking = new Ranking(population);                        

		int remain = populationSize;
		int index  = 0;
		SolutionSet front = null;
		population.clear();

		//-> Obtain the next front
		front = ranking.getSubfront(index);

		while ((remain > 0) && (remain >= front.size())){                
			//Asign crowding distance to individuals
			distance_.crowdingDistanceAssignment(front,problem_.getNumberOfObjectives());                
			//Add the individuals of this front
			for (int k = 0; k < front.size(); k++ ) {
				result.add(front.get(k));
			} // for

			//Decrement remaint
			remain = remain - front.size();

			//Obtain the next front
			index++;
			if (remain > 0) {
				front = ranking.getSubfront(index);
			} // if        
		} // while

		//-> remain is less than front(index).size, insert only the best one
		if (remain > 0) {  // front containt individuals to insert                        
			distance_.crowdingDistanceAssignment(front,problem_.getNumberOfObjectives());
			front.sort(crowdingComparator_);
			for (int k = 0; k < remain; k++) {
				result.add(front.get(k));
			} // for

			remain = 0; 
		} // if

		return result;
	} // execute

} // RankingAndCrowding
