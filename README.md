# EasyCSV
Easy to use CSV parser for Java powered by codegen

You don't have to specify a type, but get CSV parsed in dataframe that strictly typed!

<br />

## How To Use
<details>
  <summary>1. add this to your to pom.xml</summary>
  
    <dependencies>
        ...
        <!-- EasyCSV dependencies -->
        <dependency>
            <groupId>io.github.tolisso</groupId>
            <artifactId>easy-csv</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
    
    <build>
        ...
        <plugins>
            ...
            <!-- EasyCSV codegen plugin -->
            <plugin>
                <groupId>io.github.tolisso</groupId>
                <artifactId>easy-csv</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate-csv</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <files>
                        <param>
                            <!-- change the values -->
                            <pathToCsv><#YOUR_PATH_TO_CSV#></pathToCsv>
                            <className><#RESULT_CLASS_NAME#></className>
                        </param>
                    </files>
                </configuration>
            </plugin>
            <!-- add EasyCSV generated sources to build -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>3.3.0</version>
                <executions>
                    <execution>
                        <id>add-source</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>${basedir}/target/generated-sources/tolisso-df</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
</details>

2. change `<#YOUR_PATH_TO_CSV#>` to your path to csv.

3. change`<#RESULT_CLASS_NAME#>` to any Java classname you want to csv be parsed.

4. run `mvn clean install`

5. invoke `<#RESULT_CLASS_NAME#>.load()` in code to get csv entity with types!

> In some IDE you also need to add `${basedir}/target/generated-sources/tolisso-df` as generated sources directory, to make new classes visible.

<br />

## Additional Info
  
  Path to csv file may be full or be relative to root of your project, so following paths are legal 
      
    <pathToCsv> aba.csv </pathToCsv>
    <!-- or -->
    <pathToCsv> C:\MyProject\aba.csv </pathToCsv>
  
  <br />
  
  Class Name can contain packages like 
    
    <className>pack1.pack2.Aba<className>

  <br />
      
  You can parse multiple CSVs at once as on the folowing example
    
    <files>
        <param>
            <pathToCsv>aba.csv</pathToCsv>
            <className>Aba</className>
        </param>
        <param>
            <pathToCsv>boba.csv</pathToCsv>
            <className>Boba</className>
        </param>
    </files>

<br />

## Collaborating & Bug Reporting

Looking for any type of your participation in this project, no matter it bug report or feature realization

My telegram @emalko \
Be in touch! :)
