#include <iostream>
#include <fstream>
#include "CHC.hh"

using namespace std;

int main (int argc, char** argv)
{
	using skeleton CHC;

//	int clear = system("clear");

	if(argc < 4)
		show_message(1);

	ifstream f1(argv[1]);
	if (!f1) show_message(11);

	ifstream f2(argv[2]);
	if (!f2) show_message(12);

	Problem pbm;
	exit(EXIT_FAILURE);

	//	f2 >> pbm;

	// Seteo el tamaño del problema.
	//pbm.setTaskCount(atof(cantidad_tareas.data()));
	//pbm.setMachineCount(atof(cantidad_maquinas.data()));

	// Cargo la instancia a resolver.
	// instance_stream >> pbm;
	//pbm.loadProblemDataFiles(scenario_stream, workload_stream, priorities_stream);

	Operator_Pool pool(pbm);
	SetUpParams cfg(pool, pbm);
	f1 >> cfg;

	vector<double> pesos;
	pesos.push_back(1.0);
	pesos.push_back(1.0);
	pbm.loadWeightData(pesos);

	Solver_Seq solver(pbm,cfg);
	solver.run();

	if (solver.pid()==0)
	{
		solver.show_state();
		cout << "Solution";
		solver.global_best_solution().show(cout);
		cout << endl << "Makespan: " << solver.global_best_solution().getMakespan() << endl;
		cout << "Energy: " << solver.global_best_solution().getEnergy(solver.global_best_solution().getMakespan()) << endl;
		cout << "Fitness: " << solver.global_best_solution().getFitness() << endl;
		solver.global_best_solution().showCustomStatics(cout);

		cout << "\n\n :( ---------------------- THE END --------------- :) ";

		ofstream fexit(argv[3]);
		if(!fexit) show_message(13);
		fexit << solver.userstatistics();
	}

	return(0);
}
