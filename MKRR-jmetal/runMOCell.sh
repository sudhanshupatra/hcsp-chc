/usr/java/jdk1.6.0_03/bin/javac -classpath /home/siturria/AE/MOScheduling/src -d /home/siturria/AE/MOScheduling/bin $(find . -name "*.java")
/usr/java/jdk1.6.0_03/bin/java -classpath /home/siturria/AE/MOScheduling/bin jmetal.experiments.scheduling.MOCell_MKRR_Exp
