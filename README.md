# Code-Modify-Tool
- 代码修改工具-（用于重构项目上线灰度发布时需要修改线上项目代码但不能提交，这时需要一个工具修改代码配合jenkins打包）
- 分别使用maven-model、XPath、以及VDT-xml 对pom文件的依赖进行修改测试
- 对比差异，只有VDT-XML 不会改变原先pom.xml的风格
