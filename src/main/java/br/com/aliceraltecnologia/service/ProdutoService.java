package br.com.aliceraltecnologia.service;

import java.util.List;
import java.util.Optional;

import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;

public interface ProdutoService {

	Produto salva(Produto produto);

	Optional<Produto> buscaPorId(Long id);

	void exclui(Produto produto);

	Produto altera(Produto produto);

	List<Produto> lista();

	List<Produto> pesquisa(ProdutoFilter dto);

}
