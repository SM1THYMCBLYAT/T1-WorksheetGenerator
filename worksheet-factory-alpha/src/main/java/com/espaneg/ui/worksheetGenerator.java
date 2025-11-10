package com.espaneg.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class worksheetGenerator {
    public static void main(String[] args) {

        ImageIcon image= new ImageIcon("LOGO2.png");
        Border headBorder1 = BorderFactory.createLineBorder(Color.WHITE,4);


        JFrame frame = new JFrame();
        frame.setTitle("Educreate"); //text box name
        frame.setSize(1920,880); //frame size
        frame.setVisible(true); //visibility of frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit button closes

        ImageIcon logo = new ImageIcon("LOGO.png"); //textbox logo
        frame.setIconImage(logo.getImage());
        frame.getContentPane().setBackground(new Color(0xdaf0ff)); //back color

        frame.revalidate(); //refreshes adding text to already made frame

    }
}