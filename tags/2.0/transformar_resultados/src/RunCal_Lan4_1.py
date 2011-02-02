'''
Created on Sep 28, 2010

@author: santiago
'''

import Transformar_Cal
import Procesar_Cal

if __name__ == '__main__':
    Transformar_Cal.Transformar2CSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/calibracion_1/lan4", "/home/santiago/eclipse/c-c++-workspace/resultados/calibracion_1/lan4_csv")
    Procesar_Cal.ProcesarCSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/calibracion_1/lan4_csv", "/home/santiago/eclipse/c-c++-workspace/resultados/calibracion_1/lan4_resumen")