package net.codejava.repository;

import java.util.Optional;
import net.codejava.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User,Long> {

	Optional <User> findByUsername (String username);
	
	Optional <User> findByRoles (String roles);

	User findByusername(String username);
}
