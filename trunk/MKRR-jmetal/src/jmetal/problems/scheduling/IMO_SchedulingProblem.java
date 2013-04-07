package jmetal.problems.scheduling;

import jmetal.base.DecisionVariables;
import jmetal.base.Solution;
import jmetal.coevolutionary.util.MKRRMatrix;
import jmetal.util.JMException;

public interface IMO_SchedulingProblem {

	/**
	 * Calculates completion times of a solution
	 * 
	 * @param Schedule
	 *            the solution to use
	 * @param numberOfTasks
	 *            the number of tasks used in the problem
	 * 
	 * @return A vector of completion times of the machines
	 */
	public abstract double[] ComputeCompletion(int[] Schedule); // ComputeCompletion

	/**
	 * Calculates the partial completion times of a solution
	 * 
	 * @param ETC
	 *            the matrix that contains the ETC values to use
	 * @param Schedule
	 *            the solution to use
	 * @param numberOfTasks
	 *            the number of tasks used in the problem
	 * @param numberOfMachines
	 *            the number of machines used in the problem
	 * @return completion a vector of completion times of the machines
	 */
	public abstract double[] ComputePartialCompletion(double[][] ETC,
			int[] Schedule, int numberOfTasks, int numberOfMachines, int start,
			int end); // computePartialCompletion

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 * @param loadingPosition
	 *            Loading position
	 * @throws JMException
	 */
	public abstract void evaluate(Solution solution) throws JMException; // evaluate

	public abstract DecisionVariables generateSpecial(String type); // generateSpecial

	public abstract int getNumberOfTasks();
	public abstract int getNumberOfMachines();
	public abstract double[][] getETCMatrix();
	
}