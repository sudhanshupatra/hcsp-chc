make clean; make; 
cd tests; 
./prueba_dora.sh; 
cd resultados;
./metricas.sh ; 
echo "c ================================"
cat FP_c.out;
echo "i ================================"
cat FP_i.out;
cd ..;
cd ..;
