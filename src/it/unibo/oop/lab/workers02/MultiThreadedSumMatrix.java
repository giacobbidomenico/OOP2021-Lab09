package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the sum of the elements of an array using threads.
 */
public class MultiThreadedSumMatrix implements SumMatrix {
    private final int numThreads;
    /**
     * Builds a new {@link MultiThreadedSumMatrix}.
     * @param numThreads
     */
    public MultiThreadedSumMatrix(final int numThreads) {
        if (numThreads < 1) {
            throw new IllegalStateException("Number of thread incorrect");
        }
        this.numThreads = numThreads;
    }
    /**
     * Class that implements a threads that takes care of adding the rows of the matrix.
     */
    private final class Worker extends Thread {
        private double result;
        private final double[][] matrix;
        private final int start;
        private final int end;
        /**
         * Builds a new {@link Worker}.
         * 
         * @param matrix
         * @param start
         * @param end
         */
        private Worker(final double[][] matrix, final int start, final int end) {
            this.matrix = matrix;
            this.start = start;
            this.end = end;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            for (int i = this.start; i < this.end; i++) {
                for (final double current: matrix[i]) {
                    this.result += current;
                }
            }
        }
        /**
         * Method that returns the sum of a series of rows of the matrix.
         * @return the sum of a series of rows of the matrix
         */
        public double getResult() {
            return this.result;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % this.numThreads + matrix.length / this.numThreads;
        final List<Worker> workers = new ArrayList<>();
        int finalPos;
        /*
         * Create threads.
         */
        //int index = 0;
        for (int i = 0; i < matrix.length; i += size) {
            finalPos = (i + size) <= matrix.length ? size + i : matrix.length;
            workers.add(new Worker(matrix, i, finalPos));
            /*
             *index++;
             *System.out.println("Thread: " + index + " sum from row:" + i + " to row:" + finalPos);
             */
        }
        /*
         * Start threads.
         */
        for (final Worker worker: workers) {
            worker.start();
        }
        /*
         * Wait threads and sum the results
         */
        double sum = 0;
        for (final Worker worker: workers) {
            try {
                worker.join();
                sum += worker.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
}
