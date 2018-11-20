package scraper;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.UnavailableURLException;
import utils.Utils;

public class GradeScrapper {
	private enum Tab {
		ASSIGNMENTS,
		CLASS_SCORES;
	}

	private final String TARGET_URL = "https://parentconnect.aacps.org";

	private WebDriver driver = null;
	private Tab currentTab = Tab.ASSIGNMENTS;

	public GradeScrapper(String firstName, String middleName, String lastName, String userName, String password, String chromeDriverPath, String... options) throws Exception {
		driver = initialize(chromeDriverPath, options);

		if(!Utils.urlIsAvailable(TARGET_URL)) {
			throw new UnavailableURLException(TARGET_URL);
		}

		driver.get(TARGET_URL);
		driver.manage().window().maximize();

		if(!Utils.urlIsAvailable(TARGET_URL)) {
			throw new UnavailableURLException(TARGET_URL);
		}

		signin(userName, password);

		if(!Utils.urlIsAvailable(TARGET_URL)) {
			throw new UnavailableURLException(TARGET_URL);
		}

		selectStudent(firstName, middleName, lastName);
	}

	public void terminate() {
		driver.close();
	}

	public ArrayList<ClassScore> getClassScores() {
		if(currentTab == Tab.ASSIGNMENTS) {
			driver.findElement(By.xpath("//a[@href='AssignmentsSchedule.asp']")).click();
			currentTab = Tab.CLASS_SCORES;
		}

		WebElement classScoreTable = driver.findElement(By.className("clIGPClassGrdTbl"));
		ArrayList<ClassScore> classScores = new ArrayList<ClassScore>();

		int i = 0;
		List<WebElement> rows = classScoreTable.findElements(By.tagName("tr"));
		for(WebElement row : rows) {
			if(i++ == 0) {
				continue;
			}

			List<WebElement> cells = row.findElements(By.tagName("td"));

			int j = 0;
			ArrayList<String> cellData = new ArrayList<String>();
			for(WebElement cell : cells) {
				if(j++ == 1) {
					cellData.add(cell.getText().replace("\n", "").replace("\r", "").replaceAll(" \\(.*\\)", ""));
				}else {
					cellData.add(cell.getText());
				}
			}

			classScores.add(new ClassScore(cellData));
		}

		return classScores;
	}

	public AssignmentsList getAssignments() {
		if(currentTab == Tab.CLASS_SCORES) {
			driver.findElement(By.xpath("//a[@href='AssignmentsGeneral.asp']")).click();
			currentTab = Tab.ASSIGNMENTS;
		}

		WebElement timeframe = driver.findElement(By.id("cbTimeFilterSelection31459"));
		Select option = new Select(timeframe);
		option.selectByValue("THISSCHOOLYEAR");

		WebElement assignmentTable = driver.findElement(By.className("clIGPTaskGenTbl"));
		AssignmentsList assignments = new AssignmentsList();

		int i = 0;
		List<WebElement> rows = assignmentTable.findElements(By.tagName("tr"));
		for(WebElement row : rows) {
			if(i++ == 0) {
				continue;
			}

			List<WebElement> cells = row.findElements(By.tagName("td"));

			ArrayList<String> cellData = new ArrayList<String>();
			for(WebElement cell : cells) {
				cellData.add(cell.getText());
			}

			assignments.add(new Assignment(cellData));
		}

		return assignments;
	}

	private void selectStudent(String firstName, String middleName, String lastName) {
		String fullName = lastName + ", " + firstName + " " + middleName;

		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(fullName)));

		driver.findElement(By.linkText(fullName)).click();

		driver.findElement(By.linkText("Assignments")).click();
	}

	private void signin(String username, String password) {
		WebElement signinButton = driver.findElement(By.className("button"));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].scrollIntoView()", signinButton);

		signinButton.click();

		driver.findElement(By.id("UserID")).sendKeys(username);
		driver.findElement(By.id("UserPwd")).sendKeys(password);

		driver.findElement(By.id("LoginButton")).click();
	}

	private WebDriver initialize(String chromeDriverPath, String... options) {
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);

		ChromeOptions args = new ChromeOptions();
		for(String arg : options) {
			args.addArguments(arg);
		}

		return new ChromeDriver(args);
	}
}
