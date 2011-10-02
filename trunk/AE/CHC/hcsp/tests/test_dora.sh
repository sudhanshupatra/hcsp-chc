PROJECT_NAME="test"
#ROOT_PATH="/home/siturria/AE/trunk/AE"
ROOT_PATH="/home/santiago/eclipse/c-c++-workspace/AE"
MALLBA_CONFIG="${ROOT_PATH}/CHC/hcsp/ejecuciones/scripts_frente_pareto/barca.cfg"
PESOS_PATH="${ROOT_PATH}/CHC/hcsp/ejecuciones/pesos_fijos.txt"
ITERACIONES=1
EXEC="mpirun -n 4 ${ROOT_PATH}/CHC/hcsp/MainLan"

data[0]="u_c_hihi.0"
data[1]="u_c_hilo.0"
data[2]="u_c_lohi.0"
data[3]="u_c_lolo.0"
data[4]="u_s_hihi.0"
data[5]="u_s_hilo.0"
data[6]="u_s_lohi.0"
data[7]="u_s_lolo.0"
data[8]="u_i_hihi.0"
data[9]="u_i_hilo.0"
data[10]="u_i_lohi.0"
data[11]="u_i_lolo.0"

for i in {0..0}
do
	#
	# Itero entre todas las instancias del problema a resolver.
	#
	echo ">>> Procesando ${i} ${data[i]}"
	
	DATA_FILE="${ROOT_PATH}/ProblemInstances/HCSP-3O-MPE/512x16_mod/${data[i]}"
	
	BASE_FOLDER="${ROOT_PATH}/CHC/hcsp/ejecuciones/resultados/${PROJECT_NAME}/${data[i]}"
	mkdir -p ${BASE_FOLDER}
	
	CONFIG_FILE="${BASE_FOLDER}/Config.cfg"

	#
	# Cada instancia la resuelvo 30 veces.
	#
	for (( j=0 ; j<$ITERACIONES ; j++))
	do
		echo "${j}"

		DEST_FOLDER="${BASE_FOLDER}/${j}"
		mkdir -p ${DEST_FOLDER} 

		echo "${MALLBA_CONFIG}" > ${CONFIG_FILE}
		echo "${DATA_FILE}" >> ${CONFIG_FILE}
		echo "${DEST_FOLDER}/${j}.sol" >> ${CONFIG_FILE}
		echo "${PESOS_PATH}" >> ${CONFIG_FILE}
		echo "512" >> ${CONFIG_FILE}
		echo "16" >> ${CONFIG_FILE}
	
		OUTPUT_FILE="${DEST_FOLDER}/${j}.log"
		
		time(${EXEC} ${CONFIG_FILE} > ${OUTPUT_FILE})		
	done
done
