package com.example.pocbackend.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SimpleReaderController {

	@CrossOrigin
	@PostMapping("/read")
	public void readFile(MultipartFile multipartFile) throws IOException {
		Path path = Paths.get("/Users/davialves/Desktop/Volkswagen/ECD/poc-backend/file.xlsx");
		multipartFile.transferTo(path);
		FileInputStream inputStream = new FileInputStream(path.toFile());
		System.out.println("Line 22");
	}
}
