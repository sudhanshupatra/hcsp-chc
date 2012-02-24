working_path="/home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp"
scripts_path="/home/siturria/instancias/8192x256.M"
instance="B.u_c_hihi"

CfgFile="${working_path}/ejecuciones/scripts_frente_pareto/barca1.cfg"
DataFile="${scripts_path}/${instance}"
OutputFile="${working_path}/tests/resultados/prueba_lan_cluster"
PesosFile="${working_path}/ejecuciones/pesos_8.txt"

echo "${CfgFile}" > ${working_path}/tests/resultados/prueba_lan.cfg
echo "$DataFile" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "$OutputFile.sol" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "$PesosFile" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "8192" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "256" >> ${working_path}/tests/resultados/prueba_lan.cfg

cat ${working_path}/tests/resultados/prueba_lan.cfg

time(/home/siturria/bin/mpich2-1.2.1p1/bin/mpirun -n 3 ${working_path}/MainLan \
	${working_path}/tests/resultados/prueba_lan.cfg > $OutputFile.log) 
#/home/siturria/bin/mpich2-1.2.1p1/bin/mpirun -n 3 ${working_path}/MainLan \
#	${working_path}/tests/resultados/prueba_lan.cfg
