# Flight Finder 

Initialisation : This is a spring boot project. Initialisation takes ~38 seconds as we are creating path till 3 hops. Intitialisation time will be increased or decreased based on no of hops.

Usage :

  http://localhost:8080/get-flights/DOH/BLR?kNoOfRoutes=7
  
  Sample output :
  
  [ { 'DOH_BLR' : { '1723' : 385 } } , { 'DOH_HYD_BLR' : { '1714_6319' : 595 } , 'DOH_COK_BLR' : { '1704_322' : 640 } , 'DOH_CCJ_BLR' : { '1712_2487' : 665 } , 'DOH_DEL_BLR' : { '1737_2175' : 670 } , 'DOH_BOM_BLR' : { '1718_432' : 695 } , 'DOH_HYD_COK_BLR' : { '1726_6334_375' : 810 } } ]
  
TODO :

  Exception Handling | Logging | Provision to add paths where we need to have more than 3 hops
  
