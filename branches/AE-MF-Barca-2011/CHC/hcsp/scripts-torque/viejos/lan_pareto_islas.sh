#!/bin/bash

# Nombre del trabajo
#PBS -N ae_islas_fp

# Requerimientos
#PBS -l nodes=17,walltime=00:40:00

# Cola
#PBS -q publica

# Working dir
#PBS -d /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/

# Correo electronico
###PBS -M siturria@fing.edu.uy

# Email
#PBS -m abe
# n: no mail will be sent.
# a: mail is sent when the job is aborted by the batch system.
# b: mail is sent when the job begins execution.
# e: mail is sent when the job terminates.

# Output path
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/islas/
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/islas/

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

#Data="/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/Braun_et_al.CPrio/u_s_hilo.0"
Data="/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/2048x64.CPrio/B.u_s_hilo"
echo "==========================================================="
echo "Datos $DataFile"
echo "==========================================================="

for i in {0..19}
do
	echo ">>> Procesando ${i}"

	DEST_FOLDER="/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/islas/${i}"
	mkdir -p ${DEST_FOLDER} 

	echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/scripts_frente_pareto/islas.cfg" > Config_LAN_FP_ISLAS.cfg
	echo "${Data}" >> Config_LAN_FP_ISLAS.cfg
	echo "${DEST_FOLDER}/${i}.sol" >> Config_LAN_FP_ISLAS.cfg
	echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/pesos.txt" >> Config_LAN_FP_ISLAS.cfg

	OutputFile="${DEST_FOLDER}/${i}.log"
	
	time($EXEC Config_LAN_FP_ISLAS.cfg > ${OutputFile}) 
done
