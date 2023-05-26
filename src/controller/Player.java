package controller;

import model.PlayerColor;

import javax.swing.*;

public class Player {
    private PlayerColor color;
    private String uerName;
    private int id;
    private String password;
    private ImageIcon avatar;

    public Player(String uerName, int id, String password, ImageIcon avatar) {
        this.uerName = uerName;
        this.id = id;
        this.password = password;
        this.avatar = avatar;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public String getUerName() {
        return uerName;
    }

    public void setUerName(String uerName) {
        this.uerName = uerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ImageIcon getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }
}
