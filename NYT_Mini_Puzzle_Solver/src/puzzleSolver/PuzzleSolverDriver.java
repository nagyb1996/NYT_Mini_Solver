/**
 * NYT Mini-Crossword Puzzle Solver
 * @author Brody Nagy
 * @version 1.0.0
 */

package puzzleSolver;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class PuzzleSolverDriver {

	public static void main(String[] args) throws InterruptedException {

		// Set up WebDrivers
		System.setProperty("webdriver.gecko.driver", "//C:\\Users\\Brody\\Desktop\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		WebDriver answerDriver = new FirefoxDriver();

		// Click start puzzle button
		driver.get("https://www.nytimes.com/crosswords/game/mini");
		driver.findElement(By.cssSelector(".buttons-modalButton--1REsR")).click();

		// List to store all cells in puzzle, and a list to store each unique down clue
		// Only one set of clues (across or down) is needed to solve the puzzle
		List<WebElement> Cells = driver.findElements(By.xpath("//*[contains(@id, 'cell-id-')]"));
		List<Clue> uniqueClues = new ArrayList<Clue>();

		// Extract the unique clues from the cells, and find the cell where the answer
		// to the clue starts
		for (WebElement cell : Cells) {
			cell.click();
			String ariaLabel = cell.getAttribute("aria-label");
			String cellid = cell.getAttribute("id");
			Clue myClue = new Clue(ariaLabel);

			char delim = ':';
			int firstDelim = ariaLabel.indexOf(delim);

			String id = ariaLabel.substring(0, firstDelim);

			if (ariaLabel.contains("Letter: 1") && (id.equals(myClue.getID()))) {
				myClue.setXpath(cellid);
			}

			if ((!uniqueClues.contains(myClue)) && (!myClue.getID().contains("A"))
					&& (!myClue.getXpath().equals("-1"))) {
				uniqueClues.add(myClue);
			}

			Thread.sleep(200);
		}

		// Retrieve answers to clues from crossword puzzle solving site
		for (Clue clue : uniqueClues) {

			answerDriver.get("https://www.wordplays.com/crossword-solver/");
			answerDriver.findElement(By.xpath("//*[@id=\"clue\"]")).sendKeys(clue.clueText);
			answerDriver.findElement(By.xpath("//*[@id=\"pattern\"]")).sendKeys(String.valueOf(clue.numLetters));
			answerDriver.findElement(By.xpath(
					"/html/body/div[1]/div[3]/div[3]/div/div[2]/form/div[2]/div[1]/table/tbody/tr[4]/td/input[1]"))
					.click();
			clue.setAnswer(answerDriver.findElement(By.xpath(
					"/html/body/div[1]/div[3]/div[3]/div/div[5]/table[1]/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/a"))
					.getText());

			clue.printClue();

			Thread.sleep(5000);

		}

		// Close solving site
		answerDriver.quit();

		// Fill in answers to clues
		for (Clue clue : uniqueClues) {
			String xpath = "//*[@id='" + clue.getXpath() + "']";

			System.out.println(xpath);
			driver.findElement(By.xpath(xpath)).click();
			driver.findElement(By.xpath(xpath)).sendKeys(clue.getAnswer());
			Thread.sleep(2000);
		}

		// Sleep long enough for the music to play
		Thread.sleep(20000);
		driver.quit();

	}

	// Represents a crossword puzzle clue
	public static class Clue {
		private String id;
		private String clueText;
		private int numLetters;
		private String answer;
		private String xpath = "-1";

		// Clue Constructor
		public Clue(String input) {
			/*
			 * Parse ariaLabel into clue fields Example: 1D: Wine fruit, Answer: 5 letters,
			 * Letter: 1
			 */

			// Replace ASCII values with correct char
			String ariaLabel = input.replaceAll("&#39;", "'");

			char delim = ':';
			int firstDelim = ariaLabel.indexOf(delim);

			this.id = ariaLabel.substring(0, firstDelim);
			this.clueText = ariaLabel.substring((firstDelim + 2), ariaLabel.indexOf(", Answer"));
			this.numLetters = Integer
					.parseInt((ariaLabel.substring(ariaLabel.indexOf("Answer: ") + 8, ariaLabel.indexOf(" letters"))));
			this.answer = "";

		}

		@Override
		public boolean equals(Object other) {

			if (!(other instanceof Clue)) {
				return false;
			}

			Clue o = (Clue) other;

			return (this.id.equals(o.id));
		}

		// Clue print method
		public void printClue() {
			System.out.println("Id = " + this.id);
			System.out.println("Clue Text = " + this.clueText);
			System.out.println("Number Letter = " + this.numLetters);
			System.out.println("Answer = " + this.answer);
			System.out.println("xpath = " + this.xpath);
			System.out.println("");
		}

		// ID Setter
		public void setID(String ID) {
			this.id = ID;
		}

		// ID Getter
		public String getID() {
			return (this.id);
		}

		// clueText Setter
		public void setClueText(String clueText) {
			this.clueText = clueText;
		}

		// clueText Getter
		public String getClueText() {
			return (this.clueText);
		}

		// numLetters Setter
		public void setNumLetters(int numLetters) {
			this.numLetters = numLetters;
		}

		// numLetters Getter
		public int getNumLetters() {
			return (this.numLetters);
		}

		// Answer Setter
		public void setAnswer(String Answer) {
			this.answer = Answer;
		}

		// Answer Getter
		public String getAnswer() {
			return (this.answer);
		}

		// xpath Setter
		public void setXpath(String xpath) {
			this.xpath = xpath;
		}

		// xpath Getter
		public String getXpath() {
			return (this.xpath);
		}

	}

}
