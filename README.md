# PrometheusAgent
Java Agent to export metrics to Prometheus

Introduction:

This java agent will instrument you application class while JVM compiles and load the class bytecode in runtime
before loading the bytecode for the class the agent will collect data (likes API counter, Request Execution Times)
Once the data is collected it will expose  those  metrics to localhost:9300/metrics on local system, which Promeheues can scrape once configured in prometheus.yml


Setup:
Build Support: mvn
Run 
`mvn clean install -U`
to generate jar file for javagent

Run you application with config `-javagent:<path/to/agent/jar>

Sample config `java -javagent:<path/to/agent/jar> -jar <path/to/your/source/java/application.jar> <path/to/your/Main.java>`

Start Prometheus in local or using docker  and configure the end point to scrape metric in prometheus.yml config
Setup Grafana in local and configure promentheus as data source to better visualsation.

Note: This agent currently support application with one DispatchServlet. Please feel free to add feature and extend the functionality.
