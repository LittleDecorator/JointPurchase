package com.acme.service.impl;

import com.acme.config.MustacheXmlLoader;
import com.acme.exception.TemplateException;
import com.acme.service.TemplateService;
import com.samskivert.mustache.Mustache;
import lombok.NonNull;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Files.getFileExtension;

/**
 * Created by kobzev on 15.12.16.
 */

@Service("templateService")
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private MustacheAutoConfiguration mustacheAutoConfiguration;

	@Value("${spring.mustache.suffix:.html}")
	private String mustacheSuffix;

	@Override
	public
	@NonNull
	String mergeTemplateIntoString(final @NonNull String templateReference, final @NonNull Map<String, Object> model) throws IOException, TemplateException {
		checkArgument(!isNullOrEmpty(templateReference.trim()), "The given templateName is null, empty or blank");
		checkArgument(Objects.equals(getFileExtension(templateReference), expectedTemplateExtension()),
				"Expected a Mustache template file with extension %s, while %s was given. To check " +
				"the default extension look at 'spring.mustache.suffix' in your application.properties file",
				expectedTemplateExtension(), getFileExtension(templateReference));

		try {
			final Reader template = mustacheAutoConfiguration.mustacheTemplateLoader().getTemplate(normalizeTemplateReference(templateReference));
			return mustacheAutoConfiguration.mustacheCompiler(mustacheAutoConfiguration.mustacheTemplateLoader()).compile(template).execute(model);
		} catch (Throwable t) {
			throw new TemplateException(t);
		}
	}

	@Override
	public String mergeXmlTemplateIntoString(final @NonNull String templateReference, final @NonNull Map<String, Object> model) throws TemplateException {
		try {
			MustacheResourceTemplateLoader templateLoader = new MustacheXmlLoader().getXmlLoader();
			final Reader template = templateLoader.getTemplate(normalizeTemplateReference(templateReference, ".xml"));
			return Mustache.compiler().withLoader(templateLoader).compile(template).execute(model);
		} catch (Throwable t) {
			throw new TemplateException(t);
		}
	}

	@Override
	public String expectedTemplateExtension() {
		return mustacheSuffix.replace(".", "");
	}

	private String normalizeTemplateReference(final String templateReference) {
		return normalizeTemplateReference(templateReference, null);
	}

	private String normalizeTemplateReference(final String templateReference, String suffix) {
		final String expectedSuffix = ("." + (Strings.isNullOrEmpty(suffix) ? mustacheSuffix : suffix)).replace("..", ".");
		return templateReference.substring(0, templateReference.lastIndexOf(expectedSuffix));
	}

}
