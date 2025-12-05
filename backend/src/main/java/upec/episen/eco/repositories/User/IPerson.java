package upec.episen.eco.repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.User.Person;

public interface IPerson extends JpaRepository<Person, Long> {

}
