'''
Created on Sep 28, 2010

@author: santiago
'''

import Transformar_Eval
import Procesar_Eval

if __name__ == '__main__':
    Transformar_Eval.TransformarEval2CSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/Braun_et_al/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/Braun_et_al_csv/")
    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/Braun_et_al_csv/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/Braun_et_al_resumen/")
    
    Transformar_Eval.TransformarEval2CSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/1024x32/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/1024x32_csv/")
    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/1024x32_csv/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/1024x32_resumen/")
    
    Transformar_Eval.TransformarEval2CSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/2048x64/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/2048x64_csv/")
    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/2048x64_csv/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_4/lan4/2048x64_resumen/")
