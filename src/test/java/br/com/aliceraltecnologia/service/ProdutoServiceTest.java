package br.com.aliceraltecnologia.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;
import br.com.aliceraltecnologia.model.repository.ProdutoRepository;
import br.com.aliceraltecnologia.service.impl.ProdutoServiceImpl;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProdutoServiceTest {

	ProdutoService service;

	@MockBean
	ProdutoRepository repository;

	@BeforeEach
	public void setUp() {
		this.service = new ProdutoServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve salvar um produto.")
	public void salva() {

		Produto produto = criaProdutoValido();
		Produto produtoSalvo = criaProdutoValido(1L);
		Mockito.when(repository.save(produto)).thenReturn(produtoSalvo);

		Produto produtoRetornado = service.salva(produto);

		assertThat(produtoRetornado.getId()).isNotNull();
		assertThat(produtoRetornado.getName()).isEqualTo("Produto A");
		assertThat(produtoRetornado.getDescription()).isEqualTo("Descrição do Produto A");
		assertThat(produtoRetornado.getPrice()).isEqualTo(101.01);
	}

	@Test
	@DisplayName("Deve obeter um produto por id.")
	public void buscaPorId() {

		Long id = 1L;
		Produto produtoPesquisado = criaProdutoValido(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(produtoPesquisado));

		Optional<Produto> produto = service.buscaPorId(id);

		assertThat(produto.isPresent()).isTrue();
		assertThat(produto.get().getId()).isEqualTo(id);
		assertThat(produto.get().getName()).isEqualTo("Produto A");
		assertThat(produto.get().getDescription()).isEqualTo("Descrição do Produto A");
		assertThat(produto.get().getPrice()).isEqualTo(101.01);
	}

	@Test
	@DisplayName("Deve retornar vazio quando não encontrar o id do produto.")
	public void buscaProdutoComIdInexistente() {

		Long id = 1L;
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		Optional<Produto> produto = service.buscaPorId(id);

		assertThat(produto.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Deve excluir um produto.")
	public void excluiProduto() {

		Long id = 1L;
		Produto produto = criaProdutoValido(id);

		Assertions.assertDoesNotThrow(() -> service.exclui(produto));

		Mockito.verify(repository, Mockito.times(1)).delete(produto);
	}

	@Test
	@DisplayName("Deve retornar erro ao tentar excluir um produto inexistente.")
	public void excluiProdutoInexistente() {

		Produto produto = new Produto();

		Assertions.assertThrows(IllegalArgumentException.class, () -> service.exclui(produto));

		Mockito.verify(repository, Mockito.never()).delete(produto);
	}

	@Test
	@DisplayName("Deve alterar um produto.")
	public void alteraProduto() {

		Long id = 1L;
		Produto produto = Produto.builder().id(id).build();

		Produto produtoAlterado = criaProdutoValido(id);
		Mockito.when(repository.save(produto)).thenReturn(produtoAlterado);

		Produto produtoRetornado = service.altera(produto);

		assertThat(produtoRetornado.getId()).isEqualTo(produtoAlterado.getId());
		assertThat(produtoRetornado.getName()).isEqualTo(produtoAlterado.getName());
		assertThat(produtoRetornado.getDescription()).isEqualTo(produtoAlterado.getDescription());
		assertThat(produtoRetornado.getPrice()).isEqualTo(produtoAlterado.getPrice());
	}

	@Test
	@DisplayName("Deve retornar erro ao tentar alterar um produto inexistente.")
	public void alteraProdutoInexistente() {

		Produto produto = new Produto();

		Assertions.assertThrows(IllegalArgumentException.class, () -> service.altera(produto));

		Mockito.verify(repository, Mockito.never()).save(produto);
	}

	@Test
	@DisplayName("Deve listar os produtos.")
	public void listaProdutos() {

		Produto bola = Produto.builder().id(1L).name("Bola").description("Bola").price(6.50).build();
		Produto lapis = Produto.builder().id(2L).name("Lápis").description("Lápis").price(0.50).build();

		Mockito.when(repository.findAll()).thenReturn(Arrays.asList(bola, lapis));

		List<Produto> produtos = service.lista();

		assertThat(produtos.size()).isEqualTo(2);
		assertThat(produtos.get(0).getName()).isEqualTo(bola.getName());
		assertThat(produtos.get(1).getName()).isEqualTo(lapis.getName());
	}

	@Test
	@DisplayName("Deve retornar vazio.")
	public void lista() {

		Mockito.when(repository.findAll()).thenReturn(Arrays.asList());

		List<Produto> produtos = service.lista();

		assertThat(produtos.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Deve filtrar os produtos.")
	public void pesquisaProdutos() throws Exception {

		Produto bola = Produto.builder().id(1L).name("Bola").description("Bola").price(6.50).build();
		Produto lapis = Produto.builder().id(2L).name("Lápis").description("Lápis").price(0.50).build();

		Mockito.when(repository.pesquisa(Mockito.any(ProdutoFilter.class))).thenReturn(Arrays.asList(bola));

		ProdutoFilter filtro = new ProdutoFilter("Bola", 0.50, 1.00);
		List<Produto> produtosFiltrados = service.pesquisa(filtro);

		assertThat(bola).isIn(produtosFiltrados);
		assertThat(lapis).isNotIn(produtosFiltrados);
	}

	@Test
	@DisplayName("Deve retornar vazio.")
	public void pesquisa() throws Exception {

		Mockito.when(repository.pesquisa(Mockito.any(ProdutoFilter.class))).thenReturn(Arrays.asList());

		ProdutoFilter filtro = new ProdutoFilter("Bola", 0.50, 1.00);
		List<Produto> produtosFiltrados = service.pesquisa(filtro);

		assertThat(produtosFiltrados).isEmpty();
	}

	private Produto criaProdutoValido() {
		return Produto.builder().name("Produto A").description("Descrição do Produto A").price(101.01).build();
	}

	private Produto criaProdutoValido(Long id) {
		Produto produtoValido = criaProdutoValido();
		produtoValido.setId(id);
		return produtoValido;
	}
}
