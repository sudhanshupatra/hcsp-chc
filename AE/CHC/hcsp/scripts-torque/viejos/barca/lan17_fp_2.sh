#!/bin/bash

# Nombre del trabajo
#PBS -N ae_17_fp_2

# Requerimientos
#PBS -l nodes=17,walltime=30:00:00

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
#PBS -e /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/barca/2
#PBS -o /home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/barca/2

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

data[6]="A.u_c_hihi"
data[7]="A.u_c_lolo"
data[8]="A.u_i_lohi"
data[9]="A.u_s_hilo"
data[10]="B.u_c_hihi"
data[11]="B.u_c_lolo"
data[12]="B.u_i_lohi"
data[13]="B.u_s_hilo"
data[14]="A.u_c_hilo"
data[15]="A.u_i_hihi"
data[16]="A.u_i_lolo"
data[17]="A.u_s_lohi"
data[18]="B.u_c_hilo"
data[19]="B.u_i_hihi"
data[20]="B.u_i_lolo"
data[21]="B.u_s_lohi"
data[22]="A.u_c_lohi"
data[23]="A.u_i_hilo"
data[24]="A.u_s_hihi"
data[25]="A.u_s_lolo"
data[26]="B.u_c_lohi"
data[27]="B.u_i_hilo"
data[28]="B.u_s_hihi"
data[29]="B.u_s_lolo"

for i in {6..29}
do
	#
	# Itero entre todas las instancias del problema a resolver.
	#
	echo ">>> Procesando ${i} ${data[i]}"
	
	DATA_FILE="/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/1024x32.CPrio/${data[i]}"
	
	BASE_FOLDER="/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/barca/2/${data[i]}"
	mkdir -p ${BASE_FOLDER}
	
	CONFIG_FILE="${BASE_FOLDER}/Config.cfg"

	#
	# Cada instancia la resuelvo 30 veces.
	#
	for (( j=0 ; j<30 ; j++))
	do
		echo "${j}"

		DEST_FOLDER="${BASE_FOLDER}/${j}"
		mkdir -p ${DEST_FOLDER} 

		echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/scripts_frente_pareto/barca.cfg" > ${CONFIG_FILE}
		echo "${DATA_FILE}" >> ${CONFIG_FILE}
		echo "${DEST_FOLDER}/${j}.sol" >> ${CONFIG_FILE}
		echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/pesos.txt" >> ${CONFIG_FILE}
	
		OUTPUT_FILE="${DEST_FOLDER}/${j}.log"
		
		time(${EXEC} ${CONFIG_FILE} > ${OUTPUT_FILE})		
	done
done
