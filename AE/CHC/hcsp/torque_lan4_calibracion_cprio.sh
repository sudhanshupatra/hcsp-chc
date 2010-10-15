#!/bin/bash

# Nombre del trabajo
#PBS -N ae_lan4_cprio_cal

# Requerimientos
#PBS -l nodes=1:cpu8:ppn=4,walltime=04:00:00

# Cola
#PBS -q publica

# Working dir
#PBS -d /home/siturria/AE/trunk/AE/CHC/hcsp/

# Correo electronico
#PBS -M siturria@fing.edu.uy

# Email
#PBS -m abe
# n: no mail will be sent.
# a: mail is sent when the job is aborted by the batch system.
# b: mail is sent when the job begins execution.
# e: mail is sent when the job terminates.

# Output path
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/calibracion/lan4_cprio/
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/calibracion/lan4_cprio/

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

data[0]="B.u_c_hilo"
data[1]="B.u_c_lohi"
data[2]="B.u_s_hilo"
data[3]="B.u_s_lohi"
data[4]="B.u_i_hilo"
data[5]="B.u_i_lohi"

Poblacion=15
Cruzamiento=0.8
Mutacion=0.9

EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec.hydra -rmk pbs /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan"

for i in {0..5}
do
	CfgFile="scripts_calibracion/chc_${Poblacion}_${Cruzamiento}_${Mutacion}.cfg"
	DataFile="../../ProblemInstances/HCSP/2048x64.CPrio/${data[i]}"
	OutputFile="calibracion/lan4_cprio/${data[i]}"
	
	echo "Datos $DataFile"
	
	echo "$CfgFile" > Config_LAN4.cfg
	echo "$DataFile" >> Config_LAN4.cfg
	echo "$OutputFile.sol" >> Config_LAN4.cfg
	
	time($EXEC Config_LAN4.cfg > $OutputFile.log)    
done
