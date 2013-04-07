package jmetal.coevolutionary.base;


/**
 * Class to manage the common part of a population
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class CommonDecisionVariables {
	
	private int                   numberOfSolutions_ ; ///< Stores the number of solutions
	private int                   numberOfIslands_   ; ///< Stores the number of Islands
	private DecisionVariables[][] matrix_            ; ///< Stores the matrix of decision variables


	/** Constructor
	 * 
	 * @param numberOfSolutions Number of solutions to manage
	 * @param numberOfIslands Number of islands
	 */
	public CommonDecisionVariables( int numberOfSolutions , int numberOfIslands ){
		
		numberOfSolutions_ = numberOfSolutions ;
		numberOfIslands_   = numberOfIslands   ;
		
		matrix_ = new DecisionVariables[numberOfSolutions_][];
		for( int i=0 ; i<numberOfSolutions_ ; ++i ) {
			DecisionVariables[] newRow = new DecisionVariables[numberOfIslands_-1];
			matrix_[i] = newRow;
		} // for
	} // CommonDecisionVariables
	
	
	/** Return a row of DecisionVariables in order to create a complete Solution
	 * 
	 * @param rowNum The desired number of row
	 * @return A row of DecisionVariables
	 */
	public DecisionVariables[] getRow( int rowNum ){
		int index;
	
		if ( ( rowNum >= 0) && ( rowNum < numberOfSolutions_ ) )
			index = rowNum;
		else if ( rowNum >= numberOfSolutions_ )
			index = numberOfSolutions_-1;
		else
			index = 0;

		return matrix_[index];
	} // getRow
	
	/** Returns a row of DecisionVariables, but instead of picking from every island
	 *  the partial solution in the same row, it picks the partial solution from a 
	 *  random row in every island 
	 * 
	 * @return A row of DecisionVariables
	 */
	public DecisionVariables[] getRow(){
			
		DecisionVariables[] dv = new DecisionVariables[numberOfIslands_-1];
		for (int i=0; i<numberOfIslands_-1; i++) {
			int index = (int) Math.floor( numberOfSolutions_*Math.random() );
			dv[i] = matrix_[index][i];
		}

		return dv;
	} // getRow


	/** Store the new row replacing the old one
	 * 
	 * @param newRowNum The number of row to be replaced
	 * @param newRow The new row
	 */
	public void setRow( int newRowNum , DecisionVariables[] newRow ){
		int index;

		if ( ( newRowNum >= 0) && ( newRowNum < numberOfSolutions_ ) )
			index = newRowNum;
		else if ( newRowNum >= numberOfSolutions_ )
			index = numberOfSolutions_-1;
		else
			index = 0;

		System.arraycopy( newRow , 0 , matrix_[index] , 0 , numberOfIslands_-1 );
	} // setRow


	/** Get the number of Solutions stored
	 * @return The Number of solutions
	 */
	public int getNumberOfSolutions() {

		return numberOfSolutions_;
	} // getNumberOfSolutions


	/** Store the new column, replacing the old one. Represents the best solutions of the rest of the islands
	 * @param newColumnNum The number of column
	 * @param newColumn The new column
	 */
	public void setColumn( int newColumnNum , DecisionVariables[] newColumn ) {
		int columnNumber;
		
		int length = ( numberOfSolutions_<=newColumn.length )? numberOfSolutions_ : newColumn.length;
		if ( ( newColumnNum>=0 ) && ( newColumnNum<(numberOfIslands_-1)) )
			columnNumber = newColumnNum;
		else if ( newColumnNum<0 )
			columnNumber = 0;
		else
			columnNumber = numberOfIslands_-2;
		
		for( int j=0 ; j<length ; ++j )
			matrix_[j][columnNumber] = newColumn[j];
	} // setColumn


	/**
	 * This method clones a commonDecisionVariables
	 * @return the cloned object
	 */
	public CommonDecisionVariables clone(){
		CommonDecisionVariables cloned = new CommonDecisionVariables( numberOfSolutions_ , numberOfIslands_ );
		
		for( int j=0 ; j<numberOfSolutions_ ; ++j )
			for( int i=0 ; i<numberOfIslands_-1 ; ++i )
				if ( matrix_[j][i]!=null )
					cloned.matrix_[j][i] = new DecisionVariables( matrix_[j][i] );
				else
					cloned.matrix_[j][i] = null;

		return( cloned );
	} // clone

} // CommonDecisionVariables
