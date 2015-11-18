set CLASSES=ojdbc14.jar;servlet-api.jar;poi-3.7-20101029.jar;poi-ooxml-3.7-20101029.jar;json-simple-1.1.1.jar
set SOURCE=.\src
set DEST=.\classes
set JAVAHOME="c:\Program Files\Java\jdk1.8.0_45"
%JAVAHOME%\bin\javac -nowarn -classpath %CLASSES% -sourcepath %SOURCE% -d %DEST% .\src\service\**.java
%JAVAHOME%\bin\javac -nowarn -classpath %CLASSES% -sourcepath %SOURCE% -d %DEST% .\src\sockets\**.java
%JAVAHOME%\bin\javac -nowarn -classpath %CLASSES% -sourcepath %SOURCE% -d %DEST% .\src\tools\**.java