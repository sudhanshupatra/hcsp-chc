package jmetal.coevolutionary.base.archive;


import java.util.Comparator;
import jmetal.coevolutionary.base.operator.comparator.EqualSolutions;
import jmetal.coevolutionary.base.operator.comparator.CrowdingDistanceComparator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;
import jmetal.coevolutionary.util.Distance;


/**
 * This class implements a bounded archive based on crowding distances (as
 * defined in NSGA-II).
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class CrowdingArchive extends SolutionSet {    

	private static final long serialVersionUID = -4581904088498940961L; ///< Eclipse mandatory

	private int        maxSize_         ; ///< Stores the maximum size of the archive.
	private int        objectives_      ; ///< stores the number of the objectives.
	private Comparator dominance_       ; ///< Stores a <code>Comparator</code> for dominance checking.
	private Comparator equals_          ; ///< Stores a <code>Comparator</code> for equality checking (in the objective space).
	private Comparator crowdingDistance_; ///< Stores a <code>Comparator</code> for checking crowding distances.
	private Distance   distance_        ; ///< Stores a <code>Distance</code> object, for distances utilities


	/**
	 * Constructor.
	 * @param loadingPosition position to insert the solutionset
	 * @param numberOfIslands number of islands to use
	 * @param numberOfSolutions number of common solutions in order to maintain the diversity
	 * @param maxSize The maximum size of the archive.
	 * @param numberOfObjectives The number of objectives.
	 */
	public CrowdingArchive( int loadingPosition    ,
			                int numberOfIslands    ,
			                int numberOfSolutions  ,
			                int maxSize            ,
			                int numberOfObjectives ) {

		super( loadingPosition , numberOfIslands , numberOfSolutions , maxSize );
		maxSize_          = maxSize;
		objectives_       = numberOfObjectives;        
		dominance_        = new DominanceComparator();
		equals_           = new EqualSolutions();
		crowdingDistance_ = new CrowdingDistanceComparator();
		distance_         = new Distance();

	} // CrowdingArchive


	/**
	 * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
	 * is dominated by any member of the archive, then it is discarded. If the 
	 * <code>Solution</code> dominates some members of the archive, these are
	 * removed. If the archive is full and the <code>Solution</code> has to be
	 * inserted, the solutions are sorted by crowding distance and the one having
	 * the minimum crowding distance value.
	 * 
	 * @param solution The <code>Solution</code>
	 * @return true if the <code>Solution</code> has been inserted, false otherwise.
	 */
	public boolean add( Solution solution ){

		int flag = 0;
		int i    = 0;
		
		while (i < solutionsList_.size()){
			Solution aux = get( i );

			flag = dominance_.compare( solution , aux );
			if ( flag==1 )                  // The solution to add is dominated
				return false;               // Discard the new solution
			else if ( flag==-1 )            // A solution in the archive is dominated
				solutionsList_.remove(i);   // Remove it from the population            
			else {
				if ( equals_.compare(aux,solution)==0 ) // There's an equal solution in the population
					return false;                       // Discard the new solution
				i++;
			} // else
		} // while

		// Insert the solution into the archive
		solutionsList_.add( solution );
		if( size()>maxSize_ ) {
			// The archive is full
			distance_.crowdingDistanceAssignment( this , objectives_ );
			sort( crowdingDistance_ );
			remove( maxSize_ ); // Remove the last
		} // if
		return true;
	} // add


} // CrowdingArchive
