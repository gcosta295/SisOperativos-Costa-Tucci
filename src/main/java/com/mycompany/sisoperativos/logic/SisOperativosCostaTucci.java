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
        // 1. Setup the Scheduler and Queues
        Scheduling scheduler = new Scheduling();
        Queue readyQueue = new Queue();

        // Let's assume the scheduler has a method to set its queue
        scheduler.createScheduling(readyQueue, "RR");

        // 2. Create sample processes (PCBs)
        // Process A: Needs 6 cycles
        PCB p1 = new PCB();
        p1.setId(101);
        p1.setDurationR(5);
        readyQueue.enqueueFIFO(p1);

// PROCESO 2 (DEBE TENER SU PROPIO 'new')
        PCB p2 = new PCB();
        p2.setId(202);
        p2.setDurationR(2);
        readyQueue.enqueueFIFO(p2);

        // 3. Add processes to the queue
        readyQueue.enqueueFIFO(p1);
        readyQueue.enqueueFIFO(p2);

        System.out.println("--- Starting Round Robin Simulation ---");
        System.out.println("Quantum is set to 4 cycles.");

        // 4. Initialize the Clock
        // We pass the scheduler so the clock can trigger 'runExecutionCycle()'
        // 1000ms = 1 second per cycle for easy reading in console
        Clock simClock = new Clock(1000, scheduler);

        // 5. Start the Clock thread
        Thread clockThread = new Thread(simClock);
        clockThread.start();

        // Optional: Let it run for 15 seconds then stop
        try {
            Thread.sleep(15000);
            simClock.detener();
            System.out.println("--- Simulation Ended ---");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
