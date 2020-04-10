# IIDR-Masker (groovy-masker)
Support building column transformations in Groovy language for IBM Change Data Capture.

Precompiled class file (ready for installation) `CdcGroovy.class` is available in the `bin` directory.
Installation is performed by placing the `CdcGroovy.class` into `{cdc-install-dir}/lib`.
It depends on [groovy-2.5.10.jar](https://dl.bintray.com/groovy/maven/apache-groovy-binary-2.5.10.zip) library, which should be put into `{cdc-install-dir}/lib` and registered in `{cdc-install-dir}/instance/{instance-name}/conf/user.cp` file (as `lib/groovy-2.5.10.jar`). 

The compiled code is installed either on the source IBM CDC agent, in case when derived columns are used, or on the target agent, in case when the derived expressions are directly mapped to the target columns.

To build with Maven, first take ts.jar from your CDC agent installation
and add it as a local Maven artifact as the following:

```bash
mvn install:install-file -Dfile=`pwd`/ts.jar \
  -DgroupId=com.ibm.iidr -DartifactId=ts -Dversion=11.4.0.2.10686 -Dpackaging=jar
```

After that pom.xml can be used for building and/or opening the project in Java IDE.

Another option would be putting the `CdcGroovy.java` file to the `{cdc-install-dir}/lib`
and building directly with javac, for example:

```bash
javac CdcGroovy.java -classpath ts.jar:groovy-2.5.10.jar
```

Groovy scripts should go to `{user.home}/cdcgroovy`, into files with names `{script-name}.groovy`.
They can be referenced in the derived expressions with the following syntax:
```
%USERFUNC("JAVA","CdcGroovy","script-name", COLUMN1, COLUMN2, ...)
```

Each Groovy script should implement the `invoke()` function with at least one argument (script name).
The number and data types of arguments should correspond to the number and data types of columns.
Multiple `invoke()` implementations can be provided in a single Groovy script file to support different number and types of arguments.
Variadic `invoke()` implementations are also supported.

Example of simple Groovy script calculating an SHA-1 hash:

```Groovy
def invoke(String scriptName, Object value) {
   if (value == null)
     return null;
   return "sha1:" + value.toString().digest("SHA-1");
}
```
