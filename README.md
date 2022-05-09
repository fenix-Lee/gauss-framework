# Gauss Engine
高斯引擎是为了优雅代码打造一款的轻量型框架. 

## How to use it
下面用一个小例子来简单介绍如何使用高斯引擎
代码还没有入仓库，需要本地下载并install进本地仓库，然后在引入dependency
```xml
<dependency>
     <groupId>com.hbfintech.gauss</groupId>
     <artifactId>gauss-engine-spring-boot-starter</artifactId>
     <version>1.1.2</version>
</dependency>
```
### BeanFactory
BeanFactory是为了解决频繁把Spring管理的实体类注入到特定的类中，也可以动态的获取某一个特定的类
```java
@Data
@Component
public class Car {
    private String name;

    private int year;
    
    private String owner;
}
```
这里我们申明一个Car的类，如果我们需要这个类就可以
```java
Car myCar = BeanFactory.acquireBean(Car.class);
```
如果需要clone这个类只需要
```java
Car secondCar = BeanFactory.getObjectCopy(Car.class);
```
这里获取的secondCar将不在是Spring管理的单例，而是clone后的car
### BeanMapper
BeanMapper是为了解决对象与对象之间属性的拷贝而写的“冗余”代码。
```java
@Data
@ToString
@Component
public class CarEntity {

    private String name;

    private int year;

    private String ownerName;
}
```
假设我们这里有一个CarEntity，现在我们要把上面Car的属性复制到CarEntity里。
```java
CarEntity carEntity = BeanMapper.mapping(myCar, CarEntity.class);
```
如果你足够细心的话就会发现Car里面有个属性叫"owner"而CarEntity里有个属性叫"ownerName"这两个名字是不匹配的，所以BeanMapper就不知道该怎么处理。现在，我们需要告诉BeanMapper这两个字段需要“匹配"
```java
@Data
@Mapper(target = CarEntity.class)
public class Car {
    private String name;

    private int year;

    @FieldMapping(scope = CarEntity.class, fieldNames = "ownerName")
    private String owner;
}
```
我们先在class上面申明```@Mapper```告诉高斯引擎这个类有字段需要匹配，同时，在字段owner上面申明```@FieldMapping```为了告诉BeanMapper这个字段需要跟哪个类里的哪个字段匹配这样我们就可以
```java
CarEntity carEntity = BeanMapper.mapping(myCar, CarEntity.class);
```
***注意***
- 这里的```@Mapper```不要跟mybatis里的mapper搞混了
- 如果想要用BeanMapper进行对象字段复制需要有get set方法(如果没有，字段将不可复制)
- 这里默认是双向的，只要申明一次就可以相互复制(用上面的例子，CarEntity向Car复制也是可以的)
- 如果目前项目中有些实体类无法添加注解，可以手动向BeanMapper里注册(fieldMap是需要匹配的字段，如果字段全相同可以填null）
```java
BeanMapper.register(source.class, target.class, fieldMap[]);
```
### Module和Factory
很多时候我们都知道要用xxx设计模式,但是用起来却不得要领。
- 面向接口编程，策略模式中的精髓
- 模块该怎么组合在一起?
- 工厂该怎么生产实体对象？  

这里用一个小例子来说明怎么使用Module和Factory
假设我们有申请apply模块以及还款repay模块
首先我们先写一个operation接口来继承Module
```java
public interface Operation extends Module {
}
```
然后我们需要一个工厂并且继承```GaussFactory```
```java
@Creator
public class FintechFactory extends GaussFactory<Operation, Procedure> {


}
```
- 这里我们需要```@Creator```注解，不然高斯引擎不知道这个是工厂自然就不能实例化出来
- 在GaussFactory里的泛型，左边是你需要的模块类型，右边是你要生成的对象类型  

工厂准备好了，下面我们要告诉工厂我们的模块有哪些具体的实现
```java
@Creator
public class FintechFactory extends GaussFactory<Operation, Procedure> {

    @Component
    public static class Apply implements Operation {

        @Override
        public void handle(ModuleProposal proposal) {
            // implement code here
            System.out.println("--- apply ---");
        }
    }

    @Component
    public static class Repay implements Operation {
        @Override
        public void handle(ModuleProposal proposal) {
            // implement code here
            System.out.println("--- repay ----");
        }
    }
}
```
- 这里我们有两个实现类(不一定非要使用内部类，这里主要是为了篇幅原因就用内部类实现)  

有了实现类，我们需要告诉高斯引擎，这两个实现类是由```FintechFactory```来组合,这时我们要
```java
@Creator
public class FintechFactory extends GaussFactory<Operation, Procedure> {


    @Component
    @Chain(factory = FintechFactory.class, sequence = 1)
    public static class Apply implements Operation {

        @Override
        public void handle(ModuleProposal proposal) {
            // implement code here
            System.out.println("--- apply ---");
        }
    }

    @Component
    @Chain(factory = FintechFactory.class, sequence = 2)
    public static class Repay implements Operation {
        @Override
        public void handle(ModuleProposal proposal) {
            // implement code here
            System.out.println("--- repay ----");
        }
    }
}
```
```@Chain```注解是为了告诉高斯引擎这里的模块是由哪个工厂来组合，sequence是表明此模块的位置. 

最后我们要告诉有一个生成实体对象的"函数"
```java
@Creator
public class FintechFactory extends GaussFactory<Operation, Procedure> {

    public static final Function<Operation, Procedure> PROCEDURE_FUNCTION = o -> new Procedure(){{setOperation(o);}};

    @Component
    @Chain(factory = FintechFactory.class, sequence = 1)
    public static class Apply implements Operation {

        @Override
        public void handle(ModuleProposal proposal) {
            // implement code here
            System.out.println("--- apply ---");
        }
    }

    public static class Repay implements Operation {
        @Override
        public void handle(ModuleProposal proposal) {
            // implement code here
            System.out.println("--- repay ----");
        }
    }
}
```
这里的常量```PROCEDURE_FUNCTION```就是为了工厂提供生成实体类的"函数",这样，我们就可以直接生成了```procedure```这个包装类
```java
FintechFactory factory = GaussFactoryGenerator.INSTANCE.getFactory(FintechFactory.class);
List<Procedure> procedures = factory.wrap(FintechFactory.PROCEDURE_FUNCTION);
```
