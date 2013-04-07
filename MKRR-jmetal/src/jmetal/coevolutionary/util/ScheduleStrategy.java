package jmetal.coevolutionary.util;

import java.util.Arrays;

import java.util.Calendar;
import java.util.Random;

/**
 * This class implements various scheduling strategies.
 *
 * @author Juan A. Caero
 * @version 1.0
 */
public class ScheduleStrategy {

	private static final int    NOT_ALLOCATED = -1;

	/** This method implements the Min-Min scheduling strategy.
	 * 
	 * @param ETC Expected time to compute matrix
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines
	 * 
	 * @return The min-min scheduling allocation
	 */
	public static int[] minMin( double[][] ETC , int numberOfTasks , int numberOfMachines ){
		int[] schedule = new int[numberOfTasks];
		
		Arrays.fill( schedule , NOT_ALLOCATED );
		
		double[] computation         = new double[ numberOfMachines ] ;
		int      numberOfAllocations = 0 ;
		double   minCT        ;
		double   minCTTask    ;
		int      bestMachine  ;
		int      bestMachTask ;
		int      bestTask     ;
		
		while (numberOfAllocations < numberOfTasks){
			// Seleccionar tarea no asignada con ct mnimo.
			bestTask    = -1   ;
			bestMachine = -1   ;
			minCT       = Double.POSITIVE_INFINITY ;
			
			for( int i=0 ; i<numberOfTasks ; ++i ){
				minCTTask    = Double.POSITIVE_INFINITY ;
				bestMachTask = -1   ;

				if ( schedule[i] == NOT_ALLOCATED ){
					// No est asignada, evaluar el minimo ct de la tarea.
					double   ct     = 0.0;
					double[] rowETC = ETC[i];
					// Recorrer mquinas.
					for ( int j=0 ; j<numberOfMachines ; ++j ){
						ct = computation[ j ] + rowETC[ j ];
						if ( ct < minCTTask ){
							minCTTask    = ct; 
							bestMachTask = j ;
						} // if
					} // for
				} // if

				if ( minCTTask < minCT ){
					minCT = minCTTask;
					bestTask = i;
					bestMachine = bestMachTask;
				} // if
			} // for
			
			computation[ bestMachine ] += ETC[ bestTask ][ bestMachine ];
			schedule[ bestTask ]       =  bestMachine;
			++numberOfAllocations;

		} // while

		return schedule;
	} // minMin

	public static int[] Sufferage( double[][] ETC , int numberOfTasks , int numberOfMachines ){
		int[] schedule = new int[numberOfTasks];
		
		Arrays.fill( schedule , NOT_ALLOCATED );
		
		double[] computation         = new double[ numberOfMachines ] ;
		int      numberOfAllocations = 0 ;
		double   worst_sufferage ;
		double   sufferage_task ;
		int      bestMachine  ;
		int      bestMachTask ;
		int      bestTask     ;
		double   minCTTask, second_minCTTask;
		
		while (numberOfAllocations < numberOfTasks){
			// Seleccionar tarea no asignada con ct mnimo.
			bestTask    = -1   ;
			bestMachine = -1   ;
			worst_sufferage = 0.0;
			
			for( int i=0 ; i<numberOfTasks ; ++i ){
				minCTTask = Double.POSITIVE_INFINITY ;
				second_minCTTask = Double.POSITIVE_INFINITY ;
				sufferage_task = -1.0;
				bestMachTask = -1   ;

				if ( schedule[i] == NOT_ALLOCATED ){
					// No est asignada, evaluar el sufferage
					double   ct     = 0.0;
					double[] rowETC = ETC[i];
					// Recorrer mquinas.
					for ( int j=0 ; j<numberOfMachines ; ++j ){
						ct = computation[ j ] + rowETC[ j ];
						if ( ct < minCTTask ){
							minCTTask    = ct; 
							bestMachTask = j ;
						} else {
							if ( ct <= second_minCTTask ){
								second_minCTTask    = ct; 
							} // if
						}
					} // for
					sufferage_task = second_minCTTask - minCTTask;
				} // if

				if (sufferage_task >= worst_sufferage){
					worst_sufferage = sufferage_task;
					bestTask = i;
					bestMachine = bestMachTask;
				} // if
			} // for
			
			computation[ bestMachine ] += ETC[ bestTask ][ bestMachine ];
			schedule[ bestTask ]       =  bestMachine;
			++numberOfAllocations;

		} // while

		return schedule;
	} // Sufferage
	
	public static int[] Sufferage_rand( double[][] ETC , int numberOfTasks , int numberOfMachines ){
		int[] schedule = new int[numberOfTasks];
		
		Arrays.fill( schedule , NOT_ALLOCATED );
		
		double[] computation         = new double[ numberOfMachines ] ;
		int      numberOfAllocations = 0 ;
		double   worst_sufferage ;
		double   sufferage_task ;
		int      bestMachine  ;
		int      bestMachTask ;
		int      bestTask     ;
		double   minCTTask, second_minCTTask;
	
        int      randomValues        = numberOfTasks>>2;

        int[] tasksIdx    = RandomVector.getRandomVector_Int( randomValues , numberOfTasks    );
        int[] machinesIdx = RandomVector.getRandomVector_Int( randomValues , numberOfMachines );

        Calendar cal = Calendar.getInstance();
        Random rand = new Random(cal.getTimeInMillis());

        // Esta linea va solo si se fija numberOfAllocationsnumberOfAllocations
        numberOfAllocations = rand.nextInt(100);
        //for( int t=0 ; t<randomValues ; ++t ){
        for( int t=0 ; t<numberOfAllocations; ++t ){
            int task    = tasksIdx[t];
            int machine = machinesIdx[t];
            schedule[ task ] = machine;
            computation[ machine ] += ETC[ task ][ machine ];
        } // for

		while (numberOfAllocations < numberOfTasks){
			// Seleccionar tarea no asignada con ct mnimo.
			bestTask    = -1   ;
			bestMachine = -1   ;
			worst_sufferage = 0.0;
			
			for( int i=0 ; i<numberOfTasks ; ++i ){
				minCTTask = Double.POSITIVE_INFINITY ;
				second_minCTTask = Double.POSITIVE_INFINITY ;
				sufferage_task = -1.0;
				bestMachTask = -1   ;

				if ( schedule[i] == NOT_ALLOCATED ){
					// No est asignada, evaluar el sufferage
					double   ct     = 0.0;
					double[] rowETC = ETC[i];
					// Recorrer mquinas.
					for ( int j=0 ; j<numberOfMachines ; ++j ){
						ct = computation[ j ] + rowETC[ j ];
						if ( ct < minCTTask ){
							minCTTask    = ct; 
							bestMachTask = j ;
						} else {
							if ( ct <= second_minCTTask ){
								second_minCTTask    = ct; 
							} // if
						}
					} // for
					sufferage_task = second_minCTTask - minCTTask;
				} // if

				if (sufferage_task >= worst_sufferage){
					worst_sufferage = sufferage_task;
					bestTask = i;
					bestMachine = bestMachTask;
				} // if
			} // for
			
			computation[ bestMachine ] += ETC[ bestTask ][ bestMachine ];
			schedule[ bestTask ]       =  bestMachine;
			++numberOfAllocations;

		} // while

		return schedule;
	} // Sufferage


	/** This method implements the Min-Min scheduling strategy for the coevolutionary:
	 *  it generates a partial solution using Min-Min
	 * 
	 * @param ETC Expected time to compute matrix
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines

	/** This method implements the Min-Min scheduling strategy for the coevolutionary:
	 *  it generates a partial solution using Min-Min
	 * 
	 * @param ETC Expected time to compute matrix
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines
	 * @param loadingPosition
	 * 
	 * @return The min-min scheduling allocation 
	 *         
	 */
	public static int[] minMin( double[][] ETC , int numberOfTasks , int numberOfMachines , int loadingPosition ){
		int[] schedule = new int[numberOfTasks];
		
		Arrays.fill( schedule , NOT_ALLOCATED );
		
		double[] computation         = new double[ numberOfMachines ] ;
		int      numberOfAllocations = 0 ;
		double   minCT        ;
		double   minCTTask    ;
		int      bestMachine  ;
		int      bestMachTask ;
		int      bestTask     ;
		
		while (numberOfAllocations < numberOfTasks){
			// Seleccionar tarea no asignada con ct mnimo.
			bestTask    = -1   ;
			bestMachine = -1   ;
			minCT       = Double.POSITIVE_INFINITY ;
			
			int init = loadingPosition*numberOfTasks;
			for( int i= 0 ; i<numberOfTasks ; ++i ){
				minCTTask    = Double.POSITIVE_INFINITY ;
				bestMachTask = -1   ;

				if ( schedule[i] == NOT_ALLOCATED ){
					// No est asignada, evaluar el minimo ct de la tarea.
					double   ct     = 0.0;
					double[] rowETC = ETC[i+init];
					// Recorrer mquinas.
					for ( int j=0 ; j<numberOfMachines ; ++j ){
						ct = computation[ j ] + rowETC[ j ];
						if ( ct < minCTTask ){
							minCTTask    = ct; 
							bestMachTask = j ;
						} // if
					} // for
				} // if

				if ( minCTTask < minCT ){
					minCT = minCTTask;
					bestTask = i;
					bestMachine = bestMachTask;
				} // if
			} // for
			
			computation[ bestMachine ] += ETC[ bestTask+init ][ bestMachine ];
			schedule[ bestTask]       =  bestMachine;
			++numberOfAllocations;

		} // while
		
//		System.out.print("Isla " + loadingPosition + "; Minmin initialization: ");
//		for(int i =0; i<numberOfTasks; i++)
//			System.out.print(schedule[i] + ", ");
//		System.out.println();

		return schedule;
	} // minMin

	/** This method implements the Min-Min  scheduling strategy for the coevolutionary:
	 *  it generates a partial solution using Min-Min
	 * 
	 * @param ETC Expected time to compute matrix
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines
	 * @param loadingPosition 
	 * 
	 * @return The min-min scheduling allocation
	 */
	public static int[] minMinInitialization( double[][] ETC , int numberOfTasks , int numberOfMachines , int loadingPosition ){
		int[] schedule = new int[numberOfTasks];
		
		Arrays.fill( schedule , NOT_ALLOCATED );
		
		int      randomValues        = numberOfTasks>>2;
		double[] computation         = new double[ numberOfMachines ] ;
		int      numberOfAllocations = randomValues ;
		double   minCT        ;
		double   minCTTask    ;
		int      bestMachine  ;
		int      bestMachTask ;
		int      bestTask     ;
		
		int[] tasksIdx    = RandomVector.getRandomVector_Int( randomValues , numberOfTasks    );
		int[] machinesIdx = RandomVector.getRandomVector_Int( randomValues , numberOfMachines );
		
		int init = loadingPosition*numberOfTasks;

		for( int t=0 ; t<randomValues ; ++t ){
			int task    = tasksIdx[t];
			int machine = machinesIdx[t];
			schedule[ task ] = machine;
			computation[ machine ] += ETC[ task + init ][ machine ];
		} // for
		
		while (numberOfAllocations < numberOfTasks){
			// Seleccionar tarea no asignada con ct mnimo.
			bestTask    = -1   ;
			bestMachine = -1   ;
			minCT       = Double.POSITIVE_INFINITY ;
			
			for( int i=0 ; i<numberOfTasks ; ++i ){
				minCTTask    = Double.POSITIVE_INFINITY ;
				bestMachTask = -1   ;

				if ( schedule[i] == NOT_ALLOCATED ){
					// No est asignada, evaluar el miimo ct de la tarea.
					double   ct     = 0.0;
					double[] rowETC = ETC[i+init];
					// Recorrer mquinas.
					for ( int j= 0 ; j<numberOfMachines ; ++j ){
						ct = computation[ j ] + rowETC[ j ];
						if ( ct < minCTTask ){
							minCTTask    = ct; 
							bestMachTask = j ;
						} // if
					} // for
				} // if

				if ( minCTTask < minCT ){
					minCT = minCTTask;
					bestTask = i;
					bestMachine = bestMachTask;
				} // if
			} // for
			
			computation[ bestMachine ] += ETC[ bestTask+init ][ bestMachine ];
			schedule[ bestTask]       =  bestMachine;
			++numberOfAllocations;

		} // while

//		System.out.print("Isla " + loadingPosition + "; Minmin initialization: ");
//		for(int i =0; i<numberOfTasks; i++)
//			System.out.print(schedule[i] + ", ");
//		System.out.println();
		
		return schedule;
	} // minMin
	
	/** This method implements the Min-Min scheduling strategy.
	 * 
	 * @param ETC Expected time to compute matrix
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines
	 * 
	 * @return The min-min scheduling allocation
	 */
	public static int[] minMinInitialization( double[][] ETC , int numberOfTasks , int numberOfMachines ){
		int[] schedule = new int[numberOfTasks];
		
		Arrays.fill( schedule , NOT_ALLOCATED );
		
		int      randomValues        = numberOfTasks>>2;
		double[] computation         = new double[ numberOfMachines ] ;
		int      numberOfAllocations = randomValues ;
		double   minCT        ;
		double   minCTTask    ;
		int      bestMachine  ;
		int      bestMachTask ;
		int      bestTask     ;
		
		int[] tasksIdx    = RandomVector.getRandomVector_Int( randomValues , numberOfTasks    );
		int[] machinesIdx = RandomVector.getRandomVector_Int( randomValues , numberOfMachines );

        Calendar cal = Calendar.getInstance();
        Random rand = new Random(cal.getTimeInMillis());
	
		// Esta linea va solo si se fija numberOfAllocationsnumberOfAllocations 
		numberOfAllocations = rand.nextInt(100);
		//for( int t=0 ; t<randomValues ; ++t ){
		for( int t=0 ; t<numberOfAllocations; ++t ){
			int task    = tasksIdx[t];
			int machine = machinesIdx[t];
			schedule[ task ] = machine;
			computation[ machine ] += ETC[ task ][ machine ];
		} // for
		
		while (numberOfAllocations < numberOfTasks){
			// Seleccionar tarea no asignada con ct mimo.
			bestTask    = -1   ;
			bestMachine = -1   ;
			minCT       = Double.POSITIVE_INFINITY ;
			
			for( int i=0 ; i<numberOfTasks ; ++i ){
				minCTTask    = Double.POSITIVE_INFINITY ;
				bestMachTask = -1   ;

				if ( schedule[i] == NOT_ALLOCATED ){
					// No est asignada, evaluar el minimo ct de la tarea.
					double   ct     = 0.0;
					double[] rowETC = ETC[i];
					// Recorrer mquinas.
					for ( int j=0 ; j<numberOfMachines ; ++j ){
						ct = computation[ j ] + rowETC[ j ];
						if ( ct < minCTTask ){
							minCTTask    = ct; 
							bestMachTask = j ;
						} // if
					} // for
				} // if

				if ( minCTTask < minCT ){
					minCT = minCTTask;
					bestTask = i;
					bestMachine = bestMachTask;
				} // if
			} // for
			
			computation[ bestMachine ] += ETC[ bestTask ][ bestMachine ];
			schedule[ bestTask ]       =  bestMachine;
			++numberOfAllocations;

		} // while

		return schedule;
	} // minMin


	/** This method calculates the makespan of a vector computation time of machines
	 * @param computation
	 */
	public static double makespan( double[] computation ) {

		return ArrayUtils.getMax( computation );
	} // makespan


	/** This method calculates the flowTime
	 * @param ETC Expected time to compute matrix
	 * @param schedule The scheduling allocation
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines
	 * 
	 * @return The flow time
	 */
	public static double flowTime_Original( double[][] ETC           , int[] schedule ,
                                            int        numberOfTasks , int   numberOfMachines ){
		double[][] A = new double[numberOfMachines][numberOfTasks];
		
		int[]  numberOfTasksAllocated = new int[numberOfMachines]; // The number of tasks assigned to each processor.
		int    machine , position ;
		double time;
		
		// Insert the tasks in the right order
		for( int j=0 ; j<numberOfTasks ; ++j ){
			machine  = schedule[j];
			position = 0;
			time     = ETC[j][machine];
			int nta  = numberOfTasksAllocated[machine];

			while( (position < nta) && ( A[machine][position]<time ) )
				++position;

			if( position < nta ) // Shift elements right by one
				System.arraycopy( A[machine] , position , A[machine] , position+1 , nta-position );

			A[machine][position] = time;
			++numberOfTasksAllocated[machine];
		} // for

		double flow = 0.0;

		int nta;

		for( int j=0 ; j<numberOfMachines ; ++j ){ // Contribution of each machine
			nta = numberOfTasksAllocated[j];
			for( int k=0 ; k<nta ; ++k ) // Contribution of the task k
				flow += A[j][k] * ( nta - k );

		} // for
		return flow;
		
	} // flowTime
	
	
	/** This method calculates the flowTime
	 * 
	 * @param ETC Expected time to compute matrix
	 * @param schedule The scheduling allocation
	 * @param numberOfTasks number of tasks
	 * @param numberOfMachines number of machines
	 * 
	 * @return The flow time
	 */
	public static double flowTime( double[][] ETC           , int[] schedule ,
                                   int        numberOfTasks , int   numberOfMachines ){
		double[][] A = new double[numberOfMachines][numberOfTasks];
		
		int[]  numberOfTasksAllocated = new int[numberOfMachines]; // The number of tasks assigned to each processor.
		int    machine ;

		for( int j=0 ; j<numberOfTasks ; ++j ){
			machine = schedule[j];
			A[ machine ][ numberOfTasksAllocated[machine] ] = ETC[j][machine];
			++numberOfTasksAllocated[machine];
		} // for

		double   flow = 0.0;
		int      nta ;
		double[] row ;

		for( int j=0 ; j<numberOfMachines ; ++j ){ // Contribution of each machine

			nta = numberOfTasksAllocated[j];
			row = A[j];
			
			Arrays.sort( row , 0 , nta );

			for( int k=0 ; k<nta ; ++k ) // Contribution of the task k
				flow += row[k] * ( nta - k );

		} // for
		return flow;
	} // flowTime

} // ScheduleStrategy
