package lnu.util;

public class Timer {
    private long timer_start;

	public Timer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Start timer
	 */
	public void tic() {timer_start = System.currentTimeMillis();}
	/**
	 * Stop timer and returns the ellapsed time (since tic()) 
	 * in milliseconds.
	 * 
	 * @return ellapsed time
	 */
	public long toc() {return System.currentTimeMillis()- timer_start;}
	
	/**
	 * Returns a string (e.g. 27 Mbytes) indicating the ammount of 
	 * memory used by the JVM. It enforces a garbage collection 
	 * before it measures the memory.
	 * @return a string containing memory size after garbage collection. 
	 */
	public String gcMem() {
	    Runtime runtime = Runtime.getRuntime();
	    long mem_size = runtime.totalMemory()-runtime.freeMemory();
	    long prev;
	    do {
	    	prev = mem_size;
	    	runtime.gc();
	    	mem_size = runtime.totalMemory()-runtime.freeMemory(); 
	    } while (prev !=mem_size);
	    return mem_size/1000000 +" Mbytes";    
	}
	
	/**
	 * Returns a string (e.g. 27 Mbytes) indicating the amount of 
	 * memory currently used by the JVM. It <em> does not</em> enforces a garbage collection 
	 * a garbage collection before it measures the memory.
	 * @return a string containing the current memory size. 
	 */
	public int currentMem() {
	    Runtime runtime = Runtime.getRuntime();
	    long mem_size = runtime.totalMemory()-runtime.freeMemory();
	    return (int)mem_size/1000000;    
	}
}
