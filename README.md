
# spring-ldap
This project explores spring-ldap sub project. Project demonstrates all features provided by spring-ldap sub project for ldap integration with spring project.

It demonstrates,

 1. Ldap Query execution  with CRUD operations using `LdapTemplate` .
 2. Attribute mapping using `AttributeMapper`.
 3. Attribute Mapping using `ContextMapper`.
 4. Use of `DirCntextAdapter` to simply Ldap query building and execution.
 5. Object-Directory Mapping (ODM).
 6. User Authentication using Spring LDAP.
 

# Prerequisite
	

 1. Java Installed (Java 8 or above).
 2. Maven Installed.
 3. Docker Installed and Running.


# Ldap Installation and Configuration

Project uses **ApacheDs** as Ldap implementation.  We will be using ApacheDs docker container.
Follow below steps to install, configure and load test data into ldap server.

### 1.  Download docker image
Use below command to download docker image. For more details on used docker image find [here
](https://hub.docker.com/r/openmicroscopy/apacheds/)

    docker pull openmicroscopy/apacheds

### 2.  Start/Run ApacheDs docker container
Start container using below command. It will start ApacheDs server at default port 389.
	
    sudo docker run --name ldap -d -p 389:10389 openmicroscopy/apacheds

### 3. Load Test Data

Load test data into ldap sever. We will use LDIF (LDAP Data Interchange Format) file to load data.
Find file at **/src/main/resources/data** with name **dap_data.ldif**

Use below command to load data into server.

    sudo ldapadd -v -h localhost:390 -c -x -D uid=admin,ou=system -w secret -f <path_to_ldap_data_file>

# Ldap Connection Details

|Config| Value|
|--|--|
|url| ldap://localhost:389 |
|Admin user (dn)| cn=admin,dc=openmicroscopy,dc=org |
|Admin user password| asdf1234 |
|Suffix/Ldap Root/Base| dc=openmicroscopy,dc=org |

Connection parameters are configured in properties file at **/src/main/resources/ldap-context-source-properties**.

# Usage
All samples are executed as a Junit Test cases. So use below command to execute all samples from root directory of project.

    mvn clean test


