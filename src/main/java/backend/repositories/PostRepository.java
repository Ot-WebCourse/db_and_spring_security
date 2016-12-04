package backend.repositories;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import backend.models.Post;
import backend.models.User;

public interface PostRepository extends CrudRepository<Post, Long> {
    public Page<Post> findByAuthorInOrderByCreatedAtDesc(Set<User> users, Pageable pageable);
}
