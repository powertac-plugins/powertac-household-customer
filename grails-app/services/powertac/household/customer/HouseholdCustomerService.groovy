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

import java.util.Random

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
  def randomSeedService // autowire
  def tariffMarketService

  PluginConfig configuration
  HashMap hm
  Random randomGen = null

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


  void init(PluginConfig config) {

    configuration = config

    //Reading the config file
    ConfigObject conf = new ConfigSlurper().parse(new File(getConfigFile()).toURL())

    Random gen = ensureRandomSeed()

    def number = (int)conf.household.general.NumberOfVillages
    for (int i = 1; i < number+1;i++){
      def villageInfo = new CustomerInfo(Name: "Village " + i,customerType: CustomerType.CustomerHousehold, powerTypes: [PowerType.CONSUMPTION])
      assert(villageInfo.save())
      def village = new Village(CustomerInfo: villageInfo)
      village.initialize(conf,gen)
      village.init()
      village.subscribeDefault()
      village.createBootstrapData()
      village.createActualData(conf,gen)
      assert(village.save())
    }
  }

  void activate(Instant now, int phase) {

    log.info "Activate"
    def villageList = Village.list()

    Random gen = ensureRandomSeed()

    if (phase == 1) villageList*.step(gen)
    else villageList*.toString()
  }

  private Random ensureRandomSeed ()
  {
    if (randomGen == null) {
      long randomSeed = randomSeedService.nextSeed('HouseholdCustomerService', 'household', 'model')
      randomGen = new Random(randomSeed)
    }
    return randomGen
  }
}