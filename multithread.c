#define _POSIX_C_SOURCE 199309L
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define N 1800
#define THREADS 8

static double *a, *b, *c;

typedef struct {
  int first_row;
  int end_row; // Exclusive
} Task;

static double seconds(void) {
  struct timespec ts;
  clock_gettime(CLOCK_MONOTONIC, &ts);
  return ts.tv_sec + ts.tv_nsec / 1e9;
}

static void *multiply_rows(void *arg) {
  Task *task = arg;

  for (int i = task->first_row; i < task->end_row; ++i) {
    for (int k = 0; k < N; ++k) {
      double aik = a[(size_t)i * N + k];
      for (int j = 0; j < N; ++j) {
        c[(size_t)i * N + j] += aik * b[(size_t)k * N + j];
      }
    }
  }

  return NULL;
}

int main(void) {
  const size_t cells = (size_t)N * N;
  a = malloc(cells * sizeof(*a));
  b = malloc(cells * sizeof(*b));
  c = calloc(cells, sizeof(*c));

  if (!a || !b || !c) {
    fprintf(stderr, "Could not allocate matrices\n");
    free(a);
    free(b);
    free(c);
    return 1;
  }

  for (size_t i = 0; i < cells; ++i) {
    a[i] = (double)((i * 17 + 3) % 101) / 101.0;
    b[i] = (double)((i * 29 + 7) % 103) / 103.0;
  }

  pthread_t threads[THREADS];
  Task tasks[THREADS];

  double start = seconds();

  for (int t = 0; t < THREADS; ++t) {
    tasks[t].first_row = t * N / THREADS;
    tasks[t].end_row = (t + 1) * N / THREADS;

    int error = pthread_create(&threads[t], NULL, multiply_rows, &tasks[t]);
    if (error != 0) {
      fprintf(stderr, "pthread_create failed for thread %d\n", t);
      return 1;
    }
  }

  for (int t = 0; t < THREADS; ++t) {
    pthread_join(threads[t], NULL);
  }

  double elapsed = seconds() - start;

  double checksum = 0.0;
  for (size_t i = 0; i < cells; ++i) {
    checksum += c[i];
  }

  printf("Matrix size: %d x %d\n", N, N);
  printf("Threads: %d\n", THREADS);
  printf("Elapsed: %.6f seconds\n", elapsed);
  printf("Checksum: %.12f\n", checksum);

  free(a);
  free(b);
  free(c);
  return 0;
}
