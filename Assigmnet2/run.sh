mpic++ main.cpp -o rsacracker -lboost_mpi -lboost_serialization -lboost_timer -lboost_system
sh ~/scpsh.sh rsacracker edgarsg
mpirun -hostfile /home/itmuser/hosts -np 10 --wdir /home/osboxes/2PC/edgarsg/ rsacracker

