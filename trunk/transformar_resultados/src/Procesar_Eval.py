'''
Created on Sep 29, 2010

@author: santiago
'''

import os
import math

def significanciaSort(a, b):
    avg_a = a[2]
    std_a = a[3]
    
    avg_b = b[2]
    std_b = b[3]
    
    max_std = std_a
    if std_b > max_std: max_std = std_b
    
    if avg_a < avg_b:
        diff_avg = avg_b - avg_a
        if diff_avg < max_std:
            return -1
        else:
            return int(a[1]-b[1])        
    elif avg_b < avg_a:
        diff_avg = avg_a - avg_b
        if diff_avg < max_std:
            return 1
        else:
            return int(a[1]-b[1])    
    else:
        return int(a[1]-b[1])

class ProcesarEvalCSV(object):
    '''
    classdocs
    '''

    def __init__(self):
        '''
        Constructor
        '''
   
    def do(self, inputPath, outputPath):
        print "InputPath: %s" % inputPath
        print "OutputPath: %s" % outputPath
        
        data = {}
        
        contenido = os.listdir(inputPath)
        for relativeItem in contenido:
            if os.path.splitext(relativeItem)[1] == ".csv":
                absoluteItem = os.path.join(inputPath,relativeItem)
                
                currentFile = None
                currentData = None
                
                itemFile = open(absoluteItem, 'r')
                for line in itemFile:
                    splittedLine = line.split(';')
                    
                    if splittedLine[0] != "":
                        currentFile = splittedLine[0]
                        
                        if not data.has_key(currentFile):
                            data[currentFile] = []
                        
                        currentData = data[currentFile]
                    else:
                        if currentData != None and currentFile != None:
                            bestValue = splittedLine[1]
                            currentData.append(float(bestValue))
                    
        preSortedData = {}
                                       
        for fileName in sorted(data.keys()):
            outputFileName = "%s.resumen.csv" % (fileName)
            outputFileName = os.path.join(outputPath, outputFileName)
            
            if not os.path.exists(outputFileName):
                outputFile = open(outputFileName, 'w+')
                outputFile.write('\"file\";\"best\";\"average\";\"stdev\";\"stdev %\";\"count\";\n')
            else:
                outputFile = open(outputFileName, 'a+')
            
            fileData = data[fileName]
            
            overallBestValue = fileData[0]
            totalValue = 0
            
            for itemData in fileData:
                if itemData < overallBestValue:
                    overallBestValue = itemData
                    
                totalValue = totalValue + itemData
                
            averageValue = totalValue / len(fileData)
            
            stdDeviationSum = 0
            for itemData in fileData:
                stdDeviationSum = stdDeviationSum + math.pow((itemData - averageValue), 2)
                
            standardDeviation = math.sqrt(stdDeviationSum / len(fileData))
            relativeStandardDeviation = (standardDeviation / averageValue) * 100
                
            #print "%s, best = %f, average = %f, stdev = %f (%f%%)" % (fileName, overallBestValue, averageValue, standardDeviation, relativeStandardDeviation)
            outputFile.write('\"%s\";%f;%f;%f;%f;%d;\n' % (fileName, overallBestValue, averageValue, standardDeviation, relativeStandardDeviation, len(fileData)))
            outputFile.close()
            
            if not preSortedData.has_key(outputFileName):
                preSortedData[outputFileName] = []
                
            preSortedData[outputFileName].append((fileName, overallBestValue, averageValue, standardDeviation, relativeStandardDeviation))
            