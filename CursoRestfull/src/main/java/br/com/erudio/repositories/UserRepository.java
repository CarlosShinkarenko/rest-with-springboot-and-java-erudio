package br.com.erudio.repositories;

import java.util.logging.Logger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.erudio.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	Logger logger = Logger.getLogger(UserRepository.class.getName());
	
	@Query("SELECT u FROM User u where u.userName = :userName")
	User findByUsername(@Param("userName") String userName);

}
