/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos;

/**
 *
 * @author astv06
 */
public class Process {
    private PCB PCB;
    private String processName;
    private int duration;
    private int inputOutput;
    private int deadLine;

    public String getProcessName() {
        return processName;
    }

    public int getDuration() {
        return duration;
    }

    public int getInputOutput() {
        return inputOutput;
    }

    public int getDeadLine() {
        return deadLine;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setInputOutput(int inputOutput) {
        this.inputOutput = inputOutput;
    }

    public void setDeadLine(int deadLine) {
        this.deadLine = deadLine;
    }
    
    public Process(PCB pcb){
        this.PCB=pcb;
        this.duration=0;
        this.deadLine=0;
        this.inputOutput=0;
        this.processName=null;
    }
    
}
