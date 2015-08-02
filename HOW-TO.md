# Updating rules
When updateing the rules make sure you don't:
 * Change the package namespace in the script, it should always be:
  ```
  edu.isistan.carcha.concern
  ```
* Change the script name:
  ```
  cdetector.ruta
  ```
* Avoid the use of Token and use `org.cleartk.token.type.Token`:  
    e.g: `Token{FEATURE("lemma","storage") -> MARK(persistence)};`  
    vs  
    `org.cleartk.token.type.Token{FEATURE("lemma","storage") -> MARK(persistence)};`

## Steps

1. Replace the existing `cdetector.ruta` with the new one, remember to use the full name for Annotations in the script.

2. In `cdetectorEngine.xml` remove the values for `descriptorPaths`, `scriptPaths` and `resourcePaths`.

3. In `cdetectorEngine.xml` replace the: `<typeSystemDescription>` element with:
  ```
  <typeSystemDescription>
    <name>edu.isistan.carcha.concern.cdetectorTypeSystem</name>
    <imports>
    <import name="org.apache.uima.ruta.engine.BasicTypeSystem" />
    <import location="../../../../org/cleartk/ClearTKTypeSystem.xml"/>
    <import location="cdetectorTypeSystem.xml"/>
    </imports>
  </typeSystemDescription>
  ```

4. In `cdetectorTypeSystem.xml` remove all the TypeSystem that are not the ones declared in the script

5. update `src/main/resources/TypeSystem.xml` : It should point to uima ruta, cleartk and typesystem from (4)

6. Run the tests
