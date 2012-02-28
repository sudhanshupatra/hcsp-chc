for instance_dir in $(ls -d */)
do
    echo "Procesando ${instance_dir}"
    cd ${instance_dir}

    wc -l FP_*.out > fp_count.log
    wc -l FP.out > fp_count.all.log

    cd ..
done

