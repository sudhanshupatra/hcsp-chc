/**
 * MOCellStudy.java
 *
 * @author Bernabe Dorronsoro
 * @version 1.0
 */
package jmetal.experiments.scheduling;

import jmetal.experiments.*;

import java.util.logging.Logger;

import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.experiments.settings.MOCell_Settings;
import jmetal.experiments.settings.MOCell_Settings_AG;
import jmetal.experiments.settings.MOCell_Settings_SRF;
//import jmetal.problems.MOBypassLinks;
import jmetal.util.JMException;
import jmetal.experiments.util.GeneratePareto;

import jmetal.experiments.settings.CEC10.*;

/**
 * @author Bernabe Dorronsoro
 * 
 *         This experiment class is configured to solve 100 instances of every
 *         scheduling problem class with NSGAII, MOCell, IBEA, and MOEA/D (100
 *         independent runs per algorithm/instance)
 */
public class MOCHC_MKRR_1k_Exp extends ExperimentNoPareto {

	// Number of independent runs per algorithm and problem
	private int independentRunsDf_ = 1;
	// Number of threads to use (= number of algorithms to run in parallel)
	private int numberOfThreadsDf_ = 1;
	// Number of instances to solve per problem
	private int numberOfInstancesDF_ = 30;
//	private int numberOfInstancesDF_ = 1;

	/**
	 * Configures the algorithms in each independent run
	 * 
	 * @param problem
	 *            The problem to solve
	 * @param problemIndex
	 */
	public synchronized void algorithmSettings(Problem problem,
			int problemIndex, Algorithm[] algorithm) {
		try {
			int numberOfAlgorithms = algorithmNameList_.length;
			algorithm[0] = new MOCHC_Settings(problem).configure();
		} catch (JMException ex) {
			Logger.getLogger(NSGAIIStudy.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public static void main(String[] args) throws JMException, IOException {

		if (args.length != 0) {
			System.out.println("Error. Try: MO_CHC_MKRR_1k");
			System.exit(-1);
		} // if

		Object[] params = new String[] { "Int" };

		MOCHC_MKRR_1k_Exp exp = new MOCHC_MKRR_1k_Exp();
		exp.experimentName_ = "MO_CHC_MKRR_1k";
		exp.timmingFileName_ = "TIMMINGS";
		exp.algorithmNameList_ = new String[] { "MOCHC" };
		exp.problemList_ = new String[] { 
				"scheduling.MKRR_1k_A_u_c_hihi", "scheduling.MKRR_1k_A_u_c_lohi", "scheduling.MKRR_1k_A_u_c_hilo",
				"scheduling.MKRR_1k_A_u_c_lolo", "scheduling.MKRR_1k_A_u_i_hihi", "scheduling.MKRR_1k_A_u_i_lohi",
				"scheduling.MKRR_1k_A_u_i_hilo", "scheduling.MKRR_1k_A_u_i_lolo", "scheduling.MKRR_1k_A_u_s_hihi",
				"scheduling.MKRR_1k_A_u_s_lohi", "scheduling.MKRR_1k_A_u_s_hilo", "scheduling.MKRR_1k_A_u_s_lolo",
				"scheduling.MKRR_1k_B_u_c_hihi", "scheduling.MKRR_1k_B_u_c_lohi", "scheduling.MKRR_1k_B_u_c_hilo",
				"scheduling.MKRR_1k_B_u_c_lolo", "scheduling.MKRR_1k_B_u_i_hihi", "scheduling.MKRR_1k_B_u_i_lohi",
				"scheduling.MKRR_1k_B_u_i_hilo", "scheduling.MKRR_1k_B_u_i_lolo", "scheduling.MKRR_1k_B_u_s_hihi",
				"scheduling.MKRR_1k_B_u_s_lohi", "scheduling.MKRR_1k_B_u_s_hilo", "scheduling.MKRR_1k_B_u_s_lolo"
				};
		exp.paretoFrontFile_ = new String[] { 
				"scheduling.MKRR_1k_A_u_c_hihi", "scheduling.MKRR_1k_A_u_c_lohi", "scheduling.MKRR_1k_A_u_c_hilo",
				"scheduling.MKRR_1k_A_u_c_lolo", "scheduling.MKRR_1k_A_u_i_hihi", "scheduling.MKRR_1k_A_u_i_lohi",
				"scheduling.MKRR_1k_A_u_i_hilo", "scheduling.MKRR_1k_A_u_i_lolo", "scheduling.MKRR_1k_A_u_s_hihi",
				"scheduling.MKRR_1k_A_u_s_lohi", "scheduling.MKRR_1k_A_u_s_hilo", "scheduling.MKRR_1k_A_u_s_lolo",
				"scheduling.MKRR_1k_B_u_c_hihi", "scheduling.MKRR_1k_B_u_c_lohi", "scheduling.MKRR_1k_B_u_c_hilo",
				"scheduling.MKRR_1k_B_u_c_lolo", "scheduling.MKRR_1k_B_u_i_hihi", "scheduling.MKRR_1k_B_u_i_lohi",
				"scheduling.MKRR_1k_B_u_i_hilo", "scheduling.MKRR_1k_B_u_i_lolo", "scheduling.MKRR_1k_B_u_s_hihi",
				"scheduling.MKRR_1k_B_u_s_lohi", "scheduling.MKRR_1k_B_u_s_hilo", "scheduling.MKRR_1k_B_u_s_lolo"
				};
		exp.indicatorList_ = new String[] { "HV", "SPREAD" };
		exp.experimentBaseDirectory_ = "./Results/"
				+ exp.experimentName_;
		exp.paretoFrontDirectory_ = "./Results";
		exp.instances_ = exp.numberOfInstancesDF_;

		// create the Pareto front files
		for (int i = 0; i < exp.paretoFrontFile_.length; i++) {
			File file = null;
			if (exp.instances_ != 1) {
				for (int inst = 0; inst < exp.instances_; inst++) {
					file = new File(exp.paretoFrontDirectory_ + "/"
							+ exp.paretoFrontFile_[i] + "." + inst);
					try {
						file.createNewFile();
					} catch (IOException ioe) {
						System.out
								.println("Error while creating the empty file : "
										+ exp.paretoFrontDirectory_
										+ exp.paretoFrontFile_[i] + ioe);
					}
				}

			} else {
				file = new File(exp.paretoFrontDirectory_ + "/"
						+ exp.paretoFrontFile_[i]);
				try {
					file.createNewFile();
				} catch (IOException ioe) {
					System.out.println("Error while creating the empty file : "
							+ exp.paretoFrontDirectory_
							+ exp.paretoFrontFile_[i] + ioe);
				}
			}
		}

		int numberOfAlgorithms = exp.algorithmNameList_.length;
		exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

		exp.independentRuns_ = exp.independentRunsDf_;

		// Run the experiments
		int numberOfThreads = exp.numberOfThreadsDf_;
		exp.runExperiment(numberOfThreads, params);

		// Since the true Pareto front is not known for this problem, we
		// generate it by merging all the obtained Pareto fronts in the
		// experimentation
		GeneratePareto paretoFront = new GeneratePareto(exp);
		paretoFront.run();
		paretoFront.computeQualityIndicators();

		// Generate latex tables (comment this sentence is not desired)
		exp.generateLatexTables();
	}
} // NSGAIIStudy

