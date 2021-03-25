package br.com.aliceraltecnologia.api.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.aliceraltecnologia.api.form.ProdutoForm;
import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;
import br.com.aliceraltecnologia.service.ProdutoService;

@WebMvcTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProdutoControllerTest {

	static String PRODUTO_API = "/products";

	@Autowired
	MockMvc mvc;

	@MockBean
	ProdutoService service;
	
	@Test
	@DisplayName("Deve criar um produto com sucesso.")
	public void criaProduto() throws Exception {
		
		Long id = 1L;
		ProdutoForm form = criaProdutoFormValido();
		Produto produtoSalvo =  criaProdutoValido(id);
		BDDMockito.given(service.salva(Mockito.any(Produto.class))).willReturn(produtoSalvo);
		
		String json = new ObjectMapper().writeValueAsString(form);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(PRODUTO_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("name").value(form.getName()))
			.andExpect(jsonPath("description").value(form.getDescription()))
			.andExpect(jsonPath("price").value(form.getPrice()));
	}

	@Test
	@DisplayName("Deve lançar erro quando não houver dados suficientes para a criação do produto.")
	public void criaProdutoComInconsistencia() throws Exception {

		String json = new ObjectMapper().writeValueAsString(criaFormVazio());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(PRODUTO_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("message").value("Campo(s) inválido(s)"));
	}

	@Test
	@DisplayName("Deve lançar erro quando o preço do produto for negativo.")
	public void criaProdutoComPrecoInvalido() throws Exception {

		ProdutoForm form = criaProdutoFormComPrecoNegativo();
		
		String json = new ObjectMapper().writeValueAsString(form);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(PRODUTO_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("message").value("Campo(s) inválido(s)"));
	}
	
	@Test
	@DisplayName("Deve obter informações de um produto.")
	public void buscaProduto() throws Exception {
		
		Long id = 1L;		
		Produto produtoRetornado = criaProdutoValido(id);
		BDDMockito.given(service.buscaPorId(id)).willReturn(Optional.of(produtoRetornado));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(PRODUTO_API.concat("/" +  id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("name").value(produtoRetornado.getName()))
			.andExpect(jsonPath("description").value(produtoRetornado.getDescription()))
			.andExpect(jsonPath("price").value(produtoRetornado.getPrice()));
	}

	@Test
	@DisplayName("Deve retornar resource not found quando o produto não existir.")
	public void buscaProdutoInexistente() throws Exception {
		
		Long id = 1L;
		BDDMockito.given(service.buscaPorId(Mockito.anyLong())).willReturn(Optional.empty());		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(PRODUTO_API.concat("/" +  id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve excluir um produto.")
	public void exclui() throws Exception {
		
		Long id = 1L;
		Produto produto = criaProdutoValido(id);
		BDDMockito.given(service.buscaPorId(Mockito.anyLong())).willReturn(Optional.of(produto));
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(PRODUTO_API.concat("/" +  id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando o produto não encontrar o produto para excluir.")
	public void excluiProdutoInexistente() throws Exception {
		
		Long id = 1L;
		BDDMockito.given(service.buscaPorId(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(PRODUTO_API.concat("/" +  id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve alterar um produto com sucesso.")
	public void alteraProduto() throws Exception {

		Long id = 1L;
		ProdutoForm form = ProdutoForm.builder().name("Produto A Alterado").description("Descrição do Produto A Alterado").price(100.02).build();
		Produto produto = criaProdutoValido(id);
		BDDMockito.given(service.buscaPorId(Mockito.anyLong())).willReturn(Optional.of(produto));
		Produto produtoAtualizado = Produto.builder().id(id).name("Produto A Alterado").description("Descrição do Produto A Alterado").price(100.02).build();
		BDDMockito.given(service.altera(produto)).willReturn(produtoAtualizado);
		
		String json = new ObjectMapper().writeValueAsString(form);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(PRODUTO_API.concat("/" +  id))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("name").value(form.getName()))
			.andExpect(jsonPath("description").value(form.getDescription()))
			.andExpect(jsonPath("price").value(form.getPrice()));
	}
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar atualizar um produto inexistente.")
	public void alteraProdutoInexistente() throws Exception {
		
		ProdutoForm form = ProdutoForm.builder().name("Produto A Alterado").description("Descrição do Produto A Alterado").price(100.03).build();
		BDDMockito.given(service.buscaPorId(Mockito.anyLong())).willReturn(Optional.empty());
		
		String json = new ObjectMapper().writeValueAsString(form);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(PRODUTO_API.concat("/" +  1L))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve lançar erro quando não houver dados suficientes para alterar o produto.")
	public void alteraProdutoComInconsistencia() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(criaFormVazio());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(PRODUTO_API.concat("/" +  1L))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("message").value("Campo(s) inválido(s)"));;
	}
	
	@Test
	@DisplayName("Deve lançar erro quando o preço for negativo.")
	public void alteraProdutoComPrecoNegativo() throws Exception {
		
		ProdutoForm form = criaProdutoFormComPrecoNegativo();
		
		String json = new ObjectMapper().writeValueAsString(form);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(PRODUTO_API.concat("/" +  1L))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("status_code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("message").value("Campo(s) inválido(s)"));
	}

	@Test
	@DisplayName("Deve listar os produtos.")
	public void listaProdutos() throws Exception {
		
		Produto bola = Produto.builder().id(1L).name("Bola").description("Bola").price(5.50).build();
		Produto lapis = Produto.builder().id(2L).name("Lápis").description("Lápis").price(1.50).build();
		
		BDDMockito.given(service.lista()).willReturn(Arrays.asList(bola, lapis));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(PRODUTO_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].name", is("Bola")))
			.andExpect(jsonPath("$[1].name", is("Lápis")));
	}
	
	@Test
	@DisplayName("Deve retornar uma lista vázia.")
	public void lista() throws Exception {
		
		BDDMockito.given(service.lista()).willReturn(Arrays.asList());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(PRODUTO_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	@DisplayName("Deve filtrar os produtos.")
	public void pesquisaProdutos() throws Exception {
		
		Long id = 1L;
		Produto produto = criaProdutoValido(id);
				
		BDDMockito.given(service
				.pesquisa(Mockito.any(ProdutoFilter.class)))
				.willReturn(Arrays.asList(produto));
		
		String queryParameters = String.format("?q=%s&min_price=%s&max_price=%s", produto.getName(), produto.getPrice(), produto.getPrice());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(PRODUTO_API.concat("/search" + queryParameters))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

        mvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
	}
	
	@Test
	@DisplayName("Deve retornar vazio.")
	public void pesquisa() throws Exception {
		
		Long id = 1L;
		Produto produto = criaProdutoValido(id);
				
		BDDMockito.given(service
				.pesquisa(Mockito.any(ProdutoFilter.class)))
				.willReturn(Arrays.asList());
		
		String queryParameters = String.format("?q=%s&min_price=%s&max_price=%s", produto.getName(), produto.getPrice(), produto.getPrice());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(PRODUTO_API.concat("/search" + queryParameters))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

        mvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
	}
	
	private Produto criaProdutoValido(Long id) {
		return Produto.builder().id(id).name("Produto A").description("Descrição do Produto A").price(100.01).build();
	}
	
	private ProdutoForm criaProdutoFormValido() {
		return ProdutoForm.builder().name("Produto A").description("Descrição do Produto A").price(100.01).build();
	}
	
	private ProdutoForm criaProdutoFormComPrecoNegativo() {
		return ProdutoForm.builder().name("Produto A").description("Descrição do Produto A").price(-30.01).build();
	}
	
	private ProdutoForm criaFormVazio() {
		return new ProdutoForm();
	}
	
}
