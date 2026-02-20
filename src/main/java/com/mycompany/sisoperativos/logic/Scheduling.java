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
        this.successFinish=0;
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
                newQueue.enqueueFIFO(aux);
            }
            aux = readyQueue.dequeue();
        }
        this.readyQueue = newQueue;
    }

    public void runExecutionCycle() {
        if (currentProcess == null) {
            currentProcess = readyQueue.dequeue();
            if (currentProcess != null) {
                currentProcess.setQuantum(0);//evaluar utilidad de esta linea
            } else {
                return; // Si no hay nada en la cola, no hacemos nada este ciclo.
            }
        }
        // 2. Si hay un proceso en el CPU, lo ejecutamos un ciclo
        if (currentProcess != null) {
            // Ejecución estándar
            currentProcess.setDurationR(currentProcess.getDurationR() - 1);
            currentProcess.setQuantum(currentProcess.getQuantum() + 1);
            currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);
            if (currentProcess.getInputOutput()!=null){
                InputOutput ioDevice = this.ioQueue.serchByName(currentProcess.getInputOutput());
                if (ioDevice.getCounter()>=currentProcess.getQuantum()){
                    ioDevice.setPcbProcess(currentProcess);
                    this.blockCurrentProcess();
                }
            }
            // 3. ¿Terminó con éxito?
            if (currentProcess != null && currentProcess.getDurationR() <= 0 ) {
                gui.log("Proceso " + currentProcess.getId() + " finalizado con ÉXITO.");
                currentProcess.setState("Exit");
                if (finishedQueue != null) {
                    successFinish+=1;
                    finishedQueue.enqueueFIFO(currentProcess);
                }
                currentProcess = null; // Liberamos el CPU para el próximo ciclo
            } // 4. ¿Se le acabó el deadline estando en el CPU?
            else if (currentProcess != null && currentProcess.getDeadlineR() <= 0) {
                gui.log("¡ALERTA! Proceso " + currentProcess.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido).");
                currentProcess.setState("Aborted");
                if (finishedQueue != null) {
                    finishedQueue.enqueueFIFO(currentProcess);
                }
                currentProcess = null; // Lo sacamos del CPU a la fuerza
            } // 5. LÓGICA DE PREEMPCIÓN (Solo revisamos si el proceso sigue vivo en el CPU)
            else if (currentProcess != null){
                PCB nextInQueue = readyQueue.peek(); // Asumo que peek() y getFirstP() hacen lo mismo.

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
                        readyQueue.enqueueFIFO(currentProcess); // El actual vuelve a la cola
                        currentProcess = null; // El CPU queda libre para el "retador" en el ciclo siguiente
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
        // 1. Revisamos si hay procesos en la cola de listos
        if (queue != null && queue.peek() != null) {

            Queue sobrevivientes = new Queue(); // Cola temporal

            // Sacamos los procesos uno por uno (asumo que tienes un método dequeue() o sacar())
            // Si tu método para sacar se llama diferente, cámbialo aquí:
            PCB aux = queue.dequeue();

            while (aux != null) {
                // Restamos 1 al tiempo límite por el ciclo que acaba de pasar
                aux.setDeadlineR(aux.getDeadlineR() - 1);

                // ¿Se le acabó el tiempo antes de terminar su ráfaga?
                if (aux.getDeadlineR() <= 0 && aux.getDurationR() > 0) {
                    gui.log("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido).");
                    aux.setState("Aborted"); // Lo marcamos como abortado

                    // Lo mandamos al cementerio de finalizados
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(aux);
                    }
                } else {
                    // Si sobrevive, lo metemos a la cola temporal
                    sobrevivientes.enqueueFIFO(aux);
                }

                // Sacamos el siguiente
                aux = queue.dequeue();
            }

            // Reemplazamos la cola vieja con los que sobrevivieron
            if (queue.getName()=="ReadyQueue"){
                this.readyQueue = sobrevivientes;
                this.readyQueue.setName("ReadyQueue");
            }if (queue.getName()=="BlockedQueue"){
                this.blockedQueue = sobrevivientes;
                this.blockedQueue.setName("BlockedQueue");
            }
        }
    }
    
    public void checkAndPurgeIO (){
        if (this.blockedQueue != null && this.blockedQueue.peek() != null) {
            Queue sobrevivientes = new Queue();
            PCB aux = this.blockedQueue.dequeue();
            while (aux != null) {
                InputOutput temp = this.ioQueue.getFirstIO();
                while (temp != null){
                    if (aux==temp.getPcbProcess()){
                        aux.setDeadlineR(aux.getDurationR()- 1);
                        if (aux.getDuration()-aux.getDurationR()==temp.getTesponseTime()){
                            readyQueue.enqueueFIFO(aux);
                        }else{
                            blockedQueue.enqueueFIFO(aux);
                        }
                    }
                temp = temp.getNext();
                }
            }
                aux = blockedQueue.dequeue();
        }
    }
}
