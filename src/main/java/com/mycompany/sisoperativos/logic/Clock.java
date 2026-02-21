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

    // ... (Tus otros getters y setters los dejo igual) ...
    @Override
    public void run() {
        while (encendido) { // Mejor usar la variable de control que 'true'
            try {
                // 1. Pausa del ciclo (Fuera del candado para no bloquear el GUI)
                Thread.sleep(duracionCicloMs);

                // 2. Incrementamos el ciclo
                contadorCiclos++;
                System.out.println(">>> Reloj Tick: " + contadorCiclos);

                // 3. ¡EL CANDADO GLOBAL!
                // Bloqueamos el planificador completo antes de hacer CUALQUIER cambio
                synchronized (scheduler.lock) {

                    // --- A. EJECUCIÓN DEL PLANIFICADOR ---
                    scheduler.runExecutionCycle();
                    scheduler.ageAllQueues();

                    // --- B. CREACIÓN DE PROCESOS PERIÓDICOS ---
                    // Ahora están seguros dentro del candado
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

                    // (Opcional) Reorganizar colas si tu política lo exige tras insertar nuevos procesos
                    // scheduler.Organize();
                } // 4. ¡SOLTAMOS EL CANDADO! 
                // Ahora la lógica del planificador está a salvo, podemos pintar la interfaz

                // 5. ACTUALIZAR INTERFAZ
                // (Recuerda que estas funciones en Dashboard DEBEN tener su propio 'synchronized (scheduler.lock)' adentro del invokeLater)
                if (gui != null) {
                    // Importa javax.swing.SwingUtilities; al inicio de tu archivo
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Todo lo que toca la pantalla DEBE ir aquí adentro
                        gui.updateStatus(contadorCiclos, scheduler.getCurrentProcess());
                        gui.updateReadyQueue(scheduler.getReadyQueue());
                        gui.updateBlockedQueue(scheduler.getBlockedQueue());
                        gui.updateFinishedQueue(scheduler.getFinishedQueue());
                        gui.updateSuspendedReadyQueue(scheduler.getSuspendedReadyQueue());
                        gui.updateSuspendedBlockedQueue(scheduler.getSuspendedBlockedQueue());
                    });
                } else {
                    System.out.println("!!! ERROR: El reloj no tiene conexión con la ventana (gui es null)");
                }

            } catch (InterruptedException e) {
                System.out.println("El reloj fue interrumpido.");
                break; // Salimos limpiamente si el hilo es interrumpido (ej. al cerrar app)
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
        process.periodicProcess(process.getPCB(), scheduler.getReadyQueue(), scheduler.getPolitic());
    }
}
