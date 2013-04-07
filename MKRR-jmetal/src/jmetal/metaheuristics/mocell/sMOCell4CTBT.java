/**
 * aMOCell.java
 * @author Juan J. Durillo
 * @version 1.0
 *
 */
package jmetal.metaheuristics.mocell;

import jmetal.base.*;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.base.archive.CrowdingArchive;
import jmetal.base.operator.comparator.*;
import jmetal.experiments.ExperimentNoPareto;
import jmetal.util.*;

/**
 * This class representing an asychronous version of MOCell algorithm in 
 * which all neighbors are considerated in the replace and the feedback
 * takes place through parent selection from the archive
 */
public class sMOCell4CTBT extends Algorithm{

  /**
   * Stores the problem to solve
   */
  private Problem problem_;          //The problem to solve        

  /** 
   * Constructor
   * @param problem Problem to solve
   */    
  public sMOCell4CTBT(Problem problem){
    problem_ = problem;
  } // aMOCell4

  /**   
   * Runs of the sMOCell4CTBT algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution  
   * @throws JMException 
   */  
  public SolutionSet execute() throws JMException {
    //Init the param
    int populationSize, archiveSize, maxEvaluations, evaluations, feedBack;
    Operator mutationOperator, crossoverOperator, selectionOperator;
    SolutionSet currentSolutionSet, newSolutionSet;
    CrowdingArchive archive;
    SolutionSet [] neighbors;    
    Neighborhood neighborhood;
    Comparator dominance = new DominanceComparator(),
    crowding  = new CrowdingComparator();  
    Distance distance = new Distance();


    //Read the params
    populationSize    = ((Integer)getInputParameter("populationSize")).intValue();
    archiveSize       = ((Integer)getInputParameter("archiveSize")).intValue();
    maxEvaluations    = ((Integer)getInputParameter("maxEvaluations")).intValue();                                

    //Read the operators
    mutationOperator  = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");     

    //Init the variables
    //init the population and the archive
    currentSolutionSet  = new SolutionSet(populationSize);        
    archive            = new CrowdingArchive(archiveSize,problem_.getNumberOfObjectives());                
    evaluations        = 0;                        
    newSolutionSet      = new SolutionSet(populationSize);
    neighborhood       = new Neighborhood(populationSize);
    neighbors          = new SolutionSet[populationSize];
    //Create the comparator for check dominance
    dominance = new jmetal.base.operator.comparator.DominanceComparator();   
    //Create the initial population
    for (int i = 0; i < populationSize; i++){
      Solution solution = new Solution(problem_);
      problem_.evaluate(solution);           
      problem_.evaluateConstraints(solution);
      currentSolutionSet.add(solution);
      solution.setLocation(i);
      evaluations++;
    }

    //
    int iterations = 0;
    while (evaluations < maxEvaluations){   
        newSolutionSet = new SolutionSet(populationSize);
                             
      for (int ind = 0; ind < currentSolutionSet.size(); ind++){
        Solution individual = new Solution(currentSolutionSet.get(ind));

        Solution [] parents = new Solution[2];
        Solution [] offSpring;

        //neighbors[ind] = neighborhood.getFourNeighbors(currentSolutionSet,ind);
        neighbors[ind] = neighborhood.getEightNeighbors(currentSolutionSet,ind);                                                           
        //neighbors[ind].add(individual);

        // Modification: The first parent is the individual itself and the two parents must be different
        //parents
        //parents[0] = (Solution)selectionOperator.execute(neighbors[ind]);
        parents[0] = individual;
        if (archive.size()>0) {
          parents[1] = (Solution)selectionOperator.execute(archive);
        } else {
        		parents[1] = (Solution)selectionOperator.execute(neighbors[ind]);
        }

        //Create a new solution, using genetic operators mutation and crossover
        offSpring = (Solution [])crossoverOperator.execute(parents);               
        mutationOperator.execute(offSpring[0]);

        //->Evaluate solution and constraints
        problem_.evaluate(offSpring[0]);
        problem_.evaluateConstraints(offSpring[0]);
        evaluations++;

        int flag = dominance.compare(individual,offSpring[0]);     
        
        if (flag>=0)
        {
        	offSpring[0].setLocation(individual.getLocation());
        	newSolutionSet.add(offSpring[0]);
        	//currentSolutionSet.replace(offSpring[0].getLocation(),offSpring[0]);
        	if (offSpring[0].getNumberOfViolatedConstraint() == 0)
          	  archive.add(new Solution(offSpring[0]));
        }
        else
        {
        	newSolutionSet.add(new Solution(currentSolutionSet.get(ind)));
        
        	if (offSpring[0].getNumberOfViolatedConstraint() == 0)
            	  archive.add(new Solution(offSpring[0]));
        }
        
      }
    }
    return archive;
  }  // execute      
  
}

