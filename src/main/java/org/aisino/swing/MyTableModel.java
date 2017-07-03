package org.aisino.swing;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class MyTableModel extends DefaultTableModel {

	private Set<String> set = new HashSet<>();

	public Set<String> getSet() {
		return set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}

	public MyTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		String value = (String) aValue;
		value = value.replace((char) 12288, ' ');
		
		String oriValue = (String) getValueAt(row, column);
		
		if(!"".equals(value.trim()) && !set.contains(value)) {
			set.remove(oriValue);
			set.add(aValue.toString());
		} else {
			value = oriValue;
		}
		super.setValueAt(value, row, column);
	}

	@Override
	public void addRow(Object[] rowData) {
		// TODO Auto-generated method stub
		super.addRow(rowData);
		set.add(rowData[0].toString());
		System.out.println(set);
	}

	@Override
	public void removeRow(int row) {
		// TODO Auto-generated method stub
		super.removeRow(row);
		System.out.println(set);
	}

}
