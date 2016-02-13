/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2pc.a02Concurrency;

import static java.lang.Thread.sleep;

/**
 *
 * @author Janis
 */
public class SlippedCondition {

    private boolean isLocked = true;

    public void lock() {
        synchronized (this) {
            while (isLocked) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    //do nothing, keep waiting
                }
            }
        }

        try {
            sleep(1000); // do something before seting back look
        } catch (InterruptedException e) {
        }

        synchronized (this) {
            isLocked = true;
        }
    }

    public synchronized void unlock() {
        isLocked = false;
        this.notify();
    }

}
