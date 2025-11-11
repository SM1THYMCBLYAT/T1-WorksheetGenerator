package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;

public class homePage extends JFrame{

        public homePage() {
            this.setTitle("Educreate"); //text box name
            this.setSize(1920, 880); //frame size
            this.setVisible(true); //visibility of frame
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit button closes

            ImageIcon logo = new ImageIcon("LOGO.png"); //textbox logo
            setIconImage(logo.getImage());
            getContentPane().setBackground(new Color(0xdaf0ff)); //back color

            JButton login = new JButton("Login");
            login.setBounds(50, 50, 100, 50);
            login.addActionListener(e -> {

            });
            add(login);
    }
}