package com.espaneg;

import com.espaneg.ui.homePage;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(homePage::new);
    }
}