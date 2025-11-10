package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;

public class accountCreation {

    public static void main(String[] args) {

        JFrame frame2 = new JFrame();
        frame2.setTitle("Educreate"); //text box name
        frame2.setSize(1920,880); //frame size
        frame2.setVisible(true); //visibility of frame
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit button closes

        ImageIcon logo = new ImageIcon("LOGO.png"); //textbox logo
        frame2.setIconImage(logo.getImage());
        frame2.getContentPane().setBackground(new Color(0xdaf0ff)); //back color

        frame2.revalidate(); //refreshes adding text to already made frame







    }
}

