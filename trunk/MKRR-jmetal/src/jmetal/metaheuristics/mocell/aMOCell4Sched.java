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
import jmetal.coevolutionary.util.ScheduleStrategy;
import jmetal.experiments.ExperimentNoPareto;
import jmetal.util.*;

import java.util.Calendar;
import java.util.Random;

/**
 * Class representing de MoCell algorithm
 */
public class aMOCell4Sched extends Algorithm {

	// ->fields
	private Problem problem_; // The problem to solve

	public aMOCell4Sched(Problem problem) {
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

		// RUSO
		Operator localsearch;

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

		// RUSO
		localsearch = operators_.get("localsearch");

		// Init the variables
		currentPopulation = new SolutionSet(populationSize);
		archive = new CrowdingArchive(archiveSize,
				problem_.getNumberOfObjectives());
		evaluations = 0;
		neighborhood = new Neighborhood(populationSize);
		neighbors = new SolutionSet[populationSize];

		Calendar cal = Calendar.getInstance();
		Random rand = new Random(cal.getTimeInMillis());

		// Create the initial population
		Solution newSolution = null;
		String specialSolution = ((String) getInputParameter("specialSolution"));

		int minminInit = PseudoRandom.randInt(0, populationSize - 1); // To
																		// initialize
																		// one
																		// individual
																		// with
																		// min-min
		int suffInit = PseudoRandom.randInt(0, populationSize - 1); // To
																	// initialize
																	// one
																	// individual
																	// with
																	// Sufferage
		System.out.println("Initial population minmin " + minminInit
				+ " sufferage " + suffInit);

		for (int i = 0; i < populationSize; i++) {
			if (specialSolution == null) {
				newSolution = new Solution(problem_);
			} // if
			else if (specialSolution.contains("OneMinmin")) {
				if (minminInit == i) {
					// int [] vars = ScheduleStrategy.minMin(ETC_,
					// numberOfTasks, numberOfMachines)
					DecisionVariables specialDV = problem_
							.generateSpecial(specialSolution);
					newSolution = new Solution(problem_, specialDV);
				} else
					newSolution = new Solution(problem_);
			} else if (specialSolution.equalsIgnoreCase("Min-Min")) {
				// RUSO
				// DecisionVariables specialDV = problem_.generateSpecial(
				// specialSolution );
				// newSolution = new Solution( problem_ , specialDV );
				// FIN COMENTARIO RUSO
				if (minminInit == i) {
					// specialSolution = "minMin";
					DecisionVariables specialDV = problem_
							.generateSpecial("minMin");
					newSolution = new Solution(problem_, specialDV);
				} else {
					if (suffInit == i) {
						DecisionVariables specialDV = problem_
								.generateSpecial("Sufferage");
						newSolution = new Solution(problem_, specialDV);
					} else {
						if (rand.nextFloat() < 0.5) {
							DecisionVariables specialDV = problem_
									.generateSpecial("Sufferage_rand");
							newSolution = new Solution(problem_, specialDV);
						} else {
							DecisionVariables specialDV = problem_
									.generateSpecial(specialSolution);
							newSolution = new Solution(problem_, specialDV);
						}
					}
				}
			} // else
			
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			currentPopulation.add(newSolution);
			newSolution.setLocation(i);
			
			if (minminInit == i) {
				System.out.println("MinMin: Makespan: "
						+ newSolution.getObjective(0) + " Flowtime: "
						+ newSolution.getObjective(1));
			} else if (suffInit == i) {
				System.out.println("Sufferage: Makespan: "
						+ newSolution.getObjective(0) + " Flowtime: "
						+ newSolution.getObjective(1));
			}

			evaluations++;
		} // for

		// Print the initial population
		currentPopulation.printObjectivesToFile("./InitialPopulation.fun");
		System.out
				.println("Initial population printed to ./InitialPopulation.fun");
		// RUSO
		// System.exit(-1);
		// Lo que sigue NO lo comente yo RUSO

		// Estaba asi
		// for (int i = 0; i < populationSize; i++){
		// Solution individual = new Solution(problem_);
		// problem_.evaluate(individual);
		// problem_.evaluateConstraints(individual);
		// currentPopulation.add(individual);
		// individual.setLocation(i);
		// evaluations++;
		// }

		int generations = 0;
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

				// System.out.println("ANTES Makespan:"+solution.getObjective(0)+" Flowtime: "+solution.getObjective(1));

				// parents
				parents[0] = (Solution) selectionOperator
						.execute(neighbors[ind]); // ESTABA ASI
				// parents[0] =
				// (Solution)((Object[])selectionOperator.execute(neighbors[ind]))[0];
				// // For TournamentFour
				if (archive.size() > 0) {
					parents[1] = (Solution) selectionOperator.execute(archive); // ESTABA
																				// ASI
					// parents[1] =
					// (Solution)((Object[])selectionOperator.execute(archive))[0];
					// // For TournamentFour
				} else {
					parents[1] = (Solution) selectionOperator
							.execute(neighbors[ind]); // ESTABA ASI
					// parents[1] =
					// (Solution)((Object[])selectionOperator.execute(neighbors[ind]))[0];
					// // For TournamentFour
				}

				// Create a new individual, using genetic operators mutation and
				// crossover
				offSpring = (Solution[]) crossoverOperator.execute(parents);

				problem_.evaluate(offSpring[0]);
				double m_1 = individual.getObjective(0);
				double f_1 = individual.getObjective(1);

				mutationOperator.execute(offSpring[0]);

				problem_.evaluate(offSpring[0]);
				double m_2 = (offSpring[0]).getObjective(0);
				double f_2 = (offSpring[0]).getObjective(1);
				if (m_2 < m_1) {
					System.out.println("MUTATION: ++++ Makespan: de " + m_1
							+ " a " + m_2);
				}
				if (f_2 < f_1) {
					System.out.println("MUTATION ***** Flowtime: de " + f_1
							+ " a " + f_2);
				}
				// RUSO
				localsearch.execute(offSpring[0]);
				// RUSO

				// Evaluate individual an his constraints
				problem_.evaluate(offSpring[0]);
				problem_.evaluateConstraints(offSpring[0]);
				evaluations++;

				// System.out.println("DESPUES Makespan:"+solution.getObjective(0)+" Flowtime: "+solution.getObjective(1));

				m_2 = (offSpring[0]).getObjective(0);
				f_2 = (offSpring[0]).getObjective(1);

				int flag = dominance.compare(individual, offSpring[0]);

				if (m_2 < m_1) {
					System.out.println("++++ Makespan: de " + m_1 + " a " + m_2
							+ " Flag: " + flag + " ");
				}
				if (f_2 < f_1) {
					System.out.println("***** Flowtime: de " + f_1 + " a "
							+ f_2 + ". Flag: " + flag);
				}

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
			// if (generations %10 == 0)
			// {
			// archive.printObjectivesToFile("./EvolPareto/FUN.Generation." +
			// generations);
			// }
			System.out.println("Gen: " + (generations++));

		}
		// System.out.println(evaluations);
		return archive;
	}

}
