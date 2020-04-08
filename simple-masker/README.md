# IIDR-Masker (simple-masker)
Demonstrate basic masking/hashing and type conversion capabilities for IBM Change Data Capture.

Precompiled classes are available in the `bin` directory.

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
javac CdcHasher.java -classpath ts.jar
javac CdcBin2Str.java -classpath ts.jar
```

After that, CDC will be able to calculate the derived columns in the following syntax:
```
%USERFUNC("JAVA","CdcHasher", "SHA-1", PAN, "passw0rd")
```

Here `PAN` is the name of the source column for hash compuation, and `"SHA-1"`
is the hash type.

The supported hash algorithms are listed here, for "MessageDigest" type of algorithms:
https://www.ibm.com/support/knowledgecenter/en/SSYKE2_8.0.0/com.ibm.java.security.component.80.doc/security-component/pkcs11implDocs/supportedalgorithms.html

`CdcBin2Str` allows to convert source binary (and even character) values
to hexadecimal or base64 textual representations, with the following syntax:

```
%USERFUNC("JAVA","CdcBin2Str", uuid)        -- hex conversion
%USERFUNC("JAVA","CdcBin2Str", uuid, 0)     -- hex conversion
%USERFUNC("JAVA","CdcBin2Str", uuid, 1)     -- base64 conversion
```

Non-binary data will be first converted to text (no-op for already character data on input), then this text will be converted to binary (with UTF-8 encoding), and then hex or base-64 encoded.
