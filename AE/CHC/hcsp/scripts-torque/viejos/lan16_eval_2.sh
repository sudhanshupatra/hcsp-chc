#!/bin/bash

# Nombre del trabajo
#PBS -N ae_lan16_eval2

# Requerimientos
#PBS -l nodes=16,walltime=40:00:00

# Cola
#PBS -q publica

# Working dir
#PBS -d /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/

# Correo electronico
#PBS -M siturria@fing.edu.uy

# Email
#PBS -m abe
# n: no mail will be sent.
# a: mail is sent when the job is aborted by the batch system.
# b: mail is sent when the job begins execution.
# e: mail is sent when the job terminates.

# Output path
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/evaluacion/lan16/
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/evaluacion/lan16/

#PBS -V

echo Job Name: $PBS_JOBNAME
echo Working directory: $PBS_O_WORKDIR
echo Queue: $PBS_QUEUE
echo Cantidad de tasks: $PBS_TASKNUM
echo Home: $PBS_O_HOME
echo Puerto del MOM: $PBS_MOMPORT
echo Nombre del usuario: $PBS_O_LOGNAME
echo Idioma: $PBS_O_LANG
echo Cookie: $PBS_JOBCOOKIE
echo Offset de numero de nodos: $PBS_NODENUM
echo Shell: $PBS_O_SHELL
#echo JobID: $PBS_O_JOBID
echo Host: $PBS_O_HOST
echo Cola de ejecucion: $PBS_QUEUE
echo Archivo de nodos: $PBS_NODEFILE
echo Path: $PBS_O_PATH

echo
cd $PBS_O_WORKDIR
echo Current path: 
pwd
echo
echo Nodos:
cat $PBS_NODEFILE
echo
# Define number of processors
echo Cantidad de nodos:
NPROCS=`wc -l < $PBS_NODEFILE`
echo $NPROCS
echo

EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec.hydra -rmk pbs /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan"
#EXEC="mpiexec -mpich-p4-no-shmem ../MainLan"

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

for i in {0..23}
do
	CfgFile="/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/scripts_evaluacion/chc_lan16.cfg"
	DataFile="/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/1024x32.CPrio/${data[i]}"
	OutputFile="/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/evaluacion/lan16/1024x32/${data[i]}"
	PesosFile="/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/pesos_fijos.txt"
	
	echo "Datos $DataFile"
	echo "CfgFile $CfgFile"
	cat $CfgFile
		
	echo "${CfgFile}" > Config_LAN16_eval2.cfg
	echo "${DataFile}" >> Config_LAN16_eval2.cfg
	echo "${OutputFile}.sol" >> Config_LAN16_eval2.cfg
	echo "${PesosFile}" >> Config_LAN16_eval2.cfg
	
	time($EXEC Config_LAN16_eval2.cfg > $OutputFile.log) 
done
