package com.mycompany.sisoperativos.logic;

import com.mycompany.sisoperativos.gui.Dashboard;

public class Clock implements Runnable {

    private int contadorCiclos = 0;
    private int duracionCicloMs;
    private boolean encendido = true;
    private final Scheduling scheduler;
    private Dashboard gui;

    public Clock(int duracionInicial, Scheduling scheduler, Dashboard gui) {
        this.duracionCicloMs = duracionInicial;
        this.scheduler = scheduler;
        this.gui = gui;
    }

    public void setDuracionCiclo(int nuevaDuracion) {
        this.duracionCicloMs = nuevaDuracion;
    }

    public int getContadorCiclos() {
        return contadorCiclos;
    }
    
    @Override
    public void run() {
        while (encendido) { 
            try {
                // Pausa del ciclo (Fuera del candado para no bloquear el GUI)
                Thread.sleep(duracionCicloMs);
                contadorCiclos++; //Incremento de Ciclo
                System.out.println(">>> Reloj Tick: " + contadorCiclos);
                // Bloqueamos el planificador completo antes de hacer CUALQUIER cambio
                synchronized (scheduler.lock) {
                    scheduler.runExecutionCycle();
                    scheduler.ageAllQueues();

                    // CREACIÓN DE PROCESOS PERIÓDICOS ---
                    if (contadorCiclos % 8 == 0) {
                        crearProcesoPeriodico(2);
                    }
                    if (contadorCiclos % 12 == 0) {
                        crearProcesoPeriodico(3);
                    }
                    if (contadorCiclos % 18 == 0) {
                        crearProcesoPeriodico(5);
                    }
                    if (contadorCiclos % 40 == 0) {
                        crearProcesoPeriodico(7);
                    }
                    if (contadorCiclos % 80 == 0) {
                        crearProcesoPeriodico(11);
                    }
                    if (contadorCiclos % 200 == 0) {
                        crearProcesoPeriodico(13);
                    }

                   
                }//fin candado 
               
                //actualizar interfaz
                if (gui != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        //estas son todas las tablas de las diferentes colas de la interfaz que hay que actualizar
                        gui.updateStatus(contadorCiclos, scheduler.getCurrentProcess());
                        gui.updateReadyQueue(scheduler.getReadyQueue());
                        gui.updateBlockedQueue(scheduler.getBlockedQueue());
                        gui.updateFinishedQueue(scheduler.getFinishedQueue());
                        gui.updateSuspendedReadyQueue(scheduler.getSuspendedReadyQueue());
                        gui.updateSuspendedBlockedQueue(scheduler.getSuspendedBlockedQueue());
                        double usoActual = scheduler.getCpuUtilization();
                        gui.getMonitor().updateData(usoActual);
                    });
                } else {
                    System.out.println("!!! ERROR: El reloj no tiene conexión con la ventana (gui es null)");
                }

            } catch (InterruptedException e) {
                System.out.println("El reloj fue interrumpido.");
                break; 
            } catch (Exception e) {
                System.err.println("¡CRASH EN EL RELOJ! Motivo:");
                e.printStackTrace();
                break;
            }
        }
    }

    // Método auxiliar para limpiar el código de creación de procesos
    private void crearProcesoPeriodico(int id) {
        Process process = new Process();
        process.getPCB().setId(id);
        process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic(), this.getContadorCiclos());
    }
}
