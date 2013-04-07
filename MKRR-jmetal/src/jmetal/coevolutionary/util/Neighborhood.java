package jmetal.coevolutionary.util;


import jmetal.coevolutionary.base.SolutionSet;


/**
 * Class representing neighborhoods for a <code>Solution</code> into a
 * <code>SolutionSet</code>.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */ 
public class Neighborhood {    

	private static int MAXRADIO = 2    ; ///< Maximum rate considered
	private int        solutionSetSize_; ///< Stores the size of the solutionSet.
	private int        rowSize_        ; ///< Stores the size for each row

	enum Row { N , S , E , W , NW , SW , NE , SE }; ///< Enum type for defining the North, South, ... neighbor.

	/**
	 * Stores the neighborhood.
	 * structure_ [i] represents a neighborhood for a solution.
	 * structure_ [i][j] represents a neighborhood with a ratio.
	 * structure_ [i][j][k] represents a neighbor of a solution.
	 */
	private int[][][] structure_;


	/**
	 * Constructor.
	 * Defines a neighborhood of a given size.
	 * @param solutionSetSize The size.
	 */
	public Neighborhood( int solutionSetSize ){
		solutionSetSize_ = solutionSetSize;
		//Create the structure_ for store the neighborhood
		structure_ = new int[solutionSetSize_][MAXRADIO][];

		//For each individual, and different rates the individual has a different 
		//number of neighborhoods
		for (int ind = 0; ind < solutionSetSize_; ind ++) {
			for (int radio = 0; radio < MAXRADIO; radio ++) {
				if (radio == 0) {//neighboors whit rate 1
					structure_[ind][radio] = new int[8];
				} // if
				else if (radio == 1) { //neighboors whit rate 2
					structure_[ind][radio] = new int[24];       
				} // else if
			} // for
		} // for

		//Calculate the size of a row
		rowSize_ = (int) Math.sqrt((double)solutionSetSize_);


		//Calculates the neighbors of a individual 
		for (int ind = 0; ind < solutionSetSize_; ind++){
			// rate 1
			// North neighbors
			if (ind > rowSize_ - 1)
				structure_[ind][0][Row.N.ordinal()] = ind - rowSize_;
			else
				structure_[ind][0][Row.N.ordinal()] = (ind - rowSize_ + solutionSetSize) % solutionSetSize;

			// East neighbors
			if  ((ind + 1) % rowSize_ == 0)
				structure_[ind][0][Row.E.ordinal()] = (ind - (rowSize_ - 1));
			else
				structure_[ind][0][Row.E.ordinal()] = (ind + 1);

			// West neigbors
			if (ind % rowSize_ == 0)
				structure_[ind][0][Row.W.ordinal()] = (ind + (rowSize_ - 1));
			else
				structure_[ind][0][Row.W.ordinal()] = (ind - 1);

			// South neigbors
			structure_[ind][0][Row.S.ordinal()] = (ind + rowSize_) % solutionSetSize;                        
		} // for

		for (int ind = 0; ind < solutionSetSize_; ind++){
			structure_[ind][0][Row.NE.ordinal()] = structure_[structure_[ind][0][Row.N.ordinal()]][0][Row.E.ordinal()];
			structure_[ind][0][Row.NW.ordinal()] = structure_[structure_[ind][0][Row.N.ordinal()]][0][Row.W.ordinal()];
			structure_[ind][0][Row.SE.ordinal()] = structure_[structure_[ind][0][Row.S.ordinal()]][0][Row.E.ordinal()];
			structure_[ind][0][Row.SW.ordinal()] = structure_[structure_[ind][0][Row.S.ordinal()]][0][Row.W.ordinal()];
		} // for

	} // Neighborhood


	/**
	 * Returns a <code>SolutionSet</code> with the North, Sout, East and West
	 * neighbors solutions of ratio 0 of a given location into a given 
	 * <code>SolutionSet</code>.
	 * @param solutionSet The <code>SolutionSet</code>.
	 * @param location The location.
	 * @return a <code>SolutionSet</code> with the neighbors.
	 */
	public SolutionSet getFourNeighbors( SolutionSet solutionSet , int location ){
		// SolutionSet that contains the neighbors (to return)
		SolutionSet neighbors;

		// Instance the solutionSet to a non dominated list of individuals
		neighbors = new SolutionSet( solutionSet.getLoadingPosition()   ,
				                     solutionSet.getNumberOfIslands()   ,
				                     solutionSet.getNumberOfSolutions() ,
				                     24                                 );
		neighbors.setBestExternResults( solutionSet );

		// Gets the neighboords N, S, E, W
		int index;        

		// North
		index = structure_[location][0][Row.N.ordinal()];        
		neighbors.add( solutionSet.get(index) );

		// South
		index = structure_[location][0][Row.S.ordinal()];
		neighbors.add( solutionSet.get(index) );

		// East
		index = structure_[location][0][Row.E.ordinal()];
		neighbors.add( solutionSet.get(index) );

		// West
		index = structure_[location][0][Row.W.ordinal()];
		neighbors.add( solutionSet.get(index) );

		// Return the list of non-dominated individuals
		return neighbors;        
	} // getFourNeighbors


	/**
	 * Returns a <code>SolutionSet</code> with the North, Sout, East, West, 
	 * North-West, South-West, North-East and South-East neighbors solutions of
	 * ratio 0 of a given location into a given <code>SolutionSet</code>.
	 * solutions of a given location into a given <code>SolutionSet</code>.
	 * @param population The <code>SolutionSet</code>.
	 * @param individual The individual.
	 * @return a <code>SolutionSet</code> with the neighbors.
	 */
	public SolutionSet getEightNeighbors( SolutionSet population , int individual ){
		SolutionSet neighbors; ///< SolutionSet that contains the neighbors (to return)

		// Instance the population to a non dominated li of individuals
		neighbors = new SolutionSet( population.getLoadingPosition()   ,
				                     population.getNumberOfIslands()   ,
                                     population.getNumberOfSolutions() ,
                                     24                                 );
		// Get the best of population and sets in neighbors
		neighbors.setBestExternResults( population );

		// Gets the neighboords N, S, E, W
		int index;        

		// North
		index = this.structure_[individual][0][Row.N.ordinal()];        
		neighbors.add(population.get(index));

		// South
		index = this.structure_[individual][0][Row.S.ordinal()];
		neighbors.add(population.get(index));

		// East
		index = this.structure_[individual][0][Row.E.ordinal()];
		neighbors.add(population.get(index));

		// West
		index = this.structure_[individual][0][Row.W.ordinal()];
		neighbors.add(population.get(index));

		// NorthEast
		index = this.structure_[individual][0][Row.NE.ordinal()];
		neighbors.add(population.get(index));

		// NorthWest
		index = this.structure_[individual][0][Row.NW.ordinal()];
		neighbors.add(population.get(index));

		// SouthEast
		index = this.structure_[individual][0][Row.SE.ordinal()];
		neighbors.add(population.get(index));

		// SouthWest
		index = this.structure_[individual][0][Row.SW.ordinal()];
		neighbors.add(population.get(index));

		//Return the list of non-dominated individuals
		return neighbors;        
	}  // getEightNeighbors

} // Neighborhood
