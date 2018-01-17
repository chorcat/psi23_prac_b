Nombre: Borja González Enríquez
DNI: 44483382W
Cuenta: psi23

Para que no haya problemas en el compilado y ejecucion realizamos lo siguiente:
Metemos todos los .java y el jade.jar en una misma carpeta y realizamos los siguientes pasos:

Como compilar:
javac -cp jade.jar *.java

Como ejecutar:
java -cp "jade.jar:." jade.Boot -agents "Main:psi23_MainAg;Fixed1:psi23_Fixed;Fixed2:psi23_Fixed;Random1:psi23_Random;Random2:psi23_Random"

java -cp C:\jade\jade.jar;D:\GitHub\psi23_prac_b\bin\*;. jade.Boot -agents "Main:psi23_MainAg;Fixed1:psi23_Fixed;Random1:psi23_Random"

java -cp C:\jade\jade.jar;D:\GitHub\psi23_prac_b\bin\*;. jade.Boot -agents "Main:psi23_MainAg;Fixed1:psi23_Fixed;Fixed2:psi23_Fixed;Fixed3:psi23_Fixed;Fixed4:psi23_Fixed;Random1:psi23_Random;Random2:psi23_Random;Random3:psi23_Random;Random4:psi23_Random;Borja:psi23_Intelx"
