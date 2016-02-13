/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pkg2pc.a02Concurrency;

/**
 *
 * @author Janis
 */
public class RaceCondition {    
    public static void main(String[] args) throws InterruptedException {   
        MyCounter counter = new MyCounter();

        Thread thread1 = new Thread(new CounterIncrementer(counter));
        thread1.setName("Thread1");
        thread1.start();

        Thread thread2 = new Thread(new CounterIncrementer(counter));
        thread2.setName("Thread2");
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(counter.value());
    }    
}


class CounterIncrementer implements Runnable {
    private MyCounter counter;

    public CounterIncrementer(MyCounter counter) {
        this.counter = counter;
    }

    public void run() {
        for ( int i=0; i<10000; i++ ) {
            counter.incrementBy(i);
        }
    }
}


class MyCounter {
    private volatile long c = 0;

    public synchronized void incrementBy(int i) {
        c+=i;
    }

    public long value() {
        return c;
    }    
}
