package org.thirdxiaozhu.swing;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;

public class MainForm {
    public JPanel mainPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private MyTableModel tableModel;
    private JTableHeader tableHeader;

    public MainForm(){
        Object[][] data={
                {"11","22","33","44","55","66", "77"},
                {"11","22","33","44","55","66", "77"},
        };

        int[] tabLength = {75, 205, 338, 154, 149, 150, 207};

        setTableHeader();


        tableModel = new MyTableModel(data);
        table.setIntercellSpacing(new Dimension(0,1));
        table.setModel(tableModel);
        table.setFont(new Font("ubuntu", Font.PLAIN, 40));
        table.setRowHeight(56);

        for(int i = 0; i < table.getColumnCount(); i++){
            TableColumn cm = table.getColumnModel().getColumn(i);
            cm.setCellRenderer(new MyTableRenderer());
            cm.setPreferredWidth(tabLength[i]);
        }
    }

    private void setTableHeader(){
        table.getTableHeader().setDefaultRenderer(new MyTableCellHeadRenderer());
        //获取表头
        tableHeader = table.getTableHeader();
        //修改表头的高度
        tableHeader.setBackground(new Color(0, 51 , 120));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 38));
    }
}
