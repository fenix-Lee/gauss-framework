# Gauss Engine
高斯引擎是为了优雅代码打造一款的轻量型框架. 
## Advanced Usage
这一篇是介绍高斯引擎的高阶用法
前面一篇没有看到的可以参考[Gauss Engine Reference](https://github.com/fenix-Lee/gauss-framework)  
### BeanFactory
在上一篇中，简单介绍了clone这个方法。下面问题来了，如果我想进行定制化clone，那要怎么办呢？这里我们也需要告诉告诉高斯引擎我们这个类要拷贝的话需要定制化。
```java
@Data
@ToString
@Component
public class CarEntity implements Cloneable{

    private String name;

    private int year;

    private String ownerName;

    @Override
    protected Object clone() {
        System.out.println("---- start -----");
        CarEntity copy = new CarEntity();
        copy.setName("copy");
        return copy;
    }
}
```
到这里我们还不够，因为目前高斯引擎不会全包搜索实现cloneable接口的类，所以我们还需要一个注解来告诉高斯引擎，这个类是定制化的类
```java
@Data
@ToString
@OverrideClone
public class CarEntity implements Cloneable{

    private String name;

    private int year;

    private String ownerName;

    @Override
    protected Object clone() {
        System.out.println("---- start -----");
        CarEntity copy = new CarEntity();
        copy.setName("copy");
        return copy;
    }
}
```
这样，我们加上了```@OverrideClone```注解后，高斯引擎就知道这个类在拷贝时需要定制化。
```java
CarEntity copy = BeanFactory.create(CarEntity.class);
System.out.println(copy.getName());  // "copy"
```
如果某个类是只读的，无法进行修改的话。我们还是可以给BeanFactory一个临时的行为，这样就可以临时进行定制化
```java
CarDTO carDTO = BeanFactory.create(CarDTO.class, c -> c.setOwnerName("copy"));
System.out.println(carDTO.getOwnerName()); // "copy"
```
### BeanMapper
在上一篇中我们知道BeanMapper可以在对象间属性拷贝,也用了Car和CarEntity举了一个例子。那么如果我们的工程中有多个bean需要mapping该怎么优雅的申明？  
我们可以集中在一个类上标明注解   
假设现在我们有另一个实体对象CarDTO,代码如下。
```java
@Data
@ToString
@Component
public class CarDTO {
    
    private String name;
    
    private int year;
    
    private String driver;
}
```
显然这里的driver需要跟Car里的owner相匹配。这时我们就可以在Car上这样申明
```java
@Data
@Mappers(value = {
        @Mapper(target = CarEntity.class),
        @Mapper(target = CarDTO.class)
})
public class Car {
    private String name;

    private int year;

    @Mappings(value = {
            @FieldMapping(scope = CarEntity.class, fieldNames = "ownerName"),
            @FieldMapping(scope = CarDTO.class, fieldNames = "driver")
    })
    private String owner;
}
```
```@Mappers```和```@Mappings```是一个聚合注解，里面主要是为了放多个```@Mapper```和```@FieldMapping```注解,这样我们只要在一个类上申明就可以把所有相匹配的类的都标注上。   
如果你足够细心，你会发现在```@FieldMapping```注解上有个属性叫fieldNames，其实是为了给某一个特定类里有个多个字段需要跟同一个属性相匹配所设定的。例如我们修改下CarDTO
```java
@Data
@ToString
@Component
public class CarDTO {

    private String name;

    private int year;
    
    private String ownerName;

    private String driver;
}
```
我们在原来的基础上添加一个字段----ownerName,显然，这里ownerName和driver都需要跟Car里的owner匹配，那么我们可以在car里调整为
```java
@Data
@Mappers(value = {
        @Mapper(target = CarEntity.class),
        @Mapper(target = CarDTO.class)
})
public class Car {
    private String name;

    private int year;

    @Mappings(value = {
            @FieldMapping(scope = CarEntity.class, fieldNames = "ownerName"),
            @FieldMapping(scope = CarDTO.class, fieldNames = {"driver", "ownerName"})
    })
    private String owner;
}
```
***注意***
- 上面例子中，我们在Car里的owner属性上为CarDTO匹配了两个属性。当我们反向拷贝时(CarDTO->Car)，owner值将为null.BeanMapper无法知道将用哪个属性来进行拷贝(除非有相同属性名). 
### Module and Factory
这里有跟上面相似的功能. 
在上一篇中，我们使用了一个FintechFactory来举例说明Module和Factory的用法。如果我们写的Module想复用怎么办？例如，我们的Apply模块想在别的工厂中复用，我们可以
```java
    @Chains(value = {
            @Chain(factory = FintechFactory.class, sequence = 1),
            @Chain(factory = DefaultFactory.class, sequence = 1)
    })
    public class Apply implements Operation {

        @Override
        public void handle(Operation.FlowChart proposal) {
            // implement code here
            System.out.println("--- apply ---");
        }
    }
```
这样我们就可以把apply模块同样装进DefaultFactory里
