package jmetal.coevolutionary.experiments;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.experiments.settings.MOCell_Settings;
import jmetal.coevolutionary.experiments.settings.NSGAII_Settings;
import jmetal.coevolutionary.experiments.settings.SPEA2_Settings;
import jmetal.util.JMException;


/**
 * StandardStudy.java
 *
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class StandardStudy extends Experiment {


	/**
	 * Configures the algorithms in each independent run
	 * @param problem The problem to solve
	 * @param problemIndex
	 */
	public void algorithmSettings( Problem problem, int problemIndex ) {
		try {
			int numberOfAlgorithms = algorithmNameList_.length;

			Properties[] parameters = new Properties[numberOfAlgorithms];

			for (int i = 0; i < numberOfAlgorithms; i++) {
				parameters[i] = new Properties();
			} // for

			if (!paretoFrontFile_[problemIndex].equals("")) {
				// Insert the property PARETO_FRONT_FILE in the array of properties
				for (int i = 0; i < numberOfAlgorithms; i++) 
					parameters[i].setProperty("PARETO_FRONT_FILE", paretoFrontFile_[problemIndex]);
			} // if

//			if ( numberOfAlgorithms > 0 ) algorithm_[0] = new MOCell_Settings(problem).configure(parameters[0]);
//			if ( numberOfAlgorithms > 1 ) algorithm_[1] = new NSGAII_Settings(problem).configure(parameters[1]);
//			if ( numberOfAlgorithms > 2 ) algorithm_[2] = new SPEA2_Settings(problem ).configure(parameters[2]);
			
			
			if ( numberOfAlgorithms > 0 ) algorithm_[0] = new NSGAII_Settings(problem).configure(parameters[0]);
			if ( numberOfAlgorithms > 1 ) algorithm_[1] = new SPEA2_Settings( problem).configure(parameters[1]);
			if ( numberOfAlgorithms > 2 ) algorithm_[2] = new MOCell_Settings(problem).configure(parameters[2]);

			// if ( numberOfAlgorithms > 3 ) algorithm_[3] = new OMOPSO_Settings(problem).configure(parameters[3]);
		} // try
		catch  (JMException ex) {
			Logger.getLogger(StandardStudy.class.getName()).log(Level.SEVERE, null, ex);
		} // catch
	} // algorithSettings


	public static void main(String[] args) throws JMException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		StandardStudy standardStudy = new StandardStudy();

		standardStudy.numberOfIslands    = 4;
		standardStudy.experimentName_    = "StandardStudy";
		standardStudy.timmingFileName_   = "TIMMINGS";
		// The order of this is VERY IMPORTANT!!!
		standardStudy.algorithmNameList_ = new String[]{ "NSGAII" , "SPEA2" , "MOCell" };

        standardStudy.problemList_ = new String[]{ "ZDT1"  , "ZDT2"  , "ZDT3"  ,  "ZDT4" , "ZDT6"  ,
        		                                   "DTLZ1" , "DTLZ2" , "DTLZ3" , "DTLZ4" , "DTLZ5" ,
                                                   "DTLZ6" , "DTLZ7" };//,
//                                                   "WFG1"  , "WFG2"  , "WFG3"  , "WFG4"  , "WFG5"  ,
//                                                   "WFG6"  , "WFG7"  , "WFG8"  , "WFG9"  };
        
        standardStudy.solutionTypeList_ = new String[]{ "Real" , "Real" , "Real" , "Real" , "Real" ,
        		                                        "Real" , "Real" , "Real" , "Real" , "Real" ,
        		                                        "Real" , "Real"  }; //,
//        		                                        "Real" , "Real" , "Real" , "Real" , "Real" ,
//        		                                        "Real" , "Real" , "Real" , "Real" };

        standardStudy.paretoFrontFile_ = new String[]{ "ZDT1.pf"     , "ZDT2.pf"     , "ZDT3.pf"     ,
                                                       "ZDT4.pf"     , "ZDT6.pf"     ,
                                                       "DTLZ1.2D.pf" , "DTLZ2.2D.pf" , "DTLZ3.2D.pf" ,
                                                       "DTLZ4.2D.pf" , "DTLZ5.2D.pf" , "DTLZ6.2D.pf" ,
                                                       "DTLZ7.2D.pf" }; //,
//                                                       "WFG1.2D.pf"  , "WFG2.2D.pf"  , "WFG3.2D.pf"  ,
//                                                       "WFG4.2D.pf"  , "WFG5.2D.pf"  , "WFG6.2D.pf"  ,
//                                                       "WFG7.2D.pf"  , "WFG8.2D.pf"  , "WFG9.2D.pf"  };

		standardStudy.indicatorList_ = new String[]{ "HV" , "SPREAD" , "IGD" , "EPSILON" };

		int numberOfAlgorithms = standardStudy.algorithmNameList_.length;

		String currentDir = System.getProperty("user.dir");
		String outputDir  = currentDir + "/output";

		standardStudy.experimentBaseDirectory_ = outputDir + "/" + standardStudy.experimentName_;
		standardStudy.paretoFrontDirectory_    = outputDir ;
		standardStudy.algorithmSettings_       = new Settings[  numberOfAlgorithms ];
		standardStudy.algorithm_               = new Algorithm[ numberOfAlgorithms ];
		standardStudy.independentRuns_         = 100;

		// Run the experiments
		standardStudy.runExperiment() ;

		// Generate latex tables
		standardStudy.generateLatexTables() ;

		// Configure the R scripts to be generated
		int      rows     ;
		int      columns  ;
		String   prefix   ;
		String[] problems ;

		// Configuring scripts for ZDT
		rows    = 3 ;
		columns = 2 ;
		prefix   = new String( "ZDT" );
		problems = new String[]{ "ZDT1" , "ZDT2" ,"ZDT3" , "ZDT4" ,"ZDT6" };
		standardStudy.generateRScripts( rows , columns , problems , prefix );

		// Configure scripts for DTLZ
		rows     = 3 ;
		columns  = 3 ;
		prefix   = new String( "DTLZ" );
		problems = new String[]{ "DTLZ1" , "DTLZ2" , "DTLZ3" , "DTLZ4" , "DTLZ5" ,
                                 "DTLZ6" , "DTLZ7" };
		standardStudy.generateRScripts( rows , columns , problems , prefix ) ;

	} // main

} // StandardStudy


