/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.code.gen
 *
 * @FileName ClassRecords.java
 * 
 * @FileCreated Jan 02, 2013
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <li><strong>ClassRecords</strong></li>
 * 
 * <pre>
 * public class ClassRecords <blockquote>implements</blockquote> AnnotaionList
 * </pre>
 * <p>
 * This class contains information of classes by exploring class annotations and
 * other properties. It keeps information of only entry class. A <b>entry
 * class</b> is a class where test begins. If a class has SuiteClasses then each
 * class will create new instance of ClassRecords (This class). Because, classes
 * those are in SuiteClasses runs as a separate test class.
 * </p>
 * <br/>
 * 
 * @author Shohel Shamim
 */
public class ClassRecords implements AnnotaionList {
    /* Keep annotations of a class. Key: Annotation, Value: annotation name */
    private HashMap<Annotation, String> annotations;
    /* Keep each entry class. */
    private Class<?> claz;
    /*
     * If a class has constructor then it keep constructor's parameters type as
     * String (parameter name).
     */
    private Object[] constructorsParametsType;
    /*
     * If a class has constructor then it keep constructor's parameters type
     * CLASS.
     */
    private Class<?>[] constructorsParametsTypeClass;
    /*
     * If class has @ExcludeCategory annotation then it will keep those
     * information.
     */
    private List<Class<?>> excludeCategory;
    /* True is class has annotation @RunWith(Categories.class) */
    private boolean hasRunWithCategories;
    /*
     * True if annotation @RunWith(Categories.class) found in any level of
     * ClassRecords. See hasRunWithCategoriesAtItsPath() for details
     * information.
     */
    private boolean hasRunWithCategoriesAtItsPath;
    /* True is class has annotation @RunWith(Parameterized.class) */
    private boolean hasRunWithParameterized;
    /* True is class has annotation @RunWith(Suite.class) */
    private boolean hasRunWithSuite;
    /* True is class has annotation @RunWith(Theories.class) */
    private boolean hasRunWithTheories;
    /* True is class has annotation @RunWith(Suite.class) */
    private boolean hasSuiteClasses;
    /*
     * If class has @IncludeCategory annotation then it will keep those
     * information.
     */
    private List<Class<?>> includeCategory;
    /*
     * If a class has @Ignore then it will be true. Initially it's false.
     */
    private boolean isIgnored = false;
    private Collection<?> parametersData;
    /*
     * If class has SuiteClasses then each ClassRecords contains one or multiple
     * ClassRecords. This field contains the parent of ClassRecords object.
     */
    private ClassRecords parentClassRecord;
    /*
     * If class has SuiteClasses then this field contains list of classes.
     */
    private List<ClassRecords> suiteClasses;

    /**
     * <li><strong><i>ClassRecords</i></strong></li>
     * 
     * <pre>
     * public ClassRecords(Class<?> currentClass)
     * </pre>
     * 
     * <p>
     * Take only entry class as parameter that is the starting point of a test
     * class. Do not pass any other classes. If there are SuiteClasses it will
     * find them and create new object of ClassRecords by itself. Example: If
     * ClassA has to test, then create new object of ClassRecords as<br/>
     * <code>ClassRecords cr = new ClassRecords(ClassA.class);</code>
     * </p>
     * 
     * @param currentClass
     *            - a class name with .class extension or Class Type
     * 
     * 
     * @author Shohel Shamim
     */
    public ClassRecords(Class<?> currentClass) {
	this(currentClass, null);
    }

    /**
     * <li><strong><i>ClassRecords</i></strong></li>
     * 
     * <pre>
     * private ClassRecords(Class<?> current, ClassRecords parentClassRecord)
     * </pre>
     * 
     * <p>
     * This constructor is used as an extension of <code>public
     * ClassRecords(Class<?> currentClass)</code>. If entry class has Suite
     * Classes (in simple word, multiple entry classes) then it will be used to
     * keep the parent ClassRecords.
     * </p>
     * 
     * @param currentClass
     *            - a class name with .class extension or Class Type
     * @param parentClassRecord
     *            - a parent ClassRecords
     * 
     * 
     * @author Shohel Shamim
     */
    private ClassRecords(Class<?> current, ClassRecords parentClassRecord) {
	this.claz = current;
	this.parentClassRecord = parentClassRecord;
	this.parametersData = null;
	this.suiteClasses = new ArrayList<ClassRecords>();
	this.annotations = new HashMap<Annotation, String>();
	// If there are Categories then
	// copyIncludeOrExcludeCategoriesFromParent() will store them in
	// includeCategory and excludeCategory list. Do not change the position
	// to run the system successfully.
	copyIncludeOrExcludeCategoriesFromParent();
	readAnnotations(this.claz);
    }

    /**
     * <li><strong><i>addExcludeCategoryClass</i></strong></li>
     * 
     * <pre>
     * private void addExcludeCategoryClass(Class<?> clz)
     * </pre>
     * 
     * <p>
     * Add class those are inside in ExcludeCategory as a marker.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * 
     * @author Shohel Shamim
     */
    private void addExcludeCategoryClass(Class<?> clz) {
	if (!(this.excludeCategory.contains(clz))) {
	    this.excludeCategory.add(clz);
	}
    }

    /**
     * <li><strong><i>addIncludeCategoryClass</i></strong></li>
     * 
     * <pre>
     * private void addIncludeCategoryClass(Class<?> clz)
     * </pre>
     * 
     * <p>
     * Add class those are inside in IncludeCategory as a marker.
     * </p>
     * 
     * @param clz
     *            - a class name with .class extension or Class Type
     * 
     * @author Shohel Shamim
     */
    private void addIncludeCategoryClass(Class<?> clz) {
	if (!(this.includeCategory.contains(clz))) {
	    this.includeCategory.add(clz);
	}
    }

    /**
     * <li><strong><i>copyIncludeOrExcludeCategoriesFromParent</i></strong></li>
     * 
     * <pre>
     * private void copyIncludeOrExcludeCategoriesFromParent()
     * </pre>
     * 
     * <p>
     * Keep new copy of Include and Exclude category data.
     * </p>
     * 
     * @author Shohel Shamim
     */
    private void copyIncludeOrExcludeCategoriesFromParent() {
	if (this.parentClassRecord != null) {
	    // Creating a shallow copy
	    this.includeCategory = new ArrayList<Class<?>>(
		    this.parentClassRecord.getIncludeCategoryClass());
	    this.excludeCategory = new ArrayList<Class<?>>(
		    this.parentClassRecord.getExcludeCategoryClass());
	} else {
	    this.includeCategory = new ArrayList<Class<?>>();
	    this.excludeCategory = new ArrayList<Class<?>>();
	}
    }

    /**
     * <li><strong><i>getAnnotations</i></strong></li>
     * 
     * <pre>
     * public HashMap<Annotation, String> getAnnotations()
     * </pre>
     * 
     * <p>
     * Return annotations HashMap of current class. HashMap - Key: Annotation
     * and Value: annotation Name.
     * </p>
     * 
     * @return HashMap - return Annotation List
     * 
     * @author Shohel Shamim
     */
    public HashMap<Annotation, String> getAnnotations() {
	return this.annotations;
    }

    /**
     * <li><strong><i>getConstructorsParametsType</i></strong></li>
     * 
     * <pre>
     * public Object[] getConstructorsParametsType()
     * </pre>
     * 
     * <p>
     * Return Object[] array that contains data type of class constructor in
     * actual order only SIMPLE NAME. especially used for
     * RunWith(Parameterized.class)
     * </p>
     * 
     * @return Object[] - return constructors parameters type name
     * 
     * @author Shohel Shamim
     */
    public Object[] getConstructorsParametsType() {
	return this.constructorsParametsType;
    }

    /**
     * <li><strong><i>getConstructorsParametsTypeClass</i></strong></li>
     * 
     * <pre>
     * public Class<?>[] getConstructorsParametsTypeClass()
     * </pre>
     * 
     * <p>
     * Return Object[] array that contains data type of class constructor in
     * actual order, Type is Class. especially used for
     * RunWith(Parameterized.class)
     * </p>
     * 
     * @return Class<?>[] - return constructors parameters type class
     * 
     * @author Shohel Shamim
     */
    public Class<?>[] getConstructorsParametsTypeClass() {
	return this.constructorsParametsTypeClass;
    }

    /**
     * <li><strong><i>getEntryClass</i></strong></li>
     * 
     * <pre>
     * public Class<?> getEntryClass()
     * </pre>
     * 
     * <p>
     * Return class that is the entry point of a test class that was used to
     * create ClassRecords
     * </p>
     * 
     * @return Class<?>[] - return class type
     * 
     * @author Shohel Shamim
     */
    public Class<?> getEntryClass() {
	return this.claz;
    }

    /**
     * <li><strong><i>getExcludeCategoryClass</i></strong></li>
     * 
     * <pre>
     * public List<Class<?>> getExcludeCategoryClass()
     * </pre>
     * 
     * <p>
     * Return List of classes of ExcludeCategory.
     * </p>
     * 
     * @return List<Class<?>> - return List of class type
     * 
     * @author Shohel Shamim
     */
    public List<Class<?>> getExcludeCategoryClass() {
	return this.excludeCategory;
    }

    /**
     * <li><strong><i>getIncludeCategoryClass</i></strong></li>
     * 
     * <pre>
     * public List<Class<?>> getIncludeCategoryClass()
     * </pre>
     * 
     * <p>
     * Return List of classes of IncludeCategory.
     * </p>
     * 
     * @return List<Class<?>> - return List of class type
     * 
     * @author Shohel Shamim
     */
    public List<Class<?>> getIncludeCategoryClass() {
	return this.includeCategory;
    }

    /**
     * <li><strong><i>getParameterizedMethodsData</i></strong></li>
     * 
     * <pre>
     * public Collection<?> getParameterizedMethodsData()
     * </pre>
     * 
     * <p>
     * Return parameters data.
     * </p>
     * 
     * @return Collection<?> - return Collection of Parameterized Methods values
     * 
     * @author Shohel Shamim
     */
    public Collection<?> getParameterizedMethodsData() {
	return this.parametersData;
    }

    /**
     * <li><strong><i>getParentofEntryClassRecord</i></strong></li>
     * 
     * <pre>
     * public ClassRecords getParentofEntryClassRecord()
     * </pre>
     * 
     * <p>
     * Return Parent class record if any, if current class is invoked from a
     * Suite class then that class is the parent of this class record. it's not
     * like super class and base class recorded. It's like keeping reference.
     * </p>
     * 
     * @return ClassRecords - return parent ClassRecords
     * 
     * @author Shohel Shamim
     */
    public ClassRecords getParentofEntryClassRecord() {
	return this.parentClassRecord;
    }

    /**
     * <li><strong><i>getSuiteClasses</i></strong></li>
     * 
     * <pre>
     * public List<ClassRecords> getSuiteClasses()
     * </pre>
     * 
     * <p>
     * Return List of all suited class records those are annotated by
     * SuiteClasses{{}}.
     * </p>
     * 
     * @return List<ClassRecords> - return list of ClassRecords
     * 
     * @author Shohel Shamim
     */
    public List<ClassRecords> getSuiteClasses() {
	return this.suiteClasses;
    }

    /**
     * <li><strong><i>hasRunWithCategories</i></strong></li>
     * 
     * <pre>
     * public boolean hasRunWithCategories()
     * </pre>
     * 
     * <p>
     * Return true if class has RunWith(Categories.class) annotation.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasRunWithCategories() {
	// both required to run them
	return (this.hasRunWithCategories && this.hasSuiteClasses);
    }

    /**
     * <li><strong><i>hasRunWithCategoriesAtItsPath</i></strong></li>
     * 
     * <pre>
     * public boolean hasRunWithCategoriesAtItsPath()
     * </pre>
     * 
     * <p>
     * Return true or false by looking up on its path. If this class
     * hasRunWithCategories is false then it will look through its previous
     * classes. lookupCategoriesAtItsPath = true, if found at any level of
     * parent class record of current class so, that it can determine weather
     * class should check for Category or not. Example: ClassA -> ClassB,
     * ClassC; ClassB -> ClassD, ClassE; now if ClassA is not true for Category,
     * but ClassB is true. Then, Category is True will work for all child of
     * ClassB -> ClassD, ClassE. But ClassC will be false for Category because
     * its root(s) (any level) are(is) not true.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasRunWithCategoriesAtItsPath() {
	return this.hasRunWithCategoriesAtItsPath;
    }

    /**
     * <li><strong><i>hasRunWithParameterized</i></strong></li>
     * 
     * <pre>
     * public boolean hasRunWithParameterized()
     * </pre>
     * 
     * <p>
     * Return true if class has RunWith(Parameterized.class) annotation.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasRunWithParameterized() {
	return this.hasRunWithParameterized;
    }

    /**
     * <li><strong><i>hasRunWithSuite</i></strong></li>
     * 
     * <pre>
     * public boolean hasRunWithSuite()
     * </pre>
     * 
     * <p>
     * Return true if class has RunWith(Suite.class) annotation.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasRunWithSuite() {
	// both req. to run them
	return (this.hasRunWithSuite && this.hasSuiteClasses);
    }

    /**
     * <li><strong><i>hasRunWithSuiteOrCategories</i></strong></li>
     * 
     * <pre>
     * public boolean hasRunWithSuiteOrCategories()
     * </pre>
     * 
     * <p>
     * Return true if class has RunWith(Suite.class) or
     * RunWith(Categories.class)annotation.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasRunWithSuiteOrCategories() {
	// both req. to run them
	return (hasRunWithSuite() || hasRunWithCategories());
    }

    /**
     * <li><strong><i>hasRunWithTheories</i></strong></li>
     * 
     * <pre>
     * public boolean hasRunWithTheories()
     * </pre>
     * 
     * <p>
     * Return true if class has RunWith(Theories.class) annotation.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasRunWithTheories() {
	return this.hasRunWithTheories;
    }

    /**
     * <li><strong><i>hasSuiteClasses</i></strong></li>
     * 
     * <pre>
     * public boolean hasSuiteClasses()
     * </pre>
     * 
     * <p>
     * Return true if class has SuiteClasses.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean hasSuiteClasses() {
	return (this.hasRunWithSuite && this.hasSuiteClasses)
		|| ((this.hasRunWithCategories && this.hasSuiteClasses));
    }

    /**
     * <li><strong><i>isIgnored</i></strong></li>
     * 
     * <pre>
     * public boolean isIgnored()
     * </pre>
     * 
     * <p>
     * Return true if class has Ignore annotation.
     * </p>
     * 
     * @return boolean - return true or false
     * 
     * @author Shohel Shamim
     */
    public boolean isIgnored() {
	return this.isIgnored;
    }

    /**
     * <li><strong><i>readAnnotations</i></strong></li>
     * 
     * <pre>
     * private void readAnnotations(Class<?> cls)
     * </pre>
     * 
     * <p>
     * Retrieve annotations of classes and keep them in the right property.
     * Depending on Annotation different property was set. This method is very
     * important to generate code.
     * </p>
     * 
     * @param cls
     *            - a class name with .class extension or Class Type
     * 
     * @author Shohel Shamim
     */
    private void readAnnotations(Class<?> cls) {
	Class<?>[] suits = null;
	Class<?> inCategory = null;
	Class<?> exCategory = null;
	// reset value of essential properties of class
	resetValues();
	// Setting properties and collecting list of @Suite classes
	for (Annotation annotation : cls.getAnnotations()) {
	    String name = annotation.annotationType().getSimpleName();
	    this.annotations.put(annotation, name);
	    if (name.equalsIgnoreCase(Ignore)) {
		this.isIgnored = true;
	    } else if (name.equalsIgnoreCase(RunWith)) {
		RunWith runWith = (RunWith) annotation;
		if (runWith.value().getSimpleName().equalsIgnoreCase(Suite)) {
		    this.hasRunWithSuite = true;
		} else if (runWith.value().getSimpleName()
			.equalsIgnoreCase(Categories)) {
		    this.hasRunWithCategories = true;
		} else if (runWith.value().getSimpleName()
			.equalsIgnoreCase(Theories)) {
		    this.hasRunWithTheories = true;
		} else if (runWith.value().getSimpleName()
			.equalsIgnoreCase(Parameterized)) {
		    this.hasRunWithParameterized = true;
		}
	    } else if (name.equalsIgnoreCase(IncludeCategory)) {
		inCategory = ((IncludeCategory) annotation).value();
	    } else if (name.equalsIgnoreCase(ExcludeCategory)) {
		exCategory = ((ExcludeCategory) annotation).value();
	    } else if (name.equalsIgnoreCase(SuiteClasses)) {
		// Adding classes those are in @Suite.SuiteClasses{};
		// class will be store as a object of ClassRecords later
		suits = ((SuiteClasses) annotation).value();
		this.hasSuiteClasses = true;
	    }
	}
	if (hasRunWithCategories()) {
	    this.hasRunWithCategoriesAtItsPath = true;
	} else {
	    if (this.parentClassRecord != null
		    && this.parentClassRecord.hasRunWithCategoriesAtItsPath()) {
		this.hasRunWithCategoriesAtItsPath = true;
	    }
	}
	// If parent class is ignored then other essential properties won't
	// work; so must need to reset them
	if (this.isIgnored) {
	    resetValues();
	} else if (this.hasRunWithParameterized) {
	    Constructor<?>[] constructors = cls.getDeclaredConstructors();
	    // Parameterized class have only one constructor, not more or less,
	    // so, it can call first element from the array
	    Constructor<?> constructor = constructors[0];
	    this.constructorsParametsType = new Object[constructor
		    .getParameterTypes().length];
	    this.constructorsParametsTypeClass = new Class<?>[constructor
		    .getParameterTypes().length];
	    for (int i = 0; i < constructor.getParameterTypes().length; i++) {
		this.constructorsParametsType[i] = constructor
			.getParameterTypes()[i].getSimpleName();
		this.constructorsParametsTypeClass[i] = constructor
			.getParameterTypes()[i];
	    }
	} else {
	    // if (hasRunWithSuite && hasSuiteClasses) {
	    if (hasSuiteClasses()) {
		if (this.hasRunWithCategories) {
		    if (inCategory != null) {
			addIncludeCategoryClass(inCategory);
		    }
		    if (exCategory != null) {
			addExcludeCategoryClass(exCategory);
		    }
		}
		for (Class<?> clz : suits) {
		    // Lookup all @Suite classes and add new object of
		    // ClassRecords, so that each class can contains their own
		    // properties
		    this.suiteClasses.add(new ClassRecords(clz, this));
		}
	    }
	}
    }

    /**
     * <li><strong><i>resetValues</i></strong></li>
     * 
     * <pre>
     * private void resetValues()
     * </pre>
     * 
     * <p>
     * Reset essential properties if class has Ignore annotation.
     * </p>
     * 
     * 
     * @author Shohel Shamim
     */
    private void resetValues() {
	this.hasSuiteClasses = false;
	this.hasRunWithSuite = false;
	this.hasRunWithCategories = false;
	this.hasRunWithTheories = false;
	this.hasRunWithParameterized = false;
	this.hasRunWithCategoriesAtItsPath = false;
    }

    /**
     * <li><strong><i>setParameterizedMethodsData</i></strong></li>
     * 
     * <pre>
     * public void setParameterizedMethodsData(Collection<?> data)
     * </pre>
     * 
     * <p>
     * Keep values from Parameterized methods. A method that is annotated by
     * Parameters return Collection data type.
     * </p>
     * 
     * @param data
     *            - a Collection type data
     * 
     * @author Shohel Shamim
     */
    public void setParameterizedMethodsData(Collection<?> data) {
	if (this.parametersData == null || this.parametersData.isEmpty()) {
	    this.parametersData = data;
	}
    }
}