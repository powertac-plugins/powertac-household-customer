
class HouseholdCustomerBootStrap {

  def simpleGencoService

  def init = { servletContext ->
    /*
     //Reading the config file
     Scanner sc = new Scanner(System.in);
     conf = new org.powertac.common.configurations.Config();
     conf.readConf();
     int number = (int)conf.variablesHashMap.get("NumberOfVillages")
     // create some villages
     for (int i = 1; i < number+1;i++){
     def villageInfo = new CustomerInfo(Name: "Village " + i,customerType: CustomerType.CustomerHousehold, powerType: PowerType.CONSUMPTION)
     villageInfo.save()
     def village = new Village(CustomerInfo: villageInfo)
     village.initialize(hash, publicVacationVector)
     //    village.init()
     village.save()
     village.fillAggWeeklyLoad()
     village.showAggWeeklyLoad()
     }
     */   //HouseholdCustomerService.init()
  }

  def destroy = {
  }
}

