package jmetal.coevolutionary.base.operator.mutation;

import jmetal.base.variable.Int;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


/** This class implements the mutation by swaping on a set of tasks assigned
 * to a set of machines.
 * 
 * <b>IMPORTANT NOTE:</b> This class needs to work only with sliced (naked) <code>Solution</code>'s.
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class PermMutation extends Operator {

	private static final long serialVersionUID = -6750346217628471969L;
	
	private double        probability_          = 0.1 ; ///< The probability to perform the mutation by default
	private Problem       problem_                    ; ///< The problem to solve
	private int           rounds_                     ;


	/**
	 * Constructor
	 */
	public PermMutation(){

		super();
	} // RandomMutation


	// REMARK Aunque esto deberia funcionar con cualquier tipo, solo lo hace con tipo int y no lo controla, por culpa del swapeo
	/** This method performs a mutation over a solution, works only with sliced (naked) solutions.
	 * 
	 * @param solution Solution to mutate
	 * @param islandId
	 * @throws JMException
	 */
	private void doMutation( Solution solution , int islandId ) throws JMException{
		DecisionVariables decisionVariables;
		int               start;
		int               end;
		int               sliceSz;
		int               offset;

		int[]    Schedule;

		// Test if the solution is linked
		if ( solution.isLinked() ) {
			decisionVariables = new DecisionVariables( solution.getDecisionVariables()         ,
                                                       solution.getExternalDecisionVariables() ,
                                                       islandId                                );
			sliceSz = decisionVariables.size() / problem_.getNumberOfIslands();
			start   = sliceSz * islandId ;
			end     = start + sliceSz;
			offset  = 0;
			Schedule    = new int[ decisionVariables.size() ];       // Contains the vector of assignments
			// Recover only the involved slice.
			for( int var=start ; var<end ; ++var )
				Schedule[var] = (int) decisionVariables.variables_[var].getValue();
		} // if
		else {
			decisionVariables = solution.getDecisionVariables();
			sliceSz = decisionVariables.size();
			start   = sliceSz * islandId ;
			end     = start + sliceSz;
			offset  = ( problem_.getNumberOfIslands() > 1 )? start : 0;
			Schedule    = new int[ decisionVariables.size()*problem_.getNumberOfIslands() ];       // Contains the vector of assignments
			for( int var=start ; var<end ; ++var )
				Schedule[var] = (int) decisionVariables.variables_[var-offset].getValue();
		} // else

		doRandomPermutation(Schedule, start, sliceSz, decisionVariables , offset);
		
		for( int var=start ; var<end ; ++var )
			((Int) decisionVariables.variables_[ var-offset ]).setValue( Schedule[var] );

		if ( solution.isLinked() ) // Assign the decision variables in the correct slice
			solution.setDecisionVariables( decisionVariables.extractSlice( islandId ) );
		else
			solution.setDecisionVariables( decisionVariables );
	} // doMutation


	/**
	 * @param Schedule
	 * @param start
	 * @param sliceSz
	 * @param decisionVariables
	 * @param offset
	 */
	private void doRandomPermutation(int[] Schedule, int start, int sliceSz,DecisionVariables decisionVariables,
			int offset) {
		for( int times=0 ; times<rounds_ ; ++times ){
			// Select randomly 2 task with different machines (if it's possible)
			// (Think in the hipotetical case:  Schedule[i] == Schedule[j] for all i!=j)
			
			int   attemptsRemaining = 6; // Maximum (insane) value: sliceSz
			int   t1,t2;
			int[] indices;
			do {
				indices = RandomVector.getRandomVector_Int( 2 , sliceSz );
				t1 = start+indices[0];
				t2 = start+indices[1];
				--attemptsRemaining;
			} while ( ( attemptsRemaining>0 ) && (Schedule[t1]==Schedule[t2]));
			if (attemptsRemaining==0) {
				int m1 = PseudoRandom.randInt( (int) problem_.getLowerLimit(t1-offset) , (int) problem_.getUpperLimit(t1-offset)-1 );
				int m2 = PseudoRandom.randInt( (int) problem_.getLowerLimit(t2-offset) , (int) problem_.getUpperLimit(t2-offset)-1 );
				Schedule[t1] = m1;				
				Schedule[t2] = m2;
				((Int) decisionVariables.variables_[ t1-offset ]).setValue( m1 );
				((Int) decisionVariables.variables_[ t2-offset ]).setValue( m2 );
			} // if
			else {
				// Swap its values
				int m = Schedule[t1];
				Schedule[t1] = Schedule[t2];
				Schedule[t2] = m ;
				((Int) decisionVariables.variables_[ t1-offset ]).setValue( Schedule[t1] );
				((Int) decisionVariables.variables_[ t2-offset ]).setValue( m );
			} // else
		} // for
	} // doRandomPermutation


	@Override
	public Object execute( Object object , int islandId ) throws JMException {
		
		if ( problem_ == null ) {
			problem_     =  (Problem) getParameter( "Problem"     );
			probability_ = ((Double)  getParameter( "probability" )).doubleValue();
			rounds_      = ((Integer) getParameter( "rounds"      )).intValue();
		} // if

		Solution solution = (Solution) object;
		double rand = Math.random();
		if ( rand <= probability_ ) 
			doMutation( solution , islandId );
		return null;
	} // execute


} // RandomMutation
