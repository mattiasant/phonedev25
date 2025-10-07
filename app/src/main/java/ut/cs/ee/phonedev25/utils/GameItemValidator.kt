package ut.cs.ee.phonedev25.utils

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object GameItemValidator {

    fun validateItemName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Item name cannot be empty")
            name.length < 3 -> ValidationResult.Error("Item name must be at least 3 characters")
            name.length > 50 -> ValidationResult.Error("Item name must not exceed 50 characters")
            else -> ValidationResult.Success
        }
    }

    fun validateItemType(type: String): ValidationResult {
        val validTypes = listOf("", "", "", "", "")
        return when {
            type.isBlank() -> ValidationResult.Error("Please select an  type")
            !validTypes.contains(type.lowercase()) -> ValidationResult.Error("Invalid item type")
            else -> ValidationResult.Success
        }
    }

    fun validatePowerLevel(level: Int): ValidationResult {
        return when {
            level < 1 -> ValidationResult.Error("Power level must be at least 1") //example text, should be changed later
            level > 999 -> ValidationResult.Error("Power level cannot exceed 999")
            else -> ValidationResult.Success
        }
    }

    fun validateDescription(description: String): ValidationResult {
        return when {
            description.isBlank() -> ValidationResult.Error("Description cannot be empty")
            description.length < 10 -> ValidationResult.Error("Description must be at least 10 characters")
            description.length > 500 -> ValidationResult.Error("Description must not exceed 500 characters")
            else -> ValidationResult.Success
        }
    }

    fun validateRarity(rarity: String): ValidationResult {
        val validRarities = listOf("common", "uncommon", "rare", "epic", "legendary")
        return when {
            rarity.isBlank() -> ValidationResult.Error("Please select a rarity")
            !validRarities.contains(rarity.lowercase()) -> ValidationResult.Error("Invalid rarity")
            else -> ValidationResult.Success
        }
    }

    fun validateGameItem(
        name: String,
        type: String,
        powerLevel: Int,
        description: String,
        rarity: String
    ): List<ValidationResult.Error> {
        val errors = mutableListOf<ValidationResult.Error>()

        (validateItemName(name) as? ValidationResult.Error)?.let { errors.add(it) }
        (validateItemType(type) as? ValidationResult.Error)?.let { errors.add(it) }
        (validatePowerLevel(powerLevel) as? ValidationResult.Error)?.let { errors.add(it) }
        (validateDescription(description) as? ValidationResult.Error)?.let { errors.add(it) }
        (validateRarity(rarity) as? ValidationResult.Error)?.let { errors.add(it) }

        return errors
    }
}