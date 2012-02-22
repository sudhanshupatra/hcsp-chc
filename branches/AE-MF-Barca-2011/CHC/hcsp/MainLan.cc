#include <iostream>
#include <fstream>
#include <vector>
#include <assert.h>

#include "CHC.hh"

using namespace std;

int main(int argc, char** argv) {
	using skeleton CHC;
	char path[MAX_BUFFER] = "";

	ifstream f(argv[1]);
	if(!f) show_message(10);

	// Leo desde el configuration file ================================
	f.getline(path,MAX_BUFFER,'\n');
	string skeleton_file(path);
	ifstream f1(path);
	if(!f1) show_message(11);

	f.getline(path,MAX_BUFFER,'\n');
	string instance_file(path);
	ifstream f2(path);
	if(!f2) show_message(12);

	f.getline(path,MAX_BUFFER,'\n'); // sol.txt
	string solution_file(path);

	f.getline(path,MAX_BUFFER,'\n'); // pesos.txt
	string pesos_file(path);
	ifstream f3(path);
	if(!f3) show_message(-1);

	f.getline(path,MAX_BUFFER,'\n'); // tasks
	string tasks_count(path);

	f.getline(path,MAX_BUFFER,'\n'); // machines
	string machines_count(path);

	Problem pbm(atoi(tasks_count.data()), atoi(machines_count.data()));
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

	pbm.loadWeights(pesos);
	// ============================================

	Solver_Lan solver(pbm,cfg,argc,argv);
	solver.run();

	if (solver.pid()!=0)
	{
		char str_pid[100];
		sprintf(str_pid, "%d", solver.pid());

		{
			solution_file = solution_file.append("_").append(str_pid);
			ofstream fexit(solution_file.data());
			if(!fexit) show_message(13);

			fexit << solver.best_solution_trial().makespan() * 10000.0 << " " << solver.best_solution_trial().accumulatedWeightedResponseRatio() * 10000.0 << " " << solver.pid() << endl;

			for (int i = 0; i < solver.population().parents().size(); i++) {
				fexit << solver.population().parents()[i]->makespan() * 10000.0 << " " << solver.population().parents()[i]->accumulatedWeightedResponseRatio() * 10000.0 << " " << solver.pid() << endl;
			}

			for (int i = 0; i < solver.population().offsprings().size(); i++) {
				fexit << solver.population().offsprings()[i]->makespan() * 10000.0 << " " << solver.population().offsprings()[i]->accumulatedWeightedResponseRatio() * 10000.0 << " " << solver.pid() << endl;
			}

			fexit.close();
		}
		{
			//string fit_solution_file = solution_file.append("_fit");
			//ofstream fexit(fit_solution_file.data());
			//if(!fexit) show_message(13);

			//fexit << solver.best_solution_trial().fitness() << " " << solver.pid() << endl;
			//solver.best_solution_trial().show(fexit);

			//for (int i = 0; i < solver.population().parents().size(); i++) {
				//fexit << solver.population().parents()[i]->fitness() << " " << solver.pid() << endl;
				//fexit << "-2" << endl;
				//solver.population().parents()[i]->show(fexit);
			//}

			//for (int i = 0; i < solver.population().offsprings().size(); i++) {
				//fexit << solver.population().offsprings()[i]->fitness() << " " << solver.pid() << endl;
				//fexit << "-2" << endl;
				//solver.population().offsprings()[i]->show(fexit);
			//}

			//fexit.close();
		}

		{
			string meta_solution_file = solution_file.append("_meta");
			ofstream fexit(meta_solution_file.data());
			if(!fexit) show_message(13);

			fexit << "Pesos asignados" << endl;
			fexit << "Makespan weight: " << pbm.getMakespanWeight(solver.pid()) << endl;
			fexit << "WRR weight: " << pbm.getWRRWeight(solver.pid()) << endl;

			fexit.close();
		}
	}
	else {
		cout << "[INFO] Exec: " << argv[0] << endl;
		cout << "[INFO] Configuration file: " << argv[1] << endl;
		cout << "[CONFIG] Skeleton file: " << skeleton_file << endl;
		cout << "[CONFIG] Instancia: " << instance_file << endl;
		cout << "[CONFIG] Summary:" << solution_file << endl;
		cout << "[CONFIG] Pesos:" << pesos_file << endl << endl;

		cout << "[CONFIG] Pesos: " << pesos.size() << endl;
		for (unsigned int i = 0; i < pesos.size() - 1; i = i + 2) {
			cout << "(Makespan: " << pesos[i] << ", WRR: " << pesos[i+1] << ")" << endl;
		}
		assert(pesos.size() % 2 == 0);
		cout << endl << endl;

		cout << cfg;

		solver.statistics();
		solver.show_state();

		cout << "Makespan: " << solver.global_best_solution().makespan() * 10000.0 << endl;
		cout << "WRR: " << solver.global_best_solution().accumulatedWeightedResponseRatio() * 10000.0 << endl;
		cout << "Makespan (reference): " << Solution::getMakespan_reference() << endl;
		cout << "WRR (reference): " << Solution::getWRR_reference() << endl;

		double weight_mks = 0.0, weight_wrr = 0.0;
		double min_fitness = INFINITY;
		for (unsigned int i = 0; i < pesos.size() - 1; i=i+2) {
			double aux_fitness;
			aux_fitness = pesos[i]*(Solution::getMakespan_reference()+solver.global_best_solution().makespan())/Solution::getMakespan_reference()
			+ pesos[i+1]*(Solution::getWRR_reference()+solver.global_best_solution().accumulatedWeightedResponseRatio())/Solution::getWRR_reference();

			if (aux_fitness < min_fitness) {
				min_fitness = aux_fitness;
				weight_mks = pesos[i];
				weight_wrr = pesos[i+1];
			}
		}

		cout << "Fitness: " << min_fitness << " (" << weight_mks << ", " << weight_wrr << ")" << endl;

		ofstream fexit(solution_file.data());
		if(!fexit) show_message(13);
		fexit << solver.userstatistics();
		fexit.close();

		cout << endl << endl << " :( ---------------------- THE END --------------- :) " << endl;
	}
	return(0);
}
