#!/bin/bash

# Nombre del trabajo
#PBS -N ae_lan24_e.1

# Requerimientos
#PBS -l nodes=1:class2:ppn=24,walltime=36:00:00

# Cola
#PBS -q publica

# Working dir
#PBS -d /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/resultados/evaluacion

# Correo electronico
#PBS -M siturria@fing.edu.uy

# Email
#PBS -m abe
# n: no mail will be sent.
# a: mail is sent when the job is aborted by the batch system.
# b: mail is sent when the job begins execution.
# e: mail is sent when the job terminates.

# Output path
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/resultados/evaluacion/
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/resultados/evaluacion/

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

ROOT_PATH="/home/siturria/AE/trunk/AE"
EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec.hydra -rmk pbs ${ROOT_PATH}/CHC/hcsp/MainLan.1"
MALLBA_CONFIG="${ROOT_PATH}/CHC/hcsp/ejecuciones/scripts_evaluacion/config.cfg"
ITERACIONES=20

for peso in {3..3}
do
	PROJECT_NAME="lan24.eval.1"
	PESOS_PATH="${ROOT_PATH}/CHC/hcsp/ejecuciones/scripts_evaluacion/pesos.${peso}"

	for scenario in {0,3,1,8,2,4}
	do
		for workload in {0,1,10,11,20,21,31,32}
		do
			for priorities in {0..0}
			do
				#
				# Itero entre todas las instancias del problema a resolver.
				#
				echo ">>> Procesando scenario.${scenario} workload.${workload} priorities.${priorities}"
				
				SCENARIO_FILE="${ROOT_PATH}/ProblemInstances/HCSP-3O-MPE/instances/scenario.${scenario}"
				WORKLOAD_FILE="${ROOT_PATH}/ProblemInstances/HCSP-3O-MPE/instances/workload.${workload}"
				PRIORITIES_FILE="${ROOT_PATH}/ProblemInstances/HCSP-3O-MPE/instances/priorities.${priorities}"
				
				BASE_FOLDER="${ROOT_PATH}/CHC/hcsp/ejecuciones/resultados/evaluacion/${PROJECT_NAME}/s.${scenario}_w.${workload}_p.${priorities}"
				mkdir -p ${BASE_FOLDER}
				
				CONFIG_FILE="${BASE_FOLDER}/config.cfg"
			
				#
				# Cada instancia la resuelvo 30 veces.
				#
				for (( j=0 ; j<${ITERACIONES} ; j++))
				do
					echo "${j}"
			
					DEST_FOLDER="${BASE_FOLDER}/${j}"
					mkdir -p ${DEST_FOLDER} 
			
					echo "${MALLBA_CONFIG}" > ${CONFIG_FILE}
					echo "${SCENARIO_FILE}" >> ${CONFIG_FILE}
					echo "${WORKLOAD_FILE}" >> ${CONFIG_FILE}
					echo "${PRIORITIES_FILE}" >> ${CONFIG_FILE}
					echo "${DEST_FOLDER}/${j}.sol" >> ${CONFIG_FILE}
					echo "${PESOS_PATH}" >> ${CONFIG_FILE}
					echo "512" >> ${CONFIG_FILE}
					echo "16" >> ${CONFIG_FILE}
				
					OUTPUT_FILE="${DEST_FOLDER}/${j}.log"
					
					time(${EXEC} ${CONFIG_FILE} > ${OUTPUT_FILE})		
				done
			done
		done
	done
done
