package jmetal.coevolutionary.base.operator.localSearch;


import jmetal.base.Variable;
import jmetal.base.variable.Int;
import jmetal.coevolutionary.base.operator.localSearch.LocalSearch;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.problems.scheduling.MO_Scheduling;
import jmetal.coevolutionary.util.Matrix;

import jmetal.util.JMException;


/**
 * This class implements a local search operator based on swapping two jobs, and
 * applied the pair of jobs that yields the best reduction in the completion
 * time.
 * 
 * <b>IMPORTANT NOTE:</b> This class needs to work only with sliced (naked) solutions.
 * 
 * @author Anowar El Amouri
 * @author Juan A. Ca–ero (bug fixes, optimization and new features added)
 * @version 1.1
 */
public class LMCTSLocalSearch extends LocalSearch {

	private static final long serialVersionUID = 7138734592015581051L;
	
	private Matrix        M_       = null;
	private MO_Scheduling problem_ = null;


	/**
	 * Constructor
	 * Creates a new local search object.
	 */
	public LMCTSLocalSearch(){

		super();
	} // LMCTSLocalSearch


	/**
	 * Executes the local search. 
	 * @param object Object representing a package with a solution and an islandId
	 * @return An object containing the new improved solution
	 * @throws JMException 
	 */
	public Object execute( Object object , int islandId ) throws JMException {

//		System.out.println("entro!");
		// Recover the parameters (solution and island)
		Solution solution = (Solution) object;

		if ( problem_ == null ) problem_ = (MO_Scheduling) getParameter( "Problem" );
		if ( M_       == null ) M_       = problem_.getMatrix();

		doLocalSearch( solution, islandId );
		// return new Solution(solution);
		return null;
	} // execute 


	/**
	 * @param solution
	 * @param islandId
	 * @throws JMException
	 */
	private void doLocalSearch( Solution solution, int islandId ) throws JMException {
		DecisionVariables decisionVariables ;
		if ( solution.isLinked() )
			decisionVariables = new DecisionVariables( solution.getDecisionVariables()         ,
                                                       solution.getExternalDecisionVariables() ,
                                                       islandId                                );
		else
			decisionVariables = solution.getDecisionVariables();

		// Recover the solution parameters
//		int task_Number    = M_.getNumberOfTasks()    ;
		int task_Number    = decisionVariables.size()    ;
		int machine_Number = M_.getNumberOfMachines() ;

		int[]    Schedule  = new int[ task_Number ];       // Contains the solution values
		int[]    Schedule1 = new int[ task_Number ];       // Contains the solution swapped values

		// Recover ETC Matrix
		double[][] MatrixETC = M_.getETCmatrix();

		// start and end of the slice. The main loop only works in the specified slice
		int sliceSz = decisionVariables.size(); // / problem_.getNumberOfIslands();
		//int start   = sliceSz * islandId ;
		int start   = 0 ;
		int end     = start + sliceSz;
		
//		System.out.println("start: " + start + "; sliceSz: " + sliceSz + "; end: " + end + "; Nb of vars: " + decisionVariables.size());
		// Recover Solution values
		for( int var=start ; var<end ; ++var )
			Schedule[var] = (int) decisionVariables.variables_[var].getValue();   

		// Apply LMCTS Local Search in the correct slice

		int j = start + 1;
		System.arraycopy( Schedule , start , Schedule1 , start , sliceSz );
		while( j<end ){
			// 1. Compute partial completion time of the solution
			double[] completion = problem_.ComputePartialCompletion( MatrixETC , Schedule , task_Number , machine_Number , start , end );

			// 2. Create the tab of the solution with swapped values
			if ( j>(start+1) )
				System.arraycopy( Schedule , j-2 , Schedule1 , j-2 , 2 );

			if ( Schedule1[ j-1 ] != Schedule1[ j ] ){

				int temp = Schedule1[ j-1 ];
				Schedule1[ j-1 ] = Schedule1[ j ];
				Schedule1[ j   ] = temp;

				// 3. Compute partial completion time of the solution with swapped values
				double[] completion1 = problem_.ComputePartialCompletion( MatrixETC , Schedule1 , task_Number , machine_Number , start , end );

				// 4. Compare the two solutions and apply the best
				int m1 = Schedule[j-1];
				int m2 = Schedule[j];
				
				double sum  = completion[  m1 ] + completion[  m2 ];
				double sum1 = completion1[ m1 ] + completion1[ m2 ];

				if ( sum1<sum )
					System.arraycopy( Schedule1 , j-1 , Schedule , j-1 , 2 );
			} // if
			// 5. Choose another pair of jobs
			++j;
		} // while

		// Recover New Solution values
		Variable[] v = decisionVariables.variables_;
		for( int var=start ; var<end ; ++var ) {
			((Int) v[var]).setValue( Schedule[var] );
		} // for

		//solution.setDecisionVariables( decisionVariables.extractSlice( islandId ) );
		solution.setDecisionVariables( decisionVariables );
		// REMARK En offspring se hace, no se si es necesario aqui
		solution.setCrowdingDistance(0.0);
		solution.setRank(0);
	} // doLocalSearch


	public int getEvaluations() {

		return 0;
	} // getEvaluations

} // LMCTSLocalSearch
