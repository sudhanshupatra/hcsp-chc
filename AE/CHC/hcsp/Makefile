include ../environment

all: MainSeq MainLan

clean:
	rm -f MainLan MainSeq MainWan *.o *% *~

MainSeq: CHC.req.o CHC.pro.o MainSeq.o
	$(CXX) $(LDFLAGS) $^ $(LOADLIBES) $(CPPFLAGS) -o $@

MainLan: CHC.req.o CHC.pro.o MainLan.o
	$(CXX) $(LDFLAGS) $^ $(LOADLIBES) $(CPPFLAGS) -o $@

MainWan: CHC.req.o CHC.pro.o MainWan.o
	$(CXX) $(LDFLAGS) $^ $(LOADLIBES) $(CPPFLAGS) -o $@

LAN8:
	$(RUN) -np 4 /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainLan

LAN4:
	$(RUN) -np 4 /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan

LAN8C:
	$(RUN) -np 4 /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan

LAN4C:
	$(RUN) -np 4 /home/siturria/eclipse/c-c++-workspace/AE/CHC/hcsp/MainLan

LAN3:
	$(RUN) -np 3 /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainLan

LAN2:
	$(RUN) -np 2 /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainLan

WAN:
	$(RUN) -v  -p4pg pgfileWan MainWan

SEQ:
	./MainSeq CHC_SEQ.cfg ../../ProblemInstances/HCSP/1024x32.mod/A.u_i_hihi res/sol.txt

SEQP:
	./MainSeq CHC.cfg ../../ProblemInstances/HCSP/prioridades/simple res/sol.txt