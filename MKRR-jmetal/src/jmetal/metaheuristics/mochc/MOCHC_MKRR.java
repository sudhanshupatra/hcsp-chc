/**
 * MOCHC_main.java
 *
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.metaheuristics.mochc;

import jmetal.base.*;
import jmetal.base.operator.crossover.*;
import jmetal.base.operator.mutation.*;
import jmetal.base.operator.selection.*;
import jmetal.problems.*;
import jmetal.problems.DTLZ.*;
import jmetal.problems.ZDT.*;
import jmetal.problems.scheduling.MO_MKRR;
import jmetal.problems.scheduling.MKRR_u_i_hihi;
import jmetal.problems.WFG.*;

public class MOCHC_MKRR {

	public static void main(String[] args) {
		try {
			/*Problem problem = new u_i_hihi_MKRR(149);

			Algorithm algorithm = new MOCHC(problem);

			algorithm.setInputParameter("initialConvergenceCount", 0.25);
			algorithm.setInputParameter("preservedPopulation", 0.05);
			algorithm.setInputParameter("convergenceValue", 3);
			algorithm.setInputParameter("populationSize", 10);
			algorithm.setInputParameter("maxEvaluations", 60000);

			// Crossover operator.
			Operator crossoverOperator;
			crossoverOperator = CrossoverFactory
					.getCrossoverOperator("HUXCrossover");
			crossoverOperator.setParameter("probability", 1.0);
			algorithm.addOperator("crossover", crossoverOperator);

			// Parent selection.
			Operator parentsSelection;
			parentsSelection = SelectionFactory
					.getSelectionOperator("RandomSelection");
			algorithm.addOperator("parentSelection", parentsSelection);

			// Offspring selection.
			Operator newGenerationSelection;
			newGenerationSelection = SelectionFactory
					.getSelectionOperator("RankingAndCrowdingSelection");
			newGenerationSelection.setParameter("problem", problem);
			algorithm.addOperator("newGenerationSelection",
					newGenerationSelection);

			// Mutation operator.
			Operator mutationOperator;
			mutationOperator = MutationFactory
					.getMutationOperator("BitFlipMutation");
			mutationOperator.setParameter("probability", 0.35);
			algorithm.addOperator("cataclysmicMutation", mutationOperator);

			// Execute the Algorithm
			long initTime = System.currentTimeMillis();
			SolutionSet population = algorithm.execute();
			long estimatedTime = System.currentTimeMillis() - initTime;
			System.out.println("Total execution time: " + estimatedTime);

			// Print results
			population.printVariablesToFile("VAR");
			population.printObjectivesToFile("FUN");*/
		} // try
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		} // catch
	}// main
}
