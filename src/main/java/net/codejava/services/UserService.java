package net.codejava.services;

import javax.transaction.Transactional;
import net.codejava.repository.UserRepository;
import net.codejava.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
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

    public User get(long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(long userId) {
        repo.deleteById(userId);
    }

    public User getUsername(String username){return repo.findByusername(username);}

    public boolean isUsernamePresent(User user) {
        return listAll().stream().anyMatch(username -> username.getUsername().equals(user.getUsername()));
    }

    public boolean isUserValid(User user ){
        return listAll().stream().anyMatch(p -> p.getPassword().equals(user.getPassword())
                && p.getUsername().equals(user.getUsername()));
    }
}



