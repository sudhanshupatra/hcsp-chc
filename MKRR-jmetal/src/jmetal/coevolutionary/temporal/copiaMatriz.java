package jmetal.coevolutionary.temporal;

public class copiaMatriz {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int NOTA=3;
		int allocation=0;
		double[] A = { 1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0 , 7.0 };

		//for( int k=NOTA ; k>allocation ; --k )
			//A[k] = A[k-1];
		
		System.arraycopy( A , allocation , A , allocation+1 , NOTA-allocation );


	}

}
