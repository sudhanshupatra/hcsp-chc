WorkingPath="/home/santiago/workspace/AE-ME-Madrid-2012/CHC/hcsp"
InstancePath="/home/santiago/Scheduling/Instances/Makespan-Energy"

Scenario="scenario.19"
Workload="workload.B.u_c_hihi"

CfgFile="${WorkingPath}/ejecuciones/config.cfg"
PesosFile="${WorkingPath}/ejecuciones/pesos_fijos.txt"

ScenarioFile="${InstancePath}/512x16.ME/${Scenario}"
WorkloadFile="${InstancePath}/512x16.ME/${Workload}"

OutputPath="${WorkingPath}/tests/resultados"
OutputFile="${OutputPath}/prueba_dora"

echo "${CfgFile}" > ${OutputPath}/prueba_dora.cfg
echo "${ScenarioFile}" >> ${OutputPath}/prueba_dora.cfg
echo "${WorkloadFile}" >> ${OutputPath}/prueba_dora.cfg
echo "${OutputFile}.sol" >> ${OutputPath}/prueba_dora.cfg
echo "${PesosFile}" >> ${OutputPath}/prueba_dora.cfg
echo "512" >> ${OutputPath}/prueba_dora.cfg
echo "16" >> ${OutputPath}/prueba_dora.cfg

time(mpirun -n 3 ${WorkingPath}/MainLan ${OutputPath}/prueba_dora.cfg > ${OutputFile}.log)
#mpirun -n 3 ${WorkingPath}/MainLan ${OutputPath}/prueba_dora.cfg 
