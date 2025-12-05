package upec.episen.eco.repositories.User;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.User.Enterprise;

public interface IEnterprise extends JpaRepository<Enterprise, Long> {

}
