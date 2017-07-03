package org.aisino.execute;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aisino.entity.Info;
import org.aisino.exception.NonExistentOrgIdException;
import org.aisino.exception.NonExistentTaxcodeException;
import org.aisino.exception.RepetitiveOrgNoException;
import org.aisino.swing.NewPanel;
import org.aisino.util.EncryptUtil;
import org.aisino.util.IOUtil;
import org.aisino.util.LogUtil;
import org.aisino.util.OrgTools;
import org.aisino.util.ReadExcel;
import org.apache.log4j.Logger;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class UserImport {

	private final static Logger log = Logger.getLogger(UserImport.class);

	private static final String ROLE_ID_INV = "fef54bf3c4574c0a8a14b374c5af46e4";
	private static final String ROLE_ID_INVROB = "812e799e75b149f18b3e8820623bc545";
	private static final String ROLE_ID_INVKIO = "ab70b8ee0e2a47eca2bbe3dcb350366e";

	private static final String PWD_INV = "D1445875A627409E992B72ECF11B04A9";
	private static final String PWD_INVROB = "A83B341B7ADE323CC07C5994BC1E466F";
	private static final String PWD_INVKIO = "C73411290FCEADCE086D1A075E3BA5D1";

	public static final String SEPARATIONLINE = "********************";

	public void exe(final String filename) throws Exception {
		// 事务
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() {
				// TODO Auto-generated method stub
				try {
					exe2(filename);
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtil.logPrint(NewPanel.area, log, UserImport.class, e);
					return false;
				}
			}
		});

	}

	private void exe2(String filename) throws Exception {
		try {

			// 读取待处理数据转化为 bean -> dataList
			List<Info> dataList = ReadExcel.readXml(filename);
			NewPanel.upBar();//

			Set<String> checkSet = new HashSet<String>();// 检查是否有重复的 org_no

			LogUtil.appendLog(NewPanel.area, "★餐厅信息	", false);
			// 给定数据重复检查
			int checkSize = 0;
			for (Info info : dataList) {
				LogUtil.appendLog(NewPanel.area, info.toString(), true);

				checkSet.add(info.getOrg_no());
				if (checkSet.size() == checkSize) {
					LogUtil.logPrint(NewPanel.area, log, UserImport.class, "重复的美国编号 : " + info.getOrg_no());
					throw new RepetitiveOrgNoException();
				}
				checkSize++;
			}
			NewPanel.upBar();//

			// 税号检查，不存在直接抛出异常以及不存在的税号
			LogUtil.appendLog(NewPanel.area, "", false);
			LogUtil.appendLog(NewPanel.area, "★税号存在查询", false);

			for (Info info : dataList) {
				Record record = Db.findFirst("select * from tbl_nsrxx where taxcode='" + info.getTaxcode() + "'");
				if (record == null) {
					LogUtil.logPrint(NewPanel.area, log, UserImport.class, "税号 " + info.getTaxcode() + " 未上线..");
					throw new NonExistentTaxcodeException();
				}
			}
			LogUtil.appendLog(NewPanel.area, "当前餐厅的税号均已上线..", true);
			NewPanel.upBar();//

			// org查询，且插入不存在的org
			LogUtil.appendLog(NewPanel.area, "", false);
			LogUtil.appendLog(NewPanel.area, "★美国编号查询 ", false);

			List<Info> list_org_no = new ArrayList<Info>();// 存在记录集
			List<Info> list_org_no_notExist = new ArrayList<Info>();// 不存在记录集
			for (Info info : dataList) {
				Record record = Db.findFirst("select * from sajt_org where org_no ='" + info.getOrg_no() + "'");
				if (record != null) {
					list_org_no.add(info);
				} else {
					list_org_no_notExist.add(info);
				}
			}
			NewPanel.upBar();//

			if (list_org_no_notExist.size() == 0) {
				LogUtil.appendLog(NewPanel.area, "当前美国编号均存在..", true);
			} else {
				LogUtil.appendLog(NewPanel.area, "美国编号不存在列表：", true);
				for (Info i : list_org_no_notExist) {
					LogUtil.appendLog(NewPanel.area, "\t" + i.getOrg_no(), true);
				}

				// 更新
				LogUtil.appendLog(NewPanel.area, "更新美国编号中..", true);

				if (list_org_no_notExist.size() > 0) {
					for (Info info : list_org_no_notExist) {
						Record record = new Record().set("org_no", info.getOrg_no())
									.set("org_name", info.getOrg_name())
									.set("szcs", info.getSzcs())
									.set("addr", info.getAddr())
									.set("tel", info.getTel())
									.set("taxcode", info.getTaxcode())
									.set("skr", info.getPayee())
									.set("fhr", info.getChecker());
						Db.save("sajt_org", record);
					}
				}
				LogUtil.appendLog(NewPanel.area, "不存在列表中的美国编号均已上线..", true);
			}
			NewPanel.upBar();//

			// 查询org记录的org_id，并更新bean中org_id以进行user插入
			for (Info info : dataList) {
				Record record = Db.findFirst("select * from sajt_org where org_no='" + info.getOrg_no() + "'");
				if(record != null) {
					info.setOrg_id(record.getStr("id"));
					continue;
				}
				LogUtil.logPrint(NewPanel.area, log, UserImport.class, "错误的餐厅信息,餐厅编号 " + info.getOrg_no() + ".");
				throw new NonExistentOrgIdException();
			}
			NewPanel.upBar();//

			// 初始化待插入bean密码
			IOUtil.appendNewRecord(dataList);
			NewPanel.upBar();//

			// 初始化所有 bean 密码
			Map<String, String> userMap = new HashMap<String, String>();
			for (Info i : dataList) {
				userMap.put(i.getUser_human(), i.getUser_human_pwd());
				userMap.put(i.getUser_new(), i.getUser_new_pwd());
				userMap.put(i.getUser_robot(), i.getUser_robot_pwd());
			}
			NewPanel.upBar();//

			// user筛选
			Map<String, String> userMap_notExist = new HashMap<String, String>();
			for (Map.Entry<String, String> e : userMap.entrySet()) {
				Record record = Db.findFirst("select * from sajt_user where login_id='" + e.getKey() + "'"); 
				if(record == null) {
					userMap_notExist.put(e.getKey(), e.getValue());
				}
			}
			userMap = userMap_notExist;
			NewPanel.upBar();//

			// 更新不存在的user
			LogUtil.appendLog(NewPanel.area, "", false);
			LogUtil.appendLog(NewPanel.area, "★开设新的用户账号", false);
			Map<String, Info> infoMap = new HashMap<String, Info>();
			for (Info i : dataList) {
				infoMap.put(i.getOrg_no(), i);
			}
			for (Map.Entry<String, String> set : userMap.entrySet()) {
				String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				String login_id = set.getKey();
				String org_no = OrgTools.getOrg_no(login_id);
				Info info = infoMap.get(org_no);
				Record record = new Record();
				Map<Integer, String> paramsMap = new HashMap<Integer, String>();
				paramsMap.put(0, login_id);
				record.set("login_id", login_id);
				if (login_id.endsWith("inv")) {
					record.set("login_pwd", PWD_INV);
					record.set("role_id", ROLE_ID_INV);
				} else if (login_id.endsWith("invrob")) {
					record.set("login_pwd", PWD_INVROB);
					record.set("role_id", ROLE_ID_INVROB);
				} else if (login_id.endsWith("invkio")) {
					record.set("login_pwd", PWD_INVKIO);
					record.set("role_id", ROLE_ID_INVKIO);
				}
				record.set("nickname", org_no);
				record.set("org_id", info.getOrg_id());
				record.set("create_date", date);
				record.set("pwd_update_date", date);
				record.set("last_login_date", date);
				record.set("islock", 0);
				
				Db.save("sajt_user", record);
			}
			LogUtil.appendLog(NewPanel.area, "创建新用户账号成功..", true);
			NewPanel.upBar();//

			// 更新user密码
			LogUtil.appendLog(NewPanel.area, "", false);
			LogUtil.appendLog(NewPanel.area, "★初始化用户账号密码", false);
			for (Map.Entry<String, String> set : userMap.entrySet()) {
				String pwd = EncryptUtil.getSM32(set.getValue());
				String sql = "update sajt_user set login_pwd='" + pwd + "' where login_id='" + set.getKey() + "'";
				Db.update(sql);
				log.info(sql);
				
			}
			LogUtil.appendLog(NewPanel.area, "OK!", true);
			NewPanel.upBar();//

			// 导出密码
			LogUtil.appendLog(NewPanel.area, "", false);
			LogUtil.appendLog(NewPanel.area, "★正在弹出密码", false);
			List<Map.Entry<String, String>> sortList = new LinkedList<Map.Entry<String, String>>(userMap.entrySet());
			Collections.sort(sortList, new Comparator<Map.Entry<String, String>>() {

				@Override
				public int compare(Entry<String, String> o1, Entry<String, String> o2) {
					String suffix1 = o1.getKey().split("-\\d+")[1];
					String suffix2 = o2.getKey().split("-\\d+")[1];
					int len1 = suffix1.length();
					int len2 = suffix2.length();
					if (len1 != len2) {
						return len1 - len2;
					}
					int min = len1 >= len2 ? len2 : len1;
					for (int i = 0; i < min; i++) {
						String c1 = suffix1.substring(i, i + 1);
						String c2 = suffix2.substring(i, i + 1);
						if (c1.toCharArray()[0] - c2.toCharArray()[0] == 0) {
							continue;
						} else {
							return c1.toCharArray()[0] - c2.toCharArray()[0];
						}
					}
					// continue
					len1 = o1.getKey().length();
					len2 = o2.getKey().length();
					if (len1 != len2) {
						return len1 - len2;
					}
					min = len1 >= len2 ? len2 : len1;
					for (int i = 0; i < min; i++) {
						String c1 = o1.getKey().substring(i, i + 1);
						String c2 = o2.getKey().substring(i, i + 1);
						if (c1.toCharArray()[0] - c2.toCharArray()[0] == 0) {
							continue;
						} else {
							return c1.toCharArray()[0] - c2.toCharArray()[0];
						}
					}
					return 0;
				}
			});

			Map<String, String> sortMap = new LinkedHashMap<String, String>();
			for (Entry<String, String> entry : sortList) {
				sortMap.put(entry.getKey(), entry.getValue());
			}

			IOUtil.writeUser(sortMap);
			LogUtil.appendLog(NewPanel.area, "OK! --> result.txt", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
