use std::time::Instant;

const N: usize = 1000;

fn main() {
    // Start timer.
    let then = Instant::now();

    // Matrices (2D) stored as flat-arrays (1D)Chat
    let a = vec![0; N * N];
    let b = vec![0; N * N];

    matrix_multiply(&a, &b);

    // Get time after computation finished.
    let now = Instant::now();

    // Get duration that passed between.
    println!("Time: {:?}", now.duration_since(then));
}

fn matrix_multiply(a: &[i32], b: &[i32]) -> Vec<i64> {
    assert_eq!(a.len(), N * N);
    assert_eq!(b.len(), N * N);

    let mut c: Vec<i64> = vec![0; N * N];

    // Rows
    for i in 0..N {
        // Columns
        for j in 0..N {
            let mut sum: i64 = 0;

            // Traverse a's rows and b's columns and multiply them
            for k in 0..N {
                // Row i, column k
                let compute_a = i64::from(a[i * N + k]);

                // Row k, column j
                let compute_b = i64::from(b[k * N + j]);

                sum += compute_a * compute_b;
            }
            c[i * N + j] = sum;
        }
    }

    c
}
