package com.example.pocbackend.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.functions.NumericFunction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.pocbackend.model.CellUpdateDTO;

@RestController
public class UpdateValueController {

	@CrossOrigin
	@PutMapping("/update-value")
	public ResponseEntity<HttpResponse> updateValue(@RequestBody CellUpdateDTO cellUpdateDTO) throws IOException {

		FileInputStream inputStream = new FileInputStream("/Users/davialves/Desktop/Volkswagen/ECD/poc-backend/file.xlsx");
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(cellUpdateDTO.getId());

		Map<String, Integer> columnMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		int index = 0;
		for (Cell cell: headerRow) {
			columnMap.put(cell.getStringCellValue().toLowerCase(Locale.ROOT), index++);
		}

		Integer columnIndex = columnMap.get(cellUpdateDTO.getColumn());

		Cell cell = row.getCell(columnIndex);
		CellType cellType = cell.getCellType();
		if (cellType.equals(CellType.NUMERIC)) {
			cell.setCellValue(Long.parseLong(cellUpdateDTO.getNewValue()));
		} else {
			cell.setCellValue((cellUpdateDTO.newValue));
		}

		FileOutputStream fileOutputStream = new FileOutputStream("/Users/davialves/Desktop/Volkswagen/ECD/poc-backend/file.xlsx");
		workbook.write(fileOutputStream);
		inputStream.close();
		fileOutputStream.close();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
