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
public class Deadlock {

    static class Student {

        private final String name;

        public Student(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public synchronized void sayPaldies(Student itmstudent) {
            System.out.format("%s: %s"
                    + " said Paldies to me!%n",
                    this.name, itmstudent.getName());
            itmstudent.sayLudzu(this);
        }

        public synchronized void sayLudzu(Student itmstudent) {
            System.out.format("%s: %s"
                    + " said back Ludzu to me!%n",
                    this.name, itmstudent.getName());
        }
    }

    public static void main(String[] args) {
        final Student karlis
                = new Student("Karlis");
        final Student peteris
                = new Student("peteris");
        new Thread(new Runnable() {
            public void run() {
                karlis.sayPaldies(peteris);
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                peteris.sayPaldies(karlis);
            }
        }).start();
    }
}
