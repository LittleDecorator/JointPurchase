package com.acme;

import com.acme.model.dto.RouteDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nikolay on 22.10.17.
 */
public class RouteTest {

    public static void main(String args[]) throws IOException, ParseException {
        String js = CharStreams.toString(new InputStreamReader(RouteTest.class.getClassLoader().getResourceAsStream("routes.js"), Charsets.UTF_8));
        String partialFiltered = js
                .replaceAll("/\\*.*\\*/", "")
                .replaceAll("\'","\"")
                .replaceAll("\\r\\n+","\r\n")
                .replaceAll("},\\r\\n","},")
                .replaceAll("},\\s+\\{","},,{")
                .replaceAll(" \\r\\n ","");

        int f = partialFiltered.indexOf('[');
        int l = partialFiltered.lastIndexOf(']');
        partialFiltered = partialFiltered.substring(f+1,l).trim();

        String[] blocks = partialFiltered.split(",,");
        for(int i=0; i< blocks.length; i++){
            int viewPos = blocks[i].indexOf("views");
            String pBlock = blocks[i].substring(0,viewPos) + "}";
            pBlock  = pBlock
                    .replaceAll("\\s+","")
                    .replaceAll(",}","}")
                    .replaceAll("name","\"name\"")
                    .replaceAll("url","\"url\"")
                    .replaceAll("parent","\"parent\"");
            blocks[i] = pBlock;
        }
        String result = Arrays.toString(blocks);
        ObjectMapper mapper = new ObjectMapper();
        List<RouteDto> routes = mapper.readValue(result, mapper.getTypeFactory().constructCollectionType(List.class, RouteDto.class));
    }
}
