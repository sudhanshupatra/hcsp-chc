for instance_dir in $(ls -d */)
do
    echo "Procesando ${instance_dir}"
    cd ${instance_dir}

    trial=0
    for trial_dir in $(ls -d */)
    do
        echo "> ${trial_dir} (${trial})"
	/home/santiago/workspace/AE-MF-Barca-2011/CHC/hcsp/tests/resultados/FP_2obj_3col results_${trial}.txt > results_${trial}.log
        mv FP.out FP_${trial}.out
	trial=$(($trial + 1))
    done

    /home/santiago/workspace/AE-MF-Barca-2011/CHC/hcsp/tests/resultados/FP_2obj_3col results.txt > results.log
    cd ..
done

