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

        if ("EDF".equals(this.politic)) { // Shortest Remaining Time / Deadline
            // 2. Loop until there are no more processes in the source queue
            while (aux != null) {
                // 3. Insert into the new queue using your sorted method
                newQueue.enqueueByDeadline(aux);

                // 4. Get the next process from the source
                aux = oldQueue.dequeue();
            }
        } else if ("FCFS".equals(this.politic)) {
            // Example of how to add another policy
            while (aux != null) {
                newQueue.enqueueFIFO(aux);
                aux = oldQueue.dequeue();
            }
        } else if ("SRT".equals(this.politic)) {
            while (aux != null) {
                newQueue.enqueueByRemainingTime(aux);
                aux = oldQueue.dequeue();
            }
        } else if ("Priority".equals(this.politic)) {
            while (aux != null) {
                newQueue.enqueueByPriority(aux);
                aux = oldQueue.dequeue();
            }
        }

        return newQueue;
    }
    
    public void executeRoundRobin(Queue readyQueue, int systemQuantum) {
        // 1. Get the first process in line
        PCB currentP = readyQueue.dequeue();

        if (currentP != null) {
            // Set the process state to Running
            currentP.setState("Running");

            // We calculate how much time to run: the minimum between the system quantum and what the process needs
            int timeToExecute = Math.min(systemQuantum, currentP.getDurationR());

            System.out.println("Executing Process ID: " + currentP.getId() + " for " + timeToExecute + " cycles.");

            // 2. Subtract the execution time from the remaining duration
            currentP.setDurationR(currentP.getDurationR() - timeToExecute);

            // 3. Check if the process finished or needs to go back to the queue
            if (currentP.getDurationR() > 0) {
                // Quantum expired but process is not finished
                currentP.setState("Ready");
                System.out.println("Quantum expired. Moving Process " + currentP.getId() + " to the back of the queue.");
                readyQueue.enqueueFIFO(currentP); // Goes to the back of the line
            } else {
                // Process finished its work
                currentP.setState("Terminated");
                System.out.println("Process " + currentP.getId() + " has finished execution.");
            }
        }
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
