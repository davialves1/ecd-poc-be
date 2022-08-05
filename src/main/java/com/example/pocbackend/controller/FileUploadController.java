package com.example.pocbackend.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

		MetaDataDTO metaDataDTO;

		// Check the file format OLE2(newer) or OOXML(older)
		if (FileMagic.valueOf(bufferedInputStream).equals(FileMagic.OLE2)) {
			POIFSFileSystem poifsFileSystem = new POIFSFileSystem(bufferedInputStream);
			DirectoryEntry dir = poifsFileSystem.getRoot();
			DocumentEntry siEntry = (DocumentEntry)dir.getEntry(SummaryInformation.DEFAULT_STREAM_NAME);
			DocumentInputStream dis = new DocumentInputStream(siEntry);
			PropertySet ps = new PropertySet(dis);
			SummaryInformation si = new SummaryInformation(ps);
			inputStream.close();
			metaDataDTO = new MetaDataDTO(
				file.getOriginalFilename(),
				formatDate(si.getCreateDateTime()),
				formatDate(si.getLastSaveDateTime()),
				si.getAuthor()
			);
		} else {
			OPCPackage opcPackage = OPCPackage.open(bufferedInputStream);
			PackageProperties props = opcPackage.getPackageProperties();
			metaDataDTO = new MetaDataDTO(
				file.getOriginalFilename(),
				formatDate(props.getCreatedProperty().orElse(new Date())),
				formatDate(props.getModifiedProperty().orElse(new Date())),
				props.getCreatorProperty().orElse("")
			);
			opcPackage.close();
		}
		return new ResponseEntity<>(metaDataDTO, HttpStatus.OK);
	}

	public String formatDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		return dateFormat.format(date);
	}

	public void printRow(Sheet sheet) {
		Iterator<Row> iterator = sheet.rowIterator();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
					case STRING:
						System.out.println(cell.getStringCellValue());
						break;
					case NUMERIC:
						System.out.println((long)cell.getNumericCellValue());
						break;
					default: break;
				}
			}
		}
	}
}
