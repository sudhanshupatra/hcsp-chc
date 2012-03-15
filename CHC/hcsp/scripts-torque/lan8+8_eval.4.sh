#!/bin/bash

# Nombre del trabajo
#PBS -N ae_8+8_4

# Requerimientos
#PBS -l nodes=2:class0:ppn=8,walltime=40:00:00

# Cola
#PBS -q publica

# Working dir
#PBS -d /home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp/ejecuciones

# Correo electronico
#PBS -M siturria@fing.edu.uy

# Email
#PBS -m abe
# n: no mail will be sent.
# a: mail is sent when the job is aborted by the batch system.
# b: mail is sent when the job begins execution.
# e: mail is sent when the job terminates.

# Output path
#PBS -e /home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp/ejecuciones/evaluacion/lan8+8_4/
#PBS -o /home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp/ejecuciones/evaluacion/lan8+8_4/

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

EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec.hydra -rmk pbs /home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp/MainLan"
#EXEC="mpiexec -mpich-p4-no-shmem ../MainLan"

cfg[0]="config-0512.cfg"
cfg[1]="config-1024.cfg"
cfg[2]="config-2048.cfg"
cfg[3]="config-4096.cfg"
cfg[4]="config-8192.cfg"

dim_tasks[0]="512"
dim_tasks[1]="1024"
dim_tasks[2]="2048"
dim_tasks[3]="4096"
dim_tasks[4]="8192"

dim_machines[0]="16"
dim_machines[1]="32"
dim_machines[2]="48"
dim_machines[3]="64"
dim_machines[4]="128"

inst_path[0]="/home/siturria/instancias/512x16.M"
inst_path[1]="/home/siturria/instancias/1024x32.M"
inst_path[2]="/home/siturria/instancias/2048x64.M"
inst_path[3]="/home/siturria/instancias/4096x128.M"
inst_path[4]="/home/siturria/instancias/8192x256.M"

data[0]="A.u_i_hihi"
data[1]="A.u_i_lohi"
data[2]="A.u_i_hilo"
data[3]="A.u_i_lolo"
data[4]="B.u_i_hihi"
data[5]="B.u_i_lohi"
data[6]="B.u_i_hilo"
data[7]="B.u_i_lolo"

BASE_PATH="/home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp"

for c in {0..0}
do
    for i in {0..7}
    do
    	for (( j=0 ; j<5 ; j++ ))
    	do
    	    	CfgFile="${BASE_PATH}/ejecuciones/${cfg[c]}"
    	    	DataFile="${inst_path[c]}/${data[i]}"
    	    	OutputPath="${BASE_PATH}/ejecuciones/evaluacion/lan8+8_4/${cfg[c]}/${data[i]}/${j}"
    	    	PesosFile="${BASE_PATH}/ejecuciones/pesos_16.txt"
        	
    	    	mkdir -p ${OutputPath}
    
                echo "Datos $DataFile"
    	    	echo "CfgFile $CfgFile"
    	    	#cat $CfgFile
        		
    	    	echo "${CfgFile}" > Config_LAN8+8_4_eval.cfg
    	    	echo "${DataFile}" >> Config_LAN8+8_4_eval.cfg
    	    	echo "${OutputPath}/${j}.sol" >> Config_LAN8+8_4_eval.cfg
    	    	echo "${PesosFile}" >> Config_LAN8+8_4_eval.cfg
                echo "${dim_tasks[c]}" >> Config_LAN8+8_4_eval.cfg
            	echo "${dim_machines[c]}" >> Config_LAN8+8_4_eval.cfg
        	
    	    	time($EXEC Config_LAN8+8_4_eval.cfg > ${OutputPath}/${j}.log) 
    	        echo "==============================================="
    	done
    done
done

