package br.com.erudio.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.services.BookServices;
import br.com.erudio.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping ("/api/book/v1")
@Tag(name = "Book", description = "Endpoints for managing books")
public class BookController {

	@Autowired
	BookServices service;
	
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	
	@Operation(summary = "Finds all books", description = "Finds all books",
		tags = {"Book"},
		responses = {
				@ApiResponse(description = "Sucess", responseCode = "200",
						content = {
								@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = BookVO.class))
								)
						}),
				@ApiResponse(description = "BadRequest", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unautorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "NotFound", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
		}
	)
	
	public ResponseEntity<PagedModel<EntityModel<BookVO>>> findAll(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "12") Integer size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction
			) {
		
		System.out.println("Cntrou no book controller");
		
		var sortDirection = "desc".equalsIgnoreCase(direction)? Direction.DESC : Direction.ASC;
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));
		
		System.out.println("controller 2");
		
		return ResponseEntity.ok(service.findAll(pageable));
		
	}
	
	@GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	
	@Operation(summary = "Finds a book", description = "Finds a book",
		tags = {"Book"},
		responses = {
				@ApiResponse(description = "Sucess", responseCode = "200",
						content = {
								@Content(schema = @Schema(implementation = BookVO.class)
								)
						}),
				@ApiResponse(description = "BadRequest", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unautorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "NotFound", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
		}
	)
	
	public BookVO findById(@PathVariable(value = "id") Long id) throws Exception {
		
		
		return service.findById(id);
		
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
			produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	
	@Operation(summary = "Creates a book", description = "Finds a book",
		tags = {"Book"},
		responses = {
				@ApiResponse(description = "Sucess", responseCode = "200",
						content = {
								@Content(schema = @Schema(implementation = BookVO.class)
								)
						}),
				@ApiResponse(description = "BadRequest", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unautorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
		}
	)
	
	public BookVO create (@RequestBody BookVO book) throws Exception {
		
		return service.create(book);
		
	}
	
	@PutMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
			produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	
	@Operation(summary = "Updates a book", description = "Finds a book",
		tags = {"Book"},
		responses = {
				@ApiResponse(description = "Sucess", responseCode = "200",
						content = {
								@Content(schema = @Schema(implementation = BookVO.class)
								)
						}),
				@ApiResponse(description = "BadRequest", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unautorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "NotFound", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
		}
	)
	
	public BookVO update (@RequestBody BookVO book) throws Exception {
		
		return service.update(book);
		
	}
	
	@DeleteMapping(value = "/{id}")
	
	@Operation(summary = "Deletes a book", description = "Finds a book",
		tags = {"Book"},
		responses = {
				@ApiResponse(description = "Sucess", responseCode = "200",
						content = {
								@Content(schema = @Schema(implementation = BookVO.class)
								)
						}),
				@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
				@ApiResponse(description = "BadRequest", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unautorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "NotFound", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
		}
	)
	
	public void delete (@PathVariable("id") Long id) {
		
		service.delete(id);
		
	}
	
}
