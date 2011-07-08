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
import org.powertac.common.CustomerInfo
import org.powertac.common.PluginConfig
import org.powertac.common.Rate
import org.powertac.common.Tariff
import org.powertac.common.TariffSpecification
import org.powertac.common.TariffSubscription
import org.powertac.common.TariffTransaction
import org.powertac.common.TimeService
import org.powertac.common.enumerations.PowerType
import org.powertac.common.enumerations.TariffTransactionType
import org.powertac.common.msg.TariffRevoke
import org.powertac.common.msg.TariffStatus
import org.powertac.consumers.Village

class CustomerServiceTests extends GroovyTestCase {

  def timeService
  def tariffMarketService
  def tariffMarketInitializationService
  def householdCustomerService
  def householdCustomerInitializationService
  def villageConsumersService
  def householdConsumersService

  Competition comp
  Tariff tariff
  TariffSpecification defaultTariffSpec
  Broker broker1
  Broker broker2
  Instant exp
  Instant start
  Instant now

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

    TariffSpecification.list()*.delete()
    Tariff.list()*.delete()
    //Broker.list()*.delete()
    Broker.findByUsername('Joe')?.delete()
    broker1 = new Broker(username: "Joe")
    broker1.save()
    Broker.findByUsername('Anna')?.delete()
    broker2 = new Broker(username: "Anna")
    broker2.save()

    now = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
    timeService.currentTime = now
    timeService.base = now.millis

    // initialize the tariff market
    PluginConfig.findByRoleName('TariffMarket')?.delete()
    tariffMarketInitializationService.setDefaults()
    tariffMarketInitializationService.initialize(comp, ['AccountingService'])

    exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec =
        new TariffSpecification(broker: broker1,
        expiration: exp,
        minDuration: TimeService.WEEK * 8)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()

    defaultTariffSpec = new TariffSpecification(broker: broker1,
        expiration: exp,
        minDuration: TimeService.WEEK * 8)
    defaultTariffSpec.addToRates(new Rate(value: 0.5))
    defaultTariffSpec.save()

    tariffMarketService.setDefaultTariff(defaultTariffSpec)

    assertEquals("correct Default Tariff", defaultTariffSpec,
        tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType).tariffSpec)
    assertEquals("One Tariff", 1, Tariff.count())

  }

  protected void tearDown() {
    super.tearDown()
  }

  void initializeService () {
    householdCustomerInitializationService.setDefaults()
    PluginConfig config = PluginConfig.findByRoleName('HouseholdCustomer')
    config.configuration['configFile'] = '../powertac-household-customer/grails-app/conf/HouseholdConfig.groovy'
    householdCustomerInitializationService.initialize(comp, [
      'TariffMarket',
      'DefaultBroker'
    ])
  }
  /*
   void testNormalInitialization () {
   householdCustomerInitializationService.setDefaults()
   PluginConfig config = PluginConfig.findByRoleName('HouseholdCustomer')
   assertNotNull("config created correctly", config)
   def result = householdCustomerInitializationService.initialize(comp, [
   'TariffMarket',
   'DefaultBroker'
   ])
   assertEquals("correct return value", 'HouseholdCustomer', result)
   assertEquals("correct configuration file", '../powertac-household-customer/grails-app/conf/HouseholdConfig.groovy', householdCustomerService.getConfigFile())
   }
   void testBogusInitialization () {
   PluginConfig config = PluginConfig.findByRoleName('HouseholdCustomer')
   assertNull("config not created", config)
   def result = householdCustomerInitializationService.initialize(comp, [
   'TariffMarket',
   'DefaultBroker'
   ])
   assertEquals("failure return value", 'fail', result)
   }
   void testConfiguration(){
   def config = new ConfigSlurper("LA").parse(new File('grails-app/conf/HouseholdConfig.groovy').toURL())
   assert config.household.general.NumberOfVillages == 2
   assert config.household.houses.NewShiftingCustomers == 200
   }
   void testVillagesInitialization() {
   initializeService()
   assertEquals("Two Villages Created", Village.count(), AbstractCustomer.count())
   assertFalse("Village 1 subscribed", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 1")).subscriptions == null)
   assertFalse("Village 2 subscribed", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 2")).subscriptions == null)
   assertFalse("Village 1 subscribed to default", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 1")).subscriptions == tariffMarketService.getDefaultTariff(PowerType.CONSUMPTION))
   assertFalse("Village 2 subscribed to default", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 2")).subscriptions == tariffMarketService.getDefaultTariff(PowerType.CONSUMPTION))
   }
   void testPowerConsumption() {
   initializeService()
   timeService.setCurrentTime(new Instant(now.millis + (TimeService.HOUR)))
   householdCustomerService.activate(timeService.currentTime, 1)
   Village.list().each { village ->
   assertFalse("Customer consumed power", village.subscriptions?.totalUsage == null || village.subscriptions?.totalUsage == 0)
   }
   assertEquals("Tariff Transactions Created", Village.count(), TariffTransaction.findByTxType(TariffTransactionType.CONSUME).count())
   }
   void testChangingSubscriptions() {
   initializeService()
   def tsc1 = new TariffSpecification(broker: broker2,
   expiration: new Instant(now.millis + TimeService.DAY),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   def tsc2 = new TariffSpecification(broker: broker2,
   expiration: new Instant(now.millis + TimeService.DAY * 2),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   def tsc3 = new TariffSpecification(broker: broker2,
   expiration: new Instant(now.millis + TimeService.DAY * 3),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   Rate r2 = new Rate(value: 0.222)
   tsc1.addToRates(r2)
   tsc2.addToRates(r2)
   tsc3.addToRates(r2)
   tariffMarketService.processTariff(tsc1)
   tariffMarketService.processTariff(tsc2)
   tariffMarketService.processTariff(tsc3)
   assertEquals("Five tariff specifications", 5, TariffSpecification.count())
   assertEquals("Four tariffs", 4, Tariff.count())
   Village.list().each {village ->
   village.changeSubscription(tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType))
   List<Tariff> lastTariff = village.subscriptions?.tariff
   lastTariff.each { tariff ->
   village.changeSubscription(tariff,tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType))
   village.changeSubscription(tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType), tariff, 5)
   }
   assertFalse("Changed from default tariff", village.subscriptions?.tariff.toString() == tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType).toString())
   }
   }
   void testRevokingSubscriptions() {
   initializeService()
   println("Number Of Subscriptions in DB: ${TariffSubscription.count()}")
   // create some tariffs
   def tsc1 = new TariffSpecification(broker: broker1,
   expiration: new Instant(now.millis + TimeService.DAY * 5),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   def tsc2 = new TariffSpecification(broker: broker1,
   expiration: new Instant(now.millis + TimeService.DAY * 7),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   def tsc3 = new TariffSpecification(broker: broker1,
   expiration: new Instant(now.millis + TimeService.DAY * 9),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   Rate r1 = new Rate(value: 0.222)
   tsc1.addToRates(r1)
   tsc2.addToRates(r1)
   tsc3.addToRates(r1)
   tariffMarketService.processTariff(tsc1)
   tariffMarketService.processTariff(tsc2)
   tariffMarketService.processTariff(tsc3)
   Tariff tc1 = Tariff.findBySpecId(tsc1.id)
   assertNotNull("first tariff found", tc1)
   Tariff tc2 = Tariff.findBySpecId(tsc2.id)
   assertNotNull("second tariff found", tc2)
   Tariff tc3 = Tariff.findBySpecId(tsc3.id)
   assertNotNull("third tariff found", tc3)
   // make sure we have three active tariffs
   def tclist = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
   assertEquals("4 consumption tariffs", 4, tclist.size())
   assertEquals("three transaction", 3, TariffTransaction.count())
   // householdCustomerService.activate(timeService.currentTime, 1)
   Village.list().each{ village ->
   TariffSubscription tsd =
   TariffSubscription.findByTariffAndCustomer(tariffMarketService.getDefaultTariff(PowerType.CONSUMPTION), village)
   village.unsubscribe(tsd,3)
   village.subscribe(tc1, 3)
   village.subscribe(tc2, 3)
   village.subscribe(tc3, 4)
   TariffSubscription ts1 =
   TariffSubscription.findByTariffAndCustomer(tc1, village)
   village.unsubscribe(ts1, 2)
   TariffSubscription ts2 =
   TariffSubscription.findByTariffAndCustomer(tc2, village)
   village.unsubscribe(ts2, 1)
   TariffSubscription ts3 =
   TariffSubscription.findByTariffAndCustomer(tc3, village)
   village.unsubscribe(ts3, 2)
   println("Number Of Subscriptions in DB: ${TariffSubscription.count()}")
   assertEquals("4 Subscriptions for customer",4, village.subscriptions?.size())
   timeService.currentTime = new Instant(timeService.currentTime.millis + TimeService.HOUR)
   }
   TariffRevoke tex = new TariffRevoke(tariffId: tsc2.id, broker: tc2.broker)
   def status = tariffMarketService.processTariff(tex)
   assertNotNull("non-null status", status)
   assertEquals("success", TariffStatus.Status.success, status.status)
   assertTrue("tariff revoked", tc2.isRevoked())
   // should now be just two active tariffs
   tclist = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
   assertEquals("3 consumption tariffs", 3, tclist.size())
   Village.list().each{ village ->
   // retrieve revoked-subscription list
   def revokedCustomer = tariffMarketService.getRevokedSubscriptionList(village)
   assertEquals("one item in list", 1, revokedCustomer.size())
   assertEquals("it's the correct one", TariffSubscription.findByTariffAndCustomer(tc2,village), revokedCustomer[0])
   }
   householdCustomerService.activate(timeService.currentTime, 1)
   Village.list().each{ village ->
   assertEquals("3 Subscriptions for customer", 3, village.subscriptions?.size())
   }
   println("Number Of Subscriptions in DB: ${TariffSubscription.count()}")
   TariffRevoke tex3 = new TariffRevoke(tariffId: tsc3.id, broker: tc1.broker)
   def status3 = tariffMarketService.processTariff(tex3)
   assertNotNull("non-null status", status3)
   assertEquals("success", TariffStatus.Status.success, status3.status)
   assertTrue("tariff revoked", tc3.isRevoked())
   // should now be just two active tariffs
   def tclist3 = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
   assertEquals("2 consumption tariffs", 2, tclist3.size())
   // retrieve revoked-subscription list
   Village.list().each{ village ->
   def revokedCustomer3 = tariffMarketService.getRevokedSubscriptionList(village)
   assertEquals("one item in list", 1, revokedCustomer3.size())
   assertEquals("it's the correct one", TariffSubscription.findByTariffAndCustomer(tc3,village), revokedCustomer3[0])
   log.info "Revoked Tariffs ${revokedCustomer3.toString()} "
   }
   householdCustomerService.activate(timeService.currentTime, 1)
   Village.list().each{ village ->
   assertEquals("2 Subscriptions for customer", 2, village.subscriptions?.size())
   }
   TariffRevoke tex2 = new TariffRevoke(tariffId: tsc1.id, broker: tc1.broker)
   def status2 = tariffMarketService.processTariff(tex2)
   assertNotNull("non-null status", status2)
   assertEquals("success", TariffStatus.Status.success, status2.status)
   assertTrue("tariff revoked", tc1.isRevoked())
   // should now be just two active tariffs
   def tclist2 = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
   assertEquals("1 consumption tariffs", 1, tclist2.size())
   Village.list().each{ village ->
   // retrieve revoked-subscription list
   def revokedCustomer2 = tariffMarketService.getRevokedSubscriptionList(village)
   assertEquals("one item in list", 1, revokedCustomer2.size())
   assertEquals("it's the correct one", TariffSubscription.findByTariffAndCustomer(tc1,village), revokedCustomer2[0])
   log.info "Revoked Tariffs ${revokedCustomer2.toString()} "
   }
   householdCustomerService.activate(timeService.currentTime, 1)
   Village.list().each{ village ->
   assertEquals("1 Subscriptions for customer", 1, village.subscriptions?.size())
   }
   }
   */
  void testEvaluatingTariffs() {
    initializeService()
    println("Number Of Subscriptions in DB: ${TariffSubscription.count()}")
    // create some tariffs
    def tsc1 = new TariffSpecification(broker: broker1,
        expiration: new Instant(now.millis + TimeService.DAY * 5),
        minDuration: TimeService.WEEK * 8, powerType: PowerType.PRODUCTION)
    def tsc2 = new TariffSpecification(broker: broker1,
        expiration: new Instant(now.millis + TimeService.DAY * 7),
        minDuration: TimeService.WEEK * 8, powerType: PowerType.PRODUCTION)
    def tsc3 = new TariffSpecification(broker: broker1,
        expiration: new Instant(now.millis + TimeService.DAY * 9),
        minDuration: TimeService.WEEK * 8, powerType: PowerType.PRODUCTION)
    Rate r1 = new Rate(value: 0.222)
    tsc1.addToRates(r1)
    tsc2.addToRates(r1)
    tsc3.addToRates(r1)
    tariffMarketService.processTariff(tsc1)
    tariffMarketService.processTariff(tsc2)
    tariffMarketService.processTariff(tsc3)
    Tariff tc1 = Tariff.findBySpecId(tsc1.id)
    assertNotNull("first tariff found", tc1)
    Tariff tc2 = Tariff.findBySpecId(tsc2.id)
    assertNotNull("second tariff found", tc2)
    Tariff tc3 = Tariff.findBySpecId(tsc3.id)
    assertNotNull("third tariff found", tc3)
    // make sure we have three active tariffs
    def tclist = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
    assertEquals("4 consumption tariffs", 1, tclist.size())
    assertEquals("three transaction", 3, TariffTransaction.count())
    Village.list().each{ customer ->
      customer.possibilityEvaluationNewTariffs(Tariff.list())
    }
  }

  /*
   void testVillageRefreshModels() {
   initializeService()
   timeService.base = now.toInstant().millis
   timeService.currentTime = new Instant(timeService.currentTime.millis + TimeService.HOUR*22)
   timeService.currentTime = new Instant(timeService.currentTime.millis + TimeService.DAY*6)
   householdCustomerService.activate(timeService.currentTime, 1)
   println(householdConsumersService.appliancesPossibilityOperations.toString())
   println(householdConsumersService.appliancesOperations.toString())
   println(householdConsumersService.appliancesLoads.toString())
   }
   void testDailyShifting()
   {
   initializeService()
   def tsc1 = new TariffSpecification(broker: broker1,
   expiration: new Instant(now.millis + TimeService.DAY * 5),
   minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
   Rate r1 = new Rate(value: Math.random(), dailyBegin: 0, dailyEnd: 0)
   assert r1.save()
   Rate r2 = new Rate(value: Math.random(), dailyBegin: 1, dailyEnd: 1)
   assert r2.save()
   Rate r3 = new Rate(value: Math.random(), dailyBegin: 2, dailyEnd: 2)
   assert r3.save()
   Rate r4 = new Rate(value: Math.random(), dailyBegin: 3, dailyEnd: 3)
   assert r4.save()
   Rate r5 = new Rate(value: Math.random(), dailyBegin: 4, dailyEnd: 4)
   assert r5.save()
   Rate r6 = new Rate(value: Math.random(), dailyBegin: 5, dailyEnd: 5)
   assert r6.save()
   Rate r7 = new Rate(value: Math.random(), dailyBegin: 6, dailyEnd: 6)
   assert r7.save()
   Rate r8 = new Rate(value: Math.random(), dailyBegin: 7, dailyEnd: 7)
   assert r8.save()
   Rate r9 = new Rate(value: Math.random(), dailyBegin: 8, dailyEnd: 8)
   assert r9.save()
   Rate r10 = new Rate(value: Math.random(), dailyBegin: 9, dailyEnd: 9)
   assert r10.save()
   Rate r11 = new Rate(value: Math.random(), dailyBegin: 10, dailyEnd: 10)
   assert r11.save()
   Rate r12 = new Rate(value: Math.random(), dailyBegin: 11, dailyEnd: 11)
   assert r12.save()
   Rate r13 = new Rate(value: Math.random(), dailyBegin: 12, dailyEnd: 12)
   assert r13.save()
   Rate r14 = new Rate(value: Math.random(), dailyBegin: 13, dailyEnd: 13)
   assert r14.save()
   Rate r15 = new Rate(value: Math.random(), dailyBegin: 14, dailyEnd: 14)
   assert r15.save()
   Rate r16 = new Rate(value: Math.random(), dailyBegin: 15, dailyEnd: 15)
   assert r16.save()
   Rate r17 = new Rate(value: Math.random(), dailyBegin: 16, dailyEnd: 16)
   assert r17.save()
   Rate r18 = new Rate(value: Math.random(), dailyBegin: 17, dailyEnd: 17)
   assert r18.save()
   Rate r19 = new Rate(value: Math.random(), dailyBegin: 18, dailyEnd: 18)
   assert r19.save()
   Rate r20 = new Rate(value: Math.random(), dailyBegin: 19, dailyEnd: 19)
   assert r20.save()
   Rate r21 = new Rate(value: Math.random(), dailyBegin: 20, dailyEnd: 20)
   assert r21.save()
   Rate r22 = new Rate(value: Math.random(), dailyBegin: 21, dailyEnd: 21)
   assert r22.save()
   Rate r23 = new Rate(value: Math.random(), dailyBegin: 22, dailyEnd: 22)
   assert r23.save()
   Rate r24 = new Rate(value: Math.random(), dailyBegin: 23, dailyEnd: 23)
   assert r24.save()
   tsc1.addToRates(r1)
   tsc1.addToRates(r2)
   tsc1.addToRates(r3)
   tsc1.addToRates(r4)
   tsc1.addToRates(r5)
   tsc1.addToRates(r6)
   tsc1.addToRates(r7)
   tsc1.addToRates(r8)
   tsc1.addToRates(r9)
   tsc1.addToRates(r10)
   tsc1.addToRates(r11)
   tsc1.addToRates(r12)
   tsc1.addToRates(r13)
   tsc1.addToRates(r14)
   tsc1.addToRates(r15)
   tsc1.addToRates(r16)
   tsc1.addToRates(r17)
   tsc1.addToRates(r18)
   tsc1.addToRates(r19)
   tsc1.addToRates(r20)
   tsc1.addToRates(r21)
   tsc1.addToRates(r22)
   tsc1.addToRates(r23)
   tsc1.addToRates(r24)
   tariffMarketService.processTariff(tsc1)
   Tariff tc1 = Tariff.findBySpecId(tsc1.id)
   assertNotNull("first tariff found", tc1)
   def tclist = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
   Village.list().each{ customer ->
   customer.possibilityEvaluationNewTariffs(Tariff.list())
   }
   timeService.base = now.toInstant().millis
   timeService.currentTime = new Instant(timeService.currentTime.millis + TimeService.HOUR*11)
   householdCustomerService.activate(timeService.currentTime, 1)
   for (int i=0;i < 60; i++) {
   timeService.currentTime = new Instant(timeService.currentTime.millis + TimeService.HOUR*24)
   householdCustomerService.activate(timeService.currentTime, 1)
   }
   }*/
}