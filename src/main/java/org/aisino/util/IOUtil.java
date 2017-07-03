package org.aisino.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aisino.entity.Info;

public class IOUtil {

	public static void writeUser(Map<String, String> map) throws Exception {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("result.txt"));
			pw.append(" ----------      " + "新增加的用户名密码信息(对已存在的用户的密码不做更新)" + "      ---------- \r\n");
			pw.append(" ----------                      "
					+ new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date())
					+ "                      ---------- \r\n");
			for (Map.Entry<String, String> set : map.entrySet()) {
				pw.append(set.getKey() + "\t" + set.getValue() + "\r\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public static void writeORGNO(List<Info> list) throws Exception {
		PrintWriter pw = null;
		try {
			if (list != null) {
				pw = new PrintWriter(new File("newOrgNo.txt"));
				for (Info info : list) {
					pw.println(info.getOrg_no());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * 追加记录
	 * 
	 * @param fileName
	 * @param record
	 * @throws Exception
	 */
	public static void appendNewRecord(List<Info> dataList) throws Exception {
		for (Info i : dataList) {
			String org_no = i.getOrg_no();

			String user_human = "cn-" + org_no + "inv";
			String user_human_pwd = OrgTools.split_org_no(org_no);
			i.setUser_human(user_human);
			i.setUser_human_pwd(user_human_pwd);

			String user_new = "cn-" + org_no + "invkio";
			String user_new_pwd = RandomPWD.getRandomPWD();
			i.setUser_new(user_new);
			i.setUser_new_pwd(user_new_pwd);

			String user_robot = "cn-" + org_no + "invrob";
			String user_robot_pwd = RandomPWD.getRandomPWD();
			i.setUser_robot(user_robot);
			i.setUser_robot_pwd(user_robot_pwd);
		}
	}

	/**
	 * 获取 org_no
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static List<String> getOrgNo(String fileName) throws Exception {
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
			String msg = null;
			while ((msg = br.readLine()) != null) {
				// System.out.println("* " + msg);
				list.add(msg);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static void main(String[] args) {
	}

}
