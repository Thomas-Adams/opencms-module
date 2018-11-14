package test.org.adams.opencms

class TestUtils extends GroovyTestCase {

    public final static String TEST_RESOURCES_BASEPATH = ""

    void testFilePath() {

        File f = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs\\manifest.xml')
        File d = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs\\')
        File d2 = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs\\system\\modules\\org.adams.opencms.synchron.module\\schemas')





        println('Absolute path: ' + f.getAbsolutePath())
        println('Absolute path: ' + d.getAbsolutePath())
        println('relative path: ' + d2.toPath().toString().substring(d.toPath().toString().length() + 1))

    }

}
