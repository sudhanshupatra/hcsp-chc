package jmetal.base.operator.localSearch;

import jmetal.base.Variable;
import jmetal.base.variable.Int;
import jmetal.base.operator.localSearch.LocalSearch;
import jmetal.base.DecisionVariables;
import jmetal.base.Solution;
//RUSO
//import jmetal.problems.scheduling.MO_Scheduling;
import jmetal.problems.scheduling.IMO_SchedulingProblem;
import jmetal.problems.scheduling.MO_Scheduling_mak_flow;
import jmetal.coevolutionary.util.Matrix;

import jmetal.util.JMException;

import java.util.Calendar;
import java.util.Random;

import jmetal.base.SolutionSet;
import jmetal.problems.scheduling.MO_Scheduling_mak_flow;
import jmetal.base.Solution;
import jmetal.base.Operator;
import jmetal.base.SolutionSet;
import jmetal.base.operator.comparator.OverallConstraintViolationComparator;
import jmetal.base.operator.comparator.DominanceComparator;

/**
 * This class implements a local search operator based on swapping two jobs, and
 * applied the pair of jobs that yields the best reduction in the completion
 * time.
 * 
 * <b>IMPORTANT NOTE:</b> This class needs to work only with sliced (naked)
 * solutions.
 * 
 * @author Anowar El Amouri
 * @author Juan A. ero (bug fixes, optimization and new features added)
 * @version 1.1
 */
public class LMCTSLocalSearch extends LocalSearch {

	private static final long serialVersionUID = 7138734592015581051L;

//	private Matrix M_ = null;
	private IMO_SchedulingProblem problem_ = null;

	private SolutionSet archive_;

	/**
	 * Constructor Creates a new local search object.
	 */
	public LMCTSLocalSearch() {

		super();
	} // LMCTSLocalSearch

	/**
	 * Executes the local search.
	 * 
	 * @param object
	 *            Object representing a package with a solution and an islandId
	 * @return An object containing the new improved solution
	 * @throws JMException
	 */
	public Object execute(Object object) throws JMException {

		// Recover the parameters (solution and island)
		Solution solution = (Solution) object;

		if (problem_ == null)
			problem_ = (IMO_SchedulingProblem) getParameter("Problem");
//		if (M_ == null)
//			M_ = problem_.getMatrix();

		// RUSO
		double m_1 = solution.getObjective(0);
		double f_1 = solution.getObjective(1);
		// System.out.println("ANTES Makespan:"+solution.getObjective(0)+" Flowtime: "+solution.getObjective(1));

		doLocalSearch(solution);

		// System.out.println("DESPUES Makespan:"+solution.getObjective(0)+" Flowtime: "+solution.getObjective(1));

		double m_2 = solution.getObjective(0);
		double f_2 = solution.getObjective(1);

//		if ((m_2 < m_1) || (f_2 < f_1)) {
//			System.out.println("******* LS: ++++ Makespan: de " + m_1 + " a "
//					+ m_2 + ", Flowtime: de " + f_1 + " a " + f_2);
//		}
		// fin Ruso

		// return new Solution(solution);
		return null;
	} // execute

	/**
	 * @param solution
	 * @param islandId
	 * @throws JMException
	 */
	private void doLocalSearch_old(Solution solution) throws JMException {
		DecisionVariables decisionVariables;
		decisionVariables = solution.getDecisionVariables();

		// Recover the solution parameters
		int task_Number = problem_.getNumberOfTasks();
		int machine_Number = problem_.getNumberOfMachines();

		int[] Schedule = new int[task_Number]; // Contains the solution values
		int[] Schedule1 = new int[task_Number]; // Contains the solution swapped
												// values

		// Recover ETC Matrix
		double[][] MatrixETC = problem_.getETCMatrix();

		// start and end of the slice. The main loop only works in the specified
		// slice
		int sliceSz = decisionVariables.size();
		int start = sliceSz * 0;
		int end = start + sliceSz;
		// Recover Solution values
		for (int var = start; var < end; ++var)
			Schedule[var] = (int) decisionVariables.variables_[var].getValue();

		// Apply LMCTS Local Search in the correct slice

		int j = start + 1;
		System.arraycopy(Schedule, start, Schedule1, start, sliceSz);
		while (j < end) {
			// 1. Compute partial completion time of the solution
			double[] completion = problem_.ComputePartialCompletion(MatrixETC,
					Schedule, task_Number, machine_Number, start, end);

			// 2. Create the tab of the solution with swapped values
			if (j > (start + 1))
				System.arraycopy(Schedule, j - 2, Schedule1, j - 2, 2);

			if (Schedule1[j - 1] != Schedule1[j]) {

				int temp = Schedule1[j - 1];
				Schedule1[j - 1] = Schedule1[j];
				Schedule1[j] = temp;

				// 3. Compute partial completion time of the solution with
				// swapped values
				double[] completion1 = problem_.ComputePartialCompletion(
						MatrixETC, Schedule1, task_Number, machine_Number,
						start, end);

				// 4. Compare the two solutions and apply the best
				int m1 = Schedule[j - 1];
				int m2 = Schedule[j];

				double sum = completion[m1] + completion[m2];
				double sum1 = completion1[m1] + completion1[m2];

				if (sum1 < sum)
					System.arraycopy(Schedule1, j - 1, Schedule, j - 1, 2);
			} // if
				// 5. Choose another pair of jobs
			++j;
		} // while

		// Recover New Solution values
		Variable[] v = decisionVariables.variables_;
		for (int var = start; var < end; ++var) {
			((Int) v[var]).setValue(Schedule[var]);
		} // for

		solution.setDecisionVariables(decisionVariables);
		solution.setCrowdingDistance(0.0);
		solution.setRank(0);
	} // doLocalSearch

	private void doLocalSearch(Solution solution) throws JMException {
		DecisionVariables decisionVariables;
		decisionVariables = solution.getDecisionVariables();

		Calendar cal = Calendar.getInstance();
		Random rand = new Random(cal.getTimeInMillis());

		if (rand.nextFloat() < 0.5) {

			int num_swap = rand.nextInt(20);
			// num_swap = 20;
			// System.out.print("++++ Makespan:"+solution.getObjective(0)+" Flowtime: "+solution.getObjective(1)+" ("+num_swap+" swaps) -> ");

			// Recover the solution parameters
			int task_Number = problem_.getNumberOfTasks();
			int machine_Number = problem_.getNumberOfMachines();

			int[] Schedule = new int[task_Number]; // Contains the solution
													// values
			int[] Schedule1 = new int[task_Number]; // Contains the solution
													// swapped values

			// Recover ETC Matrix
			double[][] MatrixETC = problem_.getETCMatrix();

			// start and end of the slice. The main loop only works in the
			// specified slice
			int sliceSz = decisionVariables.size();
			int start = 0;
			int end = sliceSz;

			int search = 1;

			// Recover Solution values
			for (int var = start; var < end; ++var)
				Schedule[var] = (int) decisionVariables.variables_[var]
						.getValue();

			double[] completion = problem_.ComputePartialCompletion(MatrixETC,
					Schedule, task_Number, machine_Number, start, end);

			for (int l = 0; (l < num_swap) && (search == 1); l++) {

				// Apply LMCTS Local Search in the correct slice

				int heavy_mach = 0;
				double mak_max = completion[0];
				for (int k = 1; k < machine_Number; k++) {
					if (completion[k] > mak_max) {
						mak_max = completion[k];
						heavy_mach = k;
					}
				}

				if (rand.nextFloat() < 0.3) {
					heavy_mach = rand.nextInt(machine_Number);
				}

				double max_delta = -999999.0;
				double delta = 0.0;
				int best_move = -1;
				int best_swap = -1;

				int TASK_INIT = rand.nextInt(task_Number);
				int TOPE_K = rand.nextInt(task_Number);

				int TASK_INIT_H = rand.nextInt(task_Number);
				int TOPE_H = rand.nextInt(task_Number);

//				System.out.println("LS: " + TOPE_K + "x" + TOPE_H + ", heavy: "
//						+ heavy_mach);
				TOPE_K = 500;
				TOPE_H = 500;
				// Recorrer tareas de heavy
				// for (int k=0; k<task_Number; k++){}
				int tried_k = 0;
				for (int k = TASK_INIT; tried_k < TOPE_K; k = (k + 1)
						% task_Number) {
					if (Schedule[k] == heavy_mach) {
						int best_swap_k = -1;
						double max_delta_k = -999999.0;
						// Recorrer tareas de otras maquinas
						// for (int h=0; h<task_Number; h++){}
						int tried_h = 0;
						for (int h = TASK_INIT_H; tried_h < TOPE_H; h = (h + 1)
								% task_Number) {
							if (Schedule[h] != heavy_mach) {
								delta = (MatrixETC[k][heavy_mach] - MatrixETC[k][Schedule[h]])
										+ (MatrixETC[h][Schedule[h]] - MatrixETC[h][heavy_mach]);
								if (delta > max_delta) {
									best_swap_k = h;
									max_delta_k = delta;
								}
							}
							tried_h++;
						}
						if (max_delta_k > max_delta) {
							best_move = k;
							best_swap = best_swap_k;
							max_delta = max_delta_k;
						}
					}
					tried_k++;
				}

				// aplicar mejor swap
				if ((best_move > -1) && (best_swap > -1)) {
					// System.out.println("**** : swap "+best_move+" de maq "+heavy_mach+" a maq "+Schedule[best_swap]+", y "+best_swap+" de maq "+Schedule[best_swap]+" a maq "+heavy_mach+", max_delta: "+max_delta);
					Schedule[best_move] = Schedule[best_swap];
					Schedule[best_swap] = heavy_mach;
					completion = problem_.ComputePartialCompletion(MatrixETC,
							Schedule, task_Number, machine_Number, start, end);
					if (completion[heavy_mach] < completion[Schedule[best_move]]) {
						search = 0;
					}
				}
			}
			// Recover New Solution values
			Variable[] v = decisionVariables.variables_;
			for (int var = start; var < end; ++var) {
				((Int) v[var]).setValue(Schedule[var]);
			} // for

			solution.setDecisionVariables(decisionVariables);
			solution.setCrowdingDistance(0.0);
			solution.setRank(0);

			problem_.evaluate(solution);

			// System.out.println(" Makespan:"+solution.getObjective(0)+" Flowtime: "+solution.getObjective(1));

		}
	} // doLocalSearch

	public int getEvaluations() {

		return 0;
	} // getEvaluations

} // LMCTSLocalSearch
