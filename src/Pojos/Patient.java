/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pojos;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 *
 * @author maria
 */
public class Patient {

    private String name;
    private String lastName;
    private LocalDate dob;
    private String email;
    private String gender;
    private Integer id;
    //private String username;
    //private String password;

    public Patient() {
    }

    public Patient(Integer id, String name, LocalDate dob, String lastName, String gender) {
        this.name = name;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.id = id;
    }

    public Patient(Integer id, String name, LocalDate dob, String lastName, String gender, String email) {
        this.name = name;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.id = id;
        this.email = email;
    }

    public Patient(String name, String lastName, LocalDate dob, String email, String gender) {
        this.name = name;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /*public String getUsername() {
        return username;
    }*/

 /*public void setUsername(String username) {
        this.username = username;
    }*/
 /*public String getPassword() {
        return password;
    }*/

 /*public void setPassword(String password) {
        this.password = password;
    }*/
    public static String formatDate(LocalDate dob) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(dob);
    }

    @Override
    public String toString() {
        return "Patient{" + ", name=" + name + ", lastName=" + lastName
                + ", dob=" + dob + ", gender=" + gender + ", email=" + email
                + "}";
    }
}
