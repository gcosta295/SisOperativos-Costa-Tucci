/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisoperativos.logic;

/**
 *
 * @author astv06
 */
public class Process {
    private PCB PCB;
    private String processName;
    private int duration;
    private int quantum;
    private int inputOutput;
    private int deadLine;

    public String getProcessName() {
        return processName;
    }

    public int getDuration() {
        return duration;
    }

    public int getQuantum() {
        return quantum;
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

    public void setQuantum(int quantum) {
        this.quantum = quantum;
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
    
    public Process periodicProcess(PCB pcb){
        int id = pcb.getPriority();
        boolean flag = true;
        if ((id % 13 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(1);
           this.processName="Image upload";
        }
        if ((id % 11 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(2);
           this.processName="Temperature";
        }
        if ((id % 7 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(3);
           this.processName="Location";
        }
        if ((id % 5 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(4);
           this.processName="Telemetry";
        }
        if ((id % 3 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(5);
           this.processName="Beacon Radio";
        }
        if ((id % 2 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(6);
           this.processName="System Health";
        }
        if ((id % 107==0) || (id % 113==0) || (id % 139==0) || (id % 101==0) || (id % 127==0)){
            this.aperiodicProcess(pcb, flag);
        }
        return this; 
    }
    
    
    
    public void aperiodicProcess(PCB pcb, boolean flag){
        int id = (int) ((System.currentTimeMillis() % 10000000)+1);
        pcb.setId(id);
        if (flag){
           /**
 * button call
 */
        }else{
            if (id % 2 == 0){
                pcb.setUser("Mission Control Center");
                flag = true;
                if ((id % 3 == 0)&&(flag)){
                    flag = false;
                    this.processName="Update Software";
                    pcb.setPriority(4);
                }
                if (flag){
                    flag = false;
                    this.processName="Message";
                    pcb.setPriority(4);
                }
            }else{
                flag = true;
                pcb.setUser("Emergency");
                if ((id % 3 == 0)&&(flag)){
                    flag = false;
                    this.processName="Collision avoidance";
                    pcb.setPriority(7);
                }
                if ((id % 5 == 0)&&(flag)){
                    flag = false;
                    this.processName="Full Memory";
                    pcb.setPriority(7);
                    
                }
                if ((id % 7 == 0)&&(flag)){
                    flag = false;
                    this.processName="Radiation Sensor";
                    pcb.setPriority(7);
                }
                if (flag){
                    flag = false;
                    this.processName="Charging Cut";
                    pcb.setPriority(7);
                }
            }
        } 
        /**
 * put in queue 
 */        
    }
}
