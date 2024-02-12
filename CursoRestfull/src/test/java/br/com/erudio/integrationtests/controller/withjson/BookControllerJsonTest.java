package br.com.erudio.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVo;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.wrappers.WrapperBookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	
	private static ObjectMapper objectMapper;
	
	private static BookVO book;
	
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookVO();
		
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
	
		AccountCredentialsVo user = new AccountCredentialsVo("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " +accessToken)
				.setBasePath("api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		
		mockBook();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(book)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunch_date());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());
		
		assertTrue(persistedBook.getId()>0);
		
		assertEquals("H. P. Lovecraft", persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunch_date());
		assertEquals(13.00, persistedBook.getPrice());
		assertEquals("Call of Cthulhu", persistedBook.getTitle());
		
	}

	@Test
	@Order(2)
	public void findById() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", book.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunch_date());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());
		
		assertEquals(book.getId(), persistedBook.getId());
		
		assertEquals("H. P. Lovecraft", persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunch_date());
		assertEquals(13.00, persistedBook.getPrice());
		assertEquals("Call of Cthulhu", persistedBook.getTitle());
		
	}
	
	@Test
	@Order(3)
	public void update() throws JsonMappingException, JsonProcessingException {
		
		book.setAuthor("Howard Phillips Lovecraft");
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(book)
				.when()
					.put()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunch_date());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertEquals(book.getId(), persistedBook.getId());
		
		assertEquals("Howard Phillips Lovecraft", persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunch_date());
		assertEquals(13.00, persistedBook.getPrice());
		assertEquals("Call of Cthulhu", persistedBook.getTitle());
		
	}
	
	@Test
	@Order(4)
	public void delete () {
		
		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(200);
		
	}

	@Test
	@Order(5)
	public void findAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
			.queryParams("page", 0, "size", 12, "direction", "asc")
			.when()
				.get()
			.then()
				.statusCode(200)
					.extract()
						.body()
							.asString();
			
		WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);
		
		var book = wrapper.getEmbedded().getBooks();
		
		BookVO foundBookOne = book.get(0);
		
		assertNotNull(foundBookOne);
		
		assertNotNull(foundBookOne.getId());
		assertNotNull(foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getLaunch_date());
		assertNotNull(foundBookOne.getPrice());
		assertNotNull(foundBookOne.getTitle());
		
		assertEquals(12, foundBookOne.getId());
		
		assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getLaunch_date());
		assertEquals(54.0, foundBookOne.getPrice());
		assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());
		
		BookVO foundBookFive = book.get(4);
		
		assertNotNull(foundBookFive);
		
		assertNotNull(foundBookFive.getId());
		assertNotNull(foundBookFive.getAuthor());
		assertNotNull(foundBookFive.getLaunch_date());
		assertNotNull(foundBookFive.getPrice());
		assertNotNull(foundBookFive.getTitle());
		
		assertEquals(8, foundBookFive.getId());
		
		assertEquals("Eric Evans", foundBookFive.getAuthor());
		assertNotNull(foundBookFive.getLaunch_date());
		assertEquals(92.0, foundBookFive.getPrice());
		assertEquals("Domain Driven Design", foundBookFive.getTitle());
		
	}
	
	@Test
	@Order(6)
	public void findAllWthoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given().spec(specificationWithoutToken)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				.get()
				.then()
				.statusCode(403);
		
	}	
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
			.queryParams("page", 3, "size", 2, "direction", "asc")
			.when()
				.get()
			.then()
				.statusCode(200)
					.extract()
						.body()
							.asString();
		
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/7\"}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/15\"}}"));
		
		assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=2&sort=title,asc\"}"));
		assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=2&size=2&sort=title,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1?page=3&size=2&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=4&size=2&sort=title,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=7&size=2&sort=title,asc\"}}"));
		
		assertTrue(content.contains("\"page\":{\"size\":2,\"totalElements\":15,\"totalPages\":8,\"number\":3}}"));
		
	}
	
	public void mockBook() {
		
		book.setAuthor("H. P. Lovecraft");
		book.setLaunch_date(new Date());
		book.setPrice(13.00);
		book.setTitle("Call of Cthulhu");
		
	}
	
}
