package br.com.aliceraltecnologia.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProdutoRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	ProdutoRepository repository;

	@Test
	@DisplayName("Deve salvar um produto.")
	public void salva() {
		
		Produto produto = criaProdutoValido(null);
		
		Produto produtoSalvo = repository.save(produto);
		
		assertThat(produtoSalvo.getId()).isNotNull();
	}

	@Test
	@DisplayName("Deve obter um produto por id.")
	public void buscaPorId() {

		Produto produto = criaProdutoValido(null);
		produto = entityManager.persist(produto);

		Optional<Produto> produtoRetornado = repository.findById(produto.getId());

		assertThat(produtoRetornado.isPresent()).isTrue();
	}

	@Test
	@DisplayName("Deve excluir um produto.")
	public void exclui() {

		Produto produto = criaProdutoValido(null);
		produto = entityManager.persist(produto);
		Optional<Produto> produtoRetornado = repository.findById(produto.getId());
		
		repository.delete(produtoRetornado.get());
		
		Optional<Produto> produtoExcluido = repository.findById(produto.getId());
		assertThat(produtoExcluido.isPresent()).isFalse();
	}
	
	@Test
	@DisplayName("Deve listar os produtos.")
	public void lista() {

		Produto produto = criaProdutoValido(null);
		produto = entityManager.persist(produto);
		
		List<Produto> produtos = repository.findAll();
		
		assertThat(produtos.size()).isEqualTo(1);
		assertThat(produtos.get(0).getId()).isEqualTo(produto.getId());
		assertThat(produtos.get(0).getName()).isEqualTo(produto.getName());
	}

	@Test
	@DisplayName("Deve filtrar os produtos.")
	public void pesquisaProduto() {
		
		Produto bola = Produto.builder().name("Bola").description("Descrição da Bola").price(20.00).build();
		bola = entityManager.persist(bola);
				
		ProdutoFilter produtoFilter = new ProdutoFilter("bo", 0.50, 20.00);
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(1);
		assertThat(bola).isIn(produtos);
	}
	
	@Test
	@DisplayName("Deve retornar vazio.")
	public void pesquisa() {
		
		ProdutoFilter produtoFilter = new ProdutoFilter("bo", 0.50, 20.00);
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(0);
	}
	
	@Test
	@DisplayName("Deve filtrar os produtos pelo nome.")
	public void pesquisaProdutoPeloNome() {
		
		Produto bolaDeFutebol = Produto.builder().name("Bola de futebol").description("Descrição da Bola de futebol").price(20.00).build();
		bolaDeFutebol = entityManager.persist(bolaDeFutebol);
		Produto bolaDeGude = Produto.builder().name("Bola de Gude").description("Descrição da Bola de gude").price(0.50).build();
		bolaDeGude = entityManager.persist(bolaDeGude);		
		
		ProdutoFilter produtoFilter = new ProdutoFilter("bola");
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(2);
		assertThat(bolaDeFutebol).isIn(produtos);
		assertThat(bolaDeGude).isIn(produtos);
	}
	
	@Test
	@DisplayName("Deve filtrar os produtos pela descricao.")
	public void pesquisaProdutoPelaDescricao() {
		
		Produto bolaDeFutebol = Produto.builder().name("Futebol").description("Descrição da Bola de futebol").price(20.00).build();
		bolaDeFutebol = entityManager.persist(bolaDeFutebol);
		Produto bolaDeGude = Produto.builder().name("Gude").description("Descrição da Bola de gude").price(0.50).build();
		bolaDeGude = entityManager.persist(bolaDeGude);		
		
		ProdutoFilter produtoFilter = new ProdutoFilter("bola");
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(2);
		assertThat(bolaDeFutebol).isIn(produtos);
		assertThat(bolaDeGude).isIn(produtos);
	}
	
	@Test
	@DisplayName("Deve filtrar os produtos pelo menor preço.")
	public void pesquisaProdutoPeloMenorPreco() {
		
		Produto bolaDeFutebol = Produto.builder().name("Futebol").description("Futebol").price(20.00).build();
		bolaDeFutebol = entityManager.persist(bolaDeFutebol);
		Produto bolaDeGude = Produto.builder().name("Gude").description("Gude").price(0.50).build();
		bolaDeGude = entityManager.persist(bolaDeGude);
		
		ProdutoFilter produtoFilter = new ProdutoFilter(0.51);
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(1);
		assertThat(bolaDeFutebol).isIn(produtos);
		assertThat(bolaDeGude).isNotIn(produtos);
	}
	
	@Test
	@DisplayName("Deve filtrar os produtos pelo maior preço.")
	public void pesquisaProdutoPeloMaiorPreco() {
		
		Produto bolaDeFutebol = Produto.builder().name("Futebol").description("Futebol").price(20.00).build();
		bolaDeFutebol = entityManager.persist(bolaDeFutebol);
		Produto bolaDeGude = Produto.builder().name("Gude").description("Gude").price(0.50).build();
		bolaDeGude = entityManager.persist(bolaDeGude);
		
		ProdutoFilter produtoFilter = new ProdutoFilter();
		produtoFilter.setMax_price(1.00);
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(1);
		assertThat(bolaDeGude).isIn(produtos);
		assertThat(bolaDeFutebol).isNotIn(produtos);
	}
	
	@Test
	@DisplayName("Deve filtrar os produtos pelo preço.")
	public void pesquisaProdutoPeloPreco() {
		
		Produto bolaDeFutebol = Produto.builder().name("Futebol").description("Futebol").price(20.00).build();
		bolaDeFutebol = entityManager.persist(bolaDeFutebol);
		Produto bolaDeGude = Produto.builder().name("Gude").description("Gude").price(0.50).build();
		bolaDeGude = entityManager.persist(bolaDeGude);
		
		ProdutoFilter produtoFilter = new ProdutoFilter(0.50, 20.00);
		List<Produto> produtos = repository.pesquisa(produtoFilter);
		
		assertThat(produtos.size()).isEqualTo(2);
		assertThat(bolaDeGude).isIn(produtos);
		assertThat(bolaDeFutebol).isIn(produtos);
	}
	
	private Produto criaProdutoValido(Long id) {
		return Produto.builder().id(id).name("Produto A").description("Descrição do Produto A").price(100.11).build();
	}
}
