package org.thirdxiaozhu;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.thirdxiaozhu.data.Analysis;
import org.thirdxiaozhu.swing.MainForm;

import javax.swing.*;
import java.text.ParseException;

/**
 * @author jiaxv
 * @date 10.13
 */
public class Test {

    public static void main(String[] args) throws ParseException {
        MainForm mainForm = new MainForm();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI(mainForm);
            }
        });
        new Analysis("src/main/resources/flightdata.txt", 10, "20180923000225", mainForm);
    }

    private static void createGUI(MainForm mainForm){
        FlatIntelliJLaf.install();
        JFrame frame = new JFrame("值机引导");
        frame.setContentPane(mainForm.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

}
