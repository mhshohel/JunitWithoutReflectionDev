/**
 *
 * @ProjectName JUnitWithoutReflection
 *
 * @FileName TestSquareMatrixOutput.java
 *
 * @FileCreated May 09, 2014 - 17:10:15
 *
 * @Author MD. SHOHEL SHAMIM
 *
 * @CivicRegistration 19841201-0533
 *
 * MSc. in Software Technology
 *
 * Linnaeus University, Växjö, Sweden
 *
 */
package dv103.test;

import dv103.test.TestSquareMatrix;

public class TestSquareMatrixOutput {
    public static void main(String[] args) {
        try {
            TestSquareMatrix testSquareMatrix = new TestSquareMatrix();
            TestSquareMatrix.setUpBeforeClass();
            testSquareMatrix.setUp();
            testSquareMatrix.testBasicJavaSupport();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testConstructors();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testGetColumn();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testGetCopy();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testGetNull();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testGetOne();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testGetRow();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testHashCode();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testIsDiagonal();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testIsSymmetric();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testMult();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testPlus();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testSetAndGetElementAt();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testSparseness();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testSquareMatrixBasicOps();
            testSquareMatrix.tearDown();
            testSquareMatrix.setUp();
            testSquareMatrix.testTranspose();
            testSquareMatrix.tearDown();
            TestSquareMatrix.tearDownAfterClass();
        }catch(Exception e) {
            e.printStackTrace();
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
