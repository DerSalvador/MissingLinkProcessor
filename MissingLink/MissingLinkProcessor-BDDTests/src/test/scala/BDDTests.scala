import java.io.{FileOutputStream, BufferedInputStream, FileInputStream, File}
import java.lang.Integer
import java.nio.charset.Charset
import java.util
import java.util.Calendar
import ch.bjb.MissingLinkProcessor.LocalPropertiesLocator
import ch.bjb.MissingLinkProcessor.configuration.MissingLinkProcessorModell
import ch.bjb.MissingLinkProcessor.framework.ARAWebserviceWrapper
import ch.bjb.MissingLinkProcessor.model._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.freemarkerunittest.FreemarkerTestRule
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.{GzipUtils, GzipCompressorInputStream}
import org.scalatest.exceptions.TestFailedException
import org.scalatest._
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.util.Random


class BDDTests extends FlatSpec with Matchers {

    // Static class initialization
    val localPropertiesLocator: LocalPropertiesLocator = null
    var charset: Charset = null
    var missinLinkProcessorModell: MissingLinkProcessorModell = null
    val fileList: List[String] = null
    val logger = LoggerFactory.getLogger(classOf[BDDTests])
    val CONFIG_FILE =  "../src/main/resources/MissingLink.DEV.yml"
    //Load configuration from config file
    val yamlFile: File = new File(CONFIG_FILE)
    logger.info(scala.io.Source.fromFile(CONFIG_FILE).mkString)
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory)
    try {
        missinLinkProcessorModell = mapper.readValue(yamlFile, classOf[MissingLinkProcessorModell])
    }
    catch {
        case e: Exception => {
            logger.error("unable to process config file, exiting", e)
            System.exit(1)
        }
    }
    charset = Charset.forName(missinLinkProcessorModell.getCharset)
    val ara = new ARAWebserviceWrapper(missinLinkProcessorModell)

    private val runTemplate1: String = runTemplate(bWithMissing = true)
    runTemplate1 should include regex  "missing"
    private val runTemplate2: String = runTemplate(bWithMissing = false)
    runTemplate2 should not include "missing"
    runTemplate1 should not be equal(runTemplate2)

    "Should raise TestFailedException" {
        an [TestFailedException] should be thrownBy {
            runTemplate1 should be (runTemplate2)
        }
        0
    }

    "Application path should exist " {
        val ara = new ARAWebserviceWrapper(missinLinkProcessorModell)
        val b: Boolean  = ara.checkExistance("Applications/DerSalvador/FWT/abs/13.3.0.BJB.12");
        b shouldBe(true)
        0
    }

    "SSL localhost should be created " {
        val SSL_Host_ID = "TEST/Test-SSH-LOCALHOST_" + Calendar.getInstance().getTimeInMillis
        val ara = new ARAWebserviceWrapper(missinLinkProcessorModell)
        val res = ara.createDirectory(ARAWebserviceWrapper.XLD_CI_INFRASTRUCTURE,"TEST")
        val sl = ara.createSSHLocalHost(SSL_Host_ID).toSet
        for (s <- sl) logger.info(s)
        0
    }

    "SSL host should be created " {
        val ara = new ARAWebserviceWrapper(missinLinkProcessorModell)
        val res = ara.createDirectory(ARAWebserviceWrapper.XLD_CI_INFRASTRUCTURE,"TEST")
        val sshhostname: String = "TEST-SSH-HOST-" + Random.nextInt.toString
        val sshhostCreateCommand: String = ARAWebserviceWrapper.CREATE_UNIX_INFRASTRUCTURE_OVERTHERE_SSHHOST.replace("${SSH_HOSTNAME}", sshhostname).replace("${PATH}","TEST/"+sshhostname)
        val lines = ara.callRESTApiPUT(ARAWebserviceWrapper.ENDPOINT_XLDCI_CREATE_DELETE_QUERY, ARAWebserviceWrapper.XLD_CI_INFRASTRUCTURE, sshhostCreateCommand, "TEST/"+sshhostname)
        for (s <- lines) logger.info(s)
        0
    }

    "Create and Delete Application and Version " {
        val testContainerDir: String = "TESTPackages"
        val res = createDirInApplications(testContainerDir)
        res.toStream.toString() should include regex "token"
        // Create Application first
        val testapp: String = "TestApp"
        val appdir: String = testContainerDir + "/" + testapp
        val resApp = creatApplication(testContainerDir, testapp, appdir)
        resApp.toStream.toString() should include regex "token"
        // Create Deploymentpackage = Version
        val deplpackname: String = "TEST-APP-VERSION-" + Random.nextInt.toString
        val deplpackCreateCommand: String = ARAWebserviceWrapper.CREATE_DEPLOYMENTPACKAGE.replace("${PATH}",appdir).replace("${VERSION}",deplpackname)
        val lines = ara.callRESTApiPUT(ARAWebserviceWrapper.ENDPOINT_XLDCI_CREATE_DELETE_QUERY, ARAWebserviceWrapper.XLD_CI_APPLICATIONS, deplpackCreateCommand, appdir + "/" + deplpackname)
        lines.toStream.toString() should include regex "token"
        for (s <- lines) logger.info(s)
        val linesDel = ara.callRESTApiDELETEApplication(ARAWebserviceWrapper.XLD_CI_APPLICATIONS, testContainerDir,"")
        linesDel.toStream.toString() should include regex "SUCCESSFUL-204"
        for (s <- linesDel) logger.info(s)
        0
    }

    "Undeploy Application " {
        val applicationPath = "CST/NPF/CH/SITCH/CST_SIT_CH"
        ara.unDeployApplication(applicationPath)
        0
    }

    "Change Content of an Tar Gz file in the archive" {
        val sToolsGzip: String = "src/test/scala/resources/DerSalvador-tools-13.3.2-unix.tar.gz"
        val gzipInputStream = new GzipCompressorInputStream(new FileInputStream(new File(sToolsGzip)))
        val uncompressedFilename = unGzip(sToolsGzip)
        val tarArchive = new TarArchiveInputStream(new FileInputStream(new File(uncompressedFilename )))
        var entry = tarArchive.getNextEntry()
        while( tarArchive.getCurrentEntry() != null)
        {
            entry = tarArchive.getCurrentEntry()
            tarArchive.getNextTarEntry()
            logger.info("entry name=" + entry.toString)
        }
        logger.info("toString= " + tarArchive.toString)
        0
    }

    def unGzip (tarGzFilename: String): String = {
        val fin = new FileInputStream(tarGzFilename)
        val in = new BufferedInputStream(fin)
        val uncompressedFilename = GzipUtils.getUncompressedFilename(tarGzFilename)
        val  out = new FileOutputStream(uncompressedFilename)
        val  gzIn = new GzipCompressorInputStream(in)
        val buffer = new Array[Byte](10)
        var n = 0;
        while (-1 != (n = gzIn.read(buffer))) {
            out.write(buffer, 0, n)
        }
        out.close();
        gzIn.close();
        uncompressedFilename
    }

    def creatApplication(testContainerDir: String, testapp: String, appdir: String): List[String] = {
        val appCreateCommand: String = ARAWebserviceWrapper.CREATE_APPLICATION.replace("${PATH}", appdir)
        val resAPP = ara.callRESTApiPUT(ARAWebserviceWrapper.ENDPOINT_XLDCI_CREATE_DELETE_QUERY, ARAWebserviceWrapper.XLD_CI_APPLICATIONS, appCreateCommand, testContainerDir + "/" + testapp)
        val res = resAPP.toSet
        res.toList
    }

    def createDirInApplications(testContainerDir: String) : List[String] = {
        // Create Directory
        val res = ara.createDirectory(ARAWebserviceWrapper.XLD_CI_APPLICATIONS, testContainerDir)
        res.toList
    }

    // Called Test Methods
    def runTemplate(bWithMissing: Boolean = false): String = {


        val template: FreemarkerTestRule = new FreemarkerTestRule("src/test/scala/resources", "deployit-manifest.xml.ftl")
        val deploybales = util.Arrays.asList( new DeployableImpl(1,"groupid1","tag1","filename1","name1","version1",true, util.Arrays.asList(1,2,3,4,5 ),""),
                new DeployableImpl(2,"groupid2","tag2","filename2","name2","version2",true,util.Arrays.asList(6,7,8,9,10 ),""))
        val model: util.HashMap[String,Object] = new util.HashMap[String, Object](10)
        model.put("deployables",deploybales)
        model.put("version","13.3.0")
        if (!bWithMissing)
            model.put("tag","tag")
        model.put("APPLICATION_PATH","DerSalvador/FWT")
        val configfile = new DeployableFile(1,"/var/tmp/configfile.properties","configile.properties",util.Arrays.asList(new Ci("key", new Value("String", "value"))))
        configfile.setTargetFileName("targetConfigFilename")
        configfile.setTargetPath("/u01/app/DerSalvador")
        configfile.setCreateTargetPath(true)
        configfile.setDisplayFileName("Config File Test")
        val asLisFilest: util.List[DeployableFile] = util.Arrays.asList(configfile)
        val configurationSetPerDeployable = util.Arrays.asList(new ConfigurationSet(1,"bddtestconfig",asLisFilest))
        model.put("configurationSetPerDeployable",configurationSetPerDeployable)
        val xml: String = template.xhtmlPageResponseFor(model).asXml()
        // template.template.
        logger.info(xml)
        xml
    }

    // runTemplate (bWithMissing = true) should contain atLeastOneOf ("missing", "Missing")
}



