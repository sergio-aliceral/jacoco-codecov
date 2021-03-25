package br.com.aliceraltecnologia.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;
import br.com.aliceraltecnologia.model.repository.ProdutoRepository;
import br.com.aliceraltecnologia.service.ProdutoService;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	private ProdutoRepository repository;

	public ProdutoServiceImpl(ProdutoRepository repository) {
		this.repository = repository;
	}

	@Override
	public Produto salva(Produto produto) {
		return repository.save(produto);
	}

	@Override
	public Optional<Produto> buscaPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public void exclui(Produto produto) {

		if (produto == null || produto.getId() == null)
			throw new IllegalArgumentException("Produto inválido");

		repository.delete(produto);
	}

	@Override
	public Produto altera(Produto produto) {

		if (produto == null || produto.getId() == null)
			throw new IllegalArgumentException("Produto inválido");

		return repository.save(produto);
	}

	@Override
	public List<Produto> lista() {
		return repository.findAll();
	}

	@Override
	public List<Produto> pesquisa(ProdutoFilter produtoFilter) {
		return repository.pesquisa(produtoFilter);
	}

}
