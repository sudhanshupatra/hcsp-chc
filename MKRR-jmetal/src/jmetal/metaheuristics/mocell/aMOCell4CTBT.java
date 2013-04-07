/**
 * aMOCell4b.java
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
 * Class representing de MoCell algorithm
 */
public class aMOCell4CTBT extends Algorithm{

  //->fields
  private Problem problem_;          //The problem to solve        

  public aMOCell4CTBT(Problem problem){
    problem_ = problem;
  }

  /** Execute the algorithm 
   * @throws JMException */
  public SolutionSet execute() throws JMException {
    //Init the param
    int populationSize, archiveSize, maxEvaluations, evaluations;
    Operator mutationOperator, crossoverOperator, selectionOperator;
    SolutionSet currentPopulation;
    CrowdingArchive archive;
    SolutionSet [] neighbors;    
    Neighborhood neighborhood;
    Comparator dominance = new DominanceComparator();  
    Comparator crowdingComparator = new CrowdingComparator();
    Distance distance = new Distance();

    //Init the param
    //Read the params
    populationSize    = ((Integer)getInputParameter("populationSize")).intValue();
    archiveSize       = ((Integer)getInputParameter("archiveSize")).intValue();
    maxEvaluations    = ((Integer)getInputParameter("maxEvaluations")).intValue();                                

    //Read the operators
    mutationOperator  = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");        

    //Init the variables    
    currentPopulation  = new SolutionSet(populationSize);        
    archive            = new CrowdingArchive(archiveSize,problem_.getNumberOfObjectives());                
    evaluations        = 0;                        
    neighborhood       = new Neighborhood(populationSize);
    neighbors          = new SolutionSet[populationSize];

    //Create the initial population
    for (int i = 0; i < populationSize; i++){
      Solution individual = new Solution(problem_);
      problem_.evaluate(individual);           
      problem_.evaluateConstraints(individual);
      currentPopulation.add(individual);
      individual.setLocation(i);
      evaluations++;
    }


//	  // Insert a good seed    
//    //[30386, 17690, 17969, 27013, 26115, 43598, 22987, 26384, 17323, 15711, 31257, 31349, 29025, 5361, 14005, 32281, 22, 3837, 43850, 12997, 19886, 28527, 8341, 1483, 33714, 20226, 29990, 38728, 13331, 44772, 13227, 14881, 43941, 21998, 18830, 26852, 15096, 43539, 26002, 30543, 11460, 19629, 39923, 25852, 3407, 6927, 38502, 7839, 37832, 10492]
//    currentPopulation.get(0).getDecisionVariables().variables_[0].setValue(30386);
//    currentPopulation.get(0).getDecisionVariables().variables_[1].setValue(17690);
//    currentPopulation.get(0).getDecisionVariables().variables_[2].setValue(17969);
//    currentPopulation.get(0).getDecisionVariables().variables_[3].setValue(27013);
//    currentPopulation.get(0).getDecisionVariables().variables_[4].setValue(26115);
//    currentPopulation.get(0).getDecisionVariables().variables_[5].setValue(43598);
//    currentPopulation.get(0).getDecisionVariables().variables_[6].setValue(22987);
//    currentPopulation.get(0).getDecisionVariables().variables_[7].setValue(26384);
//    currentPopulation.get(0).getDecisionVariables().variables_[8].setValue(17323);
//    currentPopulation.get(0).getDecisionVariables().variables_[9].setValue(15711);
//    currentPopulation.get(0).getDecisionVariables().variables_[10].setValue(31257);
//    currentPopulation.get(0).getDecisionVariables().variables_[11].setValue(31349);
//    currentPopulation.get(0).getDecisionVariables().variables_[12].setValue(29025);
//    currentPopulation.get(0).getDecisionVariables().variables_[13].setValue(5361);
//    currentPopulation.get(0).getDecisionVariables().variables_[14].setValue(14005);
//    currentPopulation.get(0).getDecisionVariables().variables_[15].setValue(32281);
//    currentPopulation.get(0).getDecisionVariables().variables_[16].setValue(22);
//    currentPopulation.get(0).getDecisionVariables().variables_[17].setValue(3837);
//    currentPopulation.get(0).getDecisionVariables().variables_[18].setValue(43850);
//    currentPopulation.get(0).getDecisionVariables().variables_[19].setValue(12997);
//    currentPopulation.get(0).getDecisionVariables().variables_[20].setValue(19886);
//    currentPopulation.get(0).getDecisionVariables().variables_[21].setValue(28527);
//    currentPopulation.get(0).getDecisionVariables().variables_[22].setValue(8341);
//    currentPopulation.get(0).getDecisionVariables().variables_[23].setValue(1483);
//    currentPopulation.get(0).getDecisionVariables().variables_[24].setValue(33714);
//    currentPopulation.get(0).getDecisionVariables().variables_[25].setValue(20226);
//    currentPopulation.get(0).getDecisionVariables().variables_[26].setValue(29990);
//    currentPopulation.get(0).getDecisionVariables().variables_[27].setValue(38728);
//    currentPopulation.get(0).getDecisionVariables().variables_[28].setValue(13331);
//    currentPopulation.get(0).getDecisionVariables().variables_[29].setValue(44772);
//    currentPopulation.get(0).getDecisionVariables().variables_[30].setValue(13227);
//    currentPopulation.get(0).getDecisionVariables().variables_[31].setValue(14881);
//    currentPopulation.get(0).getDecisionVariables().variables_[32].setValue(43941);
//    currentPopulation.get(0).getDecisionVariables().variables_[33].setValue(21998);
//    currentPopulation.get(0).getDecisionVariables().variables_[34].setValue(18830);
//    currentPopulation.get(0).getDecisionVariables().variables_[35].setValue(26852);
//    currentPopulation.get(0).getDecisionVariables().variables_[36].setValue(15096);
//    currentPopulation.get(0).getDecisionVariables().variables_[37].setValue(43539);
//    currentPopulation.get(0).getDecisionVariables().variables_[38].setValue(26002);
//    currentPopulation.get(0).getDecisionVariables().variables_[39].setValue(30543);
//    currentPopulation.get(0).getDecisionVariables().variables_[40].setValue(11460);
//    currentPopulation.get(0).getDecisionVariables().variables_[41].setValue(19629);
//    currentPopulation.get(0).getDecisionVariables().variables_[42].setValue(39923);
//    currentPopulation.get(0).getDecisionVariables().variables_[43].setValue(25852);
//    currentPopulation.get(0).getDecisionVariables().variables_[44].setValue(3407);
//    currentPopulation.get(0).getDecisionVariables().variables_[45].setValue(6927);
//    currentPopulation.get(0).getDecisionVariables().variables_[46].setValue(38502);
//    currentPopulation.get(0).getDecisionVariables().variables_[47].setValue(7839);
//    currentPopulation.get(0).getDecisionVariables().variables_[48].setValue(37832);
//    currentPopulation.get(0).getDecisionVariables().variables_[49].setValue(10492);
//    
////    DecisionVariables dv = new DecisionVariables(problem_, vars);
////    currentPopulation.get(0).setDecisionVariables(dv);
//    problem_.evaluate(currentPopulation.get(0));
//    problem_.evaluateConstraints(currentPopulation.get(0));

    while (evaluations < maxEvaluations){                                 
      for (int ind = 0; ind < currentPopulation.size(); ind++){
        Solution individual = new Solution(currentPopulation.get(ind));

        Solution [] parents = new Solution[2];
        Solution [] offSpring;

        //neighbors[ind] = neighborhood.getFourNeighbors(currentPopulation,ind);
        neighbors[ind] = neighborhood.getEightNeighbors(currentPopulation,ind);                                                           
        //neighbors[ind].add(individual);

        // parents
        // Modification: The first parent is the individual itself and the two parents must be different
        //parents
        //parents[0] = (Solution)selectionOperator.execute(neighbors[ind]);
        parents[0] = individual;
        if (archive.size() > 0) {
          parents[1] = (Solution)selectionOperator.execute(archive);
        } else {                   
          parents[1] = (Solution)selectionOperator.execute(neighbors[ind]);
        }

        // Create a new individual, using genetic operators mutation and crossover
        offSpring = (Solution [])crossoverOperator.execute(parents);               
        mutationOperator.execute(offSpring[0]);

        // Evaluate individual an his constraints
        problem_.evaluate(offSpring[0]);
        problem_.evaluateConstraints(offSpring[0]);
        evaluations++;

        int flag = dominance.compare(individual,offSpring[0]);

        if (flag == 1) { //The new individual dominates
          offSpring[0].setLocation(individual.getLocation());                                      
          currentPopulation.replace(offSpring[0].getLocation(),offSpring[0]);
          archive.add(new Solution(offSpring[0]));                   
        } else if (flag == 0) { //The new individual is non-dominated               
          neighbors[ind].add(offSpring[0]);               
          offSpring[0].setLocation(-1);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            distance.crowdingDistanceAssignment(rank.getSubfront(j), problem_.getNumberOfObjectives());
          }
          neighbors[ind].sort(crowdingComparator); 
          Solution worst = neighbors[ind].get(neighbors[ind].size()-1);

          if (worst.getLocation() == -1) { //The worst is the offspring
            archive.add(new Solution(offSpring[0]));
          } else {
            offSpring[0].setLocation(worst.getLocation());
            currentPopulation.replace(offSpring[0].getLocation(),offSpring[0]);
            archive.add(new Solution(offSpring[0]));
          }                                          
        }
      }                                 
    }
    //System.out.println(evaluations);
    return archive;
  }        
  
}

