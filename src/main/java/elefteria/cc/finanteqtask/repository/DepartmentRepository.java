package elefteria.cc.finanteqtask.repository;

import elefteria.cc.finanteqtask.entity.Department;
import elefteria.cc.finanteqtask.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
}
