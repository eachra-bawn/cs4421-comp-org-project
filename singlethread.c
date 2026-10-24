#define _POSIX_C_SOURCE 199309L
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define N 1800

static double seconds(void) {
  struct timespec ts;
  clock_gettime(CLOCK_MONOTONIC, &ts);
  return ts.tv_sec + ts.tv_nsec / 1e9;
}

int main(void) {
  const size_t cells = (size_t)N * N;
  double *a = malloc(cells * sizeof(*a));
  double *b = malloc(cells * sizeof(*b));
  double *c = calloc(cells, sizeof(*c));

  if (!a || !b || !c) {
    fprintf(stderr, "Could not allocate matrices\n");
    free(a);
    free(b);
    free(c);
    return 1;
  }

  // Deterministic initialization, kept outside the timed computation.
  for (size_t i = 0; i < cells; ++i) {
    a[i] = (double)((i * 17 + 3) % 101) / 101.0;
    b[i] = (double)((i * 29 + 7) % 103) / 103.0;
  }

  double start = seconds();

  // Single-threaded matrix multiplication: C = A × B.
  for (int i = 0; i < N; ++i) {
    for (int k = 0; k < N; ++k) {
      double aik = a[(size_t)i * N + k];
      for (int j = 0; j < N; ++j) {
        c[(size_t)i * N + j] += aik * b[(size_t)k * N + j];
      }
    }
  }

  double elapsed = seconds() - start;

  // Consume the result so the computation's output is observable.
  double checksum = 0.0;
  for (size_t i = 0; i < cells; ++i) {
    checksum += c[i];
  }

  printf("Matrix size: %d x %d\n", N, N);
  printf("Elapsed: %.6f seconds\n", elapsed);
  printf("Checksum: %.12f\n", checksum);

  free(a);
  free(b);
  free(c);
  return 0;
}
