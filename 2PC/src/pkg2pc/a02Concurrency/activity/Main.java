/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2pc.a02Concurrency.activity;

/**
 *
 * @author Janis
 */
public class Main {

    static Mailbox mailbox = new Mailbox();

    public static void main(String args[]) {
        System.out.println("   @@@@@");
        System.out.println("  @(   )@");
        System.out.println(" @@_) (_@@");
        System.out.println("@( . Y . )@");
        System.out.println("   )   (");
                                

        System.out.println("Delivering mail");
        Thread firstDelivery = new Delivery(mailbox, 1);
        firstDelivery.start();

        synchronized (mailbox) {
            // main Thread gets lock on m
            try {
                mailbox.wait(); // releases lock here
                // must regain the lock to reenter here
            } catch (InterruptedException e) {
            }
        }
        System.out.println("Mail Arrived: " + mailbox.getMailfromMailbox());
    }
}

class Delivery extends Thread {

    Mailbox mailbox;
    int mail;

    Delivery(Mailbox mailbox, int mail) {
        this.mailbox = mailbox;
        this.mail = mail;
    }

    public void run() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
        }
        synchronized (mailbox) {
            // this thread gets lock on m
            mailbox.putMailInMailbox(mail);
            mailbox.notify(); // puts first waiting thread in runnable state (but does not release the look)
        }
    }
}

class Mailbox {

    int mail;

    Mailbox() {
        mail = -1;
    }

    void putMailInMailbox(int m) {
        mail = m;
    }

    int getMailfromMailbox() {
        return mail;
    }
}
