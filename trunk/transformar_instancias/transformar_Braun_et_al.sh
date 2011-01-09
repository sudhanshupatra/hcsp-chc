#!/bin/bash

seed="1"
input_path="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/Braun_et_al/"
output_path="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/Braun_et_al.CPrio/"
min="1"
max="10"

data[0]="u_c_hihi.0"
data[1]="u_c_hilo.0"
data[2]="u_c_lohi.0"
data[3]="u_c_lolo.0"
data[4]="u_i_hihi.0"
data[5]="u_i_hilo.0"
data[6]="u_i_lohi.0"
data[7]="u_i_lolo.0"
data[8]="u_s_hihi.0"
data[9]="u_s_hilo.0"
data[10]="u_s_lohi.0"
data[11]="u_s_lolo.0"

for dataIndex in {0..11}
do
	Debug/transformar_instancias $input_path${data[dataIndex]} $output_path${data[dataIndex]} $seed $min $max 512 16
done
