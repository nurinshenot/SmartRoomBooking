/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author NURIN
 */
public abstract class Room {
    private String roomId;
    private String roomName;
    private int capacity;
    private String facilities;
    private boolean isAvailable;

    // Constructor
    public Room(String roomId, String roomName, int capacity, String facilities) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.facilities = facilities;
        this.isAvailable = true; 
    }

    // Getters and Setters (Encapsulation)
    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public String getFacilities() { return facilities; }
    public boolean isAvailable() { return isAvailable; }
    
    public void setAvailable(boolean available) { this.isAvailable = available; }

    // Absract Method  untuk Polymorphism
    public abstract String getRoomType();
}