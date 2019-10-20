# sde-import-springboot

for build:
need to set JAVA_HOME and MAVEN_HOME
then run  mvn com.github.eirslett:frontend-maven-plugin:1.7.6:install-node-and-npm -DnodeVersion="v10.16.0" in the project folder


for deploy:
the embeded H2 is expecting a directory C:\sdeimport so make it if it doesn't exist

I'm using winsw https://github.com/kohsuke/winsw to package as a windows service. To re-create it, you download the binary.exe and create a xml file in a directory of your choice.

I created a directory on my desktop called "windowsservice" In that directory I place the winsw.exe file, and renamed it to match my project name "sde-import.exe"
I then create an xml file named after the project "sde-import.xml"

The XML looks like:
```xml
  <service>
    <id>sde-import</id>
    <name>sde-import</name>
    <description>This service runs sde-import system.</description>
    <executable>java</executable>
    <arguments> -jar "sde-import.jar"</arguments>
    <logmode>rotate</logmode>
</service>
```

then I drop the built jar file in there and make sure its named the same "sde-import.exe"

