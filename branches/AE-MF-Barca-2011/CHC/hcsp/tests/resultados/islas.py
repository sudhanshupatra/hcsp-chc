if __name__ == '__main__':
    fp = []
    fp_file = open('FP_i.out')
    for line in fp_file:
        values = line.strip().split(' ')
        fp.append((float(values[0].strip()), float(values[1].strip())))
    
    all = []
    all_file = open('metricas_i.log')
    for line in all_file:
        values = line.strip().split(' ')
        
        print values
        
        all.append((float(values[0].strip()), \
            float(values[1].strip()), \
            int(values[2].strip())))
    
    for item_fp in fp:        
        #print "++++++++++++++++++++++++++++++++"
        islas = []
        
        for item_all in all:
            if (item_all[0] == item_fp[0] and \
                item_all[1] == item_fp[1]):
                    
                    isla = item_all[2]
                    
                    if not isla in islas:
                        islas.append(isla)
        
        if len(islas) == 0:
            mas_cercano = None
            
            for item_all in all:
                if mas_cercano == None:
                    
                    mas_cercano = item_all
                    islas.append(item_all[2])
                    
                    #print "================================"
                    #print item_all
                else:
                    if (item_all[0] == mas_cercano[0] and \
                        item_all[1] == mas_cercano[1]):
                            
                            isla = item_all[2]
                            
                            if not isla in islas:
                                islas.append(isla)
                                
                    elif (abs(item_fp[0] - item_all[0]) <= abs(item_fp[0] - mas_cercano[0]) or \
                        abs(item_fp[1] - item_all[1]) <= abs(item_fp[1] - mas_cercano[1])):
                    
                            #print "================================"
                            #print item_all
                            
                            mas_cercano = item_all
                            
                            islas = []
                            islas.append(item_all[2])
        
        print "============================="
        print item_fp                
        print islas
