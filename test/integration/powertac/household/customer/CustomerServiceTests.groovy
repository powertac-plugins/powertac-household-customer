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
import org.powertac.common.AbstractCustomer
import org.powertac.common.Broker
import org.powertac.common.Competition
import org.powertac.common.PluginConfig
import org.powertac.common.Tariff
import org.powertac.common.TariffSpecification
import org.powertac.common.Timeslot
import org.powertac.common.configurations.Config

class CustomerServiceTests extends GroovyTestCase {

  def timeService
  def tariffMarketService
  def householdCustomerService
  def householdCustomerInitializationService

  Instant exp
  Broker broker
  Config conf
  Instant start
  Competition comp

  protected void setUp() {
    super.setUp()
    PluginConfig.findByRoleName('HouseholdCustomer')?.delete()

    // create a Competition, needed for initialization
    if (Competition.count() == 0) {
      comp = new Competition(name: 'household-customer-test')
      assert comp.save()
    }
    else {
      comp = Competition.list().first()
    }

    AbstractCustomer.list()*.delete()
    Timeslot.list()*.delete()
    TariffSpecification.list()*.delete()
    Tariff.list()*.delete()
    tariffMarketService.registrations = []

    // set the clock
    def now = new DateTime(2011, 1, 26, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.setCurrentTime(now)

  }

  protected void tearDown() {
    super.tearDown()
  }

  void initializeService () {
    householdCustomerInitializationService.setDefaults()
    PluginConfig config = PluginConfig.findByRoleName('HouseholdCustomer')
    config.configuration['configFile'] = 'config.txt'
    householdCustomerInitializationService.initialize(comp, ['HouseholdCustomer'])
  }

  void testNormalInitialization () {
    householdCustomerInitializationService.setDefaults()
    PluginConfig config = PluginConfig.findByRoleName('HouseholdCustomer')
    assertNotNull("config created correctly", config)
    def result = householdCustomerInitializationService.initialize(comp, ['HouseholdCustomer'])
    assertEquals("correct return value", 'HouseholdCustomer', result)
    assertEquals("correct configuration file", 'config.txt', householdCustomerService.getConfigFile())
  }

  void testBogusInitialization () {
    PluginConfig config = PluginConfig.findByRoleName('HouseholdCustomer')
    assertNull("config not created", config)
    def result = householdCustomerInitializationService.initialize(comp, ['HouseholdCustomer'])
    assertEquals("failure return value", 'fail', result)
  }

  void testVillages() {
  }
}
