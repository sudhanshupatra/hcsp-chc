
package jmetal.coevolutionary.util;


/** This class implements a register to store the statistics
* 
* @author Juan A. Ca–ero
* @version 1.0
*/
public class StatReg {

	public double mean_         ;
	public double median_       ;
	public double stdDeviation_ ;
	public double iqr_          ;
	public double max_          ;
	public double min_          ;

	/**
	 *  Constructor
	 */
	public StatReg() {
	} // StatReg


	/** Constructor
	 * @param mean Mean value
	 * @param median Median value
	 * @param stdDeviation standart deviation
	 * @param iqr interquartile
	 * @param max maximum
	 * @param min mininimum
	 */
	public StatReg( double mean , double median , double stdDeviation ,
                    double iqr  , double max    , double min          ) {
		
		this.mean_         = mean;
		this.median_       = median;
		this.stdDeviation_ = stdDeviation;
		this.iqr_          = iqr;
		this.max_          = max;
		this.min_          = min;
	} // StatReg
	
} // StatReg
