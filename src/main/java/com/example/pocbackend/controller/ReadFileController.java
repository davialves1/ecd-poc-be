package com.example.pocbackend.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.pocbackend.model.DynamicFileDTO;
import com.example.pocbackend.model.FileDTO;
import com.example.pocbackend.model.RowDataDTO;

@RestController
public class ReadFileController {

	@CrossOrigin
	@GetMapping("/read-file-dynamic")
	public ResponseEntity<DynamicFileDTO> readDynamicFile() throws IOException {
		Iterator<Sheet> sheetIterator = getCurrentSheet().sheetIterator();
		Map<String, List<Map<String, String>>> worksheetsGroup = new HashMap<>();
		int index = 0;
		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			String sheetName = sheet.getSheetName();
			List<Map<String, String>> worksheet = getDataFromSheets(sheet);
			worksheetsGroup.put(sheetName, worksheet);
			index++;
		}
		DynamicFileDTO dynamicFileDTO = new DynamicFileDTO(worksheetsGroup);
		return new ResponseEntity<>(dynamicFileDTO, HttpStatus.OK);
	}

	private List<Map<String, String>> getDataFromSheets(Sheet sheet) {
		Map<Integer, String> columnMap = createColumnMapString(sheet);
		List<Map<String, String>> allRowValues = new ArrayList<>();
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			Map<String, String> rowValues = new HashMap<>();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cell.getRowIndex() > 0) {
					rowValues.put("Id", String.valueOf(cell.getRowIndex()));
					DataFormatter dataFormatter = new DataFormatter();
					String cellValue = dataFormatter.formatCellValue(cell);
					if (cellValue.contains("SUV")) {
						rowValues.put("errors", "DEF is not allowed");
					} else {
						rowValues.put("errors", "No errors");
					}
					String key = columnMap.get(cell.getColumnIndex());
					if(!key.isEmpty()) {
						rowValues.put(key, cellValue);
					}
				}
			}
			if (row.getRowNum() > 0) {
				allRowValues.add(rowValues);
			}
		}
		return allRowValues;
	}

	@CrossOrigin
	@GetMapping("/read-file")
	public ResponseEntity<FileDTO> readFile() throws IOException{
		Sheet sheet = getCurrentSheet().getSheetAt(0);
		FileDTO fileDTO = new FileDTO(getRowData(sheet));
		return new ResponseEntity<>(fileDTO, HttpStatus.OK);
	}

	public Workbook getCurrentSheet() throws IOException {
		FileInputStream inputStream = new FileInputStream("file.xlsx");
		Workbook workbook = WorkbookFactory.create(inputStream);
		return workbook;
	}


	private Map<Integer, String> createColumnMapString(Sheet sheet) {
		Map<Integer, String> columnMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		int index = 0;
		for (Cell cell: headerRow) {
			columnMap.put(index++, cell.getStringCellValue());
		}
		return columnMap;
	}

	private Map<String, Integer> createColumnMap(Sheet sheet) {
		Map<String, Integer> columnMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		int index = 0;
		for (Cell cell: headerRow) {
			columnMap.put(cell.getStringCellValue(), index++);
		}
		return columnMap;
	}

	public List<RowDataDTO> getRowData(Sheet sheet) {
		Map<String, Integer> columnMap = createColumnMap(sheet);

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
						if (cell.getStringCellValue().equalsIgnoreCase("usa")) {
							rowDataDTO.getErrors().put("market", "USA market not allowed");
						}
					}
					if (columnMap.get("Price") == cell.getColumnIndex()) {
						rowDataDTO.setPrice((long)cell.getNumericCellValue());
					}
				}
			}
			if (row.getRowNum() > 0 && Boolean.TRUE.equals(!rowDataDTO.checkForEmptyRow())) {
				rowDataDTO.setId(row.getRowNum());
				rows.add(rowDataDTO);
			}
		}
		return rows;
	}
}
