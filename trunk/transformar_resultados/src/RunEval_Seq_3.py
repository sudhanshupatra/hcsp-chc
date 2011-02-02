'''
Created on Sep 28, 2010

@author: santiago
'''

import Transformar_Eval
import Procesar_Eval

if __name__ == '__main__':
    Transformar_Eval.TransformarEval2CSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_3/seq/4096x128/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_3/seq/4096x128_csv/")
    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_3/seq/4096x128_csv/", "/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_3/seq/4096x128_resumen/")

