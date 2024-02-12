package br.com.erudio.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.converters.NumberConverter;
import br.com.erudio.exceptions.UnsupportedMathOperationException;
import br.com.erudio.math.SimpleMath;

@RestController

public class MathController {
	
	private static final String template = "Hello, %s!";
	private final AtomicLong  counter = new AtomicLong();
		
	private SimpleMath math = new SimpleMath();
	
	@RequestMapping(value = "/sum/{numberOne}/{numberTwo}",
			method=RequestMethod.GET)
	
	public Double sum (
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception{
		
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo) ) {	
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		return math.sub(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}

	@RequestMapping(value = "sub/{numberOne}/{numberTwo}",
			method=RequestMethod.GET)
	
	public Double sub (
			
		@PathVariable(value = "numberOne") String numberOne,
		@PathVariable(value = "numberTwo") String numberTwo
		) throws Exception{
	
			if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
		
				throw new UnsupportedMathOperationException("Please set a numeric value");
			
			}
			
			return math.sub(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
			
			
		}
	

	@RequestMapping (value = "mult/{numberOne}/{numberTwo}",
			method=RequestMethod.GET)
	
	public Double mult(
			
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		
			if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) 
				{
				
				throw new UnsupportedOperationException("Please set a numeric value");
				
				}
			return math.mult(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
		
		
	}
	
	@RequestMapping ("div/{numberOne}/{numberTwo}")
	
	public Double div (
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		
		if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			
			throw new UnsupportedMathOperationException("Please set a numeric value");
			
		}
		
		return math.div(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
		
	}
	
	@RequestMapping("med/{numberOne}/{numberTwo}")
	
	public Double med(
			
			@PathVariable("numberOne")String numberOne,
			@PathVariable("numberTwo")String numberTwo
			) throws Exception {
		
			if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			
				throw new UnsupportedMathOperationException("Please set a numeric value");
			
			}
		
			return math.med(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
		
	}
	
	
	
	
	@RequestMapping("rqd/{numberOne}")
	
	public Double rqd (
			
			@PathVariable(value = "numberOne") String numberOne
			
			) throws Exception 	{
		
		if (!NumberConverter.isNumeric(numberOne)) {
			
			throw new UnsupportedMathOperationException("Please set a numeric value");
			
		}
		
		return math.rqd(NumberConverter.convertToDouble(numberOne));
		
	}
	
	@RequestMapping("elv/{numberOne}")
	
	public double elv (
			@PathVariable("numberOne") String numberOne
			) throws Exception {
			
			if (!NumberConverter.isNumeric(numberOne)) {
				
				throw new UnsupportedMathOperationException("Digite um valor numérico");
				
				}
				
				return	math.elv(NumberConverter.convertToDouble(numberOne));
					
		}
			
	
}
