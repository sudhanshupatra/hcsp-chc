#!/bin/bash

Poblacion="10"
Cruzamiento="1.0"
Mutacion="1.0"
Pesos="1.0 1.0"

Filename="scripts_evaluacion/chc.cfg"
echo "20			// number of independent runs" > $Filename
echo "100000			// number of generations" >> $Filename
echo "$Poblacion			// number of individuals" >> $Filename
echo "0				// display state ?" >> $Filename
echo "0				// seed, 0 = aleatorio" >> $Filename
echo "$Pesos				// peso makespan, peso wqt" >> $Filename
echo "Selection-Parameters	// selections to apply" >> $Filename
echo "0.9 1 $Mutacion			// selection parameter, diverge operator & its probability" >> $Filename
echo "Intra-Operators		// operators to apply in the population" >> $Filename
echo "0 $Cruzamiento			// crossover & its probability" >> $Filename
echo "Inter-Operators  		// operators to apply between this population and anothers" >> $Filename
echo "0 50 5 1 3 1 5		// operator number, operator rate, number of individuals, selection of individual to send and remplace" >> $Filename
echo "LAN-configuration" >> $Filename
echo "1001			// refresh global state" >> $Filename
echo "0				// 0: running in asynchronized mode / 1: running in synchronized mode" >> $Filename
echo "1				// interval of generations to check solutions from other populations" >> $Filename
	