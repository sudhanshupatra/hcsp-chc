#!/bin/bash

cd /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones

#EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec.hydra -rmk pbs /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan"
EXEC="mpiexec -mpich-p4-no-shmem /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan"

data[0]="A.u_c_hihi"
data[1]="A.u_c_hilo"
data[2]="A.u_c_lohi"
data[3]="A.u_c_lolo"
data[4]="A.u_i_hihi"
data[5]="A.u_i_hilo"
data[6]="A.u_i_lohi"
data[7]="A.u_i_lolo"
data[8]="A.u_s_hihi"
data[9]="A.u_s_hilo"
data[10]="A.u_s_lohi"
data[11]="A.u_s_lolo"
data[12]="B.u_c_hihi"
data[13]="B.u_c_hilo"
data[14]="B.u_c_lohi"
data[15]="B.u_c_lolo"
data[16]="B.u_i_hihi"
data[17]="B.u_i_hilo"
data[18]="B.u_i_lohi"
data[19]="B.u_i_lolo"
data[20]="B.u_s_hihi"
data[21]="B.u_s_hilo"
data[22]="B.u_s_lohi"
data[23]="B.u_s_lolo"

#for i in {0..23}
for i in {0..0}
do
	echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/CHC_LAN4.cfg" > /home/siturria/AE/trunk/AE/CHC/hcsp/tests/Config_LAN4_Test.cfg
	echo "/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/1024x32.CPrio/${data[i]}" >> /home/siturria/AE/trunk/AE/CHC/hcsp/tests/Config_LAN4_Test.cfg
	echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/lan4/${data[i]}_LAN4.sol" >> /home/siturria/AE/trunk/AE/CHC/hcsp/tests/Config_LAN4_Test.cfg
	time($EXEC /home/siturria/AE/trunk/AE/CHC/hcsp/tests/Config_LAN4_Test.cfg > /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/lan4/${data[i]}_LAN4.log)        
done
