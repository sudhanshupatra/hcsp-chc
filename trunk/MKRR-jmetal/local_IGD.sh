CLASSPATH="/home/santiago/eclipse/java-workspace/MOScheduling/bin"

NUM_OBJ=2
TRUE_PF="/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_9/barca/1/u_c_hihi.0/FP.out"
PF="/home/santiago/eclipse/c-c++-workspace/resultados/ejecucion_9/barca/1/u_c_hihi.0/FP_0.out"

echo "Spead:"
java -classpath $CLASSPATH jmetal.qualityIndicator.SpreadNonNormalized $PF $TRUE_PF $NUM_OBJ

echo "GD (Norm):"
java -classpath $CLASSPATH jmetal.qualityIndicator.GenerationalDistance $PF $TRUE_PF $NUM_OBJ

echo "GD:"
java -classpath $CLASSPATH jmetal.qualityIndicator.GenerationalDistanceNonNormalized $PF $TRUE_PF $NUM_OBJ

echo "IGD:"
java -classpath $CLASSPATH jmetal.qualityIndicator.InvertedGenerationalDistanceNonNormalized $PF $TRUE_PF $NUM_OBJ

echo "HV:"
java -classpath $CLASSPATH jmetal.qualityIndicator.HypervolumeNonNormalized $PF $TRUE_PF $NUM_OBJ
