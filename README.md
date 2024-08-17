## Console Finance Tracker

Simple console Finance Tracker that can track current transactions, display them, add them from files, and save them to files.

## Techonolies Used

The program was created using:

* IntelliJ IDEA 2024.1.4
* JDK 22.0.2

## Installation and Launch

* Download and extract the Console-Finance-Tracker archive from the [releases](https://github.com/qwert312/Console-Finance-Tracker/releases/latest).
* Run start.bat

To work with the source code, simply download and extract the Source archive from the same location, and then open the Console-Finance-Tracker-1.0.0 folder.

## Usage

When the program starts, the user is prompted to choose from one of 9 commands. The first 8 commands pertain to the main functionality of the program, while the 9th command is for exiting the program. After each command, the program returns to displaying the list of available commands.

![screenshot1](images\image.png)

The first command allows you to add a transaction. When selected, you need to enter the type of transaction and its sum. If the values are entered incorrectly or if the transaction is an expense that exceeds the current balance, an error message will be displayed, and the program will return to the command selection.

![screenshot2](images\image-1.png)

The second command prints all transactions.

![screenshot3](images\image-2.png)

The third command prints a transaction by the ID, which the user is prompted to enter. If the transaction does not exist or the ID is entered incorrectly, an error message will be displayed, and the program will return to the command selection.

![screenshot4](images\image-3.png)

The fourth command prints all transactions between two dates that the user is prompted to enter. If there are no transactions between those dates or if the dates are entered incorrectly, an error message will be displayed, and the program will return to the command selection.

![screenshot5](images\image-4.png)

The fifth command prints current balance.

![screenshot6](images\image-5.png)

The sixth command adds transactions from a .csv file using the absolute path provided by the user. Transactions in the file must be in this format: date,type,sum. If the path is incorrect, the file is inaccessible (cannot be read or does not exist), the file type is invalid, the values in the file are in an incorrect format, or the transaction sums result in a negative balance, an error message will be displayed, and the program will return to the command selection.

The seventh command replaces the current transactions with those from a .csv file using the absolute path provided by the user. Transactions in the file must be in this format: date,type,sum. If the path is incorrect, the file is inaccessible (cannot be read or does not exist), the file type is invalid, the values in the file are in an incorrect format, or the transaction sums result in a negative balance, an error message will be displayed, and the program will return to the command selection.

![screenshot7](images\image-6.png)

The eighth command writes the current transactions to a .csv file at the path specified by the user. The existing content of the file at that path will be deleted. Transactions are recorded in the format: date,type,sum. If no file exists at the specified directory, one will be created with the name given in the path. If the path is incorrect, the file type is invalid, the file is inaccessible for writing, the file does not exist, and it could not be created in the specified location, or if there are no transactions to write, an error message will be displayed, and the program will return to the command selection.

![screenshot8](images\image-8.png)

The q command exits the program.