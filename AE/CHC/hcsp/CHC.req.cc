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

//		if (DEBUG)
//			cout << "[DEBUG] Task: " << taskPos << " => Priority: "
//					<< pbm._tasksPriorities[taskPos] << endl;
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

//			if (DEBUG)
//				cout << "[DEBUG] Task: " << taskPos << ", Machine: "
//						<< machinePos << ", ETC: "
//						<< pbm._expectedTimeToCompute[taskPos][machinePos]
//						<< endl;
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

// Solution --------------------------------------------------------------

SolutionMachine::SolutionMachine(int machineId) :
	_tasks(), _machineId(machineId) {
}

SolutionMachine::~SolutionMachine() {
}

int SolutionMachine::machineId() const {
	return _machineId;
}

void SolutionMachine::addTask(const int taskId) {
	_tasks.push_back(taskId);
}

void SolutionMachine::setTask(const int taskId, const int taskPos) {
	_tasks.at(taskPos) = taskId;
}

int SolutionMachine::getTask(const int taskPos) const {
	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());
	return _tasks[taskPos];
}

int SolutionMachine::countTasks() const {
	return _tasks.size();
}

void SolutionMachine::insertTask(const int taskId, const int taskPos) {
	_tasks.insert(_tasks.begin() + taskPos, taskId);
}

void SolutionMachine::removeTask(const int taskPos) {
	_tasks.erase(_tasks.begin() + taskPos);
}

Solution::Solution(const Problem& pbm) :
	_pbm(pbm), _machines(), _initialized(false) {
	_machines.reserve(pbm.machineCount());

	for (int machineId = 0; machineId < pbm.machineCount(); machineId++) {
		_machines.push_back(*(new SolutionMachine(machineId)));
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
			os << "  fitness: " << sol.fitnessByMachine(machineId) << endl;

			for (int i = 0; i < sol.machines()[machineId].countTasks(); i++) {
				os << "  taskPos: " << i;
				os << " taskId: " << sol.machines()[machineId].getTask(i);
				os << " ETC: "	<< sol.pbm().expectedTimeToCompute(sol.machines()[machineId].getTask(i), machineId);
				os << " priority: " << sol.pbm().taskPriority(sol.machines()[machineId].getTask(i));
				os << endl;
			}
		}
		os << "* overall fitness: " << sol.fitness() << endl;
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

//	if (DEBUG) cout << endl << "[DEBUG] Solution::operator= this fitness: " << fitness() << endl;
//	if (DEBUG) cout << "[DEBUG] Solution::operator= sol fitness: " << sol.fitness() << endl;

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
void Solution::initialize() {
	_initialized = true;

	int startTask = rand_int(0, _pbm.taskCount() - 1);
	int direction = rand_int(0, 1);
	if (direction == 0)
		direction = -1;

//	if (DEBUG) {
//		cout << endl << endl << "[DEBUG] Initialize()" << endl;
//		cout << "[DEBUG] startTask: " << startTask << endl;
//		cout << "[DEBUG] direction: " << direction << endl;
//	}

	int currentTask;
	for (int taskOffset = 0; taskOffset < _pbm.taskCount(); taskOffset++) {
		currentTask = startTask + (direction * taskOffset);
		if (currentTask < 0)
			currentTask = _pbm.taskCount() + currentTask;
		currentTask = currentTask % _pbm.taskCount();

		int currentMachine;
		currentMachine = rand_int(0, _pbm.machineCount() - 1);

//		if (DEBUG) {
//			cout << "[DEBUG] Task: " << currentTask << " sent to Machine: "
//					<< currentMachine << endl;
//		}

		_machines[currentMachine].addTask(currentTask);
	}

//	if (DEBUG) cout << "[DEBUG] initialized: " << _initialized << endl;
//	if (DEBUG) cout << "[DEBUG] fitness: " << fitness() << endl;
//	if (DEBUG) cout << endl << *this << endl;
}

double Solution::costByMachine(int machineId) const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::costByMachine start" << endl;
	double machineComputeCost = 0.0;

	for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
		int taskId;
		taskId = _machines[machineId].getTask(taskPos);

		double computeCost;
		computeCost = _pbm.expectedTimeToCompute(taskId, machineId);

		machineComputeCost += computeCost;
	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution::costByMachine end" << endl;
	return machineComputeCost;
}

double Solution::fitnessByMachine(const int machineId) const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::fitnessByMachine start" << endl;
	double fitness = 0.0;
	double machineComputeCost = 0.0;

//	if (DEBUG)
//		cout << endl << endl << "[DEBUG] Solution::fitness machineId: "
//				<< machineId << endl;

	for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
		int taskId;
		taskId = _machines[machineId].getTask(taskPos);

//		if (DEBUG)
//			cout << "[DEBUG] Solution::fitness taskId: " << taskId << endl;
//		if (DEBUG)
//			cout << "[DEBUG] Solution::fitness taskPos: " << taskPos << endl;

		double computeCost;
		computeCost = _pbm.expectedTimeToCompute(taskId, machineId);

//		if (DEBUG)
//			cout << "[DEBUG] Solution::fitness computeCost: " << computeCost
//					<< endl;
//		if (DEBUG)
//			cout << "[DEBUG] Solution::fitness taskPriority: "
//					<< _pbm.taskPriority(taskId) << endl;

		double priorityCost;
		priorityCost = 0.0;

		if ((taskPos > 0) && (_pbm.taskPriority(taskId) != 0)) {
			priorityCost += machineComputeCost / _pbm.taskPriority(taskId);
		}

//		if (DEBUG)
//			cout << "[DEBUG] Solution::fitness priorityCost: " << priorityCost
//					<< endl;

		machineComputeCost += computeCost;
		fitness += (computeCost + priorityCost);
//		if (DEBUG)
//			cout << "[DEBUG] Solution::fitness partial fitness: " << fitness
//					<< endl;
	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution::fitnessByMachine end" << endl;
	return fitness;
}

bool Solution::validate() const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::validate start" << endl;
	if (false) {
		if (_machines.size() == _pbm.machineCount()) {
			int taskCount = 0;
			for (int machineId = 0; machineId < _machines.size(); machineId++) {
				for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
					taskCount++;

					int taskId;
					taskId = _machines[machineId].getTask(taskPos);

					if ((taskId < 0) || (taskId >= _pbm.taskCount())) {
						if (DEBUG) cout << endl << "[DEBUG] (taskId < 0) || (taskId >= _pbm.taskCount())" << endl;
						return false;
					}
				}
			}

			if (taskCount != _pbm.taskCount()) {
				if (DEBUG) cout << endl << "[DEBUG] taskCount != _pbm.taskCount()" << endl;
				return false;
			}
		} else {
			if (DEBUG) cout << endl << "[DEBUG] this->_machines.size() == _pbm.machineCount()" << endl;
			return false;
		}
	} else {
		return false;
	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution::validate end" << endl;
	return true;
}

// ===================================
// Fitness de la solución.
// ===================================
double Solution::fitness() const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::fitness start" << endl;
	if (!_initialized) {
//		if (DEBUG) cout << endl << "[DEBUG] Solution not initialized, fitness: infinity" << endl;
		return infinity();
	}

	double fitness = 0.0;

	for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
		fitness += fitnessByMachine(machineId);
	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution fitness: " << fitness << endl;
//	if (DEBUG) cout << endl << "[DEBUG] Solution::fitness end" << endl;
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

//	if (DEBUG) cout << endl << "[DEBUG] Solution::distanceTo: " << distance << endl;
//	if (DEBUG) cout << *this;
//	if (DEBUG) cout << solution;
//	if (DEBUG) cout << endl << "[DEBUG] Solution::distanceTo end" << endl;
	return distance;
}

int Solution::findTask(const int taskId, int& foundMachineId, int& foundTaskPos) const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::findTask start" << endl;
	foundMachineId = -1;
	foundTaskPos = -1;

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
			if (_machines[machineId].getTask(taskPos) == taskId) {
				foundMachineId = machineId;
				foundTaskPos = taskPos;

//				if (DEBUG) cout << endl << "[DEBUG] Solution::findTask end" << endl;
				return 1;
			}
		}
	}
//	if (DEBUG) cout << endl << "[DEBUG] Solution::findTask end" << endl;
	return 0;
}

void Solution::addTask(const int taskId, const int machineId) {
	_machines[machineId].addTask(taskId);
}

void Solution::executeTaskAt(const int taskId, const int machineId,
		const int taskPos) {
	_machines[machineId].insertTask(taskId, taskPos);
}

void Solution::removeTaskAt(const int machineId, const int taskPos) {
	_machines[machineId].removeTask(taskPos);
}

void Solution::swapTasks(int machineId1, int taskPos1, int machineId2,
		int taskPos2) {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks start" << endl;

	int taskId1 = machines()[machineId1].getTask(taskPos1);
	int taskId2 = machines()[machineId2].getTask(taskPos2);

	if (taskId1 != taskId2) {
		_machines[machineId1].setTask(taskId2, taskPos1);
		_machines[machineId2].setTask(taskId1, taskPos2);
	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks end" << endl;
}

void Solution::swapTasks(Solution& solution, const int taskId) {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks start" << endl;
//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks taskId: " << taskId << endl;

	Solution& sol1 = *this;
	Solution& sol2 = solution;
	int machine1, machine2, taskPos1, taskPos2;

//	if (DEBUG) cout << sol1;
//	if (DEBUG) cout << sol2;

	if (sol1.findTask(taskId, machine1, taskPos1) && sol2.findTask(taskId,
			machine2, taskPos2)) {
//		if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks sol1 machineId: " << machine1 << " taskPos: "  << taskPos1 << endl;
//		if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks sol2 machineId: " << machine2 << " taskPos: "  << taskPos2 << endl;

		// Modifico la solución 1.
		// Borro la tarea de la ubicación original.
		sol1.removeTaskAt(machine1, taskPos1);

		// Inserto la tarea en la nueva ubicación.
		if (taskPos2 < sol1._machines[machine2].countTasks()) {
			sol1.executeTaskAt(taskId, machine2, taskPos2);
		} else {
			sol1._machines[machine2].addTask(taskId);
		}

		// Modifico la solución 2.
		// Borro la tarea de la ubicación original.
		sol2.removeTaskAt(machine2, taskPos2);

		// Inserto la tarea en la nueva ubicación.
		if (taskPos1 < sol2._machines[machine1].countTasks()) {
			sol2.executeTaskAt(taskId, machine1, taskPos1);
		} else {
			sol2._machines[machine1].addTask(taskId);
		}
	} else {
		if (DEBUG)
			cout << endl
					<< "[DEBUG] Solution::swapTasks ¡¿no encontré la tarea?!"
					<< endl;
	}

	//		sol1.show();
	//		sol2.show();
//	if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks end" << endl;
}

bool Solution::equalTasks(Solution& solution, const int taskId) const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks start" << endl;
//	if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks taskId " << taskId << endl;

	int machine1, machine2, taskPos1, taskPos2;

	if (findTask(taskId, machine1, taskPos1) && solution.findTask(taskId,
			machine2, taskPos2)) {
//		if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks sol1 machineId: " << machine1 << " taskPos: "  << taskPos1 << endl;
//		if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks sol2 machineId: " << machine2 << " taskPos: "  << taskPos2 << endl;

		bool equal = (machine1 == machine2) && (taskPos1 == taskPos2);

//		if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks equals " << equal << endl;
//		if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks end" << endl;

		return equal;
	} else {
		if (DEBUG)
			cout << endl
					<< "[DEBUG] Solution::equalTasks ¡¿no encontré la tarea?!"
					<< endl;
		return true;
	}
}

char *Solution::to_String() const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_String start" << endl;
//	if (DEBUG) cout << *this << endl;

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

//	if (DEBUG) {
//		if (DEBUG) cout << endl << "[DEBUG] Solution::to_Solution()" << endl;
//		Solution aux(_pbm);
//		aux.to_Solution((char*)raw);
//		cout << aux << endl;
//	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_String end" << endl;
	return (char*)raw;
}

void Solution::to_Solution(char *_string_) {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_Solution start" << endl;
	_initialized = false;

	int *raw = (int*)_string_;
	int machineSeparator = -1;
	int endMark = -2;
	bool endFound = false;
	int currentMachine = 0;

	for (int pos = 0; pos < (_pbm.taskCount() + _pbm.machineCount() + 1) && !endFound;
			pos++) {

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
//	if (DEBUG) cout << endl << "[DEBUG] Solution::to_Solution end" << endl;
}

const vector<struct SolutionMachine>& Solution::machines() const {
	return _machines;
}

int Solution::getBestFitnessMachineId() const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::getBestFitnessMachineId start" << endl;

	int bestFitnessMachineId = 0;
	double bestFitnessMachineValue = fitnessByMachine(0);

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineFitness;
		currentMachineFitness = fitnessByMachine(machineId);

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

//	if (DEBUG) cout << endl << "[DEBUG] Solution::getBestFitnessMachineId end" << endl;

	return bestFitnessMachineId;
}

int Solution::getMinCostMachineId() const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostMachineId start" << endl;

	int minCostMachineId = 0;
	double minCostMachineValue = costByMachine(0);

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineCost;
		currentMachineCost = costByMachine(machineId);

		if (minCostMachineValue > currentMachineCost) {
			minCostMachineValue = currentMachineCost;
			minCostMachineId = machineId;
		}
	}

//	if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostMachineId end" << endl;

	return minCostMachineId;
}

int Solution::getHighestPriorityTaskPosByMachine(int machineId) const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::getHighestPriorityTaskPosByMachine start" << endl;

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

//		if (DEBUG) cout << endl << "[DEBUG] Solution::getHighestPriorityTaskPosByMachine end" << endl;
		return highestPriorityTaskPos;
	} else {
//		if (DEBUG) cout << endl << "[DEBUG] Solution::getHighestPriorityTaskPosByMachine end" << endl;
		return -1;
	}
}

int Solution::getMinCostTaskPosByMachine(int machineId) const {
//	if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostTaskPosByMachine start" << endl;

	if (machines()[machineId].countTasks() > 0) {
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

//		if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostTaskPosByMachine end" << endl;
		return minCostTaskPos;
	} else {
//		if (DEBUG) cout << endl << "[DEBUG] Solution::getMinCostTaskPosByMachine end" << endl;
		return -1;
	}
}

int Solution::getMinDestinationCostTaskPosByMachine(int machineId,
		int destinationMachineId) const {

//	if (DEBUG) cout << endl << "[DEBUG] Solution::getMinDestinationCostTaskPosByMachine start" << endl;
//	if (DEBUG) cout << endl << "destinationMachineId: " << destinationMachineId << endl;

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

//		if (DEBUG) cout << endl << "[DEBUG] Solution::getMinDestinationCostTaskPosByMachine end" << endl;
		return minCostTaskPos;
	} else {
//		if (DEBUG) cout << endl << "[DEBUG] Solution::getMinDestinationCostTaskPosByMachine end" << endl;
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

