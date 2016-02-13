/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m03ConcurrencyVC;

/**
 *
 * @author Vairis
 */
public class SleepMessagesInterrupt {
    public static void main(String args[]) {
        String importantInfo[] = {
            "Mares eat oats",
            "Does eat oats",
            "Little lambs eat ivy",
            "A kid will eat ivy too"
        };
 
        for (int i = 0;i < importantInfo.length;i++) {
            //Pause for 4 seconds
        try {
        Thread.sleep(4000);
        } catch (InterruptedException e) {
        // We've been interrupted: no more messages.
        return;
        }
            //Print a message
            System.out.println(importantInfo[i]);
        }
    }
}