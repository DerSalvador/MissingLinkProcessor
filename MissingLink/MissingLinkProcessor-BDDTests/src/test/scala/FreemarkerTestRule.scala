package com.github.freemarkerunittest

import java.util.Arrays.asList
import org.xmlmatchers.transform.XmlConverters.the
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.net.URL
import java.util.Locale
import javax.xml.transform.Source
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.PageCreator
import com.gargoylesoftware.htmlunit.StringWebResponse
import com.gargoylesoftware.htmlunit.TextPage
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebResponse
import com.gargoylesoftware.htmlunit.WebResponseData
import com.gargoylesoftware.htmlunit.WebWindow
import com.gargoylesoftware.htmlunit.html.HTMLParser
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.XHtmlPage
import com.gargoylesoftware.htmlunit.util.NameValuePair
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version

class FreemarkerTestRule extends TestRule {
  private var templateName: String = null
  private var templatePath: String = null
  var template: Template = null
  private var textContentHeader: NameValuePair = new NameValuePair("Content-Type", "text/")
  private var anyUrl: String = "http://localhost"
  private var anyWindow: WebWindow = new WebClient().getCurrentWindow
  private var pageCreator: PageCreator = new WebClient().getPageCreator

  def this(templatePath: String, templateName: String) {
    this()
    this.templatePath = templatePath
    this.templateName = templateName
    loadTemplate

  }

  def apply(base: Statement, description: Description): Statement = {
    new Statement {
      override def evaluate(): Unit = {
        loadTemplate
        base.evaluate()
      }
    }
  }

  @throws(classOf[Exception])
  private def loadTemplate {
    val cfg: Configuration = configureTemplateLoader
    template = cfg.getTemplate(templateName)
  }

  @throws(classOf[Exception])
  private def configureTemplateLoader: Configuration = {
    val cfg: Configuration = new Configuration
    cfg.setDirectoryForTemplateLoading(new File(templatePath))
    cfg.setIncompatibleImprovements(new Version(2, 3, 20))
    cfg.setDefaultEncoding("UTF-8")
    cfg.setLocale(Locale.UK)
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
    return cfg
  }

  @throws(classOf[Exception])
  def writerResponseFor(dataModel: AnyRef): Writer = {
    val out: Writer = new StringWriter
    template.process(dataModel, out)
    return out
  }

  @throws(classOf[Exception])
  def stringResponseFor(dataModel: AnyRef): String = {
    return writerResponseFor(dataModel).toString
  }

  @throws(classOf[Exception])
  def htmlPageResponseFor(dataModel: AnyRef): HtmlPage = {
    return HTMLParser.parseHtml(webResponseFor(dataModel), anyWindow)
  }

  @throws(classOf[Exception])
  def xhtmlPageResponseFor(dataModel: AnyRef): XHtmlPage = {
    return HTMLParser.parseXHtml(webResponseFor(dataModel), anyWindow)
  }

  @throws(classOf[Exception])
  private def webResponseFor(dataModel: AnyRef): StringWebResponse = {
    return new StringWebResponse(stringResponseFor(dataModel), new URL(anyUrl))
  }

  @throws(classOf[Exception])
  def textPageResponseFor(dataModel: AnyRef): TextPage = {
    val responseData: WebResponseData = new WebResponseData(stringResponseFor(dataModel).getBytes, 200, "OK", asList(textContentHeader))
    val response: WebResponse = new WebResponse(responseData, new URL(anyUrl), HttpMethod.GET, 10)
    return pageCreator.createPage(response, anyWindow).asInstanceOf[TextPage]
  }

  @throws(classOf[Exception])
  def xmlResponseFor(dataModel: AnyRef): Source = {
    return the(htmlPageResponseFor(dataModel).asXml)
  }
}
