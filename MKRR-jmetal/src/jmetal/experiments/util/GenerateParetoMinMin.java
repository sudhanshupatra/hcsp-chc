/**
 * generatePareto.java
 *
 * This class builds a "true" Pareto front by merging the Pareto fronts 
 *   obtained by all the compared algorithms.
 *   
 * This must be used for applying the quality indicators to problems
 *   with unknown optimal Pareto front.
 *   
 * @author Bernabe Dorronsoro
 * @version 1.0
 * 
 */

package jmetal.experiments.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.StringTokenizer;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.experiments.Experiment;
import jmetal.experiments.ExperimentNoPareto;
import jmetal.experiments.Settings;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;
import jmetal.qualityIndicator.*;
import jmetal.base.Solution;
import jmetal.base.archive.AdaptiveGridArchive;

/*
 * 
 * This class is like GeneratePareto, but in this case
 * we consider two algorithm variants. The second one is in
 * directory MinMin in every problem directory
 * 
 * */

public class GenerateParetoMinMin {

	public ExperimentNoPareto experiment_;
	// public int id_;
	public HashMap<String, Object> map_;
	// public int numberOfProblems_;

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

	Object[] params_;

	int instances_ = 1; // Number of instances to solve per problem

	  
	public GenerateParetoMinMin(ExperimentNoPareto exp) {

		experiment_ = exp;
		map_ = exp.getMap();
	}

	public void run() throws JMException, IOException {
		Algorithm[] algorithm; // jMetal algorithms that were executed

		String experimentName = (String) map_.get("name");
		experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
		algorithmNameList_ = (String[]) map_.get("algorithmNameList");
		problemList_ = (String[]) map_.get("problemList");
		indicatorList_ = (String[]) map_.get("indicatorList");
		paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
		paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
		independentRuns_ = ((Integer) map_.get("independentRuns")).intValue();
		outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
		outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");

		params_ = (Object[]) map_.get("params");

		instances_ = (Integer) map_.get("instances"); // number of different instances of the problem to run
		
		int numberOfAlgorithms = algorithmNameList_.length;

		// for every problem, merge the Pareto fronts found by all the
		// algorithms into one
		// and then print it to paretoFrontFile[problemId]
	
		for (int problemId = 0; problemId < problemList_.length; problemId++) {

			Problem problem = null;
			try {
				// Parameters of the problem
				problem = (new ProblemFactory()).getProblem(
						problemList_[problemId], params_);
			} catch (JMException ex) {
				Logger.getLogger(ExperimentNoPareto.class.getName()).log(Level.SEVERE,
						null, ex);
			}

			// Read front by front (for every algorithm and independent run) and
			// put
			// their solutions into resultFront
			
				for (int instance = 0; instance<instances_; instance++) {
					
					AdaptiveGridArchive resultFront = new AdaptiveGridArchive(100,5,problem.getNumberOfObjectives());
					
					for (int algorithmId = 0; algorithmId < algorithmNameList_.length; algorithmId++) {
					
					for (int runId = 0; runId < independentRuns_; runId++) {
	
						// Read the file and fill readFont with its values
						String directory = experimentBaseDirectory_;
						directory += "/data";
						directory += "/" + algorithmNameList_[algorithmId];
						directory += "/" + problemList_[problemId];
						if (instances_>1)
							directory += "." + instance;
						directory += "/FUN." + new Integer(runId).toString();
						
						String directoryMinMin = experimentBaseDirectory_;
						directoryMinMin += "/data";
						directoryMinMin += "/" + algorithmNameList_[algorithmId];
						directoryMinMin += "/" + problemList_[problemId];
						if (instances_>1)
							directoryMinMin += "." + instance;
						directoryMinMin += "/MinMin/FUN." + new Integer(runId).toString();
	
						// Read values from data files
						File f1 = new File(directory);
						if (f1.exists()){
							FileInputStream fis = new FileInputStream(directory);
							InputStreamReader isr = new InputStreamReader(fis);
							BufferedReader br = new BufferedReader(isr);
							System.out
									.println("Reading (for generating Pareto front): "
											+ directory);
							String aux = br.readLine();
		
							StringTokenizer st = new StringTokenizer(aux);
							int numberObjectives = st.countTokens();
							System.out.println("Number of objectives: "
									+ numberObjectives);
		
							while (aux != null) {
								Solution solution = new Solution(numberObjectives);
		
								for (int objectivesId = 0; objectivesId < numberObjectives; objectivesId++) {
									// compose the solution with the data read
									double value = new Double(st.nextToken()).doubleValue();// Double.parseDouble(aux);
		//							 double value = Double.parseDouble(aux);
									solution.setObjective(objectivesId, value);
								}
								
								// Add the solution to resultFront
								resultFront.add(solution);
		
								aux = br.readLine();
								if (aux!=null)
									st = new StringTokenizer(aux);
							} // while
						}
						else
							System.out
							.println("Skipping file: " + directory + " because it does not exist");
						
						
						// Read values from MinMin data files
						File fMinMin = new File(directoryMinMin);
						if (fMinMin.exists()){
							FileInputStream fis = new FileInputStream(directoryMinMin);
							InputStreamReader isr = new InputStreamReader(fis);
							BufferedReader br = new BufferedReader(isr);
							System.out
									.println("Reading (for generating Pareto front): "
											+ directoryMinMin);
							String aux = br.readLine();
		
							StringTokenizer st = new StringTokenizer(aux);
							int numberObjectives = st.countTokens();
							System.out.println("Number of objectives: "
									+ numberObjectives);
		
							while (aux != null) {
								Solution solution = new Solution(numberObjectives);
		
								for (int objectivesId = 0; objectivesId < numberObjectives; objectivesId++) {
									// compose the solution with the data read
									double value = new Double(st.nextToken()).doubleValue();// Double.parseDouble(aux);
		//							 double value = Double.parseDouble(aux);
									solution.setObjective(objectivesId, value);
								}
								
								// Add the solution to resultFront
								resultFront.add(solution);
		
								aux = br.readLine();
								if (aux!=null)
									st = new StringTokenizer(aux);
							} // while
						}
						else
							System.out
							.println("Skipping file: " + directoryMinMin + " because it does not exist");
						
					}
					
				} // for
					
					String file = null;
					if (instances_ > 1)
						file = paretoFrontFile_[problemId] + "." + instance;
					else
						file = paretoFrontFile_[problemId];
					
					resultFront.printObjectivesToFile(file);					
					System.out.println("Generated the pseudo-true Pareto front: " + file);
			}

			// Write resutFront as the paretoFrontFile
//			resultFront.printObjectivesToFile(paretoFrontDirectory_
//					+ paretoFrontFile_[problemId]);
//			if (instances_ == 1)
//				resultFront.printObjectivesToFile(paretoFrontFile_[problemId]);
		}

	}

	public void computeQualityIndicators() throws JMException, IOException {
		Algorithm[] algorithm; // jMetal algorithms to be executed

		String experimentName = (String) map_.get("name");
		experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
		algorithmNameList_ = (String[]) map_.get("algorithmNameList");
		problemList_ = (String[]) map_.get("problemList");
		indicatorList_ = (String[]) map_.get("indicatorList");
		paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
		paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
		independentRuns_ = (Integer) map_.get("independentRuns");
		// algorithm_ = (Algorithm[]) map_.get("algorithm");
		outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
		outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");

		params_ = (Object[]) map_.get("params");

		int numberOfAlgorithms = algorithmNameList_.length;
		System.out.println("Computing the quality indicators for every front");

		algorithm = new Algorithm[numberOfAlgorithms];

		// for every problem, algorithm and run, compute all the quality
		// measures

		// // FIRST: read the "true" composed Pareto front, called realFront
		// for (int problemId = 0; problemId < problemList_.length; problemId++)
		// {
		//			
		// }

		// SECOND: read the result of every run
		for (int problemId = 0; problemId < problemList_.length; problemId++) 
				// Read front by front (for every algorithm and independent run) and
			// compute the Quality measures
			for (int algorithmId = 0; algorithmId < algorithmNameList_.length; algorithmId++) {
				for (int inst = 0; inst < instances_; inst++) {
				for (int runId = 0; runId < independentRuns_; runId++) {

					// Read the file and fill readFont with its values
					String directory = experimentBaseDirectory_;
					directory += "/data";
					directory += "/" + algorithmNameList_[algorithmId];
					directory += "/" + problemList_[problemId];
					if (instances_ > 1)
						directory += "." + inst;
					
					String directoryMinMin = directory + "/MinMin";
					
					
					if (runId == 0) {

						for (int j = 0; j < indicatorList_.length; j++) {
							if (indicatorList_[j].equals("HV")) {
								try {
									FileWriter os;
									os = new FileWriter(directory + "/HV", false);
									os.write("");
									os.close();
									
									os = new FileWriter(directoryMinMin + "/HV", false);
									os.write("");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								}
							}
							if (indicatorList_[j].equals("SPREAD")) {
								FileWriter os = null;
								try {
									os = new FileWriter(directory + "/SPREAD", false);
									os.write("");
									os.close();
									
									os = new FileWriter(directoryMinMin + "/SPREAD", false);
									os.write("");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								} finally {
									try {
										os.close();
									} catch (IOException ex) {
										Logger.getLogger(
												ExperimentNoPareto.class
														.getName()).log(
												Level.SEVERE, null, ex);
									}
								}
							}
							if (indicatorList_[j].equals("IGD")) {
								FileWriter os = null;
								try {
									os = new FileWriter(directory + "/IGD", false);
									os.write("");
									os.close();
									
									os = new FileWriter(directoryMinMin + "/IGD", false);
									os.write("");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								} finally {
									try {
										os.close();
									} catch (IOException ex) {
										Logger.getLogger(
												ExperimentNoPareto.class
														.getName()).log(
												Level.SEVERE, null, ex);
									}
								}
							}
							if (indicatorList_[j].equals("EPSILON")) {
								FileWriter os = null;
								try {
									os = new FileWriter(directory + "/EPSILON", false);
									os.write("");
									os.close();
									
									os = new FileWriter(directoryMinMin + "/EPSILON", false);
									os.write("");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								} finally {
									try {
										os.close();
									} catch (IOException ex) {
										Logger.getLogger(
												ExperimentNoPareto.class
														.getName()).log(
												Level.SEVERE, null, ex);
									}
								}
							}
						} // for

						
					}
					
					
					directory += "/FUN." + new Integer(runId).toString();
					
					// Read the MinMin file and fill readFont with its values
//					String directoryMinMin = experimentBaseDirectory_;
//					directoryMinMin += "/data";
//					directoryMinMin += "/" + algorithmNameList_[algorithmId];
//					directoryMinMin += "/" + problemList_[problemId];
//					if (instances_ > 1)
//						directoryMinMin += "." + inst;
					
//					directoryMinMin += "/MinMin/FUN." + new Integer(runId).toString();
					directoryMinMin += "/FUN." + new Integer(runId).toString();

					SolutionSet readFront = null, readFrontMinMin = null;

					// Read values from data files
					File f1 = new File(directory);
					if (f1.exists()){
						readFront = new SolutionSet(100);
						FileInputStream fis = new FileInputStream(directory);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr);
						// System.out.println("Reading (for generating Pareto
						// front): " + directory);
						String aux = br.readLine();
	
						StringTokenizer st = new StringTokenizer(aux);
						int numberObjectives = st.countTokens();
						// System.out.println("Number of objectives: " +
						// numberObjectives);
	
						while (aux != null) {
							Solution solution = new Solution(numberObjectives);
	
							for (int objectivesId = 0; objectivesId < numberObjectives; objectivesId++) {
								// compose the solution with the data read
								double value = new Double(st.nextToken())
										.doubleValue();// Double.parseDouble(aux);
								// double value = Double.parseDouble(aux);
								solution.setObjective(objectivesId, value);
	
							}
							// Add the solution to resultFront
							readFront.add(solution);
	
							aux = br.readLine();
							if (aux != null)
								st = new StringTokenizer(aux);
						} // while
					}
					else
						System.out.println("Skipping file: " + directory + " because it does not exist");
					// The front is read from the file

					
					// Read values from data files
					File fMinMin = new File(directoryMinMin);
					if (fMinMin.exists()){
						readFrontMinMin = new SolutionSet(100);
						FileInputStream fis = new FileInputStream(directoryMinMin);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr);
						// System.out.println("Reading (for generating Pareto
						// front): " + directory);
						String aux = br.readLine();
	
						StringTokenizer st = new StringTokenizer(aux);
						int numberObjectives = st.countTokens();
						// System.out.println("Number of objectives: " +
						// numberObjectives);
	
						while (aux != null) {
							Solution solution = new Solution(numberObjectives);
	
							for (int objectivesId = 0; objectivesId < numberObjectives; objectivesId++) {
								// compose the solution with the data read
								double value = new Double(st.nextToken())
										.doubleValue();// Double.parseDouble(aux);
								// double value = Double.parseDouble(aux);
								solution.setObjective(objectivesId, value);
	
							}
							// Add the solution to resultFront
							readFrontMinMin.add(solution);
	
							aux = br.readLine();
							if (aux != null)
								st = new StringTokenizer(aux);
						} // while
					}
					else
						System.out.println("Skipping file: " + directoryMinMin + " because it does not exist");
					// The MinMin front is read from the file

					// THIRD: Compute the quality indicators of readFront with
					// respect to realFront

					Problem problem; // The problem to solve
					problem = null;
					// STEP 2: get the problem from the list
					// Object[] params = { "Real" }; // Parameters of the
					// problem
					try {
						// Parameters of the problem
						problem = (new ProblemFactory()).getProblem(
								problemList_[problemId], params_);
					} catch (JMException ex) {
						Logger.getLogger(ExperimentNoPareto.class.getName())
								.log(Level.SEVERE, null, ex);
					}

					// STEP 3: check the file containing the Pareto front of the
					// problem
					synchronized (experiment_) {
						if (indicatorList_.length > 0) {
							File pfFile = null;
							if (instances_ > 1)
								pfFile = new File(paretoFrontFile_[problemId] + "." + inst);
							else
								pfFile = new File(paretoFrontFile_[problemId]);
							
							if (!pfFile.exists()) {
								paretoFrontFile_[problemId] = "";
							}
						} // if
					}

					// STEP4
					experiment_
							.algorithmSettings(problem, problemId, algorithm);
					if (indicatorList_.length > 0) {
						QualityIndicator indicators;
						// System.out.println("PF file: " +
						// paretoFrontFile_[problemId]);
						
						if (instances_ > 1)
							indicators = new QualityIndicator(problem,
									paretoFrontFile_[problemId] + "." + inst);
						else
							indicators = new QualityIndicator(problem,
								paretoFrontFile_[problemId]);

						directory = experimentBaseDirectory_ + "/data/"
								+ algorithmNameList_[algorithmId] + "/"
								+ problemList_[problemId];
						
						if (instances_ > 1)
							directory += "." + inst;

						directoryMinMin = directory + "/MinMin";
						
						File experimentDirectory = new File(directory);
						File experimentDirectoryMinMin = new File(directoryMinMin);

						for (int j = 0; j < indicatorList_.length; j++) {
							if (indicatorList_[j].equals("HV")) {
								
								try {
									FileWriter os;
									if (readFront != null) {
										double value = indicators
												.getHypervolume(readFront);
										
										os = new FileWriter(experimentDirectory
												+ "/HV", true);
										os.write("" + value + "\n");
										os.close();
									}
									if (readFrontMinMin!=null) {
										double valueMinMin = indicators
										.getHypervolume(readFrontMinMin);
										os = new FileWriter(experimentDirectoryMinMin
												+ "/HV", true);
										os.write("" + valueMinMin + "\n");
										os.close();
									}
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								}
							}
							if (indicatorList_[j].equals("SPREAD")) {
								FileWriter os = null;
								try {
									if (readFront != null) {
										double value = indicators
												.getSpread(readFront);
										os = new FileWriter(experimentDirectory
												+ "/SPREAD", true);
										os.write("" + value + "\n");
										os.close();
									}
									if (readFrontMinMin != null) {
										double valueMinMin = indicators
												.getSpread(readFrontMinMin);
										os = new FileWriter(experimentDirectoryMinMin
												+ "/SPREAD", true);
										os.write("" + valueMinMin + "\n");
										os.close();
									}
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								} 
//								finally {
//									try {
//										os.close();
//									} catch (IOException ex) {
//										Logger.getLogger(
//												ExperimentNoPareto.class
//														.getName()).log(
//												Level.SEVERE, null, ex);
//									}
//								}
							}
							if (indicatorList_[j].equals("IGD")) {
								FileWriter os = null;
								try {
									if (readFront != null) {
										double value = indicators.getIGD(readFront);
										os = new FileWriter(experimentDirectory
												+ "/IGD", true);
										os.write("" + value + "\n");
										os.close();
									}
									if (readFrontMinMin != null) {
										double valueMinMin = indicators.getIGD(readFrontMinMin);
										os = new FileWriter(experimentDirectoryMinMin
												+ "/IGD", true);
										os.write("" + valueMinMin + "\n");
										os.close();
									}
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								} 
//								finally {
//									try {
//										os.close();
//									} catch (IOException ex) {
//										Logger.getLogger(
//												ExperimentNoPareto.class
//														.getName()).log(
//												Level.SEVERE, null, ex);
//									}
//								}
							}
							if (indicatorList_[j].equals("EPSILON")) {
								FileWriter os = null;
								try {
									if (readFront != null) {
										double value = indicators
												.getEpsilon(readFront);
										os = new FileWriter(experimentDirectory
												+ "/EPSILON", true);
										os.write("" + value + "\n");
										os.close();
									}
									if (readFrontMinMin != null) {
										double valueMinMin = indicators
												.getEpsilon(readFrontMinMin);
										os = new FileWriter(experimentDirectoryMinMin
												+ "/EPSILON", true);
										os.write("" + valueMinMin + "\n");
										os.close();
									}
								} catch (IOException ex) {
									Logger.getLogger(
											ExperimentNoPareto.class.getName())
											.log(Level.SEVERE, null, ex);
								} 
//								finally {
//									try {
//										os.close();
//									} catch (IOException ex) {
//										Logger.getLogger(
//												ExperimentNoPareto.class
//														.getName()).log(
//												Level.SEVERE, null, ex);
//									}
//								}
							}
						} // for
					} // if
				}
			}

		} // for
	}

}
