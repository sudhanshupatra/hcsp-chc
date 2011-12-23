Data="u_s_hilo.0"
Poblacion=10
Cruzamiento=1.0
Mutacion=1.0

for indexP in {0..2}
do
	CfgFile="../ejecuciones/scripts_frente_pareto/chc_$indexP.cfg"
	DataFile="../../../ProblemInstances/HCSP/Braun_et_al.CPrio/$Data"
	OutputFile="resultados/$indexP"
	
	echo "Datos $DataFile"
	
	time(../MainSeq $CfgFile $DataFile resultados/$OutputFile.sol > resultados/$OutputFile.log)    
done
