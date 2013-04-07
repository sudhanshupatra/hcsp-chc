package jmetal.coevolutionary.temporal;

public class MatricesConcept {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int heigth=512;
		int width=16;
		double[][] matrix = new double[heigth][width];

		int task=234;
		int machine=10;
		matrix[task][machine]=20.0;
		
		System.out.println("Value = " + matrix[task][machine]);
	} // main

} // Matrices concept
