package com.example.pocbackend.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.codec.digest.XXHash32;
import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.UnexpectedPropertySetTypeException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.pocbackend.model.MetaDataDTO;

@RestController
public class FileUploadController {

	@CrossOrigin()
	@GetMapping( "/")
	public String test() {
		return "Hello World";
	}

	@CrossOrigin()
	@PostMapping("/upload")
	public ResponseEntity<MetaDataDTO> uploadFile(MultipartFile file)
		throws IOException, InvalidFormatException, NoPropertySetStreamException, UnexpectedPropertySetTypeException {
		Path path = Paths.get("/Users/davialves/Desktop/Volkswagen/ECD/poc-backend/file.xlsx");
		file.transferTo(path);
		FileInputStream inputStream = new FileInputStream(path.toFile());

		MetaDataDTO metaDataDTO;

		// We are checking the file format OLE2(newer) or OOXML(older)
		if (FileMagic.valueOf(inputStream).equals(FileMagic.OOXML)) {
			POIFSFileSystem poifs = new POIFSFileSystem(inputStream);
			DirectoryEntry dir = poifs.getRoot();
			DocumentEntry siEntry =      (DocumentEntry)dir.getEntry(SummaryInformation.DEFAULT_STREAM_NAME);
			DocumentInputStream dis = new DocumentInputStream(siEntry);
			PropertySet ps = new PropertySet(dis);
			SummaryInformation si = new SummaryInformation(ps);
			String author = si.getAuthor();
			inputStream.close();
			System.out.println("Author: " + author);
			System.out.println("Summary Information: " + si);
			metaDataDTO = new MetaDataDTO(
				file.getOriginalFilename(),
				formatDate(si.getCreateDateTime()),
				formatDate(si.getLastSaveDateTime()),
				si.getAuthor()
			);
		} else {
			OPCPackage opcPackage = OPCPackage.open(inputStream);
			PackageProperties props = opcPackage.getPackageProperties();
			System.out.println(props.toString());
			metaDataDTO = new MetaDataDTO(
				file.getOriginalFilename(),
				formatDate(props.getCreatedProperty().orElse(new Date())),
				formatDate(props.getModifiedProperty().orElse(new Date())),
				props.getCreatorProperty().orElse("")
			);
			opcPackage.close();
		}

		readExcelFile();
		return new ResponseEntity<MetaDataDTO>(metaDataDTO, HttpStatus.OK);

	}

	public String formatDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		return dateFormat.format(date);
	}

	public void readExcelFile() throws IOException {
		FileInputStream inputStream = new FileInputStream(new File("/Users/davialves/Desktop/Volkswagen/ECD/poc-backend/file.xlsx"));
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);
		String header = sheet.getHeader().toString();
		Integer firstRowNumber = sheet.getFirstRowNum();
		Integer lastRowNumber = sheet.getLastRowNum();
		Row row = sheet.getRow(0);

//		Log values
		System.out.println("-------------------");
		System.out.println("sheet - " + sheet);
		System.out.println("-------------------");
		System.out.println("header - " + header.toString());
		System.out.println("-------------------");
		System.out.println("firstRowNumber - " + firstRowNumber);
		System.out.println("-------------------");
		System.out.println("lastRowNumber - " + lastRowNumber);
		System.out.println("-------------------");
		System.out.println("row - " + row.getCell(0));
	}
}
