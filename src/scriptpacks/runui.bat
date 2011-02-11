@echo off
echo -------------------------
echo      Starting Eros UI
echo -------------------------
$[javaloc]  -classpath .;$[jinilibs];$[serviceuiloc];$[additionallibs] -Djava.security.policy=$[POLICY]  -Dorg.jini.projects.eros.debug=$[ENABLEDEBUG]  org.jini.projects.eros.ui.ErosMainUI  $[group]
