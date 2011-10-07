#encoding: utf-8
'''
Created on Oct 3, 2011

@author: santiago
'''

import sys
import random

AO_lo = (1,10)
AO_med = (1,20)
AO_hi = (1,30)

if __name__ == '__main__':
    argc = len(sys.argv)
    
    if argc != 4:
        print "Modo de uso: python %s <cant_tareas> <heterogeneidad> <seed>" % sys.argv[0]
        print "             heterogeneidad: LOW=1, MEDIUM=2, HIGH=3"
        exit(0)

    cantidad_tareas = int(sys.argv[1])
    heterogeneidad = int(sys.argv[2])
    current_seed = int(sys.argv[3])
    
    # Configuro la heterogeneidad seleccionada.
    if heterogeneidad == 1:
        AO_hetero = AO_lo
    elif heterogeneidad == 2:
        AO_hetero = AO_med
    elif heterogeneidad == 3:
        AO_hetero = AO_hi
    else:
        AO_hetero = (1,1)
    
    random.seed(current_seed)
    
    for task in range(cantidad_tareas):
        # Calculo la prioridad asignada a la tarea.
		prioridad = random.randint(AO_hetero[0], AO_hetero[1]) 
        
		print prioridad

