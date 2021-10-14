package org.thirdxiaozhu.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyTableCellHeadRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JLabel label = new JLabel();
        label.setText(value != null ? value.toString() : "");
        label.setFont(new Font("微软雅黑", Font.PLAIN, 30));
        label.setForeground(new Color(255, 255, 255));
        label.setPreferredSize(new Dimension(label.getWidth(), 38));
        if(column > 4){
            label.setHorizontalAlignment(JLabel.CENTER);
        }
        return label;
    }
}
