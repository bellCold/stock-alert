package sotck.stockalert.domain.user.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import sotck.stockalert.domain.user.Password

@Converter(autoApply = true)
class PasswordConverter : AttributeConverter<Password, String> {
    override fun convertToDatabaseColumn(attribute: Password?): String? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: String?): Password? {
        return dbData?.let { Password(it) }
    }
}
