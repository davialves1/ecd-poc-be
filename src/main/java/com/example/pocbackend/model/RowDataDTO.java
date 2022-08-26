package com.example.pocbackend.model;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RowDataDTO {

	private Integer id;
	private String model;
	private Integer year;
	private String market;
	private Long price;
	private Map<String, String> errors = new HashMap<>();

	public Boolean checkForEmptyRow() {
		return model.isEmpty() && year == 0 && market.isEmpty() && price == 0;
	}
}
