/**
 * MOCell_Settings.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * MOCell_Settings class of algorithm MOCell
 */
package jmetal.coevolutionary.experiments.settings.COR;

import jmetal.coevolutionary.metaheuristics.mocell.*;
import jmetal.coevolutionary.metaheuristics.spea2.*;
import jmetal.coevolutionary.metaheuristics.nsgaII.*;
import java.util.Properties;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.operator.crossover.CrossoverFactory;
import jmetal.coevolutionary.base.operator.mutation.MutationFactory;
import jmetal.coevolutionary.base.operator.selection.SelectionFactory;
import jmetal.coevolutionary.base.operator.localSearch.LocalSearchFactory;
import jmetal.coevolutionary.experiments.Settings;
import jmetal.coevolutionary.problems.ProblemFactory;
import jmetal.coevolutionary.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 *
 * @author Bernabé Dorronsoro
 */
public class SPEA2_Settings_COR extends Settings{
  
  // Default settings
//  int populationSize_ = 100    ;
  int archiveSize_    = 100    ;
//  int feedback_       = 20     ;
 
  //double mutationProbability_  = 1.0/problem_.getNumberOfVariables() ;
//  double crossoverProbability_ = 0.9 ;
  
  private double m_probability_               ;
	int    numberOfIslands_                     ; ///< Number of islands

  double  distributionIndexForMutation_ = 20    ;
  double  distributionIndexForCrossover_ = 20    ;

  	private String SELECTION             = "BinaryTournament"  ; ///< Selection operator
	private String CROSSOVER             = "DPX"  ; ///< Crossover operator
	private double c_probability_		 = 0.9                 ; ///< Crossover probability
//	private String LOCAL_SEARCH          = "LMCTSLocalSearch"  ; ///< Local search operator
	private String LOCAL_SEARCH          = null  ; ///< Local search operator
	private String MUTATION              = "RebalanceMutation" ; ///< Mutation operator
//	private double mutationProbability_  = 0.2                 ; ///< Mutation probability
	private String M_POLICY              = "simple"          ; ///< Mutation policy
	private String M_MODE                = "permissive"            ; ///< Mutation policy mode
	private double M_OVERLOAD_PER        = 0.25                ; ///< Mutation: Percentage of machines overloaded and underloaded
	private int    M_ROUNDS              = 1                   ; ///< Mutation rounds
	private int    maxEvaluations_       = 500000              ; ///< Number of evaluations
	private              int    populationSizeBD_  = 100                 ; ///< Size of the population by default
	private static final int    P_BEST_SOLUTIONS   = 20                   ; ///< Population: Number of best solutions by default
	private static final double P_PER_1st_SUBFRONT = 0.6                 ; ///< Population: Percentage of best solutions taken from the first ranking subfront
//	private static String P_SPECIAL_SOLUTION = "Min-min"           ; ///< Algorithm in order to setup the initial population
	private static String P_SPECIAL_SOLUTION = null           ; ///< Algorithm in order to setup the initial population
	private static final int    P_LINK_MODE        = 1                   ; // 0: partial solutions are constructed with the solutions of the other island with the same index; this is the one used by Alberto and in the book chapter
																		   // 1: partial solutions are constructed with random solutions of the other islands


  
  String paretoFrontFile_ = "" ;
  
  /**
   * Constructor
   */
  public SPEA2_Settings_COR(Problem problem) {
    super(problem) ;
    m_probability_     = 1.0/problem.getNumberOfVariables();
	numberOfIslands_ = problem.getNumberOfIslands();
	populationSize_  = (populationSize_==0)? populationSizeBD_ : populationSize_;
  } // MOCell_Settings
  
  /**
   * Configure the MOCell algorithm with default parameter settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator  selection   = null ; // Selection operator
	Operator  crossover   = null ; // Crossover operator
	Operator  mutation    = null ; // Mutation operator
	Operator  localsearch = null ; // LocalSearch operator
    
    QualityIndicator indicators ;
    
//	int firstLevel = (int) Math.ceil( P_PER_1st_SUBFRONT * ((double) P_BEST_SOLUTIONS) );

    algorithm = new SPEA2Sched(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("archiveSize",archiveSize_ );
//    algorithm.setInputParameter("feedBack",feedback_);
    algorithm.setInputParameter( "numberOfIslands"  , numberOfIslands_   );
	algorithm.setInputParameter( "numberOfSolutions", P_BEST_SOLUTIONS );
	algorithm.setInputParameter( "specialSolution"         , P_SPECIAL_SOLUTION );
	algorithm.setInputParameter( "mergeSolution"         , P_LINK_MODE);
    
    // Mutation and Crossover for Real codification 
	crossover = CrossoverFactory.getCrossoverOperator( CROSSOVER );
    //crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");
    crossover.setParameter("probability", c_probability_);                   
    crossover.setParameter("distributionIndex",distributionIndexForCrossover_);

	mutation = MutationFactory.getMutationOperator( MUTATION );
    //mutation = MutationFactory.getMutationOperator("PolynomialMutation"); 
    mutation.setParameter("probability", m_probability_);
    mutation.setParameter( "rounds"             , (Integer) M_ROUNDS );
	mutation.setParameter( "Problem"            , problem_           );
	mutation.setParameter( "overloadPercentage" , M_OVERLOAD_PER     );
	mutation.setParameter( "Policy"             , M_POLICY           );
	mutation.setParameter( "Mode"               , M_MODE             );

	selection = SelectionFactory.getSelectionOperator( SELECTION );

	if (LOCAL_SEARCH != null) {
		localsearch = LocalSearchFactory.getLocalSearchOperator( LOCAL_SEARCH );
		localsearch.setParameter( "Problem" , problem_ );
	}
    
    // Add the operators to the algorithm
	algorithm.addOperator( "localsearch" , localsearch );
	algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
    
//   // Creating the indicator object
 // Creating the indicator object
	if (! paretoFrontFile_.equals("")) {
		indicators = new QualityIndicator(problem_, paretoFrontFile_);
		algorithm.setInputParameter("indicators", indicators) ;  
	} // if
    return algorithm ;
  }
  
  /**
   * Configure an algorithm with user-defined parameter settings
   * @param settings
   * @return An algorithm
   * @throws jmetal.util.JMException
   */
  public Algorithm configure(Properties settings) throws JMException {
    if (settings != null) {
    	populationSize_  = Integer.parseInt(settings.getProperty("POPULATION_SIZE", ""+populationSize_)) ;
        maxEvaluations_  = Integer.parseInt(settings.getProperty("MAX_EVALUATIONS", ""+maxEvaluations_)) ;
        archiveSize_     = Integer.parseInt(settings.getProperty("ARCHIVE_SIZE", ""+archiveSize_)) ;
//        feedback_        = Integer.parseInt(settings.getProperty("FEEDBACK", ""+feedback_)) ;

        P_SPECIAL_SOLUTION = settings.getProperty("SPECIAL_SOLUTION", P_SPECIAL_SOLUTION);
        
    	  CROSSOVER = settings.getProperty("RECOMBINATION", CROSSOVER);
        
        c_probability_ = Double.parseDouble(settings.getProperty("CROSSOVER_PROBABILITY", 
                                                      ""+c_probability_)) ;
    	  
        MUTATION = settings.getProperty("MUTATION", MUTATION);
        M_ROUNDS = Integer.parseInt(settings.getProperty("MUTATION_ROUNDS", ""+M_ROUNDS));
        M_OVERLOAD_PER = Double.parseDouble(settings.getProperty("MUTATION_OVERLOAD_PERCENTAGE", ""+M_OVERLOAD_PER));
        M_POLICY = settings.getProperty("MUTATION_POLICY", M_POLICY);
        M_MODE = settings.getProperty("MUTATION_MODE", M_MODE);
        
        
        m_probability_ = Double.parseDouble(settings.getProperty("MUTATION_PROBABILITY", 
                                                      ""+m_probability_)) ;
        distributionIndexForMutation_ = 
              Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_MUTATION", 
                                                      ""+distributionIndexForMutation_)) ;
        distributionIndexForCrossover_ = 
              Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_CROSSOVER", 
                                                      ""+distributionIndexForCrossover_)) ;
        
        SELECTION = settings.getProperty("SELECTION", SELECTION);
        
        LOCAL_SEARCH = settings.getProperty("LOCAL_SEARCH", LOCAL_SEARCH);
        
        paretoFrontFile_ = settings.getProperty("PARETO_FRONT_FILE", "") ;
        
//      populationSize_  = Integer.parseInt(settings.getProperty("POPULATION_SIZE", ""+populationSize_)) ;
//      maxEvaluations_  = Integer.parseInt(settings.getProperty("MAX_EVALUATIONS", ""+maxEvaluations_)) ;
//      c_probability_       = Double.parseDouble(settings.getProperty( "CROSSOVER_PROBABILITY" , ""+c_probability_ ));
//      m_probability_       = Double.parseDouble(settings.getProperty( "MUTATION_PROBABILITY"  , ""+m_probability_ )) ;
//      archiveSize_     = Integer.parseInt(settings.getProperty("ARCHIVE_SIZE", ""+archiveSize_)) ;
//      feedback_        = Integer.parseInt(settings.getProperty("FEEDBACK", ""+feedback_)) ;
//
////      P_SPECIAL_SOLUTION = settings.getProperty("SPECIAL_SOLUTION", P_SPECIAL_SOLUTION);
//      
////  	  CROSSOVER = settings.getProperty("RECOMBINATION", CROSSOVER);
//      
////      crossoverProbability_ = Double.parseDouble(settings.getProperty("CROSSOVER_PROBABILITY", 
////                                                    ""+crossoverProbability_)) ;
//  	  
////      MUTATION = settings.getProperty("MUTATION", MUTATION);
////      M_ROUNDS = Integer.parseInt(settings.getProperty("MUTATION_ROUNDS", ""+M_ROUNDS));
////      M_OVERLOAD_PER = Double.parseDouble(settings.getProperty("MUTATION_OVERLOAD_PERCENTAGE", ""+M_OVERLOAD_PER));
////      M_POLICY = settings.getProperty("MUTATION_POLICY", M_POLICY);
////      M_MODE = settings.getProperty("MUTATION_MODE", M_MODE);
//      
//      
////      mutationProbability_ = Double.parseDouble(settings.getProperty("MUTATION_PROBABILITY", 
////                                                    ""+mutationProbability_)) ;
//      distributionIndexForMutation_ = 
//            Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_MUTATION", 
//                                                    ""+distributionIndexForMutation_)) ;
//      distributionIndexForCrossover_ = 
//            Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_CROSSOVER", 
//                                                    ""+distributionIndexForCrossover_)) ;
//      
////      SELECTION = settings.getProperty("SELECTION", SELECTION);
//      
////      LOCAL_SEARCH = settings.getProperty("LOCAL_SEARCH", LOCAL_SEARCH);
//      
//      paretoFrontFile_ = settings.getProperty("PARETO_FRONT_FILE", "") ;
    }
    
    return configure() ;
  }
} // MOCell_Settings
