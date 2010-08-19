#ifndef INC_REQ_CHC
#define INC_REQ_CHC
#include "CHC.hh"
#include <math.h>

using std::cout;

skeleton CHC {

// Problem ---------------------------------------------------------------

Problem::Problem() :
	_taskCount(0), _machineCount(0), _expectedTimeToCompute(NULL),
			_tasksPriorities() {
}

// ===================================
// Serialización del problema.
// ===================================
ostream& operator<<(ostream& output, const Problem& pbm) {
	output << endl << endl << "Number of tasks: " << pbm._taskCount << endl
			<< "Number of machines: " << pbm._machineCount << endl << endl;
	return output;
}

// ===================================
// Deserialización del problema.
// ===================================
istream& operator>>(istream& input, Problem& pbm) {
	char buffer[MAX_BUFFER];

	input.getline(buffer, MAX_BUFFER, '\n');
	sscanf(buffer, "%d %d", &pbm._taskCount, &pbm._machineCount);

	cout << "[INFO] TaskCount: " << pbm._taskCount << endl;
	cout << "[INFO] MachineCount: " << pbm._machineCount << endl;

	// Inicializo las prioridades de las tareas.
	pbm._tasksPriorities.reserve(pbm._taskCount);

	int taskPriority;
	for (int taskPos = 0; taskPos < pbm._taskCount; taskPos++) {
		input.getline(buffer, MAX_BUFFER, '\n');
		sscanf(buffer, "%d", &taskPriority);

		pbm._tasksPriorities.push_back(taskPriority);
	}

	// Inicializo toda la matriz de ETC.
	pbm._expectedTimeToCompute = new float*[pbm._taskCount];
	if (pbm._expectedTimeToCompute == NULL) {
		cout << "[ERROR] no se pudo reservar memoria para la matriz" << endl;
		show_message(7);
	}

	// Inicializo cada tarea del problema.
	for (int taskPos = 0; taskPos < pbm._taskCount; taskPos++) {
		// Por cada tarea creo una lista de maquinas.
		pbm._expectedTimeToCompute[taskPos] = new float[pbm._machineCount];

		if (pbm._expectedTimeToCompute[taskPos] == NULL) {
			cout
					<< "[ERROR] no se pudo reservar memoria para las máquinas de la tarea "
					<< taskPos << endl;
			show_message(7);
		}

		// Cargo el ETC de cada tarea en cada una de las máquinas.
		for (int machinePos = 0; machinePos < pbm._machineCount; machinePos++) {
			input.getline(buffer, MAX_BUFFER, '\n');
			sscanf(buffer, "%f",
					&pbm._expectedTimeToCompute[taskPos][machinePos]);
		}
	}

	return input;
}

Problem& Problem::operator=(const Problem& pbm) {
	return *this;
}

bool Problem::operator==(const Problem& pbm) const {
	if (taskCount() != pbm.taskCount())
		return false;
	return true;
}

bool Problem::operator!=(const Problem& pbm) const {
	return !(*this == pbm);
}

Direction Problem::direction() const {
	return minimize;
}

int Problem::taskCount() const {
	return _taskCount;
}

int Problem::machineCount() const {
	return _machineCount;
}

float Problem::expectedTimeToCompute(const int& task, const int& machine) const {
	assert(task >= 0);
	assert(task < _taskCount);
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _expectedTimeToCompute[task][machine];
}

int Problem::taskPriority(const int& task) const {
	assert(task >= 0);
	assert(task < _taskCount);
	return _tasksPriorities[task];
}

Problem::~Problem() {
}

// Solution machine ------------------------------------------------------

SolutionMachine::SolutionMachine(const Problem& problem, int machineId) :
	_tasks(), _assignedTasks(), _machineId(machineId), _fitness(0.0),
			_makespan(0.0), _dirty(true), _pbm(problem) {
}

SolutionMachine::~SolutionMachine() {
}

SolutionMachine& SolutionMachine::operator=(const SolutionMachine& machine) {
	_tasks = machine._tasks;
	_assignedTasks = machine._assignedTasks;
	_machineId = machine._machineId;
	_fitness = machine._fitness;
	_makespan = machine._makespan;
	_dirty = machine._dirty;

	return *this;
}

int SolutionMachine::machineId() const {
	return _machineId;
}

void SolutionMachine::addTask(const int taskId) {
	double computeCost = _pbm.expectedTimeToCompute(taskId, _machineId);
	double priorityCost = 0.0;

	if ((_makespan > 0) && (_pbm.taskPriority(taskId) != 0)) {
		priorityCost += _makespan / _pbm.taskPriority(taskId);
	}

	_fitness = _fitness + (computeCost + priorityCost);
	_makespan = _makespan + computeCost;

	_tasks.push_back(taskId);
	_assignedTasks[taskId] = NULL;
}

void SolutionMachine::setTask(const int taskId, const int taskPos) {
	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());

	_dirty = true;

	int removedTaskId = _tasks[taskPos];
	_assignedTasks.erase(removedTaskId);
	_assignedTasks[taskId] = NULL;

	_tasks.at(taskPos) = taskId;
}

void SolutionMachine::swapTasks(const int taskPos1, const int taskPos2) {
	assert(taskPos1 >= 0);
	assert(taskPos1 < _tasks.size());

	assert(taskPos2 >= 0);
	assert(taskPos2 < _tasks.size());

	_dirty = true;

	int taskId1 = _tasks[taskPos1];
	int taskId2 = _tasks[taskPos2];

	_tasks[taskPos1] = taskId2;
	_tasks[taskPos2] = taskId1;
}

int SolutionMachine::getTask(const int taskPos) const {
	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());
	return _tasks[taskPos];
}

int SolutionMachine::countTasks() const {
	return _tasks.size();
}

bool SolutionMachine::hasTask(const int taskId) const {
	// show content:
//	if (DEBUG)
//		cout << endl << "[DEBUG] SolutionMachine::hasTask" << endl;

	return _assignedTasks.count(taskId) == 1;
}

void SolutionMachine::showMap() const {
	for (map<int, void*>::const_iterator it = _assignedTasks.begin(); it
			!= _assignedTasks.end(); it++) {
		cout << (*it).first;
		//cout << " => " << (*it).second;
		cout << endl;
	}
}

//int SolutionMachine::getTaskPos(const int taskId) const {
//	assert(hasTask(taskId));
//	return _assignedTasks.find(taskId)->second;
//}

void SolutionMachine::insertTask(const int taskId, const int taskPos) {
	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());

	_dirty = true;

	_tasks.insert(_tasks.begin() + taskPos, taskId);
	_assignedTasks[taskId] = NULL;
}

void SolutionMachine::removeTask(const int taskPos) {
	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());

	_dirty = true;

	int removedId = _tasks[taskPos];
	_assignedTasks.erase(removedId);

	_tasks.erase(_tasks.begin() + taskPos);
}

double SolutionMachine::getMakespan() {
	refresh();
	return _makespan;
}

double SolutionMachine::getFitness() {
	refresh();
	return _fitness;
}

void SolutionMachine::refresh() {
	if (_dirty) {
		double fitness = 0.0;
		double makespan = 0.0;

		for (int taskPos = 0; taskPos < countTasks(); taskPos++) {
			int taskId;
			taskId = getTask(taskPos);

			double computeCost;
			computeCost = _pbm.expectedTimeToCompute(taskId, machineId());

			double priorityCost;
			priorityCost = 0.0;

			if ((taskPos > 0) && (_pbm.taskPriority(taskId) != 0)) {
				priorityCost += makespan / _pbm.taskPriority(taskId);
			}

			makespan += computeCost;
			fitness += (computeCost + priorityCost);
		}

		_makespan = makespan;
		_fitness = fitness;
		_dirty = false;
	}
}

// Solution --------------------------------------------------------------

Solution::Solution(const Problem& pbm) :
	_pbm(pbm), _machines(), _initialized(false) {
	_machines.reserve(pbm.machineCount());

	for (int machineId = 0; machineId < pbm.machineCount(); machineId++) {
		_machines.push_back(*(new SolutionMachine(pbm, machineId)));
	}
}

const Problem& Solution::pbm() const {
	return _pbm;
}

Solution::Solution(const Solution& sol) :
	_pbm(sol.pbm()) {
	*this = sol;
}

// ===================================
// Deserialización de la solución.
// ===================================
istream& operator>>(istream& is, Solution& sol) {
	//for (int i=0;i<sol.pbm().dimension();i++)
	//	is >> sol._var[i];

	return is;
}

// ===================================
// Deserialización de la solución.
// ===================================
NetStream& operator >>(NetStream& ns, Solution& sol) {
	//for (int i=0;i<sol._var.size();i++)
	//	ns >> sol._var[i];

	return ns;
}

// ===================================
// Serialización de la solución.
// ===================================
ostream& operator<<(ostream& os, const Solution& sol) {
	os << endl;
	if (sol.isInitilized()) {
		for (int machineId = 0; machineId < sol.machines().size(); machineId++) {
			os << "> machineId: " << machineId << endl;
			//os << "  fitness: " << sol.fitnessByMachine(machineId) << endl;

			for (int i = 0; i < sol.machines()[machineId].countTasks(); i++) {
				os << "  taskPos: " << i;
				os << " taskId: " << sol.machines()[machineId].getTask(i);
				os << " ETC: " << sol.pbm().expectedTimeToCompute(
						sol.machines()[machineId].getTask(i), machineId);
				os << " priority: " << sol.pbm().taskPriority(
						sol.machines()[machineId].getTask(i));
				os << endl;
			}
		}
		//os << "* overall fitness: " << sol.fitness() << endl;
	} else {
		os << "> solution not inialized." << endl;
	}

	return os;
}

// ===================================
// Serialización de la solución.
// ===================================
NetStream& operator <<(NetStream& ns, const Solution& sol) {
	//for (int i=0;i<sol._var.size();i++)
	//	ns << sol._var[i];

	return ns;
}

Solution& Solution::operator=(const Solution &sol) {
	_machines = sol._machines;
	_initialized = sol._initialized;

	return *this;
}

bool Solution::operator==(const Solution& sol) const {
	if (sol.pbm() != _pbm)
		return false;
	return true;
}

bool Solution::operator!=(const Solution& sol) const {
	return !(*this == sol);
}

bool Solution::isInitilized() const {
	return _initialized;
}

// ===================================
// Inicializo la solución.
// ===================================
void Solution::initialize(const int solutionIndex) {
	_initialized = true;

	if (solutionIndex == 0) {
		// Inicialización usando una heurística "pesada": MIN-MIN.
		// Utilizo MIN-MIN para un único elemento de la población inicial.

		//if (DEBUG) cout << endl << "[DEBUG] Inicialización MIN-MIN" << endl;

		vector<double> machineMakespan;
		machineMakespan.reserve(_pbm.machineCount() + 1);

		for (int machineId = 0; machineId < _pbm.machineCount(); machineId++)
			machineMakespan.push_back(0.0);

		vector<bool> taskIsUnmapped;
		taskIsUnmapped.reserve(_pbm.taskCount() + 1);

		for (int taskId = 0; taskId < _pbm.taskCount(); taskId++)
			taskIsUnmapped.push_back(true);

		int unmappedTasksCount = _pbm.taskCount();

		while (unmappedTasksCount > 0) {
			double minCT;
			minCT = infinity();

			int minCTTaskId;
			minCTTaskId = -1;

			int minCTMachineId;
			minCTMachineId = -1;

			for (int taskId = 0; taskId < taskIsUnmapped.size(); taskId++) {
				if (taskIsUnmapped[taskId]) {
					for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
						if ((machineMakespan[machineId]
								+ _pbm.expectedTimeToCompute(taskId, machineId))
								< minCT) {
							minCT = machineMakespan[machineId]
									+ _pbm.expectedTimeToCompute(taskId,
											machineId);
							minCTTaskId = taskId;
							minCTMachineId = machineId;
						}
					}
				}
			}

			unmappedTasksCount--;
			taskIsUnmapped[minCTTaskId] = false;
			machineMakespan[minCTMachineId] += _pbm.expectedTimeToCompute(
					minCTTaskId, minCTMachineId);

			_machines[minCTMachineId].addTask(minCTTaskId);
		}
	} else {
		if (RANDOM_INIT > rand01()) {
			// Inicialización aleatoria

			//if (DEBUG) cout << endl << "[DEBUG] Inicialización random" << endl;

			int startTask = rand_int(0, _pbm.taskCount() - 1);
			int direction = rand_int(0, 1);
			if (direction == 0)
				direction = -1;

			int currentTask;
			for (int taskOffset = 0; taskOffset < _pbm.taskCount(); taskOffset++) {
				currentTask = startTask + (direction * taskOffset);
				if (currentTask < 0)
					currentTask = _pbm.taskCount() + currentTask;
				currentTask = currentTask % _pbm.taskCount();

				int currentMachine;
				currentMachine = rand_int(0, _pbm.machineCount() - 1);

				_machines[currentMachine].addTask(currentTask);
			}
		} else {
			// Inicialización usando una heurística no tan buena y
			// que permita obtener diferentes soluciones: MCT

			//if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT" << endl;

			vector<double> machineMakespan;
			machineMakespan.reserve(_pbm.machineCount() + 1);

			for (int machineId = 0; machineId < _pbm.machineCount(); machineId++)
				machineMakespan.push_back(0.0);

			int startTask = rand_int(0, _pbm.taskCount() - 1);
			int direction = rand_int(0, 1);
			if (direction == 0)
				direction = -1;

			int currentTask;
			for (int taskOffset = 0; taskOffset < _pbm.taskCount(); taskOffset++) {
				currentTask = startTask + (direction * taskOffset);
				if (currentTask < 0)
					currentTask = _pbm.taskCount() + currentTask;
				currentTask = currentTask % _pbm.taskCount();

				double minCT;
				minCT = infinity();

				int minCTTaskId;
				minCTTaskId = -1;

				int minCTMachineId;
				minCTMachineId = -1;

				for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
					if ((machineMakespan[machineId]
							+ _pbm.expectedTimeToCompute(currentTask, machineId))
							< minCT) {
						minCT = machineMakespan[machineId]
								+ _pbm.expectedTimeToCompute(currentTask,
										machineId);
						minCTTaskId = currentTask;
						minCTMachineId = machineId;
					}
				}

				machineMakespan[minCTMachineId] += _pbm.expectedTimeToCompute(
						minCTTaskId, minCTMachineId);

				_machines[minCTMachineId].addTask(minCTTaskId);
			}
		}
	}
}

bool Solution::validate() const {
	if (DEBUG) cout << endl << "[DEBUG] Solution::validate" << endl;
	if (true) {
		if (_machines.size() == _pbm.machineCount()) {
			int taskCount = 0;

			for (int machineId = 0; machineId < _machines.size(); machineId++) {
				for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
					taskCount++;

					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					assert(_machines[machineId].hasTask(taskId));

					if ((taskId < 0) || (taskId >= _pbm.taskCount())) {
						if (DEBUG)
							cout << endl
									<< "[DEBUG] (taskId < 0) || (taskId >= _pbm.taskCount())"
									<< endl;
						assert(false);
					}
				}
			}

			if (taskCount != _pbm.taskCount()) {
				if (DEBUG)
					cout << endl << "[DEBUG] taskCount != _pbm.taskCount()"
							<< endl;
				assert(false);
			}
		} else {
			if (DEBUG)
				cout << endl
						<< "[DEBUG] this->_machines.size() == _pbm.machineCount()"
						<< endl;
			assert(false);
		}
	} else {
		assert(false);
	}

	return true;
}

// ===================================
// Fitness de la solución.
// ===================================
double Solution::fitness() {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::fitness" << endl;
	if (!_initialized) {
		return infinity();
	}

	double fitness = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		fitness += _machines[machineId].getFitness();
	}

	return fitness;
}

int Solution::length() const {
	return _pbm.taskCount();
}

unsigned int Solution::size() const {
	return _pbm.taskCount() * sizeof(int) + _pbm.machineCount() * sizeof(int)
			+ sizeof(int);
}

int Solution::distanceTo(const Solution& solution) const {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::distanceTo start" << endl;
	int distance = 0;

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
			int taskId;
			taskId = _machines[machineId].getTask(taskPos);

			if (solution._machines[machineId].countTasks() > taskPos) {
				if (solution._machines[machineId].getTask(taskPos) == taskId) {
					// La tarea actual es ejecutada en la misma máquina y en la misma
					// posición en ambas soluciones.
				} else {
					distance++;
				}
			} else {
				distance++;
			}
		}
	}
	return distance;
}

bool Solution::findTask(const int taskId, int& foundMachineId,
		int& foundTaskPos) {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::findTask" << endl;
	foundMachineId = -1;
	foundTaskPos = -1;

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		if (_machines[machineId].hasTask(taskId)) {
			for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
				if (_machines[machineId].getTask(taskPos) == taskId) {
					foundMachineId = machineId;
					foundTaskPos = taskPos;

					return true;
				}
			}

			assert(false);
		}
	}

	assert(false);
}

void Solution::doLocalSearch() {
//	if (DEBUG)
//		cout << endl << "[DEBUG] Solution::doLocalSearch begin" << endl;

	vector<double> fitnessByMachine;

	for (int machineId = 0; machineId < this->machines().size(); machineId++) {
		fitnessByMachine.push_back(_machines[machineId].getFitness());
	}

	RouletteWheel roulette(fitnessByMachine, true);

	vector<int> maquinasSeleccionadas;
	for (int i = 0; i < PALS_MAQ; i++) {
		maquinasSeleccionadas.push_back(roulette.drawOneByIndex());
	}

	double fitnessInicial = this->fitness();
	bool solucionAceptada = false;

	for (int machinePos = 0; (machinePos < maquinasSeleccionadas.size())
			&& !solucionAceptada; machinePos++) {

		int machineId;
		machineId = maquinasSeleccionadas[machinePos];

		// PALS aleatorio para HCSP.
		//		if (DEBUG) cout << endl << "[DEBUG] Búsqueda en la máquina " << machineId << endl;

		bool finBusqMaquina;
		finBusqMaquina = false;

		for (int intento = 0; (intento < PALS_MAX_INTENTOS) && !finBusqMaquina; intento++) {
			double mejorMovimientoFitness;
			int mejorMovimientoTaskPos, mejorMovimientoDestinoTaskPos,
					mejorMovimientoDestinoMachineId;
			mejorMovimientoFitness = fitnessInicial;
			mejorMovimientoTaskPos = -1;
			mejorMovimientoDestinoTaskPos = -1;
			mejorMovimientoDestinoMachineId = -1;

			// Itero en las tareas de la máquina actual.
			int startTaskOffset, endTaskOffset;
			if (this->machines()[machineId].countTasks() > PALS_TOP_M) {
				// Si la cantidad de tareas en la máquina actual es mayor que PALS_TOP_M.
				double rand;
				rand = rand01();

				double aux;
				aux = rand * this->machines()[machineId].countTasks();

				startTaskOffset = (int) aux;
				endTaskOffset = PALS_TOP_M;
			} else {
				// Si hay menos o igual cantidad de tareas en la máquina actual que el
				// tope PALS_TOP_M, las recorro todas.
				startTaskOffset = 0;
				endTaskOffset = this->machines()[machineId].countTasks();
			}

			for (int taskOffset = startTaskOffset; taskOffset < endTaskOffset; taskOffset++) {
				int taskPos;
				taskPos = taskOffset % this->machines()[machineId].countTasks();

				int taskId;
				taskId = this->machines()[machineId].getTask(taskPos);

				// Itero en las tareas de las otras máquinas.
				int startSwapTaskOffset, countSwapTaskOffset;

				if ((this->pbm().taskCount() - 1) > PALS_TOP_T) {
					// Si la cantidad de las tareas del problema menos la tarea que estoy
					// intentando mover es mayor que PALS_TOP_T.
					double rand;
					rand = rand01();

					double aux;
					aux = rand * this->pbm().taskCount();

					startSwapTaskOffset = (int) aux;
					countSwapTaskOffset = PALS_TOP_T;
				} else {
					// Si hay menos o igual cantidad de tareas en el problema que el número
					// PALS_TOP_T las recorro todas menos la que estoy intentando mover.
					startSwapTaskOffset = 0;
					countSwapTaskOffset = this->pbm().taskCount() - 1;
				}

				double movimientoFitness;
				movimientoFitness = 0.0;

				for (int swapTaskOffset = startSwapTaskOffset; countSwapTaskOffset
						> 0; swapTaskOffset++) {
					assert(swapTaskOffset < (2*this->pbm().taskCount()));

					int swapTaskId;
					swapTaskId = swapTaskOffset % this->pbm().taskCount();

					if (swapTaskId != taskId) {
						countSwapTaskOffset--;

						int swapMachineId, swapTaskPos;
						assert(this->findTask(swapTaskId, swapMachineId, swapTaskPos));

						//==============================================================
						//TODO: Optimizar!!!
						//==============================================================
						this->swapTasks(machineId, taskPos, swapMachineId, swapTaskPos);
						movimientoFitness = this->fitness();
						this->swapTasks(swapMachineId, swapTaskPos, machineId, taskPos);
						//==============================================================

						if (movimientoFitness < mejorMovimientoFitness) {
							mejorMovimientoFitness = movimientoFitness;
							mejorMovimientoTaskPos = taskPos;
							mejorMovimientoDestinoMachineId = swapMachineId;
							mejorMovimientoDestinoTaskPos = swapTaskPos;
						}
					}
				}
			}

			if (mejorMovimientoFitness < fitnessInicial) {
				this->swapTasks(machineId, mejorMovimientoTaskPos,
						mejorMovimientoDestinoMachineId,
						mejorMovimientoDestinoTaskPos);
				finBusqMaquina = true;
			}
		}

		solucionAceptada = (this->fitness() / fitnessInicial)
				> PALS_UMBRAL_MEJORA;
	}
}

void Solution::mutate() {
	//	if (DEBUG)	cout << endl << "[DEBUG] Solution::mutate" << endl;

	int machineCount = _machines.size();
	vector<double> fitnessByMachine;

	for (int machineId = 0; machineId < machineCount; machineId++) {
		fitnessByMachine.push_back(_machines[machineId].getFitness());
	}

	RouletteWheel roulette(fitnessByMachine, true);

	for (int i = 0; i < MUT_MAQ; i++) {
		int machineId;
		{
			int machineTasksCount;
			machineTasksCount = 0;

			// Sorteo una máquina para mutar.
			while (machineTasksCount == 0) {
				machineId = roulette.drawOneByIndex();
				machineTasksCount = _machines[machineId].countTasks();
			}
		}

		bool modificado;
		modificado = false;

		while (!modificado) {
			// Con una probabilidad de 0.5 a cada máquina sin tareas se le asigna la tarea que
			// mejor puede ejecutar de la máquina actual.
			for (int auxMachineId = 0; auxMachineId < _machines.size(); auxMachineId++) {
				if ((auxMachineId != machineId) && (rand01() >= 0.5)
						&& (_machines[auxMachineId].countTasks() == 0)
						&& (_machines[machineId].countTasks() > 0)) {

					int bestTaskPosForMachine;
					bestTaskPosForMachine
							= getMinDestinationCostTaskPosByMachine(machineId,
									auxMachineId);

					if (bestTaskPosForMachine >= 0) {
						int bestTaskIdForMachine;
						bestTaskIdForMachine = _machines[machineId].getTask(
								bestTaskPosForMachine);

						_machines[machineId].removeTask(bestTaskPosForMachine);
						_machines[auxMachineId].addTask(bestTaskIdForMachine);

						modificado = true;
					}
				}
			}

			if ((rand01() >= 0.5) && (_machines[machineId].countTasks() > 0)) {
				// Se selecciona una tarea T según rueda de ruleta por su COSTO y se
				// intercambia con la tarea de menor costo de la máquina con menor makespan.

				vector<double> costsByTaskPos;
				costsByTaskPos.clear();

				for (int taskPos = 0; taskPos
						< _machines[machineId].countTasks(); taskPos++) {
					// Inicializo el vector de costos de las tareas de la máquina actual
					// para sortear una tarea.
					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					int taskCost;
					taskCost = _pbm.expectedTimeToCompute(taskId, machineId);

					costsByTaskPos.push_back(taskCost);
				}

				// Sorteo una tarea.
				RouletteWheel roulette(costsByTaskPos, true);
				int taskPos;
				taskPos = roulette.drawOneByIndex();

				// Obtengo la máquina que aporta un menor costo al total de la solución.
				int minCostMachineId;
				minCostMachineId = getMinCostMachineId();

				if (_machines[minCostMachineId].countTasks() > 0) {
					// Si la máquina destino tiene al menos una tarea, obtengo la tarea
					// con menor costo si se ejecuta en la máquina destino.
					int minCostTaskPosOnMachine;
					minCostTaskPosOnMachine = getMinCostTaskPosByMachine(
							minCostMachineId);

					// Hago un swap entre las tareas de las máquinas.
					swapTasks(machineId, taskPos, minCostMachineId,
							minCostTaskPosOnMachine);
				} else {
					// La máquina destino no tiene tareas. Muevo la tarea sorteada en la
					// máquina origen a la destino.

					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					_machines[machineId].removeTask(taskPos);
					_machines[minCostMachineId].addTask(taskId);
				}

				modificado = true;
			}

			if ((rand01() >= 0.5) && (_machines[machineId].countTasks() > 0)) {
				// Se selecciona una tarea T según rueda de ruleta por su COSTO y se
				// intercambia con la tarea de la máquina con menor makespan que puede ejecutarse
				// más eficientemente en la máquina actual.

				vector<double> costsByTaskPos;
				costsByTaskPos.clear();

				for (int taskPos = 0; taskPos
						< _machines[machineId].countTasks(); taskPos++) {
					// Inicializo el vector de costos de las tareas de la máquina actual
					// para sortear una tarea.
					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					int taskCost;
					taskCost = _pbm.expectedTimeToCompute(taskId, machineId);

					costsByTaskPos.push_back(taskCost);
				}

				// Sorteo una tarea.
				RouletteWheel roulette(costsByTaskPos, true);
				int taskPos;
				taskPos = roulette.drawOneByIndex();

				// Obtengo la máquina que aporta un menor costo al total de la solución.
				int minCostMachineId;
				minCostMachineId = getMinCostMachineId();

				if (_machines[minCostMachineId].countTasks() > 0) {
					// Si la máquina destino tiene al menos una tarea, obtengo la tarea
					// con menor costo si se ejecuta en la máquina origen.

					int minCostTaskPosOnMachine;
					minCostTaskPosOnMachine
							= getMinDestinationCostTaskPosByMachine(
									minCostMachineId, machineId);

					// Hago un swap entre las tareas de las máquinas.
					swapTasks(machineId, taskPos, minCostMachineId,
							minCostTaskPosOnMachine);
				} else {
					// La máquina destino no tiene tareas. Muevo la tarea sorteada en la
					// máquina origen a la destino.
					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					_machines[machineId].removeTask(taskPos);
					_machines[minCostMachineId].addTask(taskId);
				}

				modificado = true;
			}

			if ((rand01() >= 0.5) && (_machines[machineId].countTasks() > 0)) {
				// Se selecciona una tarea T según rueda de ruleta por el inverso de su
				// función de PRIORIDAD y se coloca en el primer lugar de la cola de ejecución
				// de la máquina.

				vector<double> priorityByTaskPos;
				priorityByTaskPos.clear();

				for (int taskPos = 0; taskPos
						< _machines[machineId].countTasks(); taskPos++) {
					// Inicializo el vector de prioridades de las tareas de
					// la máquina actual para sortear una tarea.
					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					int taskPriority;
					taskPriority = _pbm.taskPriority(taskId);

					priorityByTaskPos.push_back(taskPriority);
				}

				// Sorteo una tarea.
				RouletteWheel roulette(priorityByTaskPos, false);
				int taskPos;
				taskPos = roulette.drawOneByIndex();

				if (taskPos > 0) {
					swapTasks(machineId, taskPos, machineId, 0);
				}

				modificado = true;
			}
		}
	}
}

void Solution::swapTasks(int machineId1, int taskPos1, int machineId2,
		int taskPos2) {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks start" << endl;

	if (machineId1 != machineId2) {
		int taskId1 = machines()[machineId1].getTask(taskPos1);
		int taskId2 = machines()[machineId2].getTask(taskPos2);

		if (taskId1 != taskId2) {
			_machines[machineId1].setTask(taskId2, taskPos1);
			_machines[machineId2].setTask(taskId1, taskPos2);
		}
	} else {
		_machines[machineId1].swapTasks(taskPos1, taskPos2);
	}
}

void Solution::swapTasks(Solution& solution, const int taskId) {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks" << endl;

	Solution& sol1 = *this;
	Solution& sol2 = solution;
	int machine1, machine2, taskPos1, taskPos2;

	assert(sol1.findTask(taskId, machine1, taskPos1));
	assert(sol2.findTask(taskId, machine2, taskPos2));

	// Modifico la solución 1.
	// Borro la tarea de la ubicación original.
	sol1._machines[machine1].removeTask(taskPos1);

	// Inserto la tarea en la nueva ubicación.
	if (taskPos2 < sol1._machines[machine2].countTasks()) {
		sol1._machines[machine2].insertTask(taskId, taskPos2);
	} else {
		sol1._machines[machine2].addTask(taskId);
	}

	// Modifico la solución 2.
	// Borro la tarea de la ubicación original.
	sol2._machines[machine2].removeTask(taskPos2);

	// Inserto la tarea en la nueva ubicación.
	if (taskPos1 < sol2._machines[machine1].countTasks()) {
		sol2._machines[machine1].insertTask(taskId, taskPos1);
	} else {
		sol2._machines[machine1].addTask(taskId);
	}
}

bool Solution::equalTasks(Solution& solution, const int taskId) {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks" << endl;
	int machine1, machine2, taskPos1, taskPos2;

	assert(findTask(taskId, machine1, taskPos1));
	assert(solution.findTask(taskId, machine2, taskPos2));

	return (machine1 == machine2) && (taskPos1 == taskPos2);
}

char *Solution::to_String() const {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::to_String" << endl;
	int machineSeparator = -1;
	int endMark = -2;

	int rawPos = 0;
	int *raw = new int[_pbm.taskCount() + _pbm.machineCount() + 1];

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
			int taskId;
			taskId = _machines[machineId].getTask(taskPos);

			raw[rawPos] = taskId;
			rawPos += 1;
		}
		raw[rawPos] = machineSeparator;
		rawPos += 1;
	}
	raw[rawPos] = endMark;
	rawPos += 1;

	return (char*) raw;
}

void Solution::to_Solution(char *_string_) {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::to_Solution" << endl;
	_initialized = false;

	int *raw = (int*) _string_;
	int machineSeparator = -1;
	int endMark = -2;
	bool endFound = false;
	int currentMachine = 0;

	for (int pos = 0; pos < (_pbm.taskCount() + _pbm.machineCount() + 1)
			&& !endFound; pos++) {

		int currentValue;
		currentValue = raw[pos];

		if (currentValue == endMark)
			endFound = true;
		else if (currentValue == machineSeparator)
			currentMachine++;
		else {
			_machines[currentMachine].addTask(currentValue);
			_initialized = true;
		}
	}
}

const vector<struct SolutionMachine>& Solution::machines() const {
	return _machines;
}

int Solution::getBestFitnessMachineId() {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::getBestFitnessMachineId" << endl;

	int bestFitnessMachineId = 0;
	double bestFitnessMachineValue = _machines[0].getFitness();

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineFitness;
		currentMachineFitness = _machines[machineId].getFitness();

		if ((bestFitnessMachineValue > currentMachineFitness)
				&& (_pbm.direction() == minimize)) {

			bestFitnessMachineValue = currentMachineFitness;
			bestFitnessMachineId = machineId;

		} else if ((bestFitnessMachineValue < currentMachineFitness)
				&& (_pbm.direction() == maximize)) {

			bestFitnessMachineValue = currentMachineFitness;
			bestFitnessMachineId = machineId;
		}
	}

	return bestFitnessMachineId;
}

int Solution::getMinCostMachineId() {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostMachineId" << endl;
	int minCostMachineId = 0;
	double minCostMachineValue = _machines[0].getMakespan();

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineCost;
		currentMachineCost = _machines[machineId].getMakespan();

		if (minCostMachineValue > currentMachineCost) {
			minCostMachineValue = currentMachineCost;
			minCostMachineId = machineId;
		}
	}

	return minCostMachineId;
}

int Solution::getHighestPriorityTaskPosByMachine(int machineId) const {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::getHighestPriorityTaskPosByMachine" << endl;

	if (machines()[machineId].countTasks() > 0) {
		int highestPriorityTaskPos = 0;
		int highestPriorityTaskValue = _pbm.taskPriority(
				machines()[machineId].getTask(0));

		for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
			int currentTaskPriority;
			currentTaskPriority = _pbm.taskPriority(
					machines()[machineId].getTask(taskPos));

			if (highestPriorityTaskValue > currentTaskPriority) {
				highestPriorityTaskValue = currentTaskPriority;
				highestPriorityTaskPos = taskPos;
			}
		}

		return highestPriorityTaskPos;
	} else {
		return -1;
	}
}

int Solution::getMinCostTaskPosByMachine(int machineId) const {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostTaskPosByMachine" << endl;
	assert(machines()[machineId].countTasks() > 0);

	int minCostTaskPos = 0;
	int minCostTaskValue = _pbm.expectedTimeToCompute(
			machines()[machineId].getTask(0), machineId);

	for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
		int currentTaskCost;
		currentTaskCost = _pbm.expectedTimeToCompute(
				machines()[machineId].getTask(taskPos), machineId);

		if (minCostTaskValue > currentTaskCost) {
			minCostTaskValue = currentTaskCost;
			minCostTaskPos = taskPos;
		}
	}

	assert(machines()[machineId].countTasks() > minCostTaskPos);
	assert(minCostTaskPos >= 0);

	return minCostTaskPos;
}

int Solution::getMinDestinationCostTaskPosByMachine(int machineId,
		int destinationMachineId) const {

	// if (DEBUG) cout << endl << "[DEBUG] Solution::getMinDestinationCostTaskPosByMachine" << endl;
	if (machines()[machineId].countTasks() > 0) {
		int minCostTaskPos = 0;
		int minCostTaskValue = _pbm.expectedTimeToCompute(
				machines()[machineId].getTask(0), destinationMachineId);

		for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
			int currentTaskCost;
			currentTaskCost = _pbm.expectedTimeToCompute(
					machines()[machineId].getTask(taskPos),
					destinationMachineId);

			if (minCostTaskValue > currentTaskCost) {
				minCostTaskValue = currentTaskCost;
				minCostTaskPos = taskPos;
			}
		}

		return minCostTaskPos;
	} else {
		return -1;
	}
}

Solution::~Solution() {
}

// UserStatistics -------------------------------------------------------

UserStatistics::UserStatistics() {
}

ostream& operator<<(ostream& os, const UserStatistics& userstat) {
	os << "\n---------------------------------------------------------------"
			<< endl;
	os << "                   STATISTICS OF TRIALS                   	 "
			<< endl;
	os << "------------------------------------------------------------------"
			<< endl;

	for (int i = 0; i < userstat.result_trials.size(); i++) {
		os << endl << userstat.result_trials[i].trial << "\t"
				<< userstat.result_trials[i].best_cost_trial << "\t\t"
				<< userstat.result_trials[i].worst_cost_trial << "\t\t\t"
				<< userstat.result_trials[i].nb_evaluation_best_found_trial
				<< "\t\t\t"
				<< userstat.result_trials[i].nb_iteration_best_found_trial
				<< "\t\t\t" << userstat.result_trials[i].time_best_found_trial
				<< "\t\t" << userstat.result_trials[i].time_spent_trial;
	}
	os << endl
			<< "------------------------------------------------------------------"
			<< endl;
	return os;
}

UserStatistics& UserStatistics::operator=(const UserStatistics& userstats) {
	result_trials = userstats.result_trials;
	return (*this);
}

void UserStatistics::update(const Solver& solver) {
	if ((solver.pid() != 0) || (solver.end_trial() != true)
			|| ((solver.current_iteration()
					!= solver.setup().nb_evolution_steps()) && !terminateQ(
					solver.pbm(), solver, solver.setup())))
		return;

	struct user_stat *new_stat;

	if ((new_stat = (struct user_stat *) malloc(sizeof(struct user_stat)))
			== NULL)
		show_message(7);
	new_stat->trial = solver.current_trial();
	new_stat->nb_evaluation_best_found_trial
			= solver.evaluations_best_found_in_trial();
	new_stat->nb_iteration_best_found_trial
			= solver.iteration_best_found_in_trial();
	new_stat->worst_cost_trial = solver.worst_cost_trial();
	new_stat->best_cost_trial = solver.best_cost_trial();
	new_stat->time_best_found_trial = solver.time_best_found_trial();
	new_stat->time_spent_trial = solver.time_spent_trial();

	result_trials.append(*new_stat);
}

void UserStatistics::clear() {
	result_trials.remove();
}

UserStatistics::~UserStatistics() {
	result_trials.remove();
}

//  User_Operator:Intra_operator ---------------------------------------------------------

User_Operator::User_Operator(const unsigned int _number_op) :
	Intra_Operator(_number_op) {
}

void User_Operator::execute(Rarray<Solution*>& sols) const {
}

void User_Operator::setup(char line[MAX_BUFFER]) {
}

Intra_Operator *User_Operator::create(const unsigned int _number_op) {
	return new User_Operator(_number_op);
}

ostream& operator<<(ostream& os, const User_Operator& u_op) {
	os << "User Operator.";
	return os;
}

void User_Operator::RefreshState(const StateCenter& _sc) const {
}

void User_Operator::UpdateFromState(const StateCenter& _sc) {
}

User_Operator::~User_Operator() {
}

// StopCondition_1 -------------------------------------------------------------------------------------

StopCondition_1::StopCondition_1() :
	StopCondition() {
}

bool StopCondition_1::EvaluateCondition(const Problem& pbm,
		const Solver& solver, const SetUpParams& setup) {
	//return ((int)solver.best_cost_trial() == pbm.dimension());
	return false;
}

StopCondition_1::~StopCondition_1() {
}

//------------------------------------------------------------------------
// Specific methods ------------------------------------------------------
//------------------------------------------------------------------------

bool terminateQ(const Problem& pbm, const Solver& solver,
		const SetUpParams& setup) {
	/*StopCondition_1 stop;
	 return stop.EvaluateCondition(pbm,solver,setup);*/

	return false;
}
}
#endif

