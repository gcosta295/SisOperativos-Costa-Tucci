package com.mycompany.sisoperativos.logic;

import com.mycompany.sisoperativos.gui.Dashboard;

/**
 * @author gabri & astv06
 */
public class SisOperativosCostaTucci {

    // El método main ahora está directamente dentro de la clase principal
    public static void main(String[] args) {

        // Esto le dice a Java que abra tu ventana (Dashboard)
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Instanciamos tu ventana
                Dashboard ventana = new Dashboard();

                // La centramos en la pantalla
                ventana.setLocationRelativeTo(null);

                // ¡La hacemos visible!
                ventana.setVisible(true);
            }
        });

    }
    
} // Fin de la clase SisOperativosCostaTucci