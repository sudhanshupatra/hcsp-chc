//============================================================================
// Name        : transformar_instancias.cpp
// Author      : Santiago Iturriaga
// Version     :
// Copyright   : 
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <string>
#include <cstdlib>
#include <fstream>
#include <stdlib.h>
#include <assert.h>

using namespace std;

// Returns a random number in [0,1].
double rand_01 ()
{
	return drand48();
}

// Returns a random number
int rand_int (int min, int max)
{
	int value =rand();
	int range = (max - min);
	int order = value % (range+1);
	return (min + order);
}

// selects a seed
void rand_seed(long int seed)
{
	srand48(seed);
	srand(seed);
}

int main(int argc, char** argv) {
	if (argc < 6) {
		cout << "Invocación incorrecta. Por favor ingrese:" << endl << endl;
		cout << argv[0] << " <intput file> <output file> <seed> <min priority> <max priority> [<task count> <machine count>]" << endl << endl;
		return EXIT_FAILURE;
	}

	string inputFilePath(argv[1]);
	cout << endl << "Input file: " << inputFilePath << endl;

	string outputFilePath(argv[2]);
	cout << "Output file: " << outputFilePath << endl;

	string randomSeedString(argv[3]);
	long int randomSeed = atol(randomSeedString.data());
	cout << "Random seed: " << randomSeed << endl;

	string minPriorityString(argv[4]);
	int minPriority = atoi(minPriorityString.data());
	cout << "Min. priority: " << minPriority << endl;

	string maxPriorityString(argv[5]);
	int maxPriority = atoi(maxPriorityString.data());
	cout << "Max. priority: " << maxPriority << endl << endl;

	ifstream inputFileStream(inputFilePath.data());
	if (!inputFileStream) {
		cout << "[ERROR] No se encontró el archivo de entrada " << inputFilePath << endl;
		return EXIT_FAILURE;
	}

	ofstream outputFileStream(outputFilePath.data());
	if (!outputFileStream) {
		cout << "[ERROR] ocurrió un error con el archivo de salida " << outputFilePath << endl;
		return EXIT_FAILURE;
	}

	assert(maxPriority >= minPriority);

	rand_seed(randomSeed);

	string aux_line;
	int priorityTaskCount[maxPriority - minPriority + 1];
	for (int i = 0; i < maxPriority - minPriority + 1; i++) priorityTaskCount[i] = 0;

	int taskCount;
	int machineCount;
	int estado = 0;

	if (argc >= 8) {
		string taskCountString, machineCountString;
		taskCountString = argv[6];
		machineCountString = argv[7];

		taskCount = atoi(taskCountString.data());
		machineCount = atoi(machineCountString.data());

		outputFileStream << taskCount << " " << machineCount << endl;
		estado = 2;
	}

	if (inputFileStream.good()) {
		inputFileStream >> aux_line;

		while (inputFileStream.good()) {
			//cout << "[ESTADO " << estado << "] " << aux_line << endl;

			switch (estado) {
			case 0:
				// Leo la información global del taskCount.
				taskCount = atoi(aux_line.data());
				outputFileStream << aux_line << " ";

				estado = 1;
				inputFileStream >> aux_line;

				break;
			case 1:
				// Leo la información global del machineCount.
				machineCount = atoi(aux_line.data());
				outputFileStream << aux_line << endl;

				estado = 2;
				inputFileStream >> aux_line;

				break;
			case 2:
				// Genero las prioridades.
				for (int taskId = 0; taskId < taskCount; taskId++) {
					int randomPriority;
					randomPriority = rand_int(minPriority, maxPriority);
					priorityTaskCount[randomPriority-1] = priorityTaskCount[randomPriority-1] + 1;

					outputFileStream << randomPriority << endl;
				}

				estado = 3;

				break;
			case 3:
				// Copio los costos de ejecución de cada tareas en las máquinas.
				outputFileStream << aux_line << endl;
				inputFileStream >> aux_line;
				//if (!inputFileStream.eof()) outputFileStream << aux_line << endl;

				break;
			}
		}
	}

	inputFileStream.close();
	outputFileStream.close();

	cout << "Prioridades asignadas:" << endl;
	for (int i = 0; i < maxPriority - minPriority + 1; i++) {
		cout << i+1 << " = " << priorityTaskCount[i] << endl;;
	}

	return EXIT_SUCCESS;
}
