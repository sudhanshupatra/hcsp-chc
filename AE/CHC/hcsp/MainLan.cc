#include <iostream>
#include <fstream>
#include <vector>
#include <assert.h>

#include "CHC.hh"

using namespace std;

int main(int argc, char** argv) {
	using skeleton CHC;
	char path[MAX_BUFFER] = "";

	//int clear = system("clear");

	//get_path(argv[0],path);
	//cout << "[INFO] argv[0] " << path << endl;

	//cout << "[INFO] argv[1] " << path << endl;
	ifstream f(argv[1]);
	if(!f) show_message(10);

	f.getline(path,MAX_BUFFER,'\n');
	//cout << "[INFO] getline " << path << endl;
	ifstream f1(path);
	if(!f1) show_message(11);

	f.getline(path,MAX_BUFFER,'\n');
	//cout << "[INFO] getline " << path << endl;
	ifstream f2(path);
	if(!f2) show_message(12);

	f.getline(path,MAX_BUFFER,'\n'); // sol.txt
	string out_file(path);

	f.getline(path,MAX_BUFFER,'\n'); // pesos.txt
	//out << "[INFO] getline " << path << endl;
	ifstream f3(path);
	if(!f3) show_message(-1);

	Problem pbm;
	f2 >> pbm;

	Operator_Pool pool(pbm);
	SetUpParams cfg(pool, pbm);
	f1 >> cfg;

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
		for (int i = 0; i < pesos.size(); i++) {
			cout << "'" << pesos[i] << "'" << endl;
		}
	assert(pesos.size() % 2 == 0);

	pbm.loadWeights(pesos);
	// ============================================

	Solver_Lan solver(pbm,cfg,argc,argv);
	solver.run();

	//if (solver.pid()==0)
	//{

	if (solver.pid()!=0)
	{
		char str_pid[100];
		sprintf(str_pid, "%d", solver.pid());

		//f.getline(path,MAX_BUFFER,'\n');
		out_file = out_file.append("_").append(str_pid);
		//cout << "Output file: " << out_file << endl;
		ofstream fexit(out_file.data());
		if(!fexit) show_message(13);
		//fexit << solver.userstatistics();

		//solver.show_state();
		//		cout << "[pid:" << solver.pid() << "] Solucion: " << solver.global_best_solution() << endl;
		//		cout << "[pid:" << solver.pid() << "] Makespan: " << solver.global_best_solution().makespan() << endl;
		//		cout << "[pid:" << solver.pid() << "] AWRR: " << solver.global_best_solution().accumulatedWeightedResponseRatio() << endl;
		//		cout << "[pid:" << solver.pid() << "] Fitness: " << solver.global_best_solution().fitness() << endl;
		//solver.global_best_solution().showCustomStatics();

		//		cout << "[pid:" << solver.pid() << "] Makespan: " << solver.current_best_solution().makespan() << endl;
		//		cout << "[pid:" << solver.pid() << "] AWRR: " << solver.current_best_solution().accumulatedWeightedResponseRatio() << endl;
		//		cout << "[pid:" << solver.pid() << "] Fitness: " << solver.current_best_solution().fitness() << endl;

		//	char salida[5000];
		//	sprintf(salida, "[pid:%d] Peso Makespan %f // Peso WRR: %f\n", solver.pid(), pbm.getMakespanWeight(), pbm.getWRRWeight());
		//	cout << salida;
		//	sprintf(salida, "[pid:%d] Makespan %f\n", solver.pid(), solver.best_solution_trial().makespan());
		//	cout << salida;
		//	sprintf(salida, "[pid:%d] WRR %f\n", solver.pid(), solver.best_solution_trial().accumulatedWeightedResponseRatio());
		//	cout << salida;
		//	sprintf(salida, "[pid:%d] Fitness %f\n", solver.pid(), solver.best_solution_trial().fitness());
		//	cout << salida;
		//	//sprintf(salida, "[pid:%d] Peso Makespan %f // Peso WRR: %f\n[pid:%d] Makespan %f\n[pid:%d] WRR %f\n[pid:%d] Fitness %f\n", solver.pid(), pbm.getMakespanWeight(), pbm.getWRRWeight(), solver.pid(), solver.best_solution_trial().makespan(), solver.pid(), solver.best_solution_trial().accumulatedWeightedResponseRatio(), solver.pid(), solver.best_solution_trial().fitness());
		//	cout << salida;

		//		cout << "[pid:" << solver.pid() << "] Peso Makespan " << pbm.getMakespanWeight() << " // Peso WRR: " << pbm.getWRRWeight() << endl;
		//		cout << "[pid:" << solver.pid() << "] Makespan: " << solver.best_solution_trial().makespan() << endl;
		//		cout << "[pid:" << solver.pid() << "] AWRR: " << solver.best_solution_trial().accumulatedWeightedResponseRatio() << endl;
		//		cout << "[pid:" << solver.pid() << "] Fitness: " << solver.best_solution_trial().fitness() << endl;

//		fexit << "[pid:" << solver.pid() << "] Peso Makespan " << pbm.getMakespanWeight() << " // Peso WRR: " << pbm.getWRRWeight() << endl;
//		fexit << "[pid:" << solver.pid() << "] Makespan: " << solver.best_solution_trial().makespan() << endl;
//		fexit << "[pid:" << solver.pid() << "] AWRR: " << solver.best_solution_trial().accumulatedWeightedResponseRatio() << endl;
//		fexit << "[pid:" << solver.pid() << "] Fitness: " << solver.best_solution_trial().fitness() << endl;

		cout << "[pid:" << solver.pid() << "] Peso Makespan " << pbm.getMakespanWeight() << " // Peso WRR: " << pbm.getWRRWeight() << endl;
		fexit << solver.best_solution_trial().makespan() << " " << solver.best_solution_trial().accumulatedWeightedResponseRatio() << " "  << solver.pid() << endl;

		//fexit << solver.best_solution_trial();

		//cout << endl << endl << " :( ---------------------- THE END --------------- :) " << endl;
	} else {
		cout << solver.userstatistics();
		solver.show_state();
	}
	return(0);
}
