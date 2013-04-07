/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmetal.experiments.util;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.experiments.ExperimentConvStudy;
import jmetal.experiments.Settings;
import jmetal.problems.ProblemFactory;
import jmetal.problems.scheduling.u_i_hihi;
import jmetal.util.JMException;
import jmetal.qualityIndicator.*;
import jmetal.coevolutionary.util.TimeEstimation;
import jmetal.metaheuristics.mocell.*;
import jmetal.metaheuristics.nsgaII.*;
import jmetal.metaheuristics.moead.*;
import jmetal.metaheuristics.ibea.*;
import jmetal.metaheuristics.abyss.*;
import jmetal.metaheuristics.cellde.*;
import jmetal.metaheuristics.densea.*;
import jmetal.metaheuristics.fastPGA.*;
import jmetal.metaheuristics.gde3.*;
import jmetal.metaheuristics.mochc.*;
import jmetal.metaheuristics.omopso.*;
import jmetal.metaheuristics.paes.*;
import jmetal.metaheuristics.pesaII.*;
import jmetal.metaheuristics.smpso.*;
import jmetal.metaheuristics.spea2.*;

/**
 * 
 * @author Bernabé Dorronsoro
 * 
 *         This class writes to a file the evolution of the convergence of the
 *         Pareto front in terms of the given quality indicators.
 * 
 *         The values on the files are averaged over the number of runs
 * 
 */
public class runExperimentConvStudy extends Thread {

	public ExperimentConvStudy experiment_;
	public int id_;
	public HashMap<String, Object> map_;
	public int numberOfThreads_;
	public int numberOfProblems_;

	int first_;
	int last_;

	String experimentName_;
	String[] algorithmNameList_; // List of the names of the algorithms to be
	// executed
	String[] problemList_; // List of problems to be solved
	String[] paretoFrontFile_; // List of the files containing the pareto
	// fronts
	// corresponding to the problems in problemList_
	String[] indicatorList_; // List of the quality indicators to be applied
	String experimentBaseDirectory_; // Directory to store the results
	String latexDirectory_; // Directory to store the latex files
	String rDirectory_; // Directory to store the generated R scripts
	String paretoFrontDirectory_; // Directory containing the Pareto front
	// files
	String outputParetoFrontFile_; // Name of the file containing the output
	// Pareto front
	String outputParetoSetFile_; // Name of the file containing the output
	// Pareto set
	int independentRuns_; // Number of independent runs per algorithm
	Settings[] algorithmSettings_; // Paremeter settings of each algorithm
	Object[] params_ = { "Real" }; // Parameters of the problem by default

	Algorithm[] algorithm_;

	Integer instances_; // number of different instances of the problem to run

	TimeEstimation time_ = null; // For computing the run time and the run time
	// left
	long[] accTime_ = null;

	public runExperimentConvStudy(ExperimentConvStudy experiment,
			HashMap<String, Object> map, int id, int numberOfThreads,
			int numberOfProblems) {
		experiment_ = experiment;
		id_ = id;
		map_ = map;
		numberOfThreads_ = numberOfThreads;
		numberOfProblems_ = numberOfProblems;

		int partitions = numberOfProblems / numberOfThreads;

		first_ = partitions * id;
		if (id == (numberOfThreads - 1)) {
			last_ = numberOfProblems - 1;
		} else {
			last_ = first_ + partitions - 1;
		}

		System.out.println("Id: " + id + "  Partitions: " + partitions
				+ " First: " + first_ + " Last: " + last_);
	}

	// TODO: Cambiar esta función para que no haga las estadísticas del final
	// Luego, después de generar el frente de pareto, habrá que sacar las
	// estadísticas
	// de todas las ejecuciones.

	public void run() {
		Algorithm[] algorithm; // jMetal algorithms to be executed

		String experimentName = (String) map_.get("name");
		experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
		algorithmNameList_ = (String[]) map_.get("algorithmNameList");
		problemList_ = (String[]) map_.get("problemList");
		indicatorList_ = (String[]) map_.get("indicatorList");
		paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
		paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
		independentRuns_ = (Integer) map_.get("independentRuns");
		algorithm_ = (Algorithm[]) map_.get("algorithm");
		outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
		outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");

		instances_ = (Integer) map_.get("instances"); // number of different instances of the problem to run
		// Added to allow executing several instances per problem class
		time_ = (TimeEstimation) map_.get("timeEstimation"); // For computing the run time and the run time left

		String timmingFileName_ = (String) map_.get("timmingFileName");

		int instanceCounter = 0; // To keep track of the instance to solve

		int numberOfAlgorithms = algorithmNameList_.length;
		System.out.println("Experiment: Number of algorithms: "
				+ numberOfAlgorithms);
		System.out.println("Experiment: runs: " + independentRuns_);
		Algorithm a  = new aMOCell4SchedConv(null); // To avoid a debugger bug, since it cannot load the class in the following line if I don't do this
		algorithm = new Algorithm[numberOfAlgorithms];
		//algorithm = new aMOCell4SchedConv[numberOfAlgorithms];
//		aMOCell4SchedConv[] a = new aMOCell4SchedConv[1];
//		algorithm = a;

		System.out.println("Name: " + experimentName);
		System.out.println("experimentDirectory: " + experimentBaseDirectory_);
		System.out.println("numberOfThreads_: " + numberOfThreads_);
		System.out.println("numberOfProblems_: " + numberOfProblems_);
		System.out.println("numberOfInstancesPerProblems_: " + instances_);
		System.out.println("first: " + first_);
		System.out.println("last: " + last_);

		SolutionSet resultFront = null;

		double[][] timmings = new double[problemList_.length ][ numberOfAlgorithms ];

		int maxEvaluations = 500000;
		int popSize = 100;
		//int generations = ((Integer)algorithm[0].getInputParameter("maxEvaluations")).intValue() / ((Integer)algorithm[0].getInputParameter("populationSize")).intValue();
		int generations = maxEvaluations / popSize;
		double[][][][][] metrics = new double[problemList_.length ][ instances_ ][ numberOfAlgorithms ][ indicatorList_.length ][ generations ];

		for (int prob = 0; prob < problemList_.length; prob++)
			for (int inst = 0; inst < instances_; inst++)
				for (int alg = 0; alg < numberOfAlgorithms; alg++)
					for (int ind = 0; ind < indicatorList_.length; ind++)
						for (int gen = 0; gen < generations; gen++)
							metrics[prob][inst][alg][ind][gen] = 0.0;
		
		for (int problemId = first_; problemId <= last_; problemId++) {

			//long[] accTime = new long[algorithmNameList_.length*instances_.intValue()];
			for (int i=0; i<numberOfAlgorithms; i++)
				timmings[problemId][i] = 0;

			for (int inst = 0; inst < instances_.intValue(); inst++)
			{
				if (instances_ > 1)
				{
					Object ob = params_[0];
					params_ = new Object[2];
					params_[0] = ob;
					params_[1] = new Integer(inst);
				}

				Problem problem; // The problem to solve

				problem = null;
				// STEP 2: get the problem from the list
				//			Object[] params = { "Real" }; // Parameters of the problem
				try {
					// Parameters of the problem
					problem = (new ProblemFactory()).getProblem(
							problemList_[problemId], params_);
				} catch (JMException ex) {
					Logger.getLogger(ExperimentConvStudy.class.getName()).log(Level.SEVERE,
							null, ex);
				}

				// STEP 3: check the file containing the Pareto front of the problem
				synchronized (experiment_) {
					if (indicatorList_.length > 0) {
						File pfFile = null;
						if (instances_ > 1)
							pfFile = new File(paretoFrontDirectory_ + "/"
									+ paretoFrontFile_[problemId] + "." + inst);
						else
							pfFile = new File(paretoFrontDirectory_ + "/"
									+ paretoFrontFile_[problemId]);

						//					pfFile = new File(paretoFrontFile_[problemId]);

						if (!pfFile.exists()) {
							try {
								FileReader f = new FileReader("nonexists"); // provoke an error 
								f.read();
							} catch (IOException ex){
								Logger.getLogger(ExperimentConvStudy.class.getName()).log(Level.SEVERE,
										"Error: The Pareto file does not exist: " + paretoFrontFile_[problemId], ex);
							}
							//							
							//	//						if (instances_ > 1)
							//	//							paretoFrontFile_[problemId] = paretoFrontDirectory_
							//	//								+ "/" + paretoFrontFile_[problemId] + "." + inst;
							//	//						else
							//	//							paretoFrontFile_[problemId] = paretoFrontDirectory_
							//	//								+ "/" + paretoFrontFile_[problemId];
							//	//					} else {
							//							// CREATE IT!
							//							try{
							//								pfFile.createNewFile();
							//							} catch (IOException ex){
							//	//							Logger.getLogger(ExperimentNoPareto.class.getName()).log(Level.SEVERE,
							//	//									null, ex);
							//								Logger.getLogger(ExperimentNoPareto.class.getName()).log(Level.SEVERE,
							//										"Error creating file: " + paretoFrontFile_[problemId], ex);
							//							}
							//							
							//							//paretoFrontFile_[problemId] = "";
							//						}
						} // if
					}

					experiment_.algorithmSettings(problem, problemId, algorithm);

					for (int runs = 0; runs < independentRuns_; runs++) {
						System.out.println("Iruns: " + runs);
						// STEP 4: configure the algorithms

						// STEP 5: run the algorithms
						for (int i = 0; i < numberOfAlgorithms; i++) {
							System.out.println(algorithm[i].getClass());
							// STEP 6: create output directories
							File experimentDirectory;
							String directory;

							if (instances_ > 1)
								directory = experimentBaseDirectory_ + "/data/"
								+ algorithmNameList_[i] + "/"
								+ problemList_[problemId] + "." + inst;
							else
								directory = experimentBaseDirectory_ + "/data/"
								+ algorithmNameList_[i] + "/"
								+ problemList_[problemId];

							experimentDirectory = new File(directory);
							if (!experimentDirectory.exists()) {
								boolean result = new File(directory).mkdirs();
								System.out.println("Creating " + directory);
							}

							// STEP 7: run the algorithm
							long startTime = 0, endTime = 0;

							if (instances_ > 1)
								System.out.println("Running algorithm: "
										+ algorithmNameList_[i] + ", problem: "
										+ problemList_[problemId] + ", run: " + runs + ", instance: " + inst);
							else
								System.out.println("Running algorithm: "
										+ algorithmNameList_[i] + ", problem: "
										+ problemList_[problemId] + ", run: " + runs);
							try {
								String paretoFrontDir  = paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId];
								if (instances_ > 1)
									paretoFrontDir +=  "." + inst;
								
								startTime = System.currentTimeMillis();
								if (algorithmNameList_[i].equalsIgnoreCase("aMOCell4SchedConv"))
									//resultFront = ((aMOCell4SchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontFile_[problemId],directory);
									resultFront = ((aMOCell4SchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontDir,directory);
								else if (algorithmNameList_[i].equalsIgnoreCase("NSGAIISchedConv"))
									//resultFront = ((NSGAIISchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontFile_[problemId],directory);
									resultFront = ((NSGAIISchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontDir,directory);
								else if (algorithmNameList_[i].equalsIgnoreCase("IBEASchedConv"))
									//resultFront = ((IBEASchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontFile_[problemId],directory);
									resultFront = ((IBEASchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontDir,directory);
								else if (algorithmNameList_[i].equalsIgnoreCase("MOEADSchedConv"))
									//resultFront = ((MOEADSchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontFile_[problemId],directory);
									resultFront = ((MOEADSchedConv)algorithm[i]).execute(metrics,problemId,inst,i,indicatorList_,paretoFrontDir,directory);
								
								endTime   = System.currentTimeMillis();
							} catch (JMException ex) {
								Logger.getLogger(ExperimentConvStudy.class.getName()).log(
										Level.SEVERE, null, ex);
							}

							time_.iteration();
							System.out.print(resultFront.size() + " sols found, " + (endTime-startTime) +
									" ms. (" + time_.getPercentageDone() + "% done, ");
							System.out.println( time_.getRemainingHumanReadable() + " remaining)");
							//accTime_[i] += (endTime-startTime);
							timmings[problemId][i] += (endTime-startTime);

							// STEP 8: put the results in the output directory
							resultFront.printObjectivesToFile(directory + "/"
									+ outputParetoFrontFile_ + "." + runs);
							resultFront.printVariablesToFile(directory + "/"
									+ outputParetoSetFile_ + "." + runs);

							// Print the run time after every execution
							try{
								FileWriter os = null;
								if (instances_ > 1)
									os = new FileWriter( experimentBaseDirectory_ + "/data/" + algorithmNameList_[i] + "/" + problemList_[problemId] + "." + inst + "/" + timmingFileName_ + ".AVG", true);
								else 
									os = new FileWriter( experimentBaseDirectory_ + "/data/" + algorithmNameList_[i] + "/" + problemList_[problemId] + "/" + timmingFileName_ + ".AVG", true);

								os.write( "" + (endTime-startTime) + "\n" );
								os.close();
								//timmings[problemId][i]=(accTime[i]/independentRuns_);
							}
							catch (IOException ex){
								Logger.getLogger(ExperimentConvStudy.class.getName()).log(Level.SEVERE, null, ex);
							}


							// STEP 9: calculate quality indicators
							if (indicatorList_.length > 0) {
								QualityIndicator indicators;
								// System.out.println("PF file: " +
								// paretoFrontFile_[problemId]);
								
//								if (instances_ > 1)
//									indicators = new QualityIndicator(problem,
//											paretoFrontFile_[problemId] + "." + inst);
//								else
//									indicators = new QualityIndicator(problem,
//										paretoFrontFile_[problemId]);
								
								String paretoFrontDir  = paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId];
								if (instances_ > 1)
									paretoFrontDir +=  "." + inst;
								
								indicators = new QualityIndicator(problem, paretoFrontDir);

//								directory = experimentBaseDirectory_ + "/data/"
//										+ algorithmNameList_[i] + "/"
//										+ problemList_[problemId];
//								
//								if (instances_ > 1)
//									directory += "." + inst;

								//File experimentDirectory = new File(directory);

								for (int j = 0; j < indicatorList_.length; j++) {
									if (indicatorList_[j].equals("HVNonNormalized")) {
										double value = indicators
												.getHypervolume(resultFront);
										FileWriter os;
										try {
											os = new FileWriter(experimentDirectory
													+ "/HVNonNormalized", true);
											os.write("" + value + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										}
									}
									if (indicatorList_[j].equals("SPREADNonNormalized")) {
										FileWriter os = null;
										try {
											double value = indicators
													.getSpread(resultFront);
											os = new FileWriter(experimentDirectory
													+ "/SPREADNonNormalized", true);
											os.write("" + value + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										} finally {
											try {
												os.close();
											} catch (IOException ex) {
												Logger.getLogger(
														ExperimentConvStudy.class
																.getName()).log(
														Level.SEVERE, null, ex);
											}
										}
									}
									if (indicatorList_[j].equals("IGDNonNormalized")) {
										FileWriter os = null;
										try {
											double value = indicators.getIGD(resultFront);
											os = new FileWriter(experimentDirectory
													+ "/IGDNonNormalized", true);
											os.write("" + value + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										} finally {
											try {
												os.close();
											} catch (IOException ex) {
												Logger.getLogger(
														ExperimentConvStudy.class
																.getName()).log(
														Level.SEVERE, null, ex);
											}
										}
									}
									if (indicatorList_[j].equals("EPSILON")) {
										FileWriter os = null;
										try {
											double value = indicators
													.getEpsilon(resultFront);
											os = new FileWriter(experimentDirectory
													+ "/EPSILON", true);
											os.write("" + value + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										} finally {
											try {
												os.close();
											} catch (IOException ex) {
												Logger.getLogger(
														ExperimentConvStudy.class
																.getName()).log(
														Level.SEVERE, null, ex);
											}
										}
									}
								} // for
							} // if
						} // for
					} // for
				} // for
			}
			
				// Print the computational time
				// STEP 11: Write The run times in a latex file
				try{
					for( int i=0 ; i<numberOfAlgorithms ; ++i ) {
						FileWriter os = new FileWriter( experimentBaseDirectory_ + "/data/" + algorithmNameList_[i] + "/" + problemList_[problemId] + ".AvgTime" );

						//os.write( "" + (accTime[i]/independentRuns_) + "\n" );
						os.write( "" + (timmings[problemId][i]/(independentRuns_*instances_)) + "\n" );
						os.close();
						//timmings[problemId][i]=(accTime[i]/independentRuns_);
						timmings[problemId][i]=(timmings[problemId][i]/(independentRuns_*instances_));
					} // for
				}
				catch (IOException ex){
					Logger.getLogger(ExperimentConvStudy.class.getName()).log(Level.SEVERE, null, ex);
				}

			} // for
		
		// FINAL STEP: print the convergence speed of the front in terms of the quality indicators
		for (int prob = 0; prob < problemList_.length; prob++)
			for (int inst = 0; inst < instances_; inst++)
				for (int alg = 0; alg < numberOfAlgorithms; alg++)
					for (int ind = 0; ind < indicatorList_.length; ind++)
						for (int gen = 0; gen < generations; gen++) {
							
							String directory = experimentBaseDirectory_ + "/data/"
							+ algorithmNameList_[alg] + "/"
							+ problemList_[prob];
							
							if (instances_ > 1)
								directory += "." + inst;
							
							directory += "/ConvergenceData/";

							File experimentDirectory = new File(directory);
							if (!experimentDirectory.exists()) {
								boolean result = new File(directory).mkdirs();
								System.out.println("Creating " + directory + " for printing the convergence speed study");
							}

							metrics[prob][inst][alg][ind][gen] /= (double)independentRuns_ ;
							
							if (indicatorList_.length > 0) {
								QualityIndicator indicators;
								// System.out.println("PF file: " +
								// paretoFrontFile_[problemId]);
								
								if (instances_ > 1)
								{
									Object ob = params_[0];
									params_ = new Object[2];
									params_[0] = ob;
									params_[1] = new Integer(inst);
								}

								Problem problem; // The problem to solve

								problem = null;
								// get the problem from the list
								//			Object[] params = { "Real" }; // Parameters of the problem
								try {
									// Parameters of the problem
									problem = (new ProblemFactory()).getProblem(
											problemList_[prob], params_);
								} catch (JMException ex) {
									Logger.getLogger(ExperimentConvStudy.class.getName()).log(Level.SEVERE,
											null, ex);
								}
								
//								if (instances_ > 1)
//									indicators = new QualityIndicator(problem,
//											paretoFrontFile_[prob] + "." + inst);
//								else
//									indicators = new QualityIndicator(problem,
//										paretoFrontFile_[prob]);
								
								String paretoFrontDir  = paretoFrontDirectory_ + "/" + paretoFrontFile_[prob];
								if (instances_ > 1)
									paretoFrontDir +=  "." + inst;
								
								//indicators = new QualityIndicator(problem, paretoFrontDir);

								FileWriter os = null;
								
								//for (int j = 0; j < indicatorList_.length; j++) {
									if (indicatorList_[ind].equals("HVNonNormalized")) {
										try {
											os = new FileWriter(experimentDirectory
													+ "/ConvHVNonNormalized", true);
											
											os.write("" + metrics[prob][inst][alg][ind][gen] + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										}
									}
									if (indicatorList_[ind].equals("SPREADNonNormalized")) {
										try {
											os = new FileWriter(experimentDirectory
													+ "/ConvSPREADNonNormalized", true);
											os.write("" + metrics[prob][inst][alg][ind][gen] + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										} finally {
											try {
												os.close();
											} catch (IOException ex) {
												Logger.getLogger(
														ExperimentConvStudy.class
														.getName()).log(
																Level.SEVERE, null, ex);
											}
										}
									}
									if (indicatorList_[ind].equals("IGDNonNormalized")) {
										try {
											os = new FileWriter(experimentDirectory
													+ "/ConvIGDNonNormalized", true);
											os.write("" + metrics[prob][inst][alg][ind][gen] + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										} finally {
											try {
												os.close();
											} catch (IOException ex) {
												Logger.getLogger(
														ExperimentConvStudy.class
														.getName()).log(
																Level.SEVERE, null, ex);
											}
										}
									}
									if (indicatorList_[ind].equals("EPSILON")) {
										try {
											os = new FileWriter(experimentDirectory
													+ "/ConvEPSILON", true);
											os.write("" + metrics[prob][inst][alg][ind][gen] + "\n");
											os.close();
										} catch (IOException ex) {
											Logger.getLogger(
													ExperimentConvStudy.class.getName())
													.log(Level.SEVERE, null, ex);
										} finally {
											try {
												os.close();
											} catch (IOException ex) {
												Logger.getLogger(
														ExperimentConvStudy.class
														.getName()).log(
																Level.SEVERE, null, ex);
											}
										}
									}
								//} // for
							} // if
							
						}
		
		
		}

		/**
		 * Made by Bernabe Dorronsoro This function was created in order to allow
		 * passing specific params to the problem class
		 * 
		 * @param params
		 */
		// public void run() {
		//
		// Algorithm[] algorithm; // jMetal algorithms to be executed
		//
		// String experimentName = (String) map_.get("name");
		// experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
		// algorithmNameList_ = (String[]) map_.get("algorithmNameList");
		// problemList_ = (String[]) map_.get("problemList");
		// indicatorList_ = (String[]) map_.get("indicatorList");
		// paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
		// paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
		// independentRuns_ = (Integer) map_.get("independentRuns");
		// // algorithm_ = (Algorithm[]) map_.get("algorithm");
		// outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
		// outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");
		//
		// int numberOfAlgorithms = algorithmNameList_.length;
		// System.out.println("Experiment: Number of algorithms: "
		// + numberOfAlgorithms);
		// System.out.println("Experiment: runs: " + independentRuns_);
		// algorithm = new Algorithm[numberOfAlgorithms];
		//
		// System.out.println("Nombre: " + experimentName);
		// System.out.println("experimentDirectory: " + experimentBaseDirectory_);
		// System.out.println("numberOfThreads_: " + numberOfThreads_);
		// System.out.println("numberOfProblems_: " + numberOfProblems_);
		// System.out.println("first: " + first_);
		// System.out.println("last: " + last_);
		//
		// SolutionSet resultFront = null;
		//
		// for (int problemId = first_; problemId <= last_; problemId++) {
		// Problem problem; // The problem to solve
		//
		// problem = null;
		// // STEP 2: get the problem from the list
		// // Object[] params = {"Real"}; // Parameters of the problem
		//
		// try {
		// // Parameters of the problem
		// problem = (new ProblemFactory()).getProblem(
		// problemList_[problemId], params);
		// } catch (JMException ex) {
		// Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE,
		// null, ex);
		// }
		//
		// // STEP 3: check the file containing the Pareto front of the problem
		// synchronized (experiment_) {
		// if (indicatorList_.length > 0) {
		// File pfFile = new File(paretoFrontDirectory_ + "/"
		// + paretoFrontFile_[problemId]);
		// if (pfFile.exists()) {
		// paretoFrontFile_[problemId] = paretoFrontDirectory_
		// + "/" + paretoFrontFile_[problemId];
		// } else {
		// paretoFrontFile_[problemId] = "";
		// }
		// } // if
		// }
		// experiment_.algorithmSettings(problem, problemId, algorithm);
		// for (int runs = 0; runs < independentRuns_; runs++) {
		// System.out.println("Iruns: " + runs);
		// // STEP 4: configure the algorithms
		//
		// // STEP 5: run the algorithms
		// for (int i = 0; i < numberOfAlgorithms; i++) {
		// System.out.println(algorithm[i].getClass());
		// // STEP 6: create output directories
		// File experimentDirectory;
		// String directory;
		//
		// directory = experimentBaseDirectory_ + "/data/"
		// + algorithmNameList_[i] + "/"
		// + problemList_[problemId];
		//
		// experimentDirectory = new File(directory);
		// if (!experimentDirectory.exists()) {
		// boolean result = new File(directory).mkdirs();
		// System.out.println("Creating " + directory);
		// }
		//
		// // STEP 7: run the algorithm
		// System.out.println("Running algorithm: "
		// + algorithmNameList_[i] + ", problem: "
		// + problemList_[problemId] + ", run: " + runs);
		// try {
		// resultFront = algorithm[i].execute();
		// } catch (JMException ex) {
		// Logger.getLogger(Experiment.class.getName()).log(
		// Level.SEVERE, null, ex);
		// }
		//
		// // STEP 8: put the results in the output directory
		// resultFront.printObjectivesToFile(directory + "/"
		// + outputParetoFrontFile_ + "." + runs);
		// resultFront.printVariablesToFile(directory + "/"
		// + outputParetoSetFile_ + "." + runs);
		//
		// // STEP 9: calculate quality indicators
		// if (indicatorList_.length > 0) {
		// QualityIndicator indicators;
		// // System.out.println("PF file: " +
		// // paretoFrontFile_[problemId]);
		// indicators = new QualityIndicator(problem,
		// paretoFrontFile_[problemId]);
		//
		// for (int j = 0; j < indicatorList_.length; j++) {
		// if (indicatorList_[j].equals("HV")) {
		// double value = indicators
		// .getHypervolume(resultFront);
		// FileWriter os;
		// try {
		// os = new FileWriter(experimentDirectory
		// + "/HV", true);
		// os.write("" + value + "\n");
		// os.close();
		// } catch (IOException ex) {
		// Logger
		// .getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// }
		// }
		// if (indicatorList_[j].equals("SPREAD")) {
		// FileWriter os = null;
		// try {
		// double value = indicators
		// .getSpread(resultFront);
		// os = new FileWriter(experimentDirectory
		// + "/SPREAD", true);
		// os.write("" + value + "\n");
		// os.close();
		// } catch (IOException ex) {
		// Logger
		// .getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// } finally {
		// try {
		// os.close();
		// } catch (IOException ex) {
		// Logger.getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// }
		// }
		// }
		// if (indicatorList_[j].equals("IGD")) {
		// FileWriter os = null;
		// try {
		// double value = indicators
		// .getIGD(resultFront);
		// os = new FileWriter(experimentDirectory
		// + "/IGD", true);
		// os.write("" + value + "\n");
		// os.close();
		// } catch (IOException ex) {
		// Logger
		// .getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// } finally {
		// try {
		// os.close();
		// } catch (IOException ex) {
		// Logger.getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// }
		// }
		// }
		// if (indicatorList_[j].equals("EPSILON")) {
		// FileWriter os = null;
		// try {
		// double value = indicators
		// .getEpsilon(resultFront);
		// os = new FileWriter(experimentDirectory
		// + "/EPSILON", true);
		// os.write("" + value + "\n");
		// os.close();
		// } catch (IOException ex) {
		// Logger
		// .getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// } finally {
		// try {
		// os.close();
		// } catch (IOException ex) {
		// Logger.getLogger(
		// Experiment.class.getName())
		// .log(Level.SEVERE, null, ex);
		// }
		// }
		// }
		// } // for
		// } // if
		// } // for
		// } // for
		// } // for
		// }

		public runExperimentConvStudy(ExperimentConvStudy experiment,
				HashMap<String, Object> map, int id, int numberOfThreads,
				int numberOfProblems, Object[] params) {
			experiment_ = experiment;
			id_ = id;
			map_ = map;
			numberOfThreads_ = numberOfThreads;
			numberOfProblems_ = numberOfProblems;
			params_ = params;

			int partitions = numberOfProblems / numberOfThreads;

			first_ = partitions * id;
			if (id == (numberOfThreads - 1)) {
				last_ = numberOfProblems - 1;
			} else {
				last_ = first_ + partitions - 1;
			}

			System.out.println("Id: " + id + "  Partitions: " + partitions
					+ " First: " + first_ + " Last: " + last_);
		}
	}
