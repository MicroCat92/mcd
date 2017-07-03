package org.aisino.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JTextArea;

import org.aisino.swing.NewPanel;
import org.apache.log4j.Logger;

public class LogUtil {
	
	private static StringWriter stringWriter;
	private static 	PrintWriter writer;
	
	static {
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
	}

	/**
	 * 错误日志模板打印
	 * 
	 * @param log
	 * @param clazz
	 * @param e
	 */
	public static void logPrint(JTextArea area, Logger log, Class clazz, Exception e) {
		String msg = "Class = " + clazz.getName() + "; ErrorMessage = " + e.getMessage() + ".";
		log.error(getTrace(e));
		appendLog(area, msg, false);
	}

	private static String getTrace(Throwable t) {
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	public static void logPrint(JTextArea area, Logger log, Class clazz, String exceptionMsg) {
		String msg = "Class = " + clazz.getName() + "; ErrorMessage = " + exceptionMsg + ".";
		log.error(msg);
		appendLog(area, msg, false);
	}

	/**
	 * 向窗体追加日志
	 * 
	 * @param msg
	 */
	public static void appendLog(JTextArea area, String msg, boolean contentFlag) {
		if (contentFlag) {
			area.append("\n\t" + msg);
		} else {
			area.append("\n" + msg);
		}
	}
}
