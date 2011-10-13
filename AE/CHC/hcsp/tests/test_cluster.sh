PROJECT_NAME="test"
ROOT_PATH="/home/siturria/AE/trunk/AE"
#ROOT_PATH="/home/santiago/eclipse/c-c++-workspace/AE"
MALLBA_CONFIG="${ROOT_PATH}/CHC/hcsp/ejecuciones/scripts_frente_pareto/barca.cfg"
PESOS_PATH="${ROOT_PATH}/CHC/hcsp/ejecuciones/pesos_fijos.txt"
ITERACIONES=1
EXEC="/home/siturria/bin/mpich2-1.2.1p1/bin/mpirun -n 8 ${ROOT_PATH}/CHC/hcsp/MainLan"

for scenario in {0..0}
do
	for workload in {0..0}
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
			
			BASE_FOLDER="${ROOT_PATH}/CHC/hcsp/ejecuciones/resultados/${PROJECT_NAME}/s.${scenario}_w.${workload}_p${priorities}"
			mkdir -p ${BASE_FOLDER}
			
			CONFIG_FILE="${BASE_FOLDER}/Config.cfg"
		
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
