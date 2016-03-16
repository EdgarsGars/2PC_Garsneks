#include <omp.h>
#include <math.h>
#include <time.h>
#include <iostream>
using namespace std;

int main(int argc, char *argv[]) {
    int N = 100000;
    long a[N];
    long b[N];
    long c[N];
    a[0] = 1;
    b[0] = 1;
    c[0] = 1;
    double wall_timer;


    wall_timer = omp_get_wtime();
    for (int i = 1; i < N; i++) {
        c[i] = (c[i - 1] + 1)*2;
    }
    cout << "\nTime with initial formula : " << omp_get_wtime() - wall_timer << "\n";

    wall_timer = omp_get_wtime();
    for (int i = 1; i < N; i++) {
        b[i] = pow(2, i - 1) * a[0] + pow(2, i) - 2;
    }
    cout << "\nTime with general formula: " << omp_get_wtime() - wall_timer << "\n";

    wall_timer = omp_get_wtime();
#pragma omp parallel for num_threads(3)
    for (int i = 1; i < N; i++) {
        a[i] = pow(2, i - 1) * a[0] + pow(2, i) - 2;
    }
    cout << "\nTime with parallel formula: " << omp_get_wtime() - wall_timer << "\n";

    //Testing single case
    int r[3] = {10, 10000, 100000};
    for (int n = 0; n < 3; n++) {

        wall_timer = omp_get_wtime();
        for (int i = 1; i < r[n]; i++) {
            c[i] = (c[i - 1] + 1)*2;
        }
        cout << "\nTime with initial formula to calculate " << r[n] << " : " << omp_get_wtime() - wall_timer << "\n";

        wall_timer = omp_get_wtime();
        long t = pow(2, r[n] - 1) * a[0] + pow(2, r[n]) - 2;
        cout << "\nTime with custom formula to calculate " << r[n] << " : " << omp_get_wtime() - wall_timer << "\n";


    }





    return 0;
}