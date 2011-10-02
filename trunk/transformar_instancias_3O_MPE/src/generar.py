#encoding: utf-8
'''
Created on Oct 2, 2011

@author: santiago
'''

import sys

if __name__ == '__main__':
    argc = len(sys.argv)
    
    # Input: ./generar <instancia_clasica> <cant_tareas> <cant_maquinas> <datos_prioridad> <datos_energia>
    if argc != 7:
        print "Argumentos incorrectos. Modo de uso:"
        print "python generar <instancia_clasica> <cant_tareas> <cant_maquinas> <datos_prioridad> <datos_energia> <salida>\n"
        exit(0);
    
    instancia_clasica_path = sys.argv[1]
    cant_tareas = int(sys.argv[2])
    cant_maquinas = int(sys.argv[3])
    datos_prioridad_path = sys.argv[4]
    datos_energia_path = sys.argv[5]
    salida_path = sys.argv[6]
    
    print "[PARAMS] Instancia clásica: %s" % instancia_clasica_path
    print "[PARAMS] Cantidad de tareas: %s" % cant_tareas
    print "[PARAMS] Cantidad de máquinas: %s" % cant_maquinas
    print "[PARAMS] Datos de prioridad: %s" % datos_prioridad_path
    print "[PARAMS] Datos de energía: %s" % datos_energia_path
    print "[PARAMS] Archivo de salida: %s" % salida_path

    print "[INFO] Genero el archivo de salida..."
    salida_archivo = open(salida_path, "w")

    ######################################################
    # ENERGIA
    ######################################################
    print "\n=============================================="
    print "[INFO] Datos de energía:"
    print "Guardando...............",
    
    datos_energia = []
    
    with open(datos_energia_path, "r") as energia_archivo:
        for line in energia_archivo:
            if len(str(line).strip()) > 0:
                cotas = str(line).split(',')
                datos_energia.append((float(cotas[0]),float(cotas[1])))
    
    for maquina_id in range(cant_maquinas):
        (maquina_consumo_idle, maquina_consumo_max)  = datos_energia[maquina_id % len(datos_energia)]
        salida_archivo.write(str(maquina_consumo_idle) + '\n')
        salida_archivo.write(str(maquina_consumo_max) + '\n')

    print "<OK>"

    ######################################################
    # PRIORIDAD
    ######################################################
    print "\n=============================================="
    print "[INFO] Datos de prioridad:"
    print "Guardando.................",
    
    datos_prioridad = []
    
    with open(datos_prioridad_path, "r") as prioridad_archivo:
        for line in prioridad_archivo:
            if len(str(line).strip()) > 0:
                datos_prioridad.append(int(line))
        
    for tarea_id in range(cant_tareas):
        salida_archivo.write(str(datos_prioridad[tarea_id % len(datos_prioridad)]) + '\n')
    
    print "<OK>"
    
    ######################################################
    # ETC
    ######################################################    
    print "\n=============================================="
    print "[INFO] Datos de ETC:"
    print "Guardando...........",
        
    with open(instancia_clasica_path, "r") as instancia_clasica:
        for line in instancia_clasica:
            salida_archivo.write(line)

    print "<OK>"
    
    salida_archivo.close()