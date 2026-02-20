/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

import com.mycompany.sisoperativos.gui.Dashboard;

/**
 *
 * @author gabri
 */
public class Scheduling {

    private Queue readyQueue;
    private Queue blockedQueue;
    private Queue ioQueue;
    private Queue finishedQueue;
    private String politic;
    private int System_Quantum;
    private PCB currentProcess;
    private Dashboard gui;
    private int ramSize;
    private int successFinish;

    public Queue getIoQueue() {
        return ioQueue;
    }

    public void setIoQueue(Queue ioQueue) {
        this.ioQueue = ioQueue;
    }

    public void setSystem_Quantum(int System_Quantum) {
        this.System_Quantum = System_Quantum;

    }

    public int getSystem_Quantum() {
        return System_Quantum;
    }

    public Scheduling(Dashboard gui) {
        this.readyQueue = new Queue();
        this.ioQueue = new Queue();
        this.politic = null;
        this.blockedQueue = new Queue();
        this.finishedQueue = new Queue();
        this.currentProcess = null;
        this.System_Quantum = 10;
        this.gui = gui;
        this.ramSize = 256;
        this.successFinish = 0;
    }

    public Queue getFinishedQueue() {
        return this.finishedQueue;
    }

    public int getSuccessFinish() {
        return successFinish;
    }

    public void createScheduling(Queue oldQueue, String politic) {
        this.readyQueue = oldQueue;
        this.politic = politic;
    }

    public void Organize() {
        synchronized (readyQueue) {
            if (this.readyQueue.getLen() == 0 && this.currentProcess == null) {
                return;
            }
            Queue newQueue = new Queue();
            PCB aux = readyQueue.dequeue();
            while (aux != null) {
                if ("EDF".equals(this.politic)) {
                    newQueue.enqueueByDeadline(aux);
                } else if ("SRT".equals(this.politic)) {
                    newQueue.enqueueByRemainingTime(aux);
                } else if ("Priority".equals(this.politic)) {
                    newQueue.enqueueByPriority(aux);
                } else {
                    // Para RR y FIFO usamos FIFO simple
                    newQueue.enqueueFIFO(aux);
                }
                aux = readyQueue.dequeue();
            }
            // IMPORTANTE: Reemplazar la cola vieja con la nueva ya ordenada
            this.readyQueue = newQueue;
        }
    }

    public void runExecutionCycle() {
        if (currentProcess == null) {
            currentProcess = readyQueue.dequeue();
            if (currentProcess != null) {
                currentProcess.setQuantum(0);
            } else {
                return; 
            }
        }
        if (currentProcess != null) {
            currentProcess.setDurationR(currentProcess.getDurationR() - 1);
            currentProcess.setQuantum(currentProcess.getQuantum() + 1);
            currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);
            if (currentProcess.getInputOutput()!=null){
                InputOutput io = this.ioQueue.ioSercher(currentProcess.getInputOutput());
                if (currentProcess.getDurationHope()-currentProcess.getDurationR()==io.getCounter()){
                    PCB aux = this.readyQueue.dequeue();
                    io.ioChecker(currentProcess,this.blockedQueue);
                }
            }
            InputOutput tempIO = this.ioQueue.getFirstIO();
                while (tempIO!=null){
                    if (tempIO.getPcbProcess().getDurationHope()-tempIO.getPcbProcess().getDurationR()==tempIO.getTimer()){
                        PCB aux = this.blockedQueue.extractById(currentProcess.getId());
                        tempIO.getPcbProcess().setInputOutput(null);
                        if (politic.equals("FIFO") || politic.equals("RR")){
                            this.readyQueue.enqueueFIFO(tempIO.getPcbProcess());
                        }else if (politic.equals("SRT")){
                            this.readyQueue.enqueueByRemainingTime(tempIO.getPcbProcess());
                        }else if (politic.equals("Priority")){
                            this.readyQueue.enqueueByPriority(tempIO.getPcbProcess());
                        }else if (politic.equals("EDF")){
                            this.readyQueue.enqueueByDeadline(tempIO.getPcbProcess());
                        }
                    }else{
                        tempIO.getPcbProcess().setDurationR(tempIO.getPcbProcess().getDurationR()-1);
                    }
                    tempIO=tempIO.getNext();
                }
            if (currentProcess.getDurationR() <= 0) {
                gui.log("Proceso " + currentProcess.getId() + " finalizado con ÉXITO.");
                currentProcess.setState("Exit");
                if (finishedQueue != null) {
                    successFinish += 1;
                    finishedQueue.enqueueFIFO(currentProcess);
                }
                currentProcess = null;
            } 
            else if (currentProcess.getDeadlineR() <= 0) {
                gui.log("¡ALERTA! Proceso " + currentProcess.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido).");
                currentProcess.setState("Aborted");
                if (finishedQueue != null) {
                    finishedQueue.enqueueFIFO(currentProcess);
                }
                currentProcess = null; 
            } 
            else {
                PCB nextInQueue = readyQueue.peek();

                if (nextInQueue != null) {
                    boolean expulsar = false;

                    if ("RR".equals(this.politic) && currentProcess.getQuantum() >= 8) {
                        gui.log("!!! QUANTUM EXPIRED !!!");
                        expulsar = true;
                    } else if ("SRT".equals(this.politic) && nextInQueue.getDurationR() < currentProcess.getDurationR()) {
                        gui.log("!!! PREEMPTION (SRT) !!! ID " + nextInQueue.getId() + " es más corto.");
                        expulsar = true;
                    } else if ("EDF".equals(this.politic) && nextInQueue.getDeadlineR() < currentProcess.getDeadlineR()) {
                        gui.log("!!! PREEMPTION (EDF) !!! ID " + nextInQueue.getId() + " es más urgente.");
                        expulsar = true;
                    } else if ("Priority".equals(this.politic) && nextInQueue.getPriority() > currentProcess.getPriority()) {
                        gui.log("!!! PREEMPTION (PRIORITY) !!! ID " + nextInQueue.getId() + " tiene más prioridad.");
                        expulsar = true;
                    }

                    if (expulsar) {
                        currentProcess.setQuantum(0);
                        readyQueue.enqueueFIFO(currentProcess); 
                        currentProcess = null; 
                    }
                }
            }
        }
    }

    public Queue getReadyQueue() {
        return this.readyQueue;
    }

    public String getPolitic() {
        return this.politic;
    }

    public void setReadyQueue(Queue readyQueue) {
        this.readyQueue = readyQueue;
    }

    public void setPolitic(String nueva) {
        this.politic = nueva;
        this.Organize();
        // No hace falta expulsar aquí manualmente, 
        // porque el runExecutionCycle lo hará solo en el siguiente ciclo 
        // al comparar los procesos con la nueva regla.
    }

    private void updateQueueDeadlines() {
        // Necesitas un método en tu clase Queue que permita 
        // restar 1 al DeadlineR de todos los PCB sin sacarlos de la fila.
        readyQueue.decrementAllDeadlines();
    }

    // Método para simular que el proceso actual pide E/S
    public void blockCurrentProcess() {
        if (currentProcess != null) {
            currentProcess.setState("Blocked");
            blockedQueue.enqueueFIFO(currentProcess); // Se va a la "sala de espera"
            currentProcess = null; // El CPU queda libre
            System.out.println("!!! PROCESO BLOQUEADO POR E/S !!!");
        }
    }

    public void unblockProcess(int id) {
        // Buscamos el proceso en la cola de bloqueados y lo pasamos a listos
        PCB p = blockedQueue.extractById(id);
        if (p != null) {
            p.setState("Ready");
            readyQueue.enqueueFIFO(p); // Vuelve a competir por el CPU
            System.out.println("ID " + id + " ha terminado su E/S y vuelve a Ready.");

            // Si la política es Preemptiva (EDF/SRT), deberíamos organizar
            this.Organize();
        }
    }

    public Queue getBlockedQueue() {
        return blockedQueue;
    }

    public PCB getCurrentProcess() {
        return currentProcess;
    }

    public Object getQueue() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
// Método para envejecer procesos y purgar los vencidos

    public void checkAndPurgeDeadlines(Queue queue) {
        if (queue != null && queue.peek() != null) {
            Queue sobrevivientes = new Queue(); 
            PCB aux = queue.dequeue();
            while (aux != null) {
                aux.setDeadlineR(aux.getDeadlineR() - 1);
                if (aux.getDeadlineR() <= 0 && aux.getDurationR() > 0) {
                    gui.log("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido).");
                    aux.setState("Aborted"); 
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(aux);
                    }
                } else {
                    sobrevivientes.enqueueFIFO(aux);
                }
                aux = queue.dequeue();
            }
            if (queue.getName().equals("ReadyQueue")){
                this.readyQueue = sobrevivientes;
                this.readyQueue.setName("ReadyQueue");
            }else if (queue.getName().equals("BlockedQueue")){
                this.blockedQueue = sobrevivientes;
                this.blockedQueue.setName("BlockedQueue");
            }
        }
    }
}
