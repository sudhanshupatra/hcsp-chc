package jmetal.problems.scheduling;


import jmetal.base.Configuration;
import jmetal.base.Variable;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Int;
import jmetal.base.DecisionVariables;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.coevolutionary.util.Matrix;
import jmetal.coevolutionary.util.ScheduleStrategy;
import jmetal.util.JMException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class representing problem in grid scheduling
 * 
 * @author Anowar El Amouri (first version)
 * @author Juan A. Caero (bug fixes, new features and optimization)
 * 
 * @version 1.1
 */
public abstract class MO_Scheduling_mak_flow extends Problem implements IMO_SchedulingProblem {

	private static final long serialVersionUID = 1838545564657006733L;
	
	protected Matrix M_;

	protected static final int numberOfVariablesByDefault_  = 512 ;
	protected static final int numberOfObjectivesByDefault_ =   2 ;
	protected static final int numberOfIslandsByDefault_    =   4 ;
	protected static final int numberOfMachinesByDefault_   =  16 ;
	protected        int       numberOfMachines_            =   0 ;
	protected        int       numberOfTasks_               =   0 ;
	
	protected double[][] ETC_;  ///< Stores the copy of ETC values
	//protected double[][] Comp_; ///< Stores the copy computation time values


	/** 
	 * Constructor.
	 * Creates a empty instance of the MO_Scheduling problem.
	 */
	protected MO_Scheduling_mak_flow(){
		// none
	} // MO_Scheduling


	/** 
	 * Constructor.
	 * Creates a default instance of the MO_Scheduling problem.
	 */
	public MO_Scheduling_mak_flow( String solutionType ){

		solutionType_ = Enum.valueOf( SolutionType_.class , solutionType );
	} // MO_Scheduling_mak_flow


	/** Constructor.
	 * Creates a new instance of the MO_Scheduling_mak_flow problem.
	 * 
	 * @param numberOfVariables Number of variables of the problem 
	 * @param solutionType The solution type must "Int"
	 */
	public MO_Scheduling_mak_flow( Integer numberOfVariables , String solutionType){
		numberOfMachines_    = ( numberOfMachines_            >0        )? numberOfMachines_            : numberOfMachinesByDefault_ ;
		numberOfVariables_   = ( numberOfVariables.intValue() >0        )? numberOfVariables.intValue() : numberOfVariablesByDefault_ ;
		
		numberOfObjectives_  = numberOfObjectivesByDefault_ ;
		numberOfConstraints_ = 0                            ;
		problemName_         = "MO_Scheduling_mak_flow"              ;

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

	} // MO_Scheduling_mak_flow
	
	/** Constructor.
	 * Creates a new instance of the MO_Scheduling_mak_flow problem.
	 * 
	 * @param numberOfVariables Number of variables of the problem 
	 * @param solutionType The solution type must "Int"
	 * @param numberOfIslands Number of islands to use, must be greater than 1
	 */
	public MO_Scheduling_mak_flow( Integer numberOfVariables , String solutionType , Integer numberOfIslands ){
		numberOfMachines_    = ( numberOfMachines_            >0        )? numberOfMachines_            : numberOfMachinesByDefault_ ;
		numberOfVariables_   = ( numberOfVariables.intValue() >0        )? numberOfVariables.intValue() : numberOfVariablesByDefault_ ;
		
		numberOfObjectives_  = numberOfObjectivesByDefault_ ;
		numberOfConstraints_ = 0                            ;
		problemName_         = "MO_Scheduling_mak_flow"              ;

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

	} // MO_Scheduling_mak_flow

	
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
	private double ComputeFlowtime( int[]  Schedule ) {
			                        //, double[] [] ETC){

		//ArrayList<Double> Assig[ numberOfMachines_ ];
		//ArrayList<Double>[ numberOfMachines_ ] Assig;

		//ArrayList<Double>[] Assig;
		//Assig = new ArrayList<Double>[numberOfMachines_];
		//
		
		ArrayList<ArrayList<Double>> Assig = new ArrayList<ArrayList<Double>>();
		//ArrayList Assig = new ArrayList();

		for( int m=0 ; m<numberOfMachines_ ; m++ ){
			ArrayList<Double> col = new ArrayList<Double>();
			Assig.add(col);
		}

		// Add tasks to machines
		for( int j=0 ; j<numberOfTasks_ ; j++ ){
			int mach = Schedule[j];
			(Assig.get(mach)).add(ETC_[j][mach]);
		}

		//ArrayList list_new; 
		for( int m=0 ; m<numberOfMachines_ ; m++ ){
			//list_new = (ArrayList).get(m);
			//Collections.sort(list_new);
			Collections.sort((ArrayList<Double>)Assig.get(m));
		}

		double flowtime = 0.0;
		int num_task = 0;
		for( int m=0 ; m<numberOfMachines_ ; m++ ){
			num_task = (Assig.get(m)).size();
			for ( int k = 0; k < num_task ; k++){
				//flowtime+=(((ArrayList)Assig.get(m)).size())-k*(Double)(((ArrayList)Assig.get(m)).get(k));
				flowtime = flowtime + (num_task-k)*((Assig.get(m)).get(k));
			}
		}
			
		return( flowtime );
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
		double flowtime = ComputeFlowtime( Schedule );

		// Find maximum of completion time
		// Find maximum robustness
		double maxCompletion = completion[0];
		
		// For efficiency issues, we are compute 0.3*CT instead of 1.3*M-CT for the robustness radius
		// Therefore, the robustness of the solution is the case in which M=CT, 
		// so it is the maximum robustness radio computed this way (when CT is max, i.e., CT = M),
		// instead of the minimum robustness radio computed as 1.3*M-CT (when M-CT is min, i.e., CT = M)
		// double maxRobustness = radius[0];

		for( int k=1 ; k<numberOfMachines_ ; ++k ){
			if ( completion[k] > maxCompletion )
				maxCompletion = completion[k];
		} // for

		// Return maximum and minimum as fitness values of makespan and robustness respectively
		double[] fitness = new double[2];
		
		fitness[0]       = maxCompletion;
		fitness[1]       = flowtime;

		return( fitness );
	} // fitness


	/** 
	 * Evaluates a solution 
	 * @param solution The solution to evaluate
	 * @param loadingPosition Loading position
	 * @throws JMException 
	 */
	public void evaluate( Solution solution ) throws JMException {
		DecisionVariables gen;

		// Recover the solution values
		gen = solution.getDecisionVariables();

		int[]    Schedule = new int[numberOfTasks_];  // Contains the solution values

		// Recover solution parameters
		for( int var=0 ; var<numberOfTasks_ ; ++var ) {
			Schedule[var] = (int) gen.variables_[var].getValue();   
		} // for

		// Return the fitness values
		setObjectives(solution, Fitness( Schedule ) );
	} // evaluate


	  /**
	   * Sets the value of all the objectives in the solution.
	   * @param solution The solution to modify.
	   * @param fitness The fitness values to be stored.
	   */
	  public void setObjectives(Solution solution, double[] fitness) {
		  for (int i=0; i<numberOfObjectivesByDefault_; i++)
		  {
			  solution.setObjective(i, fitness[i]);
		  }
		}
	  
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
	
	
	@Override
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
				// RUSO
				} else if ( type.compareToIgnoreCase("minMin") == 0 ) {
					int[] Schedule = ScheduleStrategy.minMin( M_.getETCmatrix() , numberOfTasks_ , numberOfMachines_ );
			
					Variable[] variables = new Variable[ numberOfTasks_];
					for( int i=0 ; i<numberOfTasks_ ; ++i )
						variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

					decisionVariables = new DecisionVariables( this , variables );
				} else if ( type.compareToIgnoreCase("Sufferage") == 0 ) {
					int[] Schedule = ScheduleStrategy.Sufferage( M_.getETCmatrix() , numberOfTasks_ , numberOfMachines_ );
			
					Variable[] variables = new Variable[ numberOfTasks_];
					for( int i=0 ; i<numberOfTasks_ ; ++i )
						variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

					decisionVariables = new DecisionVariables( this , variables );
				} else if ( type.compareToIgnoreCase("Sufferage_rand") == 0 ) {
					int[] Schedule = ScheduleStrategy.Sufferage_rand( M_.getETCmatrix() , numberOfTasks_ , numberOfMachines_ );
			
					Variable[] variables = new Variable[ numberOfTasks_];
					for( int i=0 ; i<numberOfTasks_ ; ++i )
						variables[i] = new Int( Schedule[i] , 0 ,  numberOfMachines_ );

					decisionVariables = new DecisionVariables( this , variables );
				// RUSO
			} // if
			else {
				Configuration.logger_.severe( "MO_Scheduling_mak_flow.generateSpecial: type \"" + 
						                      type + "\" unknown." );
				throw new RuntimeException( "Exception in MO_Scheduling_mak_flow.generateSpecial( String ) ") ;
			} // else
		} // if
		else {
			DecisionVariables slices;
			slices = new DecisionVariables( this );

			decisionVariables = new DecisionVariables( slices );
		} // else

		return decisionVariables;
	} // generateSpecial

} // MO_Scheduling_mak_flow
