package com.example.pocbackend.model;

import java.nio.file.attribute.FileTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetaDataDTO {
	public String fileName;
	public String creationDate;
	public String lastModifiedDate;
	public String author;
}
