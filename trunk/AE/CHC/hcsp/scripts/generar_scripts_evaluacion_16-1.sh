#!/bin/bash

cd ../ejecuciones

if [ $? != 0 ]; then
	exit $?
fi

Poblacion="10"
Cruzamiento="0.9"
Mutacion="0.9"
Timeout="90"

echo "Población $Poblacion"
echo "Cruzamiento $Cruzamiento"
echo "Mutación $Mutacion"
echo "Timeout $Timeout"

Filename="scripts_evaluacion/chc_lan16.cfg"
echo "30			// number of independent runs" > $Filename

if [ $? != 0 ]; then
	exit $?
fi

echo "100000			// number of generations" >> $Filename
echo "$Poblacion			// number of individuals" >> $Filename
echo "0				// display state ?" >> $Filename
echo "0				// seed, 0 = aleatorio" >> $Filename
echo "$Timeout				// peso makespan, peso wqt" >> $Filename
echo "Selection-Parameters	// selections to apply" >> $Filename
echo "0.9 1 $Mutacion			// selection parameter, diverge operator & its probability" >> $Filename
echo "Intra-Operators		// operators to apply in the population" >> $Filename
echo "0 $Cruzamiento			// crossover & its probability" >> $Filename
echo "Inter-Operators  		// operators to apply between this population and anothers" >> $Filename
echo "0 500 3 1 3 4 0		// operator number, operator rate, number of individuals, selection of indidivual to send and remplace" >> $Filename
echo "LAN-configuration" >> $Filename
echo "10001			// refresh global state" >> $Filename
echo "1				// 0: running in asynchronized mode / 1: running in synchronized mode" >> $Filename
echo "1				// interval of generations to check solutions from other populations" >> $Filename
	