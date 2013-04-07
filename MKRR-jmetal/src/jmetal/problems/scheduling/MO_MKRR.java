package jmetal.problems.scheduling;

import jmetal.base.Configuration;
import jmetal.base.Variable;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.variable.Int;
import jmetal.base.DecisionVariables;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.coevolutionary.util.MKRRMatrix;
import jmetal.coevolutionary.util.Matrix;
import jmetal.coevolutionary.util.ScheduleStrategy;
import jmetal.util.JMException;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;

/**
 * Class representing problem in grid scheduling
 * 
 * @author Anowar El Amouri (first version)
 * @author Juan A. Caero (bug fixes, new features and optimization)
 * 
 * @version 1.1
 */
public abstract class MO_MKRR extends Problem implements IMO_SchedulingProblem {

	private static final long serialVersionUID = 1838545564657006733L;

	protected MKRRMatrix M_;

	protected static final int numberOfVariablesByDefault_ = 512;
	protected static final int numberOfObjectivesByDefault_ = 2;
	protected static final int numberOfIslandsByDefault_ = 4;
	protected static final int numberOfMachinesByDefault_ = 16;
	protected int numberOfMachines_ = 0;
	protected int numberOfTasks_ = 0;

	// Stores the copy of ETC values
	protected double[][] ETC_;
	protected int[] Priorities_;

	protected MO_MKRR() {
		// none
	}

	public MO_MKRR(String solutionType) {
		solutionType_ = Enum.valueOf(SolutionType_.class, solutionType);
	}

	public MO_MKRR(Integer numberOfVariables, String solutionType) {
		problemName_ = "MO_Cell_MKRR";

		numberOfMachines_ = (numberOfMachines_ > 0) ? numberOfMachines_
				: numberOfMachinesByDefault_;
		numberOfVariables_ = (numberOfVariables.intValue() > 0) ? numberOfVariables
				.intValue() : numberOfVariablesByDefault_;

		numberOfObjectives_ = numberOfObjectivesByDefault_;
		numberOfConstraints_ = 0;

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int i = 0; i < numberOfVariables_; i++) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = numberOfMachines_;
		} // for

		solutionType_ = Enum.valueOf(SolutionType_.class, solutionType);

		// All the variables are of the same type, so the solutionType name is
		// the same than the variableType name
		variableType_ = new VariableType_[numberOfVariables_];
		for (int var = 0; var < numberOfVariables_; var++)
			variableType_[var] = Enum
					.valueOf(VariableType_.class, solutionType);
	}

	public MO_MKRR(Integer numberOfVariables, String solutionType,
			Integer numberOfIslands) {

		problemName_ = "MO_Cell_MKRR";

		numberOfMachines_ = (numberOfMachines_ > 0) ? numberOfMachines_
				: numberOfMachinesByDefault_;
		numberOfVariables_ = (numberOfVariables.intValue() > 0) ? numberOfVariables
				.intValue() : numberOfVariablesByDefault_;

		numberOfObjectives_ = numberOfObjectivesByDefault_;
		numberOfConstraints_ = 0;

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int i = 0; i < numberOfVariables_; i++) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = numberOfMachines_;
		} // for

		solutionType_ = Enum.valueOf(SolutionType_.class, solutionType);

		// All the variables are of the same type, so the solutionType name is
		// the same than the variableType name
		variableType_ = new VariableType_[numberOfVariables_];
		for (int var = 0; var < numberOfVariables_; var++)
			variableType_[var] = Enum
					.valueOf(VariableType_.class, solutionType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jmetal.problems.scheduling.MO_SchedulingProblem#ComputeCompletion(int[])
	 */
	@Override
	public double[] ComputeCompletion(int[] Schedule) {
		// Computation of ETC sum

		double[] sumETC = new double[numberOfMachines_];
		int machine;
		int numberOfTasks = Schedule.length;

		for (int j = 0; j < numberOfTasks; ++j) {
			machine = Schedule[j];
			sumETC[machine] += ETC_[j][machine];
		} // for

		return (sumETC);
	} // ComputeCompletion

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jmetal.problems.scheduling.MO_SchedulingProblem#ComputePartialCompletion
	 * (double[][], int[], int, int, int, int)
	 */
	@Override
	public double[] ComputePartialCompletion(double[][] ETC, int[] Schedule,
			int numberOfTasks, int numberOfMachines, int start, int end) {

		double[] sumETC = new double[numberOfMachines];

		// Computation of ETC sum
		int m1;

		for (int j = start; j < end; ++j) {
			m1 = Schedule[j];
			sumETC[m1] += ETC[j][m1];
		} // for

		return (sumETC);
	} // computePartialCompletion

	public class MySimpleEntryComparator implements
			Comparator<SimpleEntry<Integer, Double>> {
		@Override
		public int compare(SimpleEntry<Integer, Double> o1,
				SimpleEntry<Integer, Double> o2) {

			if (o1.getValue() > o2.getValue()) {
				return 1;
			} else if (o1.getValue() == o2.getValue()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	/**
	 * Calculates WRR of a solution
	 */
	private double ComputeWRR(int[] Schedule) {
		ArrayList<ArrayList<SimpleEntry<Integer, Double>>> Assig = new ArrayList<ArrayList<SimpleEntry<Integer, Double>>>();

		for (int m = 0; m < numberOfMachines_; m++) {
			ArrayList<SimpleEntry<Integer, Double>> col = new ArrayList<SimpleEntry<Integer, Double>>();
			Assig.add(col);
		}

		// Add tasks to machines
		for (int j = 0; j < numberOfTasks_; j++) {
			int mach = Schedule[j];
			/*
			 * Assig.get(mach).add(new SimpleEntry<Integer, Double>(j,
			 * ETC_[j][mach] / Priorities_[j]));
			 */
			Assig.get(mach).add(
					new SimpleEntry<Integer, Double>(j, ETC_[j][mach]));
			/*
			 * Assig.get(mach).add(new SimpleEntry<Integer, Double>(j,
			 * ETC_[j][mach]));
			 */
		}

		// ArrayList list_new;
		for (int m = 0; m < numberOfMachines_; m++) {
			Collections.sort(Assig.get(m), new MySimpleEntryComparator());
		}

		double wrr = 0.0;
		int num_task = 0;

		for (int m = 0; m < numberOfMachines_; m++) {
			double machine_flowtime = 0.0;

			System.out.print("[" + m + "] ");
			for (int k = 0; k < Assig.get(m).size(); k++) {
				//System.out.print(Assig.get(m).get(k).getValue() + " | ");
				
				machine_flowtime += Assig.get(m).get(k).getValue();

				if (Assig.get(m).get(k).getValue() > 0.0) {
					wrr += machine_flowtime / Assig.get(m).get(k).getValue()
							* Priorities_[Assig.get(m).get(k).getKey()];
				}
			}
			System.out.print("\n");
		}

		return (wrr);
	}

	/**
	 * Calculates fitness values of a solution
	 * 
	 * @param Schedule
	 *            the solution to use
	 * 
	 * @return fitness a vector of fitness values of the solution
	 */
	private double[] Fitness(int[] Schedule) {

		// Recover Completion Time and Robustness Radius
		double[] completion = ComputeCompletion(Schedule);
		double wrr = ComputeWRR(Schedule);

		// Find maximum of completion time
		// Find maximum robustness
		double maxCompletion = completion[0];

		// For efficiency issues, we are compute 0.3*CT instead of 1.3*M-CT for
		// the robustness radius
		// Therefore, the robustness of the solution is the case in which M=CT,
		// so it is the maximum robustness radio computed this way (when CT is
		// max, i.e., CT = M),
		// instead of the minimum robustness radio computed as 1.3*M-CT (when
		// M-CT is min, i.e., CT = M)
		// double maxRobustness = radius[0];

		for (int k = 1; k < numberOfMachines_; ++k) {
			if (completion[k] > maxCompletion)
				maxCompletion = completion[k];
		} // for

		// Return maximum and minimum as fitness values of makespan and
		// robustness respectively
		double[] fitness = new double[2];

		fitness[0] = maxCompletion;
		fitness[1] = wrr;

		return (fitness);
	} // fitness

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jmetal.problems.scheduling.MO_SchedulingProblem#evaluate(jmetal.base.
	 * Solution)
	 */
	@Override
	public void evaluate(Solution solution) throws JMException {
		DecisionVariables gen;

		// Recover the solution values
		gen = solution.getDecisionVariables();

		int[] Schedule = new int[numberOfTasks_]; // Contains the solution
													// values

		// Recover solution parameters
		for (int var = 0; var < numberOfTasks_; ++var) {
			Schedule[var] = (int) gen.variables_[var].getValue();
		} // for

		// Return the fitness values
		setObjectives(solution, Fitness(Schedule));
	} // evaluate

	/**
	 * Sets the value of all the objectives in the solution.
	 * 
	 * @param solution
	 *            The solution to modify.
	 * @param fitness
	 *            The fitness values to be stored.
	 */
	public void setObjectives(Solution solution, double[] fitness) {
		for (int i = 0; i < numberOfObjectivesByDefault_; i++) {
			solution.setObjective(i, fitness[i]);
		}
	}

	/**
	 * @param m
	 *            the matrix to set
	 */
	public void setMatrix(MKRRMatrix m) {
		M_ = m;
		numberOfTasks_ = M_.getNumberOfTasks();
		numberOfMachines_ = M_.getNumberOfMachines();
		ETC_ = M_.getETCmatrix();
		Priorities_ = M_.getPriorities();
	} // setMatrix

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmetal.problems.scheduling.MO_SchedulingProblem#getMatrix()
	 */
	public MKRRMatrix getMatrix() {
		return M_;
	} // getMatrix

	/**
	 * This method calculates the time of execution of each task in the selected
	 * machine. The result is stored in a string in order to store later in a
	 * file. Works with sliced and full solutions.
	 * 
	 * @param solution
	 *            the solution
	 * @return the string
	 * @throws JMException
	 */
	public String getETCvectorString(Solution solution, int loadingPosition)
			throws JMException {
		DecisionVariables gen;

		// Recover the solution values
		gen = solution.getDecisionVariables();

		int numberOfTasks = M_.getNumberOfTasks();

		double[] etcVector = new double[numberOfTasks];
		double[][] ETC = M_.getETCmatrix();

		// Recover solution parameters
		int m1;
		for (int var = 0; var < numberOfTasks; ++var) {
			m1 = (int) gen.variables_[var].getValue();
			etcVector[var] = ETC[var][m1];
		} // for

		String s = "";

		for (int var = 0; var < numberOfTasks; ++var)
			s += "" + etcVector[var] + " ";

		return (s);
	} // getETCvector

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jmetal.problems.scheduling.MO_SchedulingProblem#generateSpecial(java.
	 * lang.String)
	 */
	@Override
	public DecisionVariables generateSpecial(String type) {
		DecisionVariables decisionVariables = null;

		if (type != null) {
			if (type.compareToIgnoreCase("OneMinmin") == 0) {
				int[] Schedule = ScheduleStrategy.minMin(M_.getETCmatrix(),
						numberOfTasks_, numberOfMachines_);
				Variable[] variables = new Variable[numberOfTasks_];
				for (int i = 0; i < numberOfTasks_; ++i)
					variables[i] = new Int(Schedule[i], 0, numberOfMachines_);

				decisionVariables = new DecisionVariables(this, variables);
			} else if (type.compareToIgnoreCase("Min-min") == 0) {
				int[] Schedule = ScheduleStrategy.minMinInitialization(
						M_.getETCmatrix(), numberOfTasks_, numberOfMachines_);

				Variable[] variables = new Variable[numberOfTasks_];
				for (int i = 0; i < numberOfTasks_; ++i)
					variables[i] = new Int(Schedule[i], 0, numberOfMachines_);

				decisionVariables = new DecisionVariables(this, variables);
				// RUSO
			} else if (type.compareToIgnoreCase("minMin") == 0) {
				int[] Schedule = ScheduleStrategy.minMin(M_.getETCmatrix(),
						numberOfTasks_, numberOfMachines_);

				Variable[] variables = new Variable[numberOfTasks_];
				for (int i = 0; i < numberOfTasks_; ++i)
					variables[i] = new Int(Schedule[i], 0, numberOfMachines_);

				decisionVariables = new DecisionVariables(this, variables);
			} else if (type.compareToIgnoreCase("Sufferage") == 0) {
				int[] Schedule = ScheduleStrategy.Sufferage(M_.getETCmatrix(),
						numberOfTasks_, numberOfMachines_);

				Variable[] variables = new Variable[numberOfTasks_];
				for (int i = 0; i < numberOfTasks_; ++i)
					variables[i] = new Int(Schedule[i], 0, numberOfMachines_);

				decisionVariables = new DecisionVariables(this, variables);
			} else if (type.compareToIgnoreCase("Sufferage_rand") == 0) {
				int[] Schedule = ScheduleStrategy.Sufferage_rand(
						M_.getETCmatrix(), numberOfTasks_, numberOfMachines_);

				Variable[] variables = new Variable[numberOfTasks_];
				for (int i = 0; i < numberOfTasks_; ++i)
					variables[i] = new Int(Schedule[i], 0, numberOfMachines_);

				decisionVariables = new DecisionVariables(this, variables);
				// RUSO
			} // if
			else {
				Configuration.logger_.severe("MO_MKRR.generateSpecial: type \""
						+ type + "\" unknown.");
				throw new RuntimeException(
						"Exception in MO_MKRR.generateSpecial( String ) ");
			} // else
		} // if
		else {
			DecisionVariables slices;
			slices = new DecisionVariables(this);

			decisionVariables = new DecisionVariables(slices);
		} // else

		return decisionVariables;
	} // generateSpecial

} // MO_Scheduling_mak_flow
