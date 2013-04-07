/**
 * This package stores the MOCell algorithm
 */
package jmetal.coevolutionary.metaheuristics.mocell;


import java.util.Comparator;
import java.util.Iterator;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.archive.CrowdingArchive;
import jmetal.coevolutionary.base.operator.comparator.CrowdingComparator;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;
import jmetal.coevolutionary.util.Distance;
import jmetal.coevolutionary.util.Neighborhood;
import jmetal.coevolutionary.util.Ranking;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.util.JMException;


/** 
 * Class representing the MoCell algorithm
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class aMOCell4 extends Algorithm{

	private static final long serialVersionUID = -2495631790142344341L;
	
	private int                 islands_=1            ; ///< Number of islands (GAs) to use by default
	private Problem             problem_              ; ///< The problem to solve
	private int                 populationSize_       ;
	private int                 archiveSize_          ;
	private int                 maxEvaluations_       ;
	private int                 evaluations_          ;
	private Operator            mutationOperator_     ;
	private Operator            crossoverOperator_    ;
	private Operator            selectionOperator_    ;
	private SolutionSet         currentPopulation_    ;
	private CrowdingArchive     archive_              ;
	private SolutionSet[]       neighbors_            ;
	private Neighborhood        neighborhood_         ;
	private Comparator          dominance_            ;
	private Comparator          crowdingComparator_   ;
	private Distance            distance_             ;
	private int                 numberOfSolutions_    ; ///< Number of solutions transferred to another islands
	private DecisionVariables[] bestDecisionVariables_; ///< Stores the best solutions, ready to be sent to other islands

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
	 * Constructor
	 * @param problem The problem to solve
	 */
	public aMOCell4( Problem problem ){

		problem_ = problem;
	} // aMOCell4


	
	public void setup( int islandId ) throws JMException {

		//Init the param
		dominance_          = new DominanceComparator();  
		crowdingComparator_ = new CrowdingComparator();
		distance_           = new Distance();

		//Read the params
		islands_           = ((Integer) this.getInputParameter( "numberOfIslands" ) ).intValue();
		populationSize_    = ((Integer) this.getInputParameter( "populationSize")   ).intValue();
		archiveSize_       = ((Integer) this.getInputParameter( "archiveSize")      ).intValue();
		maxEvaluations_    = ((Integer) this.getInputParameter( "maxEvaluations")   ).intValue();
		numberOfSolutions_ = ((Integer) this.getInputParameter( "numberOfSolutions")).intValue();
		mergeSolution_     = ((Integer) this.getInputParameter( "mergeSolution")).intValue();

		//Read the operators
		mutationOperator_  = operators_.get( "mutation"  );
		crossoverOperator_ = operators_.get( "crossover" );
		selectionOperator_ = operators_.get( "selection" );        

		//Init the variables
		currentPopulation_  = new SolutionSet(     islandId , islands_ , numberOfSolutions_ , populationSize_ , mergeSolution_);
		archive_            = new CrowdingArchive( islandId                         ,
				                                   islands_                         ,
				                                   numberOfSolutions_               ,
				                                   archiveSize_                     , 
				                                   problem_.getNumberOfObjectives() );

		// Link externbest result of two populations
		archive_.setBestExternResults( currentPopulation_ );

		evaluations_        = 0;
		neighborhood_       = new Neighborhood( populationSize_ );
		neighbors_          = new SolutionSet[  populationSize_ ];

		// Create the initial population
		for (int i = 0; i < populationSize_; i++){
			Solution individual = new Solution( problem_ );
			currentPopulation_.add( individual );
			individual.setLocation( i );
		} // for
		
		prepareBestSolutions();
	} // setup


	public void evaluatePopulation() throws JMException {

		Iterator<Solution> it = currentPopulation_.iterator();
		int loadingPosition = currentPopulation_.getLoadingPosition();
		while( it.hasNext() ){
			Solution tmp = it.next();
			currentPopulation_.linkExternalDecisionVariables( tmp );
			problem_.evaluate( tmp , loadingPosition );
			problem_.evaluateConstraints( tmp );
			archive_.add(tmp);
			++evaluations_;
		} // while
	} // evaluatePopulation


	public void generation() throws JMException {
		
		for( int ind=0 ; ind<currentPopulation_.size() ; ind++ ){
			Solution individual = new Solution( currentPopulation_.get(ind) );

			Solution[] parents = new Solution[2];
			Solution[] offSpring;

			//neighbors[ind] = neighborhood.getFourNeighbors(currentPopulation,ind);
			neighbors_[ind] = neighborhood_.getEightNeighbors( currentPopulation_ , ind );
			neighbors_[ind].add( individual );

			// parents
			parents[0] = (Solution) selectionOperator_.execute(neighbors_[ind],0);
			if (archive_.size() > 0) {
				parents[1] = (Solution) selectionOperator_.execute(archive_,0);
			} // if
			else {                   
				parents[1] = (Solution) selectionOperator_.execute(neighbors_[ind],0);
			} // else

			// Create a new individual, using genetic operators mutation and crossover
			offSpring = ( Solution[] ) crossoverOperator_.execute( parents , 0 ); 
			Solution newIndividual = offSpring[0];
			newIndividual.unLink();
			mutationOperator_.execute( newIndividual , 0 );

			// Evaluate individual and his constraints
			currentPopulation_.linkExternalDecisionVariables( newIndividual );
			problem_.evaluate( newIndividual , currentPopulation_.getLoadingPosition() );
			problem_.evaluateConstraints( newIndividual );
			evaluations_++;

			int flag = dominance_.compare( individual , newIndividual );

			if (flag == 1) { //The new individual dominates
				newIndividual.setLocation( individual.getLocation() );
				currentPopulation_.replace( newIndividual.getLocation() , newIndividual );
				archive_.add(new Solution( newIndividual ));                   
			} // if
			else if (flag == 0) { //The new individual is non-dominated               
				neighbors_[ind].add(newIndividual);
				newIndividual.setLocation(-1);
				Ranking rank = new Ranking(neighbors_[ind]);
				for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
					distance_.crowdingDistanceAssignment( rank.getSubfront(j) , problem_.getNumberOfObjectives() );
				} // for
				neighbors_[ind].sort( crowdingComparator_ );
				Solution worst = neighbors_[ind].get( neighbors_[ind].size()-1 );

				if (worst.getLocation() == -1) { //The worst is the offspring
					archive_.add( new Solution(newIndividual) );
				} // if
				else {
					newIndividual.setLocation( worst.getLocation() );
					currentPopulation_.replace( newIndividual.getLocation() , newIndividual );
					archive_.add( new Solution(newIndividual) );
				} // else
			} // else if
		} // for
	} // generation


	public void postGeneration() {
		
		prepareBestSolutions();
	} // postGeneration


	public DecisionVariables[] getBestSolutions() {

		return bestDecisionVariables_;
	} // getBestSolution
	
	
	private void prepareBestSolutions(){
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
				int sz2 = currentPopulation_.size();
				indices = RandomVector.getRandomVector_Int( numberOfSolutions_-sz, sz2 );
				for( int i=0 ; i<(numberOfSolutions_-sz) ; ++i )
					bestDecisionVariables_[i+sz] = currentPopulation_.get( indices[i] ).getDecisionVariables();
			} // else
		} // if
		else {
			// The archive is 0 sized, then we must to use the currentPopulation
			sz = currentPopulation_.size();
			int[] indices = RandomVector.getRandomVector_Int( numberOfSolutions_, sz );
			for( int i=0 ; i<numberOfSolutions_ ; ++i )
				bestDecisionVariables_[i] = currentPopulation_.get( indices[i] ).getDecisionVariables();
		} // else
	} // prepareBestSolutions


	


	public void setBestSolutions( DecisionVariables[] bestSolution , int islandId ){

		currentPopulation_.setRemainBest( bestSolution , islandId );
		//archive_.setRemainBest(           bestSolution , islandId );
	} // setOneBestSolution


	public void setUpBestSolutions(DecisionVariables[] newSolution, int islandId) {

		currentPopulation_.setRemainBest( newSolution , islandId );
		//archive_.setRemainBest(           newSolution , islandId );		
	} // setUpBestSolution

	
	public void postExecution() {

		solutionSetToReturn_ = new SolutionSet( 0 , 1 , 0 , archive_.size() );
		Iterator<Solution> it = archive_.iterator();
		int loadingPosition = archive_.getLoadingPosition();
		while( it.hasNext() )
			solutionSetToReturn_.add( new Solution( it.next() , loadingPosition ) );
	} // postExecution



	public boolean stopCondition(){

		return( evaluations_ >= maxEvaluations_/islands_ );
	} // stopCondition
	
	
	@Override
	public Algorithm clone() {
		aMOCell4 algorithmToReturn = new aMOCell4( problem_ );
		
		// First assigns Algorithm attributes
		algorithmToReturn.operators_            = operators_           ;
		algorithmToReturn.inputParameters_      = inputParameters_     ;  
		algorithmToReturn.outPutParameters_     = outPutParameters_    ;
		
		// Second assign aMOCell4 params
		algorithmToReturn.populationSize_       = populationSize_      ;
		algorithmToReturn.archiveSize_          = archiveSize_         ;
		algorithmToReturn.maxEvaluations_       = maxEvaluations_      ;
		algorithmToReturn.evaluations_          = evaluations_         ;
		algorithmToReturn.islands_              = islands_             ;
		algorithmToReturn.mutationOperator_     = mutationOperator_    ;
		algorithmToReturn.crossoverOperator_    = crossoverOperator_   ;
		algorithmToReturn.selectionOperator_    = selectionOperator_   ;
		algorithmToReturn.currentPopulation_    = currentPopulation_   ;
		algorithmToReturn.archive_              = archive_             ;
		algorithmToReturn.neighbors_            = neighbors_           ;
		algorithmToReturn.neighborhood_         = neighborhood_        ;
		algorithmToReturn.dominance_            = dominance_           ;
		algorithmToReturn.crowdingComparator_   = crowdingComparator_  ;
		algorithmToReturn.distance_             = distance_            ;
		algorithmToReturn.numberOfSolutions_    = numberOfSolutions_   ;

		return algorithmToReturn;
	} // clone



	public SolutionSet getFinalSolutionSet() {

		return solutionSetToReturn_;
	} // getFinalSolutionSet


	@Override
	public int getPopulationSize() {

		return populationSize_;
	} // getPopulationSize

} // aMOCell4
