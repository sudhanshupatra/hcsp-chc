import os

if __name__ == '__main__':
    for path in os.listdir("."):
        if os.path.isdir(path):
            input = path + '/FP.out'
            output = path + '/FP.out.2'
            
            if os.path.exists(input):
                input_file = open(input)
                output_file = open(output,'w')
                
                for line in input_file:
                    values = line.strip().split(' ')
                    output_file.write(str(float(values[0].strip()) * 10000) + \
                        ' ' + str(float(values[1].strip()) * 10000) + '\n')
                        
            for i in range(30):
                input = path + '/FP_'+str(i)+'.out'
                output = path + '/FP_'+str(i)+'.out.2'
                
                if os.path.exists(input):
                    input_file = open(input)
                    output_file = open(output,'w')
                    
                    for line in input_file:
                        values = line.strip().split(' ')
                        output_file.write(str(float(values[0].strip()) * 10000) + \
                            ' ' + str(float(values[1].strip()) * 10000) + '\n')            
