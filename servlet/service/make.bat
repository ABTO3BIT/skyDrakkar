set ARCHIVE=service.war
set CLASSES=servlet
set JAVAHOME="c:\Program Files\Java\jdk1.8.0_45"
del %ARCHIVE%
%JAVAHOME%\bin\jar cvfM %ARCHIVE% -C %CLASSES% .

