package com.espaneg.ui;
/*;
used for user interface, such as Swing UI
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class mainWindow {
    public static void main(String[] args) {

        ImageIcon image= new ImageIcon("LOGO2.png");
        Border headBorder1 = BorderFactory.createLineBorder(Color.WHITE,4);


        JLabel label = new JLabel(); //First text label
        label.setText("Educreate"); //Sets label text
        label.setIcon(image);


        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.TOP);
        label.setVerticalAlignment(JLabel.CENTER); //label position in frame
        label.setHorizontalAlignment(JLabel.CENTER); // label position in frame

        label.setForeground(new Color(0xffffff)); //set font color
        label.setFont(new Font("Inter",Font.PLAIN,96)); // set font
        label.setOpaque(true);
        label.setBorder(headBorder1);
        label.setBounds(0,0,250,250);


        JFrame frame = new JFrame();
        frame.setTitle("Educreate"); //text box name
        frame.setSize(1920,880); //frame size
        frame.setVisible(true); //visibility of frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit button closes

        ImageIcon logo = new ImageIcon("LOGO.png"); //textbox logo
        frame.setIconImage(logo.getImage());
        frame.getContentPane().setBackground(new Color(0xdaf0ff)); //back color

        frame.add(label); //adds label to frame
        frame.revalidate(); //refreshes adding text to already made frame







    }
}