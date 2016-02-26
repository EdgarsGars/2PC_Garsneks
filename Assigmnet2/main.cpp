#include <boost/mpi.hpp>
#include <boost/mpi/environment.hpp>
#include <boost/mpi/communicator.hpp>
#include <omp.h>
#include <boost/serialization/string.hpp>
#include <boost/math/special_functions/prime.hpp>
#include <iostream>
namespace mpi = boost::mpi;

int main() {
    mpi::environment env;
    mpi::communicator world;
    bool done = false;
    while (!done) {
        unsigned long N = boost::math::prime(5000) * boost::math::prime(4000);
        double sqrtN = std::floor(std::sqrt(N));
        if (world.rank() == 0) {
            /* Do something at root process if needed */
            std::cout << "Key to crack: " << N << std::endl;
            std::cout << "SQRT " << sqrtN << std::endl;
            if ((int) sqrtN % 2 == 0) {
                sqrtN--;
            }
            int nodeCount = world.size();
            int perNode = sqrtN / nodeCount;
            for (int i = 1; i < nodeCount; i++) {
                world.send(i, 0, perNode);
                world.send(i, 1, N);
            }

            for (long i = 1; i < perNode; i += 2) {
                if (N % i == 0) {
                    std::cout << "P " << i << std::endl;
                    std::cout << "Q " << N / i << std::endl;
                }
            }

            std::cout << "Node count " << nodeCount << " perNode " << perNode << std::endl;

        } else {
            int perNode;
            unsigned long N;
            world.recv(0, 0, perNode);
            world.recv(0, 1, N);
            // std::cout << env.processor_name() << ": Check from " << world.rank() * perNode << " to " << world.rank() * perNode + perNode << std::endl;
            long start = perNode * world.rank();
            if (start % 2 == 0) {
                start--;
            }
#pragma omp parallel for num_threads(3)
            for (long i = start; i < perNode * world.rank() + perNode; i += 2) {
                if (N % i == 0) {
                    std::cout << env.processor_name() << ": P " << i << std::endl;
                    std::cout << env.processor_name() << ": Q " << N / i << std::endl;
                }
            }
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
        break;

    }

    return 0;
}