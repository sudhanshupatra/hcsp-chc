#ifndef INC_REQ_CHC
#define INC_REQ_CHC
#include "CHC.hh"
#include <math.h>

using std::cout;

skeleton CHC {

// Problem ---------------------------------------------------------------

Problem::Problem() :
	_taskCount(0), _machineCount(0), _expectedTimeToCompute(NULL), _awrr_weight(
			1.0), _makespan_weight(1.0), _tasksPriorities() {
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

int Problem::getBestTaskIdForMachine(int machineId) const {
	//TODO: Optimizar!
	int minTaskId = 0;
	for (int i = 1; i < taskCount(); i++) {
		if (expectedTimeToCompute(i, machineId) < expectedTimeToCompute(
				minTaskId, machineId)) {
			minTaskId = i;
		}
	}
	return minTaskId;
}

int Problem::getBestMachineForTaskId(int taskId) const {
	//TODO: Optimizar!
	int minMachineId = 0;
	for (int i = 1; i < machineCount(); i++) {
		if (expectedTimeToCompute(taskId, i) < expectedTimeToCompute(taskId,
				minMachineId)) {
			minMachineId = i;
		}
	}
	return minMachineId;
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

float Problem::getAWRRWeight() const {
	return _awrr_weight;
}

void Problem::setAWRRWeight(const float weight) {
	_awrr_weight = weight;
}

float Problem::getMakespanWeight() const {
	return _makespan_weight;
}

void Problem::setMakespanWeight(const float weight) {
	_makespan_weight = weight;
}

Problem::~Problem() {
}

// Solution machine ------------------------------------------------------

SolutionMachine::SolutionMachine(const Problem& problem, int machineId) :
	_tasks(), _assignedTasks(), _machineId(machineId),
			_makespan(0.0), _awrr(0.0), _dirty(true), _pbm(problem) {

	_tasks.reserve(problem.taskCount());
}

SolutionMachine::~SolutionMachine() {
}

SolutionMachine& SolutionMachine::operator=(const SolutionMachine& machine) {
	_machineId = machine._machineId;

	_makespan = 0.0;
	_dirty = true;

	_tasks.clear();
	_tasks.reserve(machine._tasks.size());

	_assignedTasks.clear();

	for (int taskPos = 0; taskPos < machine._tasks.size(); taskPos++) {
		int taskId;
		taskId = machine.getTask(taskPos);

		_tasks.push_back(taskId);
		_assignedTasks[taskId] = NULL;
	}

	return *this;
}

int SolutionMachine::machineId() const {
	return _machineId;
}

void SolutionMachine::addTask(const int taskId) {
	_dirty = true;

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
	return _assignedTasks.count(taskId) == 1;
}

void SolutionMachine::showMap() const {
	for (map<int, void*>::const_iterator it = _assignedTasks.begin(); it
			!= _assignedTasks.end(); it++) {
		cout << (*it).first;
		cout << endl;
	}
}

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

void SolutionMachine::emptyTasks() {
	_dirty = true;
	_assignedTasks.clear();
	_tasks.clear();
}

double SolutionMachine::getMakespan() {
	refresh();
	return _makespan;
}

double SolutionMachine::getAccumulatedWeightedResponseRatio() {
	refresh();
	return _awrr;
}

void SolutionMachine::refresh() {
	//_dirty = true;
	if (_dirty) {
		double partial_makespan = 0.0;
		double partial_awrr = 0.0;

		for (int taskPos = 0; taskPos < countTasks(); taskPos++) {
			int taskId;
			taskId = getTask(taskPos);

			double compute_cost;
			compute_cost = _pbm.expectedTimeToCompute(taskId, machineId());
			partial_makespan += compute_cost;

			double rr;
			rr = (partial_makespan + compute_cost) / compute_cost;

			double priority_cost;
			priority_cost = (_pbm.taskPriority(taskId) * rr);
			partial_awrr += priority_cost;
		}

		_awrr = partial_awrr;
		_makespan = partial_makespan;

		_dirty = false;
	}
}

// Solution --------------------------------------------------------------

double Solution::_awrr_reference=1.0;
double Solution::_makespan_reference=1.0;

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

	assert(false);

	return is;
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
	//	if (DEBUG) cout << endl << "[DEBUG] operator <<(NetStream& ns, Solution& sol)" << endl;

	int currentTask = 0;
	int currentItem = 0;

	int machineSeparator = -1;

	assert(sol.validate());

	for (int machineId = 0; machineId < sol.machines().size(); machineId++) {
		for (int taskPos = 0; taskPos < sol.machines()[machineId].countTasks(); taskPos++) {
			int taskId;
			taskId = sol.machines()[machineId].getTask(taskPos);

			assert(taskId >= 0);
			assert(taskId < sol.pbm().taskCount());

			ns << taskId;
			//			if (DEBUG) cout << "[DEBUG] operator<< " << taskId << endl;

			currentTask++;
			currentItem++;
		}
		ns << machineSeparator;
		//		if (DEBUG) cout << "[DEBUG] operator<< " << machineSeparator << endl;

		currentItem++;
	}

	//	if (DEBUG) cout << "[DEBUG] operator<< En total se mandaron " << currentItem << " integers." << endl;

	assert(currentTask == sol.pbm().taskCount());
	assert(currentItem == sol.pbm().taskCount() + sol.pbm().machineCount());

	return ns;
}

// ===================================
// Deserialización de la solución.
// ===================================
NetStream& operator >>(NetStream& ns, Solution& sol) {
	//	if (DEBUG) cout << endl << "[DEBUG] operator >>(NetStream& ns, Solution& sol)" << endl;

	int machineSeparator = -1;

	int currentTask = 0;
	int currentMachine = 0;

	//	if (DEBUG) cout << "[DEBUG] operator>> voy a leer "
	//			<< sol.pbm().taskCount() + sol.pbm().machineCount() <<
	//			" integers." << endl;

	//	if (DEBUG) cout << "[DEBUG] operator>> cantidad actual de tasks " << sol.countTasks() << " las voy a vaciar." << endl;
	sol.emptyTasks();

	for (int pos = 0; pos < sol.pbm().taskCount() + sol.pbm().machineCount(); pos++) {
		int currentValue;
		ns >> currentValue;

		//		if (DEBUG) cout << "[DEBUG] operator>> currentMachine:" << currentMachine
		//				<< " currentTask:" << currentTask << " currentValue:" << currentValue << endl;

		//		if (DEBUG) cout << "[DEBUG] operator>> " << currentValue << endl;

		if (currentValue == machineSeparator) {
			assert(currentMachine < sol.pbm().machineCount());
			currentMachine++;
		} else {
			assert(currentValue >= 0);
			assert(currentValue < sol.pbm().taskCount());
			assert(currentMachine >= 0);
			assert(currentMachine < sol.pbm().machineCount());

			sol.addTask(currentMachine, currentValue);
			currentTask++;
		}
	}

	//	if (DEBUG) cout << "[DEBUG] operator >> sol.pbm().taskCount() = " << sol.pbm().taskCount() << endl;
	//	if (DEBUG) cout << "[DEBUG] operator >> currentTask = " << currentTask << endl;

	assert(sol.machines().size() == sol.pbm().machineCount());
	assert(currentTask == sol.pbm().taskCount());

	sol.markAsInitialized();
	sol.validate();

	return ns;
}

Solution& Solution::operator=(const Solution &sol) {
	for (int machineId = 0; machineId < sol._machines.size(); machineId++) {
		_machines[machineId] = sol._machines[machineId];
	}

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

void Solution::emptyTasks() {
	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		_machines[machineId].emptyTasks();
	}
}

int Solution::countTasks() {
	int count = 0;
	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		count += _machines[machineId].countTasks();
	}
	return count;
}

// ===================================
// Inicializo la solución.
// ===================================
void Solution::initializeStaticMCT() {
	//if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT Estática" << endl;

	initializeMCT(0, 1);
}

void Solution::initializeRandomMCT() {
	//if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT Aleatoria" << endl;

	int startTask = rand_int(0, _pbm.taskCount() - 1);
	int direction = rand_int(0, 1);
	if (direction == 0)
		direction = -1;

	initializeMCT(startTask, direction);
}

void Solution::initializeMCT(int startTask, int direction) {
	//	if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT" << endl;

	vector<double> machineMakespan;
	machineMakespan.reserve(_pbm.machineCount() + 1);

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++)
		machineMakespan.push_back(0.0);

	int currentTask;
	for (int taskOffset = 0; taskOffset < _pbm.taskCount(); taskOffset++) {
		currentTask = startTask + (direction * taskOffset);
		if (currentTask < 0)
			currentTask = _pbm.taskCount() + currentTask;
		currentTask = currentTask % _pbm.taskCount();

		double minFitness;
		minFitness = infinity();

		int minFitnessMachineId;
		minFitnessMachineId = -1;

		for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
			double makespan;
			makespan = (machineMakespan[machineId] + _pbm.expectedTimeToCompute(
					currentTask, machineId));
			double awrr;
			awrr = (machineMakespan[machineId] + _pbm.expectedTimeToCompute(
					currentTask, machineId)) * (_pbm.taskPriority(currentTask) / 5);
			double auxFitness;
			auxFitness = (_pbm.getAWRRWeight()) * awrr + (_pbm.getMakespanWeight() * makespan);

			if (auxFitness < minFitness) {
				minFitness = auxFitness;
				minFitnessMachineId = machineId;
			}
		}

		machineMakespan[minFitnessMachineId] += _pbm.expectedTimeToCompute(
				currentTask, minFitnessMachineId);

		_machines[minFitnessMachineId].addTask(currentTask);
	}
}

void Solution::initializeMinMin() {
	//	if (DEBUG) cout << endl << "[DEBUG] Inicialización MIN-MIN" << endl;

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

		double estimatedSize = _pbm.taskCount() / _pbm.machineCount();

		for (int taskId = 0; taskId < taskIsUnmapped.size(); taskId++) {
			double priorityCoef = _pbm.taskPriority(taskId) * estimatedSize;

			if (taskIsUnmapped[taskId]) {
				for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
					if ((machineMakespan[machineId]
							+ _pbm.expectedTimeToCompute(taskId, machineId))
							< minCT) {
						minCT = machineMakespan[machineId]
								+ _pbm.expectedTimeToCompute(taskId, machineId);
						minCT = minCT + (machineMakespan[machineId]
								/ priorityCoef);
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
}

void Solution::initializeRandom() {
	//	if (DEBUG) cout << endl << "[DEBUG] Inicialización random" << endl;

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
}

void Solution::initializeSufferage() {
	//	if (DEBUG) cout << endl << "[DEBUG] Inicialización Sufferage" << endl;
	int unmappedTasks = _pbm.taskCount();

	vector<bool> taskIsUnmapped;
	taskIsUnmapped.reserve(_pbm.taskCount());
	for (int i = 0; i < _pbm.taskCount(); i++) {
		taskIsUnmapped[i] = true;
	}

	vector<double> machinesMakespan;
	machinesMakespan.reserve(_pbm.machineCount());
	for (int i = 0; i < _pbm.machineCount(); i++) {
		machinesMakespan[i] = 0.0;
	}

	int maxSufferageTaskId;
	int maxSufferageMachineId;
	double maxSufferageValue;

	while (unmappedTasks > 0) {
		maxSufferageTaskId = -1;
		maxSufferageMachineId = -1;
		maxSufferageValue = 0.0;

		double estimatedSize = _pbm.taskCount() / _pbm.machineCount();

		for (int taskId = 0; taskId < _pbm.taskCount(); taskId++) {
			double priorityCoef = _pbm.taskPriority(taskId) * estimatedSize;

			if (taskIsUnmapped[taskId]) {
				int minMakespanMachineId;
				minMakespanMachineId = -1;

				int secondMinMakespanMachineId;
				secondMinMakespanMachineId = -1;

				assert(_pbm.machineCount() > 2);

				for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
					double currentMakespan;
					double minMakespan;
					double secondMinMakespan;

					currentMakespan = machinesMakespan[machineId]
							+ _pbm.expectedTimeToCompute(taskId, machineId);
					currentMakespan = currentMakespan
							+ (machinesMakespan[machineId] / priorityCoef);

					if (minMakespanMachineId != -1) {
						minMakespan = machinesMakespan[minMakespanMachineId]
								+ _pbm.expectedTimeToCompute(taskId,
										minMakespanMachineId);
						minMakespan = minMakespan
								+ (machinesMakespan[minMakespanMachineId]
										/ priorityCoef);
					}
					if (secondMinMakespanMachineId != -1) {
						secondMinMakespan
								= machinesMakespan[secondMinMakespanMachineId]
										+ _pbm.expectedTimeToCompute(taskId,
												secondMinMakespanMachineId);
						secondMinMakespan = secondMinMakespan
								+ (machinesMakespan[secondMinMakespanMachineId]
										/ priorityCoef);
					}
					if (minMakespanMachineId == -1) {
						minMakespanMachineId = machineId;
					} else if (minMakespan > currentMakespan) {
						secondMinMakespanMachineId = minMakespanMachineId;
						minMakespanMachineId = machineId;
					} else if (secondMinMakespanMachineId == -1) {
						secondMinMakespanMachineId = machineId;
					} else if (secondMinMakespan > currentMakespan) {
						secondMinMakespanMachineId = machineId;
					}
				}

				double minMakespan;
				double secondMinMakespan;
				double sufferageValue;

				minMakespan = machinesMakespan[minMakespanMachineId]
						+ _pbm.expectedTimeToCompute(taskId,
								minMakespanMachineId);
				minMakespan = minMakespan
						+ (machinesMakespan[minMakespanMachineId]
								/ priorityCoef);

				secondMinMakespan
						= machinesMakespan[secondMinMakespanMachineId]
								+ _pbm.expectedTimeToCompute(taskId,
										secondMinMakespanMachineId);
				secondMinMakespan = secondMinMakespan
						+ (machinesMakespan[secondMinMakespanMachineId]
								/ priorityCoef);

				sufferageValue = secondMinMakespan - minMakespan;

				if ((maxSufferageMachineId == -1) || (maxSufferageTaskId == -1)) {
					maxSufferageTaskId = taskId;
					maxSufferageMachineId = minMakespanMachineId;
					maxSufferageValue = sufferageValue;
				} else if (maxSufferageValue < sufferageValue) {
					maxSufferageTaskId = taskId;
					maxSufferageMachineId = minMakespanMachineId;
					maxSufferageValue = sufferageValue;
				}
			}
		}

		assert((maxSufferageTaskId >= 0) && (maxSufferageMachineId >= 0));

		machinesMakespan[maxSufferageMachineId]
				= machinesMakespan[maxSufferageMachineId]
						+ _pbm.expectedTimeToCompute(maxSufferageTaskId,
								maxSufferageMachineId);
		+(machinesMakespan[maxSufferageMachineId] / (_pbm.taskPriority(
				maxSufferageTaskId) * estimatedSize));

		_machines[maxSufferageMachineId].addTask(maxSufferageTaskId);

		taskIsUnmapped[maxSufferageTaskId] = false;
		unmappedTasks--;
	}
}

void Solution::markAsInitialized() {
	_initialized = true;
}

void Solution::initialize(const int solutionIndex) {
	markAsInitialized();

	if (solutionIndex == 0) {
		// Inicialización usando una versión determinista de la heurística MCT.
		// La solución 0 (cero) es idéntica en todos las instancias de ejecución.
		// Utilizo la solución 0 (cero) como referencia de mejora del algoritmo.

		initializeStaticMCT();

		//NOTE: NO EVALUAR FITNESS ANTES DE ESTA ASIGNACIÓN!!!
		Solution::_awrr_reference = accumulatedWeightedResponseRatio();
		Solution::_makespan_reference = makespan();

		cout << endl << "MCT reference fitness: " << fitness() << endl;
	} else if (solutionIndex == 1) {
		// Inicialización usando una heurística "pesada": MIN-MIN.
		// Utilizo MIN-MIN para un único elemento de la población inicial.

		initializeMinMin();
		cout << endl << "Min-Min fitness: " << fitness() << endl;
	} else if (solutionIndex == 2) {
		// Inicialización usando otra heurística "pesada" diferente: Sufferage.
		// Utilizo Sufferage para un único elemento de la población inicial.

		initializeSufferage();
		cout << endl << "Sufferage fitness: " << fitness() << endl;
	} else {
		if (RANDOM_INIT > rand01()) {
			// Inicialización aleatoria

			initializeRandom();
			cout << endl << "Random fitness: " << fitness() << endl;
		} else {
			// Inicialización usando una heurística no tan buena y
			// que permita obtener diferentes soluciones: MCT

			initializeRandomMCT();
			cout << endl << "Random MCT fitness: " << fitness() << endl;
		}
	}
}

bool Solution::validate() const {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::validate" << endl;
	if (true) {
		for (int t = 0; t < _pbm.taskCount(); t++) {
			int machineId, taskPos;
			//assert(findTask(t, machineId, taskPos));
		}

		if (_machines.size() == _pbm.machineCount()) {
			int taskCount = 0;

			for (int machineId = 0; machineId < _machines.size(); machineId++) {
				for (int taskPos = 0; taskPos
						< _machines[machineId].countTasks(); taskPos++) {
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
				cout << endl << "[DEBUG] taskCount:" << taskCount
						<< " _pbm.taskCount():" << _pbm.taskCount() << endl;
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

void Solution::showCustomStatics() {
	// Tiempo de respuesta promedio
	cout << endl << "[= Statics =====================]" << endl;
	cout << " * Avg. response ratio: " << accumulatedWeightedResponseRatio() / _pbm.taskCount() << endl;

	// Peor tiempo de respuesta

	// Tiempo de respuesta promedio por prioridad
	int total_count = 0;
	int total_rr_sum = 0.0;

	cout << " * Avg. response ratio by priority." << endl;
	for (int priority = 0; priority <= 10; priority++) {
		cout << "   priority = " << priority;

		int count = 0;
		double rr_sum = 0.0;

		for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
			double partial_cost;
			partial_cost = 0.0;

			for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
				int taskId;
				taskId = _machines[machineId].getTask(taskPos);

				if (_pbm.taskPriority(taskId) == priority) {
					count++;

					rr_sum += (partial_cost + _pbm.expectedTimeToCompute(taskId, machineId))
								/ _pbm.expectedTimeToCompute(taskId, machineId);
				}

				partial_cost += _pbm.expectedTimeToCompute(taskId, machineId);
			}
		}

		total_count += count;
		total_rr_sum += rr_sum;

		if (count > 0) {
			cout << " (" << count << " tasks) ";
			cout << " >> avg. rr = " << rr_sum / count << endl;
			rr_sum = 0.0;
			count = 0;
		} else {
			cout << " N/A" << endl;
		}
	}

	cout << endl << "Total tasks: " << total_count << endl;
	cout << " Total rr: " << total_rr_sum << endl;
	cout << " Total avg_rr: " << total_rr_sum / total_count << endl;

	cout << "[===============================]" << endl;
}

// ===================================
// Fitness de la solución.
// ===================================
double Solution::fitness() {
	if (!_initialized) {
		return infinity();
	}

	double maxMakespan = 0.0;
	double awrr = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		awrr += _machines[machineId].getAccumulatedWeightedResponseRatio();

		if (_machines[machineId].getMakespan() > maxMakespan) {
			maxMakespan = _machines[machineId].getMakespan();
		}
	}

	double normalized_awrr;
	if (awrr > 0) {
		normalized_awrr = (awrr + Solution::_awrr_reference) / Solution::_awrr_reference;
	} else {
		normalized_awrr = 0;
	}

	double normalized_makespan;
	normalized_makespan = (maxMakespan + Solution::_makespan_reference) / Solution::_makespan_reference;

	if (DEBUG) {
		cout << endl << "awrr: " << awrr << endl;
		cout << endl << "normalized_awrr: " << normalized_awrr << endl;
		cout << endl << "normalized_makespan: " << normalized_makespan << endl;
	}

	return (_pbm.getMakespanWeight() * normalized_makespan) + (_pbm.getAWRRWeight()
			* normalized_awrr);
}

double Solution::makespan() {
	if (!_initialized) {
		return infinity();
	}

	double maxMakespan = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		if (_machines[machineId].getMakespan() > maxMakespan) {
			maxMakespan = _machines[machineId].getMakespan();
		}
	}

	return maxMakespan;
}

double Solution::accumulatedWeightedResponseRatio() {
	if (!_initialized) {
		return infinity();
	}

	double awrr = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		awrr += _machines[machineId].getAccumulatedWeightedResponseRatio();

//		if (DEBUG) {
//			cout << "[INFO] machine: " << machineId << " awrr:" << awrr << endl;
//		}
	}

	return awrr;
}

int Solution::length() const {
	return _pbm.taskCount();
}

unsigned int Solution::size() const {
	return (_pbm.taskCount() * sizeof(int)) + (_pbm.machineCount()
			* sizeof(int)) + sizeof(int);
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

double Solution::getMachineFitness(int machineId) {
	double awrr_ratio = (accumulatedWeightedResponseRatio() + Solution::_awrr_reference) / Solution::_awrr_reference;
	double makespan_ratio = (makespan() + Solution::_makespan_reference) / Solution::_makespan_reference;
	return (_pbm.getAWRRWeight() * awrr_ratio) + (_pbm.getMakespanWeight() * makespan_ratio);
}

void Solution::doLocalSearch() {
	//	if (DEBUG)
	//		cout << endl << "[DEBUG] Solution::doLocalSearch begin" << endl;

	vector<double> fitnessByMachine;

	for (int machineId = 0; machineId < this->machines().size(); machineId++) {
		fitnessByMachine.push_back(getMachineFitness(machineId));
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

			//			if (DEBUG) cout << endl << "[DEBUG] Intento " << intento << endl;

			// Itero en las tareas de la máquina actual.
			int startTaskOffset, endTaskOffset;
			if (this->machines()[machineId].countTasks() > PALS_TOP_M) {
				// Si la cantidad de tareas en la máquina actual es mayor que PALS_TOP_M.
				double rand;
				rand = rand01();

				double aux;
				aux = rand * this->machines()[machineId].countTasks();

				startTaskOffset = (int) aux;
				endTaskOffset = startTaskOffset + PALS_TOP_M;
			} else {
				// Si hay menos o igual cantidad de tareas en la máquina actual que el
				// tope PALS_TOP_M, las recorro todas.
				startTaskOffset = 0;
				endTaskOffset = this->machines()[machineId].countTasks();
			}

			//			if (DEBUG) cout << endl << "[DEBUG] En la máquina actual hay " << this->machines()[machineId].countTasks()
			//					<< " tareas, pruebo desde la " << startTaskOffset << " a la " << endTaskOffset << endl;

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

				//				if (DEBUG) cout << endl << "[DEBUG] En el problema hay " << this->pbm().taskCount()
				//						<< " tareas, pruebo desde la " << startSwapTaskOffset << endl;

				for (int swapTaskOffset = startSwapTaskOffset; countSwapTaskOffset
						> 0; swapTaskOffset++) {
					assert(swapTaskOffset < (2*this->pbm().taskCount()));

					int swapTaskId;
					swapTaskId = swapTaskOffset % this->pbm().taskCount();

					//					if (DEBUG) cout << endl << "[DEBUG] Intento swapear taskId=" << taskId
					//							<< "con taskId=" << swapTaskId << endl;

					if (swapTaskId != taskId) {
						countSwapTaskOffset--;

						int swapMachineId, swapTaskPos;
						assert(this->findTask(swapTaskId, swapMachineId, swapTaskPos));

						//==============================================================
						//TODO: Optimizar!!!
						//==============================================================
						this->swapTasks(machineId, taskPos, swapMachineId,
								swapTaskPos);
						movimientoFitness = this->fitness();
						this->swapTasks(swapMachineId, swapTaskPos, machineId,
								taskPos);
						//==============================================================

						if (movimientoFitness < mejorMovimientoFitness) {
							//							cout << endl << "Mejora parcial " << movimientoFitness - mejorMovimientoFitness << endl;

							mejorMovimientoFitness = movimientoFitness;
							mejorMovimientoTaskPos = taskPos;
							mejorMovimientoDestinoMachineId = swapMachineId;
							mejorMovimientoDestinoTaskPos = swapTaskPos;
						}
					}
				}
			}

			if (mejorMovimientoFitness < fitnessInicial) {
				//				if (DEBUG) cout << endl << "[DEBUG] Se mejoró la solución!" << endl;
				this->swapTasks(machineId, mejorMovimientoTaskPos,
						mejorMovimientoDestinoMachineId,
						mejorMovimientoDestinoTaskPos);
				finBusqMaquina = true;
			}
		}

		solucionAceptada = (this->fitness() / fitnessInicial)
				>= PALS_UMBRAL_MEJORA;
	}
}

void Solution::mutate() {
	//		if (DEBUG)	cout << endl << "[DEBUG] Solution::mutate" << endl;

	// Con una probabilidad de 0.8 a cada máquina sin tareas se le asigna la tarea que
	// mejor puede ejecutar.
	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		if (_machines[machineId].countTasks() == 0) {
			if (rand01() < 0.8) {
				{
					int bestTaskIdForMachine;
					bestTaskIdForMachine = _pbm.getBestTaskIdForMachine(
							machineId);

					int origenMachineId, origenTaskPos;
					assert(findTask(bestTaskIdForMachine, origenMachineId, origenTaskPos));

					if (_machines[origenMachineId].countTasks() > 1) {
						if (_pbm.getBestTaskIdForMachine(origenMachineId)
								!= bestTaskIdForMachine) {
							_machines[origenMachineId].removeTask(origenTaskPos);
							_machines[machineId].addTask(bestTaskIdForMachine);
						}
					}
				}

				{
					if (_machines[machineId].countTasks() == 0) {
						int mostLoadedMachineId = getMaxCostMachineId();

						int bestTaskPosForMachine;
						bestTaskPosForMachine
								= getMinDestinationCostTaskPosByMachine(
										mostLoadedMachineId, machineId);

						int bestTaskIdForMachine;
						bestTaskIdForMachine
								= _machines[mostLoadedMachineId].getTask(
										bestTaskPosForMachine);

						_machines[mostLoadedMachineId].removeTask(
								bestTaskPosForMachine);
						_machines[machineId].addTask(bestTaskIdForMachine);
					}
				}
			}
		}
	}

	vector<double> fitnessByMachine;
	fitnessByMachine.reserve(_machines.size());

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		fitnessByMachine.push_back(getMachineFitness(machineId));
	}

	RouletteWheel roulette(fitnessByMachine, true);

	for (int i = 0; i < MUT_MAQ; i++) {
		int machineId;
		{
			// Sorteo una máquina para mutar.
			do {
				machineId = roulette.drawOneByIndex();
			} while (_machines[machineId].countTasks() == 0);
		}

		if (_machines[machineId].countTasks() > 0) {
			{
				vector<double> costsByTaskPos;
				costsByTaskPos.reserve(_machines[machineId].countTasks());
				costsByTaskPos.clear();

				for (int taskPos = 0; taskPos
						< _machines[machineId].countTasks(); taskPos++) {
					// Inicializo el vector de costos de las tareas de la máquina actual
					// para sortear una tarea.
					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					double taskCost;
					taskCost = _pbm.expectedTimeToCompute(taskId, machineId);

					costsByTaskPos.push_back(taskCost);
				}

				RouletteWheel roulette(costsByTaskPos, true);

				set<int> taskPosSorteadas;
				taskPosSorteadas.clear();

				for (int MUT_TASKS_COUNT = 0; MUT_TASKS_COUNT < MUT_TASKS; MUT_TASKS_COUNT++) {
					taskPosSorteadas.insert(roulette.drawOneByIndex());
				}

				for (int selectedTaskPos = 0; selectedTaskPos
						< taskPosSorteadas.size(); selectedTaskPos++) {
					if (rand01() >= 0.5) {
						// Se selecciona una tarea T según rueda de ruleta por su COSTO y se
						// intercambia con la tarea que mejor puede ejecutarse en la máquina actual de
						// la máquina en la que mejor puede ejecutarse la tarea sorteada.

						// Obtengo la máquina que que mejor puede ejecutar la tarea.
						int bestMachineId;
						bestMachineId = _pbm.getBestMachineForTaskId(
								_machines[machineId].getTask(selectedTaskPos));

						if (bestMachineId != machineId) {
							if (_machines[bestMachineId].countTasks() > 0) {
								// Si la máquina destino tiene al menos una tarea, obtengo la tarea
								// con menor costo de ejecución en la máquina sorteada.
								int minCostTaskPosOnMachine;
								minCostTaskPosOnMachine
										= getMinDestinationCostTaskPosByMachine(
												bestMachineId, machineId);

								// Hago un swap entre las tareas de las máquinas.
								swapTasks(machineId, selectedTaskPos,
										bestMachineId, minCostTaskPosOnMachine);
							}
						}
					}

					if (rand01() >= 0.5) {
						// Se selecciona una tarea T según rueda de ruleta por su COSTO y se
						// intercambia con la tarea de la máquina con menor makespan que puede ejecutarse
						// más eficientemente en la máquina actual.

						// Obtengo la máquina que aporta un menor costo al total de la solución.
						int minCostMachineId;
						minCostMachineId = getMinCostMachineId();

						if (_machines[minCostMachineId].countTasks() > 0) {
							// Si la máquina destino tiene al menos una tarea, obtengo la tarea
							// con menor costo de ejecución en la máquina sorteada.

							int minCostTaskPosOnMachine;
							minCostTaskPosOnMachine
									= getMinDestinationCostTaskPosByMachine(
											minCostMachineId, machineId);

							// Hago un swap entre las tareas de las máquinas.
							swapTasks(machineId, selectedTaskPos,
									minCostMachineId, minCostTaskPosOnMachine);
						}
					}
				}
			}

			if (rand01() >= 0.5) {
				// Se selecciona una tarea T según su función de PRIORIDAD y se
				// adelanta si lugar en la cola de ejecución.

				//				for (int taskPos = _machines[machineId].countTasks() - 1;
				//						taskPos > 0; taskPos--) {
				for (int taskPos = 1; taskPos
						< _machines[machineId].countTasks(); taskPos++) {

					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					int taskPriority;
					taskPriority = _pbm.taskPriority(taskId);

					int anteriorTaskId;
					anteriorTaskId = _machines[machineId].getTask(taskPos - 1);

					int anteriorTaskPriority;
					anteriorTaskPriority = _pbm.taskPriority(anteriorTaskId);

					if (taskPriority < anteriorTaskPriority) {
						_machines[machineId].swapTasks(taskPos, taskPos - 1);
					}
				}
			}
		}
	}
}

void Solution::addTask(const int machineId, const int taskId) {
	_machines[machineId].addTask(taskId);
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
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_String begin" << endl;
	int machineSeparator = -1;
	int endMark = -2;

	int rawPos = 0;
	char *rawChar = new char[size()];
	int *raw = (int*) rawChar;

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

	//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_String end" << endl;

	return rawChar;
}

void Solution::to_Solution(char *_string_) {
	//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_Solution" << endl;

	int *raw = (int*) _string_;

	int machineSeparator = -1;
	int endMark = -2;

	bool endFound = false;

	int currentTask = 0;
	int currentMachine = 0;

	for (int pos = 0; pos < (_pbm.taskCount() + _pbm.machineCount() + 1)
			&& !endFound; pos++) {

		int currentValue;
		currentValue = raw[pos];

		if (currentValue == endMark) {
			endFound = true;
		} else if (currentValue == machineSeparator) {
			assert(currentMachine < _pbm.machineCount());

			currentMachine++;
		} else {
			assert(currentValue >= 0);
			assert(currentValue < _pbm.taskCount());
			assert(currentMachine < _pbm.machineCount());

			_machines[currentMachine].addTask(currentValue);
			currentTask++;
		}
	}

	assert(_machines.size() == _pbm.machineCount());
	assert(currentTask == _pbm.taskCount());
	assert(endFound);

	markAsInitialized();
}

const vector<struct SolutionMachine>& Solution::machines() const {
	return _machines;
}

int Solution::getBestFitnessMachineId() {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::getBestFitnessMachineId" << endl;

	int bestFitnessMachineId = 0;
	double bestFitnessMachineValue = getMachineFitness(0);

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineFitness;
		currentMachineFitness = getMachineFitness(machineId);

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

int Solution::getMaxCostMachineId() {
	// if (DEBUG) cout << endl << "[DEBUG] Solution::getMaxCostMachineId" << endl;
	int maxCostMachineId = 0;
	double maxCostMachineValue = _machines[0].getMakespan();

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineCost;
		currentMachineCost = _machines[machineId].getMakespan();

		if (maxCostMachineValue < currentMachineCost) {
			maxCostMachineValue = currentMachineCost;
			maxCostMachineId = machineId;
		}
	}

	return maxCostMachineId;
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
	double minCostTaskValue = _pbm.expectedTimeToCompute(
			machines()[machineId].getTask(0), machineId);

	for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
		double currentTaskCost;
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
		double minCostTaskValue = _pbm.expectedTimeToCompute(
				machines()[machineId].getTask(0), destinationMachineId);

		for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
			double currentTaskCost;
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

	if (solver.time_spent_trial() >= 6.0e+07) {
		return true;
	} else {
		return false;
	}

	return false;
}

StopCondition_1::~StopCondition_1() {
}

//------------------------------------------------------------------------
// Specific methods ------------------------------------------------------
//------------------------------------------------------------------------

bool terminateQ(const Problem& pbm, const Solver& solver,
		const SetUpParams& setup) {

	StopCondition_1 stop;
	return stop.EvaluateCondition(pbm, solver, setup);

	return false;
}

}
#endif

