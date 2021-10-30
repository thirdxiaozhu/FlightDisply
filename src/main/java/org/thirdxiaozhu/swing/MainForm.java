package org.thirdxiaozhu.swing;

import org.thirdxiaozhu.Util;
import org.thirdxiaozhu.data.Flight;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

/**
 * @author jiaxv
 * @date 10.13
 */
public class MainForm {
    public JPanel mainPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private JTableHeader tableHeader;
    private final int[] tabLength = {75, 205, 238, 303, 250, 207};

    public MainForm(){
        Object[][] data = new Object[][]{
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
                {"", "", "", "", "", ""},
        };
        MyTableModel tableModel = new MyTableModel(data);
        table.setModel(tableModel);
        setTableHeader();
        table.setIntercellSpacing(new Dimension(0,1));
        table.setFont(new Font("ubuntu", Font.PLAIN, 40));
        table.setRowHeight(56);

    }

    //设置表头
    private void setTableHeader(){
        table.getTableHeader().setDefaultRenderer(new MyTableCellHeadRenderer());
        //获取表头
        tableHeader = table.getTableHeader();
        //修改表头的高度
        tableHeader.setBackground(new Color(0, 51 , 120));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 38));
    }

    public void updateTable(List<Flight> flights){
        for(int i = 0; i < 10; i++){
            //如果一页不足10条，那么用“”填充余下字段（flights.size()最大为10）
            if(i < flights.size()) {
                Flight f = flights.get(i);
                table.setValueAt(f.getFlightId(), i, 1);
                table.setValueAt(f.getApcds(), i, 2);
                table.setValueAt(Util.dealTime(f), i, 3);
                table.setValueAt(f.getCkno(), i, 4);
                table.setValueAt(Util.getState(f), i, 5);
            }else {
                for(int r: new int[]{0, 1, 2, 3, 4, 5}){
                    table.setValueAt("", i, r);
                }
            }
        }

        //设置单元格样式
        for(int r = 0; r < table.getColumnCount(); r++){
            TableColumn cm = table.getColumnModel().getColumn(r);
            cm.setCellRenderer(new MyTableRenderer());
            cm.setPreferredWidth(tabLength[r]);
        }
    }
}
