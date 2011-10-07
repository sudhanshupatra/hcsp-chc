#encoding: utf-8
'''
Created on Oct 3, 2011

@author: santiago
'''

import sys
import random

lo = (10,1)
med = (10,2)
hi = (10,4)

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
        (mu, sigma) = lo
    elif heterogeneidad == 2:
        (mu, sigma) = med
    elif heterogeneidad == 3:
        (mu, sigma) = hi
    else:
        (mu, sigma) = (5,0)
    
    random.seed(current_seed)
    
    for task in range(cantidad_tareas):
        # Calculo la prioridad asignada a la tarea.
        prioridad = int(round(random.gauss(mu, sigma))) 

        if prioridad < 1: prioridad = 1
        
        print prioridad

