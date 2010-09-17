#!/bin/bash

# Nombre del trabajo
#PBS -N ae_lan4_cal

# Requerimientos
#PBS -l nodes=1:cpu8:ppn=4,walltime=100:00:00

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
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/calibracion/lan4/
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/calibracion/lan4/

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

data[0]="u_c_hilo.0"
data[1]="u_c_lohi.0"
data[2]="u_s_hilo.0"
data[3]="u_s_lohi.0"

Poblacion[0]=5
Poblacion[1]=10
Poblacion[2]=15
Poblacion[3]=20
Poblacion[4]=30

Cruzamiento[0]=0.7
Cruzamiento[1]=0.8
Cruzamiento[2]=0.9
Cruzamiento[3]=1.0

Mutacion[0]=0.5
Mutacion[1]=0.7
Mutacion[2]=0.9
Mutacion[3]=1.0

EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec.hydra -rmk pbs /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan"

for indexP in {0..4}
do
	for indexC in {0..3}
	do
		for indexM in {0..3}
		do
			echo "Población ${Poblacion[indexP]}"
			echo "Cruzamiento ${Cruzamiento[indexC]}"
			echo "Mutación ${Mutacion[indexM]}"

			for i in {0..3}
			do
				CfgFile="chc_${Poblacion[indexP]}_${Cruzamiento[indexC]}_${Mutacion[indexM]}.cfg"
				DataFile="../../ProblemInstances/HCSP/Braun_et_al.mod/${data[i]}"
				OutputFile="calibracion/lan4/$DataFile_$CfgFile"
				
				echo "Datos $DataFile"
				
				echo "$CfgFile" > Config_LAN4.cfg
				echo "$DataFile" >> Config_LAN4.cfg
				echo "$OutputFile.sol" >> Config_LAN4.cfg
				
				time($EXEC Config_LAN4.cfg > $OutputFile.log)    
			done
		done
	done
done
