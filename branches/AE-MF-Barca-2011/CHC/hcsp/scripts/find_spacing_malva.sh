for instance_dir in $(ls -d */)
do
    #echo "Procesando ${instance_dir}"
    cd ${instance_dir}

    #trial=0
    #for trial_dir in $(ls -d */)
    #do
    #    echo "> ${trial_dir} (${trial})"
    #    /home/santiago/workspace/AE-MF-Barca-2011/CHC/hcsp/tests/resultados/Spacing_2obj FP_${trial}.txt > results_${trial}.log
    #    mv FP.out FP_${trial}.out
	#trial=$(($trial + 1))
    #done

    /home/santiago/workspace/AE-MF-Barca-2011/CHC/hcsp/tests/resultados/Spacing_2obj FP.out > spread.log
    echo "${instance_dir} $(cat spread.log)"
    cd ..
done

