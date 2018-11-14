package test.org.adams.opencms.beans

import groovy.json.JsonSlurper

class ModuleFileHandlerTest  extends GroovyTestCase{

    
    
    void testJson() {
        String fileName = 'E:/_DEV/projects/java/gradle/manifestbuilder/src/test/resources/test_meta.json'
        File file = new File(fileName)

        Object my =  new JsonSlurper().parse(file)

        println(  my.properties.each {
            println(it.key + '=' + it.value)
        })

    }

}
