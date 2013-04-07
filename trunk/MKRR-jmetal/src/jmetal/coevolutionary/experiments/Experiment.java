/**
 * This package contains classes to perform the test of the algorithms and problems
 */
package jmetal.coevolutionary.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import jmetal.coevolutionary.Islands;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.archive.AdaptiveGridArchive;
import jmetal.coevolutionary.problems.ProblemFactory;
import jmetal.coevolutionary.qualityIndicator.QualityIndicator;
import jmetal.coevolutionary.util.Latexize;
import jmetal.coevolutionary.util.StatReg;
import jmetal.coevolutionary.util.TimeEstimation;
import jmetal.util.JMException;


/** This is the base class to define experiments to be carried out with jMetal
 * 
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero (New features added)
 * @version 1.1
 */
public abstract class Experiment {

	public String      experimentName_;
	public String[]    algorithmNameList_;       ///< List of the names of the algorithms to be executed
	public String[]    problemList_;             ///< List of problems to be solved
	public String[]    solutionTypeList_;        ///< List of the solution type of each problem
	public String[]    paretoFrontFile_;         ///< List of the files containing the pareto fronts
	// corresponding to the problems in problemList_
	public String		timmingFileName_;         ///< Name of the timming
	public String[]    indicatorList_;           ///< List of the quality indicators to be applied
	public String      experimentBaseDirectory_; ///< Directory to store the results
	public String      latexDirectory_ ;         ///< Directory to store the latex files
	public String      rDirectory_ ;             ///< Directory to store the generated R scripts
	public String      paretoFrontDirectory_;    ///< Directory containing the Pareto front files
	public String      outputParetoFrontFile_;   ///< Name of the file containing the output 
	// Pareto front
	public String      outputParetoSetFile_;     ///< Name of the file containing the output 
	// Pareto set
	public int         independentRuns_;         ///< Number of independent runs per algorithm
	public Settings[]  algorithmSettings_;       ///< Paremeter settings of each algorithm
	public Algorithm[] algorithm_;               ///< jMetal algorithms to be executed
	
	public int         numberOfIslands ;         ///< Number of islands to use ( 0 -> Default value of the problem )
	public int         populationSize_;
	
	public int			instances_;


	/**
	 * Constructor, contains default settings.
	 */
	public Experiment() {
		experimentName_    = "noName";

		algorithmNameList_       = null;
		problemList_             = null;
		paretoFrontFile_         = null;
		indicatorList_           = null;

		experimentBaseDirectory_ = "";
		paretoFrontDirectory_    = "";
		latexDirectory_          = "latex" ;
		rDirectory_              = "R" ;

		outputParetoFrontFile_   = "FUN";
		outputParetoSetFile_     = "VAR";

		algorithmSettings_       = null;
		algorithm_               = null;

		independentRuns_         = 0;
	} // Constructor


	/**
	 * Runs the experiment.
	 * @throws ClassNotFoundException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	public void runExperiment() throws JMException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		int           numberOfAlgorithms = algorithmNameList_.length;
		double[][]    timmings           = new double[  problemList_.length ][ numberOfAlgorithms ];
//		StatReg[][]   finalRobustness    = new StatReg[ problemList_.length ][ numberOfAlgorithms ];
//		StatReg[][]   finalMakespan      = new StatReg[ problemList_.length ][ numberOfAlgorithms ];
		long          numberOfExecutions = (long) numberOfAlgorithms * (long) independentRuns_ * (long) problemList_.length;

		// Step 1: check experiment base directory
		checkExperimentDirectory();
		TimeEstimation time = new TimeEstimation( (long) numberOfExecutions );

		for( int problemId=0 ; problemId < problemList_.length ; problemId++ ) {
			Problem problem;   // The problem to solve
			long[] accTime = new long[numberOfAlgorithms];
			for( int i=0 ; i<numberOfAlgorithms ; ++i )
				accTime[i] = 0;

//			StatReg[][] robustnessStats = new StatReg[independentRuns_   ][numberOfAlgorithms];
//			StatReg[][] makespanStats   = new StatReg[independentRuns_   ][numberOfAlgorithms];
			// STEP 2: get the problem from the list
			Object[] params = { 0 , solutionTypeList_[problemId] , numberOfIslands }; // Parameters of the problem, 0 -> Put the value by default
//			Object[] params = { 40 , solutionTypeList_[problemId] , numberOfIslands };
			problem = ( new ProblemFactory() ).getProblem( problemList_[problemId], params );

			// STEP 3: check the file containing the Pareto front of the problem
			if ( ( indicatorList_!=null ) && ( indicatorList_.length!=0) ) {
				File pfFile = new File(paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId]);
				if (pfFile.exists()) {
					paretoFrontFile_[problemId] = paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId];
				} // if
				else {
					paretoFrontFile_[problemId] = "";
				} // else
			} // if

			AdaptiveGridArchive[] resultFronts = new AdaptiveGridArchive[ numberOfAlgorithms ];
			
			for (int runs = 0; runs < independentRuns_; runs++) {
				// STEP 4: configure the algorithms
				algorithmSettings( problem , problemId );
				if ( paretoFrontFile_==null )
					for( int alg=0 ; alg<numberOfAlgorithms ; ++alg )
						if ( resultFronts[alg]==null ) // algorithm_[alg].getPopulationSize() vale 0 en este punto
							resultFronts[alg] = new AdaptiveGridArchive( 0 , 1 , 0 , populationSize_ , 5 , problem.getNumberOfObjectives() );

				// STEP 5: run the algorithms
				for (int alg = 0; alg < numberOfAlgorithms ; ++alg) {
					// STEP 6: create output directories
					File   experimentDirectory;
					String directory;

					directory = experimentBaseDirectory_ + "/" + algorithmNameList_[alg] + "/" + problemList_[problemId];

					experimentDirectory = makeDirectory( directory );

					// STEP 7: run the algorithm
					System.out.print( "Algorithm: " + algorithmNameList_[alg] +
							          ", problem: " + problemList_[problemId] +
							          ", run: " + runs );

					QualityIndicator indicators;

					Islands islands   = new Islands( problem , algorithm_[alg] );

					long startTime = System.currentTimeMillis();
					SolutionSet result = islands.execute();
					long endTime   = System.currentTimeMillis();

					time.iteration();
					
					System.out.print( ", " + result.size() + " sols found, " + (endTime-startTime) +
	                          " ms. (" + time.getPercentageDone() + "% done, ");
					
					System.out.println( time.getRemainingHumanReadable() + " remaining)");
										
					accTime[alg] += (endTime-startTime);

					// STEP 8: put the results in the output directory
					result.printObjectivesToFile(directory + "/" + outputParetoFrontFile_ + "." + runs);
					result.printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + runs);
					
					StatReg[] stats = new StatReg[2];
					stats = printStatsToFile( result , directory + "/" + outputParetoFrontFile_ + "." + runs );
//					makespanStats[runs][alg] = stats[0];
//					robustnessStats[runs][alg] = stats[1];

					if ( paretoFrontFile_==null ) // A–adir el resultado a la poblacion indicada
						addElementsParetoFront( resultFronts[alg] , result );
					
					// STEP 9: calculate quality indicators 
					if ( indicatorList_.length > 0 ) {
						//QualityIndicator indicators;
						//System.out.println("PF file: " + paretoFrontFile_[problemId]);
						indicators = new QualityIndicator( problem , paretoFrontFile_[problemId] );

						for (int j = 0; j < indicatorList_.length; j++) {
							if (indicatorList_[j].equals("HV")) {
								double value = indicators.getHypervolume( result );
								FileWriter os = new FileWriter( experimentDirectory + "/HV", true);
								os.write("" + value + "\n");
								os.close();
							} // if
							if (indicatorList_[j].equals("SPREAD")) {
								double value = indicators.getSpread( result );
								FileWriter os = new FileWriter( experimentDirectory + "/SPREAD", true);
								os.write("" + value + "\n");
								os.close();
							} // if
							if (indicatorList_[j].equals("IGD")) {
								double value = indicators.getIGD( result );
								FileWriter os = new FileWriter( experimentDirectory + "/IGD", true);
								os.write("" + value + "\n");
								os.close();
							} // if
							if (indicatorList_[j].equals("EPSILON")) {
								double value = indicators.getEpsilon( result );
								FileWriter os = new FileWriter( experimentDirectory + "/EPSILON", true);
								os.write("" + value + "\n");
								os.close();
							} // if
						} // for
					} // if
					
					// STEP 10: Write the used time in a file
					FileWriter os = new FileWriter( directory + "/" + timmingFileName_ + "." + runs);
					os.write( "" + (endTime-startTime) + "\n" );
					os.close();
					
				} // for
			} // for
			if ( paretoFrontFile_==null ) {
				for( int alg=1 ; alg<numberOfAlgorithms ; ++alg )
					addElementsParetoFront( resultFronts[0] , resultFronts[alg] );

				String file = paretoFrontDirectory_ + "/" + problemList_[problemId] + ".pf";
				resultFronts[0].printObjectivesToFile( file );
			} // if
			
			System.out.println();
			// STEP 11: Write The timmings in a latex file
			for( int i=0 ; i<numberOfAlgorithms ; ++i ) {
				FileWriter os = new FileWriter( experimentBaseDirectory_ + "/" + algorithmNameList_[i] + "/" + problemList_[problemId] + "/" + timmingFileName_ + ".AVG" );
				os.write( "" + (accTime[i]/independentRuns_) + "\n" );
				os.close();
				timmings[problemId][i]=(accTime[i]/independentRuns_);
			} // for
			
			// STEP 12: Write the statistical values in 2 files
//			StatReg[] st = generateStats( makespanStats   );
//			for( int alg=0 ; alg<algorithm_.length ; ++alg ) {
//				String file = experimentBaseDirectory_ + "/" + algorithmNameList_[alg] + "/" + problemList_[problemId] + "/" + "makespan.stats";
//				printStatsToFile( st[alg] , file );
//			} // for
//			System.arraycopy( st, 0, finalMakespan[ problemId ], 0, algorithm_.length );
//			st = generateStats( robustnessStats );
//			for( int alg=0 ; alg<algorithm_.length ; ++alg ) {
//				String file = experimentBaseDirectory_ + "/" + algorithmNameList_[alg] + "/" + problemList_[problemId] + "/" + "robustness.stats";
//				printStatsToFile( st[alg] , file );
//			} // for
//			System.arraycopy( st, 0, finalRobustness[ problemId ], 0, algorithm_.length );
		} //for
		
//		String file = experimentBaseDirectory_ + "/Statistics.tex";
//		generateLatexTables( finalRobustness , "Robustness" ,
//				              finalMakespan   , "Makespan"   , file );
		
		String file = experimentBaseDirectory_ + "/Timmings.tex";
		printTimmings( file , timmings );
	} // runExperiment


	/**
	 * @param directory Directory name to make
	 * @return a File id
	 */
	private File makeDirectory( String directory ) {
		File experimentDirectory;
		experimentDirectory = new File(directory);
		if (!experimentDirectory.exists()) {
			new File(directory).mkdirs();
			System.out.println( "Creating " + directory );
		} // if
		return experimentDirectory;
	} // makeDirectory


	/**
	 * @param paretoFront
	 * @param concreteResult
	 */
	private void addElementsParetoFront( AdaptiveGridArchive paretoFront, SolutionSet concreteResult ) {
		
		Iterator<Solution> it = concreteResult.iterator();
		while( it.hasNext() )
			paretoFront.add( it.next() );
	} // AddElementsParetoFront


	/** This method generate 2 latext tables containing the statistics values previously
	 * calculated.
	 * 
	 * @param finalRobustness
	 * @param robustness
	 * @param finalMakespan
	 * @param makespan
	 * @param fileName
	 * 
	 * @throws IOException
	 */
	private void generateLatexTables( StatReg[][] finalRobustness , String robustness ,
                                      StatReg[][] finalMakespan   , String makespan   ,
                                      String      fileName                            ) throws IOException {

		printHeaderLatexCommands( fileName );
		
		FileWriter os = new FileWriter( fileName , true );

		generateLatexTable( os , finalMakespan   , makespan   );
		generateLatexTable( os , finalRobustness , robustness );
		
		os.close();
		
		printEndLatexCommands( fileName );
	} // generateLatexTables


	/** Flush to file a latex table representing the given matrix
	 *
	 * @param os File descriptor
	 * @param finalData A matrix of statitistic registers
	 * @param Indicator A string that indicates The values represented
	 * 
	 * @throws IOException
	 */
	private void generateLatexTable( FileWriter os , StatReg[][] finalData , String Indicator ) throws IOException {

		os.write("\n");
		os.write("\\begin{table}\n");
		os.write("\\caption{" + Indicator + ". Statistical values}\n");
		os.write("\\label{table:stats." + Indicator + "}\n");
		os.write("\\centering\n\n");
		
		String cols="c";
		for(int i=0 ; i<algorithmNameList_.length ; ++i )
			cols+="cccc";
		os.write("\\begin{tabular}{" + cols + "}\n");
		os.write("\\hline\n");
		
		String textLine="\n\\multirow{2}*{Problem}\n";
		for(int i=0 ; i<algorithmNameList_.length ; ++i )
			textLine+=" & \\multicolumn{4}{c}{" + algorithmNameList_[i] + "} ";
		textLine+="\\\\\n";
		for(int i=0 ; i<algorithmNameList_.length ; ++i )
			textLine+=" & Min & Max & Mean & Median ";
		textLine += "\\\\\n";
		os.write("\\hline "+textLine);
		os.write("\\hline\n");
		os.flush();
		for( int j=0 ; j<problemList_.length ; ++j ){
			String s = Latexize.String( problemList_[j] );
			textLine= s + " ";
			for(int i=0 ; i<algorithmNameList_.length ; ++i ){
				textLine+="& " + Latexize.Double( finalData[j][i].min_    , 20 ) + " ";
				textLine+="& " + Latexize.Double( finalData[j][i].max_    , 20 ) + " ";
				textLine+="& " + Latexize.Double( finalData[j][i].mean_   , 20 ) + " ";
				textLine+="& " + Latexize.Double( finalData[j][i].median_ , 20 ) + " ";
			} // for
			textLine+="\\\\\n";
			os.write( textLine );
			os.flush();
		} // for
		os.write("\\hline\n");
		os.write("\\hline\n");
		os.write("\\end{tabular}\n\n");
		os.write("\\end{table}\n\n");

	} // generateLatexTable


	/** Write The statistical register to a file
	 * @param statR The statistical register
	 * @param filename The file
	 * @throws IOException
	 */
	private void printStatsToFile( StatReg statR , String filename ) throws IOException {

		FileWriter os = new FileWriter( filename );
		os.write( ""  + statR.mean_         );
		os.write( " " + statR.median_       );
		os.write( " " + statR.stdDeviation_ );
		os.write( " " + statR.iqr_          );
		os.write( " " + statR.max_          );
		os.write( " " + statR.min_ + "\n"   );
		os.close();
	} // printStatsToFile


	/** This method generate the statistical values of statistical values
	 * @param bigStat A matrix of statistical values
	 * @return The register contatining the statistical values
	 */
	private StatReg[] generateStats( StatReg[][] bigStat ) {
		double[] mean   = new double[ independentRuns_ ];
		double[] median = new double[ independentRuns_ ];
		double[] max    = new double[ independentRuns_ ];
		double[] min    = new double[ independentRuns_ ];
		
		StatReg[] statStat = new StatReg[algorithm_.length];
		
		for( int alg=0 ; alg<algorithm_.length ; ++alg ) {
			
			for( int i=0 ; i<independentRuns_ ; ++i){
				mean[i]   = bigStat[i][alg].mean_;
				median[i] = bigStat[i][alg].median_;
				max[i]    = bigStat[i][alg].max_;
				min[i]    = bigStat[i][alg].min_;
			} // for
			StatReg meanStat  ;
			StatReg medianStat;
			StatReg maxStat   ;
			StatReg minStat   ;
			
			meanStat   = calculateStatistics( mean   );
			medianStat = calculateStatistics( median );
			maxStat    = calculateStatistics( max    );
			minStat    = calculateStatistics( min    );
			
			statStat[alg] = new StatReg( meanStat.median_   ,
			                             medianStat.median_ ,
			                             0.0                ,
			                             0.0                ,
			                             maxStat.median_    ,
			                             minStat.median_    );
		} // for
		
		return statStat;
	} // generateStats
	

	/** This method calculates statistical values of a given vector, like the
	 * mean, the median, the standart deviation, the inter quartile rank, the
	 * maximum and the minimum.
	 * 
	 * <b>Warning:</b> This method sorts the input vector.
	 * 
	 * @param vector The vector
	 * @return The statistics
	 */
	@SuppressWarnings("unchecked")
	private StatReg calculateStatistics( double[] vector ){

		int     len   = vector.length;
		StatReg statR ;
		
		if ( len>1 ) {
			Vector vec = new Vector();

			Arrays.sort( vector );
			for( int i=0 ; i<len ; ++i )
				vec.add( (Double) vector[i]);

			Map<String, Double> statValues = new HashMap<String, Double>();

			statValues.put( "mean"         , 0.0 );
			statValues.put( "median"       , 0.0 );
			statValues.put( "stdDeviation" , 0.0 );
			statValues.put( "iqr"          , 0.0 );
			statValues.put( "max"          , 0.0 );
			statValues.put( "min"          , 0.0 );

			calculateStatistics( vec , statValues );

			statR = new StatReg();
			statR.mean_         = statValues.get("mean");
			statR.median_       = statValues.get("median");
			statR.stdDeviation_ = statValues.get("stdDeviation");
			statR.iqr_          = statValues.get("iqr");
			statR.max_          = statValues.get("max");
			statR.min_          = statValues.get("min");
		} // if
		else {
			double value = vector[0];
			statR = new StatReg( value , value , 0.0 , 0.0 , value , value );
		} // else
		
		return statR;
	} // calculateStatistics


	/** This method writes the statistical values of the objectives of the
	 * solutionSet in two files: the Makespan and the Robustness
	 * @param sol The solution set
	 * @param filename The base of the filename
	 * @return The statistical values
	 * @throws IOException
	 */
	private StatReg[] printStatsToFile( SolutionSet sol      ,
                                        String     filename  ) throws IOException {
		StatReg[] statR = new StatReg[2];

		double[][] objs = extractObjectives2matrix( sol );
		
		for( int i=0 ; i<sol.size() ; ++i)
			objs[1][i] = -objs[1][i];

		statR[0] = calculateStatistics( objs[0] );
		statR[1] = calculateStatistics( objs[1] );
		
		printStatsToFile( statR[0] , filename + ".Makespan"   );
		printStatsToFile( statR[1] , filename + ".Robustness" );

		return statR;		
	} // printStatsToFile


	/** This method extracts in a matrix the objectives of each solution of the
	 * SolutionSet
	 * 
	 * @param solutionSet The Solution set
	 * @return The matrix with the objectives
	 */
	private double[][] extractObjectives2matrix( SolutionSet solutionSet ) {
		double[][] matrix = new double[ 2 ][ solutionSet.size() ];
		
		double[] fitness;
		
		Iterator<Solution> it = solutionSet.iterator();
		int i=0;
		while( it.hasNext() ) {
			fitness = ( it.next() ).getObjectives();
			
			matrix[0][i] = fitness[0];
			matrix[1][i] = fitness[1];
			++i;
		} // while

		return matrix;
	} // extractObjectives2vectors


	/** Write the timming table in a latex file
	 * @param fileName The name of the file
	 * @param timmings the timming table
	 * @throws IOException
	 */
	private void printTimmings( String fileName , double[][] timmings) throws IOException {
		
		printHeaderLatexCommands(fileName);
		
		FileWriter os = new FileWriter(fileName, true);
		os.write("\\begin{table}\n");
		os.write("\\caption{TIMMINGS. Mean}\n");
		os.write("\\label{table:mean.TIMMINGS}\n");
		os.write("\\centering\n");
		String cols="l";
		for(int i=0 ; i<algorithmNameList_.length ; ++i )
			cols+="l";
		os.write("\\begin{tabular}{" + cols + "}\n");
		String textLine="";
		for(int i=0 ; i<algorithmNameList_.length ; ++i )
			textLine+="& "+algorithmNameList_[i] + " ";
		os.write("\\hline "+textLine+"\\\\\n");
		os.write("\\hline\n");
		for( int j=0 ; j<problemList_.length ; ++j ){
			textLine = Latexize.String(problemList_[j]) + " ";
			for(int i=0 ; i<algorithmNameList_.length ; ++i )
				textLine+="& $"+timmings[j][i]+" ms $ ";
			textLine+="\\\\\n";
			os.write( textLine );
		} // for
		os.write("\\hline\n");
		os.write("\\end{tabular}\n");
		os.write("\\end{table}\n");
		os.close();

		printEndLatexCommands(fileName);
	} // printTimmings


	/**
	 * Check if exist the destination directory and creates it in case of inexistence.
	 */
	@SuppressWarnings("unused")
	public void checkExperimentDirectory() {
		File experimentDirectory;

		experimentDirectory = new File(experimentBaseDirectory_);
		if (experimentDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (experimentDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} //if
			else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			} // else
			experimentDirectory.delete();
			boolean result = new File(experimentBaseDirectory_).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			boolean result = new File(experimentBaseDirectory_).mkdirs();
		} // else
	} // checkDirectories


	/**
	 * Especifies the settings of each algorith. This method is checked in each
	 * experiment run
	 * @param problem Problem to solve
	 * @param problemId Index of the problem in problemList_
	 */
	public abstract void algorithmSettings(Problem problem, int problemId); // algorithmSettings

	@SuppressWarnings("unchecked")
	public void generateLatexTables() throws FileNotFoundException, IOException {
		Vector[][][] data = new Vector[indicatorList_.length][][];
		for (int indicator = 0; indicator < indicatorList_.length; indicator++) {
			// A data vector per problem 
			data[indicator] = new Vector[problemList_.length][];

			for (int problem = 0; problem < problemList_.length; problem++) {
				data[indicator][problem] = new Vector[algorithmNameList_.length];

				for (int algorithm = 0; algorithm < algorithmNameList_.length; algorithm++) {
					data[indicator][problem][algorithm] = new Vector();

					String directory = experimentBaseDirectory_;
					directory += "/" + algorithmNameList_[algorithm];
					directory += "/" + problemList_[problem];
					directory += "/" + indicatorList_[indicator];
					// Read values from data files
					FileInputStream fis = new FileInputStream(directory);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(isr);
					System.out.println(directory);
					String aux = br.readLine();
					while (aux != null) {
						data[indicator][problem][algorithm].add(Double.parseDouble(aux));
						System.out.println(Double.parseDouble(aux));
						aux = br.readLine();
					} // while
				} // for    
			} // for
		} // for

		double[][][] mean;
		double[][][] median;
		double[][][] stdDeviation;
		double[][][] iqr;
		double[][][] max ;
		double[][][] min ;
		int   [][][] numberOfValues ;

		Map<String, Double> statValues = new HashMap<String, Double>();

		statValues.put( "mean"         , 0.0 );
		statValues.put( "median"       , 0.0 );
		statValues.put( "stdDeviation" , 0.0 );
		statValues.put( "iqr"          , 0.0 );
		statValues.put( "max"          , 0.0 );
		statValues.put( "min"          , 0.0 );

		mean           = new double[indicatorList_.length][][];
		median         = new double[indicatorList_.length][][];
		stdDeviation   = new double[indicatorList_.length][][];
		iqr            = new double[indicatorList_.length][][];
		min            = new double[indicatorList_.length][][];
		max            = new double[indicatorList_.length][][];
		numberOfValues = new    int[indicatorList_.length][][];

		for (int indicator = 0; indicator < indicatorList_.length; indicator++) {
			// A data vector per problem 
			mean[indicator] = new double[problemList_.length][];
			median[indicator] = new double[problemList_.length][];
			stdDeviation[indicator] = new double[problemList_.length][];
			iqr[indicator] = new double[problemList_.length][];
			min[indicator] = new double[problemList_.length][];
			max[indicator] = new double[problemList_.length][];
			numberOfValues[indicator] = new int[problemList_.length][];

			for (int problem = 0; problem < problemList_.length; problem++) {
				mean[indicator][problem] = new double[algorithmNameList_.length];
				median[indicator][problem] = new double[algorithmNameList_.length];
				stdDeviation[indicator][problem] = new double[algorithmNameList_.length];
				iqr[indicator][problem] = new double[algorithmNameList_.length];
				min[indicator][problem] = new double[algorithmNameList_.length];
				max[indicator][problem] = new double[algorithmNameList_.length];
				numberOfValues[indicator][problem] = new int[algorithmNameList_.length];

				for (int algorithm = 0; algorithm < algorithmNameList_.length; algorithm++) {
					Collections.sort(data[indicator][problem][algorithm]);

					String directory = experimentBaseDirectory_;
					directory += "/" + algorithmNameList_[algorithm];
					directory += "/" + problemList_[problem];
					directory += "/" + indicatorList_[indicator];

					//System.out.println("----" + directory + "-----");
					//calculateStatistics(data[indicator][problem][algorithm], meanV, medianV, minV, maxV, stdDeviationV, iqrV) ;
					calculateStatistics(data[indicator][problem][algorithm], statValues);

					mean[indicator][problem][algorithm] = statValues.get("mean");
					median[indicator][problem][algorithm] = statValues.get("median");
					stdDeviation[indicator][problem][algorithm] = statValues.get("stdDeviation");
					iqr[indicator][problem][algorithm] = statValues.get("iqr");
					min[indicator][problem][algorithm] = statValues.get("min");
					max[indicator][problem][algorithm] = statValues.get("max");
					numberOfValues[indicator][problem][algorithm] = data[indicator][problem][algorithm].size() ;
				} // for
			} // for
		} // for

		File latexOutput ;
		latexOutput = new File(latexDirectory_);
		if (!latexOutput.exists()) {
			@SuppressWarnings("unused")
			boolean result = new File(latexDirectory_).mkdirs();
			System.out.println("Creating " + latexDirectory_ + " directory");
		} // if
		System.out.println("Experiment name: " + experimentName_) ;
		String latexFile = latexDirectory_ +"/" + experimentName_ + ".tex" ;
		printHeaderLatexCommands(latexFile) ;
		for (int i = 0; i < indicatorList_.length; i++) {
			printMeanStdDev(latexFile, i, mean, stdDeviation) ; 
			printMedianIQR(latexFile, i, median, iqr) ; 
		} // for
		printEndLatexCommands(latexFile) ;
	} // generateLatexTables


	/**
	 * Calculates statistical values from a vector of Double objects
	 * @param vector
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	void calculateStatistics( Vector              vector ,
			                  Map<String, Double> values ) {

		@SuppressWarnings("unused")
		double sum, minimum, maximum, sqsum, min, max, median, mean, iqr, stdDeviation;

		sqsum = 0.0;
		sum = 0.0;
		min = 1E300;
		max = -1E300;
		median = 0;

		for (int i = 0; i < vector.size(); i++) {
			double val = (Double) vector.elementAt(i);

			sqsum += val * val;
			sum += val;
			if (val < min) {
				min = val;
			} // if
			if (val > max) {
				max = val;
			} // if
		} // for

		// Mean
		mean = sum / vector.size();

		// Standard deviation
		if (sqsum / vector.size() - mean * mean < 0.0) {
			stdDeviation = 0.0;
		} // if
		else {
			stdDeviation = Math.sqrt(sqsum / vector.size() - mean * mean);
		} // else

		// Median
		if (vector.size() % 2 != 0) {
			median = (Double) vector.elementAt(vector.size() / 2);
		} // if
		else {
			median = ((Double) vector.elementAt(vector.size() / 2 - 1) +
					(Double) vector.elementAt(vector.size() / 2)) / 2.0;
		} // else

		values.put("mean", (Double) mean);
		values.put("median", calculateMedian(vector, 0, vector.size()-1));
		values.put("iqr", calculateIQR(vector)) ;
		values.put("stdDeviation", (Double) stdDeviation);
		values.put("min", (Double) min);
		values.put("max", (Double) max);	
	} // calculateStatistics


	/**
	 * Calculates the median of a vector considering the positions indicated by 
	 * the parameters first and last
	 * @param vector
	 * @param first index of first position to consider in the vector
	 * @param last index of last position to consider in the vector
	 * @return The median
	 */
	@SuppressWarnings("unchecked")
	Double calculateMedian( Vector vector, int first, int last ) {
		double median = 0.0;

		int size = last - first + 1 ;
		//System.out.println("size: " + size) ;

		if (size % 2 != 0) {
			median = (Double) vector.elementAt(first + size / 2);
		} // if
		else {
			median = ((Double) vector.elementAt(first + size / 2 - 1) +
					(Double) vector.elementAt(first + size / 2)) / 2.0;
		} // else

		return median;
	} // calculatemedian


	/**
	 * Calculates the interquartile range (IQR) of a vector of Doubles
	 * @param vector
	 * @return The IQR
	 */
	@SuppressWarnings("unchecked")
	Double calculateIQR( Vector vector ) {
		double q3     = 0.0 ;
		double q1     = 0.0 ;

		if (vector.size() % 2 != 0) {
			q3 = calculateMedian(vector, vector.size()/2+1, vector.size()-1) ;
			q1 = calculateMedian(vector, 0, vector.size()/2-1) ;
		} // if
		else {
			q3 = calculateMedian(vector, vector.size()/2, vector.size()-1) ;
			q1 = calculateMedian(vector, 0, vector.size()/2-1) ;
		} // else

		return q3 - q1;
	} // calculateIQR


	void printHeaderLatexCommands(String fileName) throws IOException {
		FileWriter os = new FileWriter(fileName, false);
		os.write("\\documentclass{article}"+ "\n");
		os.write("\\title{"+ experimentName_ +"}"+ "\n") ;
		os.write("\\author{}"+ "\n") ;
		os.write("\\usepackage{multirow}\n\n");
		os.write("\\begin{document}"+ "\n") ;
		os.write("\\maketitle"+ "\n") ;
		os.write("\\section{Tables}"+ "\n") ;

		os.close();
	} // printHeaderLatexCommands


	void printEndLatexCommands(String fileName) throws IOException {
		FileWriter os = new FileWriter(fileName, true);
		os.write("\\end{document}"+ "\n") ;
		os.close();
	} // printEndLatexCommands


	void printMeanStdDev(String fileName, int indicator,  double[][][] mean, double[][][]stdDev) throws IOException {
		FileWriter os = new FileWriter(fileName, true);
		os.write("\\"+"\n") ;
		os.write("\\begin{table}"+ "\n") ;
		os.write("\\caption{" + indicatorList_[indicator] + ". Mean and standard deviation}"+ "\n") ;
		os.write("\\label{table:mean."+indicatorList_[indicator]+"}"+ "\n") ;
		os.write("\\centering"+ "\n") ;
		os.write("\\begin{tabular}{l") ;

		// calculate the number of columns
		for (int i = 0; i < algorithmNameList_.length; i++)
			os.write("l") ;
		os.write("}\n") ;

		os.write("\\hline") ;
		// write table head
		for (int i = -1; i < algorithmNameList_.length; i++)
			if (i == -1)
				os.write(" & ") ;
			else if (i == (algorithmNameList_.length-1))
				os.write(" "+algorithmNameList_[i] + "\\\\"+ "\n") ;
			else
				os.write("" + algorithmNameList_[i] + " & ") ;
		os.write("\\hline"+ "\n") ;

		String m, s ;
		// write lines
		for (int i = 0 ; i < problemList_.length; i++) {
			os.write(problemList_[i] + " & ") ;
			for (int j = 0; j < (algorithmNameList_.length-1); j++) {

				m = String.format(Locale.ENGLISH,"%10.2e", mean[indicator][i][j]) ;
				s = String.format(Locale.ENGLISH,"%8.1e", stdDev[indicator][i][j]) ;
				os.write("$" + m + "_{" + s+ "}$ & " ) ;
			} // for
			m = String.format(Locale.ENGLISH,"%10.2e", mean[indicator][i][algorithmNameList_.length-1]) ;
			s = String.format(Locale.ENGLISH,"%8.1e", stdDev[indicator][i][algorithmNameList_.length-1]) ;
			os.write("$" + m + "_{" +s+ "}$ \\\\"+ "\n" ) ;
		} // for

		os.write("\\hline" + "\n") ;
		os.write("\\end{tabular}"+ "\n") ;
		os.write("\\end{table}"+ "\n") ;
		os.close() ;
	} // printMeanStdDev


	void printMedianIQR(String fileName, int indicator,  double[][][] median, double[][][]IQR) throws IOException {
		FileWriter os = new FileWriter(fileName, true);
		os.write("\\"+"\n") ;
		os.write("\\begin{table}"+ "\n") ;
		os.write("\\caption{" + indicatorList_[indicator] + ". Median and IQR}"+ "\n") ;
		os.write("\\label{table:median."+indicatorList_[indicator]+"}"+ "\n") ;
		os.write("\\centering"+ "\n") ;
		os.write("\\begin{tabular}{l") ;

		// calculate the number of columns
		for (int i = 0; i < algorithmNameList_.length; i++)
			os.write("l") ;
		os.write("}\n") ;

		os.write("\\hline") ;
		// write table head
		for (int i = -1; i < algorithmNameList_.length; i++)
			if (i == -1)
				os.write(" & ") ;
			else if (i == (algorithmNameList_.length-1))
				os.write(" "+algorithmNameList_[i] + "\\\\"+ "\n") ;
			else
				os.write("" + algorithmNameList_[i] + " & ") ;
		os.write("\\hline"+ "\n") ;

		String m, s ;
		// write lines
		for (int i = 0 ; i < problemList_.length; i++) {
			os.write(problemList_[i] + " & ") ;
			for (int j = 0; j < (algorithmNameList_.length-1); j++) {

				m = String.format(Locale.ENGLISH,"%10.2e", median[indicator][i][j]) ;
				s = String.format(Locale.ENGLISH,"%8.1e", IQR[indicator][i][j]) ;
				os.write("$" + m + "_{" + s+ "}$ & " ) ;
			} // for
			m = String.format(Locale.ENGLISH,"%10.2e", median[indicator][i][algorithmNameList_.length-1]) ;
			s = String.format(Locale.ENGLISH,"%8.1e", IQR[indicator][i][algorithmNameList_.length-1]) ;
			os.write("$" + m + "_{" +s+ "}$ \\\\"+ "\n" ) ;
		} // for
		//os.write("" + mean[0][problemList_.length-1][algorithmNameList_.length-1] + "\\\\"+ "\n" ) ;

		os.write("\\hline" + "\n") ;
		os.write("\\end{tabular}"+ "\n") ;
		os.write("\\end{table}"+ "\n") ;
		os.close() ;
	} // printMedianIQR


	/**
	 * This script produces R scripts for generating eps files containing boxplots
	 * of the results previosly obtained. The boxplots will be arranged in a grid
	 * of rows x cols. As the number of problems in the experiment can be too high, 
	 * the @param problems includes a list of the problems to be plotted.
	 * @param rows 
	 * @param cols
	 * @param problems List of problem to plot
	 * @param prefix Prefix to be added to the names of the R scripts
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public void generateRScripts( int       rows     , 
                                  int       cols     ,
                                  String [] problems ,
                                  String    prefix   )
	throws FileNotFoundException, IOException {

		// STEP 1. Creating R output directory
		File rOutput ;
		rOutput = new File(rDirectory_);
		if (!rOutput.exists()) {
			@SuppressWarnings("unused")
			boolean result = new File(rDirectory_).mkdirs();
			System.out.println("Creating " + rDirectory_ + " directory");
		} // if

		for (int indicator = 0; indicator < indicatorList_.length; indicator++) {
			System.out.println("Indicator: " + indicatorList_[indicator]) ;
			String rFile = rDirectory_ +"/" + prefix + "." + indicatorList_[indicator] + ".R" ;

			FileWriter os = new FileWriter(rFile, false);
			os.write("postscript(\"" + prefix + "." +
					indicatorList_[indicator] +
					".eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
			"\n");
			os.write("resultDirectory<-\"../" + experimentName_ +"\"" + "\n");
			os.write("qIndicator <- function(indicator, problem)" +"\n");
			os.write("{" +"\n");

			for (int i = 0; i <  algorithmNameList_.length; i++) {
				os.write("file"+algorithmNameList_[i] +
						"<-paste(resultDirectory, \""+
						algorithmNameList_[i]+ "\", sep=\"/\")" + "\n") ;
				os.write("file"+algorithmNameList_[i] +
						"<-paste(file"+algorithmNameList_[i]+ ", "+
						"problem, sep=\"/\")" + "\n") ;
				os.write("file"+algorithmNameList_[i] +
						"<-paste(file"+algorithmNameList_[i]+ ", "+
						"indicator, sep=\"/\")" + "\n") ;
				os.write(algorithmNameList_[i]+"<-scan("+ "file"+algorithmNameList_[i]+")"+ "\n") ;
				os.write("\n") ;
			} // for

			os.write("algs<-c(");
			for (int i = 0; i <  algorithmNameList_.length -1 ; i++) {
				os.write("\""+algorithmNameList_[i] + "\",") ;
			} // for
			os.write("\""+algorithmNameList_[algorithmNameList_.length-1] + "\")" + "\n") ;

			os.write("boxplot(") ;
			for (int i = 0; i <  algorithmNameList_.length ; i++) {
				os.write(algorithmNameList_[i] + ",") ;
			} // for
			os.write("names=algs)" + "\n") ;
			os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n") ;
			os.write("title(main=titulo)" + "\n") ;

			os.write("}" +"\n");

			os.write("par(mfrow=c("+rows+","+cols+"))" + "\n") ;

			os.write("indicator<-\"" + indicatorList_[indicator] + "\"" + "\n") ;

			for (int i = 0; i < problems.length; i++)
				os.write("qIndicator(indicator, \"" + problems[i] + "\")"+"\n") ;

			os.close();
		} // for

	} // generateRScripts


} // Experiment
