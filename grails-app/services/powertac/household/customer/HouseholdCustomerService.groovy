/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package powertac.household.customer

import org.joda.time.Instant
import org.powertac.common.CustomerInfo
import org.powertac.common.PluginConfig
import org.powertac.common.enumerations.CustomerType
import org.powertac.common.enumerations.PowerType
import org.powertac.common.interfaces.TimeslotPhaseProcessor
import org.powertac.consumers.Village

class HouseholdCustomerService implements TimeslotPhaseProcessor {

  static transactional = true

  def timeService // autowire
  def competitionControlService

  PluginConfig configuration

  void afterPropertiesSet ()
  {
    competitionControlService.registerTimeslotPhase(this, 1)
    competitionControlService.registerTimeslotPhase(this, 2)
  }

  // ----------------- Configuration access ------------------
  String getConfigFile()
  {
    return configuration.configuration['configFile'].toString()
  }


  void init() {

    //Reading the config file
    Scanner sc = new Scanner(System.in);
    def conf = new org.powertac.common.configurations.Config();
    conf.readConf(getConfigFile());

    def number = (int)conf.variablesHashMap.get("NumberOfVillages")
    log.info "${number} "
    for (int i = 1; i < number+1;i++){
      def villageInfo = new CustomerInfo(Name: "Village " + i,customerType: CustomerType.CustomerHousehold, powerType: PowerType.CONSUMPTION)
      assert(villageInfo.save())
      def village = new Village(CustomerInfo: villageInfo)
      village.initialize(conf.variablesHashMap)
      village.init()
      assert(village.save())
    }
  }

  void activate(Instant now, int phase) {

    log.info "Activate"
    def villageList = Village.list()

    if (phase == 1){
      villageList*.step()
    }
    else {
      villageList*.toString()
    }
  }
}