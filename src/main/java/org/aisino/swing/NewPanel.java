package org.aisino.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.aisino.execute.UserImport;
import org.aisino.util.LogUtil;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class NewPanel extends JPanel {

	private final static Logger log = Logger.getLogger(NewPanel.class);

	public static JTextArea area;// 日志输出区
	private JButton buttonChoose;// 文件选择
	private JButton buttonExecute;// 导入
	private JTextField field_filepath;// 文件路径
	public static JProgressBar bar;// 进度条

	public NewPanel() {
		paint();
		setMonitor();
	}

	private static void setBarProgressValue(int n) {
		if (n >= 100) {
			n = 100;
		}
		NewPanel.bar.setValue(n);
	}

	public static void upBar() {
		setBarProgressValue(NewPanel.bar.getValue() + 7);
	}

	private void paint() {

		// 布局
		this.setLayout(new BorderLayout());

		this.setLayout(null);

		buttonChoose = new JButton("选择文件");
		buttonChoose.setBounds(10, 10, 80, 25);
		this.add(buttonChoose);

		field_filepath = new JTextField(21);
		field_filepath.setBounds(95, 10, 500, 25);
		field_filepath.setEditable(false);
		this.add(field_filepath);

		buttonExecute = new JButton("导入");
		buttonExecute.setBounds(600, 10, 60, 25);
		this.add(buttonExecute);

		area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		scroll.setBounds(10, 50, 650, 250);
		scroll.setAutoscrolls(true);
		area.setEditable(false);
		this.add(scroll);

		bar = new JProgressBar(0, 100);
		bar.setBounds(10, 310, 650, 30);
		this.add(bar);

	}

	// 监听器
	private void setMonitor() {
		buttonChoose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showDialog(new JLabel(), "选择导入文件");
				File file = jfc.getSelectedFile();
				if (file == null) {
					return;
				} else {
					field_filepath.setText(file.getAbsolutePath());
				}
			}
		});

		buttonExecute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

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
		String pathname = field_filepath.getText();

		area.setText("");
		NewPanel.setBarProgressValue(0);

		LogUtil.appendLog(NewPanel.area, "★麦当劳用户导入", false);
		LogUtil.appendLog(NewPanel.area, "时间 : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
				true);
		LogUtil.appendLog(NewPanel.area, "", false);

		if (pathname == null || "".equals(pathname)) {
			LogUtil.appendLog(NewPanel.area, "WARNING : 未选中任何文件", true);
			return;
		}
		NewPanel.upBar();
		if ((pathname.endsWith(".xls") || pathname.endsWith(".xlsx"))) {
			try {
				if (new UserImport().exe(pathname)) {
					Desktop.getDesktop().open(new File("result.txt"));
					setBarProgressValue(100);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				LogUtil.logPrint(NewPanel.area, log, NewPanel.class, e1);
			}
		} else {
			LogUtil.logPrint(NewPanel.area, log, NewPanel.class, "请导入正确的[Excel]文件!");
		}
	}

}
