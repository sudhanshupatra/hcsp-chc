echo "CHC_LAN.cfg" > Config.cfg
echo "../../ProblemInstances/HCSP/1024x32.mod/A.u_i_hihi" >> Config.cfg
echo "res/lan.sol.txt" >> Config.cfg
time(make LAN4 > res/A.u_c_hihi_LAN4.log)
