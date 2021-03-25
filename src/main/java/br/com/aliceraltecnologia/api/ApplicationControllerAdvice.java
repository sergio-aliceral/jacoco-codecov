package br.com.aliceraltecnologia.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.aliceraltecnologia.api.dto.ErroDTO;

@RestControllerAdvice
public class ApplicationControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErroDTO handleValidationExceptions(MethodArgumentNotValidException exception) {
		int badRequest = HttpStatus.BAD_REQUEST.value();
		return new ErroDTO(String.valueOf(badRequest), "Campo(s) inválido(s)");
	}

}
