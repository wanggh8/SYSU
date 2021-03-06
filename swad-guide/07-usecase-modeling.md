---
layout: default
title: 用例建模
---

# 用例建模 - 业务建模方法

练习材料: [Asg-RH.pdf](material/Asg_RH.pdf)

## 1、业务流程（BP）与业务流程重构（BPR）

几乎所有的企业部门办公室都有以下一些图表：

* 组织与岗位职责表
* 业务流程图
* 项目/生产计划进度图（或燃尽表）

这些图表的重要性不言而喻，企业软件项目的一个重要目标就是用程序实现（并改进）部分业务流程，取代人工，提高劳动生产率。人们常担心机器取代人，但几十年数字化实践表明，IT技术应用没有减少的工作岗位，而是推动了人员业务转型，创造了更多岗位。因此，信息系统软件的一个重要职能就是业务自动化，认识业务流程是软件生产的关键步骤！

### 1.1 业务流程

> A **business process** or **business method** is a collection of related, structured activities or tasks by people or equipment that in a specific sequence produces a **service** or **product** (serves a particular business goal) for a particular customer or customers.

* 流程是一种程序
* 流程与程序采用同样的可视化工具（如：流程图、UML 活动图）
* 业务流程与用例存在天然的联系

相关课程：业务流程自动化（BPM 或 Workflow 或 ERM）

业务流程的一些基本特点：

* 从无到有，从简单到复杂，从特殊到标准化
* 从简单服务到分类服务，从统一服务到个性化（柔性/按需）服务，从人工服务到智能服务

案例：[供应链详细业务流程（采购业务）](https://wenku.baidu.com/view/45071421050876323012129a.html)

案例：请打开京东商城客户服务页面！

* 哇塞，这么复杂！这些用例怎么想出来的？

案例：请打开淘宝聚划算

* 数据服务是软件运维的核心工作，是互联网软件产品变现的基础。数据服务产品的变化与发展，展现了业务的进化的过程！

### 1.2 业务流程重构（Business Process Reengineering）

业务过程自动化是个复杂的、长周期的优化过程，常常伴随着业务过程重构。

如何应对重构的挑战，内容超出本课程范围，请参考：[供应链业务流程重构](https://wenku.baidu.com/view/6224196f4a35eefdc8d376eeaeaad1f34793116e.html)


## 2、业务建模与应用程序开发

VS2015 案例研究：[场景：使用可视化和建模更改设计](https://docs.microsoft.com/zh-cn/visualstudio/modeling/scenario-change-your-design-using-visualization-and-modeling?view=vs-2015)

* 基本步骤
    - 了解系统在业务过程中的作用
        - 使用 UML 活动图建模业务过程
        - 将业务过程转化为 UML 用例图
    - 了解业务实体（数据）模型
        - 使用 UML 类图构建领域模型
    - 跟踪包结构（代码逻辑架构）
    - 跟踪组件结构与接口（代码的功能组合架构）
    - 描述交互序列（具体类协作时序）
* 变化场景
    - 在 dinner now 中 Create Reviews
    - 使用新的支付系统


## 3、业务建模练习 - 活动图

### 3.1 UML 活动图

* [UML 活动图参考](https://docs.microsoft.com/zh-cn/visualstudio/modeling/uml-activity-diagrams-reference?view=vs-2015)
* [UML 活动图指南](https://docs.microsoft.com/zh-cn/visualstudio/modeling/uml-activity-diagrams-guidelines?view=vs-2015)

### 3.2 UML 多泳道图实践

多泳道图常用于描述 2 个以上组织、或角色、或系统之间的交互业务流程。例如：描述利用投递柜，投递员完成包裹投递到客户的业务过程。因此，这个过程至少需要三个泳道，投递柜系统、投递员、和收件人。

练习：请描述以下不同场景的投递业务过程

1. x科技公司发明了投递柜，它们自建了投递柜以及远程控制系统。注册的投递员在推广期免费使用投递柜。由于缺乏资源，仅能使用y移动平台向客户发送短信通知。
2. 随着产品推广，x公司与各大快递z公司达成协议。x公司在快递柜上添加了二维码扫描装置，z公司的快递员不仅可在快递柜上登陆（由z公司提供认证服务），且可扫描快递单号，投递入柜后自动由z公司发短信给客户。客户取件后，自动发送给z公司投递完成。
3. x公司进一步优化服务，开发了微信小程序实现扫码取快递。如果用户关注了该公司公众号，直接通过过公众号推送给用户取件码等信息。不再发送短信。

**备注：** 关于z公司投递员认证过程。一般采用用户登陆z公司，z公司用私钥签名的token（二维码包含公司名，用户ID，有效期等）；x公司扫描该二维码，用对应公钥验证该token。

请根据业务模型，思考以下问题：

* 根据最后一个业务流程，给出快递柜系统的用例图模型。用不同颜色给出新增加或修改的用例或子用例
* 根据微软提供的案例，通过跟踪需求变化，开发团队如何有效组织代码开发？

### 3.3 UML 活动图

练习：用活动图描述 ATM 机取款流程

* 第一步描述流程的  happy path 
* 第二步描述可能的分支业务

请根据业务模型思考以下问题：

* 如果考虑 ATM 钱箱数额、假币鉴别、记录每张人民币编号、网络异常等因素，使用活动图建模这些细节是否合适？
* UML活动图 与 用例文档 之间的关系？

练习：根据订旅馆建模文档，给出 make reservation 用例的活动图。

## 4、项目文档提交！

* 需求规格说明书
    - 1、 用例图模型与业务过程
    - 2、 用例描述
        - 至少一个用例描述是正式格式，且配有一个或多个流程图
* 设计说明书
    - 1、 UI界面原型
* 生产规范与指南
    - XX 代码规范
* X2 KanBan

## 5、作业

使用 **UMLet** 建模：

* 1、根据订旅馆建模文档，[Asg-RH.pdf](material/Asg_RH.pdf)：
    - 绘制用例图模型（到子用例）
    - 给出 make reservation 用例的活动图
* 2、根据课程练习“投递员使用投递箱给收件人快递包裹”的业务场景
    - 分别用多泳道图建模三个场景的业务过程
    - 根据上述流程，给出快递柜系统最终的用例图模型
        - 用正常色彩表示第一个业务流程反映的用例
        - 用绿色背景表述第二个业务场景添加或修改的用例，以及支持 Actor
        - 用黄色背景表述第三个业务场景添加或修改的用例，以及支持 Actor










