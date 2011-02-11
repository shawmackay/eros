@echo off
echo -------------------------
echo      Starting Eros
echo -------------------------
$[javaloc]  -classpath .;$[jinilibs];$[serviceuiloc];$[additionallibs] -Djava.security.policy=$[POLICY] -Djava.rmi.server.codebase=$[CODEBASE] -Dorg.jini.projects.eros.debug=$[ENABLEDEBUG] -Dorg.jini.projects.eros.store.xml=$[storeconfigxml] org.jini.projects.eros.ErosServiceImpl  $[config]
