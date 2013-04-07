/**
 * aMOCell4SchedConv.java
 *
 * @author Bernabe Dorronsoro
 * @version 1.0
 *
 */
package jmetal.metaheuristics.mocell;

import jmetal.base.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.base.archive.CrowdingArchive;
import jmetal.base.operator.comparator.*;
import jmetal.coevolutionary.util.ScheduleStrategy;
import jmetal.experiments.ExperimentNoPareto;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.*;

/** 
 * Class representing de MoCell algorithm
 * 
 * This class writes to a file the evolution of 
 * the convergence of the Pareto front in terms
 * of the given quality indicators.
 * 
 * The values on the files are averaged over the
 * number of runs
 * 
 */
public class aMOCell4SchedConv extends Algorithm{

  //->fields
  private Problem problem_;          //The problem to solve        

  public aMOCell4SchedConv(Problem problem){
    problem_ = problem;
  }

  public SolutionSet execute()
  {
	System.out.println("Error in aMOCell4SchedConv: for the convergence study, the method execute with no parameters cannot be called");
	return null;
  }
  
  /** Execute the algorithm 
   * @throws JMException */
  public SolutionSet execute(double [][][][][] metrics, int problemID, int inst, int alg, String[] indicatorList, String paretoFrontFile, String experimentDirectory) throws JMException {
    //Init the param
    int populationSize, archiveSize, maxEvaluations, evaluations;
    Operator mutationOperator, crossoverOperator, selectionOperator;
    SolutionSet currentPopulation;
    CrowdingArchive archive;
    SolutionSet [] neighbors;    
    Neighborhood neighborhood;
    Comparator dominance = new DominanceComparator();  
    Comparator crowdingComparator = new CrowdingComparator();
    Distance distance = new Distance();
     
    //Init the param
    //Read the params
    populationSize    = ((Integer)getInputParameter("populationSize")).intValue();
    archiveSize       = ((Integer)getInputParameter("archiveSize")).intValue();
    maxEvaluations    = ((Integer)getInputParameter("maxEvaluations")).intValue();                                

    //Read the operators
    mutationOperator  = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");        

    //Init the variables    
    currentPopulation  = new SolutionSet(populationSize);        
    archive            = new CrowdingArchive(archiveSize,problem_.getNumberOfObjectives());                
    evaluations        = 0;                        
    neighborhood       = new Neighborhood(populationSize);
    neighbors          = new SolutionSet[populationSize];

    //Create the initial population
    Solution newSolution = null;
	String specialSolution         = ((String) getInputParameter( "specialSolution" ));
	
	int minminInit = PseudoRandom.randInt(0, populationSize-1); // To initialize one individual with min-min
	
	for (int i = 0; i < populationSize; i++){
		if ( specialSolution == null ){
    		newSolution = new Solution(problem_);                    
    	} // if
		else if (specialSolution.contains("OneMinmin")) {
			if (minminInit==i) {
				//int [] vars = ScheduleStrategy.minMin(ETC_, numberOfTasks, numberOfMachines)
				DecisionVariables specialDV   = problem_.generateSpecial( specialSolution );
	    		newSolution = new Solution( problem_ , specialDV );
			}
			else
				newSolution = new Solution(problem_);
			}
    	else if (specialSolution.equalsIgnoreCase("Min-Min")){
    		DecisionVariables specialDV   = problem_.generateSpecial( specialSolution );
    		newSolution = new Solution( problem_ , specialDV );
    	} // else
		problem_.evaluate( newSolution );
		problem_.evaluateConstraints( newSolution );
		currentPopulation.add( newSolution );
		newSolution.setLocation(i);
		evaluations++;
	} // for
	
//	Print the initial population
//	currentPopulation.printObjectivesToFile("./InitialPopulation.fun");
	
	// Estaba as’
//	for (int i = 0; i < populationSize; i++){
//      Solution individual = new Solution(problem_);
//      problem_.evaluate(individual);           
//      problem_.evaluateConstraints(individual);
//      currentPopulation.add(individual);
//      individual.setLocation(i);
//      evaluations++;
//    }


	QualityIndicator indicators = null;
	if (indicatorList.length > 0) {
		// System.out.println("PF file: " +
		// paretoFrontFile_[problemId]);
		indicators = new QualityIndicator(problem_,
				paretoFrontFile, false);
	}
	
	int generations = 0;
	
	while (evaluations < maxEvaluations){                                 
      for (int ind = 0; ind < currentPopulation.size(); ind++){
        Solution individual = new Solution(currentPopulation.get(ind));

        Solution [] parents = new Solution[2];
        Solution [] offSpring;

        //neighbors[ind] = neighborhood.getFourNeighbors(currentPopulation,ind);
        neighbors[ind] = neighborhood.getEightNeighbors(currentPopulation,ind);                                                           
        neighbors[ind].add(individual);

        // parents
         parents[0] = (Solution)selectionOperator.execute(neighbors[ind]); // ESTABA ASI
//        parents[0] = (Solution)((Object[])selectionOperator.execute(neighbors[ind]))[0]; // For TournamentFour
        if (archive.size() > 0) {
          parents[1] = (Solution)selectionOperator.execute(archive);  // ESTABA ASI
//        	parents[1] = (Solution)((Object[])selectionOperator.execute(archive))[0]; // For TournamentFour
        } else {                   
          parents[1] = (Solution)selectionOperator.execute(neighbors[ind]); // ESTABA ASI
//        	parents[1] = (Solution)((Object[])selectionOperator.execute(neighbors[ind]))[0]; // For TournamentFour
        }

        // Create a new individual, using genetic operators mutation and crossover
        offSpring = (Solution [])crossoverOperator.execute(parents);               
        mutationOperator.execute(offSpring[0]);

        // Evaluate individual an his constraints
        problem_.evaluate(offSpring[0]);
        problem_.evaluateConstraints(offSpring[0]);
        evaluations++;

        int flag = dominance.compare(individual,offSpring[0]);

        if (flag == 1) { //The new individual dominates
          offSpring[0].setLocation(individual.getLocation());                                      
          currentPopulation.replace(offSpring[0].getLocation(),offSpring[0]);
          archive.add(new Solution(offSpring[0]));                   
        } else if (flag == 0) { //The new individual is non-dominated               
          neighbors[ind].add(offSpring[0]);               
          offSpring[0].setLocation(-1);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            distance.crowdingDistanceAssignment(rank.getSubfront(j), problem_.getNumberOfObjectives());
          }
          neighbors[ind].sort(crowdingComparator); 
          Solution worst = neighbors[ind].get(neighbors[ind].size()-1);

          if (worst.getLocation() == -1) { //The worst is the offspring
            archive.add(new Solution(offSpring[0]));
          } else {
            offSpring[0].setLocation(worst.getLocation());
            currentPopulation.replace(offSpring[0].getLocation(),offSpring[0]);
            archive.add(new Solution(offSpring[0]));
          }                                          
        }
      }
      
   // NEW STEP FOR THE CONVERGENCE STUDY: for calculating the convergence speed of the front in terms of the quality indicators
      if (indicatorList.length > 0) {
//			QualityIndicator indicators;
//			// System.out.println("PF file: " +
//			// paretoFrontFile_[problemId]);
//			indicators = new QualityIndicator(problem_,
//					paretoFrontFile);

			for (int j = 0; j < indicatorList.length; j++) {
				if (indicatorList[j].equals("HVNonNormalized")) {
					double value = indicators.getHypervolumeNonNormalized(archive);
					metrics[problemID][inst][alg][j][generations] += value;
				}
				if (indicatorList[j].equals("SPREADNonNormalized")) {
						double value = indicators.getSpreadNonNormalized(archive);
						metrics[problemID][inst][alg][j][generations] += value;
				}
				if (indicatorList[j].equals("IGDNonNormalized")) {
						double value = indicators.getIGDNonNormalized(archive);
						metrics[problemID][inst][alg][j][generations] += value;
				}
				if (indicatorList[j].equals("EPSILON")) {
						double value = indicators.getEpsilon(archive);
						metrics[problemID][inst][alg][j][generations] += value;
				}
			} // for
		} // if
      
      generations++;
    }
	
	// NEW STEP ADDED FOR CONVERGENCE STUDY: Print the values to disk
	int maxgen = generations - 1;
	

	// Prints all the values in metrics in files
//	if (indicatorList.length > 0) {
//
//		for (int j = 0; j < indicatorList.length; j++) {
//			if (indicatorList[j].equals("HV")) {
//				FileWriter os;
//				try {
//					os = new FileWriter(experimentDirectory
//							+ "/ConvHV", true);
//					for (int gens = 0; gens < maxgen; gens++)
//						os.write("" + metrics[problemID][inst][alg][j][gens] + "\n");
//					os.close();
//				} catch (IOException ex) {
//					Logger.getLogger(
//							ExperimentNoPareto.class.getName())
//							.log(Level.SEVERE, null, ex);
//				}
//			}
//			if (indicatorList[j].equals("SPREAD")) {
//				FileWriter os = null;
//				try {
//					
//					os = new FileWriter(experimentDirectory
//							+ "/ConvSPREAD", true);
//					for (int gens = 0; gens < maxgen; gens++)
//						os.write("" + metrics[problemID][inst][alg][j][gens] + "\n");
//					os.close();
//				} catch (IOException ex) {
//					Logger.getLogger(
//							ExperimentNoPareto.class.getName())
//							.log(Level.SEVERE, null, ex);
//				} finally {
//					try {
//						os.close();
//					} catch (IOException ex) {
//						Logger.getLogger(
//								ExperimentNoPareto.class
//								.getName()).log(
//										Level.SEVERE, null, ex);
//					}
//				}
//			}
//			if (indicatorList[j].equals("IGD")) {
//				FileWriter os = null;
//				try {
//					os = new FileWriter(experimentDirectory
//							+ "/ConvIGD", true);
//					for (int gens = 0; gens < maxgen; gens++)
//						os.write("" + metrics[problemID][inst][alg][j][gens] + "\n");
//					os.close();
//				} catch (IOException ex) {
//					Logger.getLogger(
//							ExperimentNoPareto.class.getName())
//							.log(Level.SEVERE, null, ex);
//				} finally {
//					try {
//						os.close();
//					} catch (IOException ex) {
//						Logger.getLogger(
//								ExperimentNoPareto.class
//								.getName()).log(
//										Level.SEVERE, null, ex);
//					}
//				}
//			}
//			if (indicatorList[j].equals("EPSILON")) {
//				FileWriter os = null;
//				try {
//					os = new FileWriter(experimentDirectory
//							+ "/ConvEPSILON", true);
//					for (int gens = 0; gens < maxgen; gens++)
//						os.write("" + metrics[problemID][inst][alg][j][gens] + "\n");
//					os.close();
//				} catch (IOException ex) {
//					Logger.getLogger(
//							ExperimentNoPareto.class.getName())
//							.log(Level.SEVERE, null, ex);
//				} finally {
//					try {
//						os.close();
//					} catch (IOException ex) {
//						Logger.getLogger(
//								ExperimentNoPareto.class
//								.getName()).log(
//										Level.SEVERE, null, ex);
//					}
//				}
//			}
//		} // for
//	} // if
	
    //System.out.println(evaluations);
    return archive;
  }        
  
}

