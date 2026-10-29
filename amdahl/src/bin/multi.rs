use std::time::Instant;

const THREADS: usize = 8;

const N: usize = 1000;

fn main() {
    // Start timer.
    let then = Instant::now();

    // Matrices (2D) stored as flat-arrays (1D).
    let a = vec![0; N * N];
    let b = vec![0; N * N];

    matrix_multiply(&a, &b);

    // Get time after computation finished.
    let now = Instant::now();

    // Get duration that passed between.
    println!("Time: {:?}", now.duration_since(then));
}

use std::thread;

fn matrix_multiply(a: &[i32], b: &[i32]) -> Vec<i64> {
    assert_eq!(a.len(), N * N);
    assert_eq!(b.len(), N * N);

    let mut c = vec![0; N * N];

    // Divide rows evenly between threads.
    let rows_per_thread = N.div_ceil(THREADS);

    // Begin a group of threads.
    thread::scope(|scope| {
        // Each chunk of the matrix is the rows per thread with N columns.
        for (chunk_index, c_chunk) in c.chunks_mut(rows_per_thread * N).enumerate() {
            let start_row = chunk_index * rows_per_thread;

            // Spawn a thread.
            scope.spawn(move || {
                // Rows.
                for (i, c_row) in c_chunk.chunks_mut(N).enumerate() {
                    let i = start_row + i;

                    // Columns.
                    for (j, cell) in c_row.iter_mut().enumerate() {
                        let mut sum: i64 = 0;

                        // Traverse a's rows and b's columns and multiply them.
                        for k in 0..N {
                            // Row i, column k.
                            let compute_a = i64::from(a[i * N + k]);

                            // Row k, column j.
                            let compute_b = i64::from(b[k * N + j]);

                            sum += compute_a * compute_b;
                        }

                        // Update value at each position with the result of the sum.
                        *cell = sum;
                    }
                }
            });
        }
    });

    c
}
