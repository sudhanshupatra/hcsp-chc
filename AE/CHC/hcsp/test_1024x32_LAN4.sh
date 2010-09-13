#!/bin/bash

CFG_FILE="CHC_LAN4.cfg"
MAKE_CFG="LAN4"

echo $CFG_FILE > Config.cfg
echo "../../ProblemInstances/HCSP/1024x32.mod/A.u_i_hihi" >> Config.cfg
echo "res/$MAKE_CFG.sol.txt" >> Config.cfg
time(make $MAKE_CFG > res/A.u_c_hihi_$MAKE_CFG.log)
