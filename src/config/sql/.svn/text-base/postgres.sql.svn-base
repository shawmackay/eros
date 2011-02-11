CREATE DATABASE eros ;
CREATE USER eros PASSWORD 'erospassword' createdb createuser;
GRANT all on database eros to eros;
\c eros
CREATE TABLE eros_error
( 
  message varchar(500),
  trace_level varchar(30),
  stacktrace varchar(1000),
  arguments varchar(500),
  instancekey int8,
  methodname varchar(255),
  classname varchar(500),
  exceptionname varchar(500),
  time_stamp timestamp
);

CREATE TABLE eros_instance
(
  id int8,
  application varchar(150),
  ipaddress varchar(200),
  initialgroups varchar(255),
  osname varchar(100),
  osversion varchar(30),
  osuser varchar(100),
  jvmversion varchar(50)
);
