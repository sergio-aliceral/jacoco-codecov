package br.com.aliceraltecnologia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErroDTO {

	private String status_code;

	private String message;

}
