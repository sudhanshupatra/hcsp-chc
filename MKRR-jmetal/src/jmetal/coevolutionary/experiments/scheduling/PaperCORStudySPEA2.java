package jmetal.coevolutionary.experiments.scheduling;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.experiments.settings.COR.*;
import jmetal.coevolutionary.experiments.ExperimentNoPareto;
import jmetal.coevolutionary.experiments.*;
import jmetal.experiments.NSGAIIStudy;
import jmetal.util.JMException;


/**
 * StandardStudyHCSP.java
 *
 * @author Juan A. Ca–ero (Adapted to study HCSP problems)
 * @version 1.0
 */
public class PaperCORStudySPEA2 extends ExperimentNoPareto {

	private int independentRunsDf_ = 100;   // Number of independent runs per algorithm and problem
//	private int independentRunsDf_ = 1;   // Number of independent runs per algorithm and problem
//	private int numberOfThreadsDf_ = 1;   // Number of threads to use (= number of algorithms to run in parallel)
//	private int numberOfInstancesDF_ = 2; // Number of instances to solve per problem
	private int numberOfInstancesDF_ = 10; // Number of instances to solve per problem

	/**
	 * Configures the algorithms in each independent run
	 * @param problem The problem to solve
	 * @param problemIndex
	 */
	public void algorithmSettings( Problem problem, int problemIndex ) {
		
		try {

		      algorithm_[0] = new SPEA2_Settings_COR(problem).configure();
//		      algorithm[1] = new NSGAII_Settings_CEC10(problem).configure(parameters[1]);
//		      algorithm[2] = new IBEA_Settings_CEC10(problem).configure(parameters[2]);
//		      algorithm[3] = new MOEAD_Settings_CEC10(problem).configure(parameters[3]);
		      
		    } catch (JMException ex) {
		      Logger.getLogger(NSGAIIStudy.class.getName()).log(Level.SEVERE, null, ex);
		    }
		    
//		try {
//			int numberOfAlgorithms = algorithmNameList_.length;
//
//			Properties[] parameters = new Properties[numberOfAlgorithms];
//
//			for (int i = 0; i < numberOfAlgorithms; i++) {
//				parameters[i] = new Properties();
//			} // for
//
//			if ( (paretoFrontFile_ != null) && !paretoFrontFile_[problemIndex].equals("") ) {
//				// Insert the property PARETO_FRONT_FILE in the array of properties
//				for (int i = 0; i < numberOfAlgorithms; i++) 
//					parameters[i].setProperty("PARETO_FRONT_FILE", paretoFrontFile_[problemIndex]);
//			} // if
//
//			if ( numberOfAlgorithms > 0 ) {
//				Settings set = new NSGAII_HCSP_Settings(problem);
//				populationSize_ = set.populationSize_;
//				algorithm_[0] = set.configure( parameters[0] );
//			} // if
//			
////			if ( numberOfAlgorithms > 0 ) {
////				Settings set = new MOCell_HCSP_Settings(problem);
////				populationSize_ = set.populationSize_;
////				algorithm_[0] = set.configure( parameters[0] );
////			} // if
//			
//			if ( numberOfAlgorithms > 1 ) {
//				Settings set = new SPEA2_HCSP_Settings(problem);
//				populationSize_ = set.populationSize_;
//				algorithm_[1] = set.configure( parameters[1] );
//			} // if
//			
//			if ( numberOfAlgorithms > 2 ) {
//				Settings set = new MOCell_HCSP_Settings(problem);
//				populationSize_ = set.populationSize_;
//				algorithm_[2] = set.configure( parameters[2] );
//			} // if
//		
//			
//		} // try
//		catch  (JMException ex) {
//			Logger.getLogger(StandardStudyHCSP.class.getName()).log(Level.SEVERE, null, ex);
//		} // catch
	} // algorithmSettings


	/**
	   * Configures the algorithms in each independent run
	   * @param problem The problem to solve
	   * @param problemIndex
	   */
	  public synchronized void  algorithmSettings(Problem problem, int problemIndex, Algorithm[] algorithm) {
	    try {
	      
	      algorithm[0] = new SPEA2_Settings_COR(problem).configure();
//	      algorithm[1] = new NSGAII_Settings_CEC10(problem).configure(parameters[1]);
//	      algorithm[2] = new IBEA_Settings_CEC10(problem).configure(parameters[2]);
//	      algorithm[3] = new MOEAD_Settings_CEC10(problem).configure(parameters[3]);
	      
	    } catch (JMException ex) {
	      Logger.getLogger(NSGAIIStudy.class.getName()).log(Level.SEVERE, null, ex);
	    }
	  }

	  
	public static void main(String[] args) throws JMException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		
		if (args.length != 0) {
			System.out.println("Error. Try: PaperCEC10Study");
			System.exit(-1);
	    } // if

	  //Integer[] params = new Integer[] {new Integer(args[0]).intValue(), new Integer(args[1]).intValue(), new Integer(args[2]).intValue(), new Integer(args[3]).intValue()};
	  Object[] params = new String[]{"Int"};
	  
		PaperCORStudySPEA2 standardStudy = new PaperCORStudySPEA2();

		standardStudy.numberOfIslands    = 4;
		standardStudy.experimentName_    = "PaperCORStudy";
		standardStudy.timmingFileName_   = "TIMMINGS";
		// The order of this is VERY IMPORTANT!!!
//		standardStudy.algorithmNameList_ = new String[]{ "NSGAII" , "SPEA2" , "MOCell" };
		standardStudy.algorithmNameList_ = new String[]{ "SPEA2Sched" };

		standardStudy.problemList_ = new String[]{ "scheduling.u_i_hihi" , "scheduling.u_i_lolo" };
		
        
//        standardStudy.solutionTypeList_ = new String[]{ "Int" , "Int" , "Int" ,
//        		                                        "Int" , "Int" , "Int" ,
//        		                                        "Int" , "Int" , "Int" ,
//        		                                        "Int" , "Int" , "Int" };

		standardStudy.solutionTypeList_ = new String[]{ "Int" , "Int" };
		
//		standardStudy.paretoFrontFile_ = new String[]{"u_i_hihi.pf"};
//
//		standardStudy.indicatorList_   = new String[] {"HV", "SPREAD", "IGD", "EPSILON"} ;

		standardStudy.paretoFrontFile_ = null;
		standardStudy.indicatorList_   = new String[]{};
		
		int numberOfAlgorithms = standardStudy.algorithmNameList_.length;

		String currentDir = System.getProperty("user.dir");
		String outputDir  = currentDir + "/output";

		standardStudy.experimentBaseDirectory_ = outputDir + "/" + standardStudy.experimentName_;
		standardStudy.paretoFrontDirectory_    = outputDir + "./paretoFronts/scheduling" ;
		standardStudy.algorithmSettings_       = new Settings[  numberOfAlgorithms ];
		standardStudy.algorithm_               = new Algorithm[ numberOfAlgorithms ];
		
		standardStudy.instances_ = standardStudy.numberOfInstancesDF_;

		standardStudy.algorithmSettings_ = new Settings[numberOfAlgorithms];
		standardStudy.independentRuns_ = standardStudy.independentRunsDf_;  // 100 independet runs, the normal mode
		
		// Run the experiments
		standardStudy.runExperiment() ;

		// FIXME Do not run in HCSP
		// Generate latex tables
		// standardStudy.generateLatexTables() ;
	} // main

} // StandardStudyHCSP
