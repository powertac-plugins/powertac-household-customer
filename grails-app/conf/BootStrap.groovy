import java.util.Random

import org.powertac.common.*


class BootStrap {
  def randomSeedService // autowire
  def villageConsumerService

  Random randomGen = null

  def init = { servletContext ->
    /*
     def configFile = "../powertac-household-customer/grails-app/conf/HouseholdConfig.groovy"
     ConfigObject conf = new ConfigSlurper().parse(new File(configFile).toURL())
     Random gen = ensureRandomSeed()
     def villageInfo = new CustomerInfo(Name: "Village 1",customerType: CustomerType.CustomerHousehold, powerTypes: [PowerType.CONSUMPTION])
     assert(villageInfo.save())
     def village = new Village(CustomerInfo: villageInfo)
     village.initialize(conf,gen)
     village.init()
     assert(village.save())
     for (int i =0;i < 1;i++){
     village.refresh(conf,gen)
     }
     int value=0;
     int weekday = 0;
     village.houses.each { house ->
     for (int i=0;i < 1;i++){
     for (int j=0;j < 24;j++){
     weekday = i % 7
     value = house.weeklyLoadInHours.get(i).get(j)
     Consumption cons = new Consumption()
     cons.initialize(village,house,i,weekday,j,value)
     cons.save()
     }
     }
     }*/
  }

  def destroy = {
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

