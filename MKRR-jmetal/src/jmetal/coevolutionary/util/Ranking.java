package jmetal.coevolutionary.util;


import java.util.Comparator;
import java.util.Iterator;

import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.OverallConstraintViolationComparator;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;
import java.util.*;


/**
 * This class implements some facilities for ranking solutions.
 * Given a <code>SolutionSet</code> object, their solutions are ranked 
 * according to scheme proposed in NSGA-II; as a result, a set of subsets 
 * are obtained. The subsets are numbered starting from 0 (in NSGA-II, the 
 * numbering starts from 1); thus, subset 0 contains the non-dominated 
 * solutions, subset 1 contains the non-dominated solutions after removing those
 * belonging to subset 0, and so on.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class Ranking {

	/**
	 * The <code>SolutionSet</code> to rank
	 */
	private SolutionSet   solutionSet_ ;

	/**
	 * An array containing all the fronts found during the search
	 */
	private SolutionSet[] ranking_  ;

	/**
	 * stores a <code>Comparator</code> for dominance checking
	 */
	private static final Comparator dominance_ = new DominanceComparator();

	/**
	 * stores a <code>Comparator</code> for Overal Constraint Violation Comparator
	 * checking
	 */
	private static final Comparator constraint_ = new OverallConstraintViolationComparator();

	/** 
	 * Constructor.
	 * @param solutionSet The <code>SolutionSet</code> to be ranked.
	 */       
	public Ranking( SolutionSet solutionSet ) {
		solutionSet_ = solutionSet ;

		// dominateMe[i] contains the number of solutions dominating i        
		int solutioSetSize = solutionSet_.size();
		int [] dominateMe = new int[ solutioSetSize ];

		// iDominate[k] contains the list of solutions dominated by k
		List<Integer> [] iDominate = new List[ solutioSetSize ];

		// front[i] contains the list of individuals belonging to the front i
		List<Integer> [] front = new List[ solutioSetSize+1 ];

		// flagDominate is an auxiliar variable
		int flagDominate;    

		// Initialize the fronts 
		for (int i = 0; i < front.length; i++)
			front[i] = new LinkedList<Integer>();

		// Use a buffer to speedup the acces of individuals in the nested for's
		Solution[] bufferOfSolutions = new Solution[ solutioSetSize ];
		Iterator<Solution> it = solutionSet_.iterator();
		int i=0;
		while( it.hasNext() )
			bufferOfSolutions[i++] = it.next();
		
		//-> Fast non dominated sorting algorithm
		for( int p=0 ; p < solutioSetSize ; ++p ) {
			// Initialice the list of individuals that i dominate and the number
			// of individuals that dominate me
			iDominate[ p ]  = new LinkedList<Integer>();
			dominateMe[ p ] = 0;
			// For all q individuals , calculate if p dominates q or vice versa
			Solution solutionP = bufferOfSolutions[p];
				//solutionSet_.get(p);

			for( int q=0; q<solutioSetSize ; ++q ) {
				Solution solutionQ = bufferOfSolutions[q];
					//solutionSet_.get(q);

				flagDominate = constraint_.compare( solutionP , solutionQ );
				if ( flagDominate==0 )
					flagDominate = dominance_.compare( solutionP , solutionQ );

				if( flagDominate==-1 )
					iDominate[ p ].add( new Integer(q) );

				else if (flagDominate == 1)
					dominateMe[p]++;

			} // for

			// If nobody dominates p, p belongs to the first front
			if( dominateMe[p]==0 ) {
				front[0].add(new Integer(p));
				solutionSet.get(p).setRank(0);
			}   // if         
		} // for

		//Obtain the rest of fronts
		i = 0;
		Iterator<Integer> it1, it2 ; // Iterators
		while (front[i].size()!= 0) {
			i++;
			it1 = front[i-1].iterator();
			while (it1.hasNext()) {
				it2 = iDominate[it1.next().intValue()].iterator();
				while (it2.hasNext()) {
					int index = it2.next().intValue();
					dominateMe[index]--;
					if (dominateMe[index]==0) {
						front[i].add(new Integer(index));
						solutionSet_.get(index).setRank(i);
					} // if
				} // while
			} // while
		} // while

		ranking_ = new SolutionSet[i];
		// 0,1,2,....,i-1 are front, then i fronts
		for (int j = 0; j < i; j++) {
			ranking_[j] = new SolutionSet( solutionSet.getLoadingPosition()   ,
					                       solutionSet.getNumberOfIslands()   ,
					                       solutionSet.getNumberOfSolutions() ,
					                       front[j].size()                    );
			ranking_[j].setBestExternResults( solutionSet_ );
			it1 = front[j].iterator();
			while (it1.hasNext()) {
				ranking_[j].add( solutionSet.get(it1.next().intValue()) );       
			} // while
		} // for

	} // Ranking


	/**
	 * Returns a <code>SolutionSet</code> containing the solutions of a given rank. 
	 * @param rank The rank
	 * @return Object representing the <code>SolutionSet</code>.
	 */
	public SolutionSet getSubfront(int rank) {
//		if (ranking_.length <= rank)
////			for (int i=0; i<rank; i++) {
////				ranking_[i] = new SolutionSet(0,0,0);
////				ranking_[i] = null;
////			}
//
//			return null;
		
		return ranking_[rank];
	} // getSubFront

	/** 
	 * Returns the total number of subFronts founds.
	 */
	public int getNumberOfSubfronts() {
		return ranking_.length;
	} // getNumberOfSubfronts
	
	
	/**
	 * Returns a <code>SolutionSet</code> containing all the solutions sorted by subfronts. 
	 * @return
	 */
	public SolutionSet getAllSubfronts() {
		
		if ( ranking_.length > 0 ) {
			SolutionSet subfronts = ranking_[0];

			for( int f=1 ; f<ranking_.length ; ++f )
				subfronts = subfronts.union( ranking_[f] );

			return(subfronts);
		} // if
		else {
			return( solutionSet_ );
		} // else
	} // getNumberOfSubfronts


} // Ranking
