package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;

public class homePage {

    public static void main(String[] args) {

        JFrame frame3 = new JFrame();
        frame3.setTitle("Educreate"); //text box name
        frame3.setSize(1920,880); //frame size
        frame3.setVisible(true); //visibility of frame
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit button closes

        ImageIcon logo = new ImageIcon("LOGO.png"); //textbox logo
        frame3.setIconImage(logo.getImage());
        frame3.getContentPane().setBackground(new Color(0xdaf0ff)); //back color

        frame3.revalidate(); //refreshes adding text to already made frame







    }
}

