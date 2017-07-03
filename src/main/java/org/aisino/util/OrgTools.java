package org.aisino.util;

public class OrgTools {
	
	/**
	 * org_no拆分
	 * @param obj
	 * @return
	 * @throws Exception 
	 */
	public static String split_org_no(Object obj) throws Exception {
		String org_no = null;
		try {
			if(obj instanceof String) {
				org_no = (String) obj;
			} else if(obj instanceof Integer) {
				org_no = (Integer)obj + "";
			} else {
				throw new Exception("错误的org_no值");
			}
			if(org_no == null || org_no.trim().length()==0) {
				throw new Exception("org_no 为空");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return "E" + org_no.substring(0, 3) + "@" + org_no.substring(3) + "#inv";
	}
	
	public static String getOrg_no(String str) {
		return str.substring(str.indexOf("cn-") + "cn-".length(), str.indexOf("inv"));
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(OrgTools.split_org_no(1980163));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
