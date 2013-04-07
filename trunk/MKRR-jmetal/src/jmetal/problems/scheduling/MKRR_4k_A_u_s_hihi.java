package jmetal.problems.scheduling;

import jmetal.base.Configuration.SolutionType_;
import jmetal.base.Configuration.VariableType_;
import jmetal.base.Problem;
import jmetal.coevolutionary.util.MKRRMatrix;
import jmetal.coevolutionary.util.Matrix;

/**
 * Class representing the problem u_i_hihi_mak_flow
 * 
 * UNIFORM distribution INCONSISTENT HIGH task heterogeneity HIGH machine
 * heterogeneity
 * 
 * @author Juan A. Caero Tamayo modified by Bernab Dorronsoro
 * 
 * @version 1.0
 */
public class MKRR_4k_A_u_s_hihi extends MO_MKRR {

	private static final long serialVersionUID = -2210887953365922092L;

	// Base directory of the matrices instances
	private static final String path_ = "./SchedInstances/WRRETC_4k/";
	private static final String name_ = "A.u_s_hihi"; // /< Name of the problem
	// private static final double per_ = 0.3; ///< Constant used in the matrix

	private int instanceNo_ = 0;

	/**
	 * Constructor
	 * 
	 * @param numberOfVariables
	 *            Number of variables of the problem
	 * @param solutionType
	 *            The solution type must "Int"
	 * @param numberOfIslands
	 *            Number of islands to use, must be greater than 1
	 */
	public MKRR_4k_A_u_s_hihi(Integer numberOfVariables, String solutionType,
			Integer numberOfIslands) {

		M_ = new MKRRMatrix();
//		M_.recoverData(512, 16, path_ + name_ + "." + instanceNo_);
		M_.recoverData(512, 16, path_ + name_);	
		ETC_ = M_.getETCmatrix();
		Priorities_ = M_.getPriorities();
		
		int numberOfVars = M_.getNumberOfTasks();
		int numberOfMachs = M_.getNumberOfMachines();

		numberOfTasks_ = numberOfVars;
		numberOfMachines_ = (numberOfMachs > 0) ? numberOfMachs
				: numberOfMachinesByDefault_;
		numberOfVariables_ = (numberOfVars > 0) ? numberOfVars
				: numberOfVariablesByDefault_;

		numberOfObjectives_ = numberOfObjectivesByDefault_;
		numberOfConstraints_ = 0;
		problemName_ = name_;

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int i = 0; i < numberOfVariables_; i++) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = numberOfMachines_;
		} // for

		solutionType_ = Enum.valueOf(SolutionType_.class, solutionType);

		// All the variables are of the same type, so the solutionType name is
		// the same than the variableType name.
		variableType_ = new VariableType_[numberOfVariables_];
		for (int var = 0; var < numberOfVariables_; ++var)
			variableType_[var] = Enum
					.valueOf(VariableType_.class, solutionType);

	} // u_i_hihi_mak_flow

	/**
	 * Constructor
	 * 
	 * @param solutionType
	 *            The solution type must "Int"
	 */
	public MKRR_4k_A_u_s_hihi(String solutionType) {

		this(numberOfVariablesByDefault_, solutionType,
				numberOfIslandsByDefault_);
	} // u_i_hihi_mak_flow

	public MKRR_4k_A_u_s_hihi(String solutionType, Integer instanceNumber) {
		this(numberOfVariablesByDefault_, solutionType,
				numberOfIslandsByDefault_);
		instanceNo_ = instanceNumber.intValue();
	} // u_i_hihi_mak_flow

	// public void nextInstance()
	// {
	// instanceNo_++;
	// }

	@Override
	public Problem clone() {
		MKRR_4k_A_u_s_hihi newProblem = new MKRR_4k_A_u_s_hihi("Int");

		newProblem.M_ = this.M_;
		newProblem.numberOfMachines_ = this.numberOfMachines_;
		newProblem.numberOfTasks_ = this.numberOfTasks_;
		newProblem.ETC_ = this.ETC_;

		return (newProblem);
	} // clone
	
	@Override
	public int getNumberOfTasks() {
		return M_.getNumberOfTasks();
	}

	@Override
	public int getNumberOfMachines() {
		return M_.getNumberOfMachines();
	}

	@Override
	public double[][] getETCMatrix() {
		return ETC_;
	}
} // u_i_hihi_mak_flow
