# Hotel Reservation System
A hotel reservation system which allows users to create and manage reservations.

## Compile All Classes Using Generated File List
Because this program utilises features from Java 22 and Preview features, the ``--enable-preview`` flag must be used when compiling and running it.

To keep separation of compiled classes, I used the following command to create a files list from the root project folder.
``dir .\*.java /s /b > FilesList.txt``

I changed the paths in the file to relative by removing everything up to, but not including, src.

Then, compile all files from this list and move the compiled classes to classes dir:
``javac  --source 22 --enable-preview @FilesList.txt -d classes``

Use ``xcopy /s /y resources\*.properties classes\`` to copy the resource files to the correct location.

And finally use the following command to run the program directly from the root folder:

``java --enable-preview -cp classes Run``

The ``-cp`` flag indicates the classpath used, i.e. the location of the actual compiled classes.

Use -Duser.language=fr -Duser.country=FR for French localisation (welcome message only, for demonstration purposes - es_ES also available):

``java --enable-preview -cp classes -Duser.language=fr -Duser.country=FR Run``

