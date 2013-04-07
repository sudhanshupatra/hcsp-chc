/**
 * NSGAIIStudy.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.experiments;

import java.util.logging.Logger;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.experiments.settings.NSGAII_Settings;
import jmetal.util.JMException;

/**
 * @author Antonio J. Nebro
 */
public class NSGAIIStudy extends Experiment {
  
  /**
   * Configures the algorithms in each independent run
   * @param problem The problem to solve
   * @param problemIndex
   */
  public synchronized void  algorithmSettings(Problem problem, int problemIndex, Algorithm[] algorithm) {
    try {
      int numberOfAlgorithms = algorithmNameList_.length;

      Properties[] parameters = new Properties[numberOfAlgorithms];
      //Settings[] configuration = new Settings[algorithmName_.length];

      for (int i = 0; i < numberOfAlgorithms; i++) {
        parameters[i] = new Properties();
      }

      parameters[0].setProperty("CROSSOVER_PROBABILITY", "1.0");
      parameters[1].setProperty("CROSSOVER_PROBABILITY", "0.9");
      parameters[2].setProperty("CROSSOVER_PROBABILITY", "0.8");
      parameters[3].setProperty("CROSSOVER_PROBABILITY", "0.7"); 

      if (!paretoFrontFile_[problemIndex].equals("")) {
        for (int i = 0; i < numberOfAlgorithms; i++)
        parameters[i].setProperty("PARETO_FRONT_FILE", paretoFrontFile_[problemIndex]);
      } // if
 
      for (int i = 0; i < numberOfAlgorithms; i++)
        algorithm[i] = new NSGAII_Settings(problem).configure(parameters[i]);
      
    } catch (JMException ex) {
      Logger.getLogger(NSGAIIStudy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public static void main(String[] args) throws JMException, IOException {
    NSGAIIStudy exp = new NSGAIIStudy() ; // exp = experiment
    
    exp.experimentName_  = "NSGAIIStudy1t" ;
    exp.algorithmNameList_   = new String[] {
      "NSGAIIa", "NSGAIIb", "NSGAIIc", "NSGAIId"} ;
    exp.problemList_     = new String[] {
      "ZDT1", "ZDT2", "ZDT3", "ZDT4", "DTLZ1", "WFG2"} ;
    exp.paretoFrontFile_ = new String[] {
      "ZDT1.pf", "ZDT2.pf", "ZDT3.pf","ZDT4.pf", "DTLZ1.2D.pf", "WFG2.2D.pf"} ;
    exp.indicatorList_   = new String[] {"HV", "SPREAD", "IGD", "EPSILON"} ;
    
    int numberOfAlgorithms = exp.algorithmNameList_.length ;

    exp.experimentBaseDirectory_ = "/Users/bernabe/Desktop/work/trabajos/jMetal2.2/jmetal/trunk/jmetal/" +
                                   exp.experimentName_;
    exp.paretoFrontDirectory_ = "/Users/bernabe/Desktop/work/trabajos/jMetal2.2/jmetal/results/paretoFronts";
    
    exp.algorithmSettings_ = new Settings[numberOfAlgorithms] ;
    
    exp.independentRuns_ = 30 ;
    
    // Run the experiments
    int numberOfThreads ;
    exp.runExperiment(numberOfThreads = 1) ;
    
    // Generate latex tables (comment this sentence is not desired)
    exp.generateLatexTables() ;
    
    // Configure the R scripts to be generated
    int rows  ;
    int columns  ;
    String prefix ;
    String [] problems ;

    rows = 2 ;
    columns = 3 ;
    prefix = new String("Problems");
    problems = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4", "DTLZ1", "WFG2"} ;

    boolean notch ;
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false) ;
    exp.generateRWilcoxonScripts(problems, prefix) ;
  }
} // NSGAIIStudy


