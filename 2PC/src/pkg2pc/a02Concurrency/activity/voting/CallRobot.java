/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pkg2pc.a02Concurrency.activity.voting;

import java.util.Random;

/**
 *
 * @author Janis
 */
public class CallRobot {
    
    public static int[] makeCall(int nrOfCand){
        
        int cand[] = new int[nrOfCand];
        for (int i = 0; i < cand.length; i++) {
            cand[i] = i;
        }
        
        shuffleArray(cand);
        
        return cand;
    }
    
    static void shuffleArray(int[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
    
}
