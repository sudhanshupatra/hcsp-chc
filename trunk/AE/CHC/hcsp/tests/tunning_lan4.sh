data[0]="A.u_c_lolo"

for i in {0..0}
do
	CfgFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/chc_lan.cfg"
	DataFile="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/2048x64.CPrio/${data[i]}"
	OutputFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_lan_${data[i]}"
	
	echo "${CfgFile}" > /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg
	echo "$DataFile" >> /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg
	echo "$OutputFile.sol" >> /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg
	
	time(mpirun -n 8 /home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainLan \
		/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg > $OutputFile.log) 
done
