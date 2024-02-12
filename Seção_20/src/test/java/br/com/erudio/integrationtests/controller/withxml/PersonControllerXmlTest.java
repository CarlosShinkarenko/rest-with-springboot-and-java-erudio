package br.com.erudio.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVo;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelPerson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	
	private static XmlMapper objectMapper;
	
	private static PersonVO person;
	
	
	@BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new PersonVO();
		
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
	
		AccountCredentialsVo user = new AccountCredentialsVo("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.accept(TestConfigs.CONTENT_TYPE_JSON)
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
				.setBasePath("api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(person)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId()>0);
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
	}

	@Test
	@Order(2)
	public void disablePersonById() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
	}
	
	@Test
	@Order(3)
	public void findById() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertFalse(persistedPerson.getEnabled());
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
		/*assertTrue(persistedPerson.getEnabled());
		
		assertEquals("Ayrton", persistedPerson.getFirstName());
		assertEquals("Senna", persistedPerson.getLastName());
		assertEquals("São Paulo", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());*/
		
	}
	
	@Test
	@Order(4)
	public void update() throws JsonMappingException, JsonProcessingException {
		
		person.setFirstName("Richards");
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(person)
				.when()
					.put()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Richards", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
	}
	
	@Test
	@Order(5)
	public void delete () {
		
		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
		
	}

	@Test
	@Order(6)
	public void findAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
			.queryParams("page", 3, "size", 10, "direction", "asc")
			.when()
				.get()
			.then()
				.statusCode(200)
					.extract()
						.body()
							.asString();
			
		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
		
		var people = wrapper.getContent();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		assertTrue(foundPersonOne.getEnabled());
		
		assertEquals(679, foundPersonOne.getId());
		
		assertEquals("Alic", foundPersonOne.getFirstName());
		assertEquals("Terbrug", foundPersonOne.getLastName());
		assertEquals("3 Eagle Crest Court", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
		
		PersonVO foundPersonFive = people.get(4);
		
		assertNotNull(foundPersonFive);
		
		assertNotNull(foundPersonFive.getId());
		assertNotNull(foundPersonFive.getFirstName());
		assertNotNull(foundPersonFive.getLastName());
		assertNotNull(foundPersonFive.getAddress());
		assertNotNull(foundPersonFive.getGender());
		assertFalse(foundPersonFive.getEnabled());
		
		assertEquals(716, foundPersonFive.getId());
		
		assertEquals("Alla", foundPersonFive.getFirstName());
		assertEquals("Astall", foundPersonFive.getLastName());
		assertEquals("72525 Emmet Alley", foundPersonFive.getAddress());
		assertEquals("Female", foundPersonFive.getGender());
		
	}
	
	@Test
	@Order(7)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("firstName", "ayr")
				.queryParams("page", 0, "size", 10, "direction", "asc")
				.when()
					.get("findPersonByName/{firstName}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
	
		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
		var people = wrapper.getContent();
	
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());

		assertTrue(foundPersonOne.getEnabled());
		
		assertEquals(1, foundPersonOne.getId());
		
		assertEquals("Ayrton", foundPersonOne.getFirstName());
		assertEquals("Senna", foundPersonOne.getLastName());
		assertEquals("São Paulo", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
	}
	
	@Test
	@Order(8)
	public void findAllWthoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("api/person/v1")
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
	
	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
			.queryParams("page", 3, "size", 10, "direction", "asc")
			.when()
				.get()
			.then()
				.statusCode(200)
					.extract()
						.body()
							.asString();
			
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/679</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/416</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/848</href></links>"));
		
		assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=0&amp;size=10&amp;sort=firstName,asc</href></links>"));
		assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=2&amp;size=10&amp;sort=firstName,asc</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1?page=3&amp;size=10&amp;direction=asc</href></links>"));
		assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=4&amp;size=10&amp;sort=firstName,asc</href></links>"));
		assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=100&amp;size=10&amp;sort=firstName,asc</href></links>"));

		assertTrue(content.contains("<page><size>10</size><totalElements>1007</totalElements><totalPages>101</totalPages><number>3</number></page>"));

	}
	
	public void mockPerson() {
		
		person.setFirstName("Richard");
		person.setLastName("Stallman");
		person.setAddress("New York City, New York, US");
		person.setGender("Male");
		person.setEnabled(true);
		
	}
	
}
