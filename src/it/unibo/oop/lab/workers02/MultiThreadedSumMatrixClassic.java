package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MultiThreadedSumMatrixClassic implements SumMatrix {
    private final int numThreads;
    /**
     * Builds a new {@link MultiThreadedSumMatrixClassic}.
     * @param numThreads
     */
    public MultiThreadedSumMatrixClassic(final int numThreads) {
        this.numThreads = numThreads;
    }
    /**
     *
     */
    private final class Worker extends Thread {
        private double result;
        private final double[][] matrix;
        private final int start;
        private final int end;
        /**
         * Builds a new {@link Worker}.
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
         * 
         * @return the sum of a row of the matrix
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
        final List<Worker> workers = new ArrayList<>();
        final int size = matrix.length % this.numThreads + matrix.length / this.numThreads;
        int finalPos;
        for (int i = 0; i < matrix.length; i += size) {
            finalPos = (i + size) < matrix.length ? size : matrix.length - i - 1;
            workers.add(new Worker(matrix, i, finalPos));
        }
        for (final Worker worker: workers) {
            worker.start();
        }
        double sum = 0;
        for (final Worker worker: workers) {
            try {
                worker.join();
                System.out.println(worker.getResult());
                sum += worker.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        System.out.println(sum);
        return sum;
    }
}
