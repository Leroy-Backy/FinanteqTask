package elefteria.cc.finanteqtask.repository;

import elefteria.cc.finanteqtask.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

    @Query("select e from Employee e where e.role=elefteria.cc.finanteqtask.enums.Role.DEPARTMENT_MANAGER and e.department.id in (select e.department.id From Employee e where e.id=?1)")
    List<Employee> getDepartmentManagersByEmployeeId(Long id);

    @Query("select e from Employee e where e.role=elefteria.cc.finanteqtask.enums.Role.PROJECT_MANAGER and e.project.id in (select e.project.id From Employee e where e.id=?1)")
    List<Employee> getProjectManagersByEmployeeId(Long id);

    @Query("select e from Employee e where concat(e.firstName, ' ', e.lastName) like %?1%")
    List<Employee> findByFirstNameOrLastName(String query);
}
