class PowertacHouseholdCustomerGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['powertacCommon':'0.9 > *',
                     'powertacServerInterface':'0.1 > *',
                     'powertacRandom':'0.1 > *']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Anthony Chrysopoulos"
    def authorEmail = "achryso@issel.ee.auth.gr"
    def title = "Household Customer Model"
    def description = '''\\
This plugin contains a simple autonomous model of household customer. The model has aggregated capabilities. 
It begins with simple persons and appliances, then goes up to households and then villages, cities etc.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/powertac-household-customer"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
