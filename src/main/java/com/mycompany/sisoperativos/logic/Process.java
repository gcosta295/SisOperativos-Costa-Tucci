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
    private String inputOutput;
    private int deadLine;

    public String getProcessName() {
        return processName;
    }

    public int getDuration() {
        return duration;
    }

    public String getInputOutput() {
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

    public void setInputOutput(String inputOutput) {
        this.inputOutput = inputOutput;
    }

    public void setDeadLine(int deadLine) {
        this.deadLine = deadLine;
    }
    
    public Process(PCB pcb){
        this.PCB=pcb;
        this.duration=0;
        this.deadLine=0;
        this.inputOutput=null;
        this.processName=null;
    }
    
    public Process periodicProcess(PCB pcb){
        int id = pcb.getPriority();
        boolean flag = true;
        if ((id % 13 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(1);
           this.processName="Image Upload";
           this.inputOutput="SD Memory";
           this.duration=20;
           this.deadLine=68;
           this.PCB.setSize(1);
        }
        if ((id % 11 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(2);
           this.processName="Temperature";
           this.inputOutput="Thermometer";
           this.duration=12;
           this.deadLine=40;
           this.PCB.setSize(5);

        }
        if ((id % 7 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(3);
           this.processName="Location";
           this.inputOutput="GNSS";
           this.duration=11;
           this.deadLine=38;
           this.PCB.setSize(8);
        }
        if ((id % 5 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(4);
           this.processName="Telemetry";
           this.duration=10;
           this.deadLine=20;
           this.PCB.setSize(15);
        }
        if ((id % 3 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(5);
           this.processName="Beacon Radio";
           this.inputOutput="Receptor";
           this.duration=6;
           this.deadLine=22;
           this.PCB.setSize(15);
        }
        if ((id % 2 == 0)&&(flag)){
            flag = false;
           pcb.setPriority(6);
           this.processName="System Health";
           this.duration=2;
           this.deadLine=4;
           this.PCB.setSize(60);
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
                    this.inputOutput="Sysadmin";
                    this.duration=40;
                    this.deadLine=104;
                    this.PCB.setSize(150);
                    pcb.setPriority(4);
                }
                if (flag){
                    flag = false;
                    this.processName="Message";
                    this.inputOutput="Receptor";
                    this.duration=80;
                    this.deadLine=204;
                    this.PCB.setSize(15);
                    pcb.setPriority(4);
                }
            }else{
                flag = true;
                pcb.setUser("Emergency");
                if ((id % 3 == 0)&&(flag)){
                    flag = false;
                    this.processName="Collision avoidance";
                    this.inputOutput="Sensor";
                    this.duration=10;
                    this.deadLine=38;
                    this.PCB.setSize(80);
                    pcb.setPriority(7);
                }
                if ((id % 5 == 0)&&(flag)){
                    flag = false;
                    this.processName="Full Memory";
                    this.inputOutput="SD memory";
                    this.duration=60;
                    this.deadLine=156;
                    this.PCB.setSize(1);
                    pcb.setPriority(7);
                    
                }
                if ((id % 7 == 0)&&(flag)){
                    flag = false;
                    this.processName="Radiation Sensor";
                    this.inputOutput="Radiation Detector";
                    this.duration=70;
                    this.deadLine=172;
                    this.PCB.setSize(10);
                    pcb.setPriority(7);
                }
                if (flag){
                    flag = false;
                    this.processName="Charging Cut";
                    this.inputOutput="Sysadmin";
                    this.duration=20;
                    this.deadLine=64;
                    this.PCB.setSize(150);
                    pcb.setPriority(7);
                }
            }
        } 
        /**
 * put in queue 
 */        
    }
}
