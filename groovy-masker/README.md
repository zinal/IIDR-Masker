# IIDR-Masker (groovy-masker)
Support building column transformations in Groovy language for IBM Change Data Capture.

Precompiled class is available in the `bin` directory.
It depends on `groovy-2.5.8.jar` library, which should be put into `{cdc-install-dir}/lib` and registered in `{cdc-install-dir}/instance/{instance-name}/conf/user.cp` file (as `lib/groovy-2.5.8.jar`). 

To build with Maven, first take ts.jar from your CDC agent installation
and add it as a local Maven artifact as the following:

```bash
mvn install:install-file -Dfile=`pwd`/ts.jar \
  -DgroupId=com.ibm.iidr -DartifactId=ts -Dversion=11.4.0.2.10686 -Dpackaging=jar
```

After that pom.xml can be used for building and/or opening the project in Java IDE.

Another option would be putting the `*.java` files to the `{cdc-install-dir}/lib`
and building directly with javac, for example:

```bash
javac CdcGroovy.java -classpath ts.jar:groovy-2.5.8.jar
```
