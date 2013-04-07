/**
 * This package provides some facilities for metrics.
 */
package jmetal.coevolutionary.qualityIndicator.util;


import java.io.*;
import java.util.*;

import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.util.NonDominatedSolutionList;


/**
 * This class provides some facilities for metrics.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 **/
public class MetricsUtil {

	
	/**
	 * This method reads a Pareto Front for a file.
	 * @param path The path to the file that contains the pareto front
	 * @return double[][] with the pareto front
	 **/
	public double[][] readFront( String path ) {
		try {
			// Open the file
			FileInputStream fis   = new FileInputStream(path)     ;
			InputStreamReader isr = new InputStreamReader(fis)    ;
			BufferedReader br      = new BufferedReader(isr)      ;

			List<double []> list = new ArrayList<double []>();
			int numberOfObjectives = 0;
			String aux = br.readLine();
			while (aux!= null) {
				StringTokenizer st = new StringTokenizer(aux);
				int i = 0;
				numberOfObjectives = st.countTokens();
				double [] vector = new double[st.countTokens()];
				while (st.hasMoreTokens()) {
					double value = (new Double(st.nextToken())).doubleValue();
					vector[i] = value;
					i++;
				} // while
				list.add(vector);
				aux = br.readLine();
			} // while

			br.close();

			double [][] front = new double[list.size()][numberOfObjectives];
			for (int i = 0; i < list.size(); i++) {
				front[i] = list.get(i);
			} // for
			return front;
		} // try
		catch (Exception e) {
			System.out.println("InputFacilities crashed reading for file: "+path);
			e.printStackTrace();
		} // catch
		return null;
	} // readFront

	
	/** Gets the maximun values for each objectives in a given pareto
	 *  front
	 *  @param front The pareto front
	 *  @param noObjectives Number of objectives in the pareto front
	 *  @return double[] An array of noOjectives values whit the maximun values
	 *  for each objective
	 **/
	public double[] getMaximumValues( double[][] front, int noObjectives ) {
		double[] maximumValue = new double[noObjectives];

		for (int i = 0; i < noObjectives; i++)
			maximumValue[i] =  Double.MIN_VALUE;


		for (int i =0; i < front.length;i++ ) {
			for (int j = 0; j < front[i].length; j++) {
				if (front[i][j] > maximumValue[j])
					maximumValue[j] = front[i][j];
			} // for
		} // for

		return maximumValue;
	} // getMaximumValues


	/** Gets the minimun values for each objectives in a given pareto
	 *  front
	 *  @param front The pareto front
	 *  @param noObjectives Number of objectives in the pareto front
	 *  @return double[] An array of noOjectives values whit the minimum values
	 *  for each objective
	 **/
	public double [] getMinimumValues( double[][] front, int noObjectives) {
		double [] minimumValue = new double[noObjectives];
		for (int i = 0; i < noObjectives; i++)
			minimumValue[i] = Double.MAX_VALUE;

		for (int i = 0;i < front.length; i++) {
			for (int j = 0; j < front[i].length; j++) {
				if (front[i][j] < minimumValue[j]) 
					minimumValue[j] = front[i][j];
			} // for
		} // for
		return minimumValue;
	} // getMinimumValues


	/** 
	 *  This method returns the distance (taken the euclidean distance) between
	 *  two points given as <code>double []</code>
	 *  @param a A point
	 *  @param b A point
	 *  @return The euclidean distance between the points
	 **/
	public double distance(double [] a, double [] b) {
		double distance = 0.0;

		for (int i = 0; i < a.length; i++) {
			distance += Math.pow(a[i]-b[i],2.0);
		} // for
		return Math.sqrt(distance);
	} // distance


	/**
	 * Gets the distance between a point and the nearest one in
	 * a given front (the front is given as <code>double [][]</code>)
	 * @param point The point
	 * @param front The front that contains the other points to calculate the distances
	 * @return The minimun distance between the point and the front
	 **/
	public double distanceToClosedPoint(double[] point, double[][] front) {
		double minDistance = distance(point,front[0]);


		for (int i = 1; i < front.length; i++) {
			double aux = distance(point,front[i]);
			if (aux < minDistance) {
				minDistance = aux;
			} // if
		} // for

		return minDistance;
	} // distanceToClosedPoint


	/**
	 * Gets the distance between a point and the nearest one in
	 * a given front, and this distance is greater than 0.0
	 * @param point The point
	 * @param front The front that contains the other points to calculate the
	 * distances
	 * @return The minimun distances greater than zero between the point and
	 * the front
	 */
	public double distanceToNearestPoint(double[] point, double[][] front) {
		double minDistance = Double.MAX_VALUE;

		for (int i = 0; i < front.length; i++) {
			double aux = distance(point,front[i]);
			if ((aux < minDistance) && (aux > 0.0)) {
				minDistance = aux;
			} // if
		} // for

		return minDistance;
	} // distanceToNearestPoint


	/** 
	 * This method receives a pareto front and two points, one whit maximun values
	 * and the other with minimun values allowed, and returns a the normalized
	 * pareto front.
	 * @param front A pareto front.
	 * @param maximumValue The maximun values allowed
	 * @param minimumValue The mininum values allowed
	 * @return the normalized pareto front
	 **/
	public double[][] getNormalizedFront( double[][] front, 
                                          double[]   maximumValue,
                                          double[]   minimumValue ) {

		double [][] normalizedFront = new double[front.length][];

		for (int i = 0; i < front.length;i++) {
			normalizedFront[i] = new double[front[i].length];
			for (int j = 0; j < front[i].length; j++) {
				normalizedFront[i][j] = (front[i][j] - minimumValue[j]) /
				(maximumValue[j] - minimumValue[j]);
			} // for
		} // for
		return normalizedFront;
	} // getNormalizedFront


	/**
	 * This method receives a normalized pareto front and return the inverted one.
	 * This operation needed for minimization problems
	 * @param front The pareto front to inverse
	 * @return The inverted pareto front
	 **/
	public double[][] invertedFront( double[][] front) {
		double[][] invertedFront = new double[front.length][];

		for (int i = 0; i < front.length; i++) {
			invertedFront[i] = new double[front[i].length];
			for (int j = 0; j < front[i].length; j++) {
				if (front[i][j] <= 1.0 && front[i][j]>= 0.0) {
					invertedFront[i][j] = 1.0 - front[i][j];
				} // if
				else if (front[i][j] > 1.0) {
					invertedFront[i][j] = 0.0;
				} // else if
				else if (front[i][j] < 0.0) {
					invertedFront[i][j] = 1.0;
				} // else if
			} // for
		} // for
		return invertedFront;
	} // invertedFront

	
	/**
	 * Reads a set of non dominated solutions from a file
	 * @param path The path of the file containing the data
	 * @return A solution set
	 */
	public SolutionSet readNonDominatedSolutionSet(String path) {
		try {
			/* Open the file */
			FileInputStream fis   = new FileInputStream(path)     ;
			InputStreamReader isr = new InputStreamReader(fis)    ;
			BufferedReader br      = new BufferedReader(isr)      ;

			SolutionSet solutionSet = new NonDominatedSolutionList( 0 , 1 , 0 );

			String aux = br.readLine();
			while (aux!= null) {
				StringTokenizer st = new StringTokenizer(aux);
				int i = 0;
				Solution solution = new Solution(st.countTokens());
				while (st.hasMoreTokens()) {
					double value = (new Double(st.nextToken())).doubleValue();
					solution.setObjective(i,value);
					i++;
				} // while
				solutionSet.add(solution);
				aux = br.readLine();
			} // while
			br.close();
			return solutionSet;
		} // try
		catch (Exception e) {
			System.out.println("jmetal.coevolutionary.qualityIndicator.util.readNonDominatedSolutionSet: "+path);
			e.printStackTrace();
		} // catch
		return null;
	} // readNonDominatedSolutionSet

	
} // MetricsUtil
