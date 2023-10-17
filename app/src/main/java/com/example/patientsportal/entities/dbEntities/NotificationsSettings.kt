package com.example.patientsportal.entities.dbEntities

data class NotificationsSettings(
    var idNotificationSettings: Int = 0,
    var patient: Patient = Patient(),
    var medicalTestEnabled: String = "",
    var medicalTestPush: String = "",
    var medicalTestEmail: String = "",
    var medicalTestReminderOne: Int = 0,
    var medicalTestReminderTwo: Int = 0,
    var medicalTestReminderThree: Int = 0,
    var medicalTestReminderFour: Int = 0,
    var medicalTestReminderFive: Int = 0,
    var drugsEnabled: String = "",
    var drugsPush: String = "",
    var drugsEmail: String = "",
    var drugsReminderOne: Int = 0,
    var drugsReminderTwo: Int = 0,
    var drugsReminderThree: Int = 0,
    var drugsReminderFour: Int = 0,
    var drugsReminderFive: Int = 0,
    var derivationsEnabled: String = "",
    var derivationsPush: String = "",
    var derivationsEmail: String = "",
    var patientsPortalEnabled: String = "",
    var patientsPortalPush: String = "",
    var patientsPortalEmail: String = ""
)
