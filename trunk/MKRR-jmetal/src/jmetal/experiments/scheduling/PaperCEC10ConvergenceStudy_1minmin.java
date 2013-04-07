/**
 * MOCellStudy.java
 *
 * @author Bernabe Dorronsoro
 * @version 1.0
 */
package jmetal.experiments.scheduling;

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
import jmetal.util.JMException;
import jmetal.experiments.util.GeneratePareto;
import jmetal.experiments.*;
import jmetal.experiments.settings.CEC10.*;

/**
 * @author Bernabe Dorronsoro
 * 
 * This experiment class is configured to solve 100 instances of
 * every scheduling problem class with NSGAII, MOCell, IBEA, and MOEA/D
 * (100 independent runs per algorithm/instance)
 */
public class PaperCEC10ConvergenceStudy_1minmin extends ExperimentConvStudy {
  
	private int independentRunsDf_ = 100;   // Number of independent runs per algorithm and problem
	private int numberOfThreadsDf_ = 1;   // Number of threads to use (= number of algorithms to run in parallel)
	private int numberOfInstancesDF_ = 5; // Number of instances to solve per problem
	
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
        parameters[i].setProperty("POPULATION_SIZE", "100");
//        parameters[i].setProperty("MAX_EVALUATIONS", "500");
        parameters[i].setProperty("MAX_EVALUATIONS", "500000");
//        parameters[i].setProperty("MAX_EVALUATIONS", "1000000");
        parameters[i].setProperty("ARCHIVE_SIZE", "100");
        parameters[i].setProperty("FEEDBACK", "20");
        parameters[i].setProperty("SPECIAL_SOLUTION", "OneMinmin");
        
        parameters[i].setProperty("SELECTION", "BinaryTournament");
//        parameters[i].setProperty("SELECTION", "TournamentFour");
        
        parameters[i].setProperty("RECOMBINATION", "DPX");
//        parameters[i].setProperty("RECOMBINATION", "UniformCrossover");
        parameters[i].setProperty("CROSSOVER_PROBABILITY", "0.9");
        
        parameters[i].setProperty("MUTATION", "RebalanceMutation");
        parameters[i].setProperty("MUTATION_ROUNDS", "1");   // Estaba a 16  !!!!!!!!!!!!
        parameters[i].setProperty("MUTATION_OVERLOAD_PERCENTAGE", "0.25");
        //parameters[i].setProperty("MUTATION_POLICY", "moderate");
        parameters[i].setProperty("MUTATION_POLICY", "simple");
        //parameters[i].setProperty("MUTATION_MODE", "strict");
        parameters[i].setProperty("MUTATION_MODE", "permissive");
        parameters[i].setProperty("MUTATION_PROBABILITY", new Double(1.0/problem.getNumberOfVariables()).toString());
        
        parameters[i].setProperty("LOCAL_SEARCH", "LMCTSLocalSearch");
        
      }

      if ( (paretoFrontFile_ != null) && !paretoFrontFile_[problemIndex].equals("")) {
        for (int i = 0; i < numberOfAlgorithms; i++)
        parameters[i].setProperty("PARETO_FRONT_FILE", paretoFrontFile_[problemIndex]);
      } // if
 
      //for (int i = 0; i < numberOfAlgorithms; i++)
      //  algorithm[i] = new NSGAII_Settings(problem).configure(parameters[i]);
      
      algorithm[0] = new MOCell_Settings_CEC10_ConvStudy(problem).configure(parameters[0]);
      algorithm[1] = new NSGAII_Settings_CEC10_ConvStudy(problem).configure(parameters[1]);
      algorithm[2] = new IBEA_Settings_CEC10_ConvStudy(problem).configure(parameters[2]);
      algorithm[3] = new MOEAD_Settings_CEC10_ConvStudy(problem).configure(parameters[3]);
      
    } catch (JMException ex) {
      Logger.getLogger(NSGAIIStudy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public static void main(String[] args) throws JMException, IOException {
	  
	  if (args.length != 0) {
			System.out.println("Error. Try: PaperCEC10ConvergenceStudy");
			System.exit(-1);
	    } // if

	  //Integer[] params = new Integer[] {new Integer(args[0]).intValue(), new Integer(args[1]).intValue(), new Integer(args[2]).intValue(), new Integer(args[3]).intValue()};
	  Object[] params = new String[]{"Int"};
		  
    PaperCEC10ConvergenceStudy_1minmin exp = new PaperCEC10ConvergenceStudy_1minmin() ; // exp = experiment
    
    exp.experimentName_  = "PaperCEC10ConvergenceStudy_1minmin" ;
	exp.timmingFileName_   = "TIMMINGS";
    //exp.algorithmNameList_   = new String[] {"aMOCell4", "aMOCell4_AG", "aMOCell4_SRF"} ;
    exp.algorithmNameList_   = new String[] {"aMOCell4SchedConv", "NSGAIISchedConv", "IBEASchedConv", "MOEADSchedConv"} ;
//	exp.algorithmNameList_   = new String[] {"aMOCell4SchedConv"} ;
//   exp.problemList_ = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6",
//            "WFG1","WFG2","WFG3","WFG4","WFG5","WFG6",
//            "WFG7","WFG8","WFG9",
//            "DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ5",
//            "DTLZ6","DTLZ7"};
    
//    exp.problemList_ = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6",
//            "WFG1","WFG2","WFG3","WFG4","WFG5","WFG6",
//            "WFG7","WFG8","WFG9"};
    
    exp.problemList_ = new String[]{"scheduling.u_i_hihi","scheduling.u_i_lolo"};
    
//exp.paretoFrontFile_ = new String[]{"ZDT1.pf", "ZDT2.pf","ZDT3.pf",
//            "ZDT4.pf","ZDT6.pf",
//            "WFG1.2D.pf","WFG2.2D.pf","WFG3.2D.pf",
//            "WFG4.2D.pf","WFG5.2D.pf","WFG6.2D.pf",
//            "WFG7.2D.pf","WFG8.2D.pf","WFG9.2D.pf",
//            "DTLZ1.2D.pf","DTLZ2.2D.pf","DTLZ3.2D.pf",
//            "DTLZ4.2D.pf","DTLZ5.2D.pf","DTLZ6.2D.pf",
//            "DTLZ7.2D.pf"};
    
    exp.paretoFrontFile_ = new String[]{"u_i_hihi.pf","u_i_lolo.pf"};

    exp.indicatorList_   = new String[] {"HVNonNormalized", "SPREADNonNormalized", "IGDNonNormalized", "EPSILON"} ; // Epsilon metric does not normalize
    
    int numberOfAlgorithms = exp.algorithmNameList_.length ;

    exp.experimentBaseDirectory_ = "./" +
                                 exp.experimentName_;
    exp.paretoFrontDirectory_ = "./paretoFronts/scheduling";
    
    exp.instances_ = exp.numberOfInstancesDF_;
    
    // create the Pareto front files
    for (int i=0; i< exp.paretoFrontFile_.length; i++){
    	File file = null;
    	if (exp.instances_ != 1)
    	{
    		for (int inst = 0; inst < exp.instances_; inst ++){
    			file = new File(exp.paretoFrontDirectory_+ "/" + exp.paretoFrontFile_[i] + "." + inst);
    			try{
    	    		file.createNewFile();	
    	    	}catch(IOException ioe)
    	        {
    	    		System.out.println("Error while creating the empty file : " + exp.paretoFrontDirectory_+ exp.paretoFrontFile_[i] + ioe);
    	    	}
    		}
    		
    	}
    	else{
    		file = new File(exp.paretoFrontDirectory_+ "/" + exp.paretoFrontFile_[i]);
	    	try{
	    		file.createNewFile();	
	    	}catch(IOException ioe)
	        {
	    		System.out.println("Error while creating the empty file : " + exp.paretoFrontDirectory_+ exp.paretoFrontFile_[i] + ioe);
	    	}
    	}
    }
    
    exp.algorithmSettings_ = new Settings[numberOfAlgorithms] ;
    
    exp.independentRuns_ = exp.independentRunsDf_ ;
    
    // Run the experiments
    int numberOfThreads = exp.numberOfThreadsDf_;
    exp.runExperiment(numberOfThreads, params);
    //exp.runExperiment(numberOfThreads = 1);
    
//    // Since the true Pareto front is not known for this problem, we
//    // generate it by merging all the obtained Pareto fronts in the experimentation
//    GeneratePareto paretoFront = new GeneratePareto(exp);
//    paretoFront.run();
//    paretoFront.computeQualityIndicators();
    
    // At this point, the files with the average (over the 100 runs) 
    // qualityIndicator value at each generation have been created  
    
    // Generate latex tables (comment this sentence is not desired)
//    exp.generateLatexTables() ;
    
// // Configure the R scripts to be generated
//    int rows  ;
//    int columns  ;
//    String prefix ;
//    String [] problems ;
//    boolean notch ;
//
//    // Configuring scripts for ZDT
//    rows = 3 ;
//    columns = 2 ;
//    prefix = new String("ZDT");
//    problems = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6"} ;
//
//    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = true) ;
//    exp.generateRWilcoxonScripts(problems, prefix) ;
//
//    // Configure scripts for DTLZ
////    rows = 3 ;
////    columns = 3 ;
////    prefix = new String("DTLZ");
////    problems = new String[]{"DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ5",
////                                    "DTLZ6","DTLZ7"} ;
////
////    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = true) ;
////    exp.generateRWilcoxonScripts(problems, prefix) ;
//
//    // Configure scripts for WFG
//    rows = 3 ;
//    columns = 3 ;
//    prefix = new String("WFG");
//    problems = new String[]{"WFG1","WFG2","WFG3","WFG4","WFG5","WFG6",
//                            "WFG7","WFG8","WFG9"} ;
//
//    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = true) ;
//    exp.generateRWilcoxonScripts(problems, prefix) ;
  }
} // NSGAIIStudy


