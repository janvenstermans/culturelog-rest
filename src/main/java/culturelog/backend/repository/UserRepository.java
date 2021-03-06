package culturelog.backend.repository;

import culturelog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jan Venstermans
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user object by unique username.
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

}
