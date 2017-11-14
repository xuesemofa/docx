package org.consume.com.index;


import org.consume.com.util.redirect.RedirectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

@RestController
public class IndexController {
    @Value("${files.w}")
    private String ww;
    @Value("${files.j}")
    private String jj;

    @RequestMapping("/")
    public ModelAndView init() {
        return new ModelAndView(RedirectUtil.getRddirect() + "/index");
    }

    @RequestMapping("/index")
    public ModelAndView init2() {
        File ws = new File(ww);
        File[] files1 = ws.listFiles();
        File js = new File(jj);
        File[] files2 = js.listFiles();
        return new ModelAndView("/index")
                .addObject("w", files1)
                .addObject("wsize", files1.length)
                .addObject("ww", ww)
                .addObject("j", files2)
                .addObject("jsize", files2.length)
                .addObject("jj", jj);
    }

}
