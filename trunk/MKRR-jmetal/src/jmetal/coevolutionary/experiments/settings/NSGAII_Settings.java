/**
 * This package contains several configurations for the metaheuristics
 */
package jmetal.coevolutionary.experiments.settings;


import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.operator.crossover.CrossoverFactory;
import jmetal.coevolutionary.base.operator.mutation.MutationFactory;
import jmetal.coevolutionary.base.operator.selection.SelectionFactory;
import jmetal.coevolutionary.experiments.Settings;
import jmetal.coevolutionary.metaheuristics.nsgaII.NSGAII;
import jmetal.coevolutionary.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

import java.util.Properties;


/**
 * NSGAII_Settings class of algorithm NSGAII
 *
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class NSGAII_Settings extends Settings{

	// Default settings
	int    populationSize_            = 100   ; ///< Size of the population
	int    maxEvaluations_            = 100000 ; ///< Number of evaluations
	int    numberOfIslands_                   ; ///< Number of islands
	int    numberOfSolutions_         = 20    ; ///< Number of best solutions by default
	double percentageOfFirstSubFront_ = 0.6   ; ///< Percentage of best solutions taken from the first ranking subfront

	double mutationProbability_  ;//= 1.0/problem_.getNumberOfVariables() ;
	double crossoverProbability_ = 0.9 ;

	double  distributionIndexForMutation_  = 20    ;
	double  distributionIndexForCrossover_ = 20    ;

	String paretoFrontFile_ = "" ;

	private static final int    P_LINK_MODE        = 0                   ; // 0: partial solutions are constructed with the solutions of the other island with the same index; this is the one used by Alberto and in the book chapter
	   // 1: partial solutions are constructed with random solutions of the other islands


	/**
	 * Constructor
	 */
	public NSGAII_Settings( Problem problem ) {

		super( problem ) ;
		mutationProbability_     = 1.0/problem.getNumberOfVariables();
		numberOfIslands_ = problem.getNumberOfIslands();
		
	} // NSGAII_Settings


	/**
	 * Configure NSGAII with user-defined parameter settings
	 * @return A NSGAII algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm   ;
		Operator  selection = null ;  // Selection operator
		Operator  crossover = null ;  // Crossover operator
		Operator  mutation  = null ;  // Mutation operator
//		Operator  localsearch ;       // Local Search operator for integer solutionType
		QualityIndicator indicators ;

		int firstLevel = (int) Math.ceil( percentageOfFirstSubFront_ * ((double) numberOfSolutions_) );
		// Creating the problem
		algorithm = new NSGAII( problem_ ) ;

		// Algorithm parameters
		algorithm.setInputParameter( "populationSize"          , populationSize_    );
		algorithm.setInputParameter( "maxEvaluations"          , maxEvaluations_    );
		algorithm.setInputParameter( "numberOfIslands"         , numberOfIslands_   );
		algorithm.setInputParameter( "numberOfSolutions"       , numberOfSolutions_ );
		algorithm.setInputParameter( "bestSolutionsFirstLevel" , firstLevel         );
		algorithm.setInputParameter( "mergeSolution"         , P_LINK_MODE);

//		if ( problem_.getSolutionType().toString().equalsIgnoreCase("real") ) {
			// Operators for Real codification 
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");                   
			crossover.setParameter("probability", crossoverProbability_);                   
			crossover.setParameter("distributionIndex",distributionIndexForCrossover_);

			mutation = MutationFactory.getMutationOperator("PolynomialMutation");                    
			mutation.setParameter("probability", mutationProbability_);
			mutation.setParameter("distributionIndex",distributionIndexForMutation_);
			
			selection = SelectionFactory.getSelectionOperator("BinaryTournament2") ;   
//		} // if
//		if ( problem_.getSolutionType().toString().equalsIgnoreCase("int") ) {
//			// Operators for Int codification
//			crossover = CrossoverFactory.getCrossoverOperator( "SinglePointCrossover" );
//			crossover.setParameter( "probability" , 1.0 );                   
//
//			mutation = MutationFactory.getMutationOperator( "RebalanceMutation" );
//			mutation.setParameter( "probability"         , 0.1      );
//			mutation.setParameter( "Problem"             , problem_ );
//			mutation.setParameter( "overloadPercentage"  , 0.25     );
//			
//			selection = SelectionFactory.getSelectionOperator("BinaryTournament");
//			
//			localsearch = LocalSearchFactory.getLocalSearchOperator("LMCTSLocalSearch");
//			localsearch.setParameter( "Problem" , problem_ );
//
//			// Add the operator to the algorithm
//			algorithm.addOperator( "localsearch" , localsearch );
//		} // if
		// Add the common operators to the algorithm
		algorithm.addOperator( "crossover" , crossover );
		algorithm.addOperator( "mutation"  , mutation  );
		algorithm.addOperator( "selection" , selection );

		// Creating the indicator object
		if (! paretoFrontFile_.equals("")) {
			indicators = new QualityIndicator(problem_, paretoFrontFile_);
			algorithm.setInputParameter("indicators", indicators) ;  
		} // if
		return algorithm ;
	} // configure


	/**
	 * Configure NSGAII with user-defined parameter settings
	 * @param settingst
	 * @return A NSGAII algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure( Properties settings ) throws JMException {

		if (settings != null) {
			populationSize_  = Integer.parseInt(settings.getProperty("POPULATION_SIZE", ""+populationSize_)) ;
			maxEvaluations_  = Integer.parseInt(settings.getProperty("MAX_EVALUATIONS", ""+maxEvaluations_)) ;
			crossoverProbability_ = Double.parseDouble(settings.getProperty("CROSSOVER_PROBABILITY", 
					""+crossoverProbability_)) ;     
			mutationProbability_ = Double.parseDouble(settings.getProperty("MUTATION_PROBABILITY", 
					""+mutationProbability_)) ;
			distributionIndexForMutation_ = 
				Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_MUTATION", 
						""+distributionIndexForMutation_)) ;
			distributionIndexForCrossover_ = 
				Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_CROSSOVER", 
						""+distributionIndexForCrossover_)) ;
			paretoFrontFile_ = settings.getProperty("PARETO_FRONT_FILE", "") ;
		} // if

		return configure() ;
	} // configure

} // NSGAII_Settings
