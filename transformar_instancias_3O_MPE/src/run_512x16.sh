dir_entrada="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/512x16/"
dir_salida="/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/512x16_mod/"

datos[0]="u_c_hihi"
datos[1]="u_c_hilo"
datos[2]="u_c_lohi"
datos[3]="u_c_lolo"
datos[4]="u_s_hihi"
datos[5]="u_s_hilo"
datos[6]="u_s_lohi"
datos[7]="u_s_lolo"
datos[8]="u_i_hihi"
datos[9]="u_i_hilo"
datos[10]="u_i_lohi"
datos[11]="u_i_lolo"

for i in {0..11}
do
	python generar.py ${dir_entrada}${datos[${i}]}.0 512 16 \
		/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/ejemplo.prioridad.csv \
		/home/santiago/eclipse/c-c++-workspace/AE/ProblemInstances/HCSP-3O-MPE/ejemplo.energia.csv \
		${dir_salida}${datos[${i}]}.0
done