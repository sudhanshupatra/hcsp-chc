data[6]="A.u_c_hihi"

for i in {6..6}
do
	CfgFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/chc.cfg"
	DataFile="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/2048x64.CPrio/${data[i]}"
	OutputFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_seq_${data[i]}"
	
	echo "Datos $DataFile"
	
	time(/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainSeq $CfgFile $DataFile $OutputFile.sol > $OutputFile.log)    
done
