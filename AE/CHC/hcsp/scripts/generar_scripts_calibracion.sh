#!/bin/bash

cd ../ejecuciones

if [ $? != 0 ]; then
	exit $?
fi

Poblacion[0]=5
Poblacion[1]=10
Poblacion[2]=15
Poblacion[3]=20
Poblacion[4]=30

Cruzamiento[0]=0.7
Cruzamiento[1]=0.8
Cruzamiento[2]=0.9
Cruzamiento[3]=1.0

Mutacion[0]=0.5
Mutacion[1]=0.7
Mutacion[2]=0.9
Mutacion[3]=1.0

for indexP in {0..4}
do
	for indexC in {0..3}
	do
		for indexM in {0..3}
		do
			echo "Población ${Poblacion[indexP]}"
			echo "Cruzamiento ${Cruzamiento[indexC]}"
			echo "Mutación ${Mutacion[indexM]}"

			Filename="scripts_calibracion/chc_${Poblacion[indexP]}_${Cruzamiento[indexC]}_${Mutacion[indexM]}.cfg"
			echo "20			// number of independent runs" > $Filename
			
			if [ $? != 0 ]; then
				exit $?
			fi
			
			echo "100000			// number of generations" >> $Filename
			echo "${Poblacion[indexP]}			// number of individuals" >> $Filename
			echo "0				// display state ?" >> $Filename
			echo "0				// seed, 0 = aleatorio" >> $Filename
			echo "1.0 1.0				// peso makespan, peso wqt" >> $Filename
			echo "Selection-Parameters	// selections to apply" >> $Filename
			echo "0.9 1 ${Mutacion[indexM]}			// selection parameter, diverge operator & its probability" >> $Filename
			echo "Intra-Operators		// operators to apply in the population" >> $Filename
			echo "0 ${Cruzamiento[indexC]}			// crossover & its probability" >> $Filename
			echo "Inter-Operators  		// operators to apply between this population and anothers" >> $Filename
			echo "0 50 5 1 3 1 5		// operator number, operator rate, number of individuals, selection of individual to send and remplace" >> $Filename
			echo "LAN-configuration" >> $Filename
			echo "1001			// refresh global state" >> $Filename
			echo "0				// 0: running in asynchronized mode / 1: running in synchronized mode" >> $Filename
			echo "1				// interval of generations to check solutions from other populations" >> $Filename
		done
	done	
done	

