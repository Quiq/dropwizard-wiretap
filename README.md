DropWizard Wiretap Logger
===========================
Log appender that works with DropWizard and exposes the last 500 log messages on an HTTP endpoint.

Why
----
Often times when working with Micro-services we've found ourselves wanting to see what just
happened on a specific instance of a service we were hitting. Examples include when an error
occurs or to verify that a message came through correctly. 

Usage
----
Simply reference this jar and setup your YML file to use the wiretap appender:
    
    appenders:
      - type: console
      - type: wiretap

Then register the included resource.
    environment.jersey().register(new WiretapResource())
    
Note: You will want to secure this endpoint. See below for how we add this off of our admin port
in order to make sure it can't accidentally get out into the wild.
    


Notes
----
This was originally written for use by all of our in house micro-services. Because of that
we've taken a very opinionated view of the logFormat to use. If you agree with our opinions
you can simply make use of this module and all of the following will apply to you. If not 
feel free to change the format and tweek the instructions below to apply.


Advanced usage (Admin Port)
----
One way to expose this is off of the admin port. Here is an example of how you can register a 
Servlet container off of the admin port and host resources in it:

    private void setupAdminEnvironment (Environment environment){
        DropwizardResourceConfig resourceConfig = new DropwizardResourceConfig(environment.metrics());
        JerseyContainerHolder servletContainerHolder = new JerseyContainerHolder(new ServletContainer(resourceConfig));

        environment.admin().addServlet("admin", servletContainerHolder.getContainer()).addMapping("/admin/*");

        resourceConfig.register(new JacksonMessageBodyProvider(Jackson.newObjectMapper(), environment.getValidator()));
        resourceConfig.register(new WiretapResource());
    }

Assuming you do this you can then see the last 500 log messages by hitting the following endpoint:

http://localhost:8081/admin/wiretap


Advanced usage (Normal logging and RequestLog)
----
If you are wondering why the Info log statements that show the HTTP requests aren't coming through 
the wiretap logger this is because you have to register the appender under the requestLog section
of the config. Below is a sample config file that will set everything to be logged (in the same 
format) to both console and wiretap:

    server:
        applicationConnectors:
          - type: http
            port: 8080
        adminConnectors:
          - type: http
            port: 8081
        requestLog:
          appenders:
            - type: console
              logFormat: '%-5p [%thread] [%d] %c: %m%n%rEx'
            - type: wiretap
    
    logging:
        level: INFO
      
        appenders:
          - type: console
            logFormat: '%-5p [%thread] [%d] %c: %m%n%rEx'
          - type: wiretap
      

Contributors
------------
* [Matt West](https://github.com/mjwest10) from [Centricient](https://github.com/mjwest10)