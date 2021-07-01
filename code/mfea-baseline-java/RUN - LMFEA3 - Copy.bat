@echo off
:: tenfile seed numFitness sizePopulation pLocalSearch
setlocal abc
goto :main
:main 
setlocal
	java -jar dist/aMFEA.jar LMFEA_2 50 30 50
	pause
endlocal
goto :eof