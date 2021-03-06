package backend.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.models.User;
import backend.repositories.UserRepository;

@Service
public class UsersService {
	
	@Autowired
	private UserRepository usersRepo;
	
	@PostConstruct
	@Transactional
	public void createAdminUser() {		
		User userAdmin=usersRepo.findByLogin("admin");
		if(userAdmin==null){
			register("admin", "admin@mail.com", "qwerty");
		}
	}
	
	
	@Transactional(readOnly = false)
	public void register(String login, String email, String pass) {
		String passHash = new BCryptPasswordEncoder().encode(pass);
		
		User u = new User(login, email.toLowerCase(), passHash);

		// підпишемо користувача на самого себе
		u.getSubscriptions().add(u);

		usersRepo.save(u);
	}
	
	@Transactional
	public List<User> getSubscribeRecommendations() {
		User currentUser = usersRepo.findOne(User.getCurrentUserId());

		// перетворює список користувачів на список їх ідентифікаторів
		List<Long> ignoreIds = new ArrayList<>();

		for(User u : currentUser.getSubscriptions()) {
			ignoreIds.add(u.getId());
		}
		
		return usersRepo.findFirst10ByIdNotIn(ignoreIds);
	}
	
	@Transactional
	public void subscribe(String login) {
		User u = usersRepo.findByLogin(login);

		// буде замінено після реалізації логіну
		User currentUser = usersRepo.findOne(User.getCurrentUserId());
		
		if(currentUser.getId() != u.getId()) {		
			currentUser.getSubscriptions().add(u);
			usersRepo.save(currentUser); //added
		}
	}
	
}
