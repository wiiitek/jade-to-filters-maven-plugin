# it

## Maven Invoker Plugin

Goals:

1. [`invoker:install`](https://maven.apache.org/plugins/maven-invoker-plugin/install-mojo.html)
   - stages the artifacts under tests into a testing repository (`target/it/*`)
2. [`invoker:run`](https://maven.apache.org/plugins/maven-invoker-plugin/run-mojo.html)
   - runs and verify integration tests for a Maven plugin

### How it works

1. Copy test files into target folder
2. Runs the plugin on each test project
3. Verifies generated output files.
