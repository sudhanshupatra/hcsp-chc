cat i_.sol_? > metricas_i.log
cat i_.sol_?? >> metricas_i.log
./FP_2obj_3col metricas_i.log
mv FP.out FP_i.out

cat c_.sol_? > metricas_c.log
cat c_.sol_?? >> metricas_c.log
./FP_2obj_3col metricas_c.log
mv FP.out FP_c.out
