 *************************************
 *                                   *
 * Eros - Error Logging Jini Service *
 *                                   *
 *************************************

 --------------
 - Extraction -
 --------------

There are two phases to the extraction of this service.
Firstly, the core libraries, dependant directories 
and main configuration files are extracted during 
installation.

Any user-configurable items, such as startup scripts 
or configuration files are handled as part of the 
post-installation process

------------------
- Implementation -
------------------

You will require your JDBC jars to be on the classpath for Eros,
select them with the add button in the Additional Libraries window
of the Post-Install screen.

-----------------
- Configuration -
-----------------
You will need to alter config/conninfo_generic.xml to point to your 
chosen database, or alter the script 'runeros' to point to a new
connection XML file. See doc/setup.html