# Gauss Framework 
[![][maven img]][maven]
[![][license img]][license]
[![Build Status](https://github.com/fenix-Lee/gauss-framework/actions/workflows/maven.yml/badge.svg)](https://github.com/fenix-Lee/gauss-framework/actions)
[![Chat](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)](https://gaussframework.slack.com)

Gauss Engine is a framework designed for Spring-Boot based Java project development that includes bean-copy with custom convertor for specific field, bean-achievement and design-pattern adoption.

## Adding Gauss to your build
To add a dependency by Maven, use the following:
```xml
<dependency>
     <groupId>xyz.gaussframework</groupId>
     <artifactId>gauss-engine-spring-boot-starter</artifactId>
     <version>2.4.0</version>
</dependency>
```
## Learn about Gauss
- Our user guide [Gauss User Guide](https://github.com/fenix-Lee/gauss-framework/wiki)

## Importance
- require JDK 8 or higher
- Don't forget `@EnableGaussEngine` along with your root Java file(such as spring application start file with `@SpringBootApplication`), otherwise some functions cannot be activated


[maven]:https://maven-badges.herokuapp.com/maven-central/xyz.gaussframework/gauss-engine-spring-boot-starter
[maven img]:https://maven-badges.herokuapp.com/maven-central/xyz.gaussframework/gauss-engine-spring-boot-starter/badge.svg

[license]:LICENSE-2.0.txt
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
