package elefteria.cc.finanteqtask.entity;

import elefteria.cc.finanteqtask.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "firstName filed is required in Employee")
    @Size(min = 2, message = "Minimal firstName length is 2 characters")
    private String firstName;

    @NotNull(message = "lastName filed is required in Employee")
    @Size(min = 2, message = "Minimal lastName length is 2 characters")
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "role filed is required in Employee")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne()
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne()
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "personal_data_id")
    private PersonalData personalData;

    public Employee(String firstName, String lastName, Role role, Position position, Department department, Project project) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.position = position;
        this.department = department;
        this.project = project;
    }
}
