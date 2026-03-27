rm -r .\docs;
mvn -pl core javadoc:javadoc;
mv .\core\target\site\apidocs .\docs;
