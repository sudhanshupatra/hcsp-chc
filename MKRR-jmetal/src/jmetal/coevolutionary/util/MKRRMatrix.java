package jmetal.coevolutionary.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * This class reads from disk and manages the computation matrices
 * 
 * @author Anowar El Amouri (first version)
 * @author Juan A. Caero Tamayo (bug fixes, optimization and XML matrices
 *         management)
 * 
 * @version 1.1
 */
public class MKRRMatrix extends Random {

	private static final long serialVersionUID = -2123991401633076229L;

	protected double[][] MatrixETC_; // /< Stores the ETC values
	protected int[] Priorities_;
	protected int numberOfTasks_; // /< Stores the number of tasks
	protected int numberOfMachines_; // /< Stores the number of machines

	/**
	 * Constructor
	 */
	public MKRRMatrix() {
	} // Matrix

	/**
	 * Recover data of ETC and Computation Matrices
	 * 
	 * @param numberOfTasks
	 *            The number of tasks used in the problem
	 * @param numberOfMachines
	 *            The number of machines used in the problem
	 * @param filename
	 *            The path+name of the ETC file to use
	 * @param per
	 *            The percentage used to calculate Computation Time values
	 */
	// public void recoverData( int numberOfTasks , int numberOfMachines ,
	// String filename, double per ){
	public void recoverData(int numberOfTasks, int numberOfMachines,
			String filename) {

		BufferedReader bf = null;
		numberOfTasks_ = numberOfTasks;
		numberOfMachines_ = numberOfMachines;
		MatrixETC_ = new double[numberOfTasks][numberOfMachines];
		Priorities_ = new int[numberOfTasks];

		// Extract ETC values from the file indicated and store
		try {
			bf = new BufferedReader(new FileReader(filename));

			for (int j = 0; j < numberOfTasks; j++) {
				int value;
				String dataLine;
				
				dataLine = bf.readLine();
				// Convert the string value in double value
				value = (dataLine != null) ? Integer.parseInt(dataLine) : 1;
				// Store the value in correct place
				Priorities_[j] = value;
			}

			for (int j = 0; j < numberOfTasks; j++) {
				double value;
				String dataLine;
				
				for (int m = 0; m < numberOfMachines; m++) {
					dataLine = bf.readLine();
					// Convert the string value in double value
					value = (dataLine != null) ? Double.parseDouble(dataLine)
							: 0.0;
					// Store the value in correct place
					MatrixETC_[j][m] = value;
				} // for
			} // for
		} // try
		catch (FileNotFoundException ex) {
			System.out.println("File didn't exist !!");
		} // catch
		catch (IOException ex) {
			System.out.println("Error in file ligne reading !!");
		} // catch
		finally {
			try {
				bf.close();
			} // try
			catch (IOException ex1) {
				System.out.println("Error in file closing !!");
			} // catch
		} // finally

	} // recoverData

	public int[] getPriorities() {
		return Priorities_;
	}
	
	/**
	 * Gets the ETC values.
	 * 
	 * @return the matrix of ETC values.
	 */
	public double[][] getETCmatrix() {
		return MatrixETC_;
	} // getETCvalues

	/**
	 * Gets the number of tasks
	 * 
	 * @return the number of tasks
	 */
	public int getNumberOfTasks() {
		return numberOfTasks_;
	} // getNumberOfTasks

	/**
	 * Gets the number of machines
	 * 
	 * @return the number of machines
	 */
	public int getNumberOfMachines() {
		return numberOfMachines_;
	} // getNumberOfMachines

} // Matrix
