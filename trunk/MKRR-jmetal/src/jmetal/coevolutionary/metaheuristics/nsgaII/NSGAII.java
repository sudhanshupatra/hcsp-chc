/**
 * Includes the NSGAII metaheuristic, coevolutionary version
 */
package jmetal.coevolutionary.metaheuristics.nsgaII;


import java.util.Iterator;

import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.localSearch.LocalSearch;
import jmetal.coevolutionary.util.RandomVector;
import jmetal.coevolutionary.util.Ranking;
import jmetal.coevolutionary.qualityIndicator.QualityIndicator;
import jmetal.coevolutionary.util.Distance;
import jmetal.util.JMException;


/**
 * This class implements the NSGA-II algorithm.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero (Islands friendly, local search added (if available) and new features added)
 * @version 1.1
 */
public class NSGAII extends Algorithm {
	private static final long serialVersionUID = -5959762302785478111L;

	private Problem  problem_                ; ///< stores the problem  to solve
	private int      populationSize_         ; ///< Size of the population
	private int      maxEvaluations_         ; ///< maximum number of evaluation
	private int      evaluations_            ;
	private int      islands_ = 1            ; ///< Number of islands (GAs) to use by default
	private int      numberOfSolutions_      ; ///< Number of solutions transferred to another islands
	private int      bestSolutionsFirstLevel_; ///< Number of solutions choosen from the first subfront

	private QualityIndicator indicators_          ; ///< QualityIndicator object
	private int              requiredEvaluations_ ; ///< Use in the example of use of the indicators object (see below)

	private SolutionSet population_          ; ///< Population
	private SolutionSet offspringPopulation_ ; ///< Offspring
	private SolutionSet union_               ; ///< Temporary <code>SolutionSet</code>

	private Operator mutationOperator_       ; ///< mutation operator
	private Operator crossoverOperator_      ; ///< crossover operator
	private Operator selectionOperator_      ; ///< selection operator
	private LocalSearch localSearchOperator_ ; ///< local search operator

	private Distance distance_;

	private Ranking ranking_;
	private String  specialSolution_ = null;
	
	DecisionVariables[] bestDecisionVariables_; ///< Stores the best solutions, ready to be sent to other islands

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
	 * @param problem Problem to solve
	 */
	public NSGAII( Problem problem ){

		this.problem_ = problem.clone();                     
	} // NSGAII
	
	
	public void setup( int islandId ) throws JMException  {
		distance_    = new Distance()  ;               

		// Read the parameters 
		indicators_ = (QualityIndicator) this.getInputParameter( "indicators" ) ;
		
		islands_                 = ((Integer) getInputParameter( "numberOfIslands"         )).intValue();
		populationSize_          = ((Integer) getInputParameter( "populationSize"          )).intValue();
		maxEvaluations_          = ((Integer) getInputParameter( "maxEvaluations"          )).intValue();
		numberOfSolutions_       = ((Integer) getInputParameter( "numberOfSolutions"       )).intValue();
		bestSolutionsFirstLevel_ = ((Integer) getInputParameter( "bestSolutionsFirstLevel" )).intValue();
		specialSolution_         = ((String)  getInputParameter( "specialSolution"         ));
		mergeSolution_     = ((Integer) this.getInputParameter( "mergeSolution")).intValue();

		//Initialize the variables
		population_  = new SolutionSet( islandId , islands_ , numberOfSolutions_ , populationSize_  , mergeSolution_);        
		evaluations_ = 0 ;         
		//bestSolutions = new Solution[islands-1];

		requiredEvaluations_ = 0 ;

		//Read the operators
		mutationOperator_    = operators_.get("mutation");
		crossoverOperator_   = operators_.get("crossover");
		selectionOperator_   = operators_.get("selection");
		localSearchOperator_ = (LocalSearch) operators_.get("localsearch");

		// NOTEIT Creation of the SolutionSet of the Islands
		// Create the initial solutionSet
		Solution newSolution;

		for( int i=0 ; i<populationSize_ ; ++i) {
			newSolution = new Solution( problem_ );
			population_.add( newSolution );
		} // for

		prepareSetupBestSolutions();
	} // setup
	
	
	/**
	 * This method is used to evaluate the first population,that it is called after
	 * the calling to <code>setup</code> method
	 * 
	 * @throws JMException
	 */
	public void evaluatePopulation() throws JMException{
		// NOTEIT First evaluation of the SolutionSet
		
		int loadingPosition = population_.getLoadingPosition();

		if ( ( specialSolution_ != null ) ){
			
			DecisionVariables[] best = new DecisionVariables[problem_.getNumberOfIslands()-1];

			population_.clear();
			for( int f=0 ; f<populationSize_ ; ++f) {
				DecisionVariables specialDV   = problem_.generateSpecial( specialSolution_ );
				if ( f < numberOfSolutions_ ){
					for( int i=0 ; i<problem_.getNumberOfIslands() ; ++i ){
						if (i<loadingPosition)
							best[i] = new DecisionVariables( specialDV.extractSlice(i) );
						if (i>loadingPosition)
							best[i-1] = new DecisionVariables( specialDV.extractSlice(i) );
					} // for
					
					population_.setBestRow( best , f );
				} // if

				DecisionVariables decisionVar = new DecisionVariables( specialDV.extractSlice( loadingPosition ) );
				Solution S = new Solution( problem_ , decisionVar );
				population_.add( S );
				S.unLink();
			} // for

		} // if

		Iterator<Solution> it = population_.iterator();
		while( it.hasNext() ){
			Solution tmp = it.next();
			tmp.unLink();
			population_.linkExternalDecisionVariables( tmp );
			problem_.evaluate( tmp , loadingPosition );
			problem_.evaluateConstraints( tmp );
			++evaluations_;
		} // while
		
		// population_.printObjectivesToFile( "FUN.S."+loadingPosition );
	} // evaluatePopulation
	
	
	public void generation() throws JMException {

//		if ( ( (evaluations_%4000) == 0 ) && (population_.getLoadingPosition()==0) ){
//			String file = "FUN." + ((int) evaluations_/4000 );
//			population_.printObjectivesToFile( file );
//		} // if

		// Create the offSpring solutionSet		
		int loadingPosition = population_.getLoadingPosition();
		
		
//		System.out.println("Isla: " + loadingPosition + "Numero de islas: " + islands_ + "Numero de variables: " + problem_.getNumberOfVariables());

		offspringPopulation_  = new SolutionSet( loadingPosition , islands_ , numberOfSolutions_ , populationSize_  , mergeSolution_);
		// Link the external dv's
		offspringPopulation_.setBestExternResults( population_ );
		Solution[] parents = new Solution[2];
		for (int i = 0; i < (populationSize_/2); i++){   
			//obtain parents
			Object obj = selectionOperator_.execute( population_ , loadingPosition );

			if ( obj.getClass().getCanonicalName().toString().equalsIgnoreCase( Solution.class.getCanonicalName().toString() ) ) {
				parents[0] = (Solution) obj;
				parents[1] = (Solution) selectionOperator_.execute( population_ , loadingPosition );
			} // if
			else {
				parents = (Solution[]) obj;
			} // else
			
			if ( evaluations_ < maxEvaluations_ ) {                                
				Solution [] offSpring = (Solution []) crossoverOperator_.execute( parents , loadingPosition );

				offSpring[0].unLink();
				offSpring[1].unLink();

				mutationOperator_.execute( offSpring[0] , loadingPosition );
				mutationOperator_.execute( offSpring[1] , loadingPosition );

				population_.linkExternalDecisionVariables( offSpring[0] );
				population_.linkExternalDecisionVariables( offSpring[1] );
				if ( localSearchOperator_ != null ) {
					localSearchOperator_.execute( offSpring[0] , loadingPosition );
					evaluations_ += localSearchOperator_.getEvaluations();
					localSearchOperator_.execute( offSpring[1] , loadingPosition);
					evaluations_ += localSearchOperator_.getEvaluations();
				} // if
				
				problem_.evaluate( offSpring[0] , loadingPosition );
				problem_.evaluateConstraints( offSpring[0] );
				problem_.evaluate( offSpring[1] , loadingPosition);
				problem_.evaluateConstraints( offSpring[1] );
				
				offspringPopulation_.add( offSpring[0] );
				offspringPopulation_.add( offSpring[1] );              
				evaluations_ += 2;
			} // if
			else {
				offspringPopulation_.add( new Solution( parents[0] ) );
				offspringPopulation_.add( new Solution( parents[1] ) );                
			} // else                            
		} // for

	} // generation

	
	public void postGeneration() {
		// Create the solutionSet union of solutionSet and offSpring
		union_ = ((SolutionSet)population_).union(offspringPopulation_);

		// Ranking the union
		Ranking ranking = new Ranking(union_);

		if (ranking.getNumberOfSubfronts() == 0)
			System.out.println("No hay subfrentes!!");
			
		int remain = populationSize_;
		int index  = 0;
		SolutionSet front = null;
		population_.clear();

		// Obtain the next front
		front = ranking.getSubfront(index);

		while ((remain > 0) && (remain >= front.size())){                
			// Assign crowding distance to individuals
			distance_.crowdingDistanceAssignment( front , problem_.getNumberOfObjectives() );                
			// Add the individuals of this front
			for (int k = 0; k < front.size(); k++ ) {
				population_.add( front.get(k) );
			} // for

			// Decrement remain
			remain = remain - front.size();

			// Obtain the next front
			index++;
			if (remain > 0) {
				front = ranking.getSubfront(index);
			} // if
		} // while

		// Remain is less than front(index).size, insert only the best one
		if (remain > 0) {  // front contains individuals to insert                        
			distance_.crowdingDistanceAssignment( front , problem_.getNumberOfObjectives() );
			front.sort(new jmetal.coevolutionary.base.operator.comparator.CrowdingComparator());
			for (int k = 0; k < remain; k++) {
				population_.add( front.get(k) );
			} // for

			remain = 0; 
		} // if              

		// This piece of code shows how to use the indicator object into the code
		// of NSGA-II. In particular, it finds the number of evaluations required
		// by the algorithm to obtain a Pareto front with a hypervolume higher
		// than the hypervolume of the true Pareto front.
		if ( (indicators_ != null) && 
				(requiredEvaluations_ == 0) ) {
			double HV = indicators_.getHypervolume( population_ ) ;
			if ( HV >= (0.98 * indicators_.getTrueParetoFrontHypervolume()) ) {
				requiredEvaluations_ = evaluations_ ;
			} // if
		} // if

		prepareBestSolutions();
	} // postGeneration


	public DecisionVariables[] getBestSolutions() {

		return bestDecisionVariables_;
	} // getBestSolution
// REMARK Cuando postGeneration llama a prepare, podria aprovechar el ranking hecho anteriormente Àno?
	
	private void prepareBestSolutions(){
		bestDecisionVariables_ = new DecisionVariables[numberOfSolutions_];
		Ranking ranking = new Ranking( population_ );
		int remain;
		
		SolutionSet subfront = ranking.getSubfront(0);
		int sz = subfront.size();
		int rest = population_.size() - sz;
		int i;
		if ( ( sz>=bestSolutionsFirstLevel_ ) && ( ranking.getNumberOfSubfronts()>1 ) && (rest>=(numberOfSolutions_-bestSolutionsFirstLevel_)) ) {
			// Subfront is enought big and the remain is assured
			int[] indices = RandomVector.getRandomVector_Int( bestSolutionsFirstLevel_ , sz );
			for( i=0 ; i<bestSolutionsFirstLevel_ ; ++i )
				bestDecisionVariables_[i] = subfront.get( indices[i] ).getDecisionVariables();
			remain = numberOfSolutions_-bestSolutionsFirstLevel_;
		} // if
		else if ( rest<(numberOfSolutions_-bestSolutionsFirstLevel_) ) {
			// The rest of subfronts are so little
			int[] indices = RandomVector.getRandomVector_Int( numberOfSolutions_ , sz );
			for( i=0 ; i<numberOfSolutions_ ; ++i )
				bestDecisionVariables_[i] = subfront.get( indices[i] ).getDecisionVariables();
			remain = 0;
		} // else if
		else if ( numberOfSolutions_ == 1 ){
			int[] indices = RandomVector.getRandomVector_Int( bestSolutionsFirstLevel_ , sz );
			bestDecisionVariables_[0] = subfront.get( indices[0] ).getDecisionVariables();
			remain = 0;
			i = 1;
		} // else if
		else {
			int[] indices = RandomVector.getRandomVector_Int( sz , sz );
			for( i=0 ; i<sz ; ++i )
				bestDecisionVariables_[i] = subfront.get( indices[i] ).getDecisionVariables();
			remain = numberOfSolutions_-sz;
		} // else
		
		int index = 1;
		while( ( index<ranking.getNumberOfSubfronts() ) && ( remain>0 ) ){
			subfront = ranking.getSubfront( index );
			sz = subfront.size();
			if ( sz>=remain ){
				int[] indices = RandomVector.getRandomVector_Int( remain , sz );
				for( int j=0 ; j<remain ; ++j , ++i )
					bestDecisionVariables_[i] = subfront.get( indices[j] ).getDecisionVariables();
				remain=0;
			} // if
			else {
				int[] indices = RandomVector.getRandomVector_Int( sz , sz );
				for( int j=0 ; j<sz ; ++j , ++i , --remain )
					bestDecisionVariables_[i] = subfront.get( indices[j] ).getDecisionVariables();
			} // else
			++index;
		} // while
		
	} // prepareBestSolutions
	
	
	private void prepareSetupBestSolutions(){

		bestDecisionVariables_ = new DecisionVariables[numberOfSolutions_];
		// Assign the best DVs of another islands
		int[] indices = RandomVector.getRandomVector_Int( numberOfSolutions_ , population_.size() );
		for( int i=0 ; i<numberOfSolutions_ ; ++i )
				bestDecisionVariables_[i] = population_.get(indices[i]).getDecisionVariables();
	} // prepareBestSolutions


	public void setBestSolutions( DecisionVariables[] bestSolution , int islandId ){

		population_.setRemainBest( bestSolution , islandId );
	} // setBestSolutions

	
	public void setUpBestSolutions( DecisionVariables[] bestSolution , int islandId ){

		population_.setRemainBest( bestSolution , islandId );
	} // setBestSolutions


	public void postExecution(){

		setOutputParameter("evaluations", requiredEvaluations_);
		// solutionSetToReturn_ = population_;
		
		solutionSetToReturn_ = new SolutionSet( 0 , 1 , 0 , population_.size() );
		Iterator<Solution> it = population_.iterator();
		int loadingPosition = population_.getLoadingPosition();
		while( it.hasNext() )
			solutionSetToReturn_.add( new Solution( it.next() , loadingPosition ) );
	} // postExecution
	
	
	/**
	 * @return The stop condition
	 */
	public boolean stopCondition(){

		return( evaluations_ >= maxEvaluations_/islands_ );
	} // stopCondition
	
	


	@Override
	public Algorithm clone() {
		NSGAII algorithmToReturn = new NSGAII( problem_ );

		// First assigns Algorithm attributes
		algorithmToReturn.operators_           = operators_          ;
		algorithmToReturn.inputParameters_     = inputParameters_    ;  
		algorithmToReturn.outPutParameters_    = outPutParameters_   ;
		
		// Second assign NSGAII params
		algorithmToReturn.populationSize_          = populationSize_          ;
		algorithmToReturn.maxEvaluations_          = maxEvaluations_          ;
		algorithmToReturn.evaluations_             = evaluations_             ;
		algorithmToReturn.islands_                 = islands_                 ;
		algorithmToReturn.indicators_              = indicators_              ;
		algorithmToReturn.requiredEvaluations_     = requiredEvaluations_     ; 
		algorithmToReturn.population_              = population_              ;
		algorithmToReturn.offspringPopulation_     = offspringPopulation_     ;
		algorithmToReturn.union_                   = union_                   ;
		algorithmToReturn.mutationOperator_        = mutationOperator_        ;
		algorithmToReturn.crossoverOperator_       = crossoverOperator_       ;
		algorithmToReturn.selectionOperator_       = selectionOperator_       ;
		algorithmToReturn.localSearchOperator_     = localSearchOperator_     ;
		algorithmToReturn.distance_                = distance_                ;
		algorithmToReturn.ranking_                 = ranking_                 ;
		algorithmToReturn.bestSolutionsFirstLevel_ = bestSolutionsFirstLevel_ ;
		algorithmToReturn.numberOfSolutions_       = numberOfSolutions_       ;

		return algorithmToReturn;
	} // clone


	public SolutionSet getFinalSolutionSet() {
		
		return solutionSetToReturn_;
	} // getFinalSolutionSet
	
	
	@Override
	public int getPopulationSize() {

		return populationSize_;
	} // getPopulationSize

} // NSGAII
