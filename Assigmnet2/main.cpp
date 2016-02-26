#include <boost/mpi.hpp>
#include <boost/mpi/environment.hpp>
#include <boost/mpi/communicator.hpp>
#include <omp.h>

#include <boost/multiprecision/cpp_int.hpp>
#include <boost/serialization/string.hpp>
#include <boost/math/special_functions/prime.hpp>
#include <iostream>
#include <time.h>


#define STARTPOS 1
#define ENDPOS 2
#define TARGET 3
#define SOLUTIONFOUND 4
#define ABORT 5

namespace mpi = boost::mpi;
namespace big = boost::multiprecision;

int main() {
    mpi::environment env;
    mpi::communicator world;
    double wall_timer = omp_get_wtime();

    if (world.rank() == 0) {
        //big::cpp_int N = boost::math::prime(5000) * boost::math::prime(6000);
        //big::cpp_int N = 819173l * 226337l;
        //big::cpp_int N = 15485077ull * 15485471ul;
        big::cpp_int sqrtN = big::sqrt(N);

        std::cout << "Key to crack: " << N << std::endl;
        std::cout << "SQRT " << sqrtN << std::endl;

        if (sqrtN % 2 == 0) {
            sqrtN--;
        }
        big::cpp_int P = 0;
        boost::mpi::request r = world.irecv(mpi::any_source, SOLUTIONFOUND, P);
        int nodeCount = world.size();
        big::cpp_int perNode = sqrtN / nodeCount;
        std::cout << "Node count " << nodeCount << " perNode " << perNode << std::endl;
#pragma omp parallel for num_threads(3)
        for (int i = 1; i < nodeCount; i++) {
            big::cpp_int start = i*perNode;
            big::cpp_int end = start + perNode;
            world.send(i, STARTPOS, start);
            world.send(i, ENDPOS, end);
            world.send(i, TARGET, N);
        }
#pragma omp parallel for num_threads(3)
        for (big::cpp_int i = 3; i < perNode; i += 2) {
            if (N % i == 0) {
                world.send(0, SOLUTIONFOUND, i);
                break;
            }
        }
        std::cout << "Waiting.. " << P << std::endl;
        while (!r.test()) {
        }

        std::cout << "Time to solve " << omp_get_wtime() - wall_timer << std::endl;
        for (int i = 1; i < nodeCount; i++) {
            world.send(i, ABORT, 1);
        }
        std::cout << "Found solution! " << P << "*" << N / P << "=" << P * (N / P) << std::endl;

    } else {
        big::cpp_int abort = 0;
        boost::mpi::request r = world.irecv(0, ABORT, abort);
        big::cpp_int N;
        big::cpp_int start;
        big::cpp_int end;

        world.recv(0, STARTPOS, start);
        world.recv(0, ENDPOS, end);
        world.recv(0, TARGET, N);

        std::cout << "Node " << world.rank() << " starting to work" << std::endl;
        if (start % 2 == 0) {
            start--;
        }
#pragma omp parallel for num_threads(3)
        for (big::cpp_int i = start; i < end; i += 2) {
            if (N % i == 0) {
                world.send(0, SOLUTIONFOUND, i);
                break;
            } else if (r.test()) {
                std::cout << "Node " << world.rank() << " aborted work" << std::endl;
                return 0;
            }
        }
    }

    std::cout << "Node " << world.rank() << " finished work" << std::endl;
    return 0;
}