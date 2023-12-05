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
    public int role;
    public String username;
    public String password;
    public int id;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public User(int role, String username, String password, int id) {
        this.role = role;
        this.username = username;
        this.password = password;
        this.id = id;
    }
    
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
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
        return "User{" + "username=" + username + ", password=" + password + ", role=" + role + ", userId=" + id + '}';
    }
}
