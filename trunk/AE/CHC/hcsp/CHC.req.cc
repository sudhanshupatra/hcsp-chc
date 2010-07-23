#ifndef INC_REQ_CHC
#define INC_REQ_CHC
#include "CHC.hh"
#include <math.h>

using std::cout;



skeleton CHC
{

	// Problem ---------------------------------------------------------------

	Problem::Problem ():_taskCount(0), _machineCount(0),
			_expectedTimeToCompute(NULL), _tasksPriorities()
	{}

	// ===================================
	// Serialización del problema.
	// ===================================
	ostream& operator<< (ostream& output, const Problem& pbm)
	{
		output << endl << endl
			<< "Number of tasks: " << pbm._taskCount << endl
			<< "Number of machines: " << pbm._machineCount << endl
			<< endl;
		return output;
	}

	// ===================================
	// Deserialización del problema.
	// ===================================
	istream& operator>> (istream& input, Problem& pbm)
	{
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

			if (DEBUG) cout << "[DEBUG] Task: " << taskPos
					<< " => Priority: " << pbm._tasksPriorities[taskPos] << endl;
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
				cout << "[ERROR] no se pudo reservar memoria para las máquinas de la tarea " << taskPos << endl;
				show_message(7);
			}

			// Cargo el ETC de cada tarea en cada una de las máquinas.
			for (int machinePos = 0; machinePos < pbm._machineCount; machinePos++) {
				input.getline(buffer, MAX_BUFFER, '\n');
				sscanf(buffer, "%f", &pbm._expectedTimeToCompute[taskPos][machinePos]);

				if (DEBUG) cout << "[DEBUG] Task: " << taskPos << ", Machine: " << machinePos
					<< ", ETC: " << pbm._expectedTimeToCompute[taskPos][machinePos] << endl;
			}
		}

		return input;
	}

	Problem& Problem::operator=  (const Problem& pbm)
	{
		return *this;
	}

	bool Problem::operator== (const Problem& pbm) const
	{
		if (taskCount() != pbm.taskCount()) return false;
		return true;
	}

	bool Problem::operator!= (const Problem& pbm) const
	{
		return !(*this == pbm);
	}

	Direction Problem::direction() const
	{
		//return maximize;
		return minimize;
	}

	int Problem::taskCount() const {
		return _taskCount;
	}

	int Problem::machineCount() const {
		return _machineCount;
	}

	float Problem::expectedTimeToCompute(const int& task, const int& machine) const {
		return _expectedTimeToCompute[task][machine];
	}

	int Problem::tasksPriorities(const int& task) const {
		return _tasksPriorities[task];
	}

	Problem::~Problem()
	{}

	// Solution --------------------------------------------------------------

	SolutionMachine::SolutionMachine(int machineId): _tasks(), _machineId(machineId) {
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
		if (taskPos < _tasks.size()) {
			return _tasks[taskPos];
		} else {
			return -1;
		}
	}

	int SolutionMachine::countTasks() const {
		return _tasks.size();
	}

	Solution::Solution (const Problem& pbm):_pbm(pbm), _machines(), _initialized(false)
	{
		_machines.reserve(pbm.machineCount());

		for (int machineId = 0; machineId < pbm.machineCount(); machineId++) {
			_machines.push_back(*(new SolutionMachine(machineId)));
		}
	}

	const Problem& Solution::pbm() const
	{
		return _pbm;
	}

	Solution::Solution(const Solution& sol):_pbm(sol.pbm())
	{
		*this=sol;
	}

	// ===================================
	// Deserialización de la solución.
	// ===================================
	istream& operator>> (istream& is, Solution& sol)
	{
		//for (int i=0;i<sol.pbm().dimension();i++)
		//	is >> sol._var[i];
		
		return is;
	}

	// ===================================
	// Deserialización de la solución.
	// ===================================
	NetStream& operator >> (NetStream& ns, Solution& sol)
	{
		//for (int i=0;i<sol._var.size();i++)
		//	ns >> sol._var[i];

		return ns;
	}

	// ===================================
	// Serialización de la solución.
	// ===================================
	ostream& operator<< (ostream& os, const Solution& sol)
	{
		//for (int i=0;i<sol.pbm().dimension();i++)
		//	os << " " << sol._var[i];

		return os;
	}

	// ===================================
	// Serialización de la solución.
	// ===================================
	NetStream& operator << (NetStream& ns, const Solution& sol)
	{
		//for (int i=0;i<sol._var.size();i++)
		//	ns << sol._var[i];

		return ns;
	}

 	Solution& Solution::operator= (const Solution &sol)
	{
		//_var=sol._var;
 		_machines = sol._machines;
 		_initialized = sol._initialized;

// 		if (DEBUG) cout << endl << "[DEBUG] Solution::operator= this fitness: " << fitness() << endl;
// 		if (DEBUG) cout << "[DEBUG] Solution::operator= sol fitness: " << sol.fitness() << endl;

		return *this;
	}

	bool Solution::operator== (const Solution& sol) const
	{
		if (sol.pbm() != _pbm) return false;
		return true;
	}

	bool Solution::operator!= (const Solution& sol) const
	{
		return !(*this == sol);
	}

	// ===================================
	// Inicializo la solución.
	// ===================================
	void Solution::initialize()
	{
		_initialized = true;

		int startTask = rand_int(0, _pbm.taskCount()-1);
		int direction = rand_int(0, 1);
		if (direction == 0) direction = -1;

		if (DEBUG) {
//			cout << "[DEBUG] Initialize()" << endl;
//			cout << "[DEBUG] startTask: " << startTask << endl;
//			cout << "[DEBUG] direction: " << direction << endl;
		}

		int currentTask;
		for (int taskOffset = 0; taskOffset < _pbm.taskCount(); taskOffset++) {
			currentTask = startTask + (direction * taskOffset);
			if (currentTask < 0) currentTask = _pbm.taskCount() + currentTask;
			currentTask = currentTask % (_pbm.taskCount()+1);

			int currentMachine;
			currentMachine = rand_int(0, _pbm.machineCount()-1);

//			if (DEBUG) {
//				cout << "[DEBUG] Task: " << currentTask << " sent to Machine: " << currentMachine << endl;
//			}

			_machines[currentMachine].addTask(currentTask);
		}
	}

	// ===================================
	// Fitness de la solución.
	// ===================================
	double Solution::fitness () const
	{
		if (!_initialized) {
			//if (DEBUG) cout << endl << "[DEBUG] Solution fitness: infinity" << endl;
			return infinity();
		}

		double fitness = 0.0;

		for (int machineId = 0; machineId < _pbm.machineCount(); machineId++) {
			int machineComputeCost;
			machineComputeCost = 0;

			//if (DEBUG) cout << "[DEBUG] Solution::fitness machineId: " << machineId << endl;

			for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
				int taskId;
				taskId = _machines[machineId].getTask(taskPos);

//				if (DEBUG) cout << "[DEBUG] Solution::fitness taskId: " << taskId << endl;
//				if (DEBUG) cout << "[DEBUG] Solution::fitness taskPos: " << taskPos << endl;

				double computeCost;
				computeCost = _pbm.expectedTimeToCompute(taskId, machineId);

//				if (DEBUG) cout << "[DEBUG] Solution::fitness computeCost: " << computeCost << endl;
//				if (DEBUG) cout << "[DEBUG] Solution::fitness taskPriority: " << _pbm.tasksPriorities(taskId) << endl;

				double priorityCost;
				priorityCost = 0.0;

				if (taskPos > 0) {
					priorityCost += machineComputeCost / _pbm.tasksPriorities(taskId);
				}

				//if (DEBUG) cout << "[DEBUG] Solution::fitness priorityCost: " << priorityCost << endl;

				machineComputeCost += computeCost;
				fitness += (computeCost + priorityCost);
				//if (DEBUG) cout << "[DEBUG] Solution::fitness partial fitness: " << fitness << endl;
			}
		}

		//if (DEBUG) cout << endl << "[DEBUG] Solution fitness: " << fitness << endl;
		return fitness;
	}

	int Solution::length() const {
		return _pbm.taskCount();
	}

	int Solution::distanceTo(const Solution& solution) const {
		int distance = 0;

		for (int machineId = 0; machineId < _machines.size(); machineId++) {
			for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
				int taskId;
				taskId = _machines[machineId].getTask(taskPos);

				if (solution._machines[machineId].countTasks() >= taskPos) {
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

		if (DEBUG) cout << endl << "[DEBUG] Solution::distanceTo: " << distance << endl;
		return distance;
	}

	int Solution::findTask(const int taskId, int& foundMachineId, int& foundTaskPos) const {
		foundMachineId = -1;
		foundTaskPos = -1;

		for (int machineId = 0; machineId < _machines.size(); machineId++) {
			for (int taskPos = 0; taskPos < _machines[machineId].countTasks(); taskPos++) {
				if (_machines[machineId].getTask(taskPos) == taskId) {
					foundMachineId = machineId;
					foundTaskPos = taskPos;
					return 1;
				}
			}
		}

		return 0;
	}

	void Solution::executeTaskAt(const int taskId, const int machineId, const int taskPos) {
		_machines[machineId].setTask(taskId, taskPos);
	}

	void Solution::swapTasks(Solution& solution, const int taskId) {
		if (DEBUG) cout << endl << "[DEBUG] Solution::swapTasks" << endl;

		int machine1, machine2, taskPos1, taskPos2;

		findTask(taskId, machine1, taskPos1);
		solution.findTask(taskId, machine2, taskPos2);

		executeTaskAt(taskId, machine2, taskPos2);
		solution.executeTaskAt(taskId, machine1, taskPos1);
	}

	bool Solution::equalTasks(Solution& solution, const int taskId) const {
		if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks taskId " << taskId << endl;

		int machine1, machine2, taskPos1, taskPos2;

		if (findTask(taskId, machine1, taskPos1) && solution.findTask(taskId, machine2, taskPos2)) {
			if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks encontrados" << endl;
			if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks sol1 machineId: " << machine1 << " taskPos: "  << taskPos1 << endl;
			if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks sol2 machineId: " << machine2 << " taskPos: "  << taskPos2 << endl;

			bool equal = (machine1 == machine2) && (taskPos1==taskPos2);
			if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks equals " << equal << endl;
			return equal;
		} else {
			if (DEBUG) cout << endl << "[DEBUG] Solution::equalTasks no encontré la tarea?" << endl;
			return true;
		}
	}

	char *Solution::to_String() const
	{
		//TODO: implementar solución a string
		//return (char *)_var.get_first();
		return "[INFO] Solution...\n";
	}


	void Solution::to_Solution(char *_string_)
	{
		//TODO: implementar string a soluctión
		/*int *ptr=(int *)_string_;
		for (int i=0;i<_pbm.dimension();i++)
		{
			_var[i]=*ptr;
			ptr++;
		}*/
	}

	const vector<struct SolutionMachine>& Solution::machines() const {
		return _machines;
	}

	Solution::~Solution()
	{}

	// UserStatistics -------------------------------------------------------

	UserStatistics::UserStatistics ()
	{}

	ostream& operator<< (ostream& os, const UserStatistics& userstat)
	{
		os << "\n---------------------------------------------------------------" << endl;
		os << "                   STATISTICS OF TRIALS                   	 " << endl;
		os << "------------------------------------------------------------------" << endl;

		for (int i=0;i< userstat.result_trials.size();i++)
		{
			os << endl
			   << userstat.result_trials[i].trial
			   << "\t" << userstat.result_trials[i].best_cost_trial
			   << "\t\t" << userstat.result_trials[i].worst_cost_trial
			   << "\t\t\t" << userstat.result_trials[i].nb_evaluation_best_found_trial
			   << "\t\t\t" << userstat.result_trials[i].nb_iteration_best_found_trial
			   << "\t\t\t" << userstat.result_trials[i].time_best_found_trial
			   << "\t\t" << userstat.result_trials[i].time_spent_trial;
		}
		os << endl << "------------------------------------------------------------------" << endl;
		return os;
	}

	UserStatistics& UserStatistics::operator= (const UserStatistics& userstats)
	{
		result_trials=userstats.result_trials;
		return (*this);
	}

	void UserStatistics::update(const Solver& solver)
	{
		if( (solver.pid()!=0) || (solver.end_trial()!=true)
		  || ((solver.current_iteration()!=solver.setup().nb_evolution_steps())
		       && !terminateQ(solver.pbm(),solver,solver.setup())))
			return;

		struct user_stat *new_stat;

		if ((new_stat=(struct user_stat *)malloc(sizeof(struct user_stat)))==NULL)
			show_message(7);
		new_stat->trial         				 = solver.current_trial();
		new_stat->nb_evaluation_best_found_trial = solver.evaluations_best_found_in_trial();
		new_stat->nb_iteration_best_found_trial  = solver.iteration_best_found_in_trial();
		new_stat->worst_cost_trial     			 = solver.worst_cost_trial();
		new_stat->best_cost_trial     			 = solver.best_cost_trial();
		new_stat->time_best_found_trial			 = solver.time_best_found_trial();
		new_stat->time_spent_trial 				 = solver.time_spent_trial();

		result_trials.append(*new_stat);
	}

	void UserStatistics::clear()
	{
		result_trials.remove();
	}

	UserStatistics::~UserStatistics()
	{
		result_trials.remove();
	}

	//  User_Operator:Intra_operator ---------------------------------------------------------

	User_Operator::User_Operator(const unsigned int _number_op):Intra_Operator(_number_op)
	{}

	void User_Operator::execute(Rarray<Solution*>& sols) const
	{}

	void User_Operator::setup(char line[MAX_BUFFER])
	{}

	Intra_Operator *User_Operator::create(const unsigned int _number_op)
	{
		return new User_Operator(_number_op);
	}

	ostream& operator<< (ostream& os, const User_Operator&  u_op)
	{
		 os << "User Operator.";
		 return os;
	}

	void User_Operator::RefreshState(const StateCenter& _sc) const
	{}

	void User_Operator::UpdateFromState(const StateCenter& _sc)
	{}

	User_Operator::~User_Operator()
	{}


// StopCondition_1 -------------------------------------------------------------------------------------

	StopCondition_1::StopCondition_1():StopCondition()
	{}

	bool StopCondition_1::EvaluateCondition(const Problem& pbm,const Solver& solver, const SetUpParams& setup)
	{
		//return ((int)solver.best_cost_trial() == pbm.dimension());
		return false;
	}

	StopCondition_1::~StopCondition_1()
	{}


	//------------------------------------------------------------------------
	// Specific methods ------------------------------------------------------
	//------------------------------------------------------------------------

	bool terminateQ (const Problem& pbm, const Solver& solver,
			 const SetUpParams& setup)
	{
		/*StopCondition_1 stop;
		return stop.EvaluateCondition(pbm,solver,setup);*/

		return false;
	}
}
#endif

