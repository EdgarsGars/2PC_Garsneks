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
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class LockTest implements Runnable {

    private Resource resource;

    public LockTest(Resource r) {
        this.resource = r;

    }

    @Override
    public void run() {
        boolean islock = false;
        try {
            islock = resource.lock.tryLock(1, TimeUnit.SECONDS);
            if (islock) {
                resource.doSomething();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //release lock
            if (islock) {
                resource.lock.unlock();
            }
        }
        resource.doLogging();
    }

    public static void main(String[] args) {
        Resource r = new Resource();
        LockTest t1 = new LockTest(r);
        LockTest t2 = new LockTest(r);

        (new Thread(t1)).start();
        (new Thread(t2)).start();
    }
}

class Resource {

    public Lock lock = new ReentrantLock();

    public void doSomething() {
        //do some operation, DB read, write etc
        try {
            System.out.println(Thread.currentThread().getName() + ":Locking and operating on Resources!");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doLogging() {
        //logging, no need for thread safety
        System.out.println(Thread.currentThread().getName() + ": Doing loging!");
    }
}
