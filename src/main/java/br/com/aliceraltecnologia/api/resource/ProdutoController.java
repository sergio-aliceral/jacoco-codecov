package br.com.aliceraltecnologia.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.aliceraltecnologia.api.dto.FiltroProdutoDTO;
import br.com.aliceraltecnologia.api.dto.ProdutoDTO;
import br.com.aliceraltecnologia.api.form.ProdutoForm;
import br.com.aliceraltecnologia.config.SwaggerConfig;
import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;
import br.com.aliceraltecnologia.service.ProdutoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
@Api(tags = { SwaggerConfig.PRODUTOS })
public class ProdutoController {

	private final ProdutoService service;

	private final ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Cria um produto")
	public ProdutoDTO salva(@RequestBody @Valid ProdutoForm form) {
		Produto produto = modelMapper.map(form, Produto.class);
		produto = service.salva(produto);
		return modelMapper.map(produto, ProdutoDTO.class);
	}

	@GetMapping("{id}")
	@ApiOperation("Busca o produto pelo id")
	public ProdutoDTO buscaPorId(@PathVariable Long id) {
		return service
				.buscaPorId(id)
				.map(produto -> modelMapper.map(produto, ProdutoDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("{id}")
	@ApiOperation("Exclui o produto")
	public void exclui(@PathVariable Long id) {
		Produto produto = service.buscaPorId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.exclui(produto);
	}
	
	@PutMapping("{id}")
	@ApiOperation("Altera um produto")
	public ProdutoDTO altera(@PathVariable Long id, @RequestBody @Valid ProdutoForm form) {
		return service.buscaPorId(id).map( produto -> {
			
			produto.setName(form.getName());
			produto.setDescription(form.getDescription());
			produto.setPrice(form.getPrice());
			produto = service.altera(produto);
			return modelMapper.map(produto, ProdutoDTO.class);
		
		}) .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
	}
	
	@GetMapping
	@ApiOperation("Lista os produtos")
	public List<ProdutoDTO> lista() {
		return service.lista().stream()
				.map(produto -> modelMapper.map(produto, ProdutoDTO.class))
				.collect(Collectors.toList());
	}
	
	@GetMapping("search")
	@ApiOperation("Pesquisa os produtos pelo filtro")
	public List<ProdutoDTO> pesquisa(FiltroProdutoDTO dto) {
		ProdutoFilter produtoFilter = modelMapper.map(dto, ProdutoFilter.class);
		return service.pesquisa(produtoFilter).stream()
			.map( produto -> modelMapper.map(produto, ProdutoDTO.class))
			.collect(Collectors.toList());
	}
}
