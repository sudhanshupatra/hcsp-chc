#!/bin/bash

EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpiexec -np 17 /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan"

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
	
	DATA_FILE="/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/4096x128.CPrio/${data[i]}"
	
	BASE_FOLDER="/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/frente_pareto/barca/4_c/${data[i]}"
	mkdir -p ${BASE_FOLDER}
	
	CONFIG_FILE="${BASE_FOLDER}/Config.cfg"

	#
	# Cada instancia la resuelvo 20 veces.
	#
	for (( j=0 ; j<20 ; j++))
	do
		echo "${j}"

		DEST_FOLDER="${BASE_FOLDER}/${j}"
		mkdir -p ${DEST_FOLDER} 

		echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/scripts_frente_pareto/barca1.cfg" > ${CONFIG_FILE}
		echo "${DATA_FILE}" >> ${CONFIG_FILE}
		echo "${DEST_FOLDER}/${j}.sol" >> ${CONFIG_FILE}
		echo "/home/siturria/AE/trunk/AE/CHC/hcsp/ejecuciones/pesos_variados.txt" >> ${CONFIG_FILE}
	
		OUTPUT_FILE="${DEST_FOLDER}/${j}.log"
		
		time(${EXEC} ${CONFIG_FILE} > ${OUTPUT_FILE})		
	done
done
