package br.com.aliceraltecnologia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroProdutoDTO {

	private String q;

	private Double min_price;

	private Double max_price;

}
