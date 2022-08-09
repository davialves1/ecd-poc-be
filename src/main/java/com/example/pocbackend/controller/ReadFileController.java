package com.example.pocbackend.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.pocbackend.model.FileDTO;
import com.example.pocbackend.model.RowDataDTO;

@RestController
public class ReadFileController {

	@CrossOrigin
	@GetMapping("/read-file")
	private ResponseEntity<FileDTO> readFile() throws IOException{
		Sheet sheet = readExcelFile();
		FileDTO fileDTO = new FileDTO(getRowData(sheet));
		return new ResponseEntity<>(fileDTO, HttpStatus.OK);
	}

	public Sheet readExcelFile() throws IOException {
		FileInputStream inputStream = new FileInputStream("file.xlsx");
		Workbook workbook = WorkbookFactory.create(inputStream);
		return workbook.getSheetAt(0);
	}

	public List<RowDataDTO> getRowData(Sheet sheet) {
		Map<String, Integer> columnMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		int index = 0;
		for (Cell cell: headerRow) {
			columnMap.put(cell.getStringCellValue(), index++);
		}

		List<RowDataDTO> rows = new ArrayList<>();
		Iterator<Row> iterator = sheet.rowIterator();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			RowDataDTO rowDataDTO = new RowDataDTO();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if(cell.getRowIndex() > 0) {
					if (columnMap.get("Model") == cell.getColumnIndex()) {
						rowDataDTO.setModel(cell.getStringCellValue());
					}
					if (columnMap.get("Year") == cell.getColumnIndex()) {
						rowDataDTO.setYear((int)cell.getNumericCellValue());
					}
					if (columnMap.get("Market") == cell.getColumnIndex()) {
						rowDataDTO.setMarket(cell.getStringCellValue());
					}
					if (columnMap.get("Price") == cell.getColumnIndex()) {
						rowDataDTO.setPrice((long)cell.getNumericCellValue());
					}
				}
			}
			if (row.getRowNum() > 0 && !rowDataDTO.checkForEmptyRow()) {
				rowDataDTO.setId(row.getRowNum());
				rows.add(rowDataDTO);
			}
		}
		return rows;
	}
}
