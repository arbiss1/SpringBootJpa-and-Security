
package net.codejava.Services;

import java.util.List;

import net.codejava.Domains.Orders;
import net.codejava.Repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrdersRepository repo;
	
	public List<Orders> listAll() {
		return repo.findAll();
	}
	public List<Orders> listAllByUser(Long userId) {
		return repo.findByUserId(userId);
	}
	public void save(Orders product) {
		repo.save(product);
	}
	
	public Orders get(long id) {
		return repo.findById(id).get();
	}

	public void delete(long id) {
		repo.deleteById(id);
	}

}
