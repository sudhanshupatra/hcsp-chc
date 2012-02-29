import os

if __name__ == '__main__':
    for path in os.listdir("."):
        if os.path.isdir(path):
            #print path
            
            total = 0
            count = 0
            
            for iter in range(30):
                f = path + '/FP_' + str(iter) + '.out'
                
                if os.path.exists(f):
                    fp_file = open(f)
                    cant_lineas = len(fp_file.readlines())
                    
                    count = count + 1
                    total = total + cant_lineas
                    
                    #print cant_lineas
                              
            f = path + '/FP.out'
            
            if os.path.exists(f):
                fp_file = open(f)
                cant_lineas = len(fp_file.readlines())
                print "%s %.2f %d" % (path, float(total) / float(count), cant_lineas)
