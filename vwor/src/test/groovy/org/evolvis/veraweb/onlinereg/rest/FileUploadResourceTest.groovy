package org.evolvis.veraweb.onlinereg.rest

import org.apache.commons.io.FileUtils
import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class FileUploadResourceTest extends Specification {

    def propertiesReaderMock = Mock(VworPropertiesReader)
    def imageFile = new File("src/test/resources/imageData.txt")
    def IMAGE_DATA_STRING = new FileUtils().readFileToString(imageFile)

    void testSaveImageIntoDataSystem() {
        setup:
            def resource = new FileUploadResource(vworPropertiesReader: propertiesReaderMock)

        when:
            resource.saveImageIntoDataSystem(IMAGE_DATA_STRING, "jpg", "09c3fdaf-8599-466c-b77e-435720528dcf")

        then:
            1 * propertiesReaderMock.getProperty("filesLocation")
    }

    @Ignore('java.lang.OutOfMemoryError: Java heap space')
    void testGetImageByUUID() {
        setup:
            def resource = new FileUploadResource(vworPropertiesReader: propertiesReaderMock)
            resource.saveImageIntoDataSystem(IMAGE_DATA_STRING, "jpg", "19c3fdaf-8599-466c-b77e-435720528dcf")

        when:
            def image = resource.getImageByUUID("19c3fdaf-8599-466c-b77e-435720528dcf")

        then:
            assert image == "data:image/jpg;base64," + IMAGE_DATA_STRING
    }
}
