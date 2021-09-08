@echo off
:: tenfile seed numFitness sizePopulation pLocalSearch
setlocal abc
goto :main
:main 
setlocal
	java -jar dist/aMFEA.jar MaTGA 50
	pause
endlocal
goto :eof