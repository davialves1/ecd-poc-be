package com.example.pocbackend.controller;

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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageProperties;
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
	public ResponseEntity<MetaDataDTO> uploadFile(MultipartFile file) throws IOException, InvalidFormatException {
		Path path = Paths.get("/Users/davialves/Desktop/Volkswagen/ECD/poc-backend/file.xlsx");
		file.transferTo(path);
		BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
		OPCPackage pckg = OPCPackage.open(file.getInputStream());
		PackageProperties props = pckg.getPackageProperties();
		System.out.println(props.toString());
		MetaDataDTO metaDataDTO = new MetaDataDTO(
			file.getOriginalFilename(),
			formatDate(props.getCreatedProperty().orElse(new Date())),
			formatDate(props.getModifiedProperty().orElse(new Date())),
			props.getCreatorProperty().orElse("")
		);

		return new ResponseEntity<MetaDataDTO>(metaDataDTO, HttpStatus.OK);
	}

	public String formatDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		return dateFormat.format(date);
	}
}
