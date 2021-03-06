package backend.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;



@Entity
@Table(indexes = {
  @Index(columnList="login", unique = true), 
  @Index(columnList="email", unique = true)
})
public class User implements UserDetails {
	private static final long serialVersionUID = -532710433531902917L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank
	@Size(min = 1, max = 512)
	@Column(unique = true)
	private String login;
	
	@NotBlank
	@Size(min = 1, max = 512)
	@Column(unique = true)
	private String email;
	
	@NotBlank
	@Size(min = 1, max = 100)
	private String password;
	
	private UserRoleEnum role;
	
	 @Transient //to prevent the creation of the field in DB table
	private Set<GrantedAuthority> authRoles = new HashSet<GrantedAuthority>();
	
	@OneToMany(mappedBy = "author")
	private List<Post> posts = new ArrayList<>();
	
	@ManyToMany
	private Set<User> subscriptions = new HashSet<>();

	
	
	public User() {
		super();
	}
	
	public User(String login, String email, String password) {
		this.login = login;
		this.email = email;
		this.password = password;
		role=UserRoleEnum.USER;
		this.setAuthorities(); //for Spring Security
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role.name();
	}

	public void setRole(UserRoleEnum role) {
		this.role = role;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Set<User> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<User> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//return AuthorityUtils.createAuthorityList("USER");
		return AuthorityUtils.createAuthorityList(this.getRole().toString());
	}
	
	//public Set<GrantedAuthority> setAuthorities() {
	public void setAuthorities() {
		// задаємо роль для користувача
		this.authRoles.add(new SimpleGrantedAuthority(this.getRole().toString()));
		//return authRoles;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return getLogin();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public static User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public static Long getCurrentUserId() {
		User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return u.getId();
	}
	
	public static boolean isAnonymous() {
		// Метод SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
		// нічого не дасть, оскільки анонімний користувач в Spring Security теж вважається авторизованим і має ім'я по замовчуванню anonymousUser
		return "anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
}
