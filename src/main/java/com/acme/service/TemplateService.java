package com.acme.service;

import com.acme.exception.TemplateException;
import lombok.NonNull;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kobzev on 15.12.16.
 *
 * Defines a service for processing templates with a template engine
 */
public interface TemplateService {

	/**
	 * Call the templateReference engine to process the given templateReference with the given model object.
	 *
	 * @param templateReference a templateReference file to be processed. The name must be complaint with the position
	 *                          of the template your your resources folder. Usually, files in {@code resources/templates}
	 *                          are resolved by passing the sole file name. Subfolders of {@code resources/templates} must
	 *                          be explicitly reported. E.g., a template {@code template.html} under the folder
	 *                          {@code resources/templates/module} must be reported as {@code "module/template"}
	 * @param model             the model object to process the templateReference
	 * @return a processed template (an HTML, or XML, or wathever the templateReference engine can process)
	 * @throws IOException       thrown if the templateReference file is not found or cannot be accessed
	 * @throws TemplateException if the templateReference cannot be processed with the given model object
	 */
	String mergeTemplateIntoString(String templateReference, Map<String, Object> model) throws IOException, TemplateException;

	String mergeXmlTemplateIntoString(final @NonNull String templateReference, final @NonNull Map<String, Object> model) throws TemplateException;

	String expectedTemplateExtension();

}
