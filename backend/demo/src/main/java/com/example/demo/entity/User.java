package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 20)
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Column(name = "role_level", nullable = false)
    private int roleLevel;

    public User() {}

    public User(String id, String password, int roleLevel) {
        this.id = id;
        this.password = password;
        this.roleLevel = roleLevel;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleLevel() {
        return roleLevel;
    }
    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
    }
}
