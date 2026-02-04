/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sisoperativos.logic;

/**
 *
 * @author gabri & astv06
 */
public class SisOperativosCostaTucci {

   public static void main(String[] args) throws InterruptedException { //Pruebas de reloj
        // 1. Iniciamos el reloj con ciclos de 1 segundo (1000ms)
        Clock miReloj = new Clock(1000);
        Thread hiloReloj = new Thread(miReloj);
        hiloReloj.start();

        // 2. Simulamos que después de 5 segundos, el usuario acelera el reloj
        Thread.sleep(5000); //Esto hace que no corra mas del codigo hasta que pasen los 5000 
        System.out.println(">>> Acelerando simulación a ciclos de 100ms...");
        miReloj.setDuracionCiclo(100); // Ahora el contador subirá mucho más rápido

        // 3. Dejamos que corra un poco más y lo detenemos
        Thread.sleep(2000);
        miReloj.detener();
    }
    
}
