/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pojos;

/**
 *
 * @author maria
 */
public class Doctor {
    private Integer doctorId; //Unique for each doctor - cannot be repeated for another patient.
    private String name;
    private String lastName;
    private String email;
    private Integer patientId;

    public Doctor() {
    }

    public Doctor(Integer doctorId, String name, String lastName, String email, Integer patientId) {
        this.doctorId = doctorId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.patientId = patientId;
    }
    
    public Doctor(Integer doctorId, String name, String lastName, String email) {
        this.doctorId = doctorId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setUserId(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public String toString() {
        return "Doctor{" + "id=" + doctorId + ", name=" + name + ", surname=" + lastName + ", email=" + email + '}';
    }
}
