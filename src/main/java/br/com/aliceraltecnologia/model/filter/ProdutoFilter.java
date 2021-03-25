package br.com.aliceraltecnologia.model.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoFilter {

	public ProdutoFilter(String q) {
		this.q = q;
	}

	public ProdutoFilter(Double min_price) {
		this.min_price = min_price;
	}

	public ProdutoFilter(Double min_price, Double max_price) {
		this.min_price = min_price;
		this.max_price = max_price;
	}
	
	private String q;

	private Double min_price;

	private Double max_price;

}
