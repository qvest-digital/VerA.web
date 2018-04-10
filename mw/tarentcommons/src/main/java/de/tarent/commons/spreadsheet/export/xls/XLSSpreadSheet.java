package de.tarent.commons.spreadsheet.export.xls;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.tarent.commons.spreadsheet.export.SpreadSheet;

public class XLSSpreadSheet implements SpreadSheet {
	protected Properties properties = new Properties();

	protected HSSFWorkbook workbook;
	protected HSSFSheet sheet;
	protected HSSFRow row;
	protected int rows;
	protected short cells;

	public void setProperty(String key, String value) throws IOException {
		properties.setProperty(key, value);
	}

	public String getProperty(String key) throws IOException {
		return properties.getProperty(key);
	}

	public void init() throws IOException {
		workbook = new HSSFWorkbook();
	}

	public void save(OutputStream outputStream) throws IOException {
		workbook.write(outputStream);
	}

	public String getContentType() {
		return "application/vnd.ms-excel";
	}

	public String getFileExtension() {
		return "xls";
	}

	public void openTable(String name, int colCount) {
		rows = 0;
		sheet = workbook.createSheet(name);
	}

	public void closeTable() {
	}

	public void openRow() {
		cells = 0;
		row = sheet.createRow(rows++);
	}

	public void closeRow() {
	}

	public void addCell(Object content) {
		HSSFCell cell = row.createCell(cells++);

		if (content == null) {
			// nothing
		} else if (content instanceof Date) {
			cell.setCellValue((Date)content);
		} else if (content instanceof Number) {
			cell.setCellValue(((Number)content).doubleValue());
		} else {
			cell.setCellValue(content.toString());
		}
	}
}
