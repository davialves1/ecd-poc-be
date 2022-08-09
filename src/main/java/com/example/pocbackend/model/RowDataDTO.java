package com.example.pocbackend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RowDataDTO {

	public Integer id;
	public String model;
	public Integer year;
	public String market;
	public Long price;
	public Map<String, String> errors = new HashMap<>();

	public Boolean checkForEmptyRow() {
		return model.isEmpty() && year == 0 && market.isEmpty() && price == 0;
	}

}
