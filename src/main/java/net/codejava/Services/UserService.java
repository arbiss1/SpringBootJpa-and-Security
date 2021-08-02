package net.codejava.Services;

import javax.transaction.Transactional;

import net.codejava.Repositories.UserRepository;
import net.codejava.Domains.User;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

	private UserRepository repo;
	
	public void save(User info) {
		repo.save(info);
	}
	
	public User get(String roles) {
		return repo.findByRoles(roles).get();
	}

	public List<User> listAll() {
		return repo.findAll();
	}

	
}
