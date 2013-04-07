package jmetal.coevolutionary.experiments;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.experiments.settings.NSGAII_HCSP_Settings;
import jmetal.coevolutionary.experiments.settings.SPEA2_HCSP_Settings;
import jmetal.coevolutionary.experiments.settings.MOCell_HCSP_Settings;
import jmetal.util.JMException;


/**
 * StandardStudyHCSP.java
 *
 * @author Juan A. Ca–ero (Adapted to study HCSP problems)
 * @version 1.0
 */
public class StandardStudyHCSP extends Experiment {

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

			if ( (paretoFrontFile_ != null) && !paretoFrontFile_[problemIndex].equals("") ) {
				// Insert the property PARETO_FRONT_FILE in the array of properties
				for (int i = 0; i < numberOfAlgorithms; i++) 
					parameters[i].setProperty("PARETO_FRONT_FILE", paretoFrontFile_[problemIndex]);
			} // if

			if ( numberOfAlgorithms > 0 ) {
				Settings set = new NSGAII_HCSP_Settings(problem);
				populationSize_ = set.populationSize_;
				algorithm_[0] = set.configure( parameters[0] );
			} // if
			
//			if ( numberOfAlgorithms > 0 ) {
//				Settings set = new MOCell_HCSP_Settings(problem);
//				populationSize_ = set.populationSize_;
//				algorithm_[0] = set.configure( parameters[0] );
//			} // if
			
			if ( numberOfAlgorithms > 1 ) {
				Settings set = new SPEA2_HCSP_Settings(problem);
				populationSize_ = set.populationSize_;
				algorithm_[1] = set.configure( parameters[1] );
			} // if
			
			if ( numberOfAlgorithms > 2 ) {
				Settings set = new MOCell_HCSP_Settings(problem);
				populationSize_ = set.populationSize_;
				algorithm_[2] = set.configure( parameters[2] );
			} // if
		
			
		} // try
		catch  (JMException ex) {
			Logger.getLogger(StandardStudyHCSP.class.getName()).log(Level.SEVERE, null, ex);
		} // catch
	} // algorithmSettings


	public static void main(String[] args) throws JMException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		StandardStudyHCSP standardStudy = new StandardStudyHCSP();

		standardStudy.numberOfIslands    = 4;
		standardStudy.experimentName_    = "StandardStudyHCSP";
		standardStudy.timmingFileName_   = "TIMMINGS";
		// The order of this is VERY IMPORTANT!!!
		standardStudy.algorithmNameList_ = new String[]{ "NSGAII" , "SPEA2" , "MOCell" };
//		standardStudy.algorithmNameList_ = new String[]{ "MOCell" };

		standardStudy.problemList_ = new String[]{ "u_c_hihi" , "u_i_hihi" , "u_s_hihi" ,
                                                   "u_c_hilo" , "u_i_hilo" , "u_s_hilo" ,
                                                   "u_c_lohi" , "u_i_lohi" , "u_s_lohi" ,
                                                   "u_c_lolo" , "u_i_lolo" , "u_s_lolo" };
        
        standardStudy.solutionTypeList_ = new String[]{ "Int" , "Int" , "Int" ,
        		                                        "Int" , "Int" , "Int" ,
        		                                        "Int" , "Int" , "Int" ,
        		                                        "Int" , "Int" , "Int" };

        standardStudy.paretoFrontFile_ = null;

		standardStudy.indicatorList_   = new String[]{};

		int numberOfAlgorithms = standardStudy.algorithmNameList_.length;

		String currentDir = System.getProperty("user.dir");
		String outputDir  = currentDir + "/output";

		standardStudy.experimentBaseDirectory_ = outputDir + "/" + standardStudy.experimentName_;
		standardStudy.paretoFrontDirectory_    = outputDir ;
		standardStudy.algorithmSettings_       = new Settings[  numberOfAlgorithms ];
		standardStudy.algorithm_               = new Algorithm[ numberOfAlgorithms ];
		// NOTEIT Independent runs
//		if ( standardStudy.paretoFrontFile_ == null )
//			standardStudy.independentRuns_ = 1000; // 1000 independent runs in order to get the pareto front
//		else
			standardStudy.independentRuns_ = 100;  // 100 independet runs, the normal mode
//		standardStudy.independentRuns_ = 1;  // 100 independet runs, the normal mode

		// Run the experiments
		standardStudy.runExperiment() ;

		// FIXME Do not run in HCSP
		// Generate latex tables
		// standardStudy.generateLatexTables() ;
	} // main

} // StandardStudyHCSP
