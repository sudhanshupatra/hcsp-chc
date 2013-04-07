package jmetal.coevolutionary.base.operator.comparator;


import java.util.Comparator;
import jmetal.base.operator.comparator.OverallConstraintViolationComparator;
import jmetal.coevolutionary.base.Solution;


/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on a constraint violation test + 
 * dominance checking, as in NSGA-II.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class DominanceComparator implements Comparator{


	/** 
	 * stores a comparator for check the OverallConstraintComparator
	 */
	private static final Comparator overallConstraintViolationComparator_ =
		new OverallConstraintViolationComparator();


	/**
	 * Compares two solutions.
	 * @param object1 Object representing the first <code>Solution</code>.
	 * @param object2 Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if solution1 dominates solution2, both are 
	 * non-dominated, or solution1  is dominated by solution22, respectively.
	 */
	public int compare(Object object1, Object object2) {
		if (object1==null)
			return 1;
		else if (object2 == null)
			return -1;

		int dominate1 ; // dominate1 indicates if some objective of solution1 
		// dominates the same objective in solution2. dominate2
		int dominate2 ; // is the complementary of dominate1.
		Solution solution1 = (Solution)object1;
		Solution solution2 = (Solution)object2;

		dominate1 = 0 ; 
		dominate2 = 0 ;

		int flag; //stores the result of the comparation

		if (solution1.getOverallConstraintViolation()!= 
			solution2.getOverallConstraintViolation() &&
			(solution1.getOverallConstraintViolation() < 0) ||         
			(solution2.getOverallConstraintViolation() < 0)){            
			return (overallConstraintViolationComparator_.compare(solution1,solution2));
		} // if

		// Equal number of violated constraint. Apply a dominance Test
		double value1, value2;
		for (int i = 0; i < solution1.numberOfObjectives(); i++) {
			value1 = solution1.getObjective(i);
			value2 = solution2.getObjective(i);
			if (value1 < value2) {
				flag = -1;
			} // if
			else if (value1 > value2) {
				flag = 1;
			} // else if
			else {
				flag = 0;
			} //else

			if (flag == -1) {
				dominate1 = 1;
			} // if

			if (flag == 1) {
				dominate2 = 1;           
			} // if
		} // for

		if (dominate1 == dominate2) {            
			return 0; //No one dominate the other
		} // if
		if (dominate1 == 1) {
			return -1; // solution1 dominate
		} // if
		return 1;    // solution2 dominate   
	} // compare


} // DominanceComparator
