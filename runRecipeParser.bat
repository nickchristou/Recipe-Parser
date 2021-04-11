@echo off
set /P inputDir=Please enter path to input directory:
set /P outputDir=Please enter path to output directory:

@echo on

java -jar target\recipe-parser-1.0-SNAPSHOT.jar %inputDir% %outputDir%

pause