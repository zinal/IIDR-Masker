# IIDR-Masker
Demonstrate basic masking/hashing capabilities for IBM Change Data Capture

To build with Maven, first take ts.jar from your CDC agent installation
and add it as a local Maven artifact as the following:

```bash
mvn install:install-file -Dfile=`pwd`/ts.jar \
  -DgroupId=com.ibm.iidr -DartifactId=ts -Dversion=11.4.0.2.10686 -Dpackaging=jar
```

After that pom.xml can be used for building and/or opening the project in Java IDE.

Another option would be putting the `CdcHasher.java` to the `{cdc-install-dir}/lib`
and building directly with javac:

```bash
javac CdcHasher.java -classpath ts.jar
```

After that, CDC will be able to calculate the derived columns in the following syntax:
```
%USERFUNC("JAVA","CdcHasher", "SHA-1", PAN, "passw0rd")
```

Here `PAN` is the name of the source column for hash compuation, and `"SHA-1"`
is the hash type.

The supported hash algorithms are listed here, for "MessageDigest" type of algorithms:
https://www.ibm.com/support/knowledgecenter/en/SSYKE2_8.0.0/com.ibm.java.security.component.80.doc/security-component/pkcs11implDocs/supportedalgorithms.html
