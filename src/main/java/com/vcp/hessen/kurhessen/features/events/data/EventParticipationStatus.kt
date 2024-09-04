package com.vcp.hessen.kurhessen.features.events.data

enum class EventParticipationStatus(val langKey : String) {
    INVITED("Invited"),
    DECLINED("Declined"),
    IN_PAYMENT("InPayment"),
    IN_REPAYMENT("InPayment"),
    REGISTERED("Registered")
}