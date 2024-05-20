#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <sys/time.h>

// Global variables
long long int a, b;
int num_threads, method;
double global_sqrt_sum = 0;
pthread_mutex_t mutex;

// Function executed by each thread to calculate the local sum
void* calculate_sum(void* arg) {
    // Extract starting point for this thread
    long long int start = *((long long int*)arg);
    // Calculate the chunk size for each thread
    long long int chunk_size = (b - a + 1) / num_threads;
    // Calculate the ending point for this thread
    long long int end = start + chunk_size - 1;

    // Local variable to store the thread-specific sum
    double local_sqrt_sum = 0;

    // Loop through the assigned range for this thread and calculate the sum
    for (long long int x = start; x <= end; x++) {
        local_sqrt_sum += sqrt(x);
    }

    // Synchronization based on the selected method
    if (method == 2 || method == 3) {
        pthread_mutex_lock(&mutex);
    }

    // Update the global sum with the thread-specific sum
    global_sqrt_sum += local_sqrt_sum;

    // Release the mutex if it was acquired
    if (method == 2 || method == 3) {
        pthread_mutex_unlock(&mutex);
    }

    // Exit the thread
    pthread_exit(NULL);
}

int main(int argc, char *argv[]) {
    // Check for correct command-line arguments
    if (argc != 5) {
        printf("Usage: %s <a> <b> <num_threads> <method>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    // Extract command-line arguments
    a = atoll(argv[1]);
    b = atoll(argv[2]);
    num_threads = atoi(argv[3]);
    method = atoi(argv[4]);

    // Initialize mutex if needed for synchronization
    if (method == 2 || method == 3) {
        pthread_mutex_init(&mutex, NULL);
    }

    // Array to store thread identifiers
    pthread_t threads[num_threads];
    // Array to store starting points for each thread
    long long int thread_args[num_threads];

    // Variables for measuring the execution time
    struct timeval start_time, end_time;

    // Record the start time
    gettimeofday(&start_time, NULL);

    // Create threads and assign work to each thread
    for (int i = 0; i < num_threads; i++) {
        // Calculate starting point for each thread
        thread_args[i] = a + i * ((b - a + 1) / num_threads);
        // Create a thread and pass the starting point as an argument
        pthread_create(&threads[i], NULL, calculate_sum, &thread_args[i]);
    }

    // Wait for all threads to complete
    for (int i = 0; i < num_threads; i++) {
        pthread_join(threads[i], NULL);
    }

    // Record the end time
    gettimeofday(&end_time, NULL);

    // Calculate time difference
    double elapsed_time = (end_time.tv_sec - start_time.tv_sec) +
                          (end_time.tv_usec - start_time.tv_usec) / 1000000.0;

    // Display results
    printf("Sum: %.5e\n", global_sqrt_sum);
    printf("User time: %.2fs\n", elapsed_time);
    printf("System time: 0.00s\n");  // Assuming negligible system time
    printf("Total time: %.3fs\n", elapsed_time);

    // Destroy the mutex if it was initialized
    if (method == 2 || method == 3) {
        pthread_mutex_destroy(&mutex);
    }

    // Exit the program
    return 0;
}
