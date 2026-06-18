/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package utility;

public class Validator {

    // Diubah suai: Nama parameter ditukar daripada matricNo kepada userID demi keseragaman global
    public static boolean validateLoginFields(String userID, String password) {
        return validateInput(userID) && validateInput(password) && password.length() >= 4;
    }

    // Mengekalkan fungsi static untuk semakan input kosong
    public static boolean validateInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    // Mengekalkan fungsi static untuk semakan asas format emel
    public static boolean validateEmail(String email) {
        if (!validateInput(email)) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
}