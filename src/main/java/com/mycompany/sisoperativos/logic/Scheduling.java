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
    private Queue suspendedReadyQueue;
    private Queue suspendedBlockedQueue;
    private String politic;
    private int System_Quantum;
    private PCB currentProcess;
    private Dashboard gui;
    private int ramSize;
    private int successFinish;
    public final Object lock = new Object();
    private int totalTicks;
    private int busyTicks;
    private double sumaTiempoEspera = 0;

    public Queue getIoQueue() {
        return ioQueue;
    }

    public Queue getSuspendedReadyQueue() {
        return this.suspendedReadyQueue;
    }

    public Queue getSuspendedBlockedQueue() {
        return this.suspendedBlockedQueue;
    }

    public int getSystem_Quantum() {
        return System_Quantum;
    }

    public void setIoQueue(Queue ioQueue) {
        this.ioQueue = ioQueue;
    }

    public void setSystem_Quantum(int System_Quantum) {
        this.System_Quantum = System_Quantum;

    }

    public Scheduling(Dashboard gui) {
        this.suspendedReadyQueue = new Queue();
        this.suspendedBlockedQueue = new Queue();
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
        this.busyTicks = 0;
        this.totalTicks = 0;
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
        synchronized (this.lock) {
            //Chequeo de que existan procesos a ejecutar y ninguno ejecutandose
            if (this.readyQueue.getLen() == 0 && this.currentProcess == null) {
                return;
            }
            //Se crea una cola temporal y elemento a elemento se agrega
            //segun su politica de ordenamiento 
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
            //Se reasigna la cola de listos con la nueva cola organizada
            this.readyQueue = newQueue;
        }
    }

    public void runExecutionCycle() {
        synchronized (this.lock) {
            this.totalTicks++;
            boolean hizoSwap = manageRAM();
            if (hizoSwap) {
            } else {
                if (currentProcess == null) {
                    currentProcess = readyQueue.dequeue();
                    if (currentProcess != null) {
                        currentProcess.setQuantum(0);
                    }
                }
                if (currentProcess != null) {
                    this.busyTicks++;
                    currentProcess.setDurationR(currentProcess.getDurationR() - 1);
                    currentProcess.setQuantum(currentProcess.getQuantum() + 1);
                    currentProcess.setDeadlineR(currentProcess.getDeadlineR() - 1);
                    if (currentProcess.getInputOutput() != null) {
                        InputOutput io = this.ioQueue.ioSercher(currentProcess.getInputOutput());
                        if (io != null && (currentProcess.getDurationHope() - currentProcess.getDurationR() == io.getCounter())) {
                            gui.log("!!! PROCESO " + currentProcess.getId() + " ENVIADO A E/S !!!");
                            currentProcess.setState("Blocked");
                            io.ioChecker(currentProcess, this.blockedQueue);
                            currentProcess = null;
                        }
                    }
                }
                if (currentProcess != null) {
                    if (currentProcess.getDurationR() <= 0) {
                        currentProcess.setState("Exit");
                        int tiempoEnSistema = this.totalTicks - currentProcess.getArrivalTime();
                        int espera = tiempoEnSistema - currentProcess.getDurationHope();
                        sumaTiempoEspera += (espera < 0) ? 0 : espera;
                        if (finishedQueue != null) {
                            successFinish += 1;
                            finishedQueue.enqueueFIFO(currentProcess);
                        }
                        currentProcess = null;
                    } else if (currentProcess.getDeadlineR() <= 0) {
                        currentProcess.setState("Aborted");
                        if (finishedQueue != null) {
                            finishedQueue.enqueueFIFO(currentProcess);
                        }
                        currentProcess = null;
                    } else {
                    }
                }
            }
            InputOutput tempIO = this.ioQueue.getFirstIO();
            while (tempIO != null) {
                tempIO = tempIO.getNext();
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

// Método para envejecer procesos y purgar los vencidos SIN romper la cola
    public void checkAndPurgeDeadlines(Queue queue) {
        if (queue == null || queue.peek() == null) {
            return;
        }

        // Sincronizamos para que la interfaz no lea la cola mientras la modificamos
        synchronized (this.lock) {
            Queue temporalesVivos = new Queue(); // Aquí guardaremos los que sobrevivan
            PCB aux = queue.dequeue(); // Lo sacamos POR COMPLETO de la cola original

            while (aux != null) {
                // 1. Restamos 1 al deadline
                aux.setDeadlineR(aux.getDeadlineR() - 1);

                // 2. ¿Se le acabó el tiempo?
                if (aux.getDeadlineR() <= 0 && aux.getDurationR() > 0) {
                    gui.log("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido en espera).");
                    aux.setState("Aborted");
                    System.out.println("¡ALERTA! Proceso " + aux.getId() + " TERMINADO ANÓMALAMENTE (Deadline Vencido en espera).");

                    // ¡LIMPIEZA ABSOLUTA! Rompemos cualquier lazo con otros procesos
                    aux.setNext(null);
                    aux.setBefore(null);

                    // Lo mandamos al cementerio
                    if (finishedQueue != null) {
                        finishedQueue.enqueueFIFO(aux);
                    }
                } else {
                    // Si sobrevive, lo preparamos y lo metemos a la sala de espera temporal
                    aux.setNext(null);
                    aux.setBefore(null);
                    temporalesVivos.enqueueFIFO(aux);
                }

                // 3. Sacamos el siguiente de la cola original
                aux = queue.dequeue();
            }

            // 4. ¡LA MAGIA! Devolvemos todos los sobrevivientes a su cola original
            PCB sobreviviente = temporalesVivos.dequeue();
            while (sobreviviente != null) {
                // Al usar enqueueFIFO, se reescriben los punteros de forma limpia y segura
                queue.enqueueFIFO(sobreviviente);
                sobreviviente = temporalesVivos.dequeue();
            }
        }
    }

public boolean manageRAM() {
        synchronized (this.lock) {
            int ramUsada = calcularRamUsada();
            boolean huboIntervencion = false; // Esta variable nos dirá si el CPU trabajó en la RAM

            // 1. SWAP OUT (Expulsar si estamos llenos)
            if (ramUsada > this.ramSize) {
                gui.setCpuModo("Modo Supervisor");

                PCB victima = extraerVictimaMayorDeadline();

                if (victima != null && victima.getSize() > 0) {
                    huboIntervencion = true; // El CPU trabajó expulsando
                    String estadoAntiguo = victima.getState();

                    victima.setNext(null);
                    victima.setBefore(null);

                    if (estadoAntiguo != null && estadoAntiguo.equalsIgnoreCase("Blocked")) {
                        victima.setState("SuspendedBlocked");
                        this.suspendedBlockedQueue.enqueueFIFO(victima);
                        gui.log("SWAP OUT: Proceso " + victima.getId() + " (Bloq) expulsado a Swap.");
                    } else {
                        victima.setState("SuspendedReady");
                        this.suspendedReadyQueue.enqueueFIFO(victima);
                        gui.log("SWAP OUT: Proceso " + victima.getId() + " (Listo) expulsado a Swap.");
                    }
                    ramUsada -= victima.getSize();
                }
            }

            // 2. SWAP IN (Traer de vuelta si hay espacio y no hemos hecho swap out este ciclo)
            if (!huboIntervencion && ramUsada < this.ramSize) {
                // gui.setCpuModo("Modo Supervisor"); // <- Esto ya lo haces dentro del if abajo, puedes quitarlo de aquí afuera

                int espacioLibre = this.ramSize - ramUsada;
                PCB aDespertar = extraerSuspendidoMenorDeadline(espacioLibre);

                if (aDespertar != null && aDespertar.getSize() > 0) {
                    huboIntervencion = true; // El CPU trabajó trayendo de vuelta
                    gui.setCpuModo("Modo Supervisor");

                    String estadoAntiguo = aDespertar.getState();
                    aDespertar.setNext(null);
                    aDespertar.setBefore(null);

                    if (estadoAntiguo != null && estadoAntiguo.equalsIgnoreCase("SuspendedBlocked")) {
                        aDespertar.setState("Blocked");
                        this.blockedQueue.enqueueFIFO(aDespertar);
                        gui.log("SWAP IN: Proceso " + aDespertar.getId() + " regresó a Bloqueados.");
                    } else {
                        aDespertar.setState("Ready");
                        this.readyQueue.enqueueFIFO(aDespertar);
                        gui.log("SWAP IN: Proceso " + aDespertar.getId() + " regresó a Listos.");

                        if ("SRT".equals(this.politic) || "EDF".equals(this.politic) || "Priority".equals(this.politic)) {
                            this.Organize();
                        }
                    }
                }
            }

            // --- NUEVO: PAUSA PARA QUE EL USUARIO VEA EL MODO SUPERVISOR ---
            if (huboIntervencion) {
                try {
                    // Pausamos medio segundo (500 milisegundos). Ajusta este valor si lo quieres más rápido o lento.
                    Thread.sleep(200); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // ---------------------------------------------------------------

            return huboIntervencion; // Retornamos si el CPU estuvo ocupado
        }
    }
    // --- MÉTODOS AUXILIARES DEL GESTOR DE MEMORIA ---
    private int calcularRamUsada() {
        int total = 0;
        if (this.currentProcess != null) {
            total += this.currentProcess.getSize();
        }

        // Sumamos los de Ready
        PCB aux = this.readyQueue.peek();
        while (aux != null) {
            total += aux.getSize();
            aux = aux.getNext();
        }

        // Sumamos los de Blocked
        aux = this.blockedQueue.peek();
        while (aux != null) {
            total += aux.getSize();
            aux = aux.getNext();
        }
        return total;
    }

    private PCB extraerVictimaMayorDeadline() {
        PCB peorReady = buscarPeorDeadline(this.readyQueue);
        PCB peorBlocked = buscarPeorDeadline(this.blockedQueue);

        if (peorReady == null && peorBlocked == null) {
            return null;
        }

        PCB victimaElegida;
        boolean estabaEnReady = false; // Nueva bandera de seguridad

        if (peorReady == null) {
            victimaElegida = peorBlocked;
        } else if (peorBlocked == null) {
            victimaElegida = peorReady;
            estabaEnReady = true;
        } else {
            // Entre peorReady y peorBlocked, expulsamos al que tenga el número de deadline MÁS ALTO
            if (peorBlocked.getDeadlineR() >= peorReady.getDeadlineR()) {
                victimaElegida = peorBlocked;
            } else {
                victimaElegida = peorReady;
                estabaEnReady = true;
            }
        }

        // ¡EL TRUCO! Ahora no nos importa qué texto tenga getState()
        // Lo sacamos de la cola correcta usando la bandera.
        PCB extraido;
        if (estabaEnReady) {
            extraido = this.readyQueue.extractById(victimaElegida.getId());
        } else {
            extraido = this.blockedQueue.extractById(victimaElegida.getId());
        }

        // Si por alguna razón anómala extractById falló, imprimimos el error
        if (extraido == null) {
            System.out.println("ERROR CRÍTICO: extractById devolvió null para el ID " + victimaElegida.getId());
        }

        return extraido;
    }

    private PCB buscarPeorDeadline(Queue q) {
        PCB aux = q.peek();

        // Si la cola está vacía, retornamos null de inmediato
        if (aux == null) {
            return null;
        }

        // Asumimos inicialmente que el primero es el que tiene el "peor" deadline
        PCB peor = aux;
        int maxDeadline = aux.getDeadlineR();

        // Empezamos a revisar desde el segundo proceso
        aux = aux.getNext();

        while (aux != null) {
            if (aux.getDeadlineR() > maxDeadline) {
                maxDeadline = aux.getDeadlineR();
                peor = aux;
            }
            aux = aux.getNext();
        }

        return peor;
    }

    private PCB extraerSuspendidoMenorDeadline(int maxTamanoPermitido) {
        PCB mejorSR = buscarMejorSuspendido(this.suspendedReadyQueue, maxTamanoPermitido);
        PCB mejorSB = buscarMejorSuspendido(this.suspendedBlockedQueue, maxTamanoPermitido);

        if (mejorSR == null && mejorSB == null) {
            return null;
        }

        PCB elegido;
        boolean estabaEnSR = false;

        if (mejorSR == null) {
            elegido = mejorSB;
        } else if (mejorSB == null) {
            elegido = mejorSR;
            estabaEnSR = true;
        } else {
            if (mejorSR.getDeadlineR() <= mejorSB.getDeadlineR()) {
                elegido = mejorSR;
                estabaEnSR = true;
            } else {
                elegido = mejorSB;
            }
        }

        // Extracción segura
        if (estabaEnSR) {
            return this.suspendedReadyQueue.extractById(elegido.getId());
        } else {
            return this.suspendedBlockedQueue.extractById(elegido.getId());
        }
    }

    private PCB buscarMejorSuspendido(Queue q, int maxTamanoPermitido) {
        PCB aux = q.peek();
        PCB mejor = null;
        int minDeadline = Integer.MAX_VALUE;

        while (aux != null) {
            // Solo lo consideramos si cabe en la RAM (aux.getSize() <= maxTamanoPermitido)
            if (aux.getSize() <= maxTamanoPermitido && aux.getDeadlineR() < minDeadline) {
                minDeadline = aux.getDeadlineR();
                mejor = aux;
            }
            aux = aux.getNext();
        }
        return mejor;
    }

    // Agrégalo en Scheduling.java
    public void ageAllQueues() {
        // 1. Envejecemos a los que están en RAM
        checkAndPurgeDeadlines(this.readyQueue);
        checkAndPurgeDeadlines(this.blockedQueue);

        // 2. ¡EL PARCHE! Envejecemos a los que están en SWAP (Suspendidos)
        checkAndPurgeDeadlines(this.suspendedReadyQueue);
        checkAndPurgeDeadlines(this.suspendedBlockedQueue);
    }

    public double getCpuUtilization() {
        if (totalTicks == 0) {
            return 0.0;
        }

        // Forzamos que la operación sea decimal usando 100.0 (con el punto)
        // O haciendo un cast a (double)
        return ((double) this.busyTicks / this.totalTicks) * 100.0;
    }

    // Método para obtener el Throughput
// Fórmula: Procesos finalizados / Tiempo total transcurrido
    public double getThroughput() {
        if (totalTicks == 0) {
            return 0;
        }
        return (double) successFinish / totalTicks;
    }

    public double getAvgWaitingTime() {
        // Usamos successFinish para no diluir el promedio con los procesos abortados
        if (this.successFinish == 0) {
            return 0.0;
        }
        return this.sumaTiempoEspera / this.successFinish;
    }
}
