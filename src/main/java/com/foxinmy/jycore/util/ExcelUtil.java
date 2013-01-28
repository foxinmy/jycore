package com.foxinmy.jycore.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	private final static String NULL = "null";

	private Collection<Object> objs = null; //

	private String[] strings = null; //

	private static HSSFWorkbook workbook = null; // 工作薄

	private HSSFSheet sheet = null; // 工作表

	private HSSFRow row = null; // 单元行

	private HSSFCell cell = null; // 单元格

	private HSSFCellStyle cellStyle = null; // 单元格样式

	private HSSFFont font = null; // 字体

	private HSSFRichTextString richText = null; // 单元格内容

	private int rowNum = 0; // 数据行号

	private int cellNum = 0; // 单元格号

	private int tempNum = 0; // 临时变量

	private int tempRow = 0; // 临时变量

	private boolean isHasMerge = false; // 是否含有合并项

	private int temp = 0; // 临时变量

	private int mergedCell = 0; // 合并单元格

	private int mergedRow = 0; // 合并数据行

	private String mergedKey; // 合并键

	private String title; // 标题

	private String type; // 类型

	private int mapIndex; // map

	static {
		workbook = new HSSFWorkbook();
	}

	public ExcelUtil(String title) {
		this.title = title;
		workbook = new HSSFWorkbook();
	}
	public static void main(String[] args){
		Workbook book = new HSSFWorkbook();
		try {
			FileOutputStream s = new FileOutputStream(new File("D:\\excel.xls"));
			book.write(s);
			book = new XSSFWorkbook();
			s = new FileOutputStream(new File("D:\\excel.xlsx"));
			book.write(s);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
