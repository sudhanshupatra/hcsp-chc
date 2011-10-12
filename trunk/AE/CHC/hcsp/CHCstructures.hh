#ifndef INC_CHC_mallba_hh
#define INC_CHC_mallba_hh

#include <iostream>
#include <fstream>
#include <math.h>
#include <values.h>
#include <Mallba/Rlist.h>
#include <Mallba/Rarray.h>
#include <Mallba/Messages.h>
#include <Mallba/mallba.hh>
#include <Mallba/States.hh>
#include <Mallba/random.hh>
#include <Mallba/time.hh>
#include <Mallba/netstream.hh>
#include <assert.h>

#define skeleton namespace
#define requires
#define provides
#define hybridizes(x)
#define inherits typedef
#define as

#define DEBUG false
#define TIMING true

#define TIMING_MIGRATION 0
#define TIMING_MUTATE 1
#define TIMING_LS 2
#define TIMING_INIT 3
#define TIMING_CROSS 4

struct individual // index of a individual in the population and its fitness
{
	int    index;
	double fitness;
	double sel_parameter;
	bool   change;
};	

#endif
