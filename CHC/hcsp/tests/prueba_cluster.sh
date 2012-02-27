working_path="/home/siturria/hcsp-chc/branches/AE-MF-Barca-2011/CHC/hcsp"
scripts_path="/home/siturria/instancias/512x16.M"

instance_i="B.u_i_hihi"
instance_c="B.u_c_hihi"

DataFileI="${scripts_path}/${instance_i}"
OutputFileI="${working_path}/tests/resultados/i_${data[i]}"

DataFileC="${scripts_path}/${instance_c}"
OutputFileC="${working_path}/tests/resultados/c_${data[i]}"

CfgFile="${working_path}/ejecuciones/config-0512-cluster.cfg"
PesosFile="${working_path}/ejecuciones/pesos_16_2.txt"

echo "${CfgFile}" > ${working_path}/tests/resultados/prueba_lan.cfg
echo "${DataFileI}" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "${OutputFileI}.sol" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "${PesosFile}" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "512" >> ${working_path}/tests/resultados/prueba_lan.cfg
echo "16" >> ${working_path}/tests/resultados/prueba_lan.cfg

time(/home/siturria/bin/mpich2-1.2.1p1/bin/mpirun -n 16 ${working_path}/MainLan \
    ${working_path}/tests/resultados/prueba_lan.cfg > ${OutputFileI}.log) 
