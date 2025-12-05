package upec.episen.eco.repositories.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.User.User;

public interface IUser extends JpaRepository<User, Long> {

    List<User> findAllByDtype(String type);

    Optional<User> findByUsername(String username);
}
