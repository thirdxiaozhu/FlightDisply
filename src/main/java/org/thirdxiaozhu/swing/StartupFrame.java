package org.thirdxiaozhu.swing;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;

public class StartupFrame {
    private static void createGUI(){
        FlatIntelliJLaf.install();
        JFrame frame = new JFrame("值机引导");
        frame.setContentPane(new MainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               createGUI();
            }
        });
    }
}
