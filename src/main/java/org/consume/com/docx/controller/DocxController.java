package org.consume.com.docx.controller;

import org.consume.com.docx.service.DocxService;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocxController {

    @Value("${files.m}")
    private String models;
    @Value("${files.w}")
    private String ww;
    @Value("${files.j}")
    private String jj;

    @RequestMapping("/add")
    public Integer add(@RequestParam("w") String w, @RequestParam("j") String j, @RequestParam("filename") String filename) {
        try {
//            完整版
            DocxService d = new DocxService();
//            使用模版替换流程
            WordprocessingMLPackage template = d.getTemplate(models);
//            替换内容
            String[] s1 = w.split("]");
            for (String s : s1) {
                d.replacePlaceholder(template, s.split(":")[1], s.split(":")[0]);
            }
            d.writeDocxToStream(template, ww + filename + "(完整版).docx");
//            精简版
            DocxService d1 = new DocxService();
//            使用模版替换流程
            WordprocessingMLPackage template1 = d1.getTemplate(models);
//            替换内容
            String[] s2 = j.split("]");
            for (String s : s2) {
                d1.replacePlaceholder(template1, s.split(":")[1], s.split(":")[0]);
            }
            d1.writeDocxToStream(template1, jj + filename + "(精简版).docx");
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
