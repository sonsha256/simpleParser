# What does this project do?
A simple parser that reads in a data set of the Institut für das Entgeltsystem im Krankenhaus (InEK), that contains all hospitals and their locations. The locations are filtered and transformed to conform to the international FHIR standard. The result is a list of all valid hospital locations in Germany that can be accessed via a getter.
The data is taken from this [website](https://krankenhausstandorte.de//login?) and can also be directly downloaded [here](https://krankenhausstandorte.de/xml/latest) but be careful with the second link, the download starts immediately!

# How to use
The code is executed through the main method in the ParserStarter class. There is no direct input or output, it's all handled in the code. But if you want to see what is read in you can activate the printStandortInformation() method line 438 of the StandortHandler. This will print the information to the console.

## Read in other files
If you want to use more recent data set from the InEK, you need to upload it in src/main/resources and call it in the VerzeichnisParser class.

