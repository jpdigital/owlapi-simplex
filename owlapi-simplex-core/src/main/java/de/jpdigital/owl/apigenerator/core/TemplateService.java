/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jpdigital.owl.apigenerator.core;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TemplateService {

    private final Configuration configuration = new Configuration(
        Configuration.VERSION_2_3_29
    );

    private final static TemplateService INSTANCE = new TemplateService();

    private TemplateService() {
        configuration.setClassForTemplateLoading(getClass(), "/templates");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(
            TemplateExceptionHandler.RETHROW_HANDLER
        );
    }

    public static final TemplateService getTemplateService() {
        return INSTANCE;
    }

    public String processTemplate(
        final String templateName, final Map<String, Object> data
    ) {
        try {
            final Template template = configuration.getTemplate(templateName);
            final StringWriter writer = new StringWriter();
            
            template.process(data, writer);
            
            return writer.toString();
        } catch(IOException | TemplateException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

}
