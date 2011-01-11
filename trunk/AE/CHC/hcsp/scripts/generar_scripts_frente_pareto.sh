#!/bin/bash

cd ../ejecuciones

if [ $? != 0 ]; then
	exit $?
fi

Poblacion="15"
Cruzamiento="0.8"
Mutacion="0.9"

Pesos[0]="0.10 0.90"
Pesos[1]="0.25 0.75"
Pesos[2]="0.5 0.5"
Pesos[3]="0.75 0.25"
Pesos[4]="0.90 0.10"

for indexP in {0..4}
do
	echo "Población $Poblacion"
	echo "Cruzamiento $Cruzamiento"
	echo "Mutación $Mutacion"
	echo "Pesos ${Pesos[indexP]}"

	Filename="scripts_frente_pareto/chc_$indexP.cfg"
	echo "5			// number of independent runs" > $Filename
	
	if [ $? != 0 ]; then
		exit $?
	fi
	
	echo "100000			// number of generations" >> $Filename
	echo "$Poblacion			// number of individuals" >> $Filename
	echo "0				// display state ?" >> $Filename
	echo "0				// seed, 0 = aleatorio" >> $Filename
	echo "${Pesos[indexP]}				// peso makespan, peso wqt" >> $Filename
	echo "Selection-Parameters	// selections to apply" >> $Filename
	echo "0.9 1 $Mutacion			// selection parameter, diverge operator & its probability" >> $Filename
	echo "Intra-Operators		// operators to apply in the population" >> $Filename
	echo "0 $Cruzamiento			// crossover & its probability" >> $Filename
	echo "Inter-Operators  		// operators to apply between this population and anothers" >> $Filename
	echo "0 50 3 1 2 1 3		// operator number, operator rate, number of individuals, selection of individual to send and remplace" >> $Filename
	echo "LAN-configuration" >> $Filename
	echo "51			// refresh global state" >> $Filename
	echo "0				// 0: running in asynchronized mode / 1: running in synchronized mode" >> $Filename
	echo "10				// interval of generations to check solutions from other populations" >> $Filename
done	