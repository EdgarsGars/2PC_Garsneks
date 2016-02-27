/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

bool isPrime(big::cpp_int number) {
    for (big::cpp_int i = 2; i < big::sqrt(number); i += 2) {
        if (number % i == 0) {
            return false;
        }
    }
    return true;
}

void masterNode() {
    double wall_timer = omp_get_wtime();
    mpi::environment env;
    mpi::communicator world;
    //big::cpp_int N = boost::math::prime(5000) * boost::math::prime(6000);
    //big::cpp_int N = 819173l * 226337l;
    vec::vector<big::cpp_int> chunks;

    big::cpp_int N = 15485077ull * 819173l;
    big::cpp_int sqrtN = big::sqrt(N);
    std::cout << "Key to crack: " << N << std::endl;
    std::cout << "SQRT " << sqrtN << std::endl;

    big::cpp_int P = 0;
    boost::mpi::request r = world.irecv(mpi::any_source, SOLUTIONFOUND, P);
    int nodeCount = world.size();
    big::cpp_int perNode = sqrtN / nodeCount;
    std::cout << "Node count " << nodeCount << " perNode " << perNode << std::endl;
#pragma omp parallel for num_threads(3)
    for (int i = 1; i < nodeCount; i++) {
        big::cpp_int start = i*perNode;
        big::cpp_int end = start + perNode + 1;
        world.send(i, STARTPOS, start);
        world.send(i, ENDPOS, end);
        world.send(i, TARGET, N);
    }

    big::cpp_int start = 3;
    big::cpp_int end = perNode;
    std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] starting work [" << start << "," << end << "]" << std::endl;
#pragma omp parallel for num_threads(3)
    for (big::cpp_int i = start; i < end; i += 2) {
        if (N % i == 0 && isPrime(i)) {
            world.send(0, SOLUTIONFOUND, i);
            break;
        }
    }
    std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] finished work" << std::endl;
    while (!r.test()) {
    }

    std::cout << "Time to solve " << omp_get_wtime() - wall_timer << std::endl;
#pragma omp parallel for num_threads(3)
    for (int i = 1; i < nodeCount; i++) {
        world.send(i, ABORT);
    }
    std::cout << "Found solution! " << P << "*" << N / P << "=" << P * (N / P) << std::endl;


}

void slaveNode() {
    mpi::environment env;
    mpi::communicator world;
    boost::mpi::request r = world.irecv(0, ABORT);
    big::cpp_int N;
    big::cpp_int start;
    big::cpp_int end;

    world.recv(0, STARTPOS, start);
    world.recv(0, ENDPOS, end);
    world.recv(0, TARGET, N);

    std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] starting work [" << start << "," << end << "]" << std::endl;
    if (start % 2 == 0) {
        start--;
    }
#pragma omp parallel for num_threads(3)
    for (big::cpp_int i = start; i <= end; i += 2) {
        if (N % i == 0) {
            world.send(0, SOLUTIONFOUND, i);
            break;
        } else if (r.test()) {
            std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] aborted work" << std::endl;
            return;
        }
    }


    std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] finished work" << std::endl;

}

void masterNodeRandom() {
    srand(time(NULL));
    double wall_timer = omp_get_wtime();
    mpi::environment env;
    mpi::communicator world;
    big::cpp_int N = 15485077ull * 819173l;
    big::cpp_int sqrtN = big::sqrt(N);
    std::cout << "Key to crack: " << N << std::endl;
    std::cout << "SQRT " << sqrtN << std::endl;

    vec::vector<big::cpp_int> chunks;

    /*
        Generate interval chunks
     */

    long chunkCount = 100;
    big::cpp_int perNode = sqrtN / chunkCount;

    for (int i = 0; i < chunkCount; i++) {
        chunks.push_back(perNode * i);
    }
    chunks.push_back(sqrtN);

    /*
         Send out random chunks
     */
    big::cpp_int P = 0;
    boost::mpi::request solution = world.irecv(mpi::any_source, SOLUTIONFOUND, P);
    boost::mpi::request workload[world.size() - 1];

    bool init = false;
    while (P == 0) {
#pragma omp parallel for num_threads(3)
        for (int i = 1; i < world.size(); i++) {
            if (!solution.test()) {

                if (!init || workload[i - 1].test()) {
                    if (chunks.size() != 0) {
                        int idx = rand() % chunks.size();
                        big::cpp_int start = chunks[idx];
                        big::cpp_int end = start + perNode + 1;
                        if (start == 0) {
                            start += 3;
                        }
                        chunks.erase(chunks.begin() + idx);

                        world.send(i, STARTPOS, start);
                        world.send(i, ENDPOS, end);
                        world.send(i, TARGET, N);

                        workload[i - 1] = world.irecv(i, WORKFINISHED);
                    }
                }
            } else {
                std::cout << "Time to solve " << omp_get_wtime() - wall_timer << std::endl;
                std::cout << "Found solution! " << P << "*" << N / P << "=" << P * (N / P) << std::endl;
#pragma omp parallel for num_threads(3)
                for (int i = 1; i < world.size(); i++) {
                    world.send(i, ABORT);
                }
                break;
            }


        }
        init = true;
    }
}

void slaveNodeRandom() {
    mpi::environment env;
    mpi::communicator world;
    boost::mpi::request r = world.irecv(0, ABORT);

    big::cpp_int N;
    big::cpp_int start;
    big::cpp_int end;
    while (true) {
        start = 0;
        end = 0;
        boost::mpi::request startR = world.irecv(0, STARTPOS, start);
        boost::mpi::request endR = world.irecv(0, ENDPOS, end);
        boost::mpi::request NR = world.irecv(0, TARGET, N);

        while (!startR.test() && !endR.test() && !NR.test()) {
            if (r.test()) {
                return;
            }
        }

        // std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] starting work [" << start << "," << end << "]" << std::endl;


        if (start % 2 == 0) {
            start--;
        }
#pragma omp parallel for num_threads(3)
        for (big::cpp_int i = start; i <= end; i += 2) {
            if (N % i == 0 && isPrime(i)) {
                world.send(0, SOLUTIONFOUND, i);
                break;
            } else if (r.test()) {
                //  std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] aborted work" << std::endl;
                return;
            }
        }

        //std::cout << "Node " << env.processor_name() << "[" << world.rank() << "] finished work" << std::endl;
        world.send(0, WORKFINISHED);
    }
}




