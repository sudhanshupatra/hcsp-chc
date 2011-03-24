#include <iostream>
#include <fstream>
#include <vector>
#include <assert.h>

#include "CHC.hh"

using namespace std;

int main(int argc, char** argv) {
	using skeleton CHC;
	char path[MAX_BUFFER] = "";

	cout << "[INFO] Exec: " << argv[0] << endl;

	ifstream f(argv[1]);
	if(!f) show_message(10);
	cout << "[INFO] Configuration file: " << argv[1] << endl;

	// Leo desde el configuration file ================================
	f.getline(path,MAX_BUFFER,'\n');
	cout << "[CONFIG] Skeleton file: " << path << endl;
	ifstream f1(path);
	if(!f1) show_message(11);

	f.getline(path,MAX_BUFFER,'\n');
	cout << "[CONFIG] Instancia: " << path << endl;
	ifstream f2(path);
	if(!f2) show_message(12);

	f.getline(path,MAX_BUFFER,'\n'); // sol.txt
	string out_file(path);
	cout << "[CONFIG] Summary:" << path << endl;

	f.getline(path,MAX_BUFFER,'\n'); // pesos.txt
	cout << "[CONFIG] Pesos:" << path << endl;
	ifstream f3(path);
	if(!f3) show_message(-1);

	Problem pbm;
	f2 >> pbm;

	Operator_Pool pool(pbm);
	SetUpParams cfg(pool, pbm);
	f1 >> cfg;
	cout << cfg;

	// ============================================
	vector<double> pesos;
	string linea_pesos;

	f3 >> linea_pesos;
	while (f3.good()) {
		if (linea_pesos.length() > 0) {
			//cout << linea_pesos << endl;
			pesos.push_back(atof(linea_pesos.data()));
		}
		f3 >> linea_pesos;
	}
	f3.close();

	cout << "Cantidad: " << pesos.size() << endl;
	for (unsigned int i = 0; i < pesos.size(); i++) {
		cout << "'" << pesos[i] << "'" << endl;
	}
	assert(pesos.size() % 2 == 0);

	pbm.loadWeights(pesos);
	// ============================================

	Solver_Lan solver(pbm,cfg,argc,argv);
	solver.run();

	if (solver.pid()!=0)
	{
		char str_pid[100];
		sprintf(str_pid, "%d", solver.pid());

		out_file = out_file.append("_").append(str_pid);
		ofstream fexit(out_file.data());
		if(!fexit) show_message(13);

		fexit << solver.best_solution_trial().makespan() << " " << solver.best_solution_trial().accumulatedWeightedResponseRatio() << " " << solver.pid() << endl;

		for (int i = 0; i < solver.population().parents().size(); i++) {
			fexit << solver.population().parents()[i]->makespan() << " " << solver.population().parents()[i]->accumulatedWeightedResponseRatio() << " " << solver.pid() << endl;
		}

		for (int i = 0; i < solver.population().offsprings().size(); i++) {
			fexit << solver.population().offsprings()[i]->makespan() << " " << solver.population().offsprings()[i]->accumulatedWeightedResponseRatio() << " " << solver.pid() << endl;
		}
	} else {
		solver.show_state();
		cout << "Solucion: " << solver.global_best_solution() << endl;
		cout << "Makespan: " << solver.global_best_solution().makespan() << endl;
		cout << "WRR: " << solver.global_best_solution().accumulatedWeightedResponseRatio() << endl;
		cout << "Fitness: " << solver.global_best_solution().fitness() << endl;

		solver.global_best_solution().showCustomStatics();
		cout << solver.userstatistics();
		cout << endl << endl << " :( ---------------------- THE END --------------- :) " << endl;
	}
	return(0);
}
