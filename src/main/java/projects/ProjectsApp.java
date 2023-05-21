package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.dao.DbConnection;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;



public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	Project curProject;
	
	//created a List of optins for the user to choose from.
	
	//@formatter:off	
		private List<String> operations = List.of(
				"1) add a project",
				"2) List projects",
				"3) Select project",
				"4) update project details",
				"5) Delete a project"
				);
		//@formatter:on

	public static void main(String[] args) {
	
		new ProjectsApp().processUserSelections();

}
	//The processUserSelections method gets a selection from the user, and then acts on that selection.
	//Added a try and catch block so an error is thrown if user makes a selection that is not listed.
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
				switch(selection) {
				  case -1:
					done = exitMenu();
					break;
				  
				   case 1:
					  createProject();
					  break;
					  
				   case 2:
					   listProjects();
					   break;
					   
				   case 3:
					   selectProject();
					   break;
					   
				   case 4:
					   updateProjectDetails();
					   break;
					   
				   case 5:
					   deleteProject();
					   break;
					
				  default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
				
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + "Try again");
				
				//e.printStackTrace();
			}
		}
		
	}
	
	
	private void deleteProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
		
	}
	
	private void updateProjectDetails() {
	  if(Objects.isNull(curProject)) {
		  System.out.println("\nPlease select a project.");
		  return;
		 }
	  
	  String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
	  BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
	  BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
	  Integer difficulty = getIntInput("Enter the project difficulty (1-5 [" + curProject.getDifficulty() + "]");
	  while(difficulty < 1 || difficulty > 5) {
			System.out.println("Invalid number try again.");
			difficulty = getIntInput("Enter the project difficulty (1-5)");
			}
	  String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
	  
	  Project project = new Project();
	  
	  project.setProjectId(curProject.getProjectId());
	  project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
	  project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
	  project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
	  project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
	  project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
	  
	  projectService.modifyProjectDetails(project);
	  curProject = projectService.fetchProjectById(curProject.getProjectId());
	}
	//This method selects the project by id 
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
		
	}
	//This method list all projects
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
		
	}
	
	//This method gather the projects details from the user.
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)"); 
		while(difficulty < 1 || difficulty > 5) {
			System.out.println("Invalid number try again.");
			difficulty = getIntInput("Enter the project difficulty (1-5)");
			}
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
	}
	//This method gets decimal input and sets it to two decimal points
	private BigDecimal getDecimalInput(String prompt) {
		
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
		return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number. Try again.");
		}
		
	}
	//This method prints a statement when user is exiting the menu.
	private boolean exitMenu() {
		System.out.println("Exiting the Menu");
		
		return true;
	}
	//This method gets user input for the selection they made as an Integer.
	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("Enter a menu selection");
		
		return Objects.isNull(input) ? -1 : input;
	}
	
	//This method gets input from the user and converts it to an Integer
	//Created a try and catch block. catch block throws exception if invalid number is enter.
	private Integer getIntInput(String prompt) {
		
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
		return null;
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}
	
	//This method is what really prints the prompts and gets the input from the user.
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}
	
	//This method prints each available selection to the console depending on the choice the user makes.
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		
		operations.forEach(line -> System.out.println("  " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}
	
	}
