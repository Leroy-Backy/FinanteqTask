package elefteria.cc.finanteqtask.repository;

import elefteria.cc.finanteqtask.entity.Position;
import elefteria.cc.finanteqtask.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByCode(String code);
}
