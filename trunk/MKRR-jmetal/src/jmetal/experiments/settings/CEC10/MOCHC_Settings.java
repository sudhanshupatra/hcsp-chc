/**
 * MOCell_Settings.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * MOCell_Settings class of algorithm MOCell
 */
package jmetal.experiments.settings.CEC10;

import jmetal.metaheuristics.mocell.*;
import jmetal.metaheuristics.mochc.MOCHC;
import jmetal.metaheuristics.mochc.MOCHCSched;

import jmetal.metaheuristics.nsgaII.*;
import jmetal.metaheuristics.cellde.*;
import java.util.Properties;
import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.base.operator.localSearch.LocalSearchFactory;
import jmetal.experiments.Settings;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 * 
 * @author Bernab Dorronsoro
 */
public class MOCHC_Settings extends Settings {
	// Default settings
	int archiveSize_ = 100;
	int feedback_ = 20;

	// Local search operator
	private String LOCAL_SEARCH = "LMCTSLocalSearch";
	// Algorithm in order to setup the initial population
	private String P_SPECIAL_SOLUTION = "Min-min";

	private double _initialConvergenceCount = 0.25;
	private double _preservedPopulation = 0.05;
	private int _convergenceValue = 3;
	private int _populationSize = 100;
	private int _maxEvaluations = 600000;

	private String _crossoverOperator = "UniformCrossover";
	private double _crossoverProbability = 1.0;

	private String _parentsSelection = "RandomSelection";
	private String _offspringSelection = "RankingAndCrowdingSelection";

	// private String _mutationOperator = "BitFlipMutation";
	private String _mutationOperator = "RebalanceMutation";
	private double _mutationProbability = 0.60;
	// private String M_POLICY = "moderate";
	// private String M_POLICY = "simple";
	private String M_POLICY = "random";
	// private String M_MODE = "strict";
	private String M_MODE = "permissive";
	// Mutation: Percentage of machines overloaded and underloaded
	private double M_OVERLOAD_PER = 0.25;

	// Mutation rounds
	// private int M_ROUNDS = 16;
	private int M_ROUNDS = 25;

	String paretoFrontFile_ = "";

	/**
	 * Constructor
	 */
	public MOCHC_Settings(Problem problem) {
		super(problem);
	} // MOCell_Settings

	/**
	 * Configure the MOCell algorithm with default parameter settings
	 * 
	 * @return an algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm;
		Operator parentSelection = null; // Selection operator
		Operator offspringSelection = null; // Selection operator
		Operator crossover = null; // Crossover operator
		Operator mutation = null; // Mutation operator
		Operator localsearch = null; // LocalSearch operator

		// Creating the problem.
		algorithm = new MOCHCSched(problem_);

		// Algorithm parameters
		algorithm.setInputParameter("populationSize", _populationSize);
		algorithm.setInputParameter("maxEvaluations", _maxEvaluations);
		algorithm.setInputParameter("archiveSize", archiveSize_);
		algorithm.setInputParameter("feedBack", feedback_);
		algorithm.setInputParameter("initialConvergenceCount",
				_initialConvergenceCount);
		algorithm
				.setInputParameter("preservedPopulation", _preservedPopulation);
		algorithm.setInputParameter("convergenceValue", _convergenceValue);

		algorithm.setInputParameter("specialSolution", P_SPECIAL_SOLUTION);

		// Mutation and Crossover for Real codification
		crossover = CrossoverFactory.getCrossoverOperator(_crossoverOperator);
		crossover.setParameter("probability", _crossoverProbability);

		mutation = MutationFactory.getMutationOperator(_mutationOperator);
		mutation.setParameter("probability", _mutationProbability);
		mutation.setParameter("rounds", (Integer) M_ROUNDS);
		mutation.setParameter("Problem", problem_);
		mutation.setParameter("overloadPercentage", M_OVERLOAD_PER);
		mutation.setParameter("Policy", M_POLICY);
		mutation.setParameter("Mode", M_MODE);

		parentSelection = SelectionFactory
				.getSelectionOperator(_parentsSelection);
		offspringSelection = SelectionFactory
				.getSelectionOperator(_offspringSelection);
		offspringSelection.setParameter("problem", problem_);

		localsearch = LocalSearchFactory.getLocalSearchOperator(LOCAL_SEARCH);
		localsearch.setParameter("Problem", problem_);

		// Add the operators to the algorithm
		algorithm.addOperator("localsearch", localsearch);
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("cataclysmicMutation", mutation);
		algorithm.addOperator("parentSelection", parentSelection);
		algorithm.addOperator("newGenerationSelection", offspringSelection);

		return algorithm;
	}

	@Override
	public Algorithm configure(Properties settings) throws JMException {
		throw new JMException("No implementado!");
	}
} // MOCell_Settings
