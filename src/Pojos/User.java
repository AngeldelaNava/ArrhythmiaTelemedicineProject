/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pojos;

/**
 *
 * @author maria
 */
public class User {

    //public String role;
    public int role_id;
    public String username;
    public byte[] password;
    public int id;

    public User() {
    }

    public User(String username, byte[] password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, byte[] password, int role) {
        super();
        this.username = username;
        this.password = password;
        this.role_id = role;
    }

    public User(int role_id, String username, byte[] password, int id) {
        this.role_id = role_id;
        //this.role = role;
        this.username = username;
        this.password = password;
        this.id = id;
    }

    /*public String getRole() {
        return role;
    }*/

 /*public void setRole(String role) {
        this.role = role;
    }*/
    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", role=" + role_id + ", userId=" + id + '}';
    }
}
