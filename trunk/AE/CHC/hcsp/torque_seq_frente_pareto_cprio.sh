#!/bin/bash

# Nombre del trabajo
#PBS -N ae_seq_cprio_fp

# Requerimientos
#PBS -l nodes=1:cpu8,walltime=01:00:00

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
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/frente_pareto/seq_cprio/
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/frente_pareto/seq_cprio/

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

Data="u_s_hilo.0"
Poblacion=10
Cruzamiento=1.0
Mutacion=1.0

for indexP in {0..2}
do
	CfgFile="scripts_frente_pareto/chc_$indexP.cfg"
	DataFile="../../ProblemInstances/HCSP/Braun_et_al.CPrio/$Data"
	OutputFile="frente_pareto/seq_cprio/$indexP"
	
	echo "Datos $DataFile"
	
	time(./MainSeq $CfgFile $DataFile $OutputFile.sol > $OutputFile.log)    
done
