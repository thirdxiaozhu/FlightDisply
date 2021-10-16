package org.thirdxiaozhu.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyTableRenderer extends DefaultTableCellRenderer {

    /*对表格进行渲染的时候单元格默认返回的是JLabel，可以根据需要返回不同的控件*/
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean arg3, int row, int column) {

        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(new Color(0, 102, 153));
        if(column > 2){
            label.setHorizontalAlignment(JLabel.CENTER);
            if(column == 4) {
                label.setBackground(new Color(0, 51, 153));
            }
        }
        label.setFont(new Font("微软雅黑", Font.PLAIN, 38));
        label.setForeground(new Color(255, 255, 255));
        if(column == 5){
            if("停止办票".equals(value.toString())){
                label.setForeground(new Color(255, 0, 0));
            }else if("正在办票".equals(value.toString())){
                label.setForeground(new Color(0, 255, 0));
            }
        }
        label.setText(value != null ? value.toString() : "");
        return label;
    }

}
