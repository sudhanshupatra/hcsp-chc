package jmetal.coevolutionary.experiments;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

//import jmetal.coevolutionary.experiments.util.*;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.experiments.settings.NSGAII_HCSP_Settings;
import jmetal.coevolutionary.experiments.settings.SPEA2_HCSP_Settings;
import jmetal.coevolutionary.experiments.settings.MOCell_HCSP_Settings;
import jmetal.util.JMException;

import jmetal.experiments.util.*;
import jmetal.experiments.*;

/**
 * StandardStudyHCSP.java
 *
 * @author Juan A. Ca�ero (Adapted to study HCSP problems)
 * @version 1.0
 */
public class MergeResultsHCSP extends ExperimentNoPareto {

	/**
	 * Configures the algorithms in each independent run
	 * @param problem The problem to solve
	 * @param problemIndex
	 */
//	public synchronized void algorithmSettings( Problem problem, int problemIndex, Algorithm[] algorithm ) {
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
//			Logger.getLogger(MergeResultsHCSP.class.getName()).log(Level.SEVERE, null, ex);
//		} // catch
//	} // algorithmSettings


	public static void main(String[] args) throws JMException, IOException, IllegalArgumentException {
		
		MergeResultsHCSP standardStudy = new MergeResultsHCSP();

//		standardStudy.numberOfIslands    = 8;
		standardStudy.experimentName_    = "StandardStudyHCSP";
//		standardStudy.timmingFileName_   = "TIMMINGS";
		// The order of this is VERY IMPORTANT!!!
		standardStudy.algorithmNameList_ = new String[]{ "NSGAII" , "SPEA2" , "MOCell" };
//		standardStudy.algorithmNameList_ = new String[]{ "MOCell" };

		standardStudy.problemList_ = new String[]{ "u_c_hihi" , "u_i_hihi" , "u_s_hihi" ,
                                                   "u_c_hilo" , "u_i_hilo" , "u_s_hilo" ,
                                                   "u_c_lohi" , "u_i_lohi" , "u_s_lohi" ,
                                                   "u_c_lolo" , "u_i_lolo" , "u_s_lolo" };
        
//        standardStudy.solutionTypeList_ = new String[]{ "Int" , "Int" , "Int" ,
//        		                                        "Int" , "Int" , "Int" ,
//        		                                        "Int" , "Int" , "Int" ,
//        		                                        "Int" , "Int" , "Int" };
//        
		// Results of the algorithms (the contents of their experimentName folder) must be in:
		// outputdir+experimentname+SOTA
		// outputdir+experimentname+4_Islands
		// outputdir+experimentname+8_Islands
        String[] ExperimentsList_ = new String[]{ "SOTA" , "4_Islands" , "8_Islands" };
//		String[] ExperimentsList_ = new String[]{ "4_Islands" , "8_Islands" };

        standardStudy.paretoFrontFile_ = new String[]{  "u_c_hihi.pf" , "u_i_hihi.pf" , "u_s_hihi.pf" ,
        												"u_c_hilo.pf" , "u_i_hilo.pf" , "u_s_hilo.pf" ,
        												"u_c_lohi.pf" , "u_i_lohi.pf" , "u_s_lohi.pf" ,
        												"u_c_lolo.pf" , "u_i_lolo.pf" , "u_s_lolo.pf" };

		standardStudy.indicatorList_   = new String[] {"HV", "SPREAD", "IGD", "EPSILON"} ;


		int numberOfAlgorithms = standardStudy.algorithmNameList_.length;

		String currentDir = System.getProperty("user.dir");
		String outputDir  = currentDir + "/output";

		standardStudy.experimentBaseDirectory_ = outputDir + "/" + standardStudy.experimentName_;
		standardStudy.paretoFrontDirectory_    = outputDir ;
//		standardStudy.algorithmSettings_       = new Settings[  numberOfAlgorithms ];
//		standardStudy.algorithm_               = new Algorithm[ numberOfAlgorithms ];
		// NOTEIT Independent runs
//		if ( standardStudy.paretoFrontFile_ == null )
//			standardStudy.independentRuns_ = 1000; // 1000 independent runs in order to get the pareto front
//		else
			standardStudy.independentRuns_ = 100;  // 100 independet runs, the normal mode
//		standardStudy.independentRuns_ = 1;  // 100 independet runs, the normal mode
/*
		standardStudy.params_ = new String[]{"Int" };
		// Create the Pareto front files
		for (int i=0; i< standardStudy.paretoFrontFile_.length; i++){
	    	File file = null;
	    	
	    		file = new File(outputDir + "/" + standardStudy.paretoFrontFile_[i]);
		    	try{
		    		file.createNewFile();	
		    	}catch(IOException ioe)
		        {
		    		System.out.println("Error while creating the empty file : " + standardStudy.paretoFrontDirectory_+ standardStudy.paretoFrontFile_[i] + ioe);
		    	}
	    	
	    }
			
		standardStudy.map_.put("experimentDirectory", standardStudy.experimentBaseDirectory_);
		standardStudy.map_.put("algorithmNameList", standardStudy.algorithmNameList_);
		standardStudy.map_.put("problemList", standardStudy.problemList_);
		standardStudy.map_.put("indicatorList", standardStudy.indicatorList_);
		standardStudy.map_.put("paretoFrontDirectory", standardStudy.paretoFrontDirectory_);
		standardStudy.map_.put("paretoFrontFile", standardStudy.paretoFrontFile_);
		standardStudy.map_.put("independentRuns", standardStudy.independentRuns_);
	    // map_.put("algorithm", algorithm_);
		standardStudy.map_.put("outputParetoFrontFile", standardStudy.outputParetoFrontFile_);
		standardStudy.map_.put("outputParetoSetFile", standardStudy.outputParetoSetFile_);

		
	    standardStudy.map_.put("params", standardStudy.params_); // parameters for the problem constructor
	    
//	    standardStudy.map_.put("instances", standardStudy.instances_); // Number of instances to solve per problem class
//	    exp.map_.put("timeEstimation", exp.time_); // For computing the run time and the run time left
//	    standardStudy.map_.put("timmingFileName", standardStudy.timmingFileName_);
	    
	    //standardStudy.map_.put("experimentsList", standardStudy.ExperimentsList_);

	    
	    // Run the experiments
//	    int numberOfThreads = numberOfThreadsDf_;
	    //exp.runExperiment(numberOfThreads, params);
	    
	    // Since the true Pareto front is not known for this problem, we
	    // generate it by merging all the obtained Pareto fronts in the experimentation
	    GeneratePareto paretoFront = new GeneratePareto(standardStudy, ExperimentsList_);
	    paretoFront.run();
	    paretoFront.computeQualityIndicators();
	    
	    // Generate latex tables (comment this sentence is not desired)
	    standardStudy.generateLatexTables(ExperimentsList_) ;
*/
	}

	@Override
	public void algorithmSettings(Problem problem, int problemId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void algorithmSettings(Problem problem, int problemId,
			Algorithm[] alg) {
		// TODO Auto-generated method stub
		
	}

	/*public void algorithmSettings(jmetal.base.Problem problem, int problemId,
			jmetal.base.Algorithm[] algorithm) {
		// TODO Auto-generated method stub
		
	}*/







} // StandardStudyHCSP
