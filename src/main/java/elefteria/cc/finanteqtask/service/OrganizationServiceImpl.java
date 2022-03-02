package elefteria.cc.finanteqtask.service;

import elefteria.cc.finanteqtask.entity.*;
import elefteria.cc.finanteqtask.enums.EntityType;
import elefteria.cc.finanteqtask.enums.Role;
import elefteria.cc.finanteqtask.enums.Gender;
import elefteria.cc.finanteqtask.exception.WrongRecordException;
import elefteria.cc.finanteqtask.repository.DepartmentRepository;
import elefteria.cc.finanteqtask.repository.EmployeeRepository;
import elefteria.cc.finanteqtask.repository.PositionRepository;
import elefteria.cc.finanteqtask.repository.ProjectRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";

    private PositionRepository positionRepository;
    private ProjectRepository projectRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;

    Scanner scanner;

    @Autowired
    public OrganizationServiceImpl(PositionRepository positionRepository, ProjectRepository projectRepository, DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.positionRepository = positionRepository;
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.scanner = new Scanner(System.in);
    }



    @Override
    public void executeFileReadingOption() {
        Path path;

        while(true) {
            System.out.print("Enter file path, or enter 'cancel' to cancel: ");

            String enteredValue = scanner.nextLine();

            if(enteredValue.equals("cancel")) return;

            path = Paths.get(enteredValue);

            if(Files.exists(path)) {
                try {
                    System.out.println("Processing...");
                    readFile(path);
                    break;
                } catch (IOException e) {
                    System.out.println(ANSI_YELLOW + "Problem with file writing" + ANSI_RESET);
                }
            } else {
                System.out.println(ANSI_YELLOW + "Wrong file path, try one more time" + ANSI_RESET);
            }
        }
    }

    //menu ti select what entity user wants to create
    @Override
    public void executeCreateOption() {
        while (true) {
            System.out.println("Enter number to select option\n1) Create Employee\n2) Create Department\n3) Create Project\n4) Create Position\n0) cancel");
            System.out.print("> ");

            String option = scanner.nextLine();

            try {
                if(option.trim().equals("1")) {
                    createEmployee();
                } else if(option.trim().equals("2")) {
                    create(new Department(), departmentRepository);
                } else if(option.trim().equals("3")) {
                    create(new Project(), projectRepository);
                } else if(option.trim().equals("4")) {
                    create(new Position(), positionRepository);
                } else if(option.trim().equals("0")) {
                    return;
                } else {
                    System.out.println("There is no such option\n");
                }
            } catch (ConstraintViolationException e) {
                System.out.println(ANSI_YELLOW + "Validation errors: ");
                e.getConstraintViolations().forEach(c -> System.out.println(c.getMessage()));
                System.out.println(ANSI_RESET);
            } catch (Exception e) {
                System.out.println(ANSI_YELLOW + "Unknown exception" + ANSI_RESET);
            }

        }
    }

    //menu to select what entity user wants to find
    @Override
    public void executeFindOption() {
        while (true) {
            System.out.println("Enter number to select option\n1) Find Employee\n2) Find Department\n3) Find Project\n4) Find Position\n0) cancel");
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                findEmployee();
            } else if(option.trim().equals("2")) {
                find(departmentRepository);
            } else if(option.trim().equals("3")) {
                find(projectRepository);
            } else if(option.trim().equals("4")) {
                find(positionRepository);
            } else if(option.trim().equals("0")) {
                return;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }
    }

    @Override
    public void executeWriteToFileOption() {
        Path path;

        while(true) {
            System.out.println("Enter path to directory where you want to save file, or enter 'cancel' to cancel: ");
            System.out.print("> ");

            String enteredValue = scanner.nextLine();

            if(enteredValue.equals("cancel")) return;

            path = Paths.get(enteredValue);

            if(Files.exists(path)) {
                try {
                    System.out.println("Processing...");
                    writeFile(Paths.get(enteredValue + "/org.csv"));
                    break;
                } catch (IOException e) {
                    System.out.println(ANSI_YELLOW + "Problem with file reading" + ANSI_RESET);
                }
            } else {
                System.out.println(ANSI_YELLOW + "Wrong file path, try one more time" + ANSI_RESET);
            }
        }
    }

    private void writeFile(Path path) throws IOException {
        CSVPrinter printer = null;

        try {
            BufferedWriter writer = Files.newBufferedWriter(path);
            printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withNullString(""));

            writeSmallEntities(printer, positionRepository);
            System.out.println(ANSI_GREEN + "All Positions were saved" + ANSI_RESET);
            writeSmallEntities(printer, projectRepository);
            System.out.println(ANSI_GREEN + "All Projects were saved" + ANSI_RESET);
            writeSmallEntities(printer, departmentRepository);
            System.out.println(ANSI_GREEN + "All Departments were saved" + ANSI_RESET);
            writeEmployee(printer);
            System.out.println(ANSI_GREEN + "All Employees were saved" + ANSI_RESET);

        } catch (IOException e) {
            System.out.println(ANSI_YELLOW + "Problem with file reading" + ANSI_RESET);
        } finally {
            printer.close();
        }
    }

    private void writeEmployee(CSVPrinter printer) {
        int currPage = 0;

        while (true) {
            Page<Employee> employees = employeeRepository.findAll(PageRequest.of(currPage, 100));

            for(Employee employee: employees) {
                try {
                    LocalDate birthDay = employee.getPersonalData().getBirthDate();

                    printer.printRecord(
                            EntityType.EMPLOYEE.name(),
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getRole().name(),
                            employee.getPersonalData().getPhone(),
                            employee.getPersonalData().getEmail(),
                            employee.getPersonalData().getCity(),
                            birthDay.getDayOfMonth() + "/" + birthDay.getMonthValue() + "/" + birthDay.getYear(),
                            employee.getPersonalData().getGender().name(),
                            employee.getPersonalData().getSalary(),
                            employee.getPosition() != null ? employee.getPosition().getCode(): null,
                            employee.getDepartment() != null ? employee.getDepartment().getCode(): null,
                            employee.getProject() != null ? employee.getProject().getCode(): null
                    );
                } catch (Exception e) {
                    System.out.println(ANSI_YELLOW + "Error with writing Employee with id: " + employee.getId() + ANSI_RESET);
                }
            }

            if(currPage < employees.getTotalPages() - 1) {
                currPage++;
            } else {
                break;
            }
        }
    }

    private void writeSmallEntities(CSVPrinter printer ,JpaRepository repository) {
        int currPage = 0;

        while (true) {
            Page<SmallEntity> elements = repository.findAll(PageRequest.of(currPage, 100));

            for(SmallEntity entity: elements) {
                try {
                    printer.printRecord(entity.getClass().getSimpleName().toUpperCase(), entity.getCode(), entity.getName());
                } catch (IOException e) {
                    System.out.println(ANSI_YELLOW + "Error with writing " + entity.getClass().getSimpleName() + " with id: " + entity.getId() + ANSI_RESET);
                }
            }

            if(currPage < elements.getTotalPages() - 1) {
                currPage++;
            } else {
                break;
            }
        }
    }

    private void createEmployee() throws ConstraintViolationException {
        Employee employee = new Employee();
        PersonalData personalData = new PersonalData();

        String enteredValue;

        System.out.print("Enter first name: ");
        employee.setFirstName(scanner.nextLine());

        System.out.print("Enter last name: ");
        employee.setLastName(scanner.nextLine());

        while (true) {
            System.out.println("Enter number to select Role\n1) Employee\n2) Project Manager\n3) Department Manager");
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                employee.setRole(Role.EMPLOYEE);
                break;
            } else if(option.trim().equals("2")) {
                employee.setRole(Role.PROJECT_MANAGER);
                break;
            } else if(option.trim().equals("3")) {
                employee.setRole(Role.DEPARTMENT_MANAGER);
                break;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }

        while (true) {
            System.out.println("Enter number to select Gender\n1) Male\n2) Female");
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                personalData.setGender(Gender.MALE);
                break;
            } else if(option.trim().equals("2")) {
                personalData.setGender(Gender.FEMALE);
                break;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }

        System.out.print("Enter phone: ");
        personalData.setPhone(scanner.nextLine());

        System.out.print("Enter email: ");
        personalData.setEmail(scanner.nextLine());

        System.out.print("Enter city: ");
        personalData.setCity(scanner.nextLine());

        while (true) {
            try {
                System.out.print("Enter birth date with format dd/MM/yyyy: ");
                personalData.setBirthDate(LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                break;
            } catch (DateTimeParseException e) {
                System.out.println(ANSI_YELLOW + "Wrong date format" + ANSI_RESET);
            }
        }

        try {
            System.out.print("Enter salary: ");
            personalData.setSalary(scanner.nextInt());
        } catch (NumberFormatException e) {
            personalData.setSalary(0);
        }

        scanner.nextLine();

        while (true) {
            System.out.print("Enter code of Position, or 'null' to set null: ");
            enteredValue = scanner.nextLine();
            if(enteredValue.equals("null")) {
                employee.setPosition(null);
                break;
            } else {
                Optional<Position> position = positionRepository.findByCode(enteredValue);
                if(position.isPresent()) {
                    employee.setPosition(position.get());
                    break;
                } else {
                    System.out.println(ANSI_YELLOW + "Position with code:" + enteredValue + " not found" + ANSI_RESET);
                }
            }
        }

        while (true) {
            System.out.print("Enter code of Department, or 'null' to set null: ");
            enteredValue = scanner.nextLine();
            if(enteredValue.equals("null")) {
                employee.setPosition(null);
                break;
            } else {
                Optional<Department> department = departmentRepository.findByCode(enteredValue);
                if(department.isPresent()) {
                    employee.setDepartment(department.get());
                    break;
                } else {
                    System.out.println(ANSI_YELLOW + "Department with code:" + enteredValue + " not found" + ANSI_RESET);
                }
            }
        }

        while (true) {
            System.out.print("Enter code of Project, or 'null' to set null: ");
            enteredValue = scanner.nextLine();
            if(enteredValue.equals("null")) {
                employee.setPosition(null);
                break;
            } else {
                Optional<Project> project = projectRepository.findByCode(enteredValue);
                if(project.isPresent()) {
                    employee.setProject(project.get());
                    break;
                } else {
                    System.out.println(ANSI_YELLOW + "Project with code:" + enteredValue + " not found" + ANSI_RESET);
                }
            }
        }

        employee.setPersonalData(personalData);

        while (true) {
            System.out.println("Enter number to select option\n1) Save Employee\n0) Cancel");
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                employeeRepository.save(employee);
                System.out.println(ANSI_GREEN + "You successfully created Employee" + ANSI_RESET);
                break;
            } else if(option.trim().equals("0")) {
                return;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }
    }

    private void create(SmallEntity entity, JpaRepository repository) {
        String enteredValue;

        System.out.println(entity.getClass().getSimpleName());

        while (true) {
            System.out.print("Enter code, or enter 'cancel' to cancel: ");

            enteredValue = scanner.nextLine();

            if(enteredValue.length() < 2) {
                System.out.println(ANSI_YELLOW + "Minimal code length is 2 characters" + ANSI_RESET);
                continue;
            } else if(enteredValue.equals("cancel")) {
                return;
            }

            if(entity.getClass().getSimpleName().equals("Position") && positionRepository.findByCode(enteredValue).isPresent()) {
                System.out.println(ANSI_YELLOW + "Code " + enteredValue + " is already taken" + ANSI_RESET);
            } else if(entity.getClass().getSimpleName().equals("Project") && projectRepository.findByCode(enteredValue).isPresent()) {
                System.out.println(ANSI_YELLOW + "Code " + enteredValue + " is already taken" + ANSI_RESET);
            } else if(entity.getClass().getSimpleName().equals("Department") && departmentRepository.findByCode(enteredValue).isPresent()){
                System.out.println(ANSI_YELLOW + "Code " + enteredValue + " is already taken" + ANSI_RESET);
            } else {
                entity.setCode(enteredValue);
                break;
            }
        }

        System.out.print("Enter name, or enter 'cancel' to cancel: ");

        enteredValue = scanner.nextLine();

        if(enteredValue.equals("cancel")) return;

        entity.setName(enteredValue);

        repository.save(entity);
        System.out.println(ANSI_GREEN + "You successfully created " + entity.getClass().getSimpleName() + ANSI_RESET);
    }

    private void findEmployee() {
        while (true) {
            System.out.println("Enter number to select option\n1) Search by id\n2) Search by first name or last name\n0) cancel");
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                System.out.print("Enter id, or enter 'cancel' to cancel: ");
                String enteredValue = scanner.nextLine();

                if(enteredValue.equals("cancel")) continue;

                try {
                    Long id = Long.parseLong(enteredValue);

                    Optional<Employee> employee = employeeRepository.findById(id);

                    if(employee.isPresent()) {
                        showEmployee(employee.get());
                        return;
                    } else {
                        System.out.println(ANSI_YELLOW + "Employee with id: " + id + " not found" + ANSI_RESET);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(ANSI_YELLOW + "Invalid number" + ANSI_RESET);
                }

            } else if(option.trim().equals("2")) {
                System.out.print("Enter name, or enter 'cancel' to cancel: ");
                String enteredValue = scanner.nextLine();

                if(enteredValue.equals("cancel")) continue;

                List<Employee> result = employeeRepository.findByFirstNameOrLastName(enteredValue);

                if(result.size() == 0) {
                    System.out.println(ANSI_YELLOW + "Employee with name: " + enteredValue + " not found" + ANSI_RESET);
                    continue;
                } else if(result.size() == 1) {
                    showEmployee(result.get(0));
                    return;
                }

                while (true) {
                    System.out.println("\nEmployees found:");
                    result.forEach(e -> System.out.println("["+e.getId()+"] " + e.getFirstName() + " " + e.getLastName()));

                    System.out.print("Please select employee by entering id, or enter 'cancel' to cancel: ");

                    enteredValue = scanner.nextLine();

                    if(enteredValue.equals("cancel")) return;

                    try {
                        Long id = Long.parseLong(enteredValue);

                        Optional<Employee> employee = result.stream().filter(e -> e.getId().equals(id)).findFirst();

                        if(employee.isPresent()) {
                            showEmployee(employee.get());
                            return;
                        } else {
                            System.out.println(ANSI_YELLOW + "Employee with id: " + id + " not found" + ANSI_RESET);
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(ANSI_YELLOW + "Invalid number" + ANSI_RESET);
                    }

                }
            } else if(option.trim().equals("0")) {
                return;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }
    }

    private void find(JpaRepository repository) {
        int currPage = 0;

        Page<SmallEntity> elements = repository.findAll(PageRequest.of(currPage, 2));

        if(elements.getTotalElements() == 0) {
            System.out.println(ANSI_YELLOW + "Elements not found" + ANSI_RESET);
            return;
        }

        listElements(elements);

        while (true) {
            String message = "Enter number to select option\n";

            boolean prevPresent = false;
            boolean nextPresent = false;

            if(currPage < elements.getTotalPages() - 1) {
                message += "2) next page\n";
                nextPresent = true;
            }
            if(currPage > 0) {
                message += "1) previous page\n";
                prevPresent = true;
            }

            message += "0) cancel";

            System.out.println(message);
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                if(!prevPresent) {
                    System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
                } else {
                    currPage--;
                    listElements(repository.findAll(PageRequest.of(currPage, 2)));
                }
            } else if(option.trim().equals("2")) {
                if(!nextPresent) {
                    System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
                } else {
                    currPage++;
                    listElements(repository.findAll(PageRequest.of(currPage, 2)));
                }
            } else if(option.trim().equals("0")) {
                return;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }
    }

    private void listElements(Page<SmallEntity> elements) {
        System.out.println("----------------------");

        System.out.println("Elements found:");
        elements.forEach(e -> System.out.println("["+e.getId()+"] code:" + e.getCode() + ", name:" + e.getName()));

        System.out.println("----------------------");
    }

    private void showEmployee(Employee employee) {
        while(true) {
            System.out.println("Enter number to select option\n1) Show managers\n2) Show employee info\n3) Delete employee\n0) cancel");
            System.out.print("> ");

            String option = scanner.nextLine();

            if(option.trim().equals("1")) {
                showEmployeeManagers(employee);
            } else if(option.trim().equals("2")) {
                showEmployeeInfo(employee);
            } else if(option.trim().equals("3")) {
                deleteEmployee(employee);
            } else if(option.trim().equals("0")) {
                break;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }

    }

    private void showEmployeeManagers(Employee employee) {
        if(employee.getRole().equals(Role.EMPLOYEE)){
            // find department managers
            List<Employee> departmentManagers = employeeRepository.getDepartmentManagersByEmployeeId(employee.getId());
            // find project managers
            List<Employee> projectManagers = employeeRepository.getProjectManagersByEmployeeId(employee.getId());

            System.out.println("----------------------------");

            if(departmentManagers.size() > 0) {
                System.out.println("Department managers:");
                departmentManagers.forEach(e -> System.out.println("["+e.getId()+"] " + e.getFirstName() + " " + e.getLastName()));
            }

            if(projectManagers.size() > 0) {
                System.out.println("Project managers:");
                projectManagers.forEach(e -> System.out.println("["+e.getId()+"] " + e.getFirstName() + " " + e.getLastName()));
            }

            System.out.println("----------------------------");

        } else {
            System.out.println(ANSI_GREEN + "This employee is a manager" + ANSI_GREEN);
        }
    }

    private void showEmployeeInfo(Employee employee) {
        System.out.println("\n----------------------------------------");
        System.out.println("id:         " + employee.getId());
        System.out.println("name:       " + employee.getFirstName() + " " + employee.getLastName());
        System.out.println("role:       " + employee.getRole());
        if(employee.getPosition() != null)
            System.out.println("position:   " + employee.getPosition());
        System.out.println("phone:      " + employee.getPersonalData().getPhone());
        System.out.println("email:      " + employee.getPersonalData().getEmail());
        System.out.println("gender:     " + employee.getPersonalData().getGender());
        if(employee.getPersonalData().getCity() != null)
            System.out.println("city:       " + employee.getPersonalData().getCity());
        if(employee.getPersonalData().getBirthDate() != null)
            System.out.println("birthDate:  " + employee.getPersonalData().getBirthDate());
        System.out.println("salary:     " + employee.getPersonalData().getSalary());
        if(employee.getProject() != null)
            System.out.println("project:    " + "Project(code=" + employee.getProject().getCode() + ", name=" + employee.getProject().getName() + ")");
        if(employee.getDepartment() != null)
            System.out.println("department: " + employee.getDepartment());
        System.out.println("----------------------------------------");
    }

    private void deleteEmployee(Employee employee) {
        while(true) {
            System.out.print("Are you sure? (y/n): ");

            String enteredValue = scanner.nextLine();

            if(enteredValue.equalsIgnoreCase("y")) {
                employeeRepository.delete(employee);
                return;
            } else if(enteredValue.equalsIgnoreCase("n")) {
                return;
            } else {
                System.out.println(ANSI_YELLOW + "There is no such option\n" + ANSI_RESET);
            }
        }
    }

    private void readFile(Path path) throws IOException {
        // Buffered Reader is a stream, it allows reading big files
        Reader reader = Files.newBufferedReader(path);

        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withNullString("")); // .withNullString("") will read empty strings as null

        long timeBefore = System.currentTimeMillis(); // save time before reading to count how much time it took

        for(CSVRecord record: parser) {
            try {
                // column with index 0 contains type of entity,
                if(record.get(0).equals(EntityType.POSITION.name())) {
                    handlePositionReading(record);
                } else if(record.get(0).equals(EntityType.DEPARTMENT.name())) {
                    handleDepartmentReading(record);
                } else if(record.get(0).equals(EntityType.PROJECT.name())) {
                    handleProjectReading(record);
                } else if(record.get(0).equals(EntityType.EMPLOYEE.name())) {
                    handleEmployeeReading(record);
                } else {
                    System.out.println(ANSI_YELLOW + "Wrong entity type in record Nr." + record.getRecordNumber() + ANSI_RESET);
                }
            } catch (ConstraintViolationException e) {
                System.out.println(ANSI_YELLOW + "Validation errors in record Nr." + record.getRecordNumber()+ ":");
                e.getConstraintViolations().forEach(c -> System.out.println(c.getMessage()));
                System.out.println(ANSI_RESET);
            } catch (WrongRecordException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(ANSI_YELLOW + " Unknown exception in record Nr." + record.getRecordNumber() + ANSI_RESET);
            }
        }

        System.out.println(ANSI_GREEN + "Ended file reading in " + (System.currentTimeMillis() - timeBefore)/60000 + " minutes!\n" + ANSI_RESET);
    }

    // 1 -> code, 2 -> name
    private void handleProjectReading(CSVRecord record) throws ConstraintViolationException {
        if(record.size() != 3) {
            throw new WrongRecordException(ANSI_YELLOW + "Record Nr." + record.getRecordNumber() + " has wrong amount of fields" + ANSI_RESET);
        }

        if(record.get(1) != null && projectRepository.findByCode(record.get(1)).isPresent()) {
            throw new WrongRecordException(ANSI_YELLOW + "Project with code: " + record.get(1) + " already exists" + ANSI_RESET);
        }

        projectRepository.save(new Project(record.get(1), record.get(2)));
    }

    // 1 -> code, 2 -> name
    private void handlePositionReading(CSVRecord record) throws ConstraintViolationException {
        if(record.size() != 3) {
            throw new WrongRecordException(ANSI_YELLOW + "Record Nr." + record.getRecordNumber() + " has wrong amount of fields" + ANSI_RESET);
        }

        if(record.get(1) != null && positionRepository.findByCode(record.get(1)).isPresent()) {
            throw new WrongRecordException(ANSI_YELLOW + "Position with code: " + record.get(1) + " already exists" + ANSI_RESET);
        }

        positionRepository.save(new Position(record.get(1), record.get(2)));
    }

    // 1 -> code, 2 -> name
    private void handleDepartmentReading(CSVRecord record) throws ConstraintViolationException {
        if(record.size() != 3) {
            throw new WrongRecordException(ANSI_YELLOW + "Record Nr." + record.getRecordNumber() + " has wrong amount of fields" + ANSI_RESET);
        }

        if(record.get(1) != null && departmentRepository.findByCode(record.get(1)).isPresent()) {
            throw new WrongRecordException(ANSI_YELLOW + "Department with code: " + record.get(1) + " already exists" + ANSI_RESET);
        }

        departmentRepository.save(new Department(record.get(1), record.get(2)));
    }

    /* 1 -> firstName, 2 -> lastName, 3 -> role, 4 -> phone, 5 -> email,
       6 -> city, 7 -> birthDate, 8 -> gender, 9 -> salary,
       10 -> position code, 11 -> department code, 12 -> project code
    */
    private void handleEmployeeReading(CSVRecord record) {
        if(record.size() != 13) {
            throw new WrongRecordException(ANSI_YELLOW + "Record Nr." + record.getRecordNumber() + " has wrong amount of fields" + ANSI_RESET);
        }

        Position position = null;
        Project project = null;
        Department department = null;

        if(record.get(10) != null) {
            position = positionRepository.findByCode(record.get(10)).get();
        }

        if(record.get(11) != null) {
            department = departmentRepository.findByCode(record.get(11)).get();
        }

        if(record.get(12) != null) {
            project = projectRepository.findByCode(record.get(12)).get();
        }

        LocalDate dateOfBirth = null;

        try {
            dateOfBirth = LocalDate.parse(record.get(7), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {

        }

        int salary = 0;

        try {
            salary = Integer.parseInt(record.get(9));
        } catch (NumberFormatException e) {

        }

        PersonalData personalData =
                new PersonalData(record.get(4), record.get(5), record.get(6), dateOfBirth, Gender.getGenderByName(record.get(8)), salary);

        Employee employee = new Employee();
        employee.setPersonalData(personalData);
        employee.setFirstName(record.get(1));
        employee.setLastName(record.get(2));
        employee.setRole(Role.getRoleByName(record.get(3)));
        employee.setPosition(position);
        employee.setDepartment(department);
        employee.setProject(project);

        employeeRepository.save(employee);
    }
}
