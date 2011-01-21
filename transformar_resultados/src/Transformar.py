'''
Created on Sep 28, 2010

@author: santiago
'''

import os

class Transformar2CSV(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        
    def do(self, inputPath, outputPath):
        print "Input path: %s" % inputPath
               
        contenido = os.listdir(inputPath)
        contenido = sorted(contenido)
        
        print "Contenido"
        print contenido
        
        for relativeItem in contenido:
            item = os.path.join(inputPath, relativeItem)
            
            if os.path.isfile(item):
                ext = os.path.splitext(item)[1]
                if ext == '.sol':
                    itemFile = open(item, 'r')
                    
                    splittedName = relativeItem.split("_")
                    outputFileName = "%s_%s_%s.csv" % (splittedName[0], splittedName[1], splittedName[2])
                    outputFileName = os.path.join(outputPath, outputFileName)
                    
                    if not os.path.exists(outputFileName):
                        csv = open(outputFileName, 'w')
                        csv.write(';\"best\";\"iter\";\"time\"\n')
                    else:
                        csv = open(outputFileName, 'a+')

                    csv.write('%s;;;\n' % relativeItem)
                    
                    for omit in range(5): itemFile.readline()
                    
                    for data in range(20):
                        dataLine = itemFile.readline()
                        splittedDataLine = dataLine.split('\t')
                        
                        bestValue = splittedDataLine[1]
                        bestValueIterationFound = splittedDataLine[9]
                        bestValueTimeFound = splittedDataLine[12]
                        
                        csv.write(';%s;%s;%s\n' % (bestValue, bestValueIterationFound, bestValueTimeFound))
                                
                    csv.close()
                    