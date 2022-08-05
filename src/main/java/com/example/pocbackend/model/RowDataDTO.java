package com.example.pocbackend.model;

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

	public Boolean checkForEmptyRow() {
		return model.isEmpty() && year == 0 && market.isEmpty() && price == 0;
	}

}
