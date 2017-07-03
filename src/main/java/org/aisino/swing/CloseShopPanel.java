package org.aisino.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.aisino.execute.CloseShop;
import org.aisino.util.LogUtil;
import org.apache.log4j.Logger;

public class CloseShopPanel extends JPanel {

	private final static Logger log = Logger.getLogger(CloseShopPanel.class);

	public static JTextArea area;
	private JButton buttonAdd;
	private JButton buttonDel;
	private JTextField field;
	private JButton buttonClose;
	private static JProgressBar bar;

	private MyTable table;
	private DefaultTableModel model;
	private Vector<String> vData;
	private Vector<String> vName;

	public CloseShopPanel() {
		vData = new Vector<>();
		vName = new Vector<>();
		vName.add("美国编号");

		paint();
		setMonitor();
	}

	private static void setBarProgressValue(int n) {
		if (n >= 100) {
			n = 100;
		}
		CloseShopPanel.bar.setValue(n);
	}

	public static void upBar(int n) {
		setBarProgressValue(CloseShopPanel.bar.getValue() + n);
	}

	private void paint() {

		// 布局
		this.setLayout(new BorderLayout());

		field = new JTextField();
		field.setBounds(10, 10, 120, 25);
		this.add(field);

		buttonAdd = new JButton("添加");
		buttonAdd.setBounds(140, 10, 60, 25);
		this.add(buttonAdd);

		buttonDel = new JButton("删除");
		buttonDel.setBounds(200, 10, 60, 25);
		this.add(buttonDel);

		// model = new DefaultTableModel(vData, vName);
		model = new MyTableModel(vData, vName);
		table = new MyTable();
		table.setModel(model);
		table.setRowHeight(30);
		table.setShowGrid(false);
		// 标题居中
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		// 单元格内容居中
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// 设置table内容居中
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, tcr);
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBounds(10, 40, 250, 260);
		this.add(tableScrollPane, BorderLayout.CENTER);

		buttonClose = new JButton("START");
		buttonClose.setBounds(280, 10, 380, 25);
		this.add(buttonClose);

		area = new JTextArea();
		JScrollPane areaScrollPane = new JScrollPane(area);
		areaScrollPane.setBounds(280, 40, 380, 260);
		area.setEditable(false);
		this.add(areaScrollPane);

		bar = new JProgressBar(0, 100);
		bar.setBounds(10, 310, 650, 30);
		this.add(bar);

		this.setLayout(null);

	}

	// 监听器
	private void setMonitor() {
		buttonAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String value = field.getText();
				if (value == null || "".equals(value.trim())) {
					return ;
				} else {
					value = value.replace((char) 12288, ' ');// 去除奇葩客户全角空格
					value = value.trim();
					if(!((MyTableModel) model).getSet().contains(value)) {
						model.addRow(new String[] { value });
						field.setText("");
					}
				}
//				System.out.println(set.toString());

			}
		});

		buttonDel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (vData.isEmpty()) {
					return;
				}
				int[] rows = table.getSelectedRows();
				for (int i = 0; i < rows.length; i++) {
					String value = (String) model.getValueAt(rows[i], 0);
					((MyTableModel) model).getSet().remove(value);
				}
				for (int i = 0; i < rows.length; i++) {
					model.removeRow(table.getSelectedRow());
				}
			}
		});

		buttonClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// 关闭 门店
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						invoke();
					}
				}, new Date(System.currentTimeMillis() + 2000));
			}
		});
	}
	
	private void invoke() {
		try {
			CloseShopPanel.bar.setValue(0);
			CloseShopPanel.area.setText("");
			
			LogUtil.appendLog(CloseShopPanel.area, "准备删除...", false);
			Set<String> set = ((MyTableModel) model).getSet();
			if(set.size() <= 0) {
				LogUtil.appendLog(CloseShopPanel.area, "空的美国编号列表.", true);
				CloseShopPanel.bar.setValue(100);
				return ;
			}
			int size = (int) Math.ceil(100.0 / set.size()) + 1;
			CloseShop closeShop = new CloseShop();
			int success = 0;
			
			for(String org_no : set) {
				LogUtil.appendLog(CloseShopPanel.area, "正在删除 " + org_no + " ...", true);
				int i = closeShop.doDel(org_no);
				if(i == 1) {
					LogUtil.appendLog(CloseShopPanel.area, "OK!", true);
					success++;
				} else {
					LogUtil.appendLog(CloseShopPanel.area, org_no + " 删除失败, 错误原因：" + org_no + " 记录不存在!", true);
				}
				CloseShopPanel.upBar(size);
			}
			LogUtil.appendLog(CloseShopPanel.area, "成功：" + success + ", 失败：" + (set.size() - success) + "!", true);
			vData.clear();
			model = new MyTableModel(vData, vName);
			table.setModel(model);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
