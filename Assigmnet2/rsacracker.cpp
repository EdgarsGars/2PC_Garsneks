#include <boost/mpi/environment.hpp>
#include <boost/mpi/communicator.hpp>
#include <boost/math/special_functions/prime.hpp>
#include <iostream>
namespace mpi = boost::mpi;

int main()
{
  mpi::environment env;
  mpi::communicator world;
  int N = boost::math::prime(30) * boost::math::prime(100);

    while(!done) {

    if (world.rank() == 0) {
      /* Do something at root process if needed */
      std::cout << "Key to crack: " << N << endl;
    }

    /* 
       Simple serial pseidoalgorithm for finding P and Q

       P = a_prime
       Q = next_prime_after(P)

       while P != Q && P*Q != N 
          Q = next_prime
		if ( Q >= sqrt(N) )
	    P = next_prime
	    Q = next_prime_after(P)

    */
	
	// TODO implement the serial (or other prime cracker) algorithm in parallel, using OpenMP and Boost.MPI
    
    
  }
  
  return 0;
}