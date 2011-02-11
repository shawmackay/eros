

create user eros identified by erospassword;

grant connect to eros;

grant resource to eros;

connect eros/erospassword;

CREATE TABLE eros_error
(
  message varchar2(500),
  trace_level varchar2(30),
  stacktrace varchar2(1000),
  arguments varchar2(500),
  instancekey numeric,
  methodname varchar2(255),
  classname varchar2(500),
  exceptionname varchar2(500),
  time_stamp timestamp
);

CREATE TABLE eros_instance
(
  id numeric,
  application varchar2(150),
  ipaddress varchar2(200),
  initialgroups varchar2(255),
  osname varchar2(100),
  osversion varchar2(30),
  osuser varchar2(100),
  jvmversion varchar2(50)
);
