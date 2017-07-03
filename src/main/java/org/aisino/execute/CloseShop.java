package org.aisino.execute;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

public class CloseShop {

	private final static Logger log = Logger.getLogger(CloseShop.class);

	public int doDel(String org_no) {
		String sql = "delete from sajt_org where org_no = '" + org_no + "'";
		log.info(sql);
		return Db.update(sql);
	}

}
