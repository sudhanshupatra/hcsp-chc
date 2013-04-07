package jmetal.coevolutionary.base.operator.mutation;

import jmetal.base.Configuration;
import jmetal.base.variable.Int;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.problems.scheduling.MO_Scheduling;
import jmetal.coevolutionary.util.ArrayUtils;
import jmetal.coevolutionary.util.Matrix;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;



/** This class implements the mutation by rebalancing on a set of tasks assigned
 * to a set of machines. For this goal chooses a task belonging to the set of
 * overloaded machines (the number of overloaded machines included on this set
 * is calculated as overloadPercentage_*numberOfMachines) and it attempts to
 * swap for another task of a underloaded machine. If certain conditions are
 * met, the task overloaded simply moves to a idle machine. This action is
 * repeated a certain number of times (rounds).
 * 
 * Also this class adds a bit of random mutation.
 * 
 * <b>IMPORTANT NOTE:</b> This class needs to work only with sliced (naked) <code>Solution</code>'s.
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class RebalanceMutation extends Operator {

	private static final long serialVersionUID = -3984586144296763572L;
	
	private double        overloadPercentage_        = 0.25 ; ///< The percentage of overloaded machines by default
	private double        probability_               = 0.2  ; ///< The probability to perform the mutation by default
	private MO_Scheduling problem_                   = null ; ///< The problem to solve
	private Matrix        M_                         = null ; ///< Stores the expected time to compute matrix
	private int           numberOfOverloadedMachines_       ; ///< The number of overloaded machines
	private int           numberOfTasks_             = 0    ; ///< The number of tasks represented in the matrix
	private int           numberOfMachines_          = 0    ; ///< the number of machines represented in the matrix
	private int           rounds_                    = 2    ;
	private int           opportunities_             = 3    ; ///< Opportunities given in randomPermutation

	private enum          Policy {
		                         	convenient ,
		                         	simple     ,
		                         	moderate   ,
		                         	heavy      ,
		                         	random     ,
		                         	undefined    // Internal use
		                  } // Policy;
	
	private enum         Mode {
                                    permissive ,
                                    strict     ,
                                    undefined
	                     } // Mode
	
	private Policy       policy_        = Policy.undefined;
	private Policy       initialPolicy_ = Policy.undefined;
	
	private Mode         mode_ = Mode.undefined;


	/**
	 * Constructor
	 */
	public RebalanceMutation() {

		super();
	} // RebalanceMutation
	
	
	/** Private method, see @doMutation
	 * @param Schedule
	 * @param start
	 * @param sliceSz
	 * @param decisionVariables
	 * @param offset
	 */
	private void doRandomPermutation( int[]             Schedule          , int start  , int sliceSz ,
			                          DecisionVariables decisionVariables , int offset ) {
		int   t1 , t2;
		int[] indices;

		for( int times=0 ; times<rounds_ ; ++times ){
			// Select randomly 2 task assigned to different machines (if it's possible)
			// (Think in the hipotetical case:  Schedule[i] == Schedule[j] for all i!=j)
			int attemptsRemaining = opportunities_; // Maximum (insane) value: sliceSz
			
			do {
				indices = RandomVector.getRandomVector_Int( 2 , sliceSz );
				t1 = start+indices[0];
				t2 = start+indices[1];
				--attemptsRemaining;
			} while ( ( attemptsRemaining>0 ) && (Schedule[t1]==Schedule[t2]));
			
			if (attemptsRemaining==0) {
				// Overwrite the values with new ones
				int[] machines = RandomVector.getRandomVector_Int( 2 , numberOfMachines_ );
				Schedule[t1] = machines[0];				
				Schedule[t2] = machines[1];
				((Int) decisionVariables.variables_[ t1-offset ]).setValue( machines[0] );
				((Int) decisionVariables.variables_[ t2-offset ]).setValue( machines[1] );
			} // if
			else {
				// Swap its values (the common case)
				int machine  = Schedule[t1];
				Schedule[t1] = Schedule[t2];
				Schedule[t2] = machine ;
				((Int) decisionVariables.variables_[ t1-offset ]).setValue( Schedule[t1] );
				((Int) decisionVariables.variables_[ t2-offset ]).setValue( machine      );
			} // else
		} // for
	} // doRandomPermutation

	
	/** Private method, see @doMutation
	 * @param Schedule
	 * @param start
	 * @param end
	 * @param decisionVariables
	 * @param offset
	 * @param MatrixETC
	 */
	private void doSimpleMutation( int[]             Schedule          , int start  , int end,
			                       DecisionVariables decisionVariables , int offset ) {
		double[] completion; // Contains the vector of completion time
		
		// Recover ETC Matrix
		double[][] ETC    = M_.getETCmatrix();
		double[]   buffer = new double[ numberOfMachines_ ];
		// Calc the partial completion, the task t / start <= t < end
		completion = problem_.ComputePartialCompletion( ETC , Schedule , numberOfTasks_ , numberOfMachines_ , start , end );
		
		for( int times=0 ; times<rounds_ ; ++times ) {

			// Perform the sorting over the vector
			System.arraycopy( completion , 0 , buffer , 0 , numberOfMachines_ );
			int [] machineIndex = ArrayUtils.sort( buffer );

			// Select an overloaded and underloaded machine
			int overloadedMachine;
			int underloadedMachine;
			if ( mode_ == Mode.permissive ) {
				int index = PseudoRandom.randInt( numberOfMachines_-numberOfOverloadedMachines_ , numberOfMachines_-1 );
				overloadedMachine = machineIndex[ index ];
				index = PseudoRandom.randInt( 0 , numberOfOverloadedMachines_ );
				underloadedMachine = machineIndex[ index ];
			} // if
			else {
				overloadedMachine  = machineIndex[ numberOfMachines_-1 ];
				underloadedMachine = machineIndex[ 0 ];
			} // else

			// Find a task (the first found) that actually is going to execute in the overloaded machine
			int candidateTaskOL = ArrayUtils.indexOf( Schedule , overloadedMachine , start );
			
			// Move the task to the underloaded machine
			int original                      = Schedule[ candidateTaskOL ];
			
			completion[ original           ] -= ETC[ candidateTaskOL ][ original           ];
			completion[ underloadedMachine ] += ETC[ candidateTaskOL ][ underloadedMachine ];
			
			Schedule[ candidateTaskOL ]       = underloadedMachine;
			((Int) decisionVariables.variables_[ candidateTaskOL-offset ]).setValue( underloadedMachine );
		} // for
	} // doSimpleMutation


	/** Private method, see @doMutation
	 * @param Schedule
	 * @param start
	 * @param end
	 * @param decisionVariables
	 * @param offset
	 * @param MatrixETC
	 */
	private void doConvenientMutation( int[]             Schedule          , int start  , int end,
			                           DecisionVariables decisionVariables , int offset ) {
		double[] completion; // Contains the vector of completion time
		
		// Recover ETC Matrix
		double[][] ETC = M_.getETCmatrix();
		
		double[] buffer = new double[numberOfMachines_];
		
		// Calc the partial completion, the task t / start <= t < end
		completion = problem_.ComputePartialCompletion( ETC , Schedule , numberOfTasks_ , numberOfMachines_ , start , end );

		for( int times=0 ; times<rounds_ ; ++times ) {

			// Perform the sorting over the vector
			System.arraycopy( completion , 0 , buffer , 0 , numberOfMachines_ );
			int [] machineIndex = ArrayUtils.sort( buffer );

			// Select the overloaded machine
			int overloadedMachine;
			if ( mode_ == Mode.permissive )
				overloadedMachine  = machineIndex[ PseudoRandom.randInt( numberOfMachines_-numberOfOverloadedMachines_ , numberOfMachines_-1 )];
			else
				overloadedMachine  = machineIndex[ numberOfMachines_-1 ];

			// Find the heaviest task that actually is going to execute in the selected overloaded machine
			int    currentTask        = ArrayUtils.indexOf( Schedule , overloadedMachine , start );
			int    heaviestTask       = currentTask;
			double timeOfHeaviestTask = ETC[currentTask][overloadedMachine];
			double timeOfCurrentTask  = timeOfHeaviestTask;

			do {
				currentTask       = ArrayUtils.indexOf( Schedule , overloadedMachine , currentTask+1 );
				if ( ( currentTask != ArrayUtils.INDEX_NOT_FOUND ) && (currentTask < end) ){
					timeOfCurrentTask = ETC[currentTask][overloadedMachine];
					if ( timeOfCurrentTask > timeOfHeaviestTask ) {
						heaviestTask       = currentTask;
						timeOfHeaviestTask = timeOfCurrentTask;
					} // if
				} // if
			} while ( (currentTask != ArrayUtils.INDEX_NOT_FOUND ) && (currentTask < end) );

			// Find The most convenient machine ( != original ) to execute the task
			int currentMachine  = 0;
			int convenientMachine = currentMachine;
			double timeOfSelectedMachine = ETC[heaviestTask][currentMachine];
			double timeOfCurrentMachine  = timeOfSelectedMachine;
			
			do {
				++currentMachine;
				if ( currentMachine != overloadedMachine ){
					timeOfSelectedMachine = ETC[heaviestTask][currentMachine];
					if ( timeOfSelectedMachine < timeOfCurrentMachine ){
						timeOfCurrentMachine = timeOfSelectedMachine;
						convenientMachine    = currentMachine;
					} // if
				} // if
			} while ( currentMachine<(numberOfMachines_-1) );
			// REMARK Is it possible that convenientMachine doesn't be the true convenient machine?
			// Assign the task to the most convenient machine
			int original = Schedule[ heaviestTask ];
			
			completion[ original ]          -= ETC[ heaviestTask ][ original          ];
			completion[ convenientMachine ] += timeOfSelectedMachine;

			Schedule[ heaviestTask ] = convenientMachine;
			((Int) decisionVariables.variables_[ heaviestTask-offset ]).setValue( convenientMachine );
		} // for
	} // doConvenientMutation

	
	
	/** Private method, see @doMutation
	 * @param Schedule
	 * @param start
	 * @param end
	 * @param decisionVariables
	 * @param offset
	 * @param MatrixETC
	 */
	private void doModerateMutation( int[]             Schedule          , int start  , int end,
			                         DecisionVariables decisionVariables , int offset ) {
		double[] completion; // Contains the vector of completion time
		
		// Recover ETC Matrix
		double[][] ETC    = M_.getETCmatrix();
		double[]   buffer = new double[numberOfMachines_];
		
		// Calc the partial completion, the task t / start <= t < end
		completion = problem_.ComputePartialCompletion( ETC , Schedule , numberOfTasks_ , numberOfMachines_ , start , end );
		
		for( int times=0 ; times<rounds_ ; ++times ) {

			// Perform the sorting over the vector
			System.arraycopy( completion , 0 , buffer , 0 , numberOfMachines_ );
			int[] machineIndex = ArrayUtils.sort( buffer );

			// Select an underloaded machine, NOTE: by definition
			// numberOfOverloadedMachines = numberOfUnderloadedMachines
			int underloadedMachine;
			int overloadedMachine;
			if ( mode_ == Mode.permissive ) {
				underloadedMachine = machineIndex[ PseudoRandom.randInt( 0 , numberOfOverloadedMachines_-1 ) ];
				overloadedMachine  = machineIndex[ PseudoRandom.randInt( numberOfMachines_-numberOfOverloadedMachines_ , numberOfMachines_-1 )];
			} // if
			else {
				underloadedMachine = machineIndex[ 0 ];
				overloadedMachine  = machineIndex[ numberOfMachines_-1 ];
			} // else
			
			int    currentTask          = ArrayUtils.indexOf( Schedule , overloadedMachine , start );			
			int    convenientTask       = currentTask;
			double timeOfConvenientTask = ETC[ currentTask ][ underloadedMachine ];
			double timeOfCurrentTask    = timeOfConvenientTask;

			do{
				currentTask = ArrayUtils.indexOf( Schedule , overloadedMachine , currentTask+1 );
				if ( ( currentTask != ArrayUtils.INDEX_NOT_FOUND ) && (currentTask < end) ){
					timeOfCurrentTask = ETC[currentTask][underloadedMachine];
					if ( timeOfCurrentTask < timeOfConvenientTask ) {
						convenientTask       = currentTask;
						timeOfConvenientTask = timeOfCurrentTask;
					} // if
				} // if
			} while ( (currentTask != ArrayUtils.INDEX_NOT_FOUND ) && (currentTask < end) );
			
			int original = Schedule[ convenientTask ];
			
			completion[ original           ] -= ETC[ convenientTask ][ original           ];
			completion[ underloadedMachine ] += ETC[ convenientTask ][ underloadedMachine ];

			Schedule[ convenientTask ] = underloadedMachine;
			
			((Int) decisionVariables.variables_[ convenientTask-offset ]).setValue( underloadedMachine );	
		} // for
	} // doModerateMutation
	
	
	
	/** Private method, see @doMutation
	 * @param Schedule
	 * @param start
	 * @param end
	 * @param decisionVariables
	 * @param offset
	 * @param MatrixETC
	 */
	private void doHeavyMutation( int[]             Schedule          , int start  , int end,
			                      DecisionVariables decisionVariables , int offset ) {
		double[] completion; // Contains the vector of completion time
		
		// Recover ETC Matrix
		double[][] ETC    = M_.getETCmatrix();
		double[]   buffer = new double[ numberOfMachines_ ];
		// Calc the partial completion, the task t / start <= t < end
		completion = problem_.ComputePartialCompletion( ETC , Schedule , numberOfTasks_ , numberOfMachines_ , start , end );
		for( int times=0 ; times<rounds_ ; ++times ) {

			// Perform the sorting over the vector
			System.arraycopy( completion , 0 , buffer , 0 , numberOfMachines_ );
			int[] machineIndex = ArrayUtils.sort( buffer );

			// Select the most overloaded machine
			int underloadedMachine;
			int overloadedMachine;
			if ( mode_ == Mode.permissive ) {
				underloadedMachine = machineIndex[ PseudoRandom.randInt( 0 , numberOfOverloadedMachines_-1 ) ];
				overloadedMachine  = machineIndex[ PseudoRandom.randInt( numberOfMachines_-numberOfOverloadedMachines_ , numberOfMachines_-1 )];
			} // if
			else {
				underloadedMachine = machineIndex[ 0 ];
				overloadedMachine  = machineIndex[ numberOfMachines_-1 ];
			} // else

			// Find the heaviest task that actually is going to execute in the selected overloaded machine
			int    currentTask        = ArrayUtils.indexOf( Schedule , overloadedMachine , start );
			int    heaviestTask       = currentTask;
			double timeOfHeaviestTask = ETC[currentTask][overloadedMachine];
			double timeOfCurrentTask  = timeOfHeaviestTask;

			do {
				currentTask       = ArrayUtils.indexOf( Schedule , overloadedMachine , currentTask+1 );
				if ( ( currentTask != ArrayUtils.INDEX_NOT_FOUND ) && (currentTask < end) ){
					timeOfCurrentTask = ETC[currentTask][overloadedMachine];
					if ( timeOfCurrentTask > timeOfHeaviestTask ) {
						heaviestTask       = currentTask;
						timeOfHeaviestTask = timeOfCurrentTask;
					} // if
				} // if
			} while ( (currentTask != ArrayUtils.INDEX_NOT_FOUND ) && (currentTask < end) );

			// Move this task to an underloaded machine
			int original = Schedule[ heaviestTask ];
			
			completion[ original           ] -= ETC[ heaviestTask ][ original           ];
			completion[ underloadedMachine ] += ETC[ heaviestTask ][ underloadedMachine ];

			Schedule[ heaviestTask ]  = underloadedMachine;
			((Int) decisionVariables.variables_[ heaviestTask-offset ]).setValue( underloadedMachine );
		} // for
	} // doHeavyMutation


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

		// Test if the solution is linked
		if ( solution.isLinked() ) {
			decisionVariables = new DecisionVariables( solution.getDecisionVariables()         ,
                                                       solution.getExternalDecisionVariables() ,
                                                       islandId                                );
			sliceSz = decisionVariables.size() / problem_.getNumberOfIslands();
			start   = sliceSz * islandId ;
			end     = start + sliceSz;
			offset  = 0;
		} // if
		else {
			decisionVariables = solution.getDecisionVariables();
			sliceSz = decisionVariables.size();
			start   = sliceSz * islandId ;
			end     = start + sliceSz;
			offset  = ( problem_.getNumberOfIslands() > 1 )? start : 0;			
		} // else
		
		// Recover only the involved slice.
		int[] Schedule = new int[ numberOfTasks_ ];
		for( int var=start ; var<end ; ++var )
			Schedule[var] = (int) decisionVariables.variables_[var-offset].getValue();

		if ( initialPolicy_ == Policy.random ) {
			doRandomPermutation( Schedule , start , sliceSz , decisionVariables , offset );
			switch( PseudoRandom.randInt(1,4) ){
				case 1  : policy_ = Policy.convenient ; break;
				case 2  : policy_ = Policy.heavy      ; break;
				case 3  : policy_ = Policy.moderate   ; break;
				default : policy_ = Policy.simple     ;
			} // switch
		} // if
		
		switch( policy_ ){
			case convenient : doConvenientMutation( Schedule , start , end , decisionVariables , offset ); break;
			case simple     : doSimpleMutation(     Schedule , start , end , decisionVariables , offset ); break;
			case moderate   : doModerateMutation(   Schedule , start , end , decisionVariables , offset ); break;
			case heavy      : doHeavyMutation(      Schedule , start , end , decisionVariables , offset ); break;
		} // switch

		if ( solution.isLinked() ) // Assign the decision variables in the correct slice
			solution.setDecisionVariables( decisionVariables.extractSlice( islandId ) );
		else
			solution.setDecisionVariables( decisionVariables );
	} // doMutation


	@Override
	public Object execute( Object object , int islandId ) throws JMException {

		if ( problem_ == null ) {
			problem_ =  (MO_Scheduling) getParameter( "Problem" );
			if ( problem_ == null ){
				String cause = "The parameter \"Problem\" must be specified.";
				Configuration.logger_.severe( cause );
				java.lang.NullPointerException ex;
				ex = new java.lang.NullPointerException( cause );
				throw new RuntimeException( cause , ex );
			} // if
		} // if
		
		Object param ;
		
		param = getParameter( "overloadPercentage" );
		overloadPercentage_ = ( param != null )? ((Double) param).doubleValue() : overloadPercentage_ ;
		param = getParameter( "probability" );
		probability_        = ( param != null )? ((Double) param).doubleValue() : probability_;
		param = getParameter( "rounds" );
		rounds_             = ( param != null )? ((Integer) param).intValue()   : rounds_;
		
		if ( policy_ == Policy.undefined ) {
			String policy = (String) getParameter( "Policy" );
			if ( policy != null ) {
				if ( policy.compareToIgnoreCase("Convenient")==0 )
					policy_ = Policy.convenient;
				else if ( policy.compareToIgnoreCase("Simple")==0 )
					policy_ = Policy.simple;
				else if ( policy.compareToIgnoreCase("Moderate")==0 )
					policy_ = Policy.moderate;
				else if ( policy.compareToIgnoreCase("Heavy")==0 )
					policy_ = Policy.heavy;
				else if ( policy.compareToIgnoreCase("Random")==0 ) {
					policy_        = Policy.random;
					initialPolicy_ = Policy.random;
				} // else if
				else {
					String cause = "The parameter \"Policy\" is incorrect. The correct values are: convenient , simple , heavy , moderate or random.";
					Configuration.logger_.severe( cause );
					java.lang.NullPointerException ex;
					ex = new java.lang.NullPointerException( cause );
					throw new RuntimeException( cause , ex );
				} // else
			} // if
			else
				policy_ = Policy.simple;
		} // if
		
		if ( mode_ == Mode.undefined ) {
			String mode = (String) getParameter( "Mode" );
			if ( mode != null ) {
				if ( mode.compareToIgnoreCase( "permissive" ) == 0 )
					mode_ = Mode.permissive;
				else if ( mode.compareToIgnoreCase( "strict" ) == 0 )
					mode_ = Mode.strict;
				else {
					String cause = "The parameter \"Mode\" is incorrect. The correct values are: permissive or strict.";
					Configuration.logger_.severe( cause );
					java.lang.NullPointerException ex;
					ex = new java.lang.NullPointerException( cause );
					throw new RuntimeException( cause , ex );
				} // else
			} // else
			else
				mode_ = Mode.permissive;
		} // if

		M_ = problem_.getMatrix();
		if ( M_ == null ) {
			String cause = "The matrix is undefined";
			Configuration.logger_.severe( cause );
			java.lang.NullPointerException ex;
			ex = new java.lang.NullPointerException( cause );
			throw new RuntimeException( cause , ex );
		} // if

		// Recover the matrix parameters
		numberOfTasks_    = M_.getNumberOfTasks()    ;
		numberOfMachines_ = M_.getNumberOfMachines() ;
		numberOfOverloadedMachines_ = (int) ( Math.round((double) numberOfMachines_ * overloadPercentage_ ) );

		Solution solution = (Solution) object;
		double rand = Math.random();
		if ( rand <= probability_ )
			doMutation( solution , islandId );
		return null;
	} // execute

} // RebalanceMutation
