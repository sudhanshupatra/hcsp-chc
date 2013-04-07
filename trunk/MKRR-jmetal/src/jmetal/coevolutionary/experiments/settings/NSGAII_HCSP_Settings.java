/**
 * This package contains several configurations for the metaheuristics
 */
package jmetal.coevolutionary.experiments.settings;


import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.operator.crossover.CrossoverFactory;
import jmetal.coevolutionary.base.operator.localSearch.LocalSearchFactory;
import jmetal.coevolutionary.base.operator.mutation.MutationFactory;
import jmetal.coevolutionary.base.operator.selection.SelectionFactory;
import jmetal.coevolutionary.experiments.Settings;
import jmetal.coevolutionary.metaheuristics.nsgaII.NSGAII;
import jmetal.coevolutionary.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

import java.util.Properties;


/**
 * NSGAII_HCSP_Settings class of algorithm NSGAII
 *
 * @author Juan A. Ca–ero (Adaptated from NSGAII_Settings)
 * @version 1.0
 */
public class NSGAII_HCSP_Settings extends Settings{

	// NOTEIT Default settings for HCSP NSGA-II
	//private static final String SELECTION          = "TournamentFour"    ; ///< Selection operator
	private static final String SELECTION          = "BinaryTournament2"    ; ///< Selection operator
//	private static final String CROSSOVER          = "UniformCrossover"  ; ///< Crossover operator
	private static final String CROSSOVER          = "DPX"  ; ///< Crossover operator
	private              double c_probability_     = 0.9                 ; ///< Crossover probability
	private static final String LOCAL_SEARCH       = "LMCTSLocalSearch"  ; ///< Local search operator
	private static final String MUTATION           = "RebalanceMutation" ; ///< Mutation operator
//	private              double m_probability_     = 0.2                 ; ///< Mutation probability
//	private static final String M_POLICY           = "moderate"          ; ///< Mutation policy
	private static final String M_POLICY           = "simple"          ; ///< Mutation policy
//	private static final String M_MODE             = "strict"            ; ///< Mutation policy mode
	private static final String M_MODE             = "permissive"            ; ///< Mutation policy mode
	private static final double M_OVERLOAD_PER     = 0.25                ; ///< Mutation: Percentage of machines overloaded and underloaded
//	private static final int    M_ROUNDS           = 16                  ; ///< Mutation rounds
	private static final int    M_ROUNDS           = 1                  ; ///< Mutation rounds
	private              int    maxEvaluations_    = 500000              ; ///< Global maximum number of evaluations (evaluations per island = maxEvaluations_/islands)
	private              int    populationSizeBD_  = 100                 ; ///< Size of the population by default
//	private static final int    P_BEST_SOLUTIONS   = 8                   ; ///< Population: Number of best solutions by default
	private static final int    P_BEST_SOLUTIONS   = 40                   ; ///< Population: Number of best solutions by default
	private static final double P_PER_1st_SUBFRONT = 0.6                 ; ///< Population: Percentage of best solutions taken from the first ranking subfront
	// P_SPECIAL_SOLUTION IS NOT USED IN THIS PROJECT, NOR FOR THE CC NEITHER FOR THE SOTA ALGORITHMS
	private static final String P_SPECIAL_SOLUTION = "Min-min"           ; ///< Algorithm in order to setup the initial population
	private static final int    P_LINK_MODE        = 1                   ; // 0: partial solutions are constructed with the solutions of the other island with the same index; this is the one used by Alberto and in the book chapter
	   // 1: partial solutions are constructed with random solutions of the other islands

	private double m_probability_               ;
	int    numberOfIslands_                     ; ///< Number of islands
	double distributionIndexForMutation_  =  20 ;
	double distributionIndexForCrossover_ =  20 ;

	String paretoFrontFile_ = "" ;


	/**
	 * Constructor
	 */
	public NSGAII_HCSP_Settings( Problem problem ) {

		super( problem ) ;
		m_probability_     = 1.0/problem.getNumberOfVariables();
		numberOfIslands_ = problem.getNumberOfIslands();
		populationSize_  = (populationSize_==0)? populationSizeBD_ : populationSize_;
	} // NSGAII_HCSP_Settings


	/**
	 * Configure NSGAII with user-defined parameter settings
	 * @return A NSGAII algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm   ;
		Operator  selection   = null ; // Selection operator
		Operator  crossover   = null ; // Crossover operator
		Operator  mutation    = null ; // Mutation operator
		Operator  localsearch = null ; // LocalSearch operator
		QualityIndicator indicators ;

		int firstLevel = (int) Math.ceil( P_PER_1st_SUBFRONT * ((double) P_BEST_SOLUTIONS) );
		// Creating the problem
		algorithm = new NSGAII( problem_ ) ;

		// Algorithm parameters
		algorithm.setInputParameter( "populationSize"          , populationSize_    );
		algorithm.setInputParameter( "maxEvaluations"          , maxEvaluations_    );
		algorithm.setInputParameter( "numberOfIslands"         , numberOfIslands_   );
		algorithm.setInputParameter( "numberOfSolutions"       , P_BEST_SOLUTIONS   );
		algorithm.setInputParameter( "bestSolutionsFirstLevel" , firstLevel         );
		algorithm.setInputParameter( "specialSolution"         , P_SPECIAL_SOLUTION );
		algorithm.setInputParameter( "mergeSolution"         , P_LINK_MODE);

		// Operators for Int codification
		crossover = CrossoverFactory.getCrossoverOperator( CROSSOVER );
		crossover.setParameter( "probability" , c_probability_ );

		mutation = MutationFactory.getMutationOperator( MUTATION );
		mutation.setParameter( "probability"        , m_probability_      );
		mutation.setParameter( "rounds"             , (Integer) M_ROUNDS );
		mutation.setParameter( "Problem"            , problem_           );
		mutation.setParameter( "overloadPercentage" , M_OVERLOAD_PER     );
		mutation.setParameter( "Policy"             , M_POLICY           );
		mutation.setParameter( "Mode"               , M_MODE             );

		selection = SelectionFactory.getSelectionOperator( SELECTION );

		localsearch = LocalSearchFactory.getLocalSearchOperator( LOCAL_SEARCH );
		localsearch.setParameter( "Problem" , problem_ );

		// Add the operator to the algorithm
		algorithm.addOperator( "localsearch" , localsearch );
		algorithm.addOperator( "crossover"   , crossover   );
		algorithm.addOperator( "mutation"    , mutation    );
		algorithm.addOperator( "selection"   , selection   );

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
			populationSize_      = Integer.parseInt(settings.getProperty(   "POPULATION_SIZE"       , ""+populationSize_ ));
			maxEvaluations_      = Integer.parseInt(settings.getProperty(   "MAX_EVALUATIONS"       , ""+maxEvaluations_ ));
			c_probability_       = Double.parseDouble(settings.getProperty( "CROSSOVER_PROBABILITY" , ""+c_probability_ ));
			m_probability_       = Double.parseDouble(settings.getProperty( "MUTATION_PROBABILITY"  , ""+m_probability_ )) ;
			
			distributionIndexForMutation_  = Double.parseDouble( settings.getProperty( "DISTRIBUTION_INDEX_FOR_MUTATION", 
                                                                                       ""+distributionIndexForMutation_)) ;
			distributionIndexForCrossover_ = Double.parseDouble(settings.getProperty(  "DISTRIBUTION_INDEX_FOR_CROSSOVER", 
                                                                                       ""+distributionIndexForCrossover_)) ;
			paretoFrontFile_ = settings.getProperty( "PARETO_FRONT_FILE", "" );
		} // if

		return configure() ;
	} // configure


} // NSGAII_HCSP_Settings
