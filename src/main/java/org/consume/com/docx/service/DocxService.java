package org.consume.com.docx.service;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * http://blog.csdn.net/zhyh1986/article/details/8766628
 */
public class DocxService {

    public void load(String filePath, String fileName) throws Exception {
//        创建
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        wordMLPackage.getMainDocumentPart().addParagraphOfText("Hello Word!");
//        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "Hello Word!");
        wordMLPackage.save(new java.io.File(filePath + fileName));
    }

    //加载模版

    /**
     * 首先，我们创建一个可用作模版的简单的word文档。对于此只需打开Word，
     * 创建新文档然后保存为template.docx，这就是我们将要用于添加内容的word文档。
     * 我们需要做的第一件事是使用docx4j将这个文档加载进来，你可以使用下面的几行代码做这件事：
     *
     * @param name
     * @return
     * @throws Docx4JException
     * @throws FileNotFoundException
     */
    public WordprocessingMLPackage getTemplate(String name) throws Docx4JException, FileNotFoundException {
        WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(name)));
        return template;
    }

    //工具类

    /**
     * 这样会返回一个表示完整的空白（在此时）文档Java对象。
     * 现在我们可以使用Docx4J API添加、删除以及更新这个word文档的内容，
     * Docx4J有一些你可以用于遍历该文档的工具类。
     * 我自己写了几个助手方法使查找指定占位符并用真实内容进行替换的操作变地很简单。
     * 让我们来看一下其中的一个，这个计算是几个JAXB计算的包装器，
     * 允许你针对一个特定的类来搜索指定元素以及它所有的孩子，
     * 例如，你可以用它获取文档中所有的表格、表格中所有的行以及其它类似的操作。
     *
     * @param obj
     * @param toSearch
     * @return
     */
    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }

        }
        return result;
    }

    //替换

    /**
     * 没什么复杂的，但真的很有帮助。让我们看一下怎样使用这个方法。
     * 在这个例子中我们只是使用不同的值来替换简单的文本占位符，例如你动态设置一个文档的标题。
     * 首先，在前面创建的模版文档中添加一个自定义占位符，我使用SJ_EX1作为占位符，我们将要用name参数来替换这个值。
     * 在docx4j中基本的文本元素用org.docx4j.wml.Text类来表示，替换这个简单的占位符我们需要做的就是调用这个方法：
     *
     * @param template
     * @param name
     * @param placeholder
     */
    public void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder) {
        List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

        for (Object text : texts) {
            Text textElement = (Text) text;
            if (textElement.getValue().equals(placeholder)) {
                textElement.setValue(name);
            }
        }
    }

    //    写入新的文件

    /**
     * 这会在文档中查找所有的Text元素，
     * 并且与占位符匹配的Text都将被我们指定的值替换，
     * 现在我们需要做的仅是将这个文档写回一个文件中。
     *
     * @param template
     * @param target
     * @throws IOException
     * @throws Docx4JException
     */
    public void writeDocxToStream(WordprocessingMLPackage template, String target) throws IOException, Docx4JException {
        File f = new File(target);
        template.save(f);
    }

    /**
     * 创建包含图片的内容
     * <p>
     * //            添加图片
     * //            template.getMainDocumentPart().getContent().add(
     * //                    newImage(template, template.getMainDocumentPart(), "D:\\123.png"));
     *
     * @param word
     * @param sourcePart
     * @param imageFilePath
     * @return
     * @throws Exception
     */
    private static final org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();

    public static P newImage(WordprocessingMLPackage word,
                             Part sourcePart,
                             String imageFilePath) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
                .createImagePart(word, sourcePart, new File(imageFilePath));
        //随机数ID
        int id = (int) (Math.random() * 10000);
        //这里的id不重复即可
        Inline inline = imagePart.createImageInline("image", "image", id, id * 2, false);

        Drawing drawing = factory.createDrawing();
        drawing.getAnchorOrInline().add(inline);

        R r = factory.createR();
        r.getContent().add(drawing);

        P p = factory.createP();
        p.getContent().add(r);

        return p;
    }
}
