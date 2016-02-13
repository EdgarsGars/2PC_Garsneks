/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m03ConcurrencyAPI;

/**
 *
 * @author Vairis
 */
import java.util.*;
import java.util.concurrent.*;

public class ForkJoinTest extends  RecursiveTask<Integer> {
	  private static final int SEQUENTIAL_THRESHOLD = 10;
	  
	  private final int[] data;
	  private final int start;
	  private final int end;
	 
	  public ForkJoinTest(int[] data, int start, int end) {
	    this.data = data;
	    this.start = start;
	    this.end = end;
	  }
	 
	  public ForkJoinTest(int[] data) {
	    this(data, 0, data.length);
	  }
	 
	  @Override
	  protected Integer compute() {
	    final int length = end - start;
	    if (length < SEQUENTIAL_THRESHOLD) {
	      return computeDirectly();
	    }
	    final int split = length / 2;
	    final ForkJoinTest left = new ForkJoinTest(data, start, start + split);
	    left.fork();
	    final ForkJoinTest right = new ForkJoinTest(data, start + split, end);
	    return Math.max(right.compute(), left.join());
	  }
	 
	  private Integer computeDirectly() {
	    System.out.println(Thread.currentThread() + " computing: " + start
	                       + " to " + end);
	    int max = Integer.MIN_VALUE;
	    for (int i = start; i < end; i++) {
	      if (data[i] > max) {
	        max = data[i];
	      }
	    }
	    return max;
	  }
	 
	  public static void main(String[] args) {
	    // create a random data set
	    final int[] data = new int[1000];
	    final Random random = new Random();
	    for (int i = 0; i < data.length; i++) {
	      data[i] = random.nextInt(100);
	    }
	 
	    // submit the task to the pool
	    final ForkJoinPool pool = new ForkJoinPool(4);
	    final ForkJoinTest finder = new ForkJoinTest(data);
	    System.out.println(pool.invoke(finder));
	  }
}

