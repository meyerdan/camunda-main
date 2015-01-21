# camunda-main

Start up camunda process engine in the main method of a java class

```java
public static void main(String[] args) {

    ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
      .setJdbcDriver("org.postgresql.Driver")
      .setJdbcUrl("jdbc:postgresql://localhost/process-engine")
      .setJdbcUsername("postgres")
      .setJdbcPassword("postgres")
      .setHistory(HISTORY_FULL)
      .setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_TRUE)
      .setJobExecutorActivate(true)
      .buildProcessEngine();
      
    try {
      // ... code goes here
    }
    finally {
      processEngine.close();
    }

  }
```
