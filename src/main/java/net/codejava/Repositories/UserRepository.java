package net.codejava.Repositories;

import java.util.List;
import java.util.Optional;

import net.codejava.Domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User,Long> {

	Optional <User> findByUsername (String username);
	
	Optional <User> findByRoles (String roles);

	List<User> findByuserId (int userId);


}
