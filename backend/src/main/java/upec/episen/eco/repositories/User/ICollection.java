package upec.episen.eco.repositories.User;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.User.Collection;
import upec.episen.eco.models.User.User;

public interface ICollection extends JpaRepository<Collection, Long> {

    List<Collection> findAllByUser(User user);

    Collection findByUserAndName(User user, String name);

    Collection findAllById(Long id);
    Collection findByName(String name);


}
