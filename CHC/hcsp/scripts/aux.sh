mkdir ../aux

for instance_dir in $(ls -d */)
do
    echo "Procesando ${instance_dir}"
    mkdir ../aux/${instance_dir}
    cd ${instance_dir}  

    for (( j=0; j < 30; j++ ))
    do
        echo "> ${j}"
        cp FP_${j}.out ../../aux/${instance_dir}
        cp FP_${j}.out.2 ../../aux/${instance_dir}
    done

    cp FP.out ../../aux/${instance_dir}
    cp FP.out.2 ../../aux/${instance_dir}
    cd ..
done

