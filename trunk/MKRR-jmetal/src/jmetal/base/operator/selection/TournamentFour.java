package jmetal.base.operator.selection;


import jmetal.base.Operator;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.util.JMException;


/**
 * Class representing a kind of operator of selection
 * 
 * @author Juan A. ero
 * 
 * @version 1.0
 */
public class TournamentFour extends Operator{

	private static final long serialVersionUID = 4399699357431732005L;

	/**
	 * Constructor
	 * Creates a new Binary tournament operator using a BinaryTournamentComparator
	 */
	public TournamentFour(){
	} // BinaryTournament

	@Override
	public Object execute(Object object ) throws JMException {
		SolutionSet SolutionSet = (SolutionSet) object;
		Solution solution1, solution2, solution3, solution4, candidate1, candidate2;
		Solution[] result;

		int[] indices = RandomVector.getRandomVector_Int( 4 , SolutionSet.size() );

		solution1 = SolutionSet.get( indices[0] );
		solution2 = SolutionSet.get( indices[1] );
		solution3 = SolutionSet.get( indices[2] );
		solution4 = SolutionSet.get( indices[3] );

		double d,d2,d3,d4;

		// PHASE 1 :: PIVOT solution1
		candidate1 = solution1;
		d2 = ManhattanDistance( solution1 , solution2 );
		d3 = ManhattanDistance( solution1 , solution3 );
		d4 = ManhattanDistance( solution1 , solution4 );

		if ( d2>=d3 ) {
			if ( d2>=d4 ) {
				candidate2 = solution2;
				d = d2;
			} // if
			else {
				candidate2 = solution4;
				d = d4;
			} // else
		} // if
		else {
			if ( d3>=d4 ) {
				candidate2 = solution3;
				d = d3;
			} // if
			else {
				candidate2 = solution4;
				d = d4;
			} // else
		} // else
		
		// PHASE 2 :: PIVOT solution2
		d3 = ManhattanDistance( solution2 , solution3 );
		d4 = ManhattanDistance( solution2 , solution4 );
		
		if ( d3>d4 ) {
			if ( d3>d ){
				candidate1 = solution2;
				candidate2 = solution3;
				d = d3;
			} // if
		} // if
		else {
			if ( d4>d ){
				candidate1 = solution2;
				candidate2 = solution4;
				d = d4;
			} // if
		} // else
		
		// PHASE 3 :: PIVOT solution3
		d4 = ManhattanDistance( solution3 , solution4 );
		if ( d4>d ){
			candidate1 = solution3;
			candidate2 = solution4;
		} // if
		
		result = new Solution[2];
		result[0] = candidate1;
		result[1] = candidate2;
		return result;
	} // execute


	/** This method calculates the manhattan distance (Norm 1) of the objectives
	 * of the 2 solutions.
	 * 
	 * @param solution1 first solution
	 * @param solution2 the second one
	 * @return The distance
	 */
	private double ManhattanDistance( Solution solution1 , Solution solution2 ) {

		double[] d1 = {solution1.getObjective(0),solution1.getObjective(1)};
		double[] d2 = {solution2.getObjective(0),solution2.getObjective(1)};

		if ( d1.length == 1 )
			return ( Math.abs(d1[0]-d2[0]) );
		else if ( d1.length == 2 )
			return ( Math.abs(d1[0]-d2[0]) + Math.abs(d1[1]-d2[1]) );
		else {
			double ac = 0.0;
			for( int i=0 ; i<d1.length ; ++i)
				ac += Math.abs(d1[i]-d2[i]);
			return ac;
		} // else
	} // ManhattanDistance

} // TournamentFour
