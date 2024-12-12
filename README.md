# Hotel Reservation System
A hotel reservation system which allows users to create and manage reservations.

## Compile and Run the Program
Because this program utilises features from Java 21+ and Preview features, the ``--enable-preview`` flag must be used when compiling and running it.

To compile from src folder:

``javac --source 21 --enable-preview Run.java``

And then to run the program from src:

``java --enable-preview Run``

## Compile All Classes Using Generated File List
To keep separation of compiled classes, I used the following command to create a files list from the root project folder.
``dir .\*.java /s /b > FilesList.txt``

I changed the paths in the file to relative by removing everything up to, but not including, src.

Then, compile all files from this list and move the compiled classes to classes dir:
``javac  --source 21 --enable-preview @FilesList.txt -d classes``

And finally use the following command to run the program directly from the root folder:

``java --enable-preview -cp classes Run``

The ``-cp`` flag indicates the classpath used, i.e. the location of the actual compiled classes.