package jmetal.coevolutionary.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jmetal.base.Configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class reads from disk and manages the computation matrices
 *
 * @author Anowar El Amouri (first version)
 * @author Juan A. Caero Tamayo (bug fixes, optimization and XML matrices management)
 * 
 * @version 1.1
 */
public class Matrix extends Random {

	private static final long serialVersionUID = -2123991401633076229L;

	protected double[][] MatrixETC_        ; ///< Stores the ETC values
	protected double[][] MatrixComputation_; ///< Stores the computation time values
	protected int        numberOfTasks_    ; ///< Stores the number of tasks
	protected int        numberOfMachines_ ; ///< Stores the number of machines

	/**
	 * Constructor
	 */
	public Matrix(){
	} // Matrix

	private double[][] readXMLmatrix( String filename ){
		
		double[][] matrix;
		try {
			File file = new File( filename );
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			if ( doc.getDocumentElement().getNodeName().equalsIgnoreCase("matrix") ){
				// Extract the attributes height and width
				int heigth = Integer.parseInt( doc.getDocumentElement().getAttribute("height") );
				int width  = Integer.parseInt( doc.getDocumentElement().getAttribute("width")  );
				// Define the matrix
				matrix = new double[heigth][width];
				NodeList rowList = doc.getElementsByTagName("row");

				if ( heigth==rowList.getLength() )
					// read the rows
					for( int j=0 ; j<rowList.getLength() ; ++j ){
						Node rowNode = rowList.item(j);

						if( rowNode.getNodeType()==Node.ELEMENT_NODE ){

							Element items = (Element) rowNode;
							NodeList itemList = items.getElementsByTagName("item");

							if( width==itemList.getLength() )
								// read the items of the row
								for( int i=0; i<itemList.getLength() ; ++i ){
									Element fstNmElmnt = (Element) itemList.item(i);
									NodeList fstNm = fstNmElmnt.getChildNodes();
									matrix[j][i]=Double.parseDouble(((Node) fstNm.item(0)).getNodeValue());
								} // for
							else {
								String cause = "The width value is incorrect (must be ";
								cause = cause + itemList.getLength() + "), fix it!";;
								Configuration.logger_.severe( cause );
								java.lang.ArrayStoreException ex;
								ex = new java.lang.ArrayStoreException( cause );
								throw new RuntimeException( cause , ex );
							} // else
						} // if

					} // for
				else {
					String cause = "The height value is incorrect (must be ";
					cause = cause + rowList.getLength() + "), fix it!";
					Configuration.logger_.severe( cause );
					java.lang.ArrayStoreException ex;
					ex = new java.lang.ArrayStoreException( cause );
					throw new RuntimeException( cause , ex );
				} // else
				return( matrix );
			} // if
			else {
				java.lang.IllegalStateException ex;
				String cause = "Root tag must be \"matrix\", not \"";
				cause = cause + doc.getDocumentElement().getNodeName() + "\"";
				ex = new java.lang.IllegalStateException( cause );
				throw new java.lang.IllegalStateException( cause , ex );
			} // else
		} // try
		catch ( java.lang.NumberFormatException e ){
			String cause = "Attributes height or width doesn't declared!";
			Configuration.logger_.severe( cause );
			throw new RuntimeException( cause , e );
		} // catch
		catch (Exception e) {
			String cause = "Unknown";
			Configuration.logger_.severe( cause );
			throw new RuntimeException( cause , e );
		} // catch
	} // readXMLmatrix


	/**
	 * Recover data of ETC and Computation Matrices
	 * 
	 * @param numberOfTasks    The number of tasks used in the problem
	 * @param numberOfMachines The number of machines used in the problem
	 * @param filename         The path+name of the ETC file to use
	 * @param per              The percentage used to calculate Computation Time values
	 */
//	public void recoverData( int numberOfTasks , int numberOfMachines , String filename, double per ){
	public void recoverData( int numberOfTasks , int numberOfMachines , String filename ){

		BufferedReader bf  = null;
		numberOfTasks_     = numberOfTasks;
		numberOfMachines_  = numberOfMachines;
		MatrixETC_         = new double[numberOfTasks][numberOfMachines];
		MatrixComputation_ = new double[numberOfTasks][numberOfMachines];

System.out.println("en recoverdata: "+filename);

		// Extract ETC values from the file indicated and store
		try {
			bf = new BufferedReader( new FileReader(filename) );
//			double factor = 1+per;
			double value;
			String dataLine;
			for( int j=0; j<numberOfTasks ; j++ ){
				for( int m=0; m<numberOfMachines ; m++ ){
					dataLine = bf.readLine();
					// Convert the string value in double value
					value = (dataLine != null)? Double.parseDouble( dataLine ) : 0.0;
					// Store the value in correct place
					MatrixETC_[j][m]         = value;
//					MatrixComputation_[j][m] = value * factor; // value+value+per = value*(1+per) = value*per2
				} // for
			} // for
		} // try
		catch ( FileNotFoundException ex ) {
			System.out.println("File didn't exist !!");
		} // catch
		catch ( IOException ex ) {
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

	
	/**
	 * Recover data of ETC and Computation Matrices
	 * 
	 * @param filename         The path+name of the ETC file to use
	 * @param per              The percentage used to calculate Computation Time values
	 */
	public void recoverXMLData( String filename, double per ){

		// Extract ETC values from the file indicated and store
		MatrixETC_ = this.readXMLmatrix( filename );
		numberOfTasks_    = MatrixETC_.length;
		numberOfMachines_ = MatrixETC_[0].length;
		MatrixComputation_ = new double[numberOfTasks_][numberOfMachines_];
		
		double factor = 1+per;
		
		for( int j=0; j<numberOfTasks_ ; j++ )
			for( int m=0; m<numberOfMachines_ ; m++ )
				MatrixComputation_[j][m] = MatrixETC_[j][m] * factor;
		MatrixComputation_ = MatrixETC_;
		// FIXME Esto lo hago porque no se puede usar MatrixComputation!!!
	} // recoverXMLData

//	/** Sets the ETC values
//	 * @param MatrixETC The ETC matrix
//	 */
//	private void setETCmatrix( double[][] MatrixETC ){
//		MatrixETC_ = MatrixETC ;
//	} // setETCvalues


	/** Gets the ETC values.
	 * @return the matrix of ETC values.
	 */
	public double[][] getETCmatrix() {
		return MatrixETC_;
	} // getETCvalues


//	/** Sets the computation time values.
//	 * @param MatrixComputation matrix computation
//	 */
//	private void setComputationMatrix( double[][] MatrixComputation ) {
//		MatrixComputation_ = MatrixComputation ;   
//	} // setComputationValues


	/** Gets the computation time values.
	 * @return the matrix of computation time values.
	 */
	public double[][] getComputationMatrix() {
		return MatrixComputation_ ;   
	} // getComputationValues


	/** Gets the number of tasks
	 * @return the number of tasks
	 */
	public int getNumberOfTasks() {
		return numberOfTasks_;
	} // getNumberOfTasks


	/** Gets the number of machines
	 * @return the number of machines
	 */
	public int getNumberOfMachines() {
		return numberOfMachines_;
	} // getNumberOfMachines

} // Matrix
