package com.example.pocbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorsDTO {
	public String columnName;
	public Integer rowId;
	public String errorMessage;
}
