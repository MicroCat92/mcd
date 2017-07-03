package org.aisino.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aisino.entity.Info;
import org.aisino.exception.NonExistentColNameException;
import org.aisino.swing.NewPanel;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.kit.PropKit;

public class ReadExcel {

	private final static Logger log = Logger.getLogger(ReadExcel.class);

	private static Set<String> parameters = new HashSet<>();

	static {
		String parameter = PropKit.get("PARAMETERS");
		String[] ps = parameter.split(",");
		for (String str : ps) {
			parameters.add(str);
		}
	}

	/**
	 * 单元处理
	 * 
	 * @param cell
	 * @return
	 */
	private static String getStr(Cell cell) {
		String str = cell.toString();
		if (str == null) {
			return "";
		} else {
			str = str.replace((char) 12288, ' ');// 去除奇葩客户全角空格
			return str.trim();
		}
	}

	public static List<Info> readXml(String fileName) throws NonExistentColNameException, IOException {
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (fileName.endsWith("xlsx"))
			isE2007 = true;
		try {
			InputStream input = new FileInputStream(fileName); // 建立输入流
			Workbook wb = null;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007) {
				wb = new XSSFWorkbook(input);
			} else {
				wb = new HSSFWorkbook(input);
			}
			Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
			Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器

			// 初始化列标
			Map<String, Integer> map = new HashMap<String, Integer>();
			if (rows.hasNext()) {
				Row row = rows.next();
				Iterator<Cell> cells = row.cellIterator();
				int i = 0;
				while (cells.hasNext()) {
					Cell cell = cells.next();
					if (map.containsKey(cell.toString())) {
						continue;
					}
					map.put(cell.toString(), i++);
				}
			}
			NewPanel.upBar();//

			// 取数据
			List<Info> dataList = new ArrayList<Info>();
			while (rows.hasNext()) {
				Row row = rows.next(); // 获得行数据
				// System.out.println("Row #" + row.getRowNum()); //获得行号从0开始
				Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器

				String msg = "";
				if (cells.hasNext()) {
					Cell cell = cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					Info info = new Info();

					Iterator<String> it = parameters.iterator();
					while (it.hasNext()) {
						String key = it.next();

						Cell infoCell = row.getCell(map.get(key)) == null ? row.createCell(0, Cell.CELL_TYPE_STRING)
								: row.getCell(map.get(key));
						infoCell.setCellType(Cell.CELL_TYPE_STRING);
						String value = getStr(infoCell);
						System.out.println(value);
						setInfoElement(info, key, value);
						msg += value;
					}

					if ("".equals(msg.trim())) {
						continue;
					}
					dataList.add(info);
				}
			}
			return dataList;
		} catch (NonExistentColNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.logPrint(NewPanel.area, log, ReadExcel.class, e);
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.logPrint(NewPanel.area, log, ReadExcel.class, e);
			throw e;
		}
	}

	private static void setInfoElement(Info info, String key, String value) throws NonExistentColNameException {
		// 美国编号 餐厅名字 市场 城市 本地编号 地址 电话 邮编 餐厅类型 OM OC 是否24小时 所属公司 所属公司名字 经营模式 收款人
		// 复核人 对应纳税号 购票方名称 购票方纳税号 地址 电话 开户银行 账号 税务负责人 是否收到报税 是否完成分发票号段 未完成原因
		switch (key) {
		case "美国编号":
			info.setOrg_no(value);
			break;
		case "餐厅名字":
			info.setOrg_name(value);
			break;
		case "对应纳税号":
			info.setTaxcode(value);
			break;
		case "城市":
			info.setSzcs(value);
			break;
		case "地址":
			info.setAddr(value);
			break;
		case "电话":
			info.setTel(value);
			break;
		case "收款人":
			info.setPayee(value);
			break;
		case "复核人":
			info.setChecker(value);
			break;
		default:
			LogUtil.logPrint(NewPanel.area, log, ReadExcel.class, "没有 " + key + "这个列名.");
			throw new NonExistentColNameException();
		}

	}
}
