'''
Created on Oct 14, 2011

@author: santiago
'''

import sys

if __name__ == '__main__':
    output0 = open(sys.argv[1] + "_00","w")
    output1 = open(sys.argv[1] + "_12","w")
    output2 = open(sys.argv[1] + "_13","w")
    output3 = open(sys.argv[1] + "_23","w")
    
    inputfile = open(sys.argv[1],"r")
    for line in inputfile:
        data = str(line).strip().split(" ")
        print data
        output0.write(data[0] + " " + data[1] + " " + data[2] + "\n")
        output1.write(data[0] + " " + data[1] + "\n")
        output2.write(data[0] + " " + data[2] + "\n")
        output3.write(data[1] + " " + data[2] + "\n")
        
    inputfile.close()
    output0.close()
    output1.close()
    output2.close()