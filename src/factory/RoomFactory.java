/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;
import model.Room;
import model.StandardRoom;
import model.PremiumRoom;
/**
 *
 * @author NURIN
 */
public class RoomFactory {
    public static Room createRoom(String type, String roomId, String roomName, int capacity, String facilities) {
        if (type.equalsIgnoreCase("STANDARD")) {
            return new StandardRoom(roomId, roomName, capacity, facilities);
        } else if (type.equalsIgnoreCase("PREMIUM")) {
            return new PremiumRoom(roomId, roomName, capacity, facilities);
        }
        return null; // Jika type salah
    }
}