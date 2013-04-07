package jmetal.coevolutionary.problems.scheduling;


import jmetal.base.Configuration;
import jmetal.base.Variable;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Int;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.util.Matrix;
import jmetal.coevolutionary.util.ScheduleStrategy;
import jmetal.util.JMException;


/**
 * Class representing problem in grid scheduling
 * 
 * @author Anowar El Amouri (first version)
 * @author Juan A. Ca–ero (bug fixes, new features and optimization)
 * 
 * @version 1.1
 */
public abstract class MO_Scheduling extends Problem {

	private static final long serialVersionUID = 1838545564657006733L;
	
	protected Matrix M_;

	protected static final int numberOfVariablesByDefault_  = 512 ;
	protected static final int numberOfObjectivesByDefault_ =   2 ;
	protected static final int numberOfIslandsByDefault_    =   4 ;
	protected static final int numberOfMachinesByDefault_   =  16 ;
	protected        int       numberOfMachines_            =   0 ;
	protected        int       numberOfTasks_               =   0 ;
	
	protected double[][] ETC_; ///< Stores the copy of ETC values
//	protected double[][] Comp_; ///< Stores the copy computation time values


	/** 
	 * Constructor.
	 * Creates a empty instance of the MO_Scheduling problem.
	 */
	protected MO_Scheduling(){
		// none
	} // MO_Scheduling


	/** 
	 * Constructor.
	 * Creates a default instance of the MO_Scheduling problem.
	 */
	public MO_Scheduling( String solutionType ){

		solutionType_ = Enum.valueOf( SolutionType_.class , solutionType );
	} // MO_Scheduling


	/** Constructor.
	 * Creates a new instance of the MO_Scheduling problem.
	 * 
	 * @param numberOfVariables Number of variables of the problem 
	 * @param solutionType The solution type must "Int"
	 * @param numberOfIslands Number of islands to use, must be greater than 1
	 */
	public MO_Scheduling( Integer numberOfVariables , String solutionType , Integer numberOfIslands ){
		numberOfMachines_    = ( numberOfMachines_            >0        )? numberOfMachines_            : numberOfMachinesByDefault_ ;
		numberOfIslands_     = ( numberOfIslands.intValue()   >0        )? numberOfIslands.intValue()   : 1 ;
		numberOfVariables_   = ( numberOfVariables.intValue() >0        )? numberOfVariables.intValue() : numberOfVariablesByDefault_ ;
		numberOfVariables_   = ( numberOfVariables_ <= numberOfIslands_ )? numberOfVariables_           : numberOfVariables_ / numberOfIslands_ ;
		
		numberOfObjectives_  = numberOfObjectivesByDefault_ ;
		numberOfConstraints_ = 0                            ;
		problemName_         = "MO_Scheduling"              ;

		upperLimit_ = new double[ numberOfVariables_ ];
		lowerLimit_ = new double[ numberOfVariables_ ];

		for( int i=0; i<numberOfVariables_ ; i++ ) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = numberOfMachines_;
		} // for

		solutionType_ = Enum.valueOf( SolutionType_.class , solutionType );

		// All the variables are of the same type, so the solutionType name is the
		// same than the variableType name
		variableType_ = new VariableType_[numberOfVariables_];
		for( int var=0 ; var<numberOfVariables_ ; var++ )
			variableType_[var] = Enum.valueOf( VariableType_.class , solutionType );    

	} // MO_Scheduling

	
	/** Calculates completion times of a solution 
	 * 
	 * @param Schedule the solution to use
	 * @param numberOfTasks the number of tasks used in the problem
	 * 
	 * @return A vector of completion times of the machines
	 */
	public double[] ComputeCompletion( int[] Schedule ){
		// Computation of ETC sum

		double[] sumETC        = new double [numberOfMachines_];
		int      machine;
		int      numberOfTasks = Schedule.length;
		
		for( int j=0 ; j<numberOfTasks ; ++j ){
			machine          = Schedule[j];
			sumETC[machine] += ETC_[j][machine];
		} // for

		return( sumETC );
	} // ComputeCompletion


	/** 
	 * Calculates the partial completion times of a solution 
	 * @param ETC the matrix that contains the ETC values to use
	 * @param Schedule the solution to use
	 * @param numberOfTasks the number of tasks used in the problem
	 * @param numberOfMachines the number of machines used in the problem
	 * @return completion a vector of completion times of the machines
	 */
	public double[] ComputePartialCompletion( double[][] ETC              ,
                                              int[]      Schedule         ,
                                              int        numberOfTasks    ,
                                              int        numberOfMachines ,
                                              int        start            ,
                                              int        end              ) {

		double[] sumETC     = new double [numberOfMachines];

		// Computation of ETC sum
		int m1;
		
		for( int j=start ; j<end ; ++j ){
			m1 = Schedule[j];
			sumETC[m1] += ETC[j][m1];
		} // for

		return( sumETC );
	} // computePartialCompletion
	
	
	/** 
	 * Calculates robustness radius of a solution 
	 * @param ETC the matrix that contains the ETC values to use
	 * @param Comp_ the matrix that contains the computation times of the solution
	 * @param Schedule the solution to use
	 * @param numberOfTasks the number of tasks used in the problem 
	 * @param numberOfMachines the number of machines used in the problem
	 * @param completion The completion vector previously computed
	 * 
	 * @return radius a vector of robustness radius of the machines
	 */
	private double[] ComputeRadius( int[]    Schedule   ,
			                        double[] completion ){

		double[] NbApp       = new double[ numberOfMachines_ ];
		double[] radius      = new double[ numberOfMachines_ ];

		// Computation of task number per machine
		for( int j=0 ; j<numberOfTasks_ ; j++ )
			NbApp[ Schedule[j] ] += 1;

		// Computation of robustness radius
		// For efficiency issues, we are computing 0.3*CT instead of 1.3*M-CT
		// Therefore, the robustness of the solution is the case in which M=CT, 
		// so it is the maximum robustness radio computed this way (when CT is max, i.e., CT = M),
		// instead of the minimum robustness radio computed as 1.3*M-CT (when M-CT is min, i.e., CT = M)
		for( int m=0 ; m<numberOfMachines_ ; ++m )
			radius[m] = ( 0.3 * completion[m] ) / Math.sqrt( NbApp[m] );

		return( radius );
	} // ComputeRadius

	
	/** 
	 * Calculates fitness values of a solution 
	 * @param Schedule the solution to use 
	 * 
	 * @return fitness a vector of fitness values of the solution
	 */
	private double[] Fitness( int[] Schedule ){
		
		// Recover Completion Time and Robustness Radius
		double[] completion = ComputeCompletion( Schedule );
		double[] radius     = ComputeRadius(     Schedule , completion );

		// Find maximum of completion time
		// Find minimum of robustness radius
		double maxCompletion = completion[0];

		// For efficiency issues, we are compute 0.3*CT instead of 1.3*M-CT for the robustness radius
		// Therefore, the robustness of the solution is the case in which M=CT, 
		// so it is the maximum robustness radio computed this way (when CT is max, i.e., CT = M),
		// instead of the minimum robustness radio computed as 1.3*M-CT (when M-CT is min, i.e., CT = M)
		double maxRobustness = radius[0];

		for( int k=1 ; k<numberOfMachines_ ; ++k ){
			if ( completion[k] > maxCompletion )
				maxCompletion = completion[k];
			if ( radius[k] > maxRobustness )
				maxRobustness = radius[k];
		} // for

		// Return maximum and minimum as fitness values of makespan and robustness respectively
		double[] fitness = new double[2];
		
		fitness[0]       = maxCompletion;
		fitness[1]       = -maxRobustness;

		return( fitness );
	} // fitness


	/** 
	 * Evaluates a solution 
	 * @param solution The solution to evaluate
	 * @param loadingPosition Loading position
	 * @throws JMException 
	 */
	public void evaluate( Solution solution, int loadingPosition ) throws JMException {
		DecisionVariables gen;

		// Recover the solution values
		if ( solution.isLinked() )
			gen = new DecisionVariables( solution.getDecisionVariables()         ,
				                         solution.getExternalDecisionVariables() ,
				                         loadingPosition                         );
		else
			gen = solution.getDecisionVariables();

		int[]    Schedule = new int[numberOfTasks_];  // Contains the solution values

		// Recover solution parameters
		for( int var=0 ; var<numberOfTasks_ ; ++var ) {
			Schedule[var] = (int) gen.variables_[var].getValue();   
		} // for

		// Return the fitness values
		solution.setObjectives( Fitness( Schedule ) );
	} // evaluate


	/**
	 * @param m the matrix to set
	 */
	public void setMatrix( Matrix m ) {
		M_                = m;
		numberOfTasks_    = M_.getNumberOfTasks();
		numberOfMachines_ = M_.getNumberOfMachines();
		ETC_              = M_.getETCmatrix();
//		Comp_             = M_.getComputationMatrix();
	} // setMatrix


	/**
	 * @return The matrix
	 */
	public Matrix getMatrix() {
		return M_;
	} // getMatrix


	// REMARK Coger una solucion y crear como ejemplo para generar gr‡ficas para el PFC
	/** This method calculates the time of execution of each task in the
	 * selected machine. The result is stored in a string in order to store
	 * later in a file. Works with sliced and full solutions.
	 * 
	 * @param solution the solution
	 * @return the string
	 * @throws JMException 
	 */
	public String getETCvectorString( Solution solution , int loadingPosition ) throws JMException{
		DecisionVariables gen;

		// Recover the solution values
		if ( solution.isLinked() )
			gen = new DecisionVariables( solution.getDecisionVariables()         ,
				                         solution.getExternalDecisionVariables() ,
				                         loadingPosition                         );
		else
			gen = solution.getDecisionVariables();

		int numberOfTasks = M_.getNumberOfTasks();

		double[] etcVector = new double[numberOfTasks];
		double[][] ETC = M_.getETCmatrix();

		// Recover solution parameters
		int m1;
		for( int var=0 ; var<numberOfTasks ; ++var ) {
			m1 = (int) gen.variables_[var].getValue();
			etcVector[var] = ETC[var][m1];
		} // for
		
		String s = "";
		
		for( int var=0 ; var<numberOfTasks ; ++var )
			s += "" + etcVector[var] + " ";

		return( s );
	} // getETCvector
	
	
//	@Override
	public DecisionVariables generateSpecial( String type ){
		DecisionVariables decisionVariables = null;

		if ( type != null ){
			if ( type.compareToIgnoreCase("OneMinmin") == 0 ) {
				int[] Schedule = ScheduleStrategy.minMin( M_.getETCmatrix() , numberOfTasks_ , numberOfMachines_ );
				Variable[] variables = new Variable[ numberOfTasks_];
				for( int i=0 ; i<numberOfTasks_ ; ++i )
					variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

				decisionVariables = new DecisionVariables( this , variables );
			}
			else if ( type.compareToIgnoreCase("Min-min") == 0 ) {
				int[] Schedule = ScheduleStrategy.minMinInitialization( M_.getETCmatrix() , numberOfTasks_ , numberOfMachines_ );
			
				Variable[] variables = new Variable[ numberOfTasks_];
				for( int i=0 ; i<numberOfTasks_ ; ++i )
					variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

				decisionVariables = new DecisionVariables( this , variables );
			} // if
			else {
				Configuration.logger_.severe( "MO_Scheduling.generateSpecial: type \"" + 
						                      type + "\" unknown." );
				throw new RuntimeException( "Exception in MO_Scheduling.generateSpecial( String ) ") ;
			} // else
		} // if
		else {
			DecisionVariables[] slices = new DecisionVariables[ numberOfIslands_-1 ];

			for( int i=0 ; i< numberOfIslands_-1 ; ++i )
				slices[i] = new DecisionVariables( this );

			decisionVariables = new DecisionVariables( new DecisionVariables( this ) , slices , 0 );
		} // else

		return decisionVariables;
	} // generateSpecial

	public DecisionVariables generateSpecial( String type , int loadingPosition ){
		DecisionVariables decisionVariables = null;

		if ( type != null ){
			if ( type.compareToIgnoreCase("OneMinmin") == 0 ) {
				int[] Schedule = ScheduleStrategy.minMin( M_.getETCmatrix() , numberOfVariables_ , numberOfMachines_ , loadingPosition);
				Variable[] variables = new Variable[ numberOfVariables_];
				for( int i=0 ; i<numberOfVariables_ ; ++i )
					variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

				decisionVariables = new DecisionVariables( this , variables );
			}
			else if ( type.compareToIgnoreCase("Min-min") == 0 ) {
				int[] Schedule = ScheduleStrategy.minMinInitialization( M_.getETCmatrix() , numberOfVariables_ , numberOfMachines_  , loadingPosition);
			
				Variable[] variables = new Variable[ numberOfVariables_];
				for( int i=0 ; i<numberOfVariables_ ; ++i )
					variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

				decisionVariables = new DecisionVariables( this , variables );
			} // if
			else {
				Configuration.logger_.severe( "MO_Scheduling.generateSpecial: type \"" + 
						                      type + "\" unknown." );
				throw new RuntimeException( "Exception in MO_Scheduling.generateSpecial( String ) ") ;
			} // else
		} // if
		else {
			DecisionVariables[] slices = new DecisionVariables[ numberOfIslands_-1 ];

			for( int i=0 ; i< numberOfIslands_-1 ; ++i )
				slices[i] = new DecisionVariables( this );

			decisionVariables = new DecisionVariables( new DecisionVariables( this ) , slices , 0 );
		} // else

		return decisionVariables;
	} // generateSpecial

} // MO_Scheduling
