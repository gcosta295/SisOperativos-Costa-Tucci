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
    private int id;
    private String state;
    private String user;
    private String processName;
    private int duration;
    private String inputOutput;
    private int deadLine;
    private int priority;

    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getUser() {
        return user;
    }

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

    public int getPriority() {
        return priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUser(String user) {
        this.user = user;
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

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public Process(){
        this.id=0;
        this.state= null;
        this.user= null;
        this.duration=0;
        this.deadLine=0;
        this.inputOutput=null;
        this.priority=0;
        this.processName=null;
    }
}
