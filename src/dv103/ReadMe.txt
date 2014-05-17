The dv103 package version 2011-03-15
=====================================

This is the dv103 package documentation for final competition PA4. 

Changes since PA3
=================
1. Test Suite is updated:
In order to make sure that the teams taking part in the competition has a generic
implementation of the RMatrix interface, i.e. SquareMatrix, we have added few 
more tests in the test suite. In addition to the matrices of element type "Int" we
now test that implementation also works with any other element type that implements
Ring interface.  See dv103.test.TestSquareMatrix for more the updates.

2. A new implementation of the Ring interface, Real.java" has been added to the dv103 package

Changes since PA2
=================
* We have removed the methods multRecursive() and multStrassen()
  from the RMatrix interface. We want you to put all your focus on a 
  single multiplication implementation mult(). This is the one that 
  will be tested in competition benchmark. We have also made corresponding
  changes in the JUnit test TestSquareMatrix.
  
* We have included a new package dv103.benchmark containing the benchmark
  to be used in the competition. This one is new, please contact Nadeem or 
  Jonas if you find any errors/problems when testing the benchmark.
  
The Matrix Benchmark
======================
* The matrix benchmark contains three classes (BenchmarkMain, 
  MatrixBenchmark and SquareMatrixGenerator). BenchmarkMain is 
  the program entry point.
  
* To run the benchmark on your matrix implementation you must
  instruct the matrix generator SquareMatrixGenerator to start
  using your matrix implementation (rather than ours). Thus,
  in the file SquareMatrixGenerator.java, replace
      import teachers_default.SquareMatrix;
  with a new import statement pointing to you implementation.
  That's it (hopefully).
  
* In the file BenchmarkMain.java there is a parameter SIZE which
  determines the size of the matrices used in benchmark. SIZE = 100
  will be used in the competition. If SIZE = 100 causes problem when 
  you first run the benchmark, try a lower value like 20 or 50.

Further
========
* Methods toString() and hashCode() should take entire matrix into account, and not
  just few of the entries there. 
