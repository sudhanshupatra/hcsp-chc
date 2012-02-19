data[6]="u_c_hihi.0"

for i in {6..6}
do
	CfgFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/chc.cfg"
	DataFile="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/Braun_et_al.CPrio/${data[i]}"
	OutputFile="/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/tests/resultados/tunning_seq_${data[i]}"
	
	echo "Datos $DataFile"
	
	/home/santiago/eclipse/c-c++-workspace/AE/CHC/hcsp/MainSeq $CfgFile $DataFile $OutputFile.sol    
done
