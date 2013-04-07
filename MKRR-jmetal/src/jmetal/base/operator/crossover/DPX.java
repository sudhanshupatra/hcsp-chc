/**
 * TwoPointsCrossover.java
 * Class representing a two points crossover operator
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.base.operator.crossover;

import java.util.Comparator;

import jmetal.base.*;    
import jmetal.base.variable.*;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.base.Configuration.* ; 
import jmetal.base.operator.comparator.*;

 /**
 * This class allows to apply a two points crossover operator using two parent
 * solutions.
 * NOTE: the operator is applied to the first variable of the solutions, and 
 * the type of the solutions must be <code>SolutionType_.Permutation</code>.
 */
  public class DPX extends Operator {
    
    
  /**
   * Constructor
   * Creates a new intance of the two point crossover operator
   */
  public DPX() {
  } // TwoPointsCrossover

  /**
  * Perform the crossover operation
  * @param probability Crossover probability
  * @param parent1 The first parent
  * @param parent2 The second parent
  * @return Two offspring solutions
   * @throws JMException 
  */
  public Solution[] doCrossover(double   probability, 
                                Solution parent1, 
                                Solution parent2) throws JMException {

    Solution [] offspring = new Solution[2];

    offspring[0] = new Solution(parent1);
    offspring[1] = new Solution(parent2);

    if (PseudoRandom.randDouble() < probability) {
        int crosspoint1        ;
        int crosspoint2        ;
        int chromosomeLength  ;
        DecisionVariables parent1Vars;
        DecisionVariables parent2Vars;
        DecisionVariables offspring1Vars;
        DecisionVariables offspring2Vars;

        //permutationLength = ((Permutation)parent1.getDecisionVariables().variables_[0]).getLength() ;
        chromosomeLength = parent1.getDecisionVariables().size(); 
        parent1Vars      = parent1.getDecisionVariables();
        parent2Vars      = parent2.getDecisionVariables();    
        offspring1Vars   = offspring[0].getDecisionVariables();
        offspring2Vars   = offspring[1].getDecisionVariables();

        // STEP 1: Get two cutting points
        crosspoint1 = PseudoRandom.randInt(0,chromosomeLength-1) ;
        crosspoint2 = PseudoRandom.randInt(0,chromosomeLength-1) ;
        
        while (crosspoint2 == crosspoint1)  
          crosspoint2 = PseudoRandom.randInt(0,chromosomeLength-1) ;

        if (crosspoint1 > crosspoint2) {
          int swap ;
          swap        = crosspoint1 ;
          crosspoint1 = crosspoint2 ;
          crosspoint2 = swap          ;
        } // if
        
//        // STEP 2: Obtain the two children
//        
//        for(int k = 0; k < crosspoint1; k++) {
//        	offspring1Vars.variables_[k] = parent1Vars.variables_[k];
//        	offspring2Vars.variables_[k] = parent2Vars.variables_[k];
//        }
//        	
//       for(int k = crosspoint1; k < crosspoint2; k++) {
//    	   offspring2Vars.variables_[k] = parent1Vars.variables_[k];
//       	   offspring1Vars.variables_[k] = parent2Vars.variables_[k];
//        } // for
//       
//       for(int k = crosspoint2; k < chromosomeLength; k++) {
//       	offspring1Vars.variables_[k] = parent1Vars.variables_[k];
//       	offspring2Vars.variables_[k] = parent2Vars.variables_[k];
//       }
        
        // STEP 2: Obtain the two children
        
        for(int k = 0; k < crosspoint1; k++) {
        	offspring1Vars.variables_[k] = parent1Vars.variables_[k].deepCopy();
        	offspring2Vars.variables_[k] = parent2Vars.variables_[k].deepCopy();
        }
        	
       for(int k = crosspoint1; k < crosspoint2; k++) {
    	   offspring2Vars.variables_[k] = parent1Vars.variables_[k].deepCopy();
       	   offspring1Vars.variables_[k] = parent2Vars.variables_[k].deepCopy();
        } // for
       
       for(int k = crosspoint2; k < chromosomeLength; k++) {
       	offspring1Vars.variables_[k] = parent1Vars.variables_[k].deepCopy();
       	offspring2Vars.variables_[k] = parent2Vars.variables_[k].deepCopy();
       }
       
       Comparator dominance = new DominanceComparator();
       
       if (((dominance.compare(parent1,parent2) != -1) && (crosspoint2-crosspoint1 >= chromosomeLength/2)) ||
    		   ((dominance.compare(parent1,parent2) == -1) && (crosspoint2-crosspoint1 < chromosomeLength/2)))
       {
    	   offspring[0].setDecisionVariables(offspring1Vars);
           offspring[1].setDecisionVariables(offspring2Vars);
       }
       else if (((dominance.compare(parent2,parent1) != -1) && (crosspoint2-crosspoint1 >= chromosomeLength/2)) ||
    		   ((dominance.compare(parent2,parent1) == -1) && (crosspoint2-crosspoint1 < chromosomeLength/2)))
       {
    	   offspring[1].setDecisionVariables(offspring1Vars);
           offspring[0].setDecisionVariables(offspring2Vars);
       }
       
       
//    else
//    {
//      Configuration.logger_.severe("TwoPointsCrossover.doCrossover: invalid " +
//          "type" + 
//          parent1.getDecisionVariables().variables_[0].getVariableType());
//      Class cls = java.lang.String.class;
//      String name = cls.getName(); 
//      throw new JMException("Exception in " + name + ".doCrossover()") ; 
//    } // else

    }
    
    return offspring;                                                                                      
  } // makeCrossover

 /**
  * Executes the operation
  * @param object An object containing an array of two solutions 
  * @return An object containing an array with the offSprings
 * @throws JMException 
  */
  public Object execute(Object object) throws JMException {
    Solution [] parents = (Solution [])object;
    Double crossoverProbability ;

//    if ((parents[0].getType() != SolutionType_.Permutation) ||
//        (parents[1].getType() != SolutionType_.Permutation)) {
//      
//      Configuration.logger_.severe("TwoPointsCrossover.execute: the solutions " +
//          "are not of the right type. The type should be 'Permutation', but " +
//          parents[0].getType() + " and " + 
//          parents[1].getType() + " are obtained");
//    } // if 
    	
    crossoverProbability = (Double)getParameter("probability");

    if (parents.length < 2)
    {
      Configuration.logger_.severe("SBXCrossover.execute: operator needs two " +
          "parents");
      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".execute()") ;      
    }
    else if (crossoverProbability == null)
    {
      Configuration.logger_.severe("SBXCrossover.execute: probability not " +
      "specified");
      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".execute()") ;  
    }      

    Solution [] offspring = doCrossover(crossoverProbability.doubleValue(),
                                        parents[0],
                                        parents[1]);

    return offspring; 
  } // execute
  
} // TwoPointsCrossover
