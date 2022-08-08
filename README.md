# Gauss Framework 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.gaussframework/gauss-engine-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.gaussframework/gauss-engine-spring-boot-starter)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) 

Gauss Engine is a framework designed for Spring-Boot based Java project development that includes bean-copy with custom convertor for specific field, bean-achievement and design-pattern adoption.

## Adding Gauss to your build
To add a dependency by Maven, use the following:
```xml
<dependency>
     <groupId>xyz.gaussframework</groupId>
     <artifactId>gauss-engine-spring-boot-starter</artifactId>
     <version>2.2.7</version>
</dependency>
```
## Learn about Gauss
- Our user guide [Gauss User Guide](https://github.com/fenix-Lee/gauss-framework/wiki)

## Importance
- require JDK 8 or higher
- Don't forget `@EnableGaussEngine` along with your root Java file(such as spring application start file with `@SpringBootApplication`), otherwise some functions cannot be activated
