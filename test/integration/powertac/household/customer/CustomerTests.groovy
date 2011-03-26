package powertac.household.customer

import consumers.Environment
import grails.test.*

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.powertac.common.Broker
import org.powertac.common.HourlyCharge
import org.powertac.common.enumerations.PowerType
import org.powertac.common.enumerations.CustomerType
import org.powertac.common.enumerations.TariffTransactionType
import org.powertac.common.AbstractCustomer
import org.powertac.common.CustomerInfo
import org.powertac.common.Rate
import org.powertac.common.Tariff
import org.powertac.common.interfaces.CompetitionControl
import org.powertac.common.interfaces.NewTariffListener
import org.powertac.common.TariffTransaction
import org.powertac.common.TariffSpecification
import org.powertac.common.TariffSubscription
import org.powertac.common.msg.TariffExpire
import org.powertac.common.msg.TariffRevoke
import org.powertac.common.msg.TariffStatus
import org.powertac.common.msg.VariableRateUpdate
import org.powertac.tariffmarket.TariffMarketService
import org.powertac.common.TimeService
import persons.Config


class CustomerTests extends GroovyTestCase 
{
  def timeService  // autowire the time service
  def tariffMarketService // autowire the market

  
  Tariff tariff
  TariffSpecification defaultTariffSpec
  Broker broker1
  Broker broker2
  Instant exp
  Instant start
  CustomerInfo customerInfo
  AbstractCustomer customer
  DateTime now
  Config conf
  protected void setUp()
  {
	
    super.setUp()
	TariffSpecification.list()*.delete()
	Tariff.list()*.delete()
    broker1 = new Broker(username: "Joe")
    broker1.save()
    broker2 = new Broker(username: "Anna")
    broker2.save()

    now = new DateTime(2011, 1, 10, 0, 0, 0, 0, DateTimeZone.UTC)
	timeService.start = now.toInstant().millis
    timeService.currentTime = now.toInstant()

    exp = new Instant(now.millis + TimeService.WEEK * 10)
    TariffSpecification tariffSpec =
        new TariffSpecification(brokerId: broker1.getId(),
        expiration: exp,
        minDuration: TimeService.WEEK * 8)
    tariffSpec.addToRates(new Rate(value: 0.121))
    tariffSpec.save()

    defaultTariffSpec = new TariffSpecification(brokerId: broker1.getId(),
        expiration: exp,
        minDuration: TimeService.WEEK * 8)
    defaultTariffSpec.addToRates(new Rate(value: 0.222))
    defaultTariffSpec.save()

    tariffMarketService.setDefaultTariff(defaultTariffSpec)

    assertEquals("correct Default Tariff", defaultTariffSpec, tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType).tariffSpec)
    assertEquals("One Tariff", 1, Tariff.count())
  
	//Reading the config file
	Scanner sc = new Scanner(System.in);
	conf = new persons.Config();
	conf.readConf();
	assertFalse("Config File Read and HashMap created", conf.hm == null)
	
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testCreationAndSubscriptionToDefault(){
	    
	  def env = new Environment()
	  env.initialize(conf.hm)
	  assert env.save()
	  
	  assertEquals("Two Villages Created", conf.hm.get("NumberOfVillages"), AbstractCustomer.count())
	  assertEquals("Two Villages Created", conf.hm.get("NumberOfVillages"), env.villages.size())
	  
	  env.villages*.subscribeDefault()
	    
	  assertFalse("Village 1 subscribed", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 1")).subscriptions == null)
	  assertFalse("Village 2 subscribed", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 2")).subscriptions == null)
	  
	  assertFalse("Village 1 subscribed to default", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 1")).subscriptions == tariffMarketService.getDefaultTariff(PowerType.CONSUMPTION))
	  assertFalse("Village 2 subscribed to default", AbstractCustomer.findByCustomerInfo(CustomerInfo.findByName("Village 2")).subscriptions == tariffMarketService.getDefaultTariff(PowerType.CONSUMPTION))
  }
 
  void testPowerConsumption(){
	  

	  
	  def env = new Environment()
	  env.initialize(conf.hm)
	  assert env.save()
	  
	  env.villages*.subscribeDefault()
	  
	  timeService.setCurrentTime(new Instant(now.millis + (TimeService.HOUR)))
	
	  env.villages.each {
		  
		  it.consumePower()
		  
		  assertFalse("Customer consumed power", it.subscriptions?.totalUsage == null || it.subscriptions?.totalUsage == 0)
		  
			  
	  }  
  
	  assertEquals("Tariff Transactions Created", conf.hm.get("NumberOfVillages"), TariffTransaction.findByTxType(TariffTransactionType.CONSUME).count())
  }
   
  void testChangingSubscriptions(){
	  

	  def env = new Environment()
	  env.initialize(conf.hm)
	  assert env.save()
	  
	  env.villages*.subscribeDefault()
	  
	  def tsc1 = new TariffSpecification(brokerId: broker2.id,
		  expiration: new Instant(now.millis + TimeService.DAY),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  def tsc2 = new TariffSpecification(brokerId: broker2.id,
		  expiration: new Instant(now.millis + TimeService.DAY * 2),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  def tsc3 = new TariffSpecification(brokerId: broker2.id,
		  expiration: new Instant(now.millis + TimeService.DAY * 3),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  Rate r2 = new Rate(value: 0.222)
	  tsc1.addToRates(r2)
	  tsc2.addToRates(r2)
	  tsc3.addToRates(r2)
	  tariffMarketService.processTariff(tsc1)
	  tariffMarketService.processTariff(tsc2)
	  tariffMarketService.processTariff(tsc3)
  
	  assertEquals("Four tariffs", 4, Tariff.count())
  
	  env.villages.each {
		  
		  it.changeSubscription(tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType), true)
		  
		  assertFalse("Changed from default tariff", it.subscriptions?.tariff.toString() == tariffMarketService.getDefaultTariff(defaultTariffSpec.powerType).toString())
		    
	  }  
  
	  
  }	
  
  void testRevokingSubscriptions(){
	  
	  def env = new Environment()
	  env.initialize(conf.hm)
	  assert env.save()
	  
	  env.villages*.subscribeDefault()
	  
	 
	  def tsc1 = new TariffSpecification(brokerId: broker1.id,
			expiration: new Instant(now.millis + TimeService.DAY * 5),
			minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  def tsc2 = new TariffSpecification(brokerId: broker1.id,
			expiration: new Instant(now.millis + TimeService.DAY * 7),
			minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  def tsc3 = new TariffSpecification(brokerId: broker1.id,
			expiration: new Instant(now.millis + TimeService.DAY * 9),
			minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  Rate r1 = new Rate(value: 0.222)
	  tsc1.addToRates(r1)
	  tsc2.addToRates(r1)
	  tsc3.addToRates(r1)
	  tariffMarketService.processTariff(tsc1)
	  tariffMarketService.processTariff(tsc2)
	  tariffMarketService.processTariff(tsc3)
	  Tariff tc1 = Tariff.get(tsc1.id)
	  assertNotNull("first tariff found", tc1)
	  Tariff tc2 = Tariff.get(tsc2.id)
	  assertNotNull("second tariff found", tc2)
	  Tariff tc3 = Tariff.get(tsc3.id)
	  assertNotNull("third tariff found", tc3)
	  
	  // make sure we have three active tariffs
	  def tclist = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
	  assertEquals("4 consumption tariffs", 4, tclist.size())
	  assertEquals("three transaction", 3, TariffTransaction.count())
	  
	  env.villages.each{
	  
		  it.subscribe(tc1, 3)
		  it.subscribe(tc2, 33)
		  it.subscribe(tc3, 32)
	  
		  assertEquals("4 Subscriptions for each customer", 4, it.subscriptions?.size())
	  }
	  
	  
	  timeService.currentTime = new Instant(timeService.currentTime.millis + TimeService.HOUR)
	  TariffRevoke tex = new TariffRevoke(tariffId: tc2.id,
										  brokerId: tc2.brokerId)
	  def status = tariffMarketService.processTariff(tex)
	  assertNotNull("non-null status", status)
	  assertEquals("success", TariffStatus.Status.success, status.status)
	  assertTrue("tariff revoked", tc2.isRevoked())
	  
	  // should now be just two active tariffs
	  tclist = tariffMarketService.getActiveTariffList(PowerType.CONSUMPTION)
	  assertEquals("3 consumption tariffs", 3, tclist.size())
	  
	  // retrieve Charley's revoked-subscription list
	  
	  env.villages.each{
	  
	
		  def revokedCustomer = tariffMarketService.getRevokedSubscriptionList(it)
		  assertEquals("one item in list", 1, revokedCustomer.size())
		  assertEquals("it's the correct one", TariffSubscription.findByTariffAndCustomer(tc2,it), revokedCustomer[0])
		
		  it.checkRevokedSubscriptions()
		  
		  assertEquals("3 Subscriptions for customer", 3, it.subscriptions?.size())
	  }
	    
  }
  
  void testTariffPublication() {
	  
	  // test competitionControl registration
	  def registrationThing = null
	  def registrationPhase = -1
	  def competitionControlService =
		  [registerTimeslotPhase: { thing, phase ->
			registrationThing = thing
			registrationPhase = phase
		  }] as CompetitionControl
	  
	  tariffMarketService.registrations = []
	  tariffMarketService.newTariffs = []
	  
	  tariffMarketService.competitionControlService = competitionControlService
	  tariffMarketService.afterPropertiesSet()
	  //assertEquals("correct thing", tariffMarketService, registrationThing)
	  assertEquals("correct phase", tariffMarketService.simulationPhase, registrationPhase)

	  start = new DateTime(2011, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toInstant()
	    
	  def env = new Environment()
	  env.initialize(conf.hm)
	  assert env.save()
	  
	  env.villages*.subscribeDefault()
	  
	  // current time is noon. Set pub interval to 3 hours.
	  tariffMarketService.publicationInterval = 3 // hours
	  assertEquals("newTariffs list is empty", 0, tariffMarketService.newTariffs.size())
	
	  assertEquals("All villages registered", conf.hm.get("NumberOfVillages"), tariffMarketService.registrations.size())
	  
	  env.villages.each {
		  
		  assertEquals("no tariffs at 12:00", 0, it.publishedTariffs.size())
		  
	  }	  
	  
	  // publish some tariffs over a period of three hours, check for publication
	  def tsc1 = new TariffSpecification(brokerId: broker1.id,
		  expiration: new Instant(start.millis + TimeService.DAY),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  Rate r1 = new Rate(value: 0.222)
	  tsc1.addToRates(r1)
	  tariffMarketService.processTariff(tsc1)
	  timeService.currentTime += TimeService.HOUR
	  // it's 13:00
	  tariffMarketService.activate(timeService.currentTime, 2)
	  
	  env.villages.each {
		  
		  assertEquals("no tariffs at 13:00", 0, it.publishedTariffs.size())
		  
	  }
	  
	  def tsc2 = new TariffSpecification(brokerId: broker1.id,
		  expiration: new Instant(start.millis + TimeService.DAY * 2),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  tsc2.addToRates(r1)
	  tariffMarketService.processTariff(tsc2)
	  def tsc3 = new TariffSpecification(brokerId: broker1.id,
		  expiration: new Instant(start.millis + TimeService.DAY * 3),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.CONSUMPTION)
	  tsc3.addToRates(r1)
	  tariffMarketService.processTariff(tsc3)
	  timeService.currentTime += TimeService.HOUR
	  // it's 14:00
	  tariffMarketService.activate(timeService.currentTime, 2)

	  env.villages.each {
		  
		  assertEquals("no tariffs at 14:00", 0, it.publishedTariffs.size())
		  
	  }	
	  def tsp1 = new TariffSpecification(brokerId: broker1.id,
		  expiration: new Instant(start.millis + TimeService.DAY),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.PRODUCTION)
	  def tsp2 = new TariffSpecification(brokerId: broker1.id,
		  expiration: new Instant(start.millis + TimeService.DAY * 2),
		  minDuration: TimeService.WEEK * 8, powerType: PowerType.PRODUCTION)
	  Rate r2 = new Rate(value: 0.119)
	  tsp1.addToRates(r2)
	  tsp2.addToRates(r2)
	  tariffMarketService.processTariff(tsp1)
	  tariffMarketService.processTariff(tsp2)
	  assertEquals("five tariffs", 6, Tariff.count())
	  timeService.currentTime += TimeService.HOUR
	  // it's 15:00 - time to publish
	  tariffMarketService.activate(timeService.currentTime, 2)
	  
	  env.villages.each {
		  
		  assertEquals("5 tariffs at 15:00", 5, it.publishedTariffs.size())
		  
	  }
	  
	  assertEquals("newTariffs list is again empty", 0, tariffMarketService.newTariffs.size())
	  
  }
 
}
