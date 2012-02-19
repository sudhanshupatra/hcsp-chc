data[6]="u_c_hihi.0"

for i in {6..6}
do
	CfgFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/ejecuciones/scripts_evaluacion/chc_lan.cfg"
	DataFile="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/Braun_et_al.CPrio/${data[i]}"
	OutputFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/prueba_lan_${data[i]}"
	PesosFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/ejecuciones/pesos_fijos.txt"
	
	echo "${CfgFile}" > /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/prueba_lan.cfg
	echo "$DataFile" >> /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/prueba_lan.cfg
	echo "$OutputFile.sol" >> /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/prueba_lan.cfg
	echo "$PesosFile" >> /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/prueba_lan.cfg
	
	time(mpirun -n 3 /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainLan \
		/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/prueba_lan.cfg > $OutputFile.log) 
done
