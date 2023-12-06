/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pojos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maria
 */
public class Role {
    
    private Integer id; //1 PATIENT Y 2 DOCTOR
    private String type; //patient o doctor   
    private List<User> users;
    
    
    public Role() {
        super();
        this.users = new ArrayList<User>();
    }

    public Role(String role){
        super();
        this.type = role;
        this.users = new ArrayList<User>();
    }

    public Role(Integer id, String type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return type;
    }

    public void setRole(String role) {
        this.type = role;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role: id=" + id + ", role=" + type + "";
    }
}
