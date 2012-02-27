for instance_dir in $(ls -d */)
do
    echo "Procesando ${instance_dir}"
    cd ${instance_dir}

    trial=0
    for trial_dir in $(ls -d */)
    do
        echo "> ${trial_dir} (${trial})"
        /home/siturria/AE/Metricas/bin/FP_2obj_3col results_${trial}.txt > results_${trial}.log
        mv FP.out FP_${trial}.out
	trial=$(($trial + 1))
    done

    /home/siturria/AE/Metricas/bin/FP_2obj_3col results.txt > results.log
    cd ..
done

