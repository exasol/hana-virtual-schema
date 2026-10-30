# Virtual Schema for SAP Hana 4.0.2, released 2026-10-30

Code name: Fixed vulnerabilities CVE-2026-86231, CVE-2026-116486, CVE-2026-110257, CVE-2026-110258, CVE-2026-110259, CVE-2026-110260, CVE-2026-110261, CVE-2026-110263, CVE-2026-110262, CVE-2026-110270

## Summary

This release fixes the following 10 vulnerabilities:

### CVE-2026-86231 (CWE-298) in dependency `com.github.mwiede:jsch:jar:2.28.5:compile`

A security flaw has been discovered in mwiede jsch up to 2.28.5. Affected is the function getRevokedKeys of the file src/main/java/com/jcraft/jsch/KnownHosts.java. Performing a manipulation of the argument known_hosts results in improper check for certificate revocation. The attack is possible to be carried out remotely. The attack is considered to have high complexity. The exploitability is told to be difficult. The exploit has been released to the public and may be used for attacks. Upgrading to version 2.28.6 is able to address this issue. The patch is named 194a2f76a5c0f1c3f778565be3fd66bcafc42d23. You should upgrade the affected component.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-86231?component-type=maven&component-name=com.github.mwiede%2Fjsch&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-86231
* https://github.com/mwiede/jsch/issues/1091
* https://github.com/mwiede/jsch/pull/1098
* https://github.com/mwiede/jsch/releases/tag/jsch-2.28.6

### CVE-2026-116486 (CWE-386) in dependency `org.objenesis:objenesis:jar:3.3:compile`

Objenesis before 3.6 contains a type confusion vulnerability that allows code in one class loader to substitute objects into another by exploiting instantiator caching keyed on class name. When a shared ObjenesisStd instance caches instantiators by clazz.getName() in ObjenesisBase.getInstantiatorOf(), same-named classes collide, causing newInstance() to return wrong-class objects and ClassCastException failures.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-116486?component-type=maven&component-name=org.objenesis%2Fobjenesis&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-116486
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/116xxx/CVE-2026-116486.json

### CVE-2026-110257 (CWE-789) in dependency `org.ow2.asm:asm:jar:9.10.1:compile`

OW2 ASM 5.0 before 9.11 contains an excessive memory allocation vulnerability in ClassReader.readBootstrapMethodsAttribute() that sizes an int array from num_bootstrap_methods without validating attribute_length. Attackers can supply a crafted class file of about 100 bytes that forces roughly 2 GB allocation, causing an OutOfMemoryError that crashes the JVM or consuming thread.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110257?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110257
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110257.json

### CVE-2026-110258 (CWE-789) in dependency `org.ow2.asm:asm:jar:9.10.1:compile`

OW2 ASM 3.0 before 9.11 contains an excessive memory allocation vulnerability in Label.addSubroutineRetSuccessors() that fails to enforce the single-RET-per-JSR rule. Attackers can supply a crafted invalid class file with multiple RET instructions for one JSR, triggering a roughly 2 GB allocation that crashes the JVM.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110258?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110258
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110258.json

### CVE-2026-110259 (CWE-674) in dependency `org.ow2.asm:asm:jar:9.10.1:compile`

OW2 ASM 3.0 before 9.11 contains an uncontrolled recursion vulnerability in ClassWriter.replaceAsmInstructions() that allows attackers to cause denial of service through crafted class files. Attackers can supply a class with a large method containing many long forward jumps, triggering unbounded toByteArray() recursion and a StackOverflowError during transformation.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110259?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110259
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110259.json

### CVE-2026-110260 (CWE-407) in dependency `org.ow2.asm:asm:jar:9.10.1:compile`

OW2 ASM 3.0 before 9.11 contains a quadratic memory allocation vulnerability in MethodWriter.computeMaxStackAndLocal() and computeAllFrames() when using COMPUTE_MAXS or COMPUTE_FRAMES. Attackers can supply a crafted class file with thousands of overlapping exception handlers, causing excessive Edge allocations and an OutOfMemoryError that crashes the JVM.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110260?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110260
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110260.json

### CVE-2026-110261 (CWE-407) in dependency `org.ow2.asm:asm:jar:9.10.1:compile`

OW2 ASM 3.0 before 9.11 contains a denial of service vulnerability that allows attackers to exhaust CPU by supplying crafted class files processed with ClassWriter.COMPUTE_FRAMES. Attackers can craft methods that make the unbounded MethodWriter.computeAllFrames() fix point algorithm converge slowly, tying up transforming threads for unbounded time.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110261?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110261
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110261.json

### CVE-2026-110263 (CWE-674) in dependency `org.ow2.asm:asm:jar:9.10.1:compile`

OW2 ASM 3.0 before 9.11 contains an uncontrolled recursion vulnerability in SignatureReader.parseType() that allows attackers to exhaust the stack via deeply nested generic type signatures. Attackers can supply a crafted class file with a long Signature attribute containing many nested type arguments to trigger a StackOverflowError during class transformation, causing denial of service.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110263?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110263
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110263.json

### CVE-2026-110262 (CWE-770) in dependency `org.ow2.asm:asm-commons:jar:9.10.1:compile`

OW2 ASM 3.1 before 9.11 contains an excessive memory allocation vulnerability in the asm-commons AdviceAdapter that allows attackers to exhaust memory by supplying crafted class files. Attackers can craft bytecode with many forward jump targets in constructors, causing unbounded stack copies in forwardJumpStackFrames and allocation of hundreds of megabytes, leading to denial of service.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110262?component-type=maven&component-name=org.ow2.asm%2Fasm-commons&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110262
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110262.json

### CVE-2026-110270 (CWE-407) in dependency `org.ow2.asm:asm-commons:jar:9.10.1:compile`

OW2 ASM 3.0 before 9.11 contains an uncontrolled memory allocation vulnerability in the asm-commons JSRInlinerAdapter that inlines JSR/RET subroutines without memory or work limits. Attackers can supply a crafted class file with nested subroutines to force O(2^n) memory allocation, causing an OutOfMemoryError that crashes the JVM.

#### References

* https://guide.sonatype.com/vulnerability/CVE-2026-110270?component-type=maven&component-name=org.ow2.asm%2Fasm-commons&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110270
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110270.json

## Security

* #51: Fixed vulnerability CVE-2026-86231 in dependency `com.github.mwiede:jsch:jar:2.28.5:compile`
* #62: Fixed vulnerability CVE-2026-116486 in dependency `org.objenesis:objenesis:jar:3.3:compile`
* #55: Fixed vulnerability CVE-2026-110257 in dependency `org.ow2.asm:asm:jar:9.10.1:compile`
* #56: Fixed vulnerability CVE-2026-110258 in dependency `org.ow2.asm:asm:jar:9.10.1:compile`
* #57: Fixed vulnerability CVE-2026-110259 in dependency `org.ow2.asm:asm:jar:9.10.1:compile`
* #58: Fixed vulnerability CVE-2026-110260 in dependency `org.ow2.asm:asm:jar:9.10.1:compile`
* #59: Fixed vulnerability CVE-2026-110261 in dependency `org.ow2.asm:asm:jar:9.10.1:compile`
* #60: Fixed vulnerability CVE-2026-110263 in dependency `org.ow2.asm:asm:jar:9.10.1:compile`
* #53: Fixed vulnerability CVE-2026-110262 in dependency `org.ow2.asm:asm-commons:jar:9.10.1:compile`
* #54: Fixed vulnerability CVE-2026-110270 in dependency `org.ow2.asm:asm-commons:jar:9.10.1:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:8.0.1` to `8.0.2`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.3` to `1.7.4`
* Updated `com.exasol:test-db-builder-java:4.0.2` to `4.0.3`
* Updated `com.exasol:udf-debugging-java:0.6.20` to `0.6.22`
* Updated `com.sap.cloud.db.jdbc:ngdbc:2.29.7` to `2.30.11`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.18` to `2.0.20`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:null` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.15` to `0.8.16`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
