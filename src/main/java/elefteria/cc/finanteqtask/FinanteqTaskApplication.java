package elefteria.cc.finanteqtask;
import elefteria.cc.finanteqtask.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.TimeZone;

@SpringBootApplication
public class FinanteqTaskApplication implements CommandLineRunner {

	private OrganizationService organizationService;

	@Autowired
	public FinanteqTaskApplication(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}


	public static void main(String[] args) {
		SpringApplication.run(FinanteqTaskApplication.class, args);

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		while(true) {
			System.out.println("Enter number to select option\n1) Read file to database\n2) Write database to file\n3) Find\n4) Create\n0) exit");
			System.out.print("> ");

			String option = scanner.nextLine();

			if(option.trim().equals("1")) {
				organizationService.executeFileReadingOption();
			} else if(option.trim().equals("2")) {
				organizationService.executeWriteToFileOption();
			} else if(option.trim().equals("3")) {
				organizationService.executeFindOption();
			} else if(option.trim().equals("4")) {
				organizationService.executeCreateOption();
			} else if(option.trim().equals("0")) {
				break;
			} else {
				System.out.println("There is no such option\n");
			}
		}

	}
}
