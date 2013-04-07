/**
 * RankingNonRepeatedSols.java
 *
 * This file is the original Ranking file in jMetal, but
 * it considers that a copy of a solution already in the 
 * front is dominated by the solution in the front.
 * 
 * We do this in order to avoid having repeated solutions
 * in front[0] to properly compute the quality metrics
 * (a repeated solution leads to a wrong increase in the metric values) 
 * 
 * @author Bernab Dorronsoro
 * @version 1.0
 */

package jmetal.util;

import jmetal.base.SolutionSet;
import jmetal.base.Solution;
import jmetal.base.operator.comparator.OverallConstraintViolationComparator;
import jmetal.base.operator.comparator.DominanceComparator;
import java.util.*;

/**
 * This class implements some facilities for ranking solutions.
 * Given a <code>SolutionSet</code> object, their solutions are ranked 
 * according to scheme proposed in NSGA-II; as a result, a set of subsets 
 * are obtained. The subsets are numbered starting from 0 (in NSGA-II, the 
 * numbering starts from 1); thus, subset 0 contains the non-dominated 
 * solutions, subset 1 contains the non-dominated solutions after removing those
 * belonging to subset 0, and so on.
 */
public class RankingNonRepeatedSols {
  
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
  public RankingNonRepeatedSols(SolutionSet solutionSet) {        
    solutionSet_ = solutionSet ;

    // dominateMe[i] contains the number of solutions dominating i        
    int [] dominateMe = new int[solutionSet_.size()];

    // iDominate[k] contains the list of solutions dominated by k
    List<Integer> [] iDominate = new List[solutionSet_.size()];

    // front[i] contains the list of individuals belonging to the front i
    List<Integer> [] front = new List[solutionSet_.size()+1];
        
    // flagDominate is an auxiliar variable
    int flagDominate;    

    // Initialize the fronts 
    for (int i = 0; i < front.length; i++)
      front[i] = new LinkedList<Integer>();
        
    int objectives = solutionSet.get(0).numberOfObjectives();
    
    //-> Fast non dominated sorting algorithm
    for (int p = 0; p < solutionSet_.size(); p++) {
    // Initialice the list of individuals that i dominate and the number
    // of individuals that dominate me
      iDominate[p] = new LinkedList<Integer>();
      dominateMe[p] = 0;
      
  	
      // For all q individuals , calculate if p dominates q or vice versa
      for (int q = 0; q < solutionSet_.size(); q++) {
    	  
    	flagDominate =constraint_.compare(solutionSet.get(p),solutionSet.get(q));
	    if (flagDominate == 0) {
	      flagDominate =dominance_.compare(solutionSet.get(p),solutionSet.get(q));                                
	    }
	    
	    if (flagDominate == -1) {
	      iDominate[p].add(new Integer(q));
	    } else if (flagDominate == 1) {
	                dominateMe[p]++;     
	    }
      }
            
      // If nobody dominates p, p belongs to the first front
      // and solution p is not already in the first front
      if (dominateMe[p] == 0) {
        front[0].add(new Integer(p));
        solutionSet.get(p).setRank(0);
      }            
    }
        
    //Obtain the rest of fronts
    int i = 0;
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
          }
        }
      }
    }
    //<-
        
    ranking_ = new SolutionSet[i];
    //0,1,2,....,i-1 are front, then i fronts
    for (int j = 0; j < i; j++) {
      ranking_[j] = new SolutionSet(front[j].size());
      it1 = front[j].iterator();
      while (it1.hasNext()) {
                ranking_[j].add(solutionSet.get(it1.next().intValue()));       
      }
    }
    
    // Remove repeated solutions from ranking_[0]
	boolean sameSol = true;
	
	int pos = 0, pos2 = 0;
	
	while (pos < ranking_[0].size()) {
		pos2 = pos+1;
		while (pos2 < ranking_[0].size()) {
			sameSol = true;
			for (int obj=0; obj<ranking_[0].get(pos).numberOfObjectives(); obj++)
				if (ranking_[0].get(pos).getObjective(obj) != ranking_[0].get(pos2).getObjective(obj)) {
							sameSol = false;
							break;
						}
			
			if (sameSol)
				ranking_[0].remove(pos2);
			else 
				pos2++;
		}
		pos++;
	}

    
  } // Ranking

  /**
   * Returns a <code>SolutionSet</code> containing the solutions of a given rank. 
   * @param rank The rank
   * @return Object representing the <code>SolutionSet</code>.
   */
  public SolutionSet getSubfront(int rank) {
    return ranking_[rank];
  } // getSubFront

  /** 
  * Returns the total number of subFronts founds.
  */
  public int getNumberOfSubfronts() {
    return ranking_.length;
  } // getNumberOfSubfronts
} // Ranking
