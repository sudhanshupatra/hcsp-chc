#ifndef INC_REQ_CHC
#define INC_REQ_CHC
#include "CHC.hh"
#include <math.h>

using namespace std;

skeleton CHC
{

	// Problem ---------------------------------------------------------------

	Problem::Problem ():_taskCount(0), _machineCount(0), _expectedTimeToCompute(NULL)
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

				/*
				cout << "[DEBUG] Task: " << taskPos << ", Machine: " << machinePos
					<< ", ETC: " << pbm._expectedTimeToCompute[taskPos][machinePos] << endl;
				*/
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
		if (dimension() != pbm.dimension()) return false;
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

	int Problem::dimension() const
	{
		return _taskCount;
	}

	int Problem::taskCount() const {
		return _taskCount;
	}

	int Problem::machineCount() const {
		return _machineCount;
	}

	float Problem::expectedTimeToCompute(int task, int machine) const {
		return _expectedTimeToCompute[task][machine];
	}

	Problem::~Problem()
	{}

	// Solution --------------------------------------------------------------

	Solution::Solution (const Problem& pbm):_pbm(pbm), _var(0)
	{
		this->_machines = Rarray<Rlist<int> >(_pbm.dimension());

		for (int machinePos = 0; machinePos < this->_machines.size(); machinePos++) {
			this->_machines[machinePos] = Rlist<int>();
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
		for (int i=0;i<sol.pbm().dimension();i++)
			is >> sol._var[i];
		
		return is;
	}

	// ===================================
	// Deserialización de la solución.
	// ===================================
	NetStream& operator >> (NetStream& ns, Solution& sol)
	{
		for (int i=0;i<sol._var.size();i++)
			ns >> sol._var[i];
		return ns;
	}

	// ===================================
	// Serialización de la solución.
	// ===================================
	ostream& operator<< (ostream& os, const Solution& sol)
	{
		for (int i=0;i<sol.pbm().dimension();i++)
			os << " " << sol._var[i];
		return os;
	}

	// ===================================
	// Serialización de la solución.
	// ===================================
	NetStream& operator << (NetStream& ns, const Solution& sol)
	{
		for (int i=0;i<sol._var.size();i++)
			ns << sol._var[i];
		return ns;
	}

 	Solution& Solution::operator= (const Solution &sol)
	{
		_var=sol._var;

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
		int startTask = rand_int(0, _pbm.dimension()-1);
		int direction = rand_int(0, 1);
		if (direction == 0) direction = -1;

		int currentTask;
		for (int taskOffset = 0; taskOffset < _pbm.dimension(); taskOffset++) {
			currentTask = startTask + (direction * taskOffset);
			if (currentTask < 0) currentTask = _pbm.dimension() + currentTask;

			currentTask = currentTask % (_pbm.dimension()+1);

			int currentMachine;
			currentMachine = rand_int(0, _pbm.machineCount()-1);
			this->_machines[currentMachine].append(currentTask);
		}
	}

	// ===================================
	// Fitness de la solución.
	// ===================================
	double Solution::fitness () const
	{
        double fitness = 0.0;

		if(_var[0] == 2) return 0.0;
			
		for (int i=0;i<_var.size();i++)
			fitness += _var[i];

		return fitness;
	}


	char *Solution::to_String() const
	{
		return (char *)_var.get_first();
	}


	void Solution::to_Solution(char *_string_)
	{
		int *ptr=(int *)_string_;
		for (int i=0;i<_pbm.dimension();i++)
		{
			_var[i]=*ptr;
			ptr++;
		}
	}

	unsigned int Solution::size() const
	{
		return (_pbm.dimension() * sizeof(int));
	}

	int Solution::lengthInBits() const
	{
		return _pbm.dimension();
	}

	void Solution::flip(const int index)
	{
			_var[index] = 1 - _var[index]; 
	}

	bool Solution::equalb(const int index,Solution &s)
	{
		return _var[index] == s._var[index];
	}

	void Solution::swap(const int index, Solution &s)
	{
		int aux = s._var[index];
		s._var[index] = _var[index];
		_var[index] = aux;		
	}

	void Solution::invalid()
	{
			_var[0] = 2;
	}

	int& Solution::var(const int index)
	{
		return _var[index];
	}


	Rarray<int>& Solution::array_var()
	{
		return _var;
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
		return ((int)solver.best_cost_trial() == pbm.dimension());
	}

	StopCondition_1::~StopCondition_1()
	{}


	//------------------------------------------------------------------------
	// Specific methods ------------------------------------------------------
	//------------------------------------------------------------------------

	bool terminateQ (const Problem& pbm, const Solver& solver,
			 const SetUpParams& setup)
	{
		StopCondition_1 stop;
		return stop.EvaluateCondition(pbm,solver,setup);
	}
}
#endif

