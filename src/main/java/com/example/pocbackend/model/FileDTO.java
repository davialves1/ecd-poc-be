package com.example.pocbackend.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDTO {
	public List<RowDataDTO> rows;
}
