package org.thirdxiaozhu.swing;

import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;
    //所有的列字段
    private static final String[] columns={"", "航班号", "前往", "办票时间", "柜台", "备注"};

    public MyTableModel(Object[][] data){
        super(data, columns);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // TODO Auto-generated method stub
        //重写isCellEditable方法，设置是否可以对表格进行编辑，也可以设置某行或者列，可以编辑或者不可以编辑
        return super.isCellEditable(row, column);
    }

    @Override
    public void setValueAt(Object arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        super.setValueAt(arg0, arg1, arg2);
    }

}
