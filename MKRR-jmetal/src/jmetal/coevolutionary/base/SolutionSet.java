package jmetal.coevolutionary.base;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jmetal.base.Configuration;

import jmetal.coevolutionary.base.Solution;


@SuppressWarnings("unchecked")
/** 
 * Class representing a SolutionSet (a set of solutions)
 */
public class SolutionSet implements Serializable {

	private static final long serialVersionUID = 9166035607851159745L;

	/**
	 * Stores a list of semi-<code>solution</code> objects. (See above)
	 */
	protected List<Solution> solutionsList_;

	private CommonDecisionVariables bestExternResults_; ///< Stores the best of another populations of another islands


	/**
	 * Stores the position where a <code>solution</code> of <code>solutionList_</code>
	 * must be inserted in order to obtain the whole <code>solution</code>. 
	 */
	private int loadingPosition_;
	
	private int capacity_ = 0      ; ///< Maximum size of the solution set 
	private int numberOfSolutions_ ; ///< Number of solution of another islands that must store 
	private int numberOfIslands_   ;
	
	private int mergeSolution_    ; // how to use the partial solutions from the islands to build the complete solution from mine
	/* 
	 * How to merge solutions? 
	 * - if 1 we pick on random partial solution from the shared 
	 *   ones in every island; 
	 * - if 0, then it is like in the book chapter, we choose a random 
	 *   index and compose the solution with the shared solution index 
	 *   in every island
	 * 
	 */

	/**
	 * Creates an unbounded solution set.
	 * @param loadingPosition position to insert the solutionset
	 * @param numberOfIslands number of islands to use
	 * @param numberOfSolutions number of common solutions to maintain the diversity, given by another populations of another islands
	 */
	public SolutionSet( int loadingPosition , int numberOfIslands , int numberOfSolutions ) {

		solutionsList_ = new ArrayList<Solution>();
		loadingPosition_ = loadingPosition;
		numberOfSolutions_ = numberOfSolutions;
		numberOfIslands_ = numberOfIslands;
		if ( numberOfIslands > 1 )
			bestExternResults_ = new CommonDecisionVariables( numberOfSolutions , numberOfIslands );
		else
			bestExternResults_ = null;
	} // SolutionSet


	/** 
	 * Creates a empty solutionSet with a maximum capacity.
	 * @param loadingPosition position to insert the solutionset
	 * @param numberOfIslands number of islands to use
	 * @param numberOfSolutions number of common solutions in order to maintain the diversity
	 * @param maximumSize Maximum size.
	 */
	public SolutionSet( int loadingPosition , int numberOfIslands , int numberOfSolutions , int maximumSize ){  

		this( loadingPosition , numberOfIslands , numberOfSolutions);
		capacity_ = maximumSize;
	} // SolutionSet

	
	
	/** 
	 * Creates a empty solutionSet with a maximum capacity.
	 * @param loadingPosition position to insert the solutionset
	 * @param numberOfIslands number of islands to use
	 * @param numberOfSolutions number of common solutions in order to maintain the diversity
	 * @param maximumSize Maximum size.
	 */
	public SolutionSet( int loadingPosition , int numberOfIslands , int numberOfSolutions , int maximumSize , int mergeSolution){  

		this( loadingPosition , numberOfIslands , numberOfSolutions);
		capacity_ = maximumSize;
		mergeSolution_ = mergeSolution;
	} // SolutionSet

	/** 
	 * Inserts a new solution into the SolutionSet. 
	 * @param solution The <code>Solution</code> to store
	 * @return True If the <code>Solution</code> has been inserted, false otherwise. 
	 */
	public boolean add( Solution solution ){
		
		if ( solutionsList_.size()==capacity_ ) {
			Configuration.logger_.severe("[The population is full");
			Configuration.logger_.severe("Capacity       is : "+capacity_);
			Configuration.logger_.severe("Size is: "+ this.size()+"]");
			return false;
		} // if

		if ( (numberOfIslands_>1) && !solution.isLinked() ) {
			int i = (int) Math.floor( numberOfSolutions_*Math.random() );
			solution.assignExternalDV( bestExternResults_.getRow(i) );
		} // if
		solutionsList_.add( solution );
		return true;
	} // add


	/**
	 * Returns the ith solution in the set.
	 * @param i Position of the solution to obtain.
	 * @return The <code>Solution</code> at the position i.
	 * @throws IndexOutOfBoundsException.
	 */
	public Solution get( int i ){
		if ( i >= solutionsList_.size() ) {
			throw new IndexOutOfBoundsException( "Index out of Bound " + i );
		} // if
		return solutionsList_.get(i);
	} // get


	/**
	 * Returns the maximum capacity of the solution set
	 * @return The maximum capacity of the solution set
	 */
	public int getMaxSize(){
		return capacity_ ;
	} // getMaxSize


	/** 
	 * Sorts a SolutionSet using a <code>Comparator</code>.
	 * @param comparator <code>Comparator</code> used to sort.
	 */
	public void sort(Comparator comparator){
		if (comparator == null) {
			Configuration.logger_.severe("No criterium for compare exist");
			return ;
		} // if
		Collections.sort(solutionsList_,comparator);
	} // sort


	/** 
	 * Returns the number of solutions in the SolutionSet.
	 * @return The size of the SolutionSet.
	 */  
	public int size(){
		return solutionsList_.size();
	} // size


	/** 
	 * Writes the objective funcion values of the <code>Solution</code> 
	 * objects into the set in a file.
	 * @param path The output file name
	 */
	public void printObjectivesToFile(String path){
		try {
			/* Open the file */
			FileOutputStream fos   = new FileOutputStream(path)     ;
			OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
			BufferedWriter bw      = new BufferedWriter(osw)        ;

			for (int i = 0; i < solutionsList_.size(); i++) {
				bw.write(solutionsList_.get(i).toString());
				bw.newLine();
			} // for
			/* Close the file */
			bw.close();
		} // try
		catch (IOException e) {
			Configuration.logger_.severe("Error acceding to the file");
			e.printStackTrace();
		} // catch
	} // printObjectivesToFile


	/**
	 * Writes the decision variable values of the <code>Solution</code>
	 * solutions objects into the set in a file.
	 * @param path The output file name
	 */
	public void printVariablesToFile(String path){
		try {
			/* Open the file */
			FileOutputStream fos   = new FileOutputStream(path)     ;
			OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
			BufferedWriter bw      = new BufferedWriter(osw)        ;            

			for (int i = 0; i < solutionsList_.size(); i++) {  
				bw.write(solutionsList_.get(i).getDecisionVariables().toString());
				bw.newLine();        
			}

			/* Close the file */
			bw.close();
		} // try
		catch (IOException e) {
			Configuration.logger_.severe("Error acceding to the file");
			e.printStackTrace();
		} // catch
	} // printVariablesToFile


	/** 
	 * Empties the SolutionSet
	 */
	public void clear(){
		solutionsList_.clear();
	} // clear

	/** 
	 * Deletes the <code>Solution</code> at position i in the set.
	 * @param i The position of the solution to remove.
	 */
	public void remove(int i){        
		if (i > solutionsList_.size()-1) {            
			Configuration.logger_.severe("Size is: "+this.size());
		} // if
		solutionsList_.remove(i);    
	} // remove


	/**
	 * Returns an <code>Iterator</code> to access to the solution set list.
	 * @return the <code>Iterator</code>.
	 */
	public Iterator<Solution> iterator(){
		return solutionsList_.iterator();
	} // iterator   


	/** 
	 * Returns a new <code>SolutionSet</code> which is the result of the union
	 * between the current solution set and the one passed as a parameter.
	 * @param solutionSet SolutionSet to join with the current solutionSet.
	 * @return The result of the union operation.
	 */
	public SolutionSet union(SolutionSet solutionSet) {

		//Check the correct size. In development
		int newSize = this.size() + solutionSet.size();
		if (newSize < capacity_)
			newSize = capacity_;

		// Create a new population
		SolutionSet union;

		union = new SolutionSet( loadingPosition_ , numberOfIslands_ , numberOfSolutions_ , newSize );
		union.bestExternResults_ = bestExternResults_;

		for (int i = 0; i < this.size(); i++)
			union.add( this.get(i) );

		for (int i = this.size(); i < (this.size() + solutionSet.size()); i++)
			union.add(solutionSet.get(i-this.size()));

		return union;        
	} // union                   


	/** 
	 * Replaces a solution by a new one
	 * @param position The position of the solution to replace
	 * @param solution The new solution
	 */
	public void replace(int position, Solution solution) {
		
		if ( (numberOfIslands_>1) && !solution.isLinked() ) {
			int i = (int) Math.floor( numberOfSolutions_*Math.random() );
			solution.assignExternalDV( bestExternResults_.getRow(i) );
		} // if
		
		if (position > this.solutionsList_.size()) {
			solutionsList_.add(solution);
		} // if 
		solutionsList_.remove(position);
		solutionsList_.add(position,solution);
	} // replace


	/**
	 * Copies the objectives of the solution set to a matrix
	 * @return A matrix containing the objectives
	 */
	public double[][] writeObjectivesToMatrix() {

		if (this.size() == 0) {
			return null;
		} // if
		double[][] objectives;
		objectives = new double[size()][get(0).numberOfObjectives()];
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < get(0).numberOfObjectives(); j++) {
				objectives[i][j] = get(i).getObjective(j);
			} // for
		} // for
		return objectives;
	} // writeObjectivesMatrix


	/**
	 * @param externalBest the values to set
	 * @param position position to insert
	 */
	public void setRemainBest( DecisionVariables[] externalBest , int position ) {

		if ( bestExternResults_!=null ){
			if ( position < loadingPosition_ ) bestExternResults_.setColumn( position   , externalBest  );
			if ( position > loadingPosition_ ) bestExternResults_.setColumn( position-1 , externalBest );
		} // if
	} // setRemainBest


	/**
	 * @return the position_
	 */
	public int getLoadingPosition() {
		return loadingPosition_;
	} // getPosition_


	/**
	 * This method returns the number of islands
	 * @return The number of islands
	 */
	public int getNumberOfIslands(){

		return numberOfIslands_;
	} // getNumberOfIslands


	/**
	 * This method returns the extended solutionSet (if exists several islands)
	 * @return The new SolutionSet containing all the Solutions -but extended- OR this object 
	 */
	public SolutionSet getExtendedSolutionSet(){
		SolutionSet extendedSolutionSet;

		if ( bestExternResults_!=null ) {
			extendedSolutionSet = new SolutionSet( 0 , 1 , 0 , capacity_ );
			int numElem = this.solutionsList_.size();
			for( int f=0 ; f<numElem ; ++f )
				extendedSolutionSet.add( new Solution( solutionsList_.get(f) , loadingPosition_ ) );
		} // if
		else {
			extendedSolutionSet = this;
		} // else
		return( extendedSolutionSet );
	} // getExtendedSolutionSet


	/** Assign the same extern results if it is an empty SolutionSet
	 * @param source 
	 */
	public void setBestExternResults( SolutionSet source ){

		if ( solutionsList_.size() == 0 )
			bestExternResults_ = source.bestExternResults_;
	} // setBestExternResults


	/** get the number of extern solutions
	 * @return the number of extern solutions
	 */
	public int getNumberOfSolutions() {

		return numberOfSolutions_;
	} // getNumberOfSolutions


	/** 
	 * link the vector of external variables of decision to one of this population
	 * 
	 * @param solution The solution to link
	 */
	public void linkExternalDecisionVariables( Solution solution ) {

		if ( numberOfIslands_>1 ) {
			if (mergeSolution_ == 0) {
				int i = (int) Math.floor( numberOfSolutions_*Math.random() );
				solution.assignExternalDV( bestExternResults_.getRow(i) );	
			}
			else
				solution.assignExternalDV( bestExternResults_.getRow() );
		} // if
	} // linkExternalDecisionVariables
	
	
	/**
	 * link the vector of external variables of decision to one of this population
	 * 
	 * @param parent
	 * @param offspring
	 */
//	public void linkExternalDecisionVariables( Solution parent , Solution offspring ) {
//
//		offspring.assignExternalDV( parent.getExternalDecisionVariables() );
//	} // linkExternalDecisionVariables


	// FIXME poner nombre mejor a la criatura
	public void setBestRow(DecisionVariables[] best, int i) {
		
		this.bestExternResults_.setRow(i, best);
	} // setBestRow


	// FIXME poner nombre mejor a la criatura
	public DecisionVariables[] getBestRow( int i) {
		
		//return bestExternResults_.getRow(i);
		if (mergeSolution_ == 0)
			return bestExternResults_.getRow(i);	
		else
			return bestExternResults_.getRow();
	} // getBestRow

} // SolutionSet
