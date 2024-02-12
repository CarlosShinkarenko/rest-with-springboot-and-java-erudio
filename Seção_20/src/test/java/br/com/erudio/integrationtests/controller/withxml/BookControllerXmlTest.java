package br.com.erudio.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVo;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {
	
	private static RequestSpecification specification;
	private static XmlMapper objectMapper;

	private static BookVO book;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
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
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
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
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/book/v1")
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
        
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getPrice());
        assertTrue(persistedBook.getId() > 0);
        assertEquals("Call of Cthulhu", persistedBook.getTitle());
        assertEquals("H. P. Lovecraft", persistedBook.getAuthor());
        assertEquals(13.00, persistedBook.getPrice());
        
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {

		book.setAuthor("Howard Phillips Lovecraft");
		
		System.out.println("ID TESTE  "+book.getId());		
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
        
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getPrice());
        assertEquals(persistedBook.getId(), book.getId());
        assertEquals("Call of Cthulhu", persistedBook.getTitle());
        assertEquals("Howard Phillips Lovecraft", persistedBook.getAuthor());
        assertEquals(13.00, persistedBook.getPrice());
        
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockBook();
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO foundBook = objectMapper.readValue(content, BookVO.class);
        
        assertNotNull(foundBook.getId());
        assertNotNull(foundBook.getTitle());
        assertNotNull(foundBook.getAuthor());
        assertNotNull(foundBook.getPrice());
        assertEquals(foundBook.getId(), book.getId());
		
        assertEquals("Call of Cthulhu", foundBook.getTitle());
        assertEquals("Howard Phillips Lovecraft", foundBook.getAuthor());
        assertEquals(13.00, foundBook.getPrice());
		
	}
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(200);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
            	.queryParams("page", 0 , "limit", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
		List<BookVO> books = wrapper.getContent();
		
		BookVO foundBookOne = books.get(0);
	    
	    assertNotNull(foundBookOne.getId());
	    assertNotNull(foundBookOne.getTitle());
	    assertNotNull(foundBookOne.getAuthor());
	    assertNotNull(foundBookOne.getPrice());
	    assertTrue(foundBookOne.getId() > 0);
	    assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());
	    assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
	    assertEquals(54.00, foundBookOne.getPrice());
	    
	    BookVO foundBookFive = books.get(4);
	    
	    assertNotNull(foundBookFive.getId());
	    assertNotNull(foundBookFive.getTitle());
	    assertNotNull(foundBookFive.getAuthor());
	    assertNotNull(foundBookFive.getPrice());
	    assertTrue(foundBookFive.getId() > 0);
	    assertEquals("Domain Driven Design", foundBookFive.getTitle());
	    assertEquals("Eric Evans", foundBookFive.getAuthor());
	    assertEquals(92.0, foundBookFive.getPrice());
	}

	
	@Test
	@Order(6)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/book/v1")
			.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.when()
				.get()
			.then()
				.statusCode(403);
	}
	
	public void mockBook() {
	
		book.setAuthor("H. P. Lovecraft");
		book.setLaunch_date(new Date());
		book.setPrice(13.00);
		book.setTitle("Call of Cthulhu");
		
	}
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 3, "size", 2, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/7</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/15</href></links>"));
		
		assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=0&amp;size=2&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=2&amp;size=2&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1?page=3&amp;size=2&amp;direction=asc</href></links>"));
		assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=4&amp;size=2&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=7&amp;size=2&amp;sort=title,asc</href></links>"));
		
		assertTrue(content.contains("<page><size>2</size><totalElements>15</totalElements><totalPages>8</totalPages><number>3</number></page>"));
		
	}
	
}