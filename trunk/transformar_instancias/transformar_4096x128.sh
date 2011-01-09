#!/bin/bash

seed="1"
input_path="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/4096x128/"
output_path="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP/4096x128.CPrio/"
min="1"
max="10"

data[0]="A.u_c_hihi"
data[1]="A.u_c_hilo"
data[2]="A.u_c_lohi"
data[3]="A.u_c_lolo"
data[4]="A.u_i_hihi"
data[5]="A.u_i_hilo"
data[6]="A.u_i_lohi"
data[7]="A.u_i_lolo"
data[8]="A.u_s_hihi"
data[9]="A.u_s_hilo"
data[10]="A.u_s_lohi"
data[11]="A.u_s_lolo"
data[12]="B.u_c_hihi"
data[13]="B.u_c_hilo"
data[14]="B.u_c_lohi"
data[15]="B.u_c_lolo"
data[16]="B.u_i_hihi"
data[17]="B.u_i_hilo"
data[18]="B.u_i_lohi"
data[19]="B.u_i_lolo"
data[20]="B.u_s_hihi"
data[21]="B.u_s_hilo"
data[22]="B.u_s_lohi"
data[23]="B.u_s_lolo"

#for dataIndex in {0..23}
for dataIndex in {0..0}
do
	Debug/transformar_instancias $input_path${data[dataIndex]} $output_path${data[dataIndex]} $seed $min $max
done
