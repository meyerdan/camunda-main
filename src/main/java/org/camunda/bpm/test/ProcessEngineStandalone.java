/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.test;

import static org.camunda.bpm.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE;
import static org.camunda.bpm.engine.ProcessEngineConfiguration.HISTORY_FULL;
import static org.camunda.bpm.engine.variable.Variables.createVariables;
import static org.camunda.bpm.engine.variable.Variables.integerValue;
import static org.camunda.bpm.engine.variable.Variables.serializedObjectValue;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.variable.Variables.SerializationDataFormats;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;

/**
 * @author Daniel Meyer
 *
 */
public class ProcessEngineStandalone {

  public static void main(String[] args) throws InterruptedException {

    List<ProcessEnginePlugin> plugins = new ArrayList<ProcessEnginePlugin>();
    plugins.add(new SpinProcessEnginePlugin());

    ProcessEngineConfigurationImpl peci = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
      .setJdbcDriver("org.postgresql.Driver")
      .setJdbcUrl("jdbc:postgresql://localhost/process-engine")
      .setJdbcUsername("postgres")
      .setJdbcPassword("postgres")
      .setHistory(HISTORY_FULL)
      .setDatabaseSchemaUpdate(DB_SCHEMA_UPDATE_TRUE)
      .setJobExecutorActivate(true);

    peci.setProcessEnginePlugins(plugins);

    ProcessEngine processEngine = peci.buildProcessEngine();

    // code goes here
    try {

      RepositoryService repositoryService = processEngine.getRepositoryService();

      Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource("loan-approval.bpmn")
        .enableDuplicateFiltering(true)
        .deploy();


      RuntimeService runtimeService = processEngine.getRuntimeService();

      runtimeService.startProcessInstanceByKey("approve-loan",
        createVariables().putValueTyped("amount", integerValue(10))
          .putValueTyped("foo",
              serializedObjectValue().objectTypeName("org.camunda.bpm.test.Foo")
              .serializationDataFormat(SerializationDataFormats.JSON).serializedValue("{\"a\": 1}")
              .create()));


      synchronized (ProcessEngineStandalone.class) {
        ProcessEngineStandalone.class.wait();
      }

    } finally {
      processEngine.close();
    }
  }

}
