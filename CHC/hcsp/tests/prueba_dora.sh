working_path="/home/santiago/workspace/AE-MF-Barca-2011/CHC/hcsp"
scripts_path="/home/santiago/Scheduling/Instances/Makespan/512x16"
instance="B.u_c_hihi"

CfgFile="${working_path}/ejecuciones/scripts_frente_pareto/barca2.cfg"
DataFile="${scripts_path}/${instance}"
OutputFile="${working_path}/tests/resultados/prueba_lan_${data[i]}"
PesosFile="${working_path}/ejecuciones/pesos_fijos.txt"

echo "${CfgFile}" > ${working_path}/tests/resultados/prueba_lan.cfg
echo "$DataFile" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "$OutputFile.sol" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "$PesosFile" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "512" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "16" >> ${working_path}/tests/resultados/prueba_lan.cfg

time(mpirun -n 3 ${working_path}/MainLan \
	${working_path}/tests/resultados/prueba_lan.cfg > $OutputFile.log) 
