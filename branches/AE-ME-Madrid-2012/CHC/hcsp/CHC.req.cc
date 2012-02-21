#ifndef INC_REQ_CHC
#define INC_REQ_CHC
#include "CHC.hh"
#include <math.h>

using std::cout;

skeleton CHC {

// Problem ---------------------------------------------------------------

Problem::Problem() :
	_taskCount(0), _machineCount(0), _makespan_weights(), _mypid(-1),
			_taskSSJComputeCost(NULL), _expectedTimeToCompute(NULL) {
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
void Problem::loadProblemDataFiles(istream& scenario, istream& workload) {
	char buffer[MAX_BUFFER];

	// Inicializo el consumo energético de cada máquina.
	{
		_machineCoreCount.reserve(_machineCount);
		_machineSSJPerformance.reserve(_machineCount);
		_machineConsumptionIdle.reserve(_machineCount);
		_machineConsumptionMax.reserve(_machineCount);

		int coreCount, ssjOps;
		float consumptionIdle, consumptionMax;

		for (int machinePos = 0; machinePos < _machineCount; machinePos++) {
			scenario.getline(buffer, MAX_BUFFER, '\n');
			sscanf(buffer, "%d %d %f %f", &coreCount, &ssjOps,
					&consumptionIdle, &consumptionMax);

			assert(coreCount > 0);
			// TODO: ¿coreCount es siempre 1?
			// coreCount = 1;
			_machineCoreCount.push_back(coreCount);

			assert(ssjOps > 0);
			_machineSSJPerformance.push_back(ssjOps);

			assert(consumptionIdle > 0.0);
			_machineConsumptionIdle.push_back(consumptionIdle);

			assert(consumptionMax > 0.0);
			_machineConsumptionMax.push_back(consumptionMax);
		}
	}

	// Inicializo toda la matriz de ETC.
	{
		_taskSSJComputeCost = new float*[_taskCount];
		if (_taskSSJComputeCost == NULL) {
			cout
					<< "[ERROR] no se pudo reservar memoria para la matriz _taskSSJComputeCost"
					<< endl;
			show_message(7);
		}

		_expectedTimeToCompute = new float*[_taskCount];
		if (_expectedTimeToCompute == NULL) {
			cout
					<< "[ERROR] no se pudo reservar memoria para la matriz _expectedTimeToCompute"
					<< endl;
			show_message(7);
		}

		// Inicializo cada tarea del problema.
		for (int taskPos = 0; taskPos < _taskCount; taskPos++) {
			// Por cada tarea creo una lista de maquinas.
			_taskSSJComputeCost[taskPos] = new float[_machineCount];
			_expectedTimeToCompute[taskPos] = new float[_machineCount];

			if (_taskSSJComputeCost[taskPos] == NULL) {
				cout
						<< "[ERROR] no se pudo reservar memoria para las máquinas de la tarea "
						<< taskPos << endl;
				show_message(7);
			}

			// Cargo el ETC de cada tarea en cada una de las máquinas.
			for (int machinePos = 0; machinePos < _machineCount; machinePos++) {
				workload.getline(buffer, MAX_BUFFER, '\n');

				sscanf(buffer, "%f", &_taskSSJComputeCost[taskPos][machinePos]);
				assert(_taskSSJComputeCost[taskPos][machinePos] >= 0);

				_expectedTimeToCompute[taskPos][machinePos]
						= _taskSSJComputeCost[taskPos][machinePos]
								/ (_machineSSJPerformance[machinePos]
										/ (_machineCoreCount[machinePos] * 1000));
			}
		}
	}
}

Problem& Problem::operator=(const Problem& pbm) {
	return *this;
}

bool Problem::operator==(const Problem& pbm) const {
	if (getTaskCount() != pbm.getTaskCount())
		return false;
	return true;
}

bool Problem::operator!=(const Problem& pbm) const {
	return !(*this == pbm);
}

Direction Problem::direction() const {
	return minimize;
}

void Problem::setTaskCount(int size) {
	_taskCount = size;
}

int Problem::getTaskCount() const {
	return _taskCount;
}

void Problem::setMachineCount(int size) {
	_machineCount = size;
}

int Problem::getMachineCount() const {
	return _machineCount;
}

int Problem::getBestTaskIdForMachine(int machineId) const {
	//TODO: Optimizar!
	int minTaskId = 0;
	for (int i = 1; i < getTaskCount(); i++) {
		if (getTaskSSJCost(i, machineId) < getTaskSSJCost(minTaskId, machineId)) {
			minTaskId = i;
		}
	}
	return minTaskId;
}

int Problem::getBestMachineForTaskId(int taskId) const {
	//TODO: Optimizar!
	int minMachineId = 0;
	for (int i = 1; i < getMachineCount(); i++) {
		if (getTaskSSJCost(taskId, i) < getTaskSSJCost(taskId, minMachineId)) {
			minMachineId = i;
		}
	}
	return minMachineId;
}

float Problem::getExpectedTimeToCompute(const int& task, const int& machine) const {
	assert(task >= 0);
	assert(task < _taskCount);
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _expectedTimeToCompute[task][machine];
}

float Problem::getTaskSSJCost(const int& task, const int& machine) const {
	assert(task >= 0);
	assert(task < _taskCount);
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _taskSSJComputeCost[task][machine];
}

int Problem::getMachineCoreCount(const int& machine) const {
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _machineCoreCount[machine];
}

int Problem::getMachineSSJPerformance(const int& machine) const {
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _machineSSJPerformance[machine];
}

float Problem::getMachineEnergyWhenIdle(const int& machine) const {
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _machineConsumptionIdle[machine];
}

float Problem::getMachineEnergyWhenMax(const int& machine) const {
	assert(machine >= 0);
	assert(machine < _machineCount);
	return _machineConsumptionMax[machine];
}

void Problem::setCurrentProcessId(const int pid) {
	_mypid = pid;
}

void Problem::loadWeightData(const vector<double> weights) {
	assert(weights.size() > 0);
	assert(weights.size() % 2 == 0);

	for (unsigned int i = 0; i < weights.size(); i = i + 2) {
		_makespan_weights.push_back(weights[i]);
		_energy_weights.push_back(weights[i + 1]);
	}
}

double Problem::getCurrentMakespanWeight() const {
	assert(_mypid >= 0);
	return getMakespanWeight(_mypid);
}

double Problem::getCurrentEnergyWeight() const {
	assert(_mypid >= 0);
	return getEnergyWeight(_mypid);
}

double Problem::getEnergyWeight(const int pid) const {
	if (pid == 0) {
		return _energy_weights[0];
	} else {
		int index = (pid - 1) % _energy_weights.size();
		return _energy_weights[index];
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

SolutionMachine::SolutionMachine(const Solution& solution, int machineId) :
	_tasks(), _assignedTasks(), _machineId(machineId), _computeTime(0.0),
			_energy(0.0), _solution(solution) {

	_tasks.reserve(_solution.pbm().getTaskCount());
}

SolutionMachine::~SolutionMachine() {
}

SolutionMachine& SolutionMachine::operator=(const SolutionMachine& machine) {
	if (DEBUG)
		cout << "[DEBUG] SolutionMachine::operator= " << _machineId << endl;

	_machineId = machine._machineId;
	_computeTime = machine._computeTime;
	_energy = machine._energy;

	_tasks.clear();
	_tasks.reserve(machine._tasks.size());

	_assignedTasks.clear();

	for (int taskPos = 0; taskPos < machine._tasks.size(); taskPos++) {
		int taskId;
		taskId = machine.getTask(taskPos);

		_tasks.push_back(taskId);
		_assignedTasks[taskId] = taskPos;
	}

	return *this;
}

int SolutionMachine::getMachineId() const {
	return _machineId;
}

void SolutionMachine::addTask(const int taskId) {
	if (DEBUG)
		cout << "[DEBUG] SolutionMachine::addTask() " << _machineId << endl;

	float max_energy = _solution.pbm().getMachineEnergyWhenMax(_machineId);

	_computeTime += _solution.pbm().getExpectedTimeToCompute(taskId,
			getMachineId());
	_energy += _solution.pbm().getExpectedTimeToCompute(taskId, getMachineId())
			* max_energy;

	_tasks.push_back(taskId);
	_assignedTasks[taskId] = _tasks.size() - 1;
}

void SolutionMachine::setTask(const int taskId, const int taskPos) {
	if (DEBUG)
		cout << "[DEBUG] SolutionMachine::setTask() " << _machineId << endl;

	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());

	float max_energy = _solution.pbm().getMachineEnergyWhenMax(_machineId);

	int removedTaskId = _tasks[taskPos];
	_assignedTasks.erase(removedTaskId);

	_computeTime -= _solution.pbm().getExpectedTimeToCompute(removedTaskId,
			getMachineId());
	_energy -= _solution.pbm().getExpectedTimeToCompute(removedTaskId,
			getMachineId()) * max_energy;

	_computeTime += _solution.pbm().getExpectedTimeToCompute(taskId,
			getMachineId());
	_energy += _solution.pbm().getExpectedTimeToCompute(taskId, getMachineId())
			* max_energy;

	_tasks.at(taskPos) = taskId;
	_assignedTasks[taskId] = taskPos;
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

int SolutionMachine::getTaskPosition(const int taskId) const {
	return _assignedTasks.find(taskId)->second;
}

void SolutionMachine::show() const {
	for (map<int, int>::const_iterator it = _assignedTasks.begin(); it
			!= _assignedTasks.end(); it++) {
		cout << (*it).first;
		cout << endl;
	}
}

void SolutionMachine::removeTask(const int taskPos) {
	if (DEBUG)
		cout << "[DEBUG] SolutionMachine::removeTask() " << _machineId << endl;

	assert(taskPos >= 0);
	assert(taskPos < _tasks.size());

	float max_energy = _solution.pbm().getMachineEnergyWhenMax(_machineId);

	int removedId = _tasks[taskPos];

	_computeTime -= _solution.pbm().getExpectedTimeToCompute(removedId,
			getMachineId());
	_energy -= _solution.pbm().getExpectedTimeToCompute(removedId,
			getMachineId()) * max_energy;

	_assignedTasks.erase(removedId);
	_tasks.erase(_tasks.begin() + taskPos);

	for (int i = taskPos; i < _tasks.size(); i++) {
		_assignedTasks[_tasks[i]] = i;
	}
}

void SolutionMachine::emptyTasks() {
	if (DEBUG)
		cout << "[DEBUG] SolutionMachine::emptyTasks() " << _machineId << endl;

	_computeTime = 0.0;
	_energy = 0.0;

	_assignedTasks.clear();
	_tasks.clear();
}

double SolutionMachine::getActiveEnergyConsumption() const {
	//cout << "[INFO] SolutionMachine::getActiveEnergyConsumption _energy:" << _energy << endl;
	return _energy;
}

double SolutionMachine::getIdleEnergyConsumption(double solutionMakespan) const {
	//cout << "[INFO] SolutionMachine::getIdleEnergyConsumption _machineId:" << _machineId << endl;
	float idle_energy = _solution.pbm().getMachineEnergyWhenIdle(_machineId);
	//cout << "[INFO] SolutionMachine::getIdleEnergyConsumption idle_energy:" << idle_energy << endl;
	//cout << "[INFO] SolutionMachine::getIdleEnergyConsumption _computeTime:" << _computeTime << endl;
	return (solutionMakespan - _computeTime) * idle_energy;
}

double SolutionMachine::getTotalEnergyConsumption(double solutionMakespan) const {
	return getActiveEnergyConsumption() + getIdleEnergyConsumption(
			solutionMakespan);
}

double SolutionMachine::getComputeTime() const {
	return _computeTime;
}

void SolutionMachine::refresh() {
	if (DEBUG)
		cout << "[DEBUG] SolutionMachine::refresh() " << _machineId << endl;

	float max_energy = _solution.pbm().getMachineEnergyWhenMax(_machineId);
	double aux_computeTime = 0.0;

	for (int taskPos = 0; taskPos < countTasks(); taskPos++) {
		int taskId;
		taskId = getTask(taskPos);

		aux_computeTime = aux_computeTime
				+ _solution.pbm().getExpectedTimeToCompute(taskId,
						getMachineId());
	}

	_computeTime = aux_computeTime;
	_energy = _computeTime * max_energy;
}

// Solution --------------------------------------------------------------

double Solution::_makespan_reference = 1.0;
double Solution::_energy_reference = 1.0;

double Solution::getEnergy_reference() {
	return Solution::_energy_reference;
}

double Solution::getMakespan_reference() {
	return Solution::_makespan_reference;
}

Solution::Solution(const Problem& pbm) :
	_pbm(pbm), _machines(), _initialized(false), _taskAssignment(NULL),
			_is_dirty(true), _current_fitness(0.0), _current_makespan(0.0),
			_current_energy(0.0) {

	_taskAssignment = (int*) malloc(sizeof(int) * pbm.getTaskCount());
	memset(_taskAssignment, 0, sizeof(int) * pbm.getTaskCount());

	Solution *s = this;

	_machines.reserve(pbm.getMachineCount());
	for (int machineId = 0; machineId < pbm.getMachineCount(); machineId++) {
		_machines.push_back(*(new SolutionMachine(*s, machineId)));
	}
}

const Problem& Solution::pbm() const {
	return _pbm;
}

Solution::Solution(const Solution& sol) :
	_pbm(sol.pbm()) {
	*this = sol;
}

void Solution::show(ostream& os) {
	if (this->isInitilized()) {
		os
				<< "\n[SOLUTION INFO]===============================================\n";
		double makespan;
		makespan = this->getMakespan();

		os << "Makespan : " << makespan << "\n";
		os << "Energy   : " << this->getEnergy(makespan) << "\n";

		os << endl;
	} else {
		os << "> solution not inialized." << endl;
	}
}

// ===================================
// Serialización de la solución.
// ===================================
NetStream& operator <<(NetStream& ns, const Solution& sol) {
	int currentTask = 0;
	int currentItem = 0;

	int machineSeparator = -1;

	for (int machineId = 0; machineId < sol.machines().size(); machineId++) {
		for (int taskPos = 0; taskPos < sol.machines()[machineId].countTasks(); taskPos++) {
			int taskId;
			taskId = sol.machines()[machineId].getTask(taskPos);

			ns << taskId;

			currentTask++;
			currentItem++;
		}
		ns << machineSeparator;

		currentItem++;
	}

	return ns;
}

// ===================================
// Deserialización de la solución.
// ===================================
NetStream& operator >>(NetStream& ns, Solution& sol) {
	if (DEBUG)
		cout << "[DEBUG] Solution::operator >>" << endl;

	int machineSeparator = -1;

	int currentTask = 0;
	int currentMachine = 0;

	sol.emptyTasks();

	for (int pos = 0; pos < sol.pbm().getTaskCount()
			+ sol.pbm().getMachineCount(); pos++) {
		int currentValue;
		ns >> currentValue;

		if (currentValue == machineSeparator) {
			assert(currentMachine < sol.pbm().getMachineCount());
			currentMachine++;
		} else {
			assert(currentValue >= 0);
			assert(currentValue < sol.pbm().getTaskCount());
			assert(currentMachine >= 0);
			assert(currentMachine < sol.pbm().getMachineCount());

			sol.addTask(currentMachine, currentValue);
			currentTask++;
		}
	}

	sol.markAsInitialized();

	return ns;
}

Solution& Solution::operator=(const Solution &sol) {
	if (DEBUG)
		cout << "[DEBUG] Solution::operator =" << endl;

	for (int machineId = 0; machineId < sol._machines.size(); machineId++) {
		_machines[machineId] = sol._machines[machineId];

		/*cout << "[DEBUG] machineId:" << machineId << endl;
		 cout << "[DEBUG] _machines[machineId].getMachineId():" << _machines[machineId].getMachineId() << endl;
		 cout << "[DEBUG] _machines[machineId].countTasks():" << _machines[machineId].countTasks() << endl;*/
	}

	memcpy(_taskAssignment, sol._taskAssignment, sizeof(int)
			* sol.pbm().getTaskCount());

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
	if (DEBUG)
		cout << "[DEBUG] Solution::emptyTasks" << endl;

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		_machines[machineId].emptyTasks();
	}

	memset(_taskAssignment, 0, sizeof(int) * pbm().getTaskCount());

	_is_dirty = false;
	_current_fitness = INFINITY;
	_current_makespan = INFINITY;
	_current_energy = INFINITY;
}

/*int Solution::countTasks() {
 int count = 0;
 for (int machineId = 0; machineId < _machines.size(); machineId++) {
 count += _machines[machineId].countTasks();
 }
 return count;
 }*/

// ===================================
// Inicializo la solución.
// ===================================
void Solution::initializeStaticMCT() {
	//if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT Estática" << endl;

	initializeMCT(0, 1);
}

void Solution::initializeRandomMCT() {
	//if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT Aleatoria" << endl;

	int startTask = rand_int(0, _pbm.getTaskCount() - 1);
	int direction = rand_int(0, 1);
	if (direction == 0)
		direction = -1;

	initializeMCT(startTask, direction);
}

void Solution::initializeMCT(int startTask, int direction) {
	//	if (DEBUG) cout << endl << "[DEBUG] Inicialización MCT" << endl;

	vector<double> machineMakespan;
	machineMakespan.reserve(_pbm.getMachineCount() + 1);

	for (int machineId = 0; machineId < _pbm.getMachineCount(); machineId++)
		machineMakespan.push_back(0.0);

	int currentTask;
	for (int taskOffset = 0; taskOffset < _pbm.getTaskCount(); taskOffset++) {
		currentTask = startTask + (direction * taskOffset);
		if (currentTask < 0)
			currentTask = _pbm.getTaskCount() + currentTask;
		currentTask = currentTask % _pbm.getTaskCount();

		double minFitness;
		minFitness = infinity();

		int minFitnessMachineId;
		minFitnessMachineId = -1;

		for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
			double makespan;
			makespan = machineMakespan[machineId]
					+ _pbm.getExpectedTimeToCompute(currentTask, machineId);

			if (makespan < minFitness) {
				minFitness = makespan;
				minFitnessMachineId = machineId;
			}
		}

		machineMakespan[minFitnessMachineId] += _pbm.getExpectedTimeToCompute(
				currentTask, minFitnessMachineId);

		addTask(minFitnessMachineId, currentTask);
	}
}

void Solution::initializeMinMin() {
	//if (DEBUG) 
	//cout << endl << "[DEBUG] Inicialización MIN-MIN" << endl;

	vector<float> machineAssignedSsjops;
	machineAssignedSsjops.reserve(_pbm.getMachineCount() + 1);

	vector<double> machineMakespan;
	machineMakespan.reserve(_pbm.getMachineCount() + 1);

	for (int machineId = 0; machineId < _pbm.getMachineCount(); machineId++) {
		machineAssignedSsjops.push_back(0.0);
		machineMakespan.push_back(0.0);
	}

	vector<bool> taskIsUnmapped;
	taskIsUnmapped.reserve(_pbm.getTaskCount() + 1);

	for (int taskId = 0; taskId < _pbm.getTaskCount(); taskId++)
		taskIsUnmapped.push_back(true);

	int unmappedTasksCount = _pbm.getTaskCount();

	while (unmappedTasksCount > 0) {
		double minCT;
		minCT = infinity();

		int minCTTaskId;
		minCTTaskId = -1;

		int minCTMachineId;
		minCTMachineId = -1;

		double newMakespan;

		for (int taskId = 0; taskId < _pbm.getTaskCount(); taskId++) {
			if (taskIsUnmapped[taskId]) {
				for (int machineId = 0; machineId < machineMakespan.size(); machineId++) {
					newMakespan = machineAssignedSsjops[machineId]
							+ _pbm.getExpectedTimeToCompute(taskId, machineId);

					if (newMakespan < minCT) {
						minCT = newMakespan;
						minCTTaskId = taskId;
						minCTMachineId = machineId;
					}
				}
			}
		}

		unmappedTasksCount--;
		taskIsUnmapped[minCTTaskId] = false;

		machineAssignedSsjops[minCTMachineId]
				= machineAssignedSsjops[minCTMachineId]
						+ _pbm.getExpectedTimeToCompute(minCTTaskId,
								minCTMachineId);
		machineMakespan[minCTMachineId] = minCT;

		addTask(minCTMachineId, minCTTaskId);
	}
}

void Solution::initializeRandom() {
	//	if (DEBUG) cout << endl << "[DEBUG] Inicialización random" << endl;

	int startTask = rand_int(0, _pbm.getTaskCount() - 1);
	int direction = rand_int(0, 1);
	if (direction == 0)
		direction = -1;

	int currentTask;
	for (int taskOffset = 0; taskOffset < _pbm.getTaskCount(); taskOffset++) {
		currentTask = startTask + (direction * taskOffset);
		if (currentTask < 0)
			currentTask = _pbm.getTaskCount() + currentTask;
		currentTask = currentTask % _pbm.getTaskCount();

		int currentMachine;
		currentMachine = rand_int(0, _pbm.getMachineCount() - 1);

		addTask(currentMachine, currentTask);
	}
}

void Solution::markAsInitialized() {
	_initialized = true;
}

void Solution::initialize(int mypid, int pnumber, const int solutionIndex) {
	timespec ts;

	if (TIMING) {
		clock_gettime(CLOCK_REALTIME, &ts);
	}

	markAsInitialized();

	if (solutionIndex == 0) {
		// Inicialización usando una versión determinista de la heurística MCT.
		// La solución 0 (cero) es idéntica en todos las instancias de ejecución.
		// Utilizo la solución 0 (cero) como referencia de mejora del algoritmo.

		initializeStaticMCT();

		//NOTE: NO EVALUAR FITNESS ANTES DE ESTA ASIGNACIÓN!!!
		double currentMakespan = getMakespan();
		Solution::_makespan_reference = currentMakespan;
		Solution::_energy_reference = getEnergy(currentMakespan);

		if (mypid == 0) {
			cout << ">> Solución de referencia:" << endl;
			cout << "   Makespan: " << Solution::_makespan_reference << endl;
			cout << "   Energy  : " << Solution::_energy_reference << endl;
		} else {
			if (DEBUG) {
				cout << endl << "[proc " << mypid << "] ";
				cout << "MCT reference fitness: " << getFitness();
				cout << ", Makespan: " << getMakespan() << endl;
			}
		}
	} else {
		initializeRandomMCT();
	}

	if (TIMING) {
		timespec ts_end;
		clock_gettime(CLOCK_REALTIME, &ts_end);

		double elapsed;
		elapsed = ((ts_end.tv_sec - ts.tv_sec) * 1000000.0) + ((ts_end.tv_nsec
				- ts.tv_nsec) / 1000.0);
		Solver::global_timing[TIMING_INIT] += elapsed;
	}
}

/*
 bool Solution::validate() const {
 //	if (DEBUG) cout << endl << "[DEBUG] Solution::validate" << endl;
 if (true) {
 for (int t = 0; t < _pbm.getTaskCount(); t++) {
 int machineId, taskPos;
 //assert(findTask(t, machineId, taskPos));
 }

 if (_machines.size() == _pbm.getMachineCount()) {
 int taskCount = 0;

 for (int machineId = 0; machineId < _machines.size(); machineId++) {
 for (int taskPos = 0; taskPos
 < _machines[machineId].countTasks(); taskPos++) {
 taskCount++;

 int taskId;
 taskId = _machines[machineId].getTask(taskPos);

 assert(_machines[machineId].hasTask(taskId));

 if ((taskId < 0) || (taskId >= _pbm.getTaskCount())) {
 if (DEBUG)
 cout << endl
 << "[DEBUG] (taskId < 0) || (taskId >= _pbm.taskCount())"
 << endl;
 assert(false);
 }
 }
 }

 if (taskCount != _pbm.getTaskCount()) {
 if (DEBUG)
 cout << endl << "[DEBUG] taskCount != _pbm.taskCount()"
 << endl;
 cout << endl << "[DEBUG] taskCount:" << taskCount
 << " _pbm.taskCount():" << _pbm.getTaskCount() << endl;
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
 */

void Solution::showCustomStatics(ostream& os) {
	os << endl << "[= Custom Statics ==================]" << endl;

	cout << "[===================================]" << endl;
}

// ===================================
// Fitness de la solución.
// ===================================
double Solution::getFitness() {
	assert(_initialized);

	if (_is_dirty) {
		double maxMakespan = 0.0;
		double energy = 0.0;

		for (int machineId = 0; machineId < _pbm.getMachineCount(); machineId++) {
			if (_machines[machineId].getComputeTime() > maxMakespan) {
				maxMakespan = _machines[machineId].getComputeTime();
			}
		}

		_current_makespan = maxMakespan;

		for (int machineId = 0; machineId < _pbm.getMachineCount(); machineId++) {
			energy
					+= _machines[machineId].getActiveEnergyConsumption()
							+ _machines[machineId].getIdleEnergyConsumption(
									maxMakespan);
		}

		_current_energy = energy;

		double normalized_makespan;
		normalized_makespan = (maxMakespan + Solution::_makespan_reference)
				/ Solution::_makespan_reference;

		double normalized_energy;
		normalized_energy = (energy + Solution::_energy_reference)
				/ Solution::_energy_reference;

		double fitness;
		fitness = (_pbm.getCurrentMakespanWeight() * normalized_makespan)
				+ (_pbm.getCurrentEnergyWeight() * normalized_energy);

		assert(!(fitness == INFINITY));
		assert(!(fitness == NAN));

		_current_fitness = fitness;
		_is_dirty = false;
	}

	return _current_fitness;
}

double Solution::getMakespan() {
	if (!_initialized) {
		return infinity();
	}

	if (_is_dirty) {
		getFitness();
	}

	return _current_makespan;
}

double Solution::getEnergy(double makespan) {
	if (!_initialized) {
		return infinity();
	}

	if (_is_dirty) {
		getFitness();
	}

	return _current_energy;
}

int Solution::length() const {
	return _pbm.getTaskCount();
}

unsigned int Solution::size() const {
	return (_pbm.getTaskCount() * sizeof(int)) + (_pbm.getMachineCount()
			* sizeof(int)) + sizeof(int);
}

int Solution::getTaskAssignment(const int taskId) const {
	assert(taskId >= 0);
	assert(taskId < _pbm.getTaskCount());

	return _taskAssignment[taskId];
}

int Solution::distanceTo(const Solution& solution) const {
	int distance = 0;

	for (int taskId = 0; taskId < _pbm.getTaskCount(); taskId++) {
		if (getTaskAssignment(taskId) != solution.getTaskAssignment(taskId)) {
			distance++;
		}
	}

	return distance;
}

bool Solution::findTask(const int taskId, int& foundMachineId,
		int& foundTaskPos) {

	foundMachineId = -1;
	foundTaskPos = -1;

	foundMachineId = getTaskAssignment(taskId);
	foundTaskPos = _machines[foundMachineId].getTaskPosition(taskId);

	return true;
}

double Solution::getMachineFitness(int machineId) {
	double makespan = getMakespan();
	double makespan_ratio = (makespan + Solution::_makespan_reference)
			/ Solution::_makespan_reference;
	double energy_ratio = (getEnergy(makespan) + Solution::_energy_reference)
			/ Solution::_energy_reference;

	return (_pbm.getCurrentMakespanWeight() * makespan_ratio)
			+ (_pbm.getCurrentEnergyWeight() * energy_ratio);
}

void Solution::doLocalSearch() {
	//if (DEBUG)
	cout << endl
			<< "[DEBUG] Solution::doLocalSearch begin <start> ========================================="
			<< endl;

	timespec ts;
	if (TIMING) {
		clock_gettime(CLOCK_REALTIME, &ts);
	}
	Solver::global_calls[TIMING_LS]++;

	double aux_pre_LS = getFitness();
	cout << "[DEBUG] Solution::doLocalSearch fitness: " << aux_pre_LS << endl;

	int max_steps = rand_int(3, 10);

	for (int iterations = 0; iterations < max_steps; iterations++) {
		int machineId;
		if (rand01() <= 0.8) {
			machineId = getMaxCostMachineId();
		} else {
			machineId = rand_int(0, this->machines().size() - 1);
		}

		if (this->machines()[machineId].countTasks() > 0) {
			// PALS aleatorio para HCSP.
			double mejorMovimientoFitness;
			int mejorMovimientoTaskPos, mejorMovimientoDestinoTaskPos,
					mejorMovimientoDestinoMachineId, mejorMovimientoTipo;

			double fitnessActual = getFitness();

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

					if (rand01() <= 0.8) {
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
							movimientoFitness = this->getFitness();
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

					if (rand01() <= 0.8) {
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
							movimientoFitness = this->getFitness();
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

	cout << "[DEBUG] Solution::doLocalSearch after fitness: " << getFitness()
			<< endl;
	cout << "[DEBUG] Solution::doLocalSearch improvement: " << getFitness()
			/ aux_pre_LS << endl;

	if (TIMING) {
		timespec ts_end;
		clock_gettime(CLOCK_REALTIME, &ts_end);

		double elapsed;
		elapsed = ((ts_end.tv_sec - ts.tv_sec) * 1000000.0) + ((ts_end.tv_nsec
				- ts.tv_nsec) / 1000.0);
		Solver::global_timing[TIMING_LS] += elapsed;
	}

	if (DEBUG)
		cout << endl << "[DEBUG] Solution::doLocalSearch begin <end>" << endl;
}

void Solution::doMutate() {
	if (DEBUG)
		cout << endl
				<< "[DEBUG] Solution::doMutate begin <start> ========================================="
				<< endl;

	timespec ts;

	if (TIMING) {
		clock_gettime(CLOCK_REALTIME, &ts);
	}

	Solver::global_calls[TIMING_MUTATE]++;

	for (int machineId = 0; machineId < _machines.size(); machineId++) {
		if (rand01() <= MUT_MAQ) {
			if (_machines[machineId].countTasks() == 0) {
				// La máquina no tiene tareas. Se le asigna la tarea que mejor puede ejecutar.
				{
					int bestTaskIdForMachine;
					bestTaskIdForMachine = _pbm.getBestTaskIdForMachine(
							machineId);

					moveTask(bestTaskIdForMachine, machineId);
				}
			} else {
				for (int selectedTaskPos = 0; selectedTaskPos
						< _machines[machineId].countTasks(); selectedTaskPos++) {
					if (rand01() < MUT_TASK) {
						int neighbourhood;
						neighbourhood = rand_int(0, 2);

						if (neighbourhood == 0) {
							// Se intercambia con la tarea que mejor puede ejecutarse en la máquina actual de
							// la máquina en la que mejor puede ejecutarse.

							// Obtengo la máquina que que mejor puede ejecutar la tarea.
							int selectedTaskId;
							selectedTaskId = _machines[machineId].getTask(
									selectedTaskPos);

							int bestMachineId;
							bestMachineId = _pbm.getBestMachineForTaskId(
									selectedTaskId);

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
											bestMachineId,
											minCostTaskPosOnMachine);
								} else {
									moveTask(selectedTaskId, bestMachineId);
								}
							}
						}

						if (neighbourhood == 1) {
							// Se intercambia con la tarea de la máquina con menor makespan que puede ejecutarse
							// más eficientemente en la máquina actual.

							// Obtengo la máquina que aporta un menor costo al total de la solución.
							int minCostMachineId;
							minCostMachineId = getMinCostMachineId();

							int selectedTaskId;
							selectedTaskId = _machines[machineId].getTask(
									selectedTaskPos);

							if (_machines[minCostMachineId].countTasks() > 0) {
								// Si la máquina destino tiene al menos una tarea, obtengo la tarea
								// con menor costo de ejecución en la máquina sorteada.
								int minCostTaskPosOnMachine;
								minCostTaskPosOnMachine
										= getMinDestinationCostTaskPosByMachine(
												minCostMachineId, machineId);

								// Hago un swap entre las tareas de las máquinas.
								swapTasks(machineId, selectedTaskPos,
										minCostMachineId,
										minCostTaskPosOnMachine);
							} else {
								moveTask(selectedTaskId, minCostMachineId);
							}
						}

						if (neighbourhood == 2) {
							int selectedTaskId;
							selectedTaskId = _machines[machineId].getTask(
									selectedTaskPos);

							int machineDstId = rand_int(0,
									pbm().getMachineCount() - 2);
							if (machineId == machineDstId)
								machineDstId++;

							moveTask(selectedTaskId, machineDstId);
						}
					}
				}
			}
		}
	}

	//_is_dirty = true;

	//if (DEBUG) cout << endl << "[DEBUG] Solution::mutate <END>" << endl;

	if (TIMING) {
		timespec ts_end;
		clock_gettime(CLOCK_REALTIME, &ts_end);

		double elapsed;
		elapsed = ((ts_end.tv_sec - ts.tv_sec) * 1000000.0) + ((ts_end.tv_nsec
				- ts.tv_nsec) / 1000.0);
		Solver::global_timing[TIMING_MUTATE] += elapsed;
	}

	if (DEBUG)
		cout << endl << "[DEBUG] Solution::doMutate begin <end>" << endl;
}

void Solution::addTask(const int machineId, const int taskId) {
	if (DEBUG)
		cout << "[DEBUG] Solution::addTask" << endl;
	_is_dirty = true;

	_taskAssignment[taskId] = machineId;
	_machines[machineId].addTask(taskId);
}

void Solution::swapTasks(int machineId1, int taskPos1, int machineId2,
		int taskPos2) {
	if (DEBUG)
		cout << "[DEBUG] Solution::swapTasks" << endl;
	_is_dirty = true;

	/*if (machines()[machineId1].getComputeTime() == getMakespan()
	 || machines()[machineId2].getComputeTime() == getMakespan()) {

	 _is_dirty = true;
	 }*/

	/*double machineEnergy1, machineEnergy2;
	 machineEnergy1 = machines()[machineId1].getTotalEnergyConsumption(
	 getMakespan());
	 machineEnergy2 = machines()[machineId2].getTotalEnergyConsumption(
	 getMakespan());*/

	if (machineId1 != machineId2) {
		int taskId1 = machines()[machineId1].getTask(taskPos1);
		int taskId2 = machines()[machineId2].getTask(taskPos2);

		_machines[machineId1].setTask(taskId2, taskPos1);
		_taskAssignment[taskId2] = machineId1;

		_machines[machineId2].setTask(taskId1, taskPos2);
		_taskAssignment[taskId1] = machineId2;
	}

	/*if (machines()[machineId1].getComputeTime() > getMakespan()
	 || machines()[machineId2].getComputeTime() > getMakespan()) {

	 _is_dirty = true;
	 }

	 if (!_is_dirty) {
	 _current_energy = _current_energy - machineEnergy1 - machineEnergy2
	 + machines()[machineId1].getTotalEnergyConsumption(
	 getMakespan())
	 + machines()[machineId2].getTotalEnergyConsumption(
	 getMakespan());

	 double normalized_makespan;
	 normalized_makespan = (getMakespan() + Solution::_makespan_reference)
	 / Solution::_makespan_reference;

	 double normalized_energy;
	 normalized_energy = (_current_energy + Solution::_energy_reference)
	 / Solution::_energy_reference;

	 double fitness;
	 fitness = (_pbm.getCurrentMakespanWeight() * normalized_makespan)
	 + (_pbm.getCurrentEnergyWeight() * normalized_energy);

	 _current_fitness = fitness;
	 }*/
}

void Solution::swapTasks(int taskId1, int taskId2) {
	if (DEBUG)
		cout << "[DEBUG] Solution::swapTasks" << endl;
	_is_dirty = true;

	int machineId1 = getTaskAssignment(taskId1);
	int machineId2 = getTaskAssignment(taskId2);

	if (machineId1 != machineId2) {
		_machines[machineId1].removeTask(_machines[machineId1].getTaskPosition(
				taskId1));
		_machines[machineId1].addTask(taskId2);
		_taskAssignment[taskId2] = machineId1;

		_machines[machineId2].removeTask(_machines[machineId2].getTaskPosition(
				taskId2));
		_machines[machineId2].addTask(taskId1);
		_taskAssignment[taskId1] = machineId2;
	}
}

void Solution::moveTask(const int taskId, const int machineId) {
	if (DEBUG)
		cout << "[DEBUG] Solution::moveTasks" << endl;
	_is_dirty = true;

	int machineIdOld = getTaskAssignment(taskId);

	if (machineId != machineIdOld) {
		_machines[machineIdOld].removeTask(
				_machines[machineIdOld].getTaskPosition(taskId));

		_machines[machineId].addTask(taskId);
		_taskAssignment[taskId] = machineId;
	}
}

char *Solution::to_String() const {
	if (DEBUG)
		cout << endl << "[DEBUG] Solution::to_String begin <start>" << endl;
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

	return rawChar;
	if (DEBUG)
		cout << endl << "[DEBUG] Solution::to_String begin <end>" << endl;
}

void Solution::to_Solution(char *_string_) {
	if (DEBUG)
		cout << endl << "[DEBUG] Solution::to_Solution <start>" << endl;

	int *raw = (int*) _string_;

	int machineSeparator = -1;
	int endMark = -2;

	bool endFound = false;

	int currentTask = 0;
	int currentMachine = 0;

	for (int pos = 0; pos < (_pbm.getTaskCount() + _pbm.getMachineCount() + 1)
			&& !endFound; pos++) {

		int currentValue;
		currentValue = raw[pos];

		if (currentValue == endMark) {
			endFound = true;
		} else if (currentValue == machineSeparator) {
			//assert(currentMachine < _pbm.getMachineCount());

			currentMachine++;
		} else {
			/*assert(currentValue >= 0);
			 assert(currentValue < _pbm.getTaskCount());
			 assert(currentMachine < _pbm.getMachineCount());*/

			addTask(currentMachine, currentValue);
			currentTask++;
		}
	}

	/*assert(_machines.size() == _pbm.getMachineCount());
	 assert(currentTask == _pbm.getTaskCount());
	 assert(endFound);*/

	_is_dirty = true;
	markAsInitialized();

	if (DEBUG)
		cout << endl << "[DEBUG] Solution::to_Solution <end>" << endl;
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
	double minCostMachineValue = _machines[0].getComputeTime();

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineCost;
		currentMachineCost = _machines[machineId].getComputeTime();

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
	double maxCostMachineValue = _machines[0].getComputeTime();

	for (int machineId = 1; machineId < machines().size(); machineId++) {
		double currentMachineCost;
		currentMachineCost = _machines[machineId].getComputeTime();

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
	double minCostTaskValue = _pbm.getTaskSSJCost(
			machines()[machineId].getTask(0), machineId);

	for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
		double currentTaskCost;
		currentTaskCost = _pbm.getTaskSSJCost(machines()[machineId].getTask(
				taskPos), machineId);

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
		double minCostTaskValue = _pbm.getTaskSSJCost(
				machines()[machineId].getTask(0), destinationMachineId);

		for (int taskPos = 1; taskPos < machines()[machineId].countTasks(); taskPos++) {
			double currentTaskCost;
			currentTaskCost = _pbm.getTaskSSJCost(
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

