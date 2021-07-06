package com.sina.sparrowframework.metric.controller;

import com.google.common.collect.Sets;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

/**
 * 内部调用，不对外
 *
 * @author Ge Hui
 */
@RestController
@RequestMapping(value = "/interxnal")
public class InternalController {

    @PostConstruct
    private void init() {
        DefaultExports.initialize();
    }

    @GetMapping(path = "/metrics", produces = TextFormat.CONTENT_TYPE_004)
    public String metrics(@RequestParam(name = "name[]", required = false) String[] names) throws IOException {
        Set<String> includedNameSet = names == null ? Collections.emptySet() : Sets.newHashSet(names);
        Writer writer = new StringWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(includedNameSet));
        return writer.toString();
    }

}
