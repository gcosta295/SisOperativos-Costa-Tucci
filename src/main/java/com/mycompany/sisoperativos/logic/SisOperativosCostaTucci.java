/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sisoperativos.logic;

/**
 *
 * @author gabri & astv06
 */
public class SisOperativosCostaTucci {

public static void main(String[] args) {
        // 1. Create the Queues
        Queue oldQueue = new Queue();
        Queue newQueue = new Queue();
        
        System.out.println("--- Step 1: Creating Processes ---");
        
        // 2. Create sample PCBs with different deadlines
        // (Remember to add a constructor or setters for these values)
        PCB p1 = new PCB();
        p1.setDurationR(50);
        p1.setUser("Process_A");

        PCB p2 = new PCB();
        p2.setDurationR(10); // This should be first (Earliest)
        p2.setUser("Process_B");

        PCB p3 = new PCB();
        p3.setDurationR(30);
        p3.setUser("Process_C");

        // 3. Add them to the oldQueue (Unsorted)
        oldQueue.enqueueFIFO(p1); // Use a standard enqueue here
        oldQueue.enqueueFIFO(p2);
        oldQueue.enqueueFIFO(p3);
        
        System.out.println("Processes added to Old Queue.");

        // 4. Test the 'organizar' logic (EDF)
        System.out.println("\n--- Step 2: Organizing by Deadline (EDF) ---");
        
        // Simulate the organizing loop
        PCB aux = oldQueue.dequeue();
        while (aux != null) {
            System.out.println("Moving: " + aux.getUser() + " with Deadline: " + aux.getDurationR());
            newQueue.enqueueByRemainingTime(aux); // Your sorted method
            aux = oldQueue.dequeue();
        }

        // 5. Verify the results
        System.out.println("\n--- Step 3: Verifying Results (New Queue) ---");
        PCB current = newQueue.dequeue();
        while (current != null) {
            System.out.println("Processing: " + current.getUser() + " | Deadline: " + current.getDurationR());
            current = newQueue.dequeue();
        }
    }
    
}
