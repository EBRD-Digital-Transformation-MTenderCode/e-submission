package com.procurement.submission.application.exception

class RequirementValueException(requirementValue: String, description: String = "") :
    RuntimeException("Incorrect value in requirement: '$requirementValue'. $description")