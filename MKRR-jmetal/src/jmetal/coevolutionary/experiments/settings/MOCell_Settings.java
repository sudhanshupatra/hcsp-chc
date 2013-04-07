package jmetal.coevolutionary.experiments.settings;


import java.util.Properties;

import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.operator.crossover.CrossoverFactory;
import jmetal.coevolutionary.base.operator.mutation.MutationFactory;
import jmetal.coevolutionary.base.operator.selection.SelectionFactory;
import jmetal.coevolutionary.experiments.Settings;
import jmetal.coevolutionary.metaheuristics.mocell.aMOCell4;
import jmetal.coevolutionary.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 * MOCell_Settings class of algorithm MOCell
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class MOCell_Settings extends Settings{

	// Default settings
	int populationSize_    = 100   ;
	int maxEvaluations_    = 100000 ;
	int archiveSize_       = 100   ;
	int feedback_          = 20    ;
	int numberOfIslands_           ; ///< Number of islands
	int numberOfSolutions_ = 20    ; ///< Number of best solutions by default

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
	public MOCell_Settings(Problem problem) {
		super(problem) ;
		mutationProbability_     = 1.0/problem.getNumberOfVariables();
		numberOfIslands_ = problem.getNumberOfIslands();

	} // MOCell_Settings


	/**
	 * Configure the MOCell algorithm with default parameter settings
	 * @return an algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm ;
		Operator  selection ;
		Operator  crossover ;
		Operator  mutation  ;

		QualityIndicator indicators ;

		// Creating the problem: there are six MOCell variants
		//algorithm = new sMOCell1( problem_ ) ;
		//algorithm = new sMOCell2( problem_ ) ;
		//algorithm = new aMOCell1( problem_ ) ;
		//algorithm = new aMOCell2( problem_ ) ;
		//algorithm = new aMOCell3( problem_ ) ;
		algorithm = new aMOCell4( problem_ ) ;

		// Algorithm parameters
		algorithm.setInputParameter( "populationSize"   , populationSize_    );
		algorithm.setInputParameter( "maxEvaluations"   , maxEvaluations_    );
		algorithm.setInputParameter( "archiveSize"      , archiveSize_       );
		algorithm.setInputParameter( "feedBack"         , feedback_          );
		algorithm.setInputParameter( "numberOfIslands"  , numberOfIslands_   );
		algorithm.setInputParameter( "numberOfSolutions", numberOfSolutions_ );
		algorithm.setInputParameter( "mergeSolution"         , P_LINK_MODE);

		// Mutation and Crossover for Real codification 
		crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");                   
		crossover.setParameter("probability", crossoverProbability_);                   
		crossover.setParameter("distributionIndex",distributionIndexForCrossover_);

		mutation = MutationFactory.getMutationOperator("PolynomialMutation");                    
		mutation.setParameter("probability", mutationProbability_);
		mutation.setParameter("distributionIndex",distributionIndexForMutation_);    

		// Selection Operator 
		selection = SelectionFactory.getSelectionOperator("BinaryTournament") ;   

		// Add the operators to the algorithm
		algorithm.addOperator("crossover",crossover);
		algorithm.addOperator("mutation",mutation);
		algorithm.addOperator("selection",selection);

		// Creating the indicator object
		if (! paretoFrontFile_.equals("")) {
			indicators = new QualityIndicator(problem_, paretoFrontFile_);
			algorithm.setInputParameter("indicators", indicators) ;  
		} // if
		return algorithm ;
	} // configure


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
			feedback_        = Integer.parseInt(settings.getProperty("FEEDBACK", ""+feedback_)) ;

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

} // MOCell_Settings
