#include <boost/mpi.hpp>
#include <boost/mpi/environment.hpp>
#include <boost/mpi/communicator.hpp>
#include <omp.h>

#include <boost/multiprecision/cpp_int.hpp>
#include <boost/serialization/string.hpp>
#include <boost/math/special_functions/prime.hpp>
#include <boost/container/vector.hpp>
#include <iostream>
#include <time.h>
#include "randomInterval.h"

#define STARTPOS 1
#define ENDPOS 2
#define TARGET 3
#define WORKFINISHED 4
#define SOLUTIONFOUND 5
#define ABORT 6


namespace mpi = boost::mpi;
namespace big = boost::multiprecision;
namespace vec = boost::container;

int main() {
    mpi::environment env;
    mpi::communicator world;

    if (world.rank() == 0) {
       masterNode();
       //masterNodeRandom();
    } else {
         slaveNode();
        //slaveNodeRandom();
    }
    return 0;
}
//       
//    return 0;

//    mpi::environment env;
//    mpi::communicator world;
//    double wall_timer = omp_get_wtime();
//
//    if (world.rank() == 0) {
//        //big::cpp_int N = boost::math::prime(5000) * boost::math::prime(6000);
//        //big::cpp_int N = 819173l * 226337l;
//        vec::vector<big::cpp_int> chunks;
//        big::cpp_int N = 15485077ull * 15485077ull;
//        big::cpp_int sqrtN = big::sqrt(N);
//        long chunkCount = world.size()*2;
//        std::cout << "Key to crack: " << N << std::endl;
//        std::cout << "SQRT " << sqrtN << std::endl;
//        big::cpp_int perNode = sqrtN / chunkCount;
//
//        for (int i = 0; i < chunkCount; i++) {
//            chunks.push_back(perNode * i);
//            std::cout << perNode * i << std::endl;
//        }
//
//        big::cpp_int P = 0;
//        boost::mpi::request solution = world.irecv(mpi::any_source, SOLUTIONFOUND, P);
//        boost::mpi::request workload[world.size() - 1];
//        std::cout << "Node count " << world.size() << " perNode " << perNode << std::endl;
//
//        bool init = false;
//        while (P == 0) {
//            for (int i = 1; i < world.size(); i++) {
//                if (!solution.test()) {
//                    if (!init || workload[i - 1].test()) {
//                        std::cout << i << " Node finished" << std::endl;
//                        int idx = rand() % chunks.size();
//                        //assign new work
//                        big::cpp_int start = chunks[idx];
//                        chunks.erase(chunks.begin() + idx);
//                        big::cpp_int end = start + perNode + 1;
//                        if (start == 0) {
//                            start += 3;
//                        }
//                        world.send(i, STARTPOS, start);
//                        world.send(i, ENDPOS, end);
//                        if (!init) {
//                            world.send(i, TARGET, N);
//                        }
//                        std::cout << i << " work on " << start << " " << end << std::endl;
//                        workload[i - 1] = world.irecv(i, WORKFINISHED);
//                    }
//                } else {
//                    break;
//                }
//            }
//            init = true;
//        }
//        std::cout << "Time to solve " << omp_get_wtime() - wall_timer << std::endl;
//        for (int i = 1; i < world.size(); i++) {
//            world.send(i, ABORT);
//        }
//        std::cout << "Found solution! " << P << "*" << N / P << "=" << P * (N / P) << std::endl;
//
//    } else {
//        boost::mpi::request r = world.irecv(0, ABORT);
//        big::cpp_int N = 0;
//        big::cpp_int start = 0;
//        big::cpp_int end = 0;
//        boost::mpi::request r3 = world.irecv(0, TARGET, N);
//        while (true) {
//            start = 0;
//            end = 0;
//
//            boost::mpi::request r1 = world.irecv(0, STARTPOS, start);
//            boost::mpi::request r2 = world.irecv(0, ENDPOS, end);
//
//            while (N == start && N == end) {
//                r1.test();
//                r2.test();
//                r3.test();
//                if (r.test()) {
//                    std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] aborted work" << std::endl;
//                    return 0;
//                }
//            }
//
//            std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] starting work [" << start << "," << end << "]" << std::endl;
//            if (start % 2 == 0) {
//                start--;
//            }
//#pragma omp parallel for num_threads(3)
//            for (big::cpp_int i = start; i <= end; i += 2) {
//                if (N % i == 0 && isPrime(i)) {
//
//                    world.send(0, SOLUTIONFOUND, i);
//                    break;
//                } else if (r.test()) {
//                    std::cout <<                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   "Node " << env.processor_name() << "[" << world.rank() << "] aborted work" << std::endl;
//                    world.send(0, WORKFINISHED);
//                    return 0;
//                }
//            }
//            world.send(0, WORKFINISHED);
//        }
//    }
//    std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] finished work" << std::endl;
//    return 0;



