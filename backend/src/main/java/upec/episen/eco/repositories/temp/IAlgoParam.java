package upec.episen.eco.repositories.temp;

import org.springframework.data.jpa.repository.JpaRepository;

import upec.episen.eco.models.temp.AlgoParam;

public interface IAlgoParam extends JpaRepository<AlgoParam, Long> {

    AlgoParam findByType(String type);
}
