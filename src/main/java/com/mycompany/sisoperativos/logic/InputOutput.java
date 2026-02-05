/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author astv06
 */
public class InputOutput {
    private String name;
    private int totalTime;
    private int counter;
    private boolean inUse;
    private PCB pcbProcess;

    public String getName() {
        return name;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getCounter() {
        return counter;
    }

    public boolean isInUse() {
        return inUse;
    }

    public PCB getpcbProceso() {
        return pcbProcess;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public void setPCBProceso(PCB pcbProcess) {
        this.pcbProcess = pcbProcess;
    }
    
    public InputOutput(){
        this.counter=0;
        this.pcbProcess=null;
        this.inUse=false;
        this.name=null;
        this.totalTime=0;
    }
}
