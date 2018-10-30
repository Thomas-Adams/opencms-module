package test.org.adams.opencms.xml

import org.adams.opencms.beans.Manifest
import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.manifest.ManifestParser

class ManifestParserTest extends GroovyTestCase {


    void testParseExistingManifestFile() {

        OpenCmsExtension extension = new OpenCmsExtension()
        extension.moduleDir ='src/test/resources/vfs/'

        File manifestFile = new File('E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\manifest.xml')

        ManifestParser mfp = new ManifestParser(extension)
        Manifest manifest = mfp.parseExistingManifestFile(manifestFile)

        File moduleDir = new File('E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\')
        String mfdir =  moduleDir.getAbsolutePath();
        mfp.createMetaFiles(manifest, mfdir)

    }

}
