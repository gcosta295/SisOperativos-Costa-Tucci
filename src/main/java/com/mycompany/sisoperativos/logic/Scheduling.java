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
    private int System_Quantum;
    private PCB currentProcess;

    public void setSystem_Quantum(int System_Quantum) {
        this.System_Quantum = System_Quantum;
    }

    public int getSystem_Quantum() {
        return System_Quantum;
    }

    public Scheduling() {
        this.newQueue = null;
        this.oldQueue = null;
        this.politic = null;
        this.currentProcess = null;
        this.System_Quantum = 10;
    }

    public void createScheduling(Queue oldQueue, String politic) {
        this.oldQueue = oldQueue;
        this.politic = politic;
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

    public void runExecutionCycleRR() { //Execution Cycle for when Round Robin is activated
        // 1. Cargar proceso si el CPU está vacío
        // 1. Intentar obtener un proceso si el CPU está libre
        if (currentProcess == null) {
            PCB nextP = oldQueue.dequeue();

            // FILTRO: Mientras lo que saquemos de la cola ya esté terminado, lo ignoramos
            while (nextP != null && nextP.getDurationR() <= 0) {
                System.out.println("[DEBUG] Saltando proceso terminado: " + nextP.getId());
                nextP = oldQueue.dequeue();
            }

            currentProcess = nextP;

            if (currentProcess != null) {
                currentProcess.setQuantum(0);
            }
        }// 1. Si no hay proceso, sacamos uno nuevo
        if (currentProcess == null) {
            currentProcess = oldQueue.dequeue();

            // FILTRO ANTI-ZOMBIE: 
            // Si el proceso que sacamos ya tiene duración 0, saltamos al siguiente
            while (currentProcess != null && currentProcess.getDurationR() <= 0) {
                System.out.println("[DEBUG] Ignorando proceso terminado: " + currentProcess.getId());
                currentProcess = oldQueue.dequeue();
            }

            if (currentProcess != null) {
                currentProcess.setQuantum(0);
            }
        }

        if (currentProcess != null) {
            // ... (el resto de tu lógica de ejecución)
        } else {
            System.out.println("CPU Idle...");
        }

        if (currentProcess != null) {
            // 2. Ejecutar un paso
            currentProcess.setDurationR(currentProcess.getDurationR() - 1);
            int currentSpentTime = currentProcess.getQuantum() + 1;
            currentProcess.setQuantum(currentSpentTime);
            currentProcess.setState("Running");

            System.out.println("Executing ID: " + currentProcess.getId()
                    + " | Cycles in this turn: " + currentSpentTime
                    + " | Remaining Duration: " + currentProcess.getDurationR());

            // 3. ¿Terminó? (Prioridad máxima)
            if (currentProcess.getDurationR() <= 0) {
                currentProcess.setState("Terminated");
                System.out.println("Process " + currentProcess.getId() + " FINISHED.");
                currentProcess = null;
            } // 4. ¿Se acabó su tiempo (Quantum)?
            else if (currentSpentTime >= 4) { // He puesto '4' directo para probar
                System.out.println("!!! QUANTUM REACHED !!! Sending ID " + currentProcess.getId() + " to back.");
                currentProcess.setState("Ready");
                currentProcess.setQuantum(0);

                oldQueue.enqueueFIFO(currentProcess); // Usamos tu método FIFO
                currentProcess = null; // Vaciamos el CPU
            }
        } else {
            System.out.println("CPU Idle...");
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
