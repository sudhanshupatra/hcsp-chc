/**
 * Solution.java
 * 
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero
 * @version 1.0 
 */

package jmetal.coevolutionary.base;


import java.io.Serializable;
import jmetal.coevolutionary.base.Problem;
import jmetal.base.Configuration.SolutionType_;


/**
 * Class representing a solution for a problem.
 */
public class Solution implements Serializable {        

	private static final long serialVersionUID = 6565677787957011486L;

	private SolutionType_       type_                       ; ///< Stores the type of the variable
	private DecisionVariables   decisionVariable_           ; ///< Stores the decision variables of the solution.
	private DecisionVariables[] externalDecisionVariables_  ; ///< The external decisionVariables
	private double[]            objective_                  ; ///< Stores the objectives values of the solution.
	private int                 numberOfObjectives_         ; ///< Stores the number of objective values of the solution
	private double              fitness_                    ; ///< Stores the so called fitness value. Used in some metaheuristics
	private int                 rank_                       ; ///< Stores the so called rank of the solution. Used in NSGA-II
	private double              overallConstraintViolation_ ; ///< Stores the overall constraint violation of the solution.
	private int                 numberOfViolatedConstraints_; ///< Stores the number of constraints violated by the solution.

	/**
	 * Used in algorithm AbYSS, this field is intended to be used to know
	 * when a <code>Solution</code> is marked.
	 */
	private boolean marked_ ;

	/**
	 * This field is intended to be used to know the location of
	 * a solution into a <code>SolutionSet</code>. Used in MOCell
	 */
	private int location_ ;

	/**
	 * Stores the distance to his k-nearest neighbor into a 
	 * <code>SolutionSet</code>. Used in SPEA2.
	 */
	private double kDistance_ ; 

	/**
	 * Stores the crowding distance of the the solution in a 
	 * <code>SolutionSet</code>. Used in NSGA-II.
	 */
	private double crowdingDistance_ ; 
	
	/**
	 * Stores the distance between this solution and a <code>SolutionSet</code>.
	 * Used in AbySS.
	 */
	private double distanceToSolutionSet_ ;


	/**
	 * Constructor.
	 */
	public Solution() {
		marked_                       = false ;
		overallConstraintViolation_   = 0.0   ;
		numberOfViolatedConstraints_  = 0     ;  
		type_                         = SolutionType_.Undefined ;
		decisionVariable_             = null ;
		objective_                    = null ;
		externalDecisionVariables_    = null ;
	} // Solution

	
	/**
	 * This constructor is used mainly to read objective values from a file to
	 * variables of a SolutionSet to apply quality indicators
	 * 
	 * @param numberOfObjectives Number of objectives of the solution
	 */
	public Solution( int numberOfObjectives ) {
		
		numberOfObjectives_        = numberOfObjectives;
		objective_                 = new double[numberOfObjectives];
		externalDecisionVariables_ = null;
	} // Solution


	/** 
	 * Constructor.
	 * @param problem The problem to solve
	 */
	public Solution( Problem problem ){

		// Initializing state variables and allocating memory
		type_                      = problem.getSolutionType() ;
		numberOfObjectives_        = problem.getNumberOfObjectives() ;
		objective_                 = new double[numberOfObjectives_] ;
		// Setting initial values
		fitness_                   = 0.0 ;
		kDistance_                 = 0.0 ;
		crowdingDistance_          = 0.0 ;        
		distanceToSolutionSet_     = Double.POSITIVE_INFINITY ;
		decisionVariable_          = new DecisionVariables( problem );
		externalDecisionVariables_ = null;
	} // Solution

	
	/** 
	 * Constructor
	 * @param problem The problem to solve
	 * @param variables The decision variables
	 */
	public Solution( Problem problem, DecisionVariables variables ){
	
		//-> Initializing state variables and allocating memory
		type_                      = problem.getSolutionType() ;
		numberOfObjectives_        = problem.getNumberOfObjectives() ;
		objective_                 = new double[numberOfObjectives_] ;
		// Setting initial values
		fitness_                   = 0.0 ;
		kDistance_                 = 0.0 ;
		crowdingDistance_          = 0.0 ;        
		distanceToSolutionSet_     = Double.POSITIVE_INFINITY ;
		decisionVariable_          = variables ;
		externalDecisionVariables_ = null;
	} // Constructor

	
	/** 
	 * Copy constructor.
	 * @param solution Solution to copy.
	 */    
	public Solution( Solution solution ) {

		//-> Initializing state variables
		type_                        = solution.type_;
		numberOfObjectives_          = solution.numberOfObjectives_;
		objective_                   = solution.objective_.clone();
		decisionVariable_            = new DecisionVariables( solution.decisionVariable_ );
		overallConstraintViolation_  = solution.overallConstraintViolation_;
		numberOfViolatedConstraints_ = solution.numberOfViolatedConstraints_;
		distanceToSolutionSet_       = solution.distanceToSolutionSet_;
		crowdingDistance_      	     = solution.crowdingDistance_;
		kDistance_             	     = solution.kDistance_;                
		fitness_               	     = solution.fitness_;
		marked_                	     = solution.marked_;
		rank_                  	     = solution.rank_;
		location_              	     = solution.location_;
		externalDecisionVariables_   = solution.externalDecisionVariables_.clone();
	} // Solution


	/** This contructor creates a new expanded <code>Solution</code>
	 * @param solution solution
	 * @param loadingPosition the position to insert
	 */
	public Solution( Solution solution , int loadingPosition ){
		
		type_ = solution.type_;
		
		this.numberOfObjectives_          = solution.numberOfObjectives_         ;
		this.objective_                   = solution.objective_                  ;
		
		this.overallConstraintViolation_  = solution.overallConstraintViolation_ ;
		this.numberOfViolatedConstraints_ = solution.numberOfViolatedConstraints_;
		this.distanceToSolutionSet_       = solution.distanceToSolutionSet_      ;
		this.crowdingDistance_            = solution.crowdingDistance_           ;
		this.kDistance_                   = solution.kDistance_                  ;
		this.fitness_                     = solution.fitness_                    ;
		this.marked_                      = solution.marked_                     ;
		this.rank_                        = solution.rank_                       ;
		this.location_                    = solution.location_                   ;

		if ( solution.externalDecisionVariables_!=null )
			this.decisionVariable_ = new DecisionVariables( solution.decisionVariable_ , solution.externalDecisionVariables_ , loadingPosition );
		else
			this.decisionVariable_ = solution.decisionVariable_;
	} // Solution


	/**
	 * Get a new solution from a determinate problem
	 * @param problem the problem
	 * @return a new solution
	 */
	static public Solution getNewSolution( Problem problem ) {

		return new Solution( problem ) ;
	} // getNewSolution


	/**
	 * Sets the distance between this solution and a <code>SolutionSet</code>.
	 * The value is stored in <code>distanceToSolutionSet_</code>.
	 * @param distance The distance to a solutionSet.
	 */
	public void setDistanceToSolutionSet( double distance ){

		distanceToSolutionSet_ = distance;
	} // SetDistanceToSolutionSet

	
	/**
	 * Gets the distance from the solution to a <code>SolutionSet</code>. 
	 * <b> REQUIRE </b>: this method has to be invoked after calling 
	 * <code>setDistanceToPopulation</code>.
	 * @return the distance to a specific solutionSet.
	 */
	public double getDistanceToSolutionSet(){

		return distanceToSolutionSet_;
	} // getDistanceToSolutionSet


	/** 
	 * Sets the distance between the solution and its k-nearest neighbor in 
	 * a <code>SolutionSet</code>. The value is stored in <code>kDistance_</code>.
	 * @param distance The distance to the k-nearest neighbor.
	 */
	public void setKDistance(double distance){

		kDistance_ = distance;
	} // setKDistance

	
	/** 
	 * Gets the distance from the solution to his k-nearest nighbor in a 
	 * <code>SolutionSet</code>. Returns the value stored in
	 * <code>kDistance_</code>. <b> REQUIRE </b>: this method has to be invoked 
	 * after calling <code>setKDistance</code>.
	 * @return the distance to k-nearest neighbor.
	 */
	public double getKDistance(){

		return kDistance_;
	} // getKDistance

	
	/**
	 * Sets the crowding distance of a solution in a <code>SolutionSet</code>.
	 * The value is stored in <code>crowdingDistance_</code>.
	 * @param distance The crowding distance of the solution.
	 */  
	public void setCrowdingDistance(double distance){

		crowdingDistance_ = distance;
	} // setCrowdingDistance


	/** 
	 * Gets the crowding distance of the solution into a <code>SolutionSet</code>.
	 * Returns the value stored in <code>crowdingDistance_</code>.
	 * <b> REQUIRE </b>: this method has to be invoked after calling 
	 * <code>setCrowdingDistance</code>.
	 * @return the distance crowding distance of the solution.
	 */
	public double getCrowdingDistance(){

		return crowdingDistance_;
	} // getCrowdingDistance

	
	/**
	 * Sets the fitness of a solution.
	 * The value is stored in <code>fitness_</code>.
	 * @param fitness The fitness of the solution.
	 */
	public void setFitness(double fitness) {

		fitness_ = fitness;
	} // setFitness

	
	/**
	 * Gets the fitness of the solution.
	 * Returns the value of stored in the variable <code>fitness_</code>.
	 * <b> REQUIRE </b>: This method has to be invoked after calling 
	 * <code>setFitness()</code>.
	 * @return the fitness.
	 */
	public double getFitness() {

		return fitness_;
	} // getFitness

	
	/**
	 * Sets the value of the i-th objective.
	 * @param i The number identifying the objective.
	 * @param value The value to be stored.
	 */
	public void setObjective(int i, double value) {

		objective_[i] = value;
	} // setObjective
	
	
	/**
	 * Sets the values of the objectives.
	 * @param values The values to be stored.
	 */
	public void setObjectives( double[] values ) {

		System.arraycopy( values , 0 , objective_ , 0 , numberOfObjectives_ );
	} // setObjectives

	
	/**
	 * Returns the value of the i-th objective.
	 * @param i The value of the objective.
	 */
	public double getObjective(int i) {

		return objective_[i];
	} // getObjective

	
	/**
	 * Returns the number of objectives.
	 * @return The number of objectives.
	 */
	public int numberOfObjectives() {
		if (objective_ == null)
			return 0 ;
		else
			return numberOfObjectives_;
	} // numberOfObjectives

	
	/**  
	 * Returns the number of decision variables of the solution.
	 * @return The number of decision variables.
	 */
	public int numberOfVariables() {
		if (decisionVariable_ == null)
			return 0 ;
		else  
			return decisionVariable_.size();
	} // numberOfVariables

	
	/** 
	 * Returns a string representing the solution.
	 * @return The string.
	 */
	public String toString() {
		String aux="";
		for (int i = 0; i < this.numberOfObjectives_; i++)
			aux = aux + this.getObjective(i) + " ";
		return aux;
	} // toString

	
	/**
	 * Returns the decision variables of the solution.
	 * @return the <code>DecisionVariables</code> object representing the decision
	 * variables of the solution.
	 */
	public DecisionVariables getDecisionVariables() {
		return this.decisionVariable_;
	} // getDecisionVariables

	
	/**
	 * Sets the decision variables for the solution.
	 * @param decisionVariables The <code>DecisionVariables</code> object 
	 * representing the decision variables of the solution.
	 */
	public void setDecisionVariables(DecisionVariables decisionVariables) {
		this.decisionVariable_ = decisionVariables;
	} // setDecisionVariables

	
	/**
	 * Indicates if the solution is marked.
	 * @return true if the method <code>marked</code> has been called and, after 
	 * that, the method <code>unmarked</code> hasn't been called. False in other
	 * case.
	 */
	public boolean isMarked() {
		return this.marked_;
	} // isMarked

	
	/**
	 * Establishes the solution as marked.
	 */
	public void marked() {
		this.marked_ = true;
	} // marked

	
	/**
	 * Established the solution as unmarked.
	 */
	public void unMarked() {
		this.marked_ = false;
	} // unMarked

	
	/**  
	 * Sets the rank of a solution. 
	 * @param value The rank of the solution.
	 */
	public void setRank(int value){
		this.rank_ = value;
	} // setRank

	
	/**
	 * Gets the rank of the solution.
	 * <b> REQUIRE </b>: This method has to be invoked after calling 
	 * <code>setRank()</code>.
	 * @return the rank of the solution.
	 */
	public int getRank(){
		return this.rank_;
	} // getRank

	
	/**
	 * Sets the overall constraints violated by the solution.
	 * @param value The overall constraints violated by the solution.
	 */
	public void setOverallConstraintViolation(double value) {
		this.overallConstraintViolation_ = value;
	} // setOverallConstraintViolation

	
	/**
	 * Gets the overall constraint violated by the solution.
	 * <b> REQUIRE </b>: This method has to be invoked after calling 
	 * <code>overallConstraintViolation</code>.
	 * @return the overall constraint violation by the solution.
	 */
	public double getOverallConstraintViolation() {
		return this.overallConstraintViolation_;
	}  //getOverallConstraintViolation


	/**
	 * Sets the number of constraints violated by the solution.
	 * @param value The number of constraints violated by the solution.
	 */
	public void setNumberOfViolatedConstraint(int value) {
		this.numberOfViolatedConstraints_ = value;
	} //setNumberOfViolatedConstraint

	
	/**
	 * Gets the number of constraint violated by the solution.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>setNumberOfViolatedConstraint</code>.
	 * @return the number of constraints violated by the solution.
	 */
	public int getNumberOfViolatedConstraint() {
		return this.numberOfViolatedConstraints_;
	} // getNumberOfViolatedConstraint

	
	/**
	 * Sets the location of the solution into a solutionSet. 
	 * @param location The location of the solution.
	 */
	public void setLocation(int location) {
		this.location_ = location;
	} // setLocation

	
	/**
	 * Gets the location of this solution in a <code>SolutionSet</code>.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>setLocation</code>.
	 * @return the location of the solution into a solutionSet
	 */
	public int getLocation() {
		return this.location_;
	} // getLocation

	
	/**
	 * Sets the type of the variable. 
	 * @param type The type of the variable.
	 */
	public void setType(String type) {
		type_ = Enum.valueOf(SolutionType_.class,type) ;
	} // setType

	
	/**
	 * Sets the type of the variable. 
	 * @param type The type of the variable.
	 */
	public void setType(SolutionType_ type) {
		type_ = type ;
	} // setType

	
	/**
	 * Gets the type of the variable
	 * @return the type of the variable
	 */
	public SolutionType_ getType() {
		return type_;
	} // getType



	/** 
	 * Returns the aggregative value of the solution
	 * @return The aggregative value.
	 */
	public double getAggregativeValue() {
		
		double value = 0.0;
		for (int i = 0; i < numberOfObjectives(); i++){            
			value += getObjective(i);
		} // for
		return value;
	} // getAggregativeValue


	// Obsolete
	/* The solution receives new objectives from another solution
	 * @param sourceSolution the source of the objectives
	 *
	public void receiveNewObjectives( Solution sourceSolution ) {

		System.arraycopy( sourceSolution.objective_ , 0 , objective_ , 0 , sourceSolution.objective_.length );
	} // substituteObjectives */


	/** Assign the desired external DecisionVariables who help to build the real Solution
	 * @param row The new externals DecisionVariables
	 */
	public void assignExternalDV( DecisionVariables[] row ){
		boolean nullContent; // Indicates if the external DV are null or is null its content
		int numberOfPieces = row.length;
		
		nullContent = ( externalDecisionVariables_==null );
		if ( !nullContent ){
			int i=0;
			while( !nullContent && (i<numberOfPieces)){
				nullContent = ( externalDecisionVariables_[i]==null ) ;
				++i;
			} // while
		} // if

		if ( nullContent ){
			externalDecisionVariables_ = row.clone();
		} // if
		else
			throw new NullPointerException("The external vector of DecisionVariables has been fixed and its posterior modification isn't allowed.");
	} // assignExternalDV
	
	
	/** This method obtain the external decision variables, for correct evaluation of the decisionVariables of one solution
	 * @return the external decision variables
	 */
	public DecisionVariables[] getExternalDecisionVariables() {

		return externalDecisionVariables_;
	} // getExternalDecisionVariables


	/** This method returns if this is linked to a determinate population
	 * @return If this solution is linked to a population
	 */
	public boolean isLinked() {
		boolean nullContent = ( externalDecisionVariables_==null );
		
		if ( !nullContent ){
			int i=0;
			int n=externalDecisionVariables_.length;
			while( !nullContent && (i<n)){
				nullContent = ( externalDecisionVariables_[i]==null ) ;
				++i;
			} // while
		} // if
		return !nullContent;
	} // isLinked


	/**
	 * Releases a solution of their membership of a population
	 */
	public void unLink() {

		externalDecisionVariables_ = null;
		for( int i=0 ; i<objective_.length ; ++i )
			objective_[i] = 0.0;
	} // unlink


	/**
	 * @return Objectives
	 */
	public double[] getObjectives() {

		return this.objective_;
	} // getObjectives

} // Solution
