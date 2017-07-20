package com.revature.rideshare.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RideshareErrorController extends AbstractErrorController {
	
	public RideshareErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@Override
	public String getErrorPath() {
		return "/#/error";
	}

	@RequestMapping("/error")
	public void handleError(HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		String destination = getErrorPath() + "?status=" + status.value();
		try {
			response.sendRedirect(destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public ResponseEntity<String> handleError(HttpServletRequest request) {
//		HttpStatus status = getStatus(request);
//		String destination = getErrorPath() + "?status=" + status.value();
//	}
	
}
