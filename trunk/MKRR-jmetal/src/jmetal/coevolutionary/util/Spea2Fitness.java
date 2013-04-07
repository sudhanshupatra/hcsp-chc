package jmetal.coevolutionary.util;


import java.util.*;


import jmetal.coevolutionary.base.operator.comparator.FitnessComparator;
import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.base.operator.comparator.DominanceComparator;
import jmetal.util.DistanceNode;
import jmetal.util.DistanceNodeComparator;

/**
 * This class implements some facilities for calculating the Spea2Fitness
 * 
 * @author Juanjo Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class Spea2Fitness {

	private double[][]            distance     = null       ; ///< Stores the distance between solutions
	private SolutionSet           solutionSet_ = null       ; ///< Stores the solutionSet to assign the fitness
	private static final Distance distance_ = new Distance(); ///< stores a <code>Distance</code> object

	@SuppressWarnings("unchecked")
	private static final Comparator distanceNodeComparator = new DistanceNodeComparator(); ///< stores a <code>Comparator</code> for distance between nodes checking
	@SuppressWarnings("unchecked")
	private static final Comparator dominance_             = new DominanceComparator()   ; ///< stores a <code>Comparator</code> for dominance checking


	/** 
	 * Constructor.
	 * Creates a new instance of Spea2Fitness for a given <code>SolutionSet</code>.
	 * @param solutionSet The <code>SolutionSet</code>
	 */
	public Spea2Fitness( SolutionSet solutionSet ) {

		distance     = distance_.distanceMatrix( solutionSet );
		solutionSet_ = solutionSet;
		for( int i = 0; i < solutionSet_.size(); ++i ) {
			solutionSet_.get(i).setLocation(i);
		} // for
	} // Spea2Fitness


	/** 
	 * Assigns fitness for all the solutions.
	 */
	@SuppressWarnings("unchecked")
	public void fitnessAssign() {
		int populationSize    = solutionSet_.size();
		double [] strength    = new double[populationSize];
		double [] rawFitness  = new double[populationSize];
		double kDistance ;
		
		// Performance speedup (copy the references)
		Solution[]         bufferOfSolutions = new Solution[populationSize];
		Iterator<Solution> it                = solutionSet_.iterator();
		int                index             = 0;
		while( it.hasNext() )
			bufferOfSolutions[index++] = it.next();
		
		//Calculate the strength value
		// strength(i) = |{j | j <- SolutionSet and i dominate j}|
		for (int i = 0; i < populationSize; i++) {
			for (int j = 0; j < populationSize;j++) {
				if (dominance_.compare( bufferOfSolutions[i] , bufferOfSolutions[j] )==-1) {
					strength[i] += 1.0;
				} // if
			} // for
		} // for


		//Calculate the raw fitness
		// rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
		for (int i = 0;i < populationSize; i++) {
			for (int j = 0; j < populationSize;j++) {
				if (dominance_.compare( bufferOfSolutions[i] , bufferOfSolutions[j] )==1) {
					rawFitness[i] += strength[j];
				} // if
			} // for
		} // for


		// Add the distance to the k-th individual. In the reference paper of SPEA2, 
		// k = sqrt(population.size()), but a value of k = 1 recommended. See
		// http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
		int k = 1 ;
		for (int i = 0; i < distance.length; i++) {
			Arrays.sort(distance[i]);
			kDistance = 1.0 / (distance[i][k] + 2.0); // Calcule de D(i) distance
			//population.get(i).setFitness(rawFitness[i]);
			bufferOfSolutions[i].setFitness( rawFitness[i] + kDistance );
		} // for                  
	} // fitnessAsign


	/** 
	 *  Gets 'size' elements from a population of more than 'size' elements
	 *  using for this de enviromentalSelection truncation
	 *  @param size The number of elements to get.
	 */
	@SuppressWarnings("unchecked")
	public SolutionSet environmentalSelection( int size ){        

		if (solutionSet_.size() < size) {
			size = solutionSet_.size();
		} // if

		// Create a new auxiliar population for no alter the original population
		SolutionSet aux = new SolutionSet( solutionSet_.getLoadingPosition()   ,
				                           solutionSet_.getNumberOfIslands()   ,
				                           solutionSet_.getNumberOfSolutions() ,
				                           solutionSet_.size()                 );
		aux.setBestExternResults( solutionSet_ );

		int i = 0;
		while (i < solutionSet_.size()){
			if (solutionSet_.get(i).getFitness()<1.0){
				aux.add(solutionSet_.get(i));
				solutionSet_.remove(i);
			} // if
			else
				i++;
		} // while

		if (aux.size() < size){
			Comparator comparator = new FitnessComparator();
			solutionSet_.sort( comparator );
			int remain = size - aux.size();
			for( i=0 ; i<remain ; ++i )
				aux.add( solutionSet_.get(i) );
			return aux;
		} // if
		else if (aux.size() == size)
			return aux;

		double [][] distance = distance_.distanceMatrix(aux);   
		List< List<DistanceNode> > distanceList = new LinkedList< List<DistanceNode> >();
		for (int pos = 0; pos < aux.size(); pos++) {
			aux.get(pos).setLocation(pos);
			List<DistanceNode> distanceNodeList = new ArrayList<DistanceNode>();
			for (int ref = 0; ref < aux.size(); ref++) {
				if (pos != ref) {
					distanceNodeList.add(new DistanceNode(distance[pos][ref],ref));
				} // if
			} // for
			distanceList.add(distanceNodeList);
		} // for                        

		for (int q = 0; q < distanceList.size(); q++){
			Collections.sort(distanceList.get(q),distanceNodeComparator);
		} // for

		while (aux.size() > size) {
			double minDistance = Double.MAX_VALUE;            
			int toRemove = 0;
			i = 0;
			Iterator<List<DistanceNode>> iterator = distanceList.iterator();
			while (iterator.hasNext()){
				List<DistanceNode> dn = iterator.next();
				if (dn.get(0).getDistance() < minDistance){
					toRemove = i;
					minDistance = dn.get(0).getDistance();
					// i and toRemove have the same distance to the first solution
				} // if
				else if (dn.get(0).getDistance() == minDistance) { 
					int k = 0;
					while ((dn.get(k).getDistance() == 
						distanceList.get(toRemove).get(k).getDistance()) &&
						k < (distanceList.get(i).size()-1)) {
						k++;
					} // while

					if (dn.get(k).getDistance() < 
							distanceList.get(toRemove).get(k).getDistance()) {
						toRemove = i;                    
					} // if
				} // else if
				i++;
			} // while

			int tmp = aux.get(toRemove).getLocation();
			aux.remove(toRemove);            
			distanceList.remove(toRemove);

			Iterator<List<DistanceNode>> externIterator = distanceList.iterator();
			while (externIterator.hasNext()){
				Iterator<DistanceNode> interIterator = externIterator.next().iterator();
				while (interIterator.hasNext()){
					if (interIterator.next().getReference() == tmp){
						interIterator.remove();
						continue;
					} // if
				} // while
			} // while      
		} // while   
		return aux;
	} // environmentalSelection   

} // Spea2Fitness
