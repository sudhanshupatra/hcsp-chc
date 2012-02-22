#!/bin/bash

# Nombre del trabajo
#PBS -N ae_lan16_eval1

# Requerimientos
#PBS -l nodes=1:class2:ppn=24,walltime=20:00:00

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

cfg[0]="config-0512.cfg"
cfg[1]="config-1024.cfg"
cfg[2]="config-2048.cfg"
cfg[3]="config-4096.cfg"
cfg[4]="config-8192.cfg"

inst_path[0]="/home/siturria/instancias/512.M"
inst_path[1]="/home/siturria/instancias/1024.M"
inst_path[2]="/home/siturria/instancias/2048.M"
inst_path[3]="/home/siturria/instancias/4096.M"
inst_path[4]="/home/siturria/instancias/8192.M"

data[0]="A.u_i_hihi"
data[1]="A.u_i_lohi"
data[2]="B.u_i_hihi"
data[3]="B.u_i_lohi"

BASE_PATH="/home/siturria/AE/trunk/AE/CHC/hcsp"

for c in {0..4}
do
    mkdir -p ${BASE_PATH}/ejecuciones/evaluacion/lan24/${cfg[c]}

    for i in {0..3}
    do
    	CfgFile="${BASE_PATH}/ejecuciones/${cfg[c]}"
    	DataFile="${inst_path[c]}/${data[i]}"
    	OutputFile="${BASE_PATH}/ejecuciones/evaluacion/lan24/${cfg[c]}/${data[i]}"
    	PesosFile="${BASE_PATH}/ejecuciones/pesos_8.txt"
    	
    	echo "Datos $DataFile"
    	echo "CfgFile $CfgFile"
    	cat $CfgFile
    		
    	echo "${CfgFile}" > Config_LAN24_eval.cfg
    	echo "${DataFile}" >> Config_LAN24_eval.cfg
    	echo "${OutputFile}.sol" >> Config_LAN24_eval.cfg
    	echo "${PesosFile}" >> Config_LAN24_eval.cfg
    	
    	time($EXEC Config_LAN24_eval.cfg > $OutputFile.log) 
    done
done