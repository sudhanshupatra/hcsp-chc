package jmetal.coevolutionary.base;

import java.io.Serializable;

import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.util.JMException;


/**
 * Abstract class representing a multiobjective optimization problem
 */
public abstract class Problem implements Serializable {

	private static final long serialVersionUID = -6394318048785125621L;

	protected int             numberOfIslands_    ; ///< Number of islands to use
	protected int             numberOfVariables_  ; ///< Stores the number of variables of the problem
	protected int             numberOfObjectives_ ; ///< Stores the number of objectives of the problem
	protected int             numberOfConstraints_; ///< Stores the number of constraints of the problem
	protected String          problemName_        ; ///< Stores the problem name
	protected SolutionType_   solutionType_       ; ///< Stores the type of the solutions of the problem
	protected double[]        lowerLimit_         ; ///< Stores the lower bound values for each variable (only if needed)
	protected double[]        upperLimit_         ; ///< Stores the upper bound values for each variable (only if needed)
	public    VariableType_[] variableType_       ; ///< Stores the type of each variable

	/**
	 * Stores the number of bits used by binary coded variables (e.g., BinaryReal
	 * variables). By default, they are initialized to DEFAULT_PRECISION)
	 */
	protected int[]           precision_          ;

	/**
	 * Stores the length of each variable when applicable (e.g., Binary and 
	 * Permutation variables)
	 */
	protected int[]           length_             ;

	
	

	/** 
	 * Constructor. 
	 */
	public Problem() {
		solutionType_ = SolutionType_.Undefined ;
	} // Problem


	/** 
	 * Gets the number of decision variables of the problem.
	 * @return the number of decision variables.
	 */
	public int getNumberOfVariables() {
		return numberOfVariables_ ;   
	} // getNumberOfVariables


	/** 
	 * Gets the the number of objectives of the problem.
	 * @return the number of objectives.
	 */
	public int getNumberOfObjectives() {
		return numberOfObjectives_ ;
	} // getNumberOfObjectives


	/** 
	 * Gets the lower bound of the ith variable of the problem.
	 * @param i The index of the variable.
	 * @return The lower bound.
	 */
	public double getLowerLimit(int i) {
		return lowerLimit_[i] ;
	} // getLowerLimit


	/** 
	 * Gets the upper bound of the ith variable of the problem.
	 * @param i The index of the variable.
	 * @return The upper bound.
	 */
	public double getUpperLimit(int i) {
		return upperLimit_[i] ;
	} // getUpperLimit 

	
	/**
	 * Evaluates a <code>Solution</code> object.
	 * @param solution The <code>Solution</code> to evaluate.
	 * @param islandId The island identificator
	 */ 
	public abstract void evaluate( Solution solution , int islandId ) throws JMException;


	/**
	 * Gets the number of side constraints in the problem.
	 * @return the number of constraints.
	 */
	public int getNumberOfConstraints() {
		return numberOfConstraints_ ;
	} // getNumberOfConstraints


	/**
	 * Evaluates the overall constraint violation of a <code>Solution</code> 
	 * object.
	 * @param solution The <code>Solution</code> to evaluate.
	 */    
	public void evaluateConstraints(Solution solution) throws JMException {
		// The default behavior is to do nothing. Only constrained problems have to
		// re-define this method
	} // evaluateConstraints


	/**
	 * Returns the number of bits that must be used to encode variable.
	 * @return the number of bits.
	 */
	public int getPrecision(int var) {
		return precision_[var] ;
	} // getPrecision


	/**
	 * Returns array containing the number of bits that must be used to encode 
	 * the variables.
	 * @return the number of bits.
	 */
	public int [] getPrecision() {
		return precision_ ;
	} // getPrecision


	/**
	 * Sets the array containing the number of bits that must be used to encode 
	 * the variables.
	 * @param precision The array
	 */
	public void setPrecision(int [] precision) {
		precision_ = precision;
	} // getPrecision


	/**
	 * Returns the length of the variable.
	 * @return the variable length.
	 */
	public int getLength(int var) {
		return length_[var] ;
	} // getLength


	/**
	 * Sets the type of the variables of the problem.
	 * @param type The type of the variables
	 */
	public void setSolutionType(SolutionType_ type) {
		solutionType_ = type;
	} // setSolutionType


	/**
	 * Returns the type of the variables of the problem.
	 * @return type of the variables of the problem.
	 */
	public SolutionType_ getSolutionType() {
		return solutionType_ ;
	} // getSolutionType


	/**
	 * Returns the problem name
	 * @return The problem name
	 */
	public String getName() {
		return problemName_ ;
	} // getName


	/**
	 * This method transfers the objetive value from <code>origin</code> to <code>destination</code>
	 * 
	 * @param origin solution origin
	 * @param destination destination
	 */
	public void transferEvaluation(Solution origin, Solution destination) {
// FIXME Esto es incorrecto, hazlo mejor con System.arraycopy (reservando memoria)
		double fx0 = origin.getObjective( 0 );
		double fx1 = origin.getObjective( 1 );

		destination.setObjective( 0 , fx0 );
		destination.setObjective( 1 , fx1 );
	} // transferEvaluation
	
	
	/**
	 * @return the numberOfIslands_
	 */
	public int getNumberOfIslands() {
		return numberOfIslands_;
	} // getNumberOfIslands

	
	/**
	 * @param numberOfIslands_ the numberOfIslands_ to set
	 */
	public void setNumberOfIslands_(int numberOfIslands_) {
		
		this.numberOfIslands_ = numberOfIslands_;
	} // setNumberOfIslands

	
	/**
	 * This method clones a <code>Problem</code>.
	 * 
	 * <b>Note:</b> To develop correctly this method, use assignAttributesProblem.
	 * 
	 * @see Problem#assignAttributesProblem
	 */
	abstract public Problem clone();


	/**
	 * @param referenceProblem The problem of reference
	 */
	public void assignAttributesProblem( Problem referenceProblem ){

		this.numberOfIslands_    = referenceProblem.numberOfIslands_    ;
		this.numberOfVariables_  = referenceProblem.numberOfVariables_  ;
		this.numberOfObjectives_ = referenceProblem.numberOfObjectives_ ;
		this.numberOfConstraints_= referenceProblem.numberOfConstraints_;
		this.problemName_        = referenceProblem.problemName_        ;
		this.solutionType_       = referenceProblem.solutionType_       ;
		this.variableType_       = referenceProblem.variableType_       ;
		
		if ( referenceProblem.lowerLimit_!= null )
			this.lowerLimit_     = referenceProblem.lowerLimit_.clone() ;
		else
			this.lowerLimit_     = null                                 ;

		if ( referenceProblem.upperLimit_!= null )
			this.upperLimit_     = referenceProblem.upperLimit_.clone() ;
		else
			this.upperLimit_     = null                                 ;

		if ( referenceProblem.precision_!= null )
			this.precision_      = referenceProblem.precision_.clone()  ;
		else
			this.precision_      = null                                 ;
		
		if ( referenceProblem.length_!= null )
			this.length_         = referenceProblem.length_.clone()     ;
		else
			this.length_         = null                                 ;

	} // assignAttributesProblem

		/** This method generates a special and non-sliced DecisionVariable
	 * 
	 * @param type The type of solution generated
	 * @return a new decisionVariable
	 */
	public abstract DecisionVariables generateSpecial( String type );
	
	public abstract DecisionVariables generateSpecial( String type , int loadingPosition);

} // Problem
