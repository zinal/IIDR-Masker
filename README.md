# IIDR-Masker
Demonstrate basic masking/hashing capabilities for IBM Change Data Capture

`simple-masker` subdirectory contains:
* `CdcHasher` - compute SHA-1 or MD-5 hashes on the input values;
* `CdcBin2Str` - convert arbitraty binary data to hex or base64 representation.

`groovy-masker` subdirectory contains `CdcGroovy`, which allows to implement arbitrary column transformations as Groovy scripts.
