#include <iostream>
#include <fstream>
#include <vector>
#include <assert.h>

#include "CHC.hh"

using namespace std;

int main(int argc, char** argv) {
	using skeleton CHC;

	string skeleton_file;
	string scenario_file;
	string workload_file;
	string priorities_file;
	string solution_file;
	string pesos_file;
	string cantidad_tareas;
	string cantidad_maquinas;

	if (argc > 1) {
		// ==================================================================
		// Leo desde el configuration file
		char path[MAX_BUFFER] = "";

		ifstream f(argv[1]); // Configuration file.
		if(!f) show_message(10);

		f.getline(path,MAX_BUFFER,'\n'); // Archivo de configuración del skeleton.
		skeleton_file.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Archivo con el scenario del problema a resolver.
		scenario_file.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Archivo con el workload del problema a resolver.
		workload_file.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Archivo con las prioridades del problema a resolver.
		priorities_file.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Archivo de salida.
		solution_file.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Archivo con pesos para las islas.
		pesos_file.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Cantidad de tareas.
		cantidad_tareas.assign(path);

		f.getline(path,MAX_BUFFER,'\n'); // Cantidad de máquinas.
		cantidad_maquinas.assign(path);
	} else {
		skeleton_file.assign("/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/ejecuciones/scripts_frente_pareto/barca.cfg");
		scenario_file.assign("/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/512x16/scenario.0");
		workload_file.assign("/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/512x16/workload.0");
		priorities_file.assign("/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/512x16/priorities.0");
		solution_file.assign("test.sol");
		pesos_file.assign("/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/ejecuciones/pesos_fijos.txt");
		cantidad_tareas.assign("512");
		cantidad_maquinas.assign("16");
	}

	cout << "[skeleton] " << skeleton_file << endl;
	cout << "[scenario] " << scenario_file << endl;
	cout << "[workload] " << workload_file << endl;
	cout << "[priorities] " << priorities_file << endl;
	cout << "[pesos] " << pesos_file << endl;

	ifstream skeleton_stream(skeleton_file.data());
	if(!skeleton_stream) show_message(11);

	ifstream scenario_stream(scenario_file.data());
	if(!scenario_stream) show_message(12);

	ifstream workload_stream(workload_file.data());
	if(!workload_stream) show_message(12);

	ifstream priorities_stream(priorities_file.data());
	if(!priorities_stream) show_message(12);

	// ==================================================================
	// Inicializo el problema y el skeleton.
	Problem pbm;

	// Seteo el tamaño del problema.
	pbm.setTaskCount(atof(cantidad_tareas.data()));
	pbm.setMachineCount(atof(cantidad_maquinas.data()));

	// Cargo la instancia a resolver.
	// instance_stream >> pbm;
	pbm.loadProblemDataFiles(scenario_stream, workload_stream, priorities_stream);

	Operator_Pool pool(pbm);
	SetUpParams cfg(pool, pbm);
	skeleton_stream >> cfg;

	// ==================================================================
	// Cargo los pesos de las islas.
	vector<double> pesos;
	{
		string linea_pesos;

		ifstream pesos_stream(pesos_file.data());
		if(!pesos_stream) show_message(-1);

		if (pesos_stream.is_open()) {
			while (!pesos_stream.eof()) {
				pesos_stream >> linea_pesos;

				if (linea_pesos.length() > 0) {
					pesos.push_back(atof(linea_pesos.data()));
				}
			}
		}

		pesos_stream.close();

		pbm.loadWeightData(pesos);
	}

	// ==================================================================
	// Inicio la ejecución del algoritmo.
	Solver_Lan solver(pbm,cfg,argc,argv);
	solver.run();

	// ==================================================================
	// Terminó el algoritmo. Escribo los resultado a disco.
	if (solver.pid()!=0)
	{
		// Si es es una isla de cómputo...

		char str_pid[100];
		sprintf(str_pid, "%d", solver.pid());
		solution_file = solution_file.append("_").append(str_pid);

		{
			// Escribo el la evaluación de las métricas en cada individuo de la población (padre e hijos).
			string makespan_solution_file = solution_file.append("_metricas");
			ofstream fexit(makespan_solution_file.data());
			if(!fexit) show_message(13);

			fexit << solver.best_solution_trial().getMakespan()
					<< " " << solver.best_solution_trial().getWRR()
					<< " " << solver.best_solution_trial().getEnergy(solver.best_solution_trial().getMakespan())
					<< " " << solver.pid() << endl;

			for (int i = 0; i < solver.population().parents().size(); i++) {
				fexit << solver.population().parents()[i]->getMakespan()
						<< " " << solver.population().parents()[i]->getWRR()
						<< " " << solver.population().parents()[i]->getEnergy(solver.population().parents()[i]->getMakespan())
						<< " " << solver.pid() << endl;
			}

			for (int i = 0; i < solver.population().offsprings().size(); i++) {
				fexit << solver.population().offsprings()[i]->getMakespan()
						<< " " << solver.population().offsprings()[i]->getWRR()
						<< " " << solver.population().parents()[i]->getEnergy(solver.population().offsprings()[i]->getMakespan())
						<< " " << solver.pid() << endl;
			}

			fexit.close();
		}
		{
			// Escribo el fitness de cada individuo de la población (padres e hijos).
			string fit_solution_file = solution_file.append("_fit");
			ofstream fexit(fit_solution_file.data());
			if(!fexit) show_message(13);

			fexit << solver.best_solution_trial().getFitness() << " " << solver.pid() << endl;

			for (int i = 0; i < solver.population().parents().size(); i++) {
				fexit << solver.population().parents()[i]->getFitness() << " " << solver.pid() << endl;
			}

			for (int i = 0; i < solver.population().offsprings().size(); i++) {
				fexit << solver.population().offsprings()[i]->getFitness() << " " << solver.pid() << endl;
			}

			fexit.close();
		}

		{
			// Escribo algo de meta información de la isla (p.ej.: que pesos le fueron asignados).
			string meta_solution_file = solution_file.append("_meta");
			ofstream fexit(meta_solution_file.data());
			if(!fexit) show_message(13);

			fexit << "Pesos asignados" << endl;
			fexit << "Makespan weight: " << pbm.getMakespanWeight(solver.pid()) << endl;
			fexit << "WRR weight: " << pbm.getWRRWeight(solver.pid()) << endl;
			fexit << "Energy weight: " << pbm.getEnergyWeight(solver.pid()) << endl;

			fexit.close();
		}
	}
	else {
		// Si es la isla master...
		cout << "[INFO] Exec: " << argv[0] << endl;
		cout << "[INFO] Configuration file: " << argv[1] << endl;
		cout << "[CONFIG] Skeleton file: " << skeleton_file << endl;
		cout << "[CONFIG] Scenario: " << scenario_file << endl;
		cout << "[CONFIG] Workload: " << workload_file << endl;
		cout << "[CONFIG] Priorities: " << priorities_file << endl;
		cout << "[CONFIG] Summary:" << solution_file << endl;
		cout << "[CONFIG] Archivo de pesos:" << pesos_file << endl << endl;
		assert(pesos.size() % 3 == 0);
		cout << "[CONFIG] Pesos cargados: " << pesos.size() / 3 << endl;
		for (unsigned int i = 0; i < pesos.size() - 1; i = i + 3) {
			cout << "(Makespan: " << pesos[i] << ", WRR: " << pesos[i+1]  << ", Energy: " << pesos[i+2] << ")" << endl;
		}
		cout << endl << endl;

		cout << cfg;

		solver.statistics();
		solver.show_state();

		cout << "Makespan: " << solver.global_best_solution().getMakespan() << endl;
		cout << "WRR: " << solver.global_best_solution().getWRR() << endl;
		cout << "Energy: " << solver.global_best_solution().getEnergy(solver.global_best_solution().getMakespan()) << endl;
		cout << "Makespan (reference): " << Solution::getMakespan_reference() << endl;
		cout << "WRR (reference): " << Solution::getWRR_reference() << endl;
		cout << "Energy (reference): " << Solution::getEnergy_reference() << endl;

		// Busco la solución "mínima" (¿?)
		// Revisar esto...
		double weight_mks = 0.0, weight_wrr = 0.0, weight_energy = 0.0;

		double min_fitness = INFINITY;
		for (unsigned int i = 0; i < pesos.size() - 1; i=i+2) {
			double aux_fitness;
			aux_fitness =
					pesos[i] * (Solution::getMakespan_reference() + solver.global_best_solution().getMakespan()) / Solution::getMakespan_reference()
					+ pesos[i+1] * (Solution::getWRR_reference() + solver.global_best_solution().getWRR()) / Solution::getWRR_reference()
					+ pesos[i+2] * (Solution::getEnergy_reference() + solver.global_best_solution().getEnergy(solver.global_best_solution().getMakespan())) / Solution::getEnergy_reference();

			if (aux_fitness < min_fitness) {
				min_fitness = aux_fitness;
				weight_mks = pesos[i];
				weight_wrr = pesos[i+1];
				weight_energy = pesos[i+2];
			}
		}

		cout << "Fitness: " << min_fitness << " (" << weight_mks << ", " << weight_wrr << ", " << weight_energy << ")" << endl;

		ofstream fexit(solution_file.data());
		if(!fexit) show_message(13);
		fexit << solver.userstatistics();
		fexit.close();

		cout << endl << endl << " :( ---------------------- THE END --------------- :) " << endl;
	}
	return(0);
}
