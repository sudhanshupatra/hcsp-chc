package jmetal.coevolutionary.experiments;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.coevolutionary.experiments.settings.MOCell_Settings;
import jmetal.coevolutionary.experiments.settings.NSGAII_Settings;
import jmetal.coevolutionary.experiments.settings.SPEA2_Settings;
import jmetal.util.JMException;

public class StandardStudyScalability extends Experiment
{
  public void algorithmSettings(Problem problem, int problemIndex)
  {
    try
    {
      int numberOfAlgorithms = this.algorithmNameList_.length;

      Properties[] parameters = new Properties[numberOfAlgorithms];

      for (int i = 0; i < numberOfAlgorithms; i++) {
        parameters[i] = new Properties();
      }

      if (!this.paretoFrontFile_[problemIndex].equals(""))
      {
        for (int i = 0; i < numberOfAlgorithms; i++) {
          parameters[i].setProperty("PARETO_FRONT_FILE", this.paretoFrontFile_[problemIndex]);
        }

      }

      if (numberOfAlgorithms > 0) this.algorithm_[0] = new NSGAII_Settings(problem).configure(parameters[0]);
      if (numberOfAlgorithms > 1) this.algorithm_[1] = new SPEA2_Settings(problem).configure(parameters[1]);
      if (numberOfAlgorithms > 2) this.algorithm_[2] = new MOCell_Settings(problem).configure(parameters[2]);

    }
    catch (JMException ex)
    {
      Logger.getLogger(StandardStudyScalability.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void main(String[] args) throws JMException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException
  {
    StandardStudyScalability standardStudy = new StandardStudyScalability();

    standardStudy.numberOfIslands = 6;
    standardStudy.experimentName_ = "StandardStudyScalability";
    standardStudy.timmingFileName_ = "TIMMINGS";

    standardStudy.algorithmNameList_ = new String[] { "NSGAII", "SPEA2", "MOCell" };

    standardStudy.problemList_ = new String[] { "DTLZ6" };

    standardStudy.solutionTypeList_ = new String[] { "Real" };

    standardStudy.paretoFrontFile_ = new String[] { "DTLZ6.2D.pf" };

    standardStudy.indicatorList_ = new String[] { "HV", "SPREAD", "IGD", "EPSILON" };

    int numberOfAlgorithms = standardStudy.algorithmNameList_.length;

    String currentDir = System.getProperty("user.dir");
    String outputDir = currentDir + "/output";

    standardStudy.experimentBaseDirectory_ = (outputDir + "/" + standardStudy.experimentName_);
    standardStudy.paretoFrontDirectory_ = outputDir;
    standardStudy.algorithmSettings_ = new Settings[numberOfAlgorithms];
    standardStudy.algorithm_ = new Algorithm[numberOfAlgorithms];
    standardStudy.independentRuns_ = 100;

    standardStudy.runExperiment();

    standardStudy.generateLatexTables();

//    int rows = 3;
//    int columns = 2;
//    String prefix = new String("ZDT");
//    String[] problems = { "ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6" };
//    standardStudy.generateRScripts(rows, columns, problems, prefix);
//
//    rows = 3;
//    columns = 3;
//    prefix = new String("DTLZ");
//    problems = new String[] { "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "DTLZ5", 
//      "DTLZ6", "DTLZ7" };
//    standardStudy.generateRScripts(rows, columns, problems, prefix);
  }
}