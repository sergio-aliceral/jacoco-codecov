package br.com.aliceraltecnologia.model.repository;

import java.util.List;

import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;

public interface ProdutoRepositoryQuery {

	public List<Produto> pesquisa(ProdutoFilter filtro);

}
