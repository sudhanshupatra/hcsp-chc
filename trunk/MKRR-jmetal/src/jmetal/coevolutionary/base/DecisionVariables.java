package jmetal.coevolutionary.base;


import java.io.Serializable;
import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Problem;
import jmetal.base.Variable;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Binary;
import jmetal.base.variable.BinaryReal;
import jmetal.base.variable.Int;
import jmetal.base.variable.Permutation;
import jmetal.base.variable.Real;


/** 
 * This class contains the decision variables of a solution
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class DecisionVariables implements Serializable {  

	private static final long serialVersionUID = 1242161110319010038L;

	public  Variable[] variables_ ; ///< Stores the decision variables of a solution
	private Problem    problem_   ; ///< The problem to solve


	/**
	 * Constructor
	 * @param problem The problem to solve
	 */
	public DecisionVariables( Problem problem ){

		problem_   = problem;
		variables_ = new Variable[problem_.getNumberOfVariables()];

		if (problem.getSolutionType() == SolutionType_.Binary) {
			for (int var = 0; var < problem_.getNumberOfVariables(); var++)
				variables_[var] = new Binary(problem_.getLength(var));       
		} // if
		else if (problem.getSolutionType() == SolutionType_.Real) {
			for (int var = 0; var < problem_.getNumberOfVariables(); var++)
				variables_[var] = new Real(problem_.getLowerLimit(var),
						problem_.getUpperLimit(var));               
		} // else if
		else if (problem.getSolutionType() == SolutionType_.Int) {
			for (int var = 0; var < problem_.getNumberOfVariables(); var++)
				variables_[var] = new Int((int)problem_.getLowerLimit(var),
						(int)problem_.getUpperLimit(var));    
		} // else if
		else if (problem.getSolutionType() == SolutionType_.Permutation) {
			for (int var = 0; var < problem_.getNumberOfVariables(); var++)
				variables_[var] = new Permutation(problem_.getLength(var)) ;   
		} // else if
		else if (problem.getSolutionType() == SolutionType_.BinaryReal) {
			for (int var = 0; var < problem_.getNumberOfVariables(); var++) {
				if (problem.getPrecision() == null) {
					int [] precision = new int[problem.getNumberOfVariables()] ;
					for (int i = 0; i < problem.getNumberOfVariables(); i++)
						precision[i] = jmetal.base.Configuration.DEFAULT_PRECISION ;
					problem.setPrecision(precision) ;
				} // if
				variables_[var] = new BinaryReal(problem_.getPrecision(var),
						problem_.getLowerLimit(var),
						problem_.getUpperLimit(var));   
			} // for 
		} // else if
		else if (problem.getSolutionType() == SolutionType_.IntReal) {
			for (int var = 0; var < problem_.getNumberOfVariables(); var++)
				if (problem.variableType_[var] == VariableType_.Int)
					variables_[var] = new Int((int)problem_.getLowerLimit(var),
							(int)problem_.getUpperLimit(var)); 
				else if (problem.variableType_[var] == VariableType_.Real)
					variables_[var] = new Real(problem_.getLowerLimit(var),
							problem_.getUpperLimit(var));  
				else {
					Configuration.logger_.severe("DecisionVariables.DecisionVariables: " +
							"error creating a Solution of type IntReal") ;
				} // else
		} // else if
		else {
			Configuration.logger_.severe("DecisionVariables.DecisionVariables: " +
					"the solution type " + problem.getSolutionType() + " is incorrect") ;
			//System.exit(-1) ;
		} // else
	} // DecisionVariable

	
	/**
	 * Copy constructor
	 * @param decisionVariables The <code>DecisionVariables</code> object to copy.
	 */
	public DecisionVariables( DecisionVariables decisionVariables ){
		
		problem_ = decisionVariables.problem_;
		variables_ = new Variable[decisionVariables.variables_.length];
		for (int var = 0; var < decisionVariables.variables_.length; var++) {
			variables_[var] = decisionVariables.variables_[var].deepCopy();
		} // for
	} // DecisionVariable


	/** This constructor creates a new DecisionVariables based in the input parameters. This method
	 * performs a concatenation of the array of DecisionVariables, but inserting the DecisionVariable
	 * in the correct order.
	 * @param decisionVariables
	 * @param restOfDecisionVariables
	 * @param loadingPosition
	 */
	public DecisionVariables( DecisionVariables decisionVariables, DecisionVariables[] restOfDecisionVariables, int loadingPosition ) {

		problem_ = decisionVariables.problem_;
		
		int position = 0;
		int length   = decisionVariables.variables_.length;
		variables_ = new Variable[ length*(1+restOfDecisionVariables.length) ];

		if ( loadingPosition==0 ){
			// Insert the objetive in the first place and concatenates the rest
			System.arraycopy( decisionVariables.variables_ , 0 , variables_ , position , length );
			position+=length;
			
			for( int k=0 ; k<restOfDecisionVariables.length ; ++k , position+=length )
				System.arraycopy( restOfDecisionVariables[k].variables_ , 0 , variables_ , position , length );

		} // if
		else {
			// Insert the objetives in the middle
			System.arraycopy( restOfDecisionVariables[0].variables_ , 0 , variables_ , position , length );
			position+=length;
			
			for( int k=1 ; k<restOfDecisionVariables.length ; ++k , position+=length ){
				// Insert the objetive in the loading position
				if ( loadingPosition==k ) {
					System.arraycopy( decisionVariables.variables_ , 0 , variables_ , position , length);
					position+=length;
				} // if
				System.arraycopy( restOfDecisionVariables[k].variables_ , 0 , variables_ , position , length );
			} // for
			if ( loadingPosition == restOfDecisionVariables.length )
				System.arraycopy( decisionVariables.variables_ , 0 , variables_ , position , length );
		} // else
	} // DecisionVariables


	/** This private constructor creates a new DecisionVariables based in the
	 * input parameters.
	 * @param problem The problem to solve
	 * @param variables The variables desired
	 */
	public DecisionVariables( Problem problem , Variable[] variables ){
		problem_   = problem;
		variables_ = variables;
	} // DecisionVariables
	
	
	/**
	 * Extract a slice (subset) of variables of the <code>DecisionVariables</code>
	 * @param loadingPosition The slice number to extract
	 * @return the new slice of the <code>DecisionVariables</code>
	 */
	public DecisionVariables extractSlice( int loadingPosition ){
		
		int slices = problem_.getNumberOfIslands();
		// Range adjustement		
		loadingPosition = ( loadingPosition <  0      )? 0        : loadingPosition ;
		loadingPosition = ( loadingPosition >= slices )? slices-1 : loadingPosition ;
		
		// Size and positions calculations
		int sliceSz = variables_.length / slices;
		int start   = sliceSz * loadingPosition ;
		
		// Prepare the slice of the decision variables to return
		Variable[] variables = new Variable[ sliceSz ];
		System.arraycopy( this.variables_ , start , variables , 0 , sliceSz );
		return new DecisionVariables( problem_ , variables );
	} // extractSlice


	/**
	 * Returns the number of decision variables.
	 * @return The number of decision variables.
	 */
	public int size(){

		return variables_.length;
	} // size


	/** Returns a String that represent the DecisionVariable
	 * @return The string.
	 */
	public String toString() {
		String aux = "";
		for (int i = 0; i < variables_.length; i++)
			aux+= " "+variables_[i].toString();
		return aux;
	} // toString

} // DecisionVariables
