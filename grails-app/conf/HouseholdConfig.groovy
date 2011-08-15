household {

  general {
    PublicVacationDuration = 3
    NumberOfVillages =  1
  }

  person {
    consumption {
      OnePersonConsumption = 1902
      TwoPersonsConsumption = 3365
      ThreePersonsConsumption = 4309
      FourPersonsConsumption = 5087
      FivePersonsConsumption = 6142
    }

    personsInHousehold {
      OnePerson = 39
      TwoPersons = 34
      ThreePersons = 13
      FourPersons = 10
      FivePersons = 4
    }

    personType {
      PeriodicPresent = 53
      MostlyPresent = 40
      RandomlyAbsent = 7
    }
  }

  sickness {
    SicknessMean = 4
    SicknessDev = 0.5
  }

  leisure {

    numberByType {

      PPLeisure = 4
      MPLeisure = 7
      RALeisure = 3
      LeisureDev = 1
    }

    duration {
      LeisureDurationMean = 8
      LeisureDurationDev = 4
    }
  }

  work {

    daysPerWeek {
      OneDay = 1
      TwoDays = 3
      ThreeDays = 3
      FourDays = 4
      FiveDays = 74
      SixDays = 12
      SevenDays = 3
    }

    duration {
      WorkingDurationMean = 38
      WorkingDurationDev = 4
    }
  }

  vacation {

    VacationDurationMean = 4
    VacationDurationDev = 0.5
    VacationAbsence = 0.5
  }

  appliances {

    refrigerator { RefrigeratorSaturation = 0.99 }

    freezer { FreezerSaturation = 0.54 }


    dishwasher {
      DishwasherSaturation = 0.66
      DishwasherWeeklyTimes = 1
    }

    stove {
      StoveSaturation = 0.85
      StoveDailyTimes = 2
    }

    washingMachine {
      WashingMachineSaturation = 0.97
      WashingMachineWeeklyTimes = 1
    }

    dryer {
      DryerSaturation = 0.42
      DryerWeeklyTimes = 1
    }

    consumerElectronics {
      ConsumerElectronicsSaturation = 0.98
      ConsumerElectronicsDailyTimes = 7
    }

    ict {
      ICTSaturation = 0.98
      ICTDailyTimes = 6
    }

    waterHeater {
      WaterHeaterSaturation = 0.2
      InstantHeater = 60
      StorageHeater = 40
      InstantHeaterDailyTimes = 0
    }

    circulationPump {
      CirculationPumpSaturation = 0.92
      CirculationPumpPercentage = 0.5
    }

    spaceHeater {
      SpaceHeaterSaturation = 0.04
      SpaceHeaterPercentage = 0.2
    }

    lights { LightsDailyTimes = 8 }

    others { OthersDailyTimes = 8 }
  }

  houses {
    NotShiftingCustomers = 10
    RandomlyShiftingCustomers = 10
    RegularlyShiftingCustomers = 10
    SmartShiftingCustomers = 10
  }
}
environments {
/*  Phoenix {
    household {
      houses {
        NotShiftingCustomers = 2
        RandomlyShiftingCustomers = 2
        RegularlyShiftingCustomers = 2
        SmartShiftingCustomers = 2
      }
    }
  }*/
  Freiamt {
    household {
      houses {
        NotShiftingCustomers = 10
        RandomlyShiftingCustomers = 10
        RegularlyShiftingCustomers = 10
        SmartShiftingCustomers = 10
      }
    }
  }
}

