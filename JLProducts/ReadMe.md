<h2>#Tech Challenge on rest Apis</h2><br/>

<h3>##Tech details</h3><br/>
Programming Language - Kotlin
Rest Framework - Ktor
Test - Groovy (using mockito) and kotlin (to test the ktor application call using jUnit)
Build tool - gradle
Application server - netty

##Git Clone - Clone this complete repository
git clone https://github.com/JananiGan/TechChallenge.git

#Build (Please note that GradleWrapper is available, so build could be triggered using gradlew.bat also)
cd <cloned_folder>/TechChallenge/JLProducts
./gradlew build

#Run the application
Note : Application configured to run on port 8080. (Main class/ktor application module - ProductsApiApplication.kt)

cd <cloned_folder>/TechChallenge/JLProducts
./gradlew run

Application would be started and the below output could be seen in the console


> Task :compileKotlin UP-TO-DATE
> Task :compileJava NO-SOURCE
> Task :compileGroovy NO-SOURCE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE

> Task :run
2019-02-27 12:19:36.111 [main] TRACE Application - {
    # application.conf @ file:/C:/Users/CogJanGanesan.NETDOM/Impdocs/TechChallenge/TechChallenge/JLProducts/build/resources/main/application.conf: 6
    "application" : {
        # application.conf @ file:/C:/Users/CogJanGanesan.NETDOM/Impdocs/TechChallenge/TechChallenge/JLProducts/build/resources/main/application.conf: 7
        "modules" : [
            # application.conf @ file:/C:/Users/CogJanGanesan.NETDOM/Impdocs/TechChallenge/TechChallenge/JLProducts/build/resources/main/application.conf: 7
            "com.challenge.products.rest.ProductsApiApplicationKt.module"
        ]
    },
    # application.conf @ file:/C:/Users/CogJanGanesan.NETDOM/Impdocs/TechChallenge/TechChallenge/JLProducts/build/resources/main/application.conf: 2
    "deployment" : {
        # application.conf @ file:/C:/Users/CogJanGanesan.NETDOM/Impdocs/TechChallenge/TechChallenge/JLProducts/build/resources/main/application.conf: 3
        "port" : 8080
    },
    # Content hidden
    "security" : "***"
}

2019-02-27 12:19:36.505 [main] INFO  Application - No ktor.deployment.watch patterns specified, automatic reload is not active
2019-02-27 12:19:36.913 [main] INFO  Application - Responding at http://0.0.0.0:8080


##Import project into IntelliJ
Navigate to TechChallenge/JLProducts and import