# IIDR-Masker
Demonstrate basic masking/hashing and other custom transformation capabilities for IBM Change Data Capture

`simple-masker` subdirectory contains:
* `CdcHasher` - compute SHA-1 or MD-5 hashes on the input values;
* `CdcBin2Str` - convert arbitraty binary data to hex or base64 representation.
* `CdcNum2Long` - convert numeric values (e.g. NUMBER, DECIMAL, DECFLOAT, etc.) to 64-bit integers
* `DESeqno` - an updated/fixed operation sequence number generator (should be used instead of the default one supplied in IIDR samples)
* `DENow` - generate the current timestamp (on the source or target CDC agent).

`groovy-masker` subdirectory contains:
* `CdcGroovy`, which allows to implement arbitrary column transformations as Groovy scripts.
