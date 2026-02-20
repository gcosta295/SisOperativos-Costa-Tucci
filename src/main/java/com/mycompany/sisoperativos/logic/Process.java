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
    private int deadLine;

    public PCB getPCB() {
        return PCB;
    }

    public String getProcessName() {
        return processName;
    }

    public int getDeadLine() {
        return deadLine;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setDeadLine(int deadLine) {
        this.deadLine = deadLine;
    }
    
    public Process(){
        this.PCB=new PCB();
        this.deadLine=0;
        this.processName=null;
    }
    
    public void periodicProcess(PCB pcb, Queue newQueue, String politic){
        int id = pcb.getId();
        boolean flag = true;
        if ((id % 13 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(1);
           this.processName="Image Upload";
           this.PCB.setInputOutput("SD Memory");
           this.PCB.setDuration(20);
           this.deadLine=68;
           this.PCB.setSize(1);
        }else if ((id % 11 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(2);
           this.processName="Temperature";
           this.PCB.setInputOutput("Thermometer");
           this.PCB.setDuration(12);
           this.deadLine=40;
           this.PCB.setSize(5);
        }else if ((id % 7 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(3);
           this.processName="Location";
           this.PCB.setInputOutput("GNSS");
           this.PCB.setDuration(11);
           this.deadLine=38;
           this.PCB.setSize(8);
        }else if ((id % 5 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(4);
           this.processName="Telemetry";
           this.PCB.setDuration(10);
           this.deadLine=20;
           this.PCB.setSize(15);
        }else if ((id % 3 == 0)&&(flag)){
           flag = false;
           pcb.setPriority(5);
           this.processName="Beacon Radio";
           this.PCB.setInputOutput("Receptor");
           this.PCB.setDuration(6);
           this.deadLine=22;
           this.PCB.setSize(15);
        }else if (flag){
           flag = false;
           pcb.setPriority(6);
           this.processName="System Health";
           this.PCB.setDuration(2);
           this.deadLine=8;
           this.PCB.setSize(60);
        }
        if ((id % 107==0) || (id % 113==0) || (id % 139==0) || (id % 101==0) || (id % 127==0)){
            Process aperiodicP = new Process();
            aperiodicP.aperiodicProcess(false, newQueue, politic);
        }
        this.pcbUpdate();
        this.enqueue(politic, newQueue, pcb);
    }
    
    
    
    public void aperiodicProcess(boolean flag, Queue newQueue, String politic){
        int id = this.PCB.getId();
        if (flag){
            int counter = 10;
            while (counter>0){
                Process aperiodicButton = new Process();
                aperiodicButton.aperiodicProcess(false, newQueue, politic);
                counter -= 1;
            }
        }else{
            if (id % 2 == 0){
                this.PCB.setUser("Mission Control Center");
                flag = true;
                if ((id % 3 == 0)&&(flag)){
                    flag = false;
                    this.processName="Update Software";
                    this.PCB.setInputOutput("Sysadmin");
                    this.PCB.setDuration(40);
                    this.deadLine=104;
                    this.PCB.setSize(150);
                    this.PCB.setPriority(4);
                }
                if (flag){
                    flag = false;
                    this.processName="Message";
                    this.PCB.setInputOutput("Receptor");
                    this.PCB.setDuration(80);
                    this.deadLine=204;
                    this.PCB.setSize(15);
                    this.PCB.setPriority(4);
                }
            }else{
                flag = true;
                this.PCB.setUser("Emergency");
                if ((id % 3 == 0)&&(flag)){
                    flag = false;
                    this.processName="Collision avoidance";
                    this.PCB.setInputOutput("Sensor");
                    this.PCB.setDuration(10);
                    this.deadLine=38;
                    this.PCB.setSize(80);
                    this.PCB.setPriority(7);
                }
                if ((id % 5 == 0)&&(flag)){
                    flag = false;
                    this.processName="Full Memory";
                    this.PCB.setInputOutput("SD Memory");
                    this.PCB.setDuration(60);
                    this.deadLine=156;
                    this.PCB.setSize(1);
                    this.PCB.setPriority(7);
                    
                }
                if ((id % 7 == 0)&&(flag)){
                    flag = false;
                    this.processName="Radiation Sensor";
                    this.PCB.setInputOutput("Radiation Detector");
                    this.PCB.setDuration(70);
                    this.deadLine=172;
                    this.PCB.setSize(10);
                    this.PCB.setPriority(7);
                }
                if (flag){
                    flag = false;
                    this.processName="Charging Cut";
                    this.PCB.setInputOutput("Sysadmin");
                    this.PCB.setDuration(20);
                    this.deadLine=64;
                    this.PCB.setSize(150);
                    this.PCB.setPriority(7);
                }
            }
            this.pcbUpdate();
            this.enqueue(politic, newQueue, PCB);
        }       
    }
    
    public void enqueue(String politic, Queue newQueue, PCB pcb){
        if (politic == "FIFO" || politic == "RR"){
            newQueue.enqueueFIFO(pcb);
        }else if (politic == "SRT"){
            newQueue.enqueueByRemainingTime(pcb);
        }else if (politic == "Priority"){
            newQueue.enqueueByPriority(pcb);
        }else if (politic == "EDF"){
            newQueue.enqueueByDeadline(pcb);
        }
    }
    
    public void pcbUpdate (){
        this.PCB.setDeadlineR(deadLine);
        this.PCB.setDurationR(this.PCB.getDuration());
    }
}
