package sotck.stockalert.domain.user.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import sotck.stockalert.domain.user.Email

@Converter(autoApply = true)
class EmailConverter : AttributeConverter<Email, String> {
    override fun convertToDatabaseColumn(attribute: Email?): String? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: String?): Email? {
        return dbData?.let { Email(it) }
    }
}
