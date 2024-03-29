/**
 * aMOCell4b.java
 * @author Juan J. Durillo
 * @version 1.0
 *
 */
package jmetal.metaheuristics.mocell;

import jmetal.base.*;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.base.archive.CrowdingArchive;
import jmetal.base.operator.comparator.*;
import jmetal.experiments.ExperimentNoPareto;
import jmetal.util.*;

/**
 * Class representing de MoCell algorithm
 */
public class aMOCell4 extends Algorithm {

	// ->fields
	private Problem problem_; // The problem to solve

	public aMOCell4(Problem problem) {
		problem_ = problem;
	}

	/**
	 * Execute the algorithm
	 * 
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException {
		// Init the param
		int populationSize, archiveSize, maxEvaluations, evaluations;
		Operator mutationOperator, crossoverOperator, selectionOperator;
		SolutionSet currentPopulation;
		CrowdingArchive archive;
		SolutionSet[] neighbors;
		Neighborhood neighborhood;
		Comparator dominance = new DominanceComparator();
		Comparator crowdingComparator = new CrowdingComparator();
		Distance distance = new Distance();

		// Init the param
		// Read the params
		populationSize = ((Integer) getInputParameter("populationSize"))
				.intValue();
		archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations"))
				.intValue();

		// Read the operators
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		selectionOperator = operators_.get("selection");

		// Init the variables
		currentPopulation = new SolutionSet(populationSize);
		archive = new CrowdingArchive(archiveSize,
				problem_.getNumberOfObjectives());
		evaluations = 0;
		neighborhood = new Neighborhood(populationSize);
		neighbors = new SolutionSet[populationSize];

		// Create the initial population
		for (int i = 0; i < populationSize; i++) {
			Solution individual = new Solution(problem_);
			problem_.evaluate(individual);
			problem_.evaluateConstraints(individual);
			currentPopulation.add(individual);
			individual.setLocation(i);
			evaluations++;
		}

		while (evaluations < maxEvaluations) {
			for (int ind = 0; ind < currentPopulation.size(); ind++) {
				Solution individual = new Solution(currentPopulation.get(ind));

				Solution[] parents = new Solution[2];
				Solution[] offSpring;

				// neighbors[ind] =
				// neighborhood.getFourNeighbors(currentPopulation,ind);
				neighbors[ind] = neighborhood.getEightNeighbors(
						currentPopulation, ind);
				neighbors[ind].add(individual);

				// parents
				parents[0] = (Solution) selectionOperator
						.execute(neighbors[ind]);
				if (archive.size() > 0) {
					parents[1] = (Solution) selectionOperator.execute(archive);
				} else {
					parents[1] = (Solution) selectionOperator
							.execute(neighbors[ind]);
				}

				// Create a new individual, using genetic operators mutation and
				// crossover
				offSpring = (Solution[]) crossoverOperator.execute(parents);
				mutationOperator.execute(offSpring[0]);

				// Evaluate individual an his constraints
				problem_.evaluate(offSpring[0]);
				problem_.evaluateConstraints(offSpring[0]);
				evaluations++;

				int flag = dominance.compare(individual, offSpring[0]);

				if (flag == 1) { // The new individual dominates
					offSpring[0].setLocation(individual.getLocation());
					currentPopulation.replace(offSpring[0].getLocation(),
							offSpring[0]);
					archive.add(new Solution(offSpring[0]));
				} else if (flag == 0) { // The new individual is non-dominated
					neighbors[ind].add(offSpring[0]);
					offSpring[0].setLocation(-1);
					Ranking rank = new Ranking(neighbors[ind]);
					for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
						distance.crowdingDistanceAssignment(
								rank.getSubfront(j),
								problem_.getNumberOfObjectives());
					}
					neighbors[ind].sort(crowdingComparator);
					Solution worst = neighbors[ind]
							.get(neighbors[ind].size() - 1);

					if (worst.getLocation() == -1) { // The worst is the
														// offspring
						archive.add(new Solution(offSpring[0]));
					} else {
						offSpring[0].setLocation(worst.getLocation());
						currentPopulation.replace(offSpring[0].getLocation(),
								offSpring[0]);
						archive.add(new Solution(offSpring[0]));
					}
				}
			}
		}
		// System.out.println(evaluations);
		return archive;
	}

}
