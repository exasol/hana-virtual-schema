<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                      | License          |
| ------------------------------- | ---------------- |
| [Virtual Schema Common JDBC][0] | [MIT License][1] |
| [error-reporting-java][2]       | [MIT License][3] |

## Test Dependencies

| Dependency                                 | License                               |
| ------------------------------------------ | ------------------------------------- |
| [Hamcrest][4]                              | [BSD-3-Clause][5]                     |
| [JUnit Jupiter (Aggregator)][6]            | [Eclipse Public License v2.0][7]      |
| [mockito-junit-jupiter][8]                 | [MIT][9]                              |
| [Test containers for Exasol on Docker][10] | [MIT License][11]                     |
| [Testcontainers :: JDBC][12]               | [MIT][13]                             |
| [SAP HANA JDBC Driver][14]                 | [SAP DEVELOPER LICENSE AGREEMENT][15] |
| [Test Database Builder for Java][16]       | [MIT License][17]                     |
| [Matcher for SQL Result Sets][18]          | [MIT License][19]                     |
| [udf-debugging-java][20]                   | [MIT License][21]                     |
| [SLF4J JDK14 Provider][22]                 | [MIT][23]                             |

## Plugin Dependencies

| Dependency                                              | License                                     |
| ------------------------------------------------------- | ------------------------------------------- |
| [SonarQube Scanner for Maven][24]                       | [GNU LGPL 3][25]                            |
| [Apache Maven Toolchains Plugin][26]                    | [Apache-2.0][27]                            |
| [Apache Maven Compiler Plugin][28]                      | [Apache-2.0][27]                            |
| [Apache Maven Enforcer Plugin][29]                      | [Apache-2.0][27]                            |
| [Maven Flatten Plugin][30]                              | [Apache Software License][27]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][31] | [ASL2][32]                                  |
| [Maven Surefire Plugin][33]                             | [Apache-2.0][27]                            |
| [Versions Maven Plugin][34]                             | [Apache License, Version 2.0][27]           |
| [duplicate-finder-maven-plugin Maven Mojo][35]          | [Apache License 2.0][36]                    |
| [Apache Maven Artifact Plugin][37]                      | [Apache-2.0][27]                            |
| [Apache Maven Assembly Plugin][38]                      | [Apache-2.0][27]                            |
| [Apache Maven JAR Plugin][39]                           | [Apache-2.0][27]                            |
| [Artifact reference checker and unifier][40]            | [MIT License][41]                           |
| [Apache Maven Dependency Plugin][42]                    | [Apache-2.0][27]                            |
| [Project Keeper Maven plugin][43]                       | [The MIT License][44]                       |
| [Maven Failsafe Plugin][45]                             | [Apache-2.0][27]                            |
| [JaCoCo :: Maven Plugin][46]                            | [EPL-2.0][47]                               |
| [Quality Summarizer Maven Plugin][48]                   | [MIT License][49]                           |
| [error-code-crawler-maven-plugin][50]                   | [MIT License][51]                           |
| [Git Commit Id Maven Plugin][52]                        | [GNU Lesser General Public License 3.0][53] |
| [Apache Maven Clean Plugin][54]                         | [Apache-2.0][27]                            |
| [Apache Maven Resources Plugin][55]                     | [Apache-2.0][27]                            |
| [Apache Maven Install Plugin][56]                       | [Apache-2.0][27]                            |
| [Apache Maven Site Plugin][57]                          | [Apache-2.0][27]                            |

[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[2]: https://github.com/exasol/error-reporting-java/
[3]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[4]: http://hamcrest.org/JavaHamcrest/
[5]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[6]: https://junit.org/
[7]: https://www.eclipse.org/legal/epl-v20.html
[8]: https://github.com/mockito/mockito
[9]: https://opensource.org/licenses/MIT
[10]: https://github.com/exasol/exasol-testcontainers/
[11]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[12]: https://java.testcontainers.org
[13]: http://opensource.org/licenses/MIT
[14]: https://help.sap.com/viewer/f1b440ded6144a54ada97ff95dac7adf/latest/en-US/434e2962074540e18c802fd478de86d6.html
[15]: https://tools.hana.ondemand.com/developer-license-3_2.txt
[16]: https://github.com/exasol/test-db-builder-java/
[17]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[18]: https://github.com/exasol/hamcrest-resultset-matcher/
[19]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[20]: https://github.com/exasol/udf-debugging-java/
[21]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[22]: http://www.slf4j.org
[23]: https://opensource.org/license/mit
[24]: https://docs.sonarsource.com/sonarqube-server/latest/extension-guide/developing-a-plugin/plugin-basics/sonar-scanner-maven/sonar-maven-plugin/
[25]: http://www.gnu.org/licenses/lgpl.txt
[26]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[27]: https://www.apache.org/licenses/LICENSE-2.0.txt
[28]: https://maven.apache.org/plugins/maven-compiler-plugin/
[29]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[30]: https://www.mojohaus.org/flatten-maven-plugin/
[31]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[32]: http://www.apache.org/licenses/LICENSE-2.0.txt
[33]: https://maven.apache.org/surefire/maven-surefire-plugin/
[34]: https://www.mojohaus.org/versions/versions-maven-plugin/
[35]: https://basepom.github.io/duplicate-finder-maven-plugin
[36]: http://www.apache.org/licenses/LICENSE-2.0.html
[37]: https://maven.apache.org/plugins/maven-artifact-plugin/
[38]: https://maven.apache.org/plugins/maven-assembly-plugin/
[39]: https://maven.apache.org/plugins/maven-jar-plugin/
[40]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[41]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[42]: https://maven.apache.org/plugins/maven-dependency-plugin/
[43]: https://github.com/exasol/project-keeper/
[44]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[45]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[46]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[47]: https://www.eclipse.org/legal/epl-2.0/
[48]: https://github.com/exasol/quality-summarizer-maven-plugin/
[49]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[50]: https://github.com/exasol/error-code-crawler-maven-plugin/
[51]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[52]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[53]: http://www.gnu.org/licenses/lgpl-3.0.txt
[54]: https://maven.apache.org/plugins/maven-clean-plugin/
[55]: https://maven.apache.org/plugins/maven-resources-plugin/
[56]: https://maven.apache.org/plugins/maven-install-plugin/
[57]: https://maven.apache.org/plugins/maven-site-plugin/
