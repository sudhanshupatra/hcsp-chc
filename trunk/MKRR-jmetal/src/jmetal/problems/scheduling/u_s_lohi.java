package jmetal.problems.scheduling;


import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.Problem;
import jmetal.coevolutionary.util.Matrix;


/**
 * Class representing the problem u_s_lohi
 * 
 * UNIFORM distribution
 * SEMICONSISTENT
 * LOW task heterogeneity
 * HIGH machine heterogeneity
 * 
 * @author Juan A. Ca–ero Tamayo
 * 
 * @version 1.0
 */
public class u_s_lohi extends MO_Scheduling {

	private static final long serialVersionUID = -2210887953365922092L;

	private static final String xmlPath_   = "HCSP instances/512x16/"; ///< Base directory of the matrices instances
	private static final String xmlMatrix_ = "u_s_lohi.0.xml";         ///< Filename where its stored the matrix
	private static final String path_      = "SchedInstances/basicETC/"; ///< Base directory of the matrices instances
	private static final String name_      = "u_s_lohi";               ///< Name of the problem
//	private static final double per_       = 0.3;                      ///< Constant used in the matrix

	private int instanceNo = 0;
	
	/** Constructor
	 * @param numberOfVariables Number of variables of the problem 
	 * @param solutionType The solution type must "Int"
	 * @param numberOfIslands Number of islands to use, must be greater than 1
	 */
	public u_s_lohi( Integer numberOfVariables, String solutionType, Integer numberOfIslands ) {

		M_ = new Matrix();
		//M_.recoverXMLData( xmlPath_+xmlMatrix_ , per_ );
		M_.recoverData(512, 16, path_+name_+"."+instanceNo );
		
		ETC_  = M_.getETCmatrix();
		

		int numberOfVars  = M_.getNumberOfTasks() ;
		int numberOfMachs = M_.getNumberOfMachines();
		
		numberOfTasks_       = numberOfVars;
		numberOfMachines_    = ( numberOfMachs              > 0         )? numberOfMachs              : numberOfMachinesByDefault_  ;
		numberOfVariables_   = ( numberOfVars               > 0         )? numberOfVars               : numberOfVariablesByDefault_ ;
		
		numberOfObjectives_  = numberOfObjectivesByDefault_ ;
		numberOfConstraints_ = 0                            ;
		problemName_         = name_                        ;

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
		for( int var=0 ; var<numberOfVariables_ ; ++var )
			variableType_[var] = Enum.valueOf( VariableType_.class , solutionType );    

	} // u_s_lohi


	/** Constructor
	 * @param solutionType The solution type must "Int"
	 */
	public u_s_lohi( String solutionType ) {

		this( numberOfVariablesByDefault_ , solutionType , numberOfIslandsByDefault_ );
	} // u_c_hihi

	public void nextInstance()
	{
		instanceNo++;
	}


	@Override
	public Problem clone() {
		u_s_lohi newProblem = new u_s_lohi( "Int" );

		newProblem.M_                = this.M_                ;
		newProblem.numberOfMachines_ = this.numberOfMachines_ ;
		newProblem.numberOfTasks_    = this.numberOfTasks_    ;
		newProblem.ETC_              = this.ETC_              ;
		
		return( newProblem );
	} // clone

} // u_s_lohi
