package net.codejava.Services;

import javax.transaction.Transactional;

import net.codejava.Repositories.UserRepository;
import net.codejava.Domains.User;
import org.springframework.stereotype.Service;

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
	
}
