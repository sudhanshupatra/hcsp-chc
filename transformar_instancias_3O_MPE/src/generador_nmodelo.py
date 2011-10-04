#encoding: utf-8
'''
Created on Oct 3, 2011

@author: santiago
'''

import sys
import random

# Todas las unidades de tiempo son segundos.
TO_min = 10*60        # 10 minutos.
TO_max = 25*60*60     # 25 horas.

# Intel Xeon E5440: cores=4, ssj_ops=150,979, E_IDLE=76.9, E_MAX=131.8
TO_default_ssj = int(150979 / 4)
TO_min_ssj = float(TO_default_ssj) * float(TO_min)
TO_max_ssj = float(TO_default_ssj) * float(TO_max) 

AO_lo = (5,20)
AO_med = (20,35)
AO_hi = (35,45)

if __name__ == '__main__':
    argc = len(sys.argv)
    
    if argc != 5:
        print "Modo de uso: python %s <cant_tareas> <cant_maquinas> <heterogeneidad> <seed>" % sys.argv[0]
        print "             heterogeneidad: LOW=0, MEDIUM=1, HIGH=2"
        exit(0)

    cantidad_tareas = int(sys.argv[1])
    cantidad_maquinas = int(sys.argv[2])
    heterogeneidad = int(sys.argv[3])
    current_seed = int(sys.argv[4])
    
    # Configuro la heterogeneidad seleccionada.
    if heterogeneidad == 0:
        AO_hetero = AO_lo
    elif heterogeneidad == 1:
        AO_hetero = AO_med
    elif heterogeneidad == 2:
        AO_hetero = AO_hi
    else:
        AO_hetero = AO_lo
    
    random.seed(current_seed)
    
    for task in range(cantidad_tareas):
        # Calculo el costo independiente de la máquina.
        TO_current = long(random.uniform(TO_min_ssj, TO_max_ssj))
        
        for machine in range(cantidad_maquinas):
            # Calculo el costo del overhead adicional para cada posible máquina.
            AO_current = random.randint(AO_hetero[0], AO_hetero[1]) 

            # Calculo TO * (1 + AO).        
            print long(TO_current * ((AO_current / 100.0) + 1))
