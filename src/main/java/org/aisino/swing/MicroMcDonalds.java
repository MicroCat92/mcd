package org.aisino.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.Logger;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;

@SuppressWarnings("serial")
public class MicroMcDonalds extends JFrame {

	private final static Logger log = Logger.getLogger(MicroMcDonalds.class);

	public static int SCREENWIDTH = 768;
	public static int SCREENHEIGHT = 384;

	private static void os() {

		InitGlobalFont(new Font("黑体", Font.PLAIN, 10));
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);
			// BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("set skin fail!");
		}
	}

	private static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}

	public static void main(String[] args) {
os();
		init();
		setOSStyle();

		MicroMcDonalds demo = new MicroMcDonalds();
		demo.setSize(SCREENWIDTH, SCREENHEIGHT);
		demo.setLocationByPlatform(true);
		demo.setTitle("麦当劳信息维护工具");
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setLocationRelativeTo(null);
		demo.setResizable(false);
		demo.setIconImage(new ImageIcon(PathKit.getRootClassPath() + File.separator + PropKit.get("LOGO")).getImage());

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.RIGHT); // 设置选项卡在坐标
		JPanel p1 = new NewPanel();
		JPanel p2 = new CloseShopPanel();
		// JPanel p3 = new ResumeShopPanel();
		tabbedPane.add("餐厅信息导入", p1);
		tabbedPane.add("门店关闭", p2);
		// tabbedPane.add("门店重开", p3);
		demo.add(tabbedPane, BorderLayout.CENTER); // 将选项卡窗体添加到 主窗体上去

		demo.setVisible(true);

	}

	private static void init() {
		PropKit.use("jdbc.properties");

		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("url"), PropKit.get("user"), PropKit.get("pwd"),
				PropKit.get("driver"));
		c3p0Plugin.start();
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		arp.setDialect(new OracleDialect());
		arp.start();
	}

	/**
	 * 使用当前系统风格
	 */
	private static void setOSStyle() {
		if (UIManager.getLookAndFeel().isSupportedLookAndFeel()) {
			final String platform = UIManager.getSystemLookAndFeelClassName();
			// If the current Look & Feel does not match the platform Look &
			// Feel,
			// change it so it does.
			if (!UIManager.getLookAndFeel().getName().equals(platform)) {
				try {
					UIManager.setLookAndFeel(platform);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

}
