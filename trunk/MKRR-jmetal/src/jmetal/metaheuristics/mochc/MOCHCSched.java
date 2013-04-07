/**
 * MOCHC.java
 * @author Juan J. Durillo
 * @version 1.0
 */

package jmetal.metaheuristics.mochc;

import jmetal.base.*;
import jmetal.base.archive.*;
import jmetal.base.operator.comparator.CrowdingComparator;
import jmetal.base.variable.Binary;
import jmetal.base.variable.Int;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.*;

/**
 * 
 * Class implementing the CHC algorithm.
 */
public class MOCHCSched extends Algorithm {

	/**
	 * Stores the problem to solve
	 */
	private Problem problem_;

	/**
	 * Constructor Creates a new instance of MOCHC
	 */
	public MOCHCSched(Problem problem) {
		problem_ = problem;
	}

	/**
	 * Compares two solutionSets to determine if both are equals
	 * 
	 * @param solutionSet
	 *            A <code>SolutionSet</code>
	 * @param newSolutionSet
	 *            A <code>SolutionSet</code>
	 * @return true if both are cotains the same solutions, false in other case
	 */
	public boolean equals(SolutionSet solutionSet, SolutionSet newSolutionSet) {
		boolean found;
		for (int i = 0; i < solutionSet.size(); i++) {

			int j = 0;
			found = false;
			while (j < newSolutionSet.size()) {

				if (solutionSet.get(i).equals(newSolutionSet.get(j))) {
					found = true;
				}
				j++;
			}
			if (!found) {
				return false;
			}
		}
		return true;
	} // equals

	public int distance(Solution solutionOne, Solution solutionTwo) {
		int distance = 0;
		for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
			Int v1 = ((Int) solutionOne.getDecisionVariables().variables_[i]);
			Int v2 = ((Int) solutionTwo.getDecisionVariables().variables_[i]);

			if (v1.getValue() != v2.getValue()) {
				distance++;
			}
		}

		return distance;
	} // hammingDistance

	/**
	 * Runs of the MOCHC algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 */
	public SolutionSet execute() throws JMException {
		int iterations;
		int populationSize;
		int convergenceValue;
		int maxEvaluations;
		int minimumDistance;
		int evaluations;

		Comparator crowdingComparator = new CrowdingComparator();

		Operator crossover;
		Operator parentSelection;
		Operator newGenerationSelection;
		Operator cataclysmicMutation;

		double preservedPopulation;
		double initialConvergenceCount;
		boolean condition = false;
		SolutionSet solutionSet, offspringPopulation, newPopulation;

		// Read parameters
		initialConvergenceCount = ((Double) getInputParameter("initialConvergenceCount"))
				.doubleValue();
		preservedPopulation = ((Double) getInputParameter("preservedPopulation"))
				.doubleValue();
		convergenceValue = ((Integer) getInputParameter("convergenceValue"))
				.intValue();
		populationSize = ((Integer) getInputParameter("populationSize"))
				.intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations"))
				.intValue();

		// Read operators
		crossover = (Operator) getOperator("crossover");
		cataclysmicMutation = (Operator) getOperator("cataclysmicMutation");
		parentSelection = (Operator) getOperator("parentSelection");
		newGenerationSelection = (Operator) getOperator("newGenerationSelection");

		iterations = 0;
		evaluations = 0;

		// Calculate the maximum problem sizes
		Solution aux = new Solution(problem_);
		int size = problem_.getNumberOfVariables();
		minimumDistance = (int) Math.floor(initialConvergenceCount * size);

		String specialSolution = ((String) getInputParameter("specialSolution"));

		// To initialize one individual with min-min
		int minminInit = PseudoRandom.randInt(0, populationSize - 1);
		// To initialize one individual with Sufferage
		int suffInit = PseudoRandom.randInt(0, populationSize - 1);

		Calendar cal = Calendar.getInstance();
		Random rand = new Random(cal.getTimeInMillis());

		solutionSet = new SolutionSet(populationSize);
		for (int i = 0; i < populationSize; i++) {
			Solution solution = null;

			try {
				if (specialSolution == null) {
					solution = new Solution(problem_);
				} else if (specialSolution.contains("OneMinmin")) {
					if (minminInit == i) {
						DecisionVariables specialDV = problem_
								.generateSpecial(specialSolution);
						solution = new Solution(problem_, specialDV);
					} else {
						solution = new Solution(problem_);
					}
				} else if (specialSolution.equalsIgnoreCase("Min-Min")) {
					if (minminInit == i) {
						// specialSolution = "minMin";
						DecisionVariables specialDV = problem_
								.generateSpecial("minMin");
						solution = new Solution(problem_, specialDV);
					} else {
						if (suffInit == i) {
							DecisionVariables specialDV = problem_
									.generateSpecial("Sufferage");
							solution = new Solution(problem_, specialDV);
						} else {
							if (rand.nextFloat() < 0.5) {
								DecisionVariables specialDV = problem_
										.generateSpecial("Sufferage_rand");
								solution = new Solution(problem_, specialDV);
							} else {
								DecisionVariables specialDV = problem_
										.generateSpecial(specialSolution);
								solution = new Solution(problem_, specialDV);
							}
						}
					}
				}
			} catch (Exception ex) {
				System.out.println("Exception message:" + ex.getMessage());
				ex.printStackTrace();
				
				solution = new Solution(problem_);
			}

			problem_.evaluate(solution);
			problem_.evaluateConstraints(solution);
			evaluations++;
			solutionSet.add(solution);
		}

		long initTime = System.currentTimeMillis();

		while (!condition) {
			offspringPopulation = new SolutionSet(populationSize);
			for (int i = 0; i < solutionSet.size() / 2; i++) {
				Solution[] parents = (Solution[]) parentSelection
						.execute(solutionSet);

				// Equality condition between solutions
				if (distance(parents[0], parents[1]) >= (minimumDistance)) {
					Solution[] offspring = (Solution[]) crossover
							.execute(parents);
					problem_.evaluate(offspring[0]);
					problem_.evaluateConstraints(offspring[0]);
					problem_.evaluate(offspring[1]);
					problem_.evaluateConstraints(offspring[1]);
					evaluations += 2;
					offspringPopulation.add(offspring[0]);
					offspringPopulation.add(offspring[1]);
				}
			}
			SolutionSet union = solutionSet.union(offspringPopulation);
			newGenerationSelection.setParameter("populationSize",
					populationSize);
			newPopulation = (SolutionSet) newGenerationSelection.execute(union);

			if (equals(solutionSet, newPopulation)) {
				minimumDistance--;
			}
			if (minimumDistance <= -convergenceValue) {

				minimumDistance = (int) (1.0 / size * (1 - 1.0 / size) * size);
				// minimumDistance = (int) (0.35 * (1 - 0.35) * size);

				int preserve = (int) Math.floor(preservedPopulation
						* populationSize);
				newPopulation = new SolutionSet(populationSize);
				solutionSet.sort(crowdingComparator);
				for (int i = 0; i < preserve; i++) {
					newPopulation.add(new Solution(solutionSet.get(i)));
				}
				for (int i = preserve; i < populationSize; i++) {
					Solution solution = new Solution(solutionSet.get(i));
					cataclysmicMutation.execute(solution);
					problem_.evaluate(solution);
					problem_.evaluateConstraints(solution);
					newPopulation.add(solution);
				}
			}

			Operator localsearch;
			localsearch = operators_.get("localsearch");
			Solution lsSol = newPopulation.get(PseudoRandom.randInt(0,
					newPopulation.size() - 1));
			localsearch.execute(lsSol);
			problem_.evaluate(lsSol);

			iterations++;

			solutionSet = newPopulation;
			// if (evaluations >= maxEvaluations) {
			// condition = true;
			// }

			if ((System.currentTimeMillis() - initTime) > 90000) {
				condition = true;
				System.out.println("Total execution time: "
						+ (System.currentTimeMillis() - initTime));
			}
		}

		CrowdingArchive archive;
		archive = new CrowdingArchive(populationSize,
				problem_.getNumberOfObjectives());
		for (int i = 0; i < solutionSet.size(); i++) {
			archive.add(solutionSet.get(i));
		}

		return archive;
	} // execute
} // MOCHC
