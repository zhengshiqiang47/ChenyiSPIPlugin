### 基于现实需求开发一个自动生成格式化代码插件。

需求：进入公司后发现，在HSF(内部一个 分布式服务框架 ) 服务间会频繁的进行跨应用服务调用，由于规范性，每个服务在调用前 都要封装一层SPI代码。用于AOP拦截入参和结果，以及对异常的捕捉。而代码往往就是 Service类的一层封装，代码类似如下：

![](https://cdn.nlark.com/yuque/0/2018/png/152699/1541420092970-acd15d85-109f-4e5f-8fe3-7b5eb33ad908.png)

xxxSPI 和 xxxService方法名，参数都一样，返回值只要在xxxService返回值基础上getModule拆一下，开发人员同时需要创建 xxxServiceSPI和xxxServiceImplSPI，然后挨个把方法调用一遍，设置拦截器，我觉得完全可以将这部分时间省掉。 目前已完成大部分编码，GitHub地址：https://github.com/zhengshiqiang47/ChenyiSPIPlugin