cat i_.sol_? > metricas_i.log
cat i_.sol_?? >> metricas_i.log
./FP_2obj_3col metricas_i.log > /dev/null
mv FP.out FP_i.out

#echo "i ================================"
#cat FP_i.out