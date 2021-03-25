package br.com.aliceraltecnologia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	public static final String PRODUTOS = "Produtos";
	
	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.aliceraltecnologia.api.resource"))
				.paths(PathSelectors.any())
				.build()
				.tags(new Tag(PRODUTOS, "Catálogo de Produtos"))
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("API Hello Swagger")
				.description("API Hello Swagger com Spring.")
				.version("1.0.0")
				.contact(contact())
				.build();
	}

	private Contact contact() {
		return new Contact(
				"Sérgio Aliceral", 
				"https://www.linkedin.com/in/sergioaliceral", 
				"sergio@aliceraltecnologia.com.br"
			);
	}

}
