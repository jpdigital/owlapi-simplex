/*
 * Copyright (C) 2020 Jens Pelzetter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
