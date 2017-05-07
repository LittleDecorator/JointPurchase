package com.acme.util;

/*
Copyright (c) 2014 Stijn Van Bael
 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:
 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>Simple template for matching and replacing named parameters.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     StringTemplate template = new StringTemplate("Hello {name}!");
 *
 *     Map&lt;String, String> map = template.parse("Hello John!"); // Yields {name="John"}
 *
 *     map.put("name", "Ellen");
 *     String formatted = template.format(map); // Yields "Hello Ellen!"
 * </pre>
 */
public class StringTemplate {
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([^/?}]+)\\}");

    private final Pattern pattern;
    private final List<String> parameters;
    private final String template;

    public StringTemplate(String template) {
        this.template = template;
        this.parameters = new ArrayList<>();
        Matcher matcher = PARAMETER_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer("\\Q");
        while (matcher.find()) {
            matcher.appendReplacement(sb, "\\\\E(?<$1>[^/?]+)\\\\Q");
            parameters.add(matcher.group(1));
        }
        matcher.appendTail(sb);
        sb.append("\\E");
        this.pattern = Pattern.compile(sb.toString());
    }

    public Map<String, String> parse(String string) {
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("String '" + string + "' does not match template '" + template + "'.");
        }
        Map<String, String> results = new HashMap<>();
        for (String parameter : parameters) {
            results.put(parameter, matcher.group(parameter));
        }
        return results;
    }

    @Override
    public String toString() {
        return template;
    }

    public boolean matches(String string) {
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public String format(Map<String, String> parameters) {
        return format(parameters::get);
    }

    public String format(Function<String, String> replacement) {
        Matcher matcher = PARAMETER_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacement.apply(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}