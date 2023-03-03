# Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

# Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

# Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

# Running the Project

## Makefile
To run the makefile, ensure that make is installed on your machine.  
Since the project uses Windows to run, install make with `choco install make`.  
You can read more about choco [here](https://chocolatey.org/install).

## Compile Script
Alternatively you can run `.\compile.ps1`.  
To run this script, ensure you have the adecuate permission.  
To view permission, run `Get-ExecutionPolicy` in PowerShell. With adequate permisions, this command should return `RemoteSigned`.  
If it does not run the command `Set-ExecutionPolicy RemoteSigned`.