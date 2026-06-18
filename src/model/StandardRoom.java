/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author NURIN
 */
public class StandardRoom extends Room {
    public StandardRoom(String roomId, String roomName, int capacity, String facilities) {
        super(roomId, roomName, capacity, facilities);
    }

    @Override
    public String getRoomType() {
        return "STANDARD";
    }
}
