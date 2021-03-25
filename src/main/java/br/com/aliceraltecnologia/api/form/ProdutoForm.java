package br.com.aliceraltecnologia.api.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoForm {

	@NotEmpty
	private String name;

	@NotEmpty
	private String description;

	@NotNull
	@Min(value = 0)
	private Double price;

}
