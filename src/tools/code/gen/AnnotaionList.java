/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName AnnotaionList.java
 * 
 * @FileCreated Jan 01, 2013
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
package tools.code.gen;

/**
 * <li><strong><i>AnnotaionList</i></strong></li>
 * 
 * <pre>
 * Interface AnnotaionList
 * </pre>
 * <p>
 * List of all Annotation of JUnit 4.0 - 4.10. This interface is used to read
 * annotation's name easily as String format.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public interface AnnotaionList {
    /**
     * After represents the name of - org.junit.After
     */
    public static final String After = "After";
    /**
     * AfterClass represents the name of - org.junit.AfterClass
     */
    public static final String AfterClass = "AfterClass";
    /**
     * Before represents the name of - org.junit.Before
     */
    public static final String Before = "Before";
    /**
     * BeforeClass represents the name of - org.junit.BeforeClass
     */
    public static final String BeforeClass = "BeforeClass";
    /**
     * Categories represents the name of -
     * org.junit.experimental.categories.Categories
     */
    public static final String Categories = "Categories";
    /**
     * Category represents the name of -
     * org.junit.experimental.categories.Category
     */
    public static final String Category = "Category";
    /**
     * ClassRule represents the name of - org.junit.ClassRule
     */
    public static final String ClassRule = "ClassRule";
    /**
     * DataPoint represents the name of -
     * org.junit.experimental.theories.DataPoint
     */
    public static final String DataPoint = "DataPoint";
    /**
     * DataPoints represents the name of -
     * org.junit.experimental.theories.DataPoints
     */
    public static final String DataPoints = "DataPoints";
    /**
     * ExcludeCategory represents the name of -
     * org.junit.experimental.categories.Categories.ExcludeCategory
     */
    public static final String ExcludeCategory = "ExcludeCategory";
    /**
     * Ignore represents the name of - @org.junit.Ignore
     */
    public static final String Ignore = "Ignore";
    /**
     * IncludeCategory represents the name of -
     * org.junit.experimental.categories.Categories.IncludeCategory
     */
    public static final String IncludeCategory = "IncludeCategory";
    /**
     * Parameterized represents the name of - org.junit.runners.Parameterized
     */
    public static final String Parameterized = "Parameterized";
    /**
     * Parameters represents the name of -
     * org.junit.runners.Parameterized.Parameters
     */
    public static final String Parameters = "Parameters";
    /**
     * ParametersSuppliedBy represents the name of -
     * org.junit.experimental.theories.ParametersSuppliedBy
     */
    public static final String ParametersSuppliedBy = "ParametersSuppliedBy";
    /**
     * Rule represents the name of - @org.junit.Rule
     */
    public static final String Rule = "Rule";
    /**
     * RunWith represents the name of - org.junit.runner.RunWith
     */
    public static final String RunWith = "RunWith";
    /**
     * Suite represents the name of - org.junit.runners.Suite
     */
    public static final String Suite = "Suite";
    /**
     * SuiteClasses represents the name of -
     * org.junit.runners.Suite.SuiteClasses
     */
    public static final String SuiteClasses = "SuiteClasses";
    /**
     * Test represents the name of - @org.junit.Test
     */
    public static final String Test = "Test";
    /**
     * TestedOn represents the name of -
     * org.junit.experimental.theories.suppliers.TestedOn
     */
    public static final String TestedOn = "TestedOn";
    /**
     * Theories represents the name of -
     * org.junit.experimental.theories.Theories
     */
    public static final String Theories = "Theories";
    /**
     * Theory represents the name of - org.junit.experimental.theories.Theory
     */
    public static final String Theory = "Theory";
}