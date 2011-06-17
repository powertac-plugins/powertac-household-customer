household {

  general {
    PublicVacationDuration = 3
    NumberOfVillages = 2
  }

  person {
    consumption {
      OnePersonConsumption = 1973
      TwoPersonsConsumption = 3261
      ThreePersonsConsumption = 4240
      FourPersonsConsumption = 4902
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
      WorkingDurationMean = 32
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

    //freezer { FreezerSaturation = 0.54 }
    freezer { FreezerSaturation = 1 }

    dishwasher {
      //DishwasherSaturation = 0.66
      DishwasherSaturation = 1
      DishwasherWeeklyTimes = 1
    }

    stove {
      //StoveSaturation = 0.85
      StoveSaturation = 1
      StoveDailyTimes = 2
    }

    washingMachine {
      WashingMachineSaturation = 0.97
      WashingMachineWeeklyTimes = 1
    }

    dryer {
      //DryerSaturation = 0.42
      DryerSaturation = 1
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
      //WaterHeaterSaturation = 0.2
      WaterHeaterSaturation = 1
      InstantHeater = 0
      StorageHeater = 100
      InstantHeaterDailyTimes = 0
    }

    circulationPump {
      CirculationPumpSaturation = 0.92
      CirculationPumpPercentage = 0.5
    }

    spaceHeater {
      SpaceHeaterSaturation = 1
      //SpaceHeaterSaturation = 0.04
      SpaceHeaterPercentage = 0.2
    }

    lights { LightsDailyTimes = 8 }

    others { OthersDailyTimes = 8 }
  }

  houses {
    NotShiftingCustomers = 1
    RandomlyShiftingCustomers = 1
    RegularlyShiftingCustomers = 1
    SmartShiftingCustomers = 1
  }
}
environments {
  Phoenix {
    household {
      houses {
        NotShiftingCustomers = 1
        RandomlyShiftingCustomers = 1
        RegularlyShiftingCustomers = 1
        SmartShiftingCustomers = 1
      }
    }
  }
  LA {
    household {
      houses {
        NotShiftingCustomers = 200
        RandomlyShiftingCustomers = 200
        RegularlyShiftingCustomers = 200
        SmartShiftingCustomers = 200
        NewShiftingCustomers = 200
      }
    }
  }
}

