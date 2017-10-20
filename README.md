# PdfBuilder
Android端使用图片生成PDF文件

## 一、应用场景
从本地选择图片生成pdf文件，由于Android本身并没有对pdf的支持，这里选择使用一个第三方的库来达成需求。

## 二、库的选择

### 2.1 当前主流的库
在众多Java语言编写的PDf库中，对Android有支持且有一定用户量的的有：iText、Qoppa qPDF工具包、PDFJet。

### 2.2 三个库的对比如下：

\ | iText | Qoppa|PDFJet
----|------|----|----
应用文件大小|	1.52MB|	0.93MB|	0.67MB
时间消耗|	3.7ms|	39ms|	51.3ms
平均CPU利用率|	29％|	77.9％|	86.8％
修改PDF|	是|	是|	没有
加密|	是|	是|	没有
形式字段函数|	是|	是|	没有
文本提取|	是|	是|	没有
将PDF转换为图像|	没有|	是|	没有
开源|	是|	没有|	是
书可用|	是|	没有|	没有
论坛，邮件列表|	是|	没有|	没有

### 2.3 选型
鉴于性能和开源，决定选择iText作为此次接入的PDF库。

## 三、iText库接入

### 3.1 资源说明

#### 3.1.1 下载链接
https://github.com/itext/itextpdf/tree/itextg

#### 3.1.2 下载说明
If you want to use iText on Android or the Google App Engine, you need to use iTextG. iTextG is almost identical to iText, except that it only uses classes that are white-listed by Google. All references to java.awt, javax.nio and other "forbidden" packages have been removed.(在Android上使用iText，需要使用iTextG。iTextG与iText基本相同，只是替换掉了java.awt，javax.nio等Android上不支持的包。)

#### 3.1.3 混淆说明
```java
# itext
-dontwarn com.itextpdf.**
-keep class com.itextpdf.** {*;}
```

### 3.2 图片生成pdf方法
#### 3.2.1 设置pdf每页的背景
```java
public class PdfBackground extends PdfPageEventHelper {
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        //设置pdf背景色为白色
        PdfContentByte canvas = writer.getDirectContentUnder();
        Rectangle rect = document.getPageSize();
        canvas.setColorFill(BaseColor.WHITE);
        canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        canvas.fill();

        //设置pdf页面内间距
        PdfContentByte canvasBorder = writer.getDirectContent();
        Rectangle rectBorder = document.getPageSize();
        rectBorder.setBorder(Rectangle.BOX);
        rectBorder.setBorderWidth(BORDER_WIDTH);
        rectBorder.setBorderColor(BaseColor.WHITE);
        rectBorder.setUseVariableBorders(true);
        canvasBorder.rectangle(rectBorder);
    }
}

```
#### 3.2.2 根据图片Uri生成pdf
```java
/**
 * 根据图片生成PDF
 *
 * @param pdfPath 生成的PDF文件的路径
 * @param imagePathList 待生成PDF文件的图片集合
 * @throws IOException 可能出现的IO操作异常
 * @throws DocumentException PDF生成异常
 */
private void createPdf(String pdfPath, List<String> imagePathList) throws IOException, DocumentException {
    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

    //设置pdf背景
    PdfBackground event = new PdfBackground();
    writer.setPageEvent(event);

    document.open();
    for (int i = 0; i < imagePathList.size(); i++) {
        document.newPage();
        Image img = Image.getInstance(imagePathList.get(i));
        //设置图片缩放到A4纸的大小
        img.scaleToFit(PageSize.A4.getWidth() - BORDER_WIDTH * 2, PageSize.A4.getHeight() - BORDER_WIDTH * 2);
        //设置图片的显示位置（居中）
        img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
        document.add(img);
    }
    document.close();
}
```

## 四、参考文献

### 4.1 iText官方网站：
http://itextpdf.com/

### 4.2 iText5图片处理相关examples：
http://developers.itextpdf.com/examples/image-examples-itext5

### 4.3 Stefan Fenz的博客地址：
http://stefan.fenz.at/creating-pdfs-on-android-an-evaluation/
