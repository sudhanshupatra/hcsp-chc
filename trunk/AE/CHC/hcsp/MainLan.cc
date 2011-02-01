#include <iostream>
#include <fstream>
#include "CHC.hh"

using namespace std;

int main (int argc, char** argv)
{
	using skeleton CHC;
	char path[MAX_BUFFER] = "";
	int len;
	int longitud;

	//int clear = system("clear");

	//get_path(argv[0],path);
	len = strlen(path);
	longitud = MAX_BUFFER - len;

	cout << "[INFO] argv[0] " << path << endl;

	strcat(path,argv[1]);
	cout << "[INFO] argv[1] " << path << endl;
	ifstream f(path);
	if(!f) show_message(10);

	f.getline(&(path[len]),longitud,'\n');
	cout << "[INFO] getline " << path << endl;
	ifstream f1(path);
	if(!f1)	show_message(11);

	f.getline(&(path[len]),longitud,'\n');
	cout << "[INFO] getline " << path << endl;
	ifstream f2(path);
	if(!f2) show_message(12);

	Problem pbm;
	f2 >> pbm;

	Operator_Pool pool(pbm);
	SetUpParams cfg(pool, pbm);
	f1 >> cfg;

	Solver_Lan solver(pbm,cfg,argc,argv);
	solver.run();

	if (solver.pid()==0)
	{
		solver.show_state();
		cout << "Solucion: " << solver.global_best_solution() << endl;
		cout << "Makespan: " << solver.global_best_solution().makespan() << endl;
		cout << "AWRR: " << solver.global_best_solution().accumulatedWeightedResponseRatio() << endl;
		cout << "Fitness: " << solver.global_best_solution().fitness() << endl;
		solver.global_best_solution().showCustomStatics();

		f.getline(&(path[len]),longitud,'\n');
	  	ofstream fexit(path);
	  	if(!fexit) show_message(13);
	  	fexit << solver.userstatistics();

		cout << endl << endl << " :( ---------------------- THE END --------------- :) " << endl;
	}
	return(0);
}
