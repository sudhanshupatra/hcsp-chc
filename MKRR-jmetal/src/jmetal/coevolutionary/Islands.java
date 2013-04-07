/**
 * Coevolutionary package
 */
package jmetal.coevolutionary;


import java.util.Iterator;

import jmetal.coevolutionary.base.archive.CrowdingArchive;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.util.JMException;


/**
 * Class to control a set of islands
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class Islands {
	
	private int         numberOfIslands_; ///< Number of islands to use
	private Problem     problem_        ; ///< The problem to solve
	private Algorithm[] algorithm_      ; ///< The algorithms to use
	private int         populationSize_ ; ///< The size of the population
	
	private int index;

	/** Build the islands
	 * @param problem The problem to solve
	 * @param algorithm The algorithm to solve
	 */
	public Islands( Problem problem , Algorithm algorithm ) {

		numberOfIslands_ = ((Integer) algorithm.getInputParameter( "numberOfIslands" )).intValue();
		populationSize_  = ((Integer) algorithm.getInputParameter( "populationSize")  ).intValue();
		problem_         = problem;
		algorithm_       = new Algorithm[numberOfIslands_];
		for( int i=0 ; i<numberOfIslands_ ; ++i )
			algorithm_[i] = algorithm.clone();
	}  // Islands


	/**
	 * Starts the execution in parallel of the algorithm
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution
	 * @throws JMException
	 * 
	 * @see executeSequential
	 */
	public SolutionSet execute() throws JMException {
		
		Thread t[]= new Thread[numberOfIslands_];

		// Create the setup threads
		for( index=0 ; index<numberOfIslands_ ; ++index)
			t[index] = new Thread( new
						            Runnable() {
						                int i=index;
						
						                public void run (){
						
						                    try {
						                        algorithm_[i].setup( i );
						                    } // try
						                    catch (JMException e) {
						                        e.printStackTrace();
						                    } // cath
						                } // run
						            } // Runnable
                            );
		// Execute the setup threads
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			t[i].start();
		
		// Wait for the setup threads to finish
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			try {
				t[i].join();
			} // try
			catch (InterruptedException e) {
				e.printStackTrace();
			} // catch

		// Broadcast the best solutions, setup and broadcast must locate in different loops
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			broadcastSetUpSolution( i );

		// First evaluation (only need once explicity)
		// Create the setup threads
		for( index=0 ; index<numberOfIslands_ ; ++index)
			t[index] = new Thread( new
						            Runnable() {
						                int i=index;
						
						                public void run (){
						
						                    try {
						                        algorithm_[i].evaluatePopulation();
						                    } // try
						                    catch (JMException e) {
						                        e.printStackTrace();
						                    } // cath
						                } // run
						            } // Runnable
                            );
		// Execute the setup threads
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			t[i].start();
		
		// Wait for the setup threads to finish
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			try {
				t[i].join();
			} // try
			catch (InterruptedException e) {
				e.printStackTrace();
			} // catch

		// The main loop
		while ( !algorithm_[0].stopCondition() ) {
			// Generation
			// Create the generation threads
			for( index=0 ; index<numberOfIslands_ ; ++index)
				t[index] = new Thread( new
							            Runnable() {
							                int i=index;
							
							                public void run (){
							
							                    try {
							                        algorithm_[i].generation();
							                    } // try
							                    catch (JMException e) {
							                        e.printStackTrace();
							                    } // cath
							                } // run
							            } // Runnable
	                            );
			// Execute the generation threads
			for( int i=0 ; i<numberOfIslands_ ; ++i)
				t[i].start();
			
			// Wait for the generation threads to finish
			for( int i=0 ; i<numberOfIslands_ ; ++i)
				try {
					t[i].join();
				} // try
				catch (InterruptedException e) {
					e.printStackTrace();
				} // catch
			// Create the postGeneration threads
			for( index=0 ; index<numberOfIslands_ ; ++index)
				t[index] = new Thread( new
								            Runnable() {
								                int i=index;
								
								                public void run (){
								                    algorithm_[i].postGeneration();
								                } // run
								            } // Runnable
		                            );
			// Execute the postGeneration threads
			for( int i=0 ; i<numberOfIslands_ ; ++i)
				t[i].start();
				
			// Wait for the postGeneration threads to finish
			for( int i=0 ; i<numberOfIslands_ ; ++i)
				try {
					t[i].join();
				} // try
				catch (InterruptedException e) {
					e.printStackTrace();
				} // catch
			// Broadcast
			for( int i=0 ; i<numberOfIslands_ ; ++i )
				broadcastBestSolution( i );
			
		} // while
		// Post execution
		// Create the postExecution threads
		for( index=0 ; index<numberOfIslands_ ; ++index)
			t[index] = new Thread( new
						            Runnable() {
						                int i=index;
						
						                public void run (){
						                    algorithm_[i].postExecution();
						                } // run
						            } // Runnable
                            );
		// Execute the postExecution threads
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			t[i].start();
		
		// Wait for the postExecution threads to finish
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			try {
				t[i].join();
			} // try
			catch (InterruptedException e) {
				e.printStackTrace();
			} // catch
		
		// Unify the solutions of the islands
		return unifySolutionSet();
	} // execute
	
	
	/**
	 * Starts the execution of the algorithm in sequential mode
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution
	 * @throws JMException
	 * 
	 * @see execute
	 */
	public SolutionSet executeSequential() throws JMException {

		// Setup
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			algorithm_[i].setup( i );
		// Broadcast the best solutions, setup and broadcast must locate in different loops
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			broadcastSetUpSolution( i );

		// First evaluation (only need once explicity)
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			algorithm_[i].evaluatePopulation();

		// The main loop
		while ( !algorithm_[0].stopCondition() ) {
			for( int i=0 ; i<numberOfIslands_ ; ++i ){
				algorithm_[i].generation();
				algorithm_[i].postGeneration();
				broadcastBestSolution( i );
			} // for
			
		} // while
		for( int i=0 ; i<numberOfIslands_ ; ++i ) 
			algorithm_[i].postExecution();
		
		return unifySolutionSet();
	} // executeSequential
	
	
	
	/** Auxiliar code. This method mixes various populations of the islands in one.
	 * @return The population
	 * @throws JMException
	 */
	private SolutionSet unifySolutionSet() throws JMException{
		
		SolutionSet[] population = new SolutionSet[numberOfIslands_];
		for( int i=0 ; i<numberOfIslands_ ; ++i)
			population[i] = algorithm_[i].getFinalSolutionSet();
		
		// NOTEIT Union of the islands' solutions
//		int bisections = 5;
//		AdaptiveGridArchive solutionToReturn = new AdaptiveGridArchive( 0 , 1 , 0 , populationSize_ , bisections , problem_.getNumberOfObjectives() );
		CrowdingArchive solutionToReturn = new CrowdingArchive(0,1,0,populationSize_,problem_.getNumberOfObjectives());
//		SolutionSet solutionToReturn = new SolutionSet(0 , 1 , 0 , 400 );

		for( int i=0 ; i<numberOfIslands_ ; ++i ){
			Iterator<Solution> it = population[i].iterator();
			while( it.hasNext() )
				solutionToReturn.add( it.next() );
		} // for

		return solutionToReturn;
	} // unifySolutionSet

	
	/** This method performs a broadcast of the best solution of one island to the rest
	 * @param islandId Island identificator
	 * 
	 * @see broadcastSetUpSolution
	 */
	private void broadcastBestSolution( int islandId ){
		DecisionVariables[] bestSolutions = algorithm_[islandId].getBestSolutions();
		
		for( int j=0 ; j<islandId ; ++j)
			algorithm_[j].setBestSolutions( bestSolutions , islandId );
	
		for( int j=islandId+1 ; j<numberOfIslands_ ; ++j)
			algorithm_[j].setBestSolutions( bestSolutions , islandId );
		
	} // broadcastBestSolution
	
	
	/** This method performs a broadcast like broadcastBestSolution, but it's performed
	 * in the initial stage.
	 * @param islandId Island identificator
	 * 
	 * @see broadcastBestSolution
	 */
	private void broadcastSetUpSolution( int islandId ){
		DecisionVariables[] bestSolutions = algorithm_[islandId].getBestSolutions();
		
		for( int j=0 ; j<islandId ; ++j)
			algorithm_[j].setUpBestSolutions( bestSolutions , islandId );
	
		for( int j=islandId+1 ; j<numberOfIslands_ ; ++j)
			algorithm_[j].setUpBestSolutions( bestSolutions , islandId );

	} // broadcastSetUpSolution


	/** Get the number of islands
	 * @return The number of islands
	 */
	public int getNumberOfIslands() {
		return numberOfIslands_;
	} // getIslands

} // Islands
