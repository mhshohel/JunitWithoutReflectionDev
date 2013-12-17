/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName tools.staticcallgraph
 *
 * @FileName SimpleObject.java
 * 
 * @FileCreated Nov 10, 2013
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
package tools.staticcallgraph;

import java.util.HashSet;
import java.util.Set;

public class SimpleObject {
    private Set<Class<?>> classes = new HashSet<Class<?>>();
    private String node;

    public SimpleObject(String node) {
	this.node = node;
    }

    public void addClassForMethod(Class<?> clss) {
	this.classes.add(clss);
    }

    public Set<Class<?>> getClasses() {
	return this.classes;
    }

    public void addNode(String node) {
	this.node = node;
    }

    public String getNode() {
	return this.node;
    }

    // public int getCountedSizeOfEachMethodCall() {
    // Object[] values = this.classes.values().toArray();
    // int size = 0;
    // for (Object value : values) {
    // size += (Integer) value;
    // }
    // return size;
    // }

    // public int getCountedSizeOfEachMethodCallByKey(Class<?> key) {
    // return this.classes.get(key);
    // }

    public int size() {
	return this.classes.size();
    }
}
