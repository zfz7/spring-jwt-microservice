package org.zfz7.auth.user

import javax.persistence.*


@Entity
class AuthorizedUser(
        @Column(unique = true, nullable = false)
        val username: String,
        @Column(nullable = false)
        var password: String,
        @Convert(converter = SensorTypeEnumListConverter::class)
        val roles: List<String>,
        val isEnabled: Boolean = true,
        val isNonExpired: Boolean = true,
        val isNonLocked: Boolean = true,
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null
)

class SensorTypeEnumListConverter : AttributeConverter<List<String>?, String> {
        override fun convertToDatabaseColumn(list: List<String>?): String? {
                if (list == null) {
                        return null
                }
                return list.joinToString(",")
        }
        override fun convertToEntityAttribute(joined: String?): List<String>? {
                if (joined.isNullOrBlank()) {
                        return listOf()
                }
                return joined.split(",")
        }
}