data[6]="A.u_c_hihi"

for i in {6..6}
do
/home/siturria/AE/trunk/AE/CHC/hcsp/
	CfgFile="/home/siturria/AE/trunk/AE/CHC/hcsp//tests/chc_lan.cfg"
	DataFile="/home/siturria/AE/trunk/AE/ProblemInstances/HCSP/2048x64.CPrio/${data[i]}"
	OutputFile="/home/siturria/AE/trunk/AE/CHC/hcsp/tests/resultados/tunning_lan_${data[i]}"
	
	echo "${CfgFile}" > /home/siturria/AE/trunk/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg
	echo "$DataFile" >> /home/siturria/AE/trunk/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg
	echo "$OutputFile.sol" >> /home/siturria/AE/trunk/AE/CHC/hcsp/resultados/tunning_lan.cfg
	
	time(/home/siturria/bin/mpich2-1.2.1p1/bin/mpirun -n 8 /home/siturria/AE/trunk/AE/CHC/hcsp/MainLan \
		/home/siturria/AE/trunk/AE/CHC/hcsp/tests/resultados/tunning_lan.cfg > $OutputFile.log) 
done
