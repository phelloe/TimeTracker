# TimeTracker

TimeTracker is a desktop application for Windows that will guesstimate start and end time of work (or general activity)
by evaluating the Windows Event Log. The earliest and latest entries to the event log will be presented in a table and
line chart format. A simple console output is available as well.
The application does not evaluate break times. Note that some tasks might also add entries to the event log while
the system is in stand-by-mode. If an activity lasts beyond midnight, the last entry will shadow the starting time of 
the next day.

## Usage
Two executables are available:
* TimeTracker.bat will start the application with a GUI
* TimeTracker-CLI.bat will start the application with a CLI (use the '-g' flag to start the GUI manually)

The following command line options are available:
* -y <arg>    Year of start date\s
* -m <arg>    Month of start date\s
* -g          Start GUI\s
* -c          Print as comma separated values

A single click on any of the table cells will directly copy the selected entry into the clipboard

## Building Instructions
Run 'mvn install' for both, jne-mod and jne-platform-mod first to obtain a modular version of JNA.
After that, 'mvn javafx:jlink' can be used to build the application.