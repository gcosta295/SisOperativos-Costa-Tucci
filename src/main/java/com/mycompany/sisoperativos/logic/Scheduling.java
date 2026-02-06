/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author gabri
 */
public class Scheduling {

    private Queue oldQueue;
    private Queue newQueue;
    private String politic;

    public Scheduling() {
        this.newQueue = null;
        this.oldQueue = null;
        this.politic = null;
    }

    public void createScheduling(Queue oldQueue, String politic) {
        this.oldQueue = oldQueue;
        this.politic = politic;
        this.newQueue = new Queue();
    }

public Queue Organize(Queue oldQueue, Queue newQueue) {
    // 1. Get the first element to start the loop
    PCB aux = oldQueue.dequeue();

    if ("SRT".equals(this.politic)) { // Shortest Remaining Time / Deadline
        // 2. Loop until there are no more processes in the source queue
        while (aux != null) {
            // 3. Insert into the new queue using your sorted method
            newQueue.enqueueByDeadline(aux);
            
            // 4. Get the next process from the source
            aux = oldQueue.dequeue();
        }
    } else if ("FIFO".equals(this.politic)) {
        // Example of how to add another policy
        while (aux != null) {
            newQueue.enqueueFIFO(aux);
            aux = oldQueue.dequeue();
        }
    }
    
    return newQueue;
}

    public Queue getOldQueue() {
        return oldQueue;
    }

    public Queue getNewQueue() {
        return newQueue;
    }

    public String getPolitic() {
        return politic;
    }

    public void setOldQueue(Queue oldQueue) {
        this.oldQueue = oldQueue;
    }

    public void setNewQueue(Queue newQueue) {
        this.newQueue = newQueue;
    }

    public void setPolitic(String politic) {
        this.politic = politic;
    }

}
