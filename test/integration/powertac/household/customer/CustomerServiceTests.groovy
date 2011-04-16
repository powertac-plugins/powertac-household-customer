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

import grails.test.*

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.powertac.common.CustomerInfo
import org.powertac.common.configurations.Config
import org.powertac.common.enumerations.CustomerType
import org.powertac.common.enumerations.PowerType
import org.powertac.consumers.Village

class CustomerServiceTests extends GroovyTestCase {

  def timeService
  def householdCustomerService

  int number
  Config conf
  Instant start

  protected void setUp() {
    super.setUp()

    //Reading the config file
    Scanner sc = new Scanner(System.in);
    conf = new org.powertac.common.configurations.Config();
    conf.readConf();
    number = (int)conf.variablesHashMap.get("NumberOfVillages")

    start = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.setCurrentTime(start)

  }

  protected void tearDown() {
    super.tearDown()
  }

  void testVillages() {

    // create some villages
    for (int i = 1; i < number+1;i++){
      def villageInfo = new CustomerInfo(Name: "Village " + i,customerType: CustomerType.CustomerHousehold, powerType: PowerType.CONSUMPTION)
      assert(villageInfo.save())
      def village = new Village(CustomerInfo: villageInfo)
      village.initialize(conf.variablesHashMap)
      village.init()
      assert(village.save())
      village.fillAggWeeklyLoad()
      village.showAggWeeklyLoad()
    }
  }
}
