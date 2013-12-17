/*
 * File: BenchmarkMain.java
 * Author: Jonas Lundberg
 * Date: 25 feb 2010
 */
package dv103.benchmark;

/**
 * @author jonasl
 * 
 */
public class BenchmarkMain {

    public static void main(String[] args) {
	/* Run Competition Benchmark */
	final int SIZE = 100; // 100 will be used the competition
	MatrixBenchmark mbm = new MatrixBenchmark(SIZE);

	double time = 0.0;
	time += mbm.runGeneralPerformance();
	time += mbm.runMultiplyDense();
	time += mbm.runMultiplySparse();

	System.out.println("\nTotal Time: " + time);
    }
}
