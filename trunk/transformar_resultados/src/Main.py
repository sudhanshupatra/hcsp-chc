'''
Created on Sep 28, 2010

@author: santiago
'''

import Transformar
import Transformar_Eval
import Procesar
import Procesar_Eval

if __name__ == '__main__':
#    Transformar.Transformar2CSV().do("/media/Datos/Facultad/Cursos/Algoritmos Evolutivos/Proyecto/calibracion_3/seq_cprio", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/input_sec_cprio/")
#    Procesar.ProcesarCSV().do("/home/santiago/eclipse/python-workspace/calibracion2csv/src/input_sec_cprio/", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/resumen_seq_cprio/")

    Transformar_Eval.TransformarEval2CSV().do("/media/Datos/Facultad/Cursos/Algoritmos Evolutivos/Proyecto/calibracion_4/lan4_cprio", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/input_lan4_cprio/")
    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/python-workspace/calibracion2csv/src/input_lan4_cprio/", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/resumen_lan4_cprio/")
    
#    Transformar_Eval.TransformarEval2CSV().do("/media/Datos/Facultad/Cursos/Algoritmos Evolutivos/Proyecto/calibracion_4/lan8_cprio", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/input_lan8_cprio/")
#    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/python-workspace/calibracion2csv/src/input_lan8_cprio/", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/resumen_lan8_cprio/")

#    Transformar_Eval.TransformarEval2CSV().do("/media/Datos/Facultad/Cursos/Algoritmos Evolutivos/Proyecto/evaluacion/seq_cprio/Braun_et_al.CPrio", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/eval_braun_sec_cprio/")
#    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/python-workspace/calibracion2csv/src/eval_braun_sec_cprio/", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/resumen_eval_braun_sec_cprio/")
#
#    Transformar_Eval.TransformarEval2CSV().do("/media/Datos/Facultad/Cursos/Algoritmos Evolutivos/Proyecto/evaluacion/seq_cprio/1024x32.CPrio", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/eval_1024_sec_cprio/")
#    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/python-workspace/calibracion2csv/src/eval_1024_sec_cprio/", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/resumen_eval_1024_sec_cprio/")
#
#    Transformar_Eval.TransformarEval2CSV().do("/media/Datos/Facultad/Cursos/Algoritmos Evolutivos/Proyecto/evaluacion/seq_cprio/2048x64.CPrio", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/eval_2048_sec_cprio/")
#    Procesar_Eval.ProcesarEvalCSV().do("/home/santiago/eclipse/python-workspace/calibracion2csv/src/eval_2048_sec_cprio/", "/home/santiago/eclipse/python-workspace/calibracion2csv/src/resumen_eval_2048_sec_cprio/")
