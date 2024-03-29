package jmetal.coevolutionary.qualityIndicator;


import jmetal.coevolutionary.qualityIndicator.util.MetricsUtil;


/**
 * This class implements the generational distance metric. It can be used also 
 * as a command line by typing: "java GenerationalDistance [solutionFrontFile]  
 * [trueFrontFile] [numberOfObjectives]"
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary 
 *            Algorithm Research: A History and Analysis. 
 *            Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force 
 *            Inst. Technol. (1998)
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca�ero
 * @version 1.0
 */
public class GenerationalDistance {

	MetricsUtil         utils_;     ///< utils_ is used to access to the MetricsUtil funcionalities
	static final double pow_ = 2.0; ///< pow. This is the pow used for the distances


	/**
	 * Constructor.
	 * Creates a new instance of the generational distance metric. 
	 */
	public GenerationalDistance() {
		utils_ = new MetricsUtil();
	} // GenerationalDistance


	/**
	 * Returns the generational distance value for a given front
	 * @param front The front 
	 * @param trueParetoFront The true pareto front
	 * @param numberOfObjectives
	 */
	public double generationalDistance( double[][] front,
			                            double[][] trueParetoFront, 
			                            int        numberOfObjectives ) {
		double[]   maximumValue          ; ///< Stores the maximum values of true pareto front.
		double[]   minimumValue          ; ///< Stores the minimum values of the true pareto front.
		double[][] normalizedFront       ; ///< Stores the normalized front.
		double[][] normalizedParetoFront ; ///< Stores the normalized true Pareto front.

		// STEP 1. Obtain the maximum and minimum values of the Pareto front
		maximumValue = utils_.getMaximumValues( trueParetoFront , numberOfObjectives );
		minimumValue = utils_.getMinimumValues( trueParetoFront , numberOfObjectives );

		// STEP 2. Get the normalized front and true Pareto fronts
		normalizedFront       = utils_.getNormalizedFront( front           , maximumValue , minimumValue );
		normalizedParetoFront = utils_.getNormalizedFront( trueParetoFront , maximumValue , minimumValue );

		// STEP 3. Sum the distances between each point of the front and the 
		// nearest point in the true Pareto front
		double sum = 0.0;
		for (int i = 0; i < front.length; i++) 
			sum += Math.pow( utils_.distanceToClosedPoint( normalizedFront[i]    ,
					                                       normalizedParetoFront ) ,
					         pow_ );


		// STEP 4. Obtain the sqrt of the sum
		sum = Math.pow( sum,1.0/pow_ );

		// STEP 5. Divide the sum by the maximum number of points of the front
		double generationalDistance = sum / normalizedFront.length;

		return generationalDistance;
	} // generationalDistance


	/**
	 * This class can be invoqued from the command line. Two params are required:
	 * 1) the name of the file containing the front, and 2) the name of the file 
	 * containig the true Pareto front
	 **/
	public static void main(String args[]) {
		if (args.length < 2) {
			System.err.println("GenerationalDistance::Main: Usage: java " +
					"GenerationalDistance <FrontFile> " +
			"<TrueFrontFile>  <numberOfObjectives>");
			System.exit(1);
		} // if

		// STEP 1. Create an instance of Generational Distance
		GenerationalDistance qualityIndicator = new GenerationalDistance();

		// STEP 2. Read the fronts from the files
		double [][] solutionFront = qualityIndicator.utils_.readFront(args[0]);
		double [][] trueFront     = qualityIndicator.utils_.readFront(args[1]);

		// STEP 3. Obtain the metric value
		double value = qualityIndicator.generationalDistance(
				solutionFront,
				trueFront, 
				(new Integer(args[2])).intValue());

		System.out.println(value);  
	} // main

} // GenerationalDistance
