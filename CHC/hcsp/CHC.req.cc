#ifndef INC_REQ_CHC
#define INC_REQ_CHC
#include "CHC.hh"
#include <math.h>

using std::cout;

void quickSort(double[], int, int);
int partition(double[], int, int);

void quickSort(double a[], int l, int r) {
	int j;

	if (l < r) {
		// divide and conquer
		j = partition(a, l, r);
		quickSort(a, l, j - 1);
		quickSort(a, j + 1, r);
	}

}

int partition(double a[], int l, int r) {
	int i, j;
	double t;
	double pivot = a[l];
	i = l;
	j = r + 1;

	while (1) {
		do
			++i;
		while (a[i] <= pivot && i <= r);
		do
			--j;
		while (a[j] > pivot);
		if (i >= j)
			break;
		t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	t = a[l];
	a[l] = a[j];
	a[j] = t;
	return j;
}

skeleton CHC {

// Problem ---------------------------------------------------------------

Problem::Problem(int tasks_count, int machines_count) :
	_taskCount(tasks_count), _machineCount(machines_count),
			_expectedTimeToCompute(NULL), _flowtime_weights(),
			_makespan_weights(), _mypid(-1) {
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
		float aux;
		for (int machinePos = 0; machinePos < pbm._machineCount; machinePos++) {
			input.getline(buffer, MAX_BUFFER, '\n');

			sscanf(buffer, "%f", &aux);

			pbm._expectedTimeToCompute[taskPos][machinePos] = aux;
			/* pbm._expectedTimeToCompute[taskPos][machinePos] = aux / 100.0;
			 pbm._expectedTimeToCompute[taskPos][machinePos] = aux / 10000.0;*/

			assert(pbm._expectedTimeToCompute[taskPos][machinePos] >= 0);
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

void Problem::setPId(const int pid) {
	_mypid = pid;
}

void Problem::loadWeights(const vector<double> weights) {
	assert(weights.size() > 0);
	assert(weights.size() % 2 == 0);

	for (unsigned int i = 0; i < weights.size(); i = i + 2) {
		_makespan_weights.push_back(weights[i]);
		_flowtime_weights.push_back(weights[i + 1]);
	}
}

double Problem::getFlowtimeWeight() const {
	assert(_mypid >= 0);
	return getFlowtimeWeight(_mypid);
}

double Problem::getMakespanWeight() const {
	assert(_mypid >= 0);
	return getMakespanWeight(_mypid);
}

double Problem::getFlowtimeWeight(const int pid) const {
	if (pid == 0) {
		return _flowtime_weights[0];
	} else {
		int index = (pid - 1) % _flowtime_weights.size();
		return _flowtime_weights[index];
	}
}

double Problem::getMakespanWeight(const int pid) const {
	if (pid == 0) {
		return _makespan_weights[0];
	} else {
		int index = (pid - 1) % _makespan_weights.size();
		return _makespan_weights[index];
	}
}

Problem::~Problem() {
}

// Solution machine ------------------------------------------------------

SolutionMachine::SolutionMachine(const Problem& problem, int machineId) :
	_tasks(), _assignedTasks(), _machineId(machineId), _makespan(0.0),
			_flowtime(0.0), _dirty(true), _pbm(problem) {

	_tasks.reserve(problem.taskCount());
}

SolutionMachine::~SolutionMachine() {
}

SolutionMachine& SolutionMachine::operator=(const SolutionMachine& machine) {
	_machineId = machine._machineId;

	_makespan = 0.0;
	_flowtime = 0.0;
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

int SolutionMachine::getTaskPos(const int taskId) const {
	for (int taskPos = 0; taskPos < _tasks.size(); taskPos++) {
		if (_tasks[taskPos] == taskId) {
			return taskPos;
		}
	}

	return -1;
}

int SolutionMachine::safeInsertTask(const int taskId, const int taskPos) {
	if (taskPos < _tasks.size()) {
		insertTask(taskId, taskPos);
		return taskPos;
	} else {
		addTask(taskId);
		return _tasks.size() - 1;
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

double SolutionMachine::getFlowtime() {
	refresh();
	return _flowtime;
}

void SolutionMachine::refresh() {
	//_dirty = true;
	if (_dirty) {
		double partial_makespan = 0.0;

		for (int taskPos = 0; taskPos < countTasks(); taskPos++) {
			int taskId;
			taskId = getTask(taskPos);

			double compute_cost;
			compute_cost = _pbm.expectedTimeToCompute(taskId, machineId());
			assert(compute_cost >= 0);

			partial_makespan += compute_cost;
		}

		_flowtime = get_sorted_flowtime();
		_makespan = partial_makespan;

		_dirty = false;
	}
}

double SolutionMachine::get_sorted_flowtime() {
	int i;

	// Calcular flowtime
	double flow = 0.0;
	double cont = 0.0;

	// Recorrer maquinas
	if (_tasks.size() > 0) {
		double *A = (double*) malloc(sizeof(double) * _tasks.size());

		// Recorrer tareas.
		int i = 0;
		for (i = 0; i < _tasks.size(); i++) {
			A[i] = _pbm.expectedTimeToCompute(_tasks[i], _machineId);
		}

		quickSort(A, 0, i - 1);

		flow = 0.0;
		for (int k = 0; k < _tasks.size(); k++) {
			// Contribucion de la tarea k
			cont = A[k] * (i - k);
			flow += cont;
		}

		free(A);
	}

	return flow;

}

// Solution --------------------------------------------------------------

double Solution::_flowtime_reference = 1.0;
double Solution::_makespan_reference = 1.0;

double Solution::getFlowtime_reference() {
	return Solution::_flowtime_reference;
}

double Solution::getMakespan_reference() {
	return Solution::_makespan_reference;
}

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
		/*for (int machineId = 0; machineId < sol.machines().size(); machineId++) {
		 os << "> machineId: " << machineId << endl;
		 //os << "  fitness: " << sol.fitnessByMachine(machineId) << endl;

		 for (int i = 0; i < sol.machines()[machineId].countTasks(); i++) {
		 os << "  taskPos: " << i;
		 os << " taskId: " << sol.machines()[machineId].getTask(i);
		 fprintf(stdout, " ETC: %f ", sol.pbm().expectedTimeToCompute(
		 sol.machines()[machineId].getTask(i), machineId));
		 fprintf(stdout, " WRR: %f ",
		 sol.machines()[machineId].getWeightedResponseRatio(i));
		 os << " priority: " << sol.pbm().taskPriority(
		 sol.machines()[machineId].getTask(i));
		 os << endl;
		 }
		 }*/
		//os << "* overall fitness: " << sol.fitness() << endl;
	} else {
		os << "> solution not initialized." << endl;
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

	//assert(sol.validate());

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
	//sol.validate();

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
			makespan = (machineMakespan[machineId]
					+ _pbm.expectedTimeToCompute(currentTask, machineId));
			double auxFitness;
			auxFitness = _pbm.getMakespanWeight() * makespan;

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

		for (int taskId = 0; taskId < _pbm.taskCount(); taskId++) {
			if (taskIsUnmapped[taskId]) {
				for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
					if ((machineMakespan[machineId]
							+ _pbm.expectedTimeToCompute(taskId, machineId))
							< minCT) {
						minCT = machineMakespan[machineId]
								+ _pbm.expectedTimeToCompute(taskId, machineId);
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

		for (int taskId = 0; taskId < _pbm.taskCount(); taskId++) {
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

					if (minMakespanMachineId != -1) {
						minMakespan = machinesMakespan[minMakespanMachineId]
								+ _pbm.expectedTimeToCompute(taskId,
										minMakespanMachineId);
					}
					if (secondMinMakespanMachineId != -1) {
						secondMinMakespan
								= machinesMakespan[secondMinMakespanMachineId]
										+ _pbm.expectedTimeToCompute(taskId,
												secondMinMakespanMachineId);
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

				secondMinMakespan
						= machinesMakespan[secondMinMakespanMachineId]
								+ _pbm.expectedTimeToCompute(taskId,
										secondMinMakespanMachineId);

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

		_machines[maxSufferageMachineId].addTask(maxSufferageTaskId);

		taskIsUnmapped[maxSufferageTaskId] = false;
		unmappedTasks--;
	}
}

void Solution::initializeRandomMinMin() {
	int minminTasks = rand_int(0, _pbm.taskCount());
	initializeRandomMinMin(minminTasks);
}

void Solution::initializeRandomMinMin(int minminTasks) {
	//if (DEBUG)
	cout << endl << "[DEBUG] Solution::initializeRandomMinMin <start>" << endl;
	cout << endl << "[DEBUG] Solution::initializeRandomMinMin minminTasks: "
			<< minminTasks << endl;

	vector<double> machineMakespan;
	machineMakespan.reserve(_pbm.machineCount() + 1);

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++)
		machineMakespan.push_back(0.0);

	vector<bool> taskIsUnmapped;
	taskIsUnmapped.reserve(_pbm.taskCount() + 1);

	for (int taskId = 0; taskId < _pbm.taskCount(); taskId++)
		taskIsUnmapped.push_back(true);

	int unmappedTasksCount;
	if (minminTasks < _pbm.taskCount()) {
		unmappedTasksCount = minminTasks;
	} else {
		unmappedTasksCount = _pbm.taskCount();
	}

	//cout << "unmappedTasksCount: " << unmappedTasksCount << endl;

	double minCT;
	int minCTTaskId;
	int minCTMachineId;
	double newMakespan;

	while (unmappedTasksCount > 0) {
		minCT = infinity();
		minCTTaskId = -1;
		minCTMachineId = -1;

		for (int taskId = 0; taskId < _pbm.taskCount(); taskId++) {
			if (taskIsUnmapped[taskId]) {
				for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
					if ((machineMakespan[machineId]
							+ _pbm.expectedTimeToCompute(taskId, machineId))
							< minCT) {
						minCT = machineMakespan[machineId]
								+ _pbm.expectedTimeToCompute(taskId, machineId);
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

	int restOfTasks;
	if (minminTasks > _pbm.taskCount()) {
		restOfTasks = 0;
	} else {
		restOfTasks = _pbm.taskCount() - minminTasks;
	}

	//cout << "restOfTasks: " << restOfTasks << endl;

	for (int taskId = 0; taskId < _pbm.taskCount(); taskId++) {
		if (taskIsUnmapped[taskId]) {
			minCT = infinity();
			minCTTaskId = -1;
			minCTMachineId = -1;

			for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
				if ((machineMakespan[machineId] + _pbm.expectedTimeToCompute(
						taskId, machineId)) < minCT) {
					minCT = machineMakespan[machineId]
							+ _pbm.expectedTimeToCompute(taskId, machineId);
					minCTTaskId = taskId;
					minCTMachineId = machineId;
				}
			}

			taskIsUnmapped[minCTTaskId] = false;

			machineMakespan[minCTMachineId] += _pbm.expectedTimeToCompute(
					minCTTaskId, minCTMachineId);
			_machines[minCTMachineId].addTask(minCTTaskId);
		}
	}
}

void Solution::markAsInitialized() {
	_initialized = true;
}

void Solution::initialize(int mypid, int pnumber, const int solutionIndex) {
	if (DEBUG) {
		cout << "[DEBUG] Solution::initialize" << endl;
		cout << "pypid: " << mypid << endl;
		cout << "pnumber: " << pnumber << endl;
		cout << "solutionIndex: " << pnumber << endl;
	}

	markAsInitialized();

	if (solutionIndex == 0) {
		// Inicialización usando una versión determinista de la heurística MCT.
		// La solución 0 (cero) es idéntica en todos las instancias de ejecución.
		// Utilizo la solución 0 (cero) como referencia de mejora del algoritmo.

		//initializeStaticMCT();
		//initializeMinMin();
		initializeRandomMinMin(1024);

		//NOTE: NO EVALUAR FITNESS ANTES DE ESTA ASIGNACIÓN!!!
		Solution::_flowtime_reference = flowtime();
		Solution::_makespan_reference = makespan();

		if (mypid == 0) {
			cout << "MCT reference fitness: " << fitness();
			cout << ", Flowtime: " << flowtime();
			cout << ", Makespan: " << makespan() << endl << endl;
		}
	} else {
		if (rand01() < RANDOM_INIT) {
			initializeRandom();
		} else {
			initializeRandomMCT();
		}
		if (DEBUG) {
			cout << endl << "[proc " << mypid << "] ";
			cout << "Random MCT fitness: " << fitness();
			cout << ", Flowtime: " << flowtime();
			cout << ", Makespan: " << makespan() << endl;
		}
	}
	if (DEBUG) {
		cout << "[DEBUG] Init ready on pid: " << mypid << endl;
	}
}

//void Solution::initialize(int mypid, int pnumber, const int solutionIndex) {
//	markAsInitialized();
//
//	if (solutionIndex == 0) {
//		// Inicialización usando una versión determinista de la heurística MCT.
//		// La solución 0 (cero) es idéntica en todos las instancias de ejecución.
//		// Utilizo la solución 0 (cero) como referencia de mejora del algoritmo.
//
//		initializeStaticMCT();
//
//		//NOTE: NO EVALUAR FITNESS ANTES DE ESTA ASIGNACIÓN!!!
//		Solution::_awrr_reference = accumulatedWeightedResponseRatio();
//		Solution::_makespan_reference = makespan();
//		if (DEBUG) {
//			cout << endl << "[proc " << mypid << "] ";
//			cout << "MCT reference fitness: " << fitness();
//			cout << ", WRR: " << accumulatedWeightedResponseRatio();
//			cout << ", Makespan: " << makespan() << endl;
//		}
//	} else {
//		int proceso_actual = mypid;
//		int offset_heuristica_actual = solutionIndex - 1;
//
//		if (offset_heuristica_actual == 0) {
//			// Inicialización usando una heurística "pesada": MIN-MIN.
//			// Utilizo MIN-MIN para un único elemento de la población inicial.
//
//			initializeMinMin();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "Min-Min fitness: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//
//			showCustomStatics();
//		} else if (offset_heuristica_actual == 1) {
//			initializeMinWRR0();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "MinMinWRR0: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//		} else if (offset_heuristica_actual == 2) {
//			// Inicialización usando otra heurística "pesada" diferente: Sufferage.
//			// Utilizo Sufferage para un único elemento de la población inicial.
//
//			initializeSufferage();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "Sufferage fitness: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//		} else if (offset_heuristica_actual == 3) {
//			initializeMinWRR4();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "MinMinWRR4: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//		} else if (offset_heuristica_actual == 4) {
//			// Inicialización usando otra heurística "pesada" diferente: Sufferage.
//			initializeMinWRR5();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "MinMinWRR5: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//		} else if (offset_heuristica_actual == 5) {
//			initializeMinWRR60();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "MinMinWRR60: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//		} else if (offset_heuristica_actual == 6) {
//			initializeMinWRR61();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "MinMinWRR61: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//		} else if (offset_heuristica_actual == 7) {
//			initializeMinWRR62();
//			if (DEBUG) {
//				cout << endl << "[proc " << proceso_actual << "] ";
//				cout << "MinMinWRR62: " << fitness();
//				cout << ", WRR: " << accumulatedWeightedResponseRatio();
//				cout << ", Makespan: " << makespan() << endl;
//			}
//
//			exit(-1);
//		}
//	}
//}

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

void Solution::show(ostream &output) {
	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
			int taskId;
			taskId = _machines[machineId].getTask(taskPos);

			output << taskId << endl;
		}
		output << "-1" << endl;
	}
}

void Solution::showCustomStatics() {
	//TODO: hacerrrrrrrrrrrrr
}

// ===================================
// Fitness de la solución.
// ===================================
double Solution::fitness() {
	assert(_initialized);

	double maxMakespan = 0.0;
	double flowtime = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		flowtime += _machines[machineId].getFlowtime();

		if (_machines[machineId].getMakespan() > maxMakespan) {
			maxMakespan = _machines[machineId].getMakespan();
		}
	}

	double normalized_flowtime;
	if (flowtime > 0) {
		normalized_flowtime = (flowtime + Solution::_flowtime_reference)
				/ Solution::_flowtime_reference;
	} else {
		normalized_flowtime = 0;
	}

	double normalized_makespan;
	normalized_makespan = (maxMakespan + Solution::_makespan_reference)
			/ Solution::_makespan_reference;

	//	cout << "Norm mks: " << normalized_makespan << ", norm wrr: " << normalized_awrr << endl;
	//	cout << "Peso mks: " << _pbm.getMakespanWeight() << ", peso wrr: " << _pbm.getWRRWeight() << endl;

	double fitness;
	fitness = (_pbm.getMakespanWeight() * normalized_makespan)
			+ (_pbm.getFlowtimeWeight() * normalized_flowtime);

	assert(!(fitness == INFINITY));
	assert(!(fitness == NAN));

	return fitness;
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

	//return floor(maxMakespan);
	return maxMakespan;
}

double Solution::flowtime() {
	if (!_initialized) {
		return infinity();
	}

	double flowtime = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		flowtime = flowtime + _machines[machineId].getFlowtime();

		//		if (DEBUG) {
		//			cout << "[INFO] machine: " << machineId << " awrr:" << _machines[machineId].getAccumulatedWeightedResponseRatio() << endl;
		//		}
	}

	//return floor(awrr);
	return flowtime;
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

			if (!solution._machines[machineId].hasTask(taskId)) {
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
	double flowtime_ratio = (flowtime() + Solution::_flowtime_reference)
			/ Solution::_flowtime_reference;
	double makespan_ratio = (makespan() + Solution::_makespan_reference)
			/ Solution::_makespan_reference;
	return (_pbm.getFlowtimeWeight() * flowtime_ratio)
			+ (_pbm.getMakespanWeight() * makespan_ratio);
}

void Solution::doLocalSearch() {
	if (DEBUG)
		cout << endl
				<< "[DEBUG] Solution::doLocalSearch begin <start> ========================================="
				<< endl;

	//double aux_pre_LS = getFitness();
	//cout << "[DEBUG] Solution::doLocalSearch fitness: " << aux_pre_LS << endl;

	int max_steps = rand_int(5, 20);

	for (int iterations = 0; iterations < max_steps; iterations++) {
		int machineId;
		if (rand01() <= 0.9) {
			machineId = getMaxCostMachineId();
		} else {
			machineId = rand_int(0, this->machines().size() - 1);
		}

		if (this->machines()[machineId].countTasks() > 0) {
			// PALS aleatorio para HCSP.
			double mejorMovimientoFitness;
			int mejorMovimientoTaskPos, mejorMovimientoDestinoTaskPos,
					mejorMovimientoDestinoMachineId, mejorMovimientoTipo;

			double fitnessActual = fitness();

			mejorMovimientoFitness = fitnessActual;
			mejorMovimientoTipo = -1;
			mejorMovimientoTaskPos = -1;
			mejorMovimientoDestinoTaskPos = -1;
			mejorMovimientoDestinoMachineId = -1;

			// Itero en las tareas de la máquina actual.
			int startTaskOffset, endTaskOffset;
			if (this->machines()[machineId].countTasks() > 1) {
				startTaskOffset = rand_int(0,
						this->machines()[machineId].countTasks() - 1);
			} else if (this->machines()[machineId].countTasks() == 1) {
				startTaskOffset = 0;
			}

			if (this->machines()[machineId].countTasks() > PALS_TOP_M) {
				// Si la cantidad de tareas en la máquina actual es mayor que PALS_TOP_M.
				endTaskOffset = startTaskOffset + PALS_TOP_M;
			} else {
				// Si hay menos o igual cantidad de tareas en la máquina actual que el
				// tope PALS_TOP_M, las recorro todas.
				endTaskOffset = startTaskOffset
						+ this->machines()[machineId].countTasks();
			}

			for (int taskOffset = startTaskOffset; taskOffset < endTaskOffset; taskOffset++) {
				int taskPos;
				taskPos = taskOffset % this->machines()[machineId].countTasks();

				int taskId;
				taskId = this->machines()[machineId].getTask(taskPos);

				if (rand01() <= 0.8) {
					// Operación SWAP !!! ===============================================

					int machineDstId;

					if (rand01() <= 0.5) {
						machineDstId = getMinCostMachineId();

						if (machineId == machineDstId) {
							machineDstId = rand_int(0, this->machines().size()
									- 2);

							if (machineId <= machineDstId)
								machineDstId++;
						}
					} else {
						machineDstId = rand_int(0, this->machines().size() - 2);

						if (machineId <= machineDstId)
							machineDstId++;
					}

					if (this->machines()[machineDstId].countTasks() > 0) {
						// Itero en las tareas de las otras máquinas.
						int startSwapTaskOffset, endSwapTaskOffset;
						if (this->machines()[machineDstId].countTasks() > 1) {
							startSwapTaskOffset = rand_int(0,
									this->machines()[machineDstId].countTasks()
											- 1);
						} else {
							startSwapTaskOffset = 0;
						}

						if (this->machines()[machineDstId].countTasks()
								> PALS_TOP_T) {
							// Si la cantidad de las tareas del problema menos la tarea que estoy
							// intentando mover es mayor que PALS_TOP_T.
							endSwapTaskOffset = startSwapTaskOffset
									+ PALS_TOP_T;
						} else {
							// Si hay menos o igual cantidad de tareas en el problema que el número
							// PALS_TOP_T las recorro todas menos la que estoy intentando mover.
							endSwapTaskOffset
									= startSwapTaskOffset
											+ this->machines()[machineDstId].countTasks();
						}

						double movimientoFitness;
						movimientoFitness = 0.0;

						for (int swapTaskOffset = startSwapTaskOffset; swapTaskOffset
								< endSwapTaskOffset; swapTaskOffset++) {
							int swapTaskPos;
							swapTaskPos
									= swapTaskOffset
											% this->machines()[machineDstId].countTasks();

							int swapTaskId;
							swapTaskId
									= this->machines()[machineDstId].getTask(
											swapTaskPos);

							//==============================================================
							//TODO: Optimizar!!!
							//==============================================================
							this->swapTasks(machineId, taskPos, machineDstId,
									swapTaskPos);
							movimientoFitness = this->fitness();
							this->swapTasks(machineDstId, swapTaskPos,
									machineId, taskPos);
							//==============================================================

							if (movimientoFitness < mejorMovimientoFitness) {
								mejorMovimientoTipo = 0; // SWAP
								mejorMovimientoFitness = movimientoFitness;
								mejorMovimientoTaskPos = taskPos;
								mejorMovimientoDestinoMachineId = machineDstId;
								mejorMovimientoDestinoTaskPos = swapTaskPos;
							}
						}
					}
				} else {
					// Operación MOVE !!! ===============================================

					// Itero en las otras máquinas.
					int startMoveMachineOffset, endMoveMachineOffset;

					if (rand01() <= 0.5) {
						startMoveMachineOffset = getMinCostMachineId();
					} else {
						startMoveMachineOffset = rand_int(0,
								this->machines().size() - 1);
					}

					if (this->machines().size() > PALS_TOP_T) {
						// Si la cantidad de las tareas del problema menos la tarea que estoy
						// intentando mover es mayor que PALS_TOP_T.
						endMoveMachineOffset = startMoveMachineOffset
								+ PALS_TOP_T;
					} else {
						// Si hay menos o igual cantidad de tareas en el problema que el número
						// PALS_TOP_T las recorro todas menos la que estoy intentando mover.
						endMoveMachineOffset = startMoveMachineOffset
								+ this->machines().size();
					}

					double movimientoFitness;
					movimientoFitness = 0.0;

					for (int moveMachineOffset = startMoveMachineOffset; moveMachineOffset
							< endMoveMachineOffset; moveMachineOffset++) {

						int moveMachineId;
						moveMachineId = moveMachineOffset
								% this->machines().size();

						if (moveMachineId != machineId) {
							//==============================================================
							//TODO: Optimizar!!!
							//==============================================================
							this->moveTask(taskId, moveMachineId);
							movimientoFitness = this->fitness();
							this->moveTask(taskId, machineId);
							//==============================================================

							if (movimientoFitness < mejorMovimientoFitness) {
								mejorMovimientoTipo = 1; // MOVE
								mejorMovimientoFitness = movimientoFitness;
								mejorMovimientoTaskPos = taskPos;
								mejorMovimientoDestinoMachineId = moveMachineId;
								mejorMovimientoDestinoTaskPos = -1;
							}
						}
					}
				}
			}

			if (mejorMovimientoFitness < fitnessActual) {
				if (mejorMovimientoTipo == 0) {
					this->swapTasks(machineId, mejorMovimientoTaskPos,
							mejorMovimientoDestinoMachineId,
							mejorMovimientoDestinoTaskPos);
				} else {
					this->moveTask(this->machines()[machineId].getTask(
							mejorMovimientoTaskPos),
							mejorMovimientoDestinoMachineId);
				}
			}
		}
	}

	/*
	 cout << "[DEBUG] Solution::doLocalSearch after fitness: " << getFitness()
	 << endl;
	 cout << "[DEBUG] Solution::doLocalSearch improvement: " << getFitness()
	 / aux_pre_LS << endl;
	 */

	if (DEBUG)
		cout << endl << "[DEBUG] Solution::doLocalSearch begin <end>" << endl;
}

void Solution::flowtime_sort() {
	//TODO: hacerrrrrrrrrrrrrrrrrrrrrrrrr

	/*for (int machineId = 0; machineId < _machines.size(); machineId++) {
	 _machines[machineId].flowtime_sort();
	 }*/
}

void Solution::doMutate() {
	if (DEBUG)
		cout << endl
				<< "[DEBUG] Solution::doMutate begin <start> ========================================="
				<< endl;

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		if (rand01() <= MUT_MAQ) {
			for (int selectedTaskPos = 0; selectedTaskPos
					< _machines[machineId].countTasks(); selectedTaskPos++) {
				if (rand01() < MUT_TASK) {
					int selectedTaskId;
					selectedTaskId = _machines[machineId].getTask(
							selectedTaskPos);

					int machineDstId = rand_int(0, pbm().machineCount() - 2);
					if (machineId >= machineDstId)
						machineDstId++;

					moveTask(selectedTaskId, machineDstId);
				}
			}
		}
	}

	if (DEBUG)
		cout << endl << "[DEBUG] Solution::doMutate begin <end>" << endl;
}

void Solution::addTask(const int machineId, const int taskId) {
	_machines[machineId].addTask(taskId);
}

void Solution::moveTask(const int taskId, const int machineId) {
	if (DEBUG)
		cout << "[DEBUG] Solution::moveTasks" << endl;

	int machineIdOld = -1;

	for (int machineId = 0; (machineId < _machines.size()) && (machineIdOld == -1); machineId++) {
		if (_machines[machineId].hasTask(taskId)) {
			machineIdOld = machineId;
		}
	}

	if (machineId != machineIdOld) {
		_machines[machineIdOld].removeTask(
				_machines[machineIdOld].getTaskPos(taskId));

		_machines[machineId].addTask(taskId);
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

	if ((machine1 != machine2) || (taskPos1 != taskPos2)) {
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

vector<struct SolutionMachine>& Solution::getMachines() {
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

	os << endl << "trial\t" << "best\t\t" << "worst\t\t\t" << "eval_best_found"
			<< "\t\t\t" << "iter_best_found" << "\t\t\t" << "time_best_found"
			<< "\t\t" << "time_spent_trial";

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

	//if (solver.current_iteration() >= setup.timeout()) {
	//	return true;
	//}

	if (solver.time_spent_trial() >= (setup.timeout() * 1.0e+06)) {
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

