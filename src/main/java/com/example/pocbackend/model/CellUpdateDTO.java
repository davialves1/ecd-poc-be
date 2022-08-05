package com.example.pocbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CellUpdateDTO {
	public Integer id;
	public String column;
	public String newValue;
}
