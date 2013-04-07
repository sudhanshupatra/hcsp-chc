/**
 * Includes the SPEA2 metaheuristic, coevolutionary version
 */

package jmetal.coevolutionary.metaheuristics.spea2;

import java.util.Iterator;

import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.coevolutionary.util.Ranking;
import jmetal.coevolutionary.util.Spea2Fitness;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


/** 
 * This class representing the SPEA2 algorithm
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class SPEA2Sched extends Algorithm {

	private static final long serialVersionUID = -4943837448092079863L;
	
	private int                 populationSize_;        ///< Size of the population
	private int                 archiveSize_;           ///< Size of the archive
	private int                 maxEvaluations_;        ///< Maximum number of evaluations
	private int                 evaluations_;
	private int                 islands_ = 1 ;          ///< Number of islands to use by default
	private int                 numberOfSolutions_ ;    ///< Number of solutions transferred to another islands
	private Operator            mutationOperator_  ;    ///< mutation operator
	private Operator            localSearchOperator_  ;
	private Operator            crossoverOperator_ ;    ///< crossover operator
	private Operator            selectionOperator_ ;    ///< selection operator
	private SolutionSet         population_;            ///< The population
	private SolutionSet         archive_;               ///< The archive
	private SolutionSet         offSpringSolutionSet_;  ///< Temporary <code>SolutionSet</code>	
	public static final int     TOURNAMENTS_ROUNDS = 1; ///< Defines the number of tournaments for creating the mating pool
	private Problem             problem_;               ///< Stores the problem to solve
	private DecisionVariables[] bestDecisionVariables_; ///< Stores the best solutions, ready to be sent to other islands

	private int 				islandID_;
	private int 				mergeSolution_ = 0; 
	/* 
	 * How to merge solutions? 
	 * - if 1 we pick on random partial solution from the shared 
	 *   ones in every island; 
	 * - if 0, then it is like in the book chapter, we choose a random 
	 *   index and compose the solution with the shared solution index 
	 *   in every island
	 * 
	 */
	
	/**
	 * Create a new SPEA2 instance
	 * @param problem Problem to solve
	 */
	public SPEA2Sched( Problem problem ){

		this.problem_ = problem;        
	} // Spea2


	public DecisionVariables[] getBestSolutions() {

		return bestDecisionVariables_;
	} // getBestSolutions
	
	
	private void prepareBestSolutions() {
		bestDecisionVariables_ = new DecisionVariables[numberOfSolutions_];

		int sz = archive_.size();
		
		if ( sz != 0 ) {
			if ( sz>=numberOfSolutions_ ) {
				// Archive is bigger than numberOfSolutions_
				int[] indices = RandomVector.getRandomVector_Int( numberOfSolutions_, sz );
				for( int i=0 ; i<numberOfSolutions_ ; ++i )
					bestDecisionVariables_[i] = archive_.get( indices[i] ).getDecisionVariables();
			} // if
			else {
				// The archive is too little, then we need to complement with currentPopulation_
				int[] indices = RandomVector.getRandomVector_Int( sz , sz );
				for( int i=0 ; i<sz ; ++i )
					bestDecisionVariables_[i] = archive_.get( indices[i] ).getDecisionVariables();
				int sz2 = population_.size();
				indices = RandomVector.getRandomVector_Int( numberOfSolutions_-sz, sz2 );
				for( int i=0 ; i<(numberOfSolutions_-sz) ; ++i )
					bestDecisionVariables_[i+sz] = population_.get( indices[i] ).getDecisionVariables();
			} // else
		} // if
		else{
			// The archive is 0 sized, then we must to use the population
			sz = population_.size();
			int[] indices = RandomVector.getRandomVector_Int( numberOfSolutions_, sz );
			for( int i=0 ; i<numberOfSolutions_ ; ++i )
				bestDecisionVariables_[i] = population_.get( indices[i] ).getDecisionVariables();
		} // else
	} // getBestSolutions



	


	public void setBestSolutions( DecisionVariables[] bestSolution , int islandId ){
		
		population_.setRemainBest( bestSolution , islandId );
		//archive_.setRemainBest( bestSolution , islandId );
	} // setOneBestSolution


	public void setUpBestSolutions( DecisionVariables[] newSolution , int islandId ){
		
		population_.setRemainBest( newSolution , islandId );
		//archive_.setRemainBest( newSolution , islandId );
	} // setOneBestSolution


	public void setup( int islandId ) throws JMException {
		
		islandID_ = islandId;

		//Read the params
		islands_            = ((Integer) this.getInputParameter( "numberOfIslands"   )).intValue();
		populationSize_     = ((Integer) this.getInputParameter( "populationSize"    )).intValue();
		archiveSize_        = ((Integer) this.getInputParameter( "archiveSize"       )).intValue();
		maxEvaluations_     = ((Integer) this.getInputParameter( "maxEvaluations"    )).intValue();
		numberOfSolutions_  = ((Integer) this.getInputParameter( "numberOfSolutions" )).intValue();
		mergeSolution_     = ((Integer) this.getInputParameter( "mergeSolution")).intValue();

		//Read the operators
		crossoverOperator_ = operators_.get( "crossover" );
		mutationOperator_  = operators_.get( "mutation"  );
		selectionOperator_ = operators_.get( "selection" );        
		localSearchOperator_ = operators_.get( "localsearch" );

		//Initialize the variables
		population_   = new SolutionSet( islandId , islands_ , numberOfSolutions_ , populationSize_  , mergeSolution_);
		archive_      = new SolutionSet( islandId , islands_ , numberOfSolutions_ , archiveSize_    );
		
		// Link externbest result of two populations
		archive_.setBestExternResults( population_ );
		
		evaluations_  = 0;

//		//-> Create the initial solutionSet
//		for (int i = 0; i < populationSize_; i++) {
//			Solution newSolution = new Solution( problem_ );
//			population_.add( newSolution );
//		} // for
		
		// Create the initial population
		Solution newSolution = null;
		String specialSolution         = ((String) getInputParameter( "specialSolution" ));
		
		int minminInit = PseudoRandom.randInt(0, populationSize_-1); // To initialize one individual with min-min
		
		for (int i = 0; i < populationSize_; i++){
			if ( specialSolution == null ){
	    		newSolution = new Solution(problem_);                    
	    	} // if
			else if (specialSolution.contains("OneMinmin")) {
				if (minminInit==i) {
					//int [] vars = ScheduleStrategy.minMin(ETC_, numberOfTasks, numberOfMachines)
					DecisionVariables specialDV   = problem_.generateSpecial( specialSolution , islandId);
		    		newSolution = new Solution( problem_ , specialDV );
				}
				else
					newSolution = new Solution(problem_);
				}
	    	else if (specialSolution.equalsIgnoreCase("Min-Min")){
	    		DecisionVariables specialDV   = problem_.generateSpecial( specialSolution , islandId);
	    		newSolution = new Solution( problem_ , specialDV );
	    	} // else
//			problem_.evaluate( newSolution );
//			problem_.evaluateConstraints( newSolution );
			population_.add( newSolution );
			newSolution.setLocation(i);
//			evaluations++;
		} // for
		
		prepareBestSolutions();
		
	} // setup
	
	
	/**
	 * This method is used to evaluate the first population,that it is called after
	 * the calling to <code>setup</code> method
	 * 
	 * @throws JMException
	 */
	public void evaluatePopulation() throws JMException{
		Iterator<Solution> it = population_.iterator();
		int loadingPosition = population_.getLoadingPosition();
		
		while( it.hasNext() ){
			Solution tmp = it.next();
			population_.linkExternalDecisionVariables( tmp );
			problem_.evaluate( tmp , loadingPosition );
			problem_.evaluateConstraints( tmp );
			archive_.add(tmp);
			++evaluations_;
		} // while
	} // evaluatePopulation


	public boolean stopCondition() {

		return( evaluations_ >= maxEvaluations_/islands_ );
	} // stopCondition
	
	
	public void generation() throws JMException {
		int loadingPosition = population_.getLoadingPosition();
		SolutionSet union = population_.union( archive_ );
		Spea2Fitness spea = new Spea2Fitness( union );
		spea.fitnessAssign();
		archive_ = spea.environmentalSelection( archiveSize_ );                       
		// Create a new offspringPopulation

		offSpringSolutionSet_ = new SolutionSet( loadingPosition , islands_ , numberOfSolutions_ , populationSize_  , mergeSolution_);
		offSpringSolutionSet_.setBestExternResults( population_ );
		Solution[] parents    = new Solution[2];
		while (offSpringSolutionSet_.size() < populationSize_){           
			int j = 0;
			do {
				j++;
				parents[0] = (Solution)selectionOperator_.execute(archive_,0);
			} while (j < SPEA2.TOURNAMENTS_ROUNDS); // do-while                    
			int k = 0;
			do {
				k++;                
				parents[1] = (Solution)selectionOperator_.execute(archive_,0);
			} while (k < SPEA2.TOURNAMENTS_ROUNDS); // do-while

			//make the crossover 
			Solution[] offSpring = ( Solution[] )crossoverOperator_.execute(parents,0);
			Solution newIndividual = offSpring[0];
			newIndividual.unLink();
			mutationOperator_.execute( newIndividual , 0 );

			// Apply local search
			if (localSearchOperator_ != null) 
				localSearchOperator_.execute(offSpring[0], islandID_);
			
			offSpringSolutionSet_.linkExternalDecisionVariables( newIndividual );
			problem_.evaluate( newIndividual , loadingPosition) ;
			problem_.evaluateConstraints( newIndividual );            
			offSpringSolutionSet_.add( newIndividual );
			++evaluations_;
		} // while
		// End Create a offSpring solutionSet
		population_ = offSpringSolutionSet_;
		
	} // generation
	
	
	public void postGeneration() {

		prepareBestSolutions();
	} // postGeneration
	
	
	public void postExecution() {
		Ranking ranking = new Ranking( archive_ );
		SolutionSet population = ranking.getSubfront(0);
		
		solutionSetToReturn_ = new SolutionSet( 0 , 1 , 0 , population.size() );
		Iterator<Solution> it = population.iterator();
		int loadingPosition = population.getLoadingPosition();
		while( it.hasNext() )
			solutionSetToReturn_.add( new Solution( it.next() , loadingPosition ) );
	} // postExecution


	@Override
	public Algorithm clone() {
		SPEA2Sched algorithmToReturn = new SPEA2Sched( problem_ );

		// First assigns Algorithm attributes
		algorithmToReturn.operators_            = operators_           ;
		algorithmToReturn.inputParameters_      = inputParameters_     ;  
		algorithmToReturn.outPutParameters_     = outPutParameters_    ;
		
		// Second assign SPEA2 params
		algorithmToReturn.populationSize_       = populationSize_      ;
		algorithmToReturn.archiveSize_          = archiveSize_         ;
		algorithmToReturn.maxEvaluations_       = maxEvaluations_      ;
		algorithmToReturn.evaluations_          = evaluations_         ;
		algorithmToReturn.islands_              = islands_             ;
		algorithmToReturn.numberOfSolutions_    = numberOfSolutions_   ;
		algorithmToReturn.mutationOperator_     = mutationOperator_    ;
		algorithmToReturn.crossoverOperator_    = crossoverOperator_   ;
		algorithmToReturn.selectionOperator_    = selectionOperator_   ;
		algorithmToReturn.population_           = population_          ;
		algorithmToReturn.archive_              = archive_             ;
		algorithmToReturn.offSpringSolutionSet_ = offSpringSolutionSet_;

		return algorithmToReturn;
	} // clone


	public SolutionSet getFinalSolutionSet() {

		return solutionSetToReturn_;
	} // getFinalSolutionSet
	
	
	@Override
	public int getPopulationSize() {

		return populationSize_;
	} // getPopulationSize

} // Spea2
