## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## HOW TO RUN

1-Compile the code in the terminal using the following command:
javac Main.java

2-Run the program with the following command:
java -Xmx4g -Xss1g Main 8 DFS-H2 15

-Xmx4g: Increases Java heap memory to 4GB.
-Xss1g: Sets the stack size to 1GB.
Main: The main class file.
41: The board size (e.g., 41x41 grid).
DFS-H2: The search method (options include BFS, DFS, DFS-H1, or DFS-H2).
15: The time limit for the search in minutes.