package com.mycompany.sisoperativos.logic;

import com.mycompany.sisoperativos.gui.Dashboard;

/**
 * @author gabri & astv06
 */
public class SisOperativosCostaTucci {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Dashboard ventana = new Dashboard();
                ventana.setLocationRelativeTo(null);
                ventana.setVisible(true);
            }
        });
    }
}