package jmetal.coevolutionary.util;


/**
 * Class to estimate the remaining time of a program
 * 
 * @author Juan A. Caero
 * @version 1.1
 */
public class TimeEstimation {

	private long numberOfIterations_; ///< Total number of iterations to perform
	private long currentIteration_;   ///< The current iteration
	private long startTime_;          ///< Start time

	
	/** Constructor
	 * 
	 * @param numberOfIterations total number of iterations to perform
	 */
	public TimeEstimation( long numberOfIterations ) {
		
		numberOfIterations_ = numberOfIterations;
		currentIteration_ = 0;
		startTime_ = System.currentTimeMillis();
	} // TimeEstimation
	
	
	/**
	 *  Perform one iteration
	 */
	public void iteration(){
		
		++currentIteration_;
	} // iteration
	

	/** Calc the percentage done
	 * @return The percentage done
	 */
	public int getPercentageDone(){

		return (int) ((100*currentIteration_)/numberOfIterations_);
	} // getPercentageDone
	
	
	/** Get the elapsed time
	 * @return The elapsed time
	 */
	public long getElapsedTime(){
		long currentTime = System.currentTimeMillis();
		long elapsedTime = startTime_ - currentTime;
		
		return elapsedTime;
	} // getElapsedTime
	
	
	/** Get an estimation of the remaining time
	 * @return An estimation of the remaining time
	 */
	public long getRemainingTime(){
		
		long currentTime = System.currentTimeMillis();
		long elapsedTime = startTime_ - currentTime;
		if ( currentIteration_ == 0 )
			return Long.MAX_VALUE;
		long remaining   = ( (numberOfIterations_-currentIteration_) * elapsedTime ) / currentIteration_;
		
		return remaining;
	} // getRemainingTime
	
	
	/** Get an estimation of the remaining time in human readable format
	 * @return a string containing the remaining time
	 */
	public String getRemainingHumanReadable(){
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - startTime_;
		if ( currentIteration_ == 0 )
			return "Infinite";
		double percentageDone = (double) (numberOfIterations_-currentIteration_) / (double) currentIteration_;
		long remaining   = (long) ( percentageDone * (double) elapsedTime );
		
		String S = "";
		int n=0;
		
		if ( remaining/604800000>0 ) {
			S += toStr( remaining/604800000 , "week" );
			remaining = (remaining % 604800000);
			if ( n== 0 ) S += ", ";
			++n;
		} // if
		
		if  (remaining/86400000>0) {
			S += toStr( remaining/86400000 , "day" );
			remaining = (remaining % 86400000);
			if ( n== 0 ) S += ", ";
			++n;
		} // if
		
		if ( (n<2) && ( remaining/3600000>0 ) ) {
			S += toStr( remaining/3600000 , "hour" );
			remaining = (remaining % 3600000);
			if ( n== 0 ) S += ", ";
			++n;
		} // if
		
		if ( (n<2) && ( remaining/60000>0 ) ){
			S += toStr( remaining/60000 , "min" );
			remaining = (remaining % 60000);
			if ( n== 0 ) S += ", ";
			++n;
		} // if


		if ( n<2 ){
			S += toStr( remaining/1000 , "sec" );
			remaining = (remaining % 1000);
			++n;
		} // if
		
		return S;
	} // ToString
	
	
	private String toStr( long value , String measure ){
		String ret = (new Long( value )).toString() + " " + measure;
		
		if ( value!=1 )
			ret += "s";
		
		return ret;
	} // toStr

} // TimeEstimation
