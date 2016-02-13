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
public class JavaAtomic {
	 
    public static void main(String[] args) throws InterruptedException {
 
        ProcessingThread pt = new ProcessingThread();
        Thread t1 = new Thread(pt, "t1");
        t1.start();
        Thread t2 = new Thread(pt, "t2");
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Processing count=" + pt.getCount());
    }
 
}
 
 
class ProcessingThread implements Runnable {
    private int count;
 
 
    @Override
    public void run() {
        for (int i = 1; i < 5; i++) {
            processSomething(i);
            count++;
        }
    }
 
 
    public int getCount() {
        return this.count;
    }
 
 
    private void processSomething(int i) {
        // processing some job
        try {
            Thread.sleep(i * 300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
}
